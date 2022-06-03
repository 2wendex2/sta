package ru.wendex.sta.scm;

import ru.wendex.sta.langbase.ParserException;

public class PairNode extends Node {
	private Node car;
	private Node cdr;

	public void printRec(String s, int k) {
		System.out.println(Node.endPrefix(s, k) + "PAIR");
		car.printRec(Node.startPrefix(s, k), 1);
		cdr.printRec(Node.startPrefix(s, k), 2);
	}
	
	public Node getCar() {
		return car;
	}
	
	public Node getCdr() {
		return cdr;
	}
	
	public PairNode(Node car, Node cdr) {
		this.car = car;
		this.cdr = cdr;
	}
}
