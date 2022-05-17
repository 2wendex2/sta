package ru.wendex.sta.scm;

public class ObjectNode implements Node {
	private Token token;
	
	public void printRec(String s, int k) {
		System.out.println(Node.endPrefix(s, k) + token.toString());
	}
	
	public ObjectNode(Token token) {
		this.token = token;
	}
}
