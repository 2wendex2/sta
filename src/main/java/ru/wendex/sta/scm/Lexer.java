package ru.wendex.sta.scm;

import java.util.ArrayList;
import java.util.HashMap;
import ru.wendex.sta.langbase.LexicError;
import ru.wendex.sta.langbase.Position;
import ru.wendex.sta.langbase.LexerException;
import ru.wendex.sta.scm.Token;
import java.io.IOException;

public class Lexer {
	private Token nextToken;
	private Position pos;
	private ArrayList<LexicError> errs = new ArrayList<>();
	private int lineb, columnb;
	private StringBuilder sb;
	private int sbi;
	
	public static Token toIdentToken(String identName, Token srcToken) {
		StringToken destToken = new StringToken(Token.IDENT, 0, 0, 0, 0, identName);
		destToken.assignCoordinates(srcToken);
		return destToken;
	}
	
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
	
	private static boolean isSeparator(int c) {
		return Character.isWhitespace(c) || c == (int)'(' || c == (int)')' || c == (int)'"' ||
			c == (int)',' || c == (int)'\'' || c == (int)'`' || c == (int)';';
	}
	
	private void addLexicError() {
		LexicError le = new LexicError(lineb, columnb, pos.prevLine(), pos.prevColumn(), sb.toString());
		errs.add(le);
	}
	
	private void tokenizeStringChar() throws IOException {
		int c = pos.peek();
		sb.appendCodePoint(c);
		if (c == (int)'\\') {
			pos.next();
			int c2 = pos.peek();
			if (c2 != Position.EOF_CHAR) {
				sb.appendCodePoint(c2);
			}
		}
		pos.next();
	}
	
	private void tokenizeStringSequence() throws IOException {
		sb = new StringBuilder();
		int c = pos.peek();
		sb.appendCodePoint(c);
		pos.next();
		for (;;) {
			c = pos.peek();
			if (c == Position.EOF_CHAR) {
				addLexicError();
				return;
			}
			if (c == (int)'"')
				break;
			tokenizeStringChar();
		}
		sb.appendCodePoint(c);
		pos.next();
		sbtokenizeString();
	}
	
	private void sbtokenizeString() {
		nextToken = new StringToken(Token.STRING_LITERAL, lineb, columnb, pos.getLine(), pos.getColumn()-1, sb.toString());
	}
	
	private void tokenizeSequenceForce() throws IOException {
		int c = pos.peek();
		for (;;) {
			if (isSeparator(c))
				break;
			sb.appendCodePoint(c);
			pos.next();
			c = pos.peek();
		}
	}
	
	private void tokenizeSequence() throws IOException {
		int c0 = pos.peek();
		pos.next();
		int c = pos.peek();
		if (c0 == (int)'#' && c == (int)'(') {
			nextToken = new Token(Token.VECTOR_PAREN, lineb, columnb, pos.getLine(), pos.getColumn());
			pos.next();
			return;
		}
		sb = new StringBuilder();
		sb.appendCodePoint(c0);
		tokenizeSequenceForce();
		sbtokenizeSequence();
	}
	
	private void sbtokenizeSequence() throws IOException {
		int c0 = sb.codePointAt(0);
		if (isInitial(c0))
			sbtokenizeIdent();
		else if (isDigit(c0))
			sbtokenizeNumber();
		else if (c0 == (int)'.') {
			if (sb.length() == 1)
				nextToken = new Token(Token.IMPROPER_PERIOD, lineb, columnb, pos.getLine(), pos.getColumn() - 1);
			else if (sb.length() == 3 && sb.codePointAt(1) == (int)'.' && sb.codePointAt(2) == (int)'.')
				nextToken = new StringToken(Token.IDENT, lineb, columnb, pos.getLine(), pos.getColumn()-1, sb.toString());
			else
				sbtokenizeNumber();
		} else if (c0 == (int)'+' || c0 == (int)'-') {
			if (sb.length() == 1)
				nextToken = new StringToken(Token.IDENT, lineb, columnb, pos.getLine(), pos.getColumn()-1, sb.toString());
			else
				sbtokenizeNumber();
		} else if (c0 == (int)'#') {
			if (sb.length() == 1) {
				addLexicError();
				return;
			}
			int c1 = sb.codePointAt(1);
			if (sb.length() == 2) {
				if (c1 == (int)'f')
					nextToken = new Token(Token.FALSE_LITERAL, lineb, columnb, pos.getLine(), pos.getColumn() - 1);
				else if (c1 == (int)'t')
					nextToken = new Token(Token.TRUE_LITERAL, lineb, columnb, pos.getLine(), pos.getColumn() - 1);
			}
			if (c1 == (int)'\\') {
				if (sb.length() == 2) {
					int c2 = pos.peek();
					sb.appendCodePoint(c2);
					pos.next();
				}
				nextToken = new StringToken(Token.CHAR_LITERAL, lineb, columnb, pos.getLine(), pos.getColumn() - 1, sb.toString());
			} else {
				sbtokenizeNumber();
			}
		}
	}
	
