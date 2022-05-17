package ru.wendex.sta.scm;

import java.util.ArrayList;

public class VectorNode implements Node {
	private ArrayList<Node> nodes;
	
	public void printRec(String s, int k) {
		System.out.println(Node.endPrefix(s, k) + "VECTOR");
		
		//car.printRec(startPrefix(s, k), 1);
		//car.printRec(startPrefix(s, k), 2);
	}
	
	public VectorNode(ArrayList<Node> nodes) {
		this.nodes = nodes;
	}
}
