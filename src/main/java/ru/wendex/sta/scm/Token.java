package ru.wendex.sta.scm;

public class Token implements ru.wendex.sta.langbase.Token {
	public static int EOF = -1;
	public static int IDENT = 0;
	public static int LPAREN = 1;
	public static int RPAREN = 2;
	public static int QUOTE = 3;
	public static int QUASIQUOTE = 4;
	public static int UNQUOTE = 5;
	public static int UNQUOTE_SPLICING = 6;
	public static int VECTOR_PAREN = 7;
	public static int LITERAL = 8;
	public static int IMPROPER_PERIOD = 9;
	
	private static final String[] TAG_NAME =
		{"IDENT", "LPAREN", "RPAREN", "QUOTE", "QUASIQUOTE", "UNQUOTE", "UNQUOTE_SPLICING", "VECTOR_PAREN", "LITERAL", "IMPROPER_PERIOD"};
	private static final String EOF_NAME = "EOF";
	
	public static String tagName(int tag) {
		if (tag < EOF || tag > IMPROPER_PERIOD)
			throw new IllegalArgumentException("Scheme token tag " + tag + " is not exists");
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
		if (tag == IDENT || tag == LITERAL)
			throw new IllegalArgumentException("Scheme token " + tagName(tag) + " must be inheritor of Token class");
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
}
