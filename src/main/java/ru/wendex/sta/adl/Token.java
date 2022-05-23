package ru.wendex.sta.adl;

public class Token {
	public static final int EOF = -1;
	public static final int IDENT = 0;
	public static final int LSQUARE = 1;
	public static final int RSQUARE = 2;
	public static final int ARROW = 3;
	public static final int SEMICOLON = 4;
	public static final int LPAREN = 5;
	public static final int RPAREN = 6;
	public static final int DEF = 7;
	public static final int FROM = 8;
	public static final int TO = 9;
	public static final int FINAL = 10;
	public static final int EPSILON = 11;
	public static final int SCHEME_IDENT = 12;
	public static final int SPEC_IDENT = 13;
	public static final int LCURLY = 14;
	public static final int RCURLY = 15;
	
	static final int MIN_TAG = -1;
	static final int MAX_TAG = 15;
	
	private static final String[] TAG_NAME =
		{"IDENT", "LSQUARE", "RSQUARE", "ARROW", "SEMICOLON", "LPAREN", "RPAREN", "DEF", "FROM", "TO",
			"FINAL", "EPSILON", "SCHEME_IDENT", "SPEC_IDENT", "LCURLY", "RCURLY"};
	private static final String EOF_NAME = "EOF";
	
	public static String tagName(int tag) {
		if (tag < MIN_TAG || tag > MAX_TAG)
			throw new IllegalArgumentException("ADL token tag " + tag + " is not exists");
		if (tag == EOF)
			return EOF_NAME;
		else
			return TAG_NAME[tag];
	}
	
	private int tag;
	private int line1;
	private int column1;
	private int line2;
	private int column2;
	
	public Token(int tag, int line1, int column1, int line2, int column2) {
		if (tag < MIN_TAG || tag > MAX_TAG)
			throw new IllegalArgumentException("ADL token tag " + tag + " is not exists");
		this.tag = tag;
		this.line1 = line1;
		this.column1 = column1;
		this.line2 = line2;
		this.column2 = column2;
	}
	
	public String toString() {
		return tagName(tag) + " ("+ line1 + ", " + column1 + ")-(" + line2 + ", " + column2 + ")";
	}
	
	public int getTag() {
		return tag;
	}
	
	public void assignCoordinates(Token token) {
		line1 = token.line1;
		column1 = token.column1;
		line2 = token.line2;
		column2 = token.column2;
	}
}
