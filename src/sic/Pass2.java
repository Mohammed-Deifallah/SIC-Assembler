package sic;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;

public class Pass2 {
	private Pass1 pass1;
	// private int firstAddress;
	private File objFile;
	private File listingFile;

	private String firstAddressHex;
	private String programSize;
	private String programName;
	private PrintWriter p;
	private PrintWriter l;

	private String operrandAddress;
	private String obcodeAddress;
	private int counter;
	private String insStart;
	private int insSize;
	private InputIntermediateLine input;
	private String objectCodeLine;

	public Pass2(Pass1 pass1) {
		counter = 0;
		input = new InputIntermediateLine();
		// TODO Auto-generated constructor stub
		this.pass1 = pass1;
		objFile = new File("ObjectFile.txt");
		listingFile = new File("ListingFile.txt");

		objectCodeLine = firstAddressHex = programSize = programName = operrandAddress = obcodeAddress = "";
		try {
			p = new PrintWriter(new FileOutputStream(objFile));
			l = new PrintWriter(new FileOutputStream(listingFile));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public File getObjectCodetFile(File intermediate) throws IOException {
		if (pass1.hasErrors()) {
			throw new RuntimeException("Syntax error !!");
		}
		Scanner in;
		try {
			in = new Scanner(intermediate);
			String temp = null;
			while (in.hasNextLine()) {
				temp = in.nextLine();
				if (temp.length() > 0 && temp.charAt(0) != '.') {
					break;
				}
			}
			input.setLine(temp);
			if (input.getOpCode().equals("START")) {
				firstAddressHex = input.getOperand();
				programSize = Integer.toHexString(pass1.getProgLength());
				programSize = normalize(programSize, 7);
				// write to listing file ??
				programName = input.getLabel();
				l.println(temp);
				firstAddressHex = normalize(firstAddressHex, 7);
				writeHeaderTobjFile(firstAddressHex, programSize, programName);
			}
			while (in.hasNextLine()) {

				counter++;

				String s = in.nextLine();
				while (s.length() > 0 && s.charAt(0) == '.' && in.hasNextLine()) {
					s = in.nextLine();
				}
				input.setLine(s);
				if (input.getOpCode().equals("END")) {
					writeEndToObjFile(firstAddressHex);
					p.close();
					in.close();
					l.println(s);
					l.close();
					break;
				} else {
					if (pass1.getOpTab().search(input.getOpCode())) {
						if (!input.getOperand().equals("")) {
							if (pass1.getSymTab().search(input.getOperand())) {
								operrandAddress = pass1.getSymTab().getSymLoc(input.getOperand());

							} else if ((input.getOperand().endsWith(",x") || input.getOperand().endsWith(",X"))
									&& pass1.getSymTab()
											.search(input.getOperand().substring(0, input.getOperand().length() - 2))) {
								System.out
										.println(
												Integer.toHexString(
														32768 + Integer
																.parseInt(
																		pass1.getSymTab()
																				.getSymLoc(
																						input.getOperand().substring(0,
																								input.getOperand()
																										.length() - 2)),
																		16)));

								operrandAddress = Integer
										.toHexString(
												32768 + Integer
														.parseInt(
																pass1.getSymTab().getSymLoc(input.getOperand()
																		.substring(0, input.getOperand().length() - 2)),
																16));

							} else {
								operrandAddress = "0000";
								// setErrorFlag undefined symbol
							}
						} else {
							operrandAddress = "0000";
						}
						obcodeAddress = String.valueOf(pass1.getOpTab().getLength(input.getOpCode())) + operrandAddress;
					} else if (input.getOpCode().equals("WORD") || input.getOpCode().equals("BYTE")) {
						obcodeAddress = new String();
						if (input.getOpCode().equals("WORD")) {
							try {
								int obcode = Integer.parseInt(input.getOperand());
								obcodeAddress = Integer.toHexString(obcode).toUpperCase();
								// obcodeAddress = normalize(obcodeAddress, 7);
							} catch (Exception e) {
								// setErrorFlag
							}

						} else {
							if (input.getOperand().charAt(0) == 'X') {
								obcodeAddress = input.getOperand().substring(2, input.getOperand().length() - 1);
							} else {
								if (input.getOperand().length() < 7) {

									String substring = input.getOperand().substring(2, input.getOperand().length() - 1);
									for (int i = 0; i < substring.length(); i++) {
										obcodeAddress += Integer.toHexString((((int) substring.charAt(i))))
												.toUpperCase();

									}
								} else {
									// error
								}
							}

						}
					}

					if (input.getOperand().charAt(0) != 'X')
						obcodeAddress = normalize(obcodeAddress, 7);
					if (counter == 1) {
						if (input.getOpCode().equals("RESB") || input.getOpCode().equals("RESW")) {
							obcodeAddress = new String();
							counter = 0;
						} else {
							insStart = input.getLocCounter();
							insStart = normalize(insStart, 7);
						}

					} else if (counter == 10) {
						counter = 0;
						insSize = Integer.parseInt(input.getLocCounter(), 16) - Integer.parseInt(insStart, 16);
						writeTextToObjFile(objectCodeLine, insStart, Integer.toHexString(insSize));
						objectCodeLine = new String();
						// obcodeAddress = new String();

					} else if (input.getOpCode().equals("RESB") || input.getOpCode().equals("RESW")) {

						counter = 0;
						insSize = Integer.parseInt(input.getLocCounter(), 16) - Integer.parseInt(insStart, 16);
						writeTextToObjFile(objectCodeLine, insStart, Integer.toHexString(insSize));
						objectCodeLine = new String();
						obcodeAddress = new String();

					}

					objectCodeLine += obcodeAddress;

					l.println(s + "   " + obcodeAddress);
				}

			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		return objFile;

	}

	private void writeHeaderTobjFile(String firstAddressHex, String programSize, String programName)
			throws IOException {
		p.print("H" + programName);
		int n = programName.length();
		int count = 7;
		while (n < count) {
			p.print(" ");
			count--;
		}
		p.print(firstAddressHex + programSize);
		p.println();
	}

	private void writeTextToObjFile(String objCodeLine, String start, String size) throws IOException {
		p.println("T" + start + size + objCodeLine);

	}

	private void writeEndToObjFile(String firstAddressHex) throws IOException {
		p.println("E" + firstAddressHex);

	}

	private String normalize(String s, int limit) {
		String ans = "";
		int i = 1;
		while (i < limit - s.length()) {
			ans += "0";
			i++;
		}
		ans += s;
		return ans;
	}

}