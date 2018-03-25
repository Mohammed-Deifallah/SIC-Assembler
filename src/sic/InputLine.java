package sic;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class InputLine {
	private String label, opCode, operand, comment;

	public InputLine() {
		label = opCode = operand = comment = "";
	}

	public String getLabel() {
		return label;
	}

	public String getOpCode() {
		return opCode.toUpperCase();
	}

	public String getOperand() {
		return operand;
	}

	public String getComment() {
		return comment;
	}

	public void setLine(String input) {
		// Regex
		String pattern = "^\\s*(\\w+)?\\s+(\\w+)\\s*(\\w+|((\\w+)\\s*,\\s*(\\w+))|[XC]'\\w*')?\\s*(\\w+)?$";
		Pattern p = Pattern.compile(pattern);
		Matcher m = p.matcher(input);

		if (m.find()) {
			if (m.group(1) != null) {
				label = m.group(1);
				System.out.println("label:" + label);
			} else {
				label = "";
			}
			if (m.group(2) != null) {
				opCode = m.group(2);
			} else {
				opCode = "";
			}
			System.out.println("opCode:" + opCode);
			if (m.group(3) != null) {
				operand = m.group(3);
			} else {
				operand = "";
			}
			System.out.println("operand:" + operand);

			if (m.group(7) != null) {
				comment = m.group(7);
			} else {
				comment = "";
			}
			System.out.println("comment: " + comment);
		}
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
		InputLine line = new InputLine();
		line.setLine("Label add str,x");
	}
}
