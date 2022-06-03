package ru.wendex.sta.scm;

import ru.wendex.sta.langbase.ParserException;

public class SymbolNode extends ObjectNode {

	public void printRec(String s, int k) {
		System.out.println(Node.endPrefix(s, k) + token.toString());
	}
	
	public SymbolNode(Token token) {
		super(token);
	}

	public String getValue() {
		return ((StringToken)token).getValue();
	}
}
