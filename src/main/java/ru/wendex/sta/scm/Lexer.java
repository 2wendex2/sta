package ru.wendex.sta.scm;

import java.util.ArrayList;
import java.util.HashMap;
import ru.wendex.sta.langbase.LexicError;
import ru.wendex.sta.langbase.Position;
import ru.wendex.sta.scm.Token;
import java.io.IOException;

public class Lexer {
	private Token nextToken;
	private Position pos;
	private ArrayList<LexicError> errs = new ArrayList<>();
	private int lineb, columnb;
	
	public Lexer(Position pos) throws IOException {
		this.pos = pos;
		next();
	}
	
	public Token peek() {
		return nextToken;
	};
	
	public ArrayList<LexicError> getErrors() {
		return errs;
	}
	
	private static boolean isLetter(int c) {
		return c >= (int)'a' && c <= (int)'z' || c >= (int)'A' && c <= (int)'Z';
	}
	
	private static boolean isSpecialInitial(int c) {
		return c == (int)'!' || c == (int)'$' || c == (int)'%' || c == (int)'&' || c == (int)'*' ||
			c == (int)'/' || c == (int)':' || c == (int)'<' || c == (int)'=' || c == (int)'>' ||
			c == (int)'?' || c == (int)'^' || c == (int)'_' || c == (int)'~';
	}
	
	private static boolean isInitial(int c) {
		return isLetter(c) || isSpecialInitial(c);
	}
	
	private static boolean isDigit(int c) {
		return c >= (int)'0' && c <= (int)'9';
	}
	
	private static boolean isSpecialSubsequent(int c) {
		return c == (int)'+' || c == (int)'-' || c == (int)'.' || c == (int)'@';
	}
	
	private static boolean isSubsequent(int c) {
		return isInitial(c) || isDigit(c) || isSpecialSubsequent(c);
	}
	
	private static boolean isErrSubsequent(int c) {
		return !Character.isWhitespace(c) && c != (int)'(' && c != (int)')' && c != (int)'#' && c != (int)'"' &&
			c != (int)',' && c != (int)'\'' && c != (int)'`' && c != (int)'.';
	}
	
	private void tokenizeString() throws IOException {
		boolean badChar = false;
		StringBuilder sb = new StringBuilder();
		int c = pos.peek();
		sb.append((char)c);
		pos.next();
		c = pos.peek();
		while (c != (int)'"') {
			if (c == Position.EOF_CHAR) {
				LexicError le = new LexicError(lineb, columnb, pos.getLine(), pos.getColumn() - 1, sb.toString());
				errs.add(le);
				break;
			}
			sb.append(Character.toChars(c));
			if (c == (int)'\\') {
				pos.next();
				c = pos.peek();
				if (c != (int)'\\' && c != (int)'"') {
					badChar = true;
				}
				sb.append(Character.toChars(c));
			}
			pos.next();
			c = pos.peek();
		}
		sb.append((char)c);
		if (badChar) {
			LexicError le = new LexicError(lineb, columnb, pos.getLine(), pos.getColumn(), sb.toString());
			errs.add(le);
		} else
			nextToken = new StringToken(Token.STRING_LITERAL, lineb, columnb, pos.getLine(), pos.getColumn(), sb.toString());
		pos.next();
	}
	
	private void tokenizeIdent() throws IOException {
		StringBuilder sb = new StringBuilder();
		for (int c = pos.peek(); isSubsequent(c); c = pos.peek()) {
			sb.append((char)c);
			pos.next();
		}
		nextToken = new StringToken(Token.IDENT, lineb, columnb, pos.getLine(), pos.getColumn()-1, sb.toString());
	}
	
	private void tokenizePeriod() throws IOException {
		pos.next();
		int c = pos.peek();
		if (c != (int)'.')
			nextToken = new Token(Token.IMPROPER_PERIOD, lineb, columnb, lineb, columnb);
		else {
			pos.next();
			c = pos.peek();
			if (c != (int)'.')
				tokenizeLexicError("..");
			else {
				nextToken = new StringToken(Token.IDENT, lineb, columnb, pos.getLine(), pos.getColumn(), "...");
				pos.next();
			}
		}
	}
	
