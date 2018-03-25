package sic;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Scanner;

public class Pass1 {
	private SymTab symTab;
	private int locCtr;
	private OpTab opTab;
	private InputLine line;
	private File intermediate;
	private int progLength;
	private String error;
	private boolean errorFlag;
	private PrintWriter p;
	private String oper;
	private String[] resWords;

	public Pass1() {
		symTab = new SymTab();
		locCtr = 0;
		line = new InputLine();
		intermediate = new File("Intermediate.txt");
		opTab = new OpTab();
		error = "";
		errorFlag = false;
		oper = "";
		resWords = new String[] { "BYTE", "RESB", "RESW", "WORD" };
		try {
			p = new PrintWriter(new FileOutputStream(intermediate));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	public File getIntermediatFile(File asmFile) {
		Scanner in;
		try {
			in = new Scanner(asmFile);
			String temp = null;
			while (in.hasNextLine()) {
				temp = in.nextLine();
				if (temp.length() > 0 && temp.charAt(0) != '.') {
					break;
				}
				p.println(temp);
			}
			// String firstLine = temp.toUpperCase();
			int startAddress = 0;
			line.setLine(temp);
			// boolean start = false;
			if (line.getOpCode().equals("START")) {
				// start = true;
				try {
					if (line.getOperand().startsWith("0x")) {
						locCtr = Integer.parseInt(line.getOperand().substring(2, line.getOperand().length()), 16);
					} else {
						locCtr = Integer.parseInt(line.getOperand(), 16);
					}
				} catch (Exception e) {
					locCtr = 0;
				}
				startAddress = locCtr;
				symTab.insert(line.getLabel(), locCtr + "");
				writeToIntermediate(line);
			}
			while (in.hasNextLine()) {
				String s = in.nextLine();
				error = "";
				while (s.length() > 0 && s.charAt(0) == '.' && in.hasNextLine()) {
					p.println(s);
					s = in.nextLine();
				}
				// s = s.toUpperCase();
				line.setLine(s);
				if (line.getOpCode().equals("END")) {
					writeToIntermediate(line);
					progLength = locCtr - startAddress;
					break;
				}
				if (!line.getLabel().equals("")) {
					if (line.getLabel().length() > 8) {
						error += "Long label       ";
						errorFlag = true;
					}
					if (!Character.isAlphabetic(line.getLabel().charAt(0))) {
						error += "Label name must start with a letter     ";
						errorFlag = true;
					}
					if (symTab.search(line.getLabel())) {
						// setErrorFlag
						error = "Duplicate label   ";
						errorFlag = true;
					} else if (opTab.search(line.getLabel().toUpperCase())
							|| Arrays.binarySearch(resWords, line.getLabel().toUpperCase()) >= 0) {
						error = "Reserved word     ";
						errorFlag = true;
					} else {
						symTab.insert(line.getLabel(), Integer.toHexString(locCtr).toUpperCase());
					}
				}
				writeToIntermediate(line);
				if (opTab.search(line.getOpCode())) {
					locCtr += 3;// opTab.getLength(line.getOpCode());
				} else if (line.getOpCode().equals("WORD")) {
					locCtr += 3;
				} else if (line.getOpCode().equals("RESW")) {
					locCtr += 3 * Integer.parseInt(line.getOperand());
				} else if (line.getOpCode().equals("RESB")) {
					locCtr += Integer.parseInt(line.getOperand());
				} else if (line.getOpCode().equals("BYTE")) {
					oper = line.getOperand();
					if (oper.length() > 0 && (oper.startsWith("C") || oper.startsWith("X"))) {
						if (oper.startsWith("C")) {
							locCtr += oper.length() - 3;
						} else {
							locCtr += Integer.parseInt(oper.substring(2, oper.length() - 1), 16);
						}
					} else {
						// setErrorFlag
						error += "Undefined definition    ";
						errorFlag = true;
					}
				} else {
					// setErrorFlag
					error += "Undefined definition        ";
					errorFlag = true;
				}
				if (line.getOperand().length() > 18) {
					error += "Long operand        ";
					errorFlag = true;
				}
				// writeToIntermediate(line);
			}
			writeMap();
			in.close();
			p.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return intermediate;
	}

	public int getProgLength() {
		return progLength;
	}

	private void writeToIntermediate(InputLine in) {
		Indent(Integer.toHexString(locCtr).toUpperCase(), 10);
		Indent(in.getLabel(), 10);
		Indent(in.getOpCode(), 7);
		Indent(in.getOperand(), 7);
		if (in.getComment() != "") {
			Indent(in.getComment(), in.getComment().length());
		}
		p.println();
		if (error.length() > 0) {
			p.println("." + error);
		}
	}

	private void writeMap() {
		String map[] = symTab.parse();
		p.println("---------------------");
		for (int i = 0; i < map.length; i += 2) {
			p.println(map[i] + " | " + map[i + 1]);
		}
		p.println("---------------------");
	}

	private void Indent(String s, int limit) {
		p.print(s);
		int i = 0;
		while (s.length() + i < limit) {
			p.print(" ");
			i++;
		}
	}

	public boolean hasErrors() {
		return errorFlag;
	}

	public SymTab getSymTab() {
		return symTab;
	}

	public OpTab getOpTab() {
		return opTab;
	}

}
