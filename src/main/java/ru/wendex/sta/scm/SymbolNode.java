package ru.wendex.sta.scm;

import ru.wendex.sta.langbase.ParserException;

public class SymbolNode extends ObjectNode {
	private boolean isIdentFlag = false;
	
	public void printRec(String s, int k) {
		String h = "";
		if (isIdentFlag)
			h = "IDENT ";
		System.out.println(Node.endPrefix(s, k) + h + token.toString());
	}
	
	public SymbolNode(Token token) {
		super(token);
	}
	
	public Node unquote() throws ParserException {
		isIdentFlag = true;
		return this;
	}
	
	public boolean isIdent() {
		return isIdentFlag;
	}
	
	public String getValue() {
		return ((StringToken)token).getValue();
	}
}