	private void sbtokenizeIdent() {
		int c0 = sb.codePointAt(0);
		if (!isInitial(c0)) {
			addLexicError();
			return;
		}
		for (int i = 1; i < sb.length(); i++) {
			int c = sb.codePointAt(i);
			if (!isSubsequent(c)) {
				addLexicError();
				return;
			}
		}
		nextToken = new StringToken(Token.IDENT, lineb, columnb, pos.getLine(), pos.getColumn()-1, sb.toString().toLowerCase());
	}
	
	
	
	
	private void sbtokenizeNumber() {
		boolean firstExact;
		sbi = 0;
		int c0 = sb.codePointAt(0);
		if (c0 != (int)'#') {
			sbtokenizeNumRadix(10);
			return;
		}
		if (sb.length() < 3) {
			addLexicError();
			return;
		}
		
		int radix = 10;
		int c1 = sb.codePointAt(1);
		if (c1 == (int)'i' || c1 == (int)'e' || c1 == (int)'I' || c1 == (int)'E')
			firstExact = true;
		else if (c1 == (int)'b'|| c1 == (int)'B') {
			firstExact = false;
			radix = 2;
		} else if (c1 == (int)'o' || c1 == (int)'O') {
			firstExact = false;
			radix = 8;
		} else if (c1 == (int)'d' || c1 == (int)'D') {
			firstExact = false;
		} else if (c1 == (int)'x' || c1 == (int)'X') {
			firstExact = false;
			radix = 16;
		} else {
			addLexicError();
			return;
		}
		
		int c2 = sb.codePointAt(2);
		if (c2 != (int)'#') {
			sbi = 2;
			sbtokenizeNumRadix(radix);
			return;
		}
		if (sb.length() < 5) {
			addLexicError();
			return;
		}
		int c3 = sb.codePointAt(3);
		if (firstExact) {
			if (c3 == (int)'b' || c3 == (int)'B') {
				firstExact = false;
				radix = 2;
			} else if (c3 == (int)'o' || c3 == (int)'O') {
				firstExact = false;
				radix = 8;
			} else if (c3 == (int)'d' || c3 == (int)'D') {
				firstExact = false;
			} else if (c3 == (int)'x' || c3 == (int)'X') {
				firstExact = false;
				radix = 16;
			} else {
				addLexicError();
				return;
			}
		} else {
			if (c3 != (int)'i' && c3 != (int)'I' && c3 != (int)'e' && c3 != (int)'E') {
				addLexicError();
				return;
			}	
		}
		sbi = 4;
		sbtokenizeNumRadix(radix);
	}
	
	
	private void sbtokenizeNumRadix(int radix) {
		try {
			int c0 = sb.codePointAt(sbi);
			boolean imaginaryAllowed = false;
			if (c0 == (int)'+' || c0 == (int)'-') {
				sbi++;
				if (sbi >= sb.length()) {
					addLexicError();
					return;
				}
				int c1 = sb.codePointAt(sbi);
				if ((c1 == (int)'i' || c1 == (int)'I') && sbi + 1 == sb.length()) {
					nextToken = new StringToken(Token.NUMBER_LITERAL, lineb, columnb, pos.getLine(), pos.getColumn()-1, sb.toString().toLowerCase());
					return;
				}
				imaginaryAllowed = true;
			}

			sbtokenizeUreal(radix);
			if (sbi == sb.length()) {
				nextToken = new StringToken(Token.NUMBER_LITERAL, lineb, columnb, pos.getLine(), pos.getColumn()-1, sb.toString().toLowerCase());
				return;
			}
			if (imaginaryAllowed) {
				int c1 = sb.codePointAt(sbi);
				if ((c1 == (int)'i' || c1 == (int)'I') && sbi + 1 == sb.length()) {
					nextToken = new StringToken(Token.NUMBER_LITERAL, lineb, columnb, pos.getLine(), pos.getColumn()-1, sb.toString().toLowerCase());
					return;
				}
			}

			c0 = sb.codePointAt(sbi);
			if (c0 != (int)'+' && c0 != (int)'-') {
				addLexicError();
				return;
			}
			sbi++;
			if (sbi >= sb.length()) {
				addLexicError();
				return;
			}
			
			int cj = sb.codePointAt(sbi);
			if ((cj == (int)'i' || cj == (int)'I') && sbi + 1 == sb.length()) {
				nextToken = new StringToken(Token.NUMBER_LITERAL, lineb, columnb, pos.getLine(), pos.getColumn()-1, sb.toString().toLowerCase());
				return;
			}
			
			sbtokenizeUreal(radix);
			if (sbi >= sb.length()) {
				addLexicError();
				return;
			}

			int ci = sb.codePointAt(sbi);
			if ((ci == (int)'i' || ci == (int)'I') && sbi + 1 == sb.length()) {
				nextToken = new StringToken(Token.NUMBER_LITERAL, lineb, columnb, pos.getLine(), pos.getColumn()-1, sb.toString().toLowerCase());
				return;
			}

			addLexicError();
		} catch (LexerException e) {
			addLexicError();
		}
	}
	
