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
	private StringBuilder numBuilder;
	
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
		nextToken = new StringToken(Token.IDENT, lineb, columnb, pos.getLine(), pos.getColumn()-1, sb.toString().toLowerCase());
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
		} else if (c == (int)'b') {
			pos.next();
			tokenizePrefNum(2);
		} else if (c == (int)'o') {
			pos.next();
			tokenizePrefNum(8);
		} else if (c == (int)'d') {
			pos.next();
			tokenizePrefNum(10);
		} else if (c == (int)'x') {
			pos.next();
			tokenizePrefNum(16);
		} else if (c == (int)'i') {
			pos.next();
			tokenizePrefExact(false);
		} else if (c == (int)'e') {
			pos.next();
			tokenizePrefExact(true);
		} else if (isDigit(c)) {
			tokenizeNum(10, "");
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
		else if (c == (int)'+')
			tokenizeSign(true);
		else if (c == (int)'-')
			tokenizeSign(false);
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
	
	
	
	
	//Функции для одного префикса (т.е. встретился лишь 1 префикс)
	private void tokenizePrefExact(boolean exact) {
		String prefix;
		if (exact)
			prefix = "#e";
		else
			prefix = "#i";
		int c = pos.peek();
		if (c == (int)'#') {
			pos.next();
			c = pos.peek();
			if (c == (int)'b') {
				pos.next();
				tokenizeNum(2, prefix + "#b");		
			} else if (c == (int)'o') {
				pos.next();
				tokenizeNum(8, prefix + "#o");
			} else if (c == (int)'d') {
				pos.next();
				tokenizeNum(10, prefix + "#d");
			} else if (c == (int)'x') {
				pos.next();
				tokenizeNum(16, prefix + "#x");
			} else
				tokenizeLexicError(prefix + "#");	
		} else
			tokenizeNum(10, prefix);
	}
	
	private void tokenizePrefNum(int radix) {
		String prefix;
		if (prefix == 2)
			prefix = "#b";
		else if (prefix == 8)
			prefix = "#o";
		else if (prefix == 10)
			prefix = "#d";
		else if (prefix == 16)
			prefix = "#x";
		
		int c = pos.peek();
		if (c == (int)'#') {
			pos.next();
			c = pos.peek();
			if (c == (int)'e') {
				pos.next();
				tokenizeNum(radix, prefix + "#e");		
			} else if (c == (int)'i') {
				pos.next();
				tokenizeNum(radix, prefix + "#i");
			} else
				tokenizeLexicError(prefix + "#");	
		} else
			tokenizeNum(radix, prefix);
	}
	
	private void tokenizeSign(boolean plus) {
		String prefix;
		if (plus)
			prefix = "+";
		else
			prifix = "-";
		
	}
	
	
	
	
	private boolean isRadixDigit(int c, int radix) {
		if (c >= (int)'0' && c < radix + (int)radix && c <= (int)'9')
			return true;
		int endLetterUp = (int)'A' + radix - 10;
		int endLetterDown = (int)'a' + radix - 10;
		return c >= (int)'A' && c < endLetterUp || c >= (int)'a' && c < endLetterDown;
	}
	
	private String tokenizeDigitSequence(int radix) {
		StringBuilder sb = new StringBuilder();
		int c = pos.peek();
		while (isRadixDigit(c, radix)) {
			sb.append((char)c);
			pos.next();
		}
		return sb.toString();
	}
	
	private String tokenizeOcthotorpSequence() {
		StringBuilder sb = new StringBuilder();
		int c = pos.peek();
		while (c == (int)'#') {
			sb.append((char)c);
			pos.next();
		}
		return sb.toString();
	}
	
	private boolean tokenizeDigitUreal(int radix) {
		String ds = tokenizeDigitSequence();
		String os = tokenizeOcthotorpSequence();
		
		int c = pos.peek();
		if (os.length() == 0 && c == (int)'.') {
			pos.next();
			String ds2 = tokenizeDigitSequence();
			String os2 = tokenizeOcthotorpSequence();
			numBuilder.append(ds);
			numBuilder.append(".");
			numBuilder.append(ds2);
			numBuilder.append(os2);
			return tokenizeSuff();
		} else if (os.length() != 0 && c == (int)'.') {
			String os2 = tokenizeOcthotorpSequence();
			numBuilder.append(ds);
			numBuilder.append(os);
			numBuilder.append(".");
			numBuilder.append(os2);
			return tokenizeSuff();
		} else if (c == (int)'/') {
			numBuilder.append(ds);
			numBuilder.append(os);
			numBuilder.append("/");
			String ds2 = tokenizeDigitSequence();
			String os2 = tokenizeOcthotorpSequence();
			numBuilder.append(ds2);
			numBuilder.append(os2);
			return ds2.length() != 0;
		} else {
			numBuilder.append(ds);
			numBuilder.append(os);
			return tokenizeSuff();
		}
	}
	
	private boolean tokenizeSuff() {
		int c1 = pos.peek();
		if (c1 != (int)'e' && c1 != (int)'s' && c1 != (int)'f' && c1 != (int)'d' && c1 != (int)'l')
			return true;
		numBuilder.append((char)c1);
		pos.next();
		int c2 = pos.peek();
		if (c2 == (int)'+' || c2 == (int)'-') {
			numBuilder.append((char)c2);
			pos.next();
		}
		
		String ds = tokenizeDigitSequence(10);
		if (ds.length() == 0) {
			return false;
		}
			
		numBuilder.append(ds);
		return true;
	}
}