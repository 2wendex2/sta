package ru.wendex.sta.scm;

import ru.wendex.sta.langbase.ParserException;

public class NullNode extends Node {
	boolean isExpr = false;
	public void printRec(String s, int k) {
		String h = "";
		if (isExpr)
			h = "EXPR ";
		System.out.println(Node.endPrefix(s, k) + h + "NULL_LIST");
	}
	
	public Node unquote() throws ParserException {
		isExpr = true;
		return this;
	}
}