	private void sbtokenizeUreal(int radix) throws LexerException {
		int c0 = sb.codePointAt(sbi);
		if (c0 == (int)'.') {
			sbi++;
			sbtokenizeDigitSequence(radix);
			sbtokenizeOcthotorpSequenceOpt();
			sbtokenizeSuffix();
			return;
		}
		
		sbtokenizeDigitSequence(radix);
		if (sbi == sb.length())
			return;
		int c1 = sb.codePointAt(sbi);
		boolean isOctPeriod = false;
		if (c1 == (int)'.') {
			sbi++;
			sbtokenizeDigitSequenceOpt(radix);
			sbtokenizeOcthotorpSequenceOpt();
			sbtokenizeSuffix();
			return;
		} else if (c1 == (int)'#') {
			isOctPeriod = true;
		}
		sbtokenizeOcthotorpSequenceOpt();
		if (sbi == sb.length())
			return;
		c1 = sb.codePointAt(sbi);
		if (c1 == (int)'.' && isOctPeriod) {
			sbi++;
			sbtokenizeOcthotorpSequenceOpt();
			sbtokenizeSuffix();
			return;
		} else if (c1 == (int)'/') {
			sbi++;
			if (sbi == sb.length()) {
				addLexicError();
				return;
			}
			sbtokenizeDigitSequence(radix);
			sbtokenizeOcthotorpSequenceOpt();
		}
		
		sbtokenizeSuffix();
	}
	
	private void sbtokenizeDigitSequence(int radix) throws LexerException {
		int c0 = sb.codePointAt(sbi);
		if (!isRadixDigit(c0, radix)) {
			throw new LexerException();
		}
		sbi++;
		for (;;) {
			if (sbi == sb.length())
				break;
			int c = sb.codePointAt(sbi);
			if (!isRadixDigit(c, radix))
				break;
			sbi++;
		}
	}
	
	private void sbtokenizeDigitSequenceOpt(int radix) {
		for (;;) {
			if (sbi == sb.length())
				break;
			int c = sb.codePointAt(sbi);
			if (!isRadixDigit(c, radix))
				break;
			sbi++;
		}
	}
	
	private void sbtokenizeOcthotorpSequenceOpt() {
		for (;;) {
			if (sbi == sb.length())
				break;
			int c = sb.codePointAt(sbi);
			if (c != (int)'#')
				break;
			sbi++;
		}
	}
	
	private void sbtokenizeSuffix() throws LexerException {
		if (sbi == sb.length())
			return;
		int c = sb.codePointAt(sbi);
		if (c != (int)'e' && c != (int)'s' && c != (int)'f' && c != (int)'d' && c != (int)'l' &&
		    c != (int)'E' && c != (int)'S' && c != (int)'F' && c != (int)'D' && c != (int)'L')
			return;
		sbi++;
		if (sbi == sb.length()) {
			throw new LexerException();
		}
		c = sb.codePointAt(sbi);
		if (c == (int)'+' || c == (int)'-') {
			sbi++;
			if (sbi == sb.length()) {
				throw new LexerException();
			}
		}
		sbtokenizeDigitSequence(10);
	}
	
	private boolean isRadixDigit(int c, int radix) {
		if (c >= (int)'0' && c < radix + (int)'0' && c <= (int)'9')
			return true;
		int endLetterUp = (int)'A' + radix - 10;
		int endLetterDown = (int)'a' + radix - 10;
		return c >= (int)'A' && c < endLetterUp || c >= (int)'a' && c < endLetterDown;
	}
	
	private void tokenize() throws IOException {
		lineb = pos.getLine();
		columnb = pos.getColumn();
		int c = pos.peek();
		if (c == (int)'(') {
			nextToken = new Token(Token.LPAREN, lineb, columnb, pos.getLine(), pos.getColumn());
			pos.next();
		} else if (c == (int)')') {
			nextToken = new Token(Token.RPAREN, lineb, columnb, pos.getLine(), pos.getColumn());
			pos.next();
		} else if (c == (int)'\'') {
			nextToken = new Token(Token.QUOTE, lineb, columnb, pos.getLine(), pos.getColumn());
			pos.next();
		} else if (c == (int)'`') {
			nextToken = new Token(Token.QUASIQUOTE, lineb, columnb, pos.getLine(), pos.getColumn());
			pos.next();
		} else if (c == (int)',') {
			pos.next();
			int c2 = pos.peek();
			if (c2 == (int)'@') {
				nextToken = new Token(Token.UNQUOTE_SPLICING, lineb, columnb, pos.getLine(), pos.getColumn());
				pos.next();
			} else 
				nextToken = new Token(Token.UNQUOTE, lineb, columnb, lineb, columnb);
		} else if (c == (int)'"')
			tokenizeStringSequence();
		else if (c == Position.EOF_CHAR)
			nextToken = new Token(Token.EOF, lineb, columnb, pos.getLine(), pos.getColumn());
		else
			tokenizeSequence();
	}
				   
	private void skipWhitespaces() throws IOException {
		for (;;) {
			int c = pos.peek();
			if (Character.isWhitespace(c))
				pos.next();
			else if (c == (int)';') {
				pos.next();
				while (!Position.isNewline(pos.peek()) && pos.peek() != Position.EOF_CHAR)
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
