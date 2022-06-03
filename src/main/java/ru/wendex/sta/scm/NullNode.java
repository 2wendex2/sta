package ru.wendex.sta.scm;

import ru.wendex.sta.langbase.ParserException;

public class NullNode extends Node {
	public void printRec(String s, int k) {
		System.out.println(Node.endPrefix(s, k) + "NULL_LIST");
	}
}
