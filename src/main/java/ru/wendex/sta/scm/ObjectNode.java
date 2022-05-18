package ru.wendex.sta.scm;

public class ObjectNode extends Node {
	protected Token token;
	
	public void printRec(String s, int k) {
		System.out.println(Node.endPrefix(s, k) + token.toString());
	}
	
	public ObjectNode(Token token) {
		this.token = token;
	}
}