	private void tokenizeComma() throws IOException {
		pos.next();
		int c = pos.peek();
		if (c == (int)'@') {
			nextToken = new Token(Token.UNQUOTE_SPLICING, lineb, columnb, pos.getLine(), pos.getColumn());
			pos.next();
		} else 
			nextToken = new Token(Token.UNQUOTE, lineb, columnb, lineb, columnb);	
	}
	
	private void tokenizeOcthotorp() throws IOException {
		pos.next();
		int c = pos.peek();
		if (c == (int)'t') {
			nextToken = new Token(Token.TRUE_LITERAL, lineb, columnb, pos.getLine(), pos.getColumn());
			pos.next();
		} else if (c == (int)'f') {
			nextToken = new Token(Token.FALSE_LITERAL, lineb, columnb, pos.getLine(), pos.getColumn());
			pos.next();
		} else if (c == (int)'(') {
			nextToken = new Token(Token.VECTOR_PAREN, lineb, columnb, pos.getLine(), pos.getColumn());
			pos.next();
		} else if (c == (int)'\\') {
			pos.next();
			c = pos.peek();
			if (isSubsequent(c)) {
				StringBuilder sb = new StringBuilder();
				for (c = pos.peek(); isSubsequent(c); c = pos.peek()) {
					sb.append((char)c);
					pos.next();
				}
				
				String s = sb.toString();
				if (s.equals("space") || s.equals("newline")) {
					nextToken = new StringToken(Token.CHAR_LITERAL, lineb, columnb, pos.getLine(), pos.getColumn()-1, "#\\" + s);
				} else {
					LexicError le = new LexicError(lineb, columnb, pos.getLine(), pos.getColumn() - 1, "#\\" + s);
					errs.add(le);
				}
			} else {
				nextToken = new StringToken(Token.CHAR_LITERAL, lineb, columnb, pos.getLine(), pos.getColumn()-1, "#\\" + String.valueOf(Character.toChars(c)));
				pos.next();
			}
		}
	}
	
	private void tokenize() throws IOException {
		lineb = pos.getLine();
		columnb = pos.getColumn();
		int c = pos.peek();
		if (c == (int)'#')
			tokenizeOcthotorp();
		else if (c == (int)'(') {
			nextToken = new Token(Token.LPAREN, lineb, columnb, pos.getLine(), pos.getColumn());
			pos.next();
		} else if (c == (int)')') {
			nextToken = new Token(Token.RPAREN, lineb, columnb, pos.getLine(), pos.getColumn());
			pos.next();
		} else if (isInitial(c))
			tokenizeIdent();
		else if (c == (int)'\'') {
			nextToken = new Token(Token.QUOTE, lineb, columnb, pos.getLine(), pos.getColumn());
			pos.next();
		} else if (c == (int)'`') {
			nextToken = new Token(Token.QUASIQUOTE, lineb, columnb, pos.getLine(), pos.getColumn());
			pos.next();
		} else if (c == (int)',')
			tokenizeComma();
		else if (c == (int)'.')
			tokenizePeriod();
		/*else if (c == (int)'+' || c == (int)'-')
			tokenizeSign();*/
		else if (c == (int)'"')
			tokenizeString();
		else if (c == Position.EOF_CHAR)
			nextToken = new Token(Token.EOF, lineb, columnb, pos.getLine(), pos.getColumn());
		else
			tokenizeLexicError("");
	}
	
	private void tokenizeLexicError(String s) throws IOException {
		StringBuilder sb = new StringBuilder(s);
		for (int c = pos.peek(); isErrSubsequent(c); c = pos.peek()) {
			sb.append(Character.toChars(c));
			pos.next();
		}
		LexicError le = new LexicError(lineb, columnb, pos.getLine(), pos.getColumn() - 1, sb.toString());
		errs.add(le);
	}
				   
	private void skipWhitespaces() throws IOException {
		for (;;) {
			int c = pos.peek();
			if (Character.isWhitespace(c))
				pos.next();
			else if (c == (int)';') {
				pos.next();
				while (!Position.isNewline(pos.peek()))
					pos.next();
			} else
				break;
		}
	}
	
	public void next() throws IOException {
		if (nextToken != null && nextToken.getTag() == Token.EOF)
			return;
		nextToken = null;
		while (nextToken == null) {
			skipWhitespaces();
			tokenize();
		}
	}
}