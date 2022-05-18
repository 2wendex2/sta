package ru.wendex.sta.scm;

public class Token {
	public static final int EOF = -1;
	public static final int IDENT = 0;
	public static final int LPAREN = 1;
	public static final int RPAREN = 2;
	public static final int QUOTE = 3;
	public static final int QUASIQUOTE = 4;
	public static final int UNQUOTE = 5;
	public static final int UNQUOTE_SPLICING = 6;
	public static final int VECTOR_PAREN = 7;
	public static final int NUMBER_LITERAL = 8;
	public static final int CHAR_LITERAL = 9;
	public static final int STRING_LITERAL = 10;
	public static final int TRUE_LITERAL = 11;
	public static final int FALSE_LITERAL = 12;
	public static final int IMPROPER_PERIOD = 13;
	
	static final int MIN_TAG = -1;
	static final int MAX_TAG = 13;
	
	private static final String[] TAG_NAME =
		{"SYMBOL", "LPAREN", "RPAREN", "QUOTE", "QUASIQUOTE", "UNQUOTE", "UNQUOTE_SPLICING", "VECTOR_PAREN",
		 "NUMBER_LITERAL", "CHAR_LITERAL", "STRING_LITERAL", "TRUE_LITERAL", "FALSE_LITERAL", "IMPROPER_PERIOD"};
	private static final String EOF_NAME = "EOF";
	
	public static String tagName(int tag) {
		if (tag < MIN_TAG || tag > MAX_TAG)
			throw new IllegalArgumentException("Scheme token tag " + tag + " is not exists");
		if (tag == EOF)
			return EOF_NAME;
		else
			return TAG_NAME[tag];
	}
	
	public boolean isObjectToken() {
		return tag == IDENT || tag == NUMBER_LITERAL || tag == CHAR_LITERAL || tag == STRING_LITERAL || tag == TRUE_LITERAL || tag == FALSE_LITERAL;
	}
	
	private int tag;
	private int line1;
	private int column1;
	private int line2;
	private int column2;
	
	public Token(int tag, int line1, int column1, int line2, int column2) {
		if (tag < MIN_TAG || tag > MAX_TAG)
			throw new IllegalArgumentException("Scheme token tag " + tag + " is not exists");
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
