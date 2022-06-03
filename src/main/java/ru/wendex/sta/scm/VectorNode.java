package ru.wendex.sta.scm;

import java.util.ArrayList;
import ru.wendex.sta.langbase.ParserException;

public class VectorNode extends Node {
	private ArrayList<Node> nodes;
	
	public void printRec(String s, int k) {
		System.out.println(Node.endPrefix(s, k) + "VECTOR");
		
		int n = nodes.size();
		for (int i = 0; i < n - 1; i++) {
			nodes.get(i).printRec(startPrefix(s, k), 1);
		}
		
		if (n > 0)
			nodes.get(n - 1).printRec(startPrefix(s, k), 2);
	}
	
	public VectorNode(ArrayList<Node> nodes) {
		this.nodes = nodes;
	}
}
