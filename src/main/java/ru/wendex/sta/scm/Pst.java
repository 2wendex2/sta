package ru.wendex.sta.scm;

import java.util.ArrayList;

public class Pst {
	private ArrayList<Node> nodes;
	
	public Pst(ArrayList<Node> nodes) {
		this.nodes = nodes;
	}
	
	public void print() {
		for (Node node : nodes) {
			node.printRec("", 0);
			System.out.println();
		}
	}
}
