package ru.wendex.sta.scm;

public class PairNode implements Node {
	private Node car;
	private Node cdr;
	
	public void printRec(String s, int k) {
		System.out.println(Node.endPrefix(s, k) + "PAIR");
		car.printRec(Node.startPrefix(s, k), 1);
		cdr.printRec(Node.startPrefix(s, k), 2);
	}
	
	public PairNode(Node car, Node cdr) {
		this.car = car;
		this.cdr = cdr;
	}
}
