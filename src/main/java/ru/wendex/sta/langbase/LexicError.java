package ru.wendex.sta.langbase;

public class LexicError {
	private int line1, column1;
	private int line2, column2;
	private String value;
	
	public LexicError(int line1, int column1, int line2, int column2, String value) {
		this.line1 = line1;
		this.column1 = column1;
		this.line2 = line2;
		this.column2 = column2;
		this.value = value;
	}
	
	public String toString() {
		return "lexic error (" + line1 + ", " + column1 + ")-(" + line2 + ", " + column2 + "): " + value;
	}
}
