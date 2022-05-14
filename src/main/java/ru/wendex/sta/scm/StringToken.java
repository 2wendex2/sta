package ru.wendex.sta.scm;

import ru.wendex.sta.scm.Token;

public class StringToken extends Token {
	private String value;
	
	public StringToken(int tag, int line1, int column1, int line2, int column2, String value) {
		super(tag, line1, column1, line2, column2);
		this.value = value;
	}
	
	public String toString() {
		return super.toString() + ": " + value;
	}
}
