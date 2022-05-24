package ru.wendex.sta.adl;

import java.util.ArrayList;
import java.util.HashMap;
import ru.wendex.sta.langbase.LexicError;
import ru.wendex.sta.langbase.Position;
import ru.wendex.sta.langbase.LexerException;
import java.io.IOException;

public class Lexer {
	private Token nextToken;
	private Position pos;
	private ArrayList<LexicError> errs = new ArrayList<>();
	private int lineb, columnb;
	private StringBuilder sb;
	private HashMap<String, Integer> identMap = new HashMap<>();
	private int identCount = 0;
	
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
	
	public int getIdentCount() {
		return identCount;
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
			c == (int)',' || c == (int)'\'' || c == (int)'`' || c == (int)';' || c == (int)'[' || c == (int)']' ||
			c == (int)'}' || c == (int)'{';
	}
	
	private void addLexicError() {
		LexicError le = new LexicError(lineb, columnb, pos.prevLine(), pos.prevColumn(), sb.toString());
		errs.add(le);
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
		sb = new StringBuilder();
		sb.appendCodePoint(c0);
		tokenizeSequenceForce();
		sbtokenizeSequence();
	}
	
	private void sbtokenizeSequence() throws IOException {
		String w = sb.toString();
		if (w.equals("def")) {
			nextToken = new Token(Token.DEF, lineb, columnb, pos.getLine(), pos.getColumn() - 1);
		} else if (w.equals("from")) {
			nextToken = new Token(Token.FROM, lineb, columnb, pos.getLine(), pos.getColumn() - 1);
		} else if (w.equals("to")) {
			nextToken = new Token(Token.TO, lineb, columnb, pos.getLine(), pos.getColumn() - 1);
		} else if (w.equals("final")) {
			nextToken = new Token(Token.FINAL, lineb, columnb, pos.getLine(), pos.getColumn() - 1);
		} else if (w.equals("epsilon")) {
			nextToken = new Token(Token.EPSILON, lineb, columnb, pos.getLine(), pos.getColumn() - 1);
		} else if (w.charAt(0) == '.') {
			sbtokenizeSchemeIdent();
		} else if (w.charAt(0) == '~') {
			sbtokenizeSpecIdent();
		} else {
			sbtokenizeIdent();
		}
	}
	
	private void sbtokenizeSpecIdent() {
		int c0 = sb.codePointAt(1);
		if (!isLetter(c0) && c0 != (int)'_') {
			addLexicError();
			return;
		}
		for (int i = 2; i < sb.length(); i++) {
			int c = sb.codePointAt(i);
			if (!isLetter(c) && !isDigit(c) && c != (int)'_') {
				String h = sb.toString();
				addLexicError();
				return;
			}
		}
		nextToken = new StringToken(Token.SPEC_IDENT, lineb, columnb, pos.getLine(), pos.getColumn()-1, sb.toString().toLowerCase().substring(1));
	}
	
	private void sbtokenizeSchemeIdent() {
		int c0 = sb.codePointAt(1);
		if (!isInitial(c0)) {
			String h = sb.toString();
			if (h.equals(".+") || h.equals(".-") || h.equals("...."))
				nextToken = new StringToken(Token.SCHEME_IDENT, lineb, columnb, pos.getLine(), pos.getColumn()-1, sb.toString().toLowerCase().substring(1));
			else
				addLexicError();
			return;
		}
		for (int i = 2; i < sb.length(); i++) {
			int c = sb.codePointAt(i);
			if (!isSubsequent(c)) {
				addLexicError();
				return;
			}
		}
		nextToken = new StringToken(Token.SCHEME_IDENT, lineb, columnb, pos.getLine(), pos.getColumn()-1, sb.toString().toLowerCase().substring(1));
	}
	
	private void sbtokenizeIdent() {
		int c0 = sb.codePointAt(0);
		if (!isLetter(c0) && c0 != (int)'_') {
			addLexicError();
			return;
		}
		for (int i = 1; i < sb.length(); i++) {
			int c = sb.codePointAt(i);
			if (!isLetter(c) && !isDigit(c) && c != (int)'_') {
				addLexicError();
				return;
			}
		}
		String s = sb.toString();
		Integer i = identMap.get(s);
		if (i == null) {
			i = identCount;
			identCount++;
			identMap.put(s, i);
		}
			
		nextToken = new IntToken(Token.IDENT, lineb, columnb, pos.getLine(), pos.getColumn()-1, i);
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
		} else if (c == (int)'[') {
			nextToken = new Token(Token.LSQUARE, lineb, columnb, pos.getLine(), pos.getColumn());
			pos.next();
		} else if (c == (int)']') {
			nextToken = new Token(Token.RSQUARE, lineb, columnb, pos.getLine(), pos.getColumn());
			pos.next();
		} else if (c == (int)'{') {
			nextToken = new Token(Token.LCURLY, lineb, columnb, pos.getLine(), pos.getColumn());
			pos.next();
		} else if (c == (int)'}') {
			nextToken = new Token(Token.RCURLY, lineb, columnb, pos.getLine(), pos.getColumn());
			pos.next();
		} else if (c == (int)'-') {
			pos.next();
			int c2 = pos.peek();
			if (c2 == (int)'>') {
				nextToken = new Token(Token.ARROW, lineb, columnb, pos.getLine(), pos.getColumn());
				pos.next();
			} else {
				LexicError le = new LexicError(lineb, columnb, pos.prevLine(), pos.prevColumn(), "-");
				errs.add(le);
			}
		} else if (c == (int)';') {
			nextToken = new Token(Token.SEMICOLON, lineb, columnb, pos.getLine(), pos.getColumn());
			pos.next();
		} else if (c == (int)'}') {
			nextToken = new Token(Token.RCURLY, lineb, columnb, pos.getLine(), pos.getColumn());
			pos.next();
		}
		else if (c == Position.EOF_CHAR)
			nextToken = new Token(Token.EOF, lineb, columnb, pos.getLine(), pos.getColumn());
		else {
			tokenizeSequence();
		}
	}
				   
	private void skipWhitespaces() throws IOException {
		for (;;) {
			int c = pos.peek();
			if (Character.isWhitespace(c))
				pos.next();
			else if (c == (int)'`') {
				pos.next();
				while (!Position.isNewline(pos.peek()) && pos.peek() != Position.EOF_CHAR)
					pos.next();
			} else if (c == (int)'\'') {
				sb = new StringBuilder();
				sb.appendCodePoint(c);
				pos.next();
				while (pos.peek() != '\'') {
					if (pos.peek() == Position.EOF_CHAR) {
						addLexicError();
						return;
					}
					sb.appendCodePoint(pos.peek());
					pos.next();
				}
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
