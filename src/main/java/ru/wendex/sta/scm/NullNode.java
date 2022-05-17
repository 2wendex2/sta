package ru.wendex.sta.scm;

public class NullNode implements Node {
	public void printRec(String s, int k) {
		System.out.println(Node.endPrefix(s, k) + "NULL_LIST");
	}
}
