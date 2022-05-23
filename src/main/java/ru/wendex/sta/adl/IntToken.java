package ru.wendex.sta.adl;

public class IntToken extends Token {
	private int value;
	
	public IntToken(int tag, int line1, int column1, int line2, int column2, int value) {
		super(tag, line1, column1, line2, column2);
		this.value = value;
	}
	
	public String toString() {
		return super.toString() + ": " + value;
	}
	
	public int getValue() {
		return value;
	}
}
