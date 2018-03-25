package sic;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class InputIntermediateLine {
	private String label, opCode, operand,locCounter;

	public InputIntermediateLine() {
		label = opCode = operand = "";
		locCounter=new String();
	}

	public String getLabel() {
		return label;
	}

	public String getOpCode() {
		return opCode;
	}

	public String getOperand() {
		return operand;
	}


	public void setLine(String input) {
		// Regex
		// "^\\s*(\\w+)?\\s+(\\w+)\\s+(\\w+|((\\w+)\\s*\\,\\s*(\\w+)))\\s*$";
		String pattern = "^\\s*(\\w+)\\s+(\\w+)?\\s+(\\w+)\\s*(\\w+|((\\w+)\\s*,\\s*(\\w+))|[XC]'\\w*')?\\s*(\\w+)?\\s*$";
		Pattern p = Pattern.compile(pattern);
		Matcher m = p.matcher(input);
		if (m.find()) {
			if (m.group(2) != null) {
				label = m.group(2);
				//System.out.println("label:"+label);
			} else {
				label = "";
				
			}
			locCounter=m.group(1);
		//	System.out.println("loc:"+locCounter);
			opCode = m.group(3);
		//	System.out.println("opCode:"+opCode);
			operand = m.group(4);
		//	System.out.println("operand:"+operand);
		}
	//	System.out.println("No:");
	}

	// public static void main(String args[]) {
	// String pattern =
	// "^\\s*(\\w+)?\\s+(\\w+)\\s+(\\w+|((\\w+)\\s*,\\s*(\\w+)))\\s*$";
	// Pattern p = Pattern.compile(pattern);
	// Matcher matcher = p.matcher(" LDA k,l");
	// if (matcher.find()) {
	// // System.out.println("Full match: " + matcher.group(0));
	// for (int i = 0; i <= matcher.groupCount(); i++) {
	// System.out.println("Group " + i + ": " + matcher.group(i));
	// }
	// }
	// }
	public static void main(String[] args) {
		InputIntermediateLine line=new InputIntermediateLine();
		line.setLine("406a           LDCH    BUFFER,C           ");
		
	}

	public String getLocCounter() {
		return locCounter;
	}

	public void setLocCounter(String locCounter) {
		this.locCounter = locCounter;
	}
}
