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
	
	public Node unquote() throws ParserException {
		for (int i = 0; i < nodes.size(); i++)
			nodes.set(i, nodes.get(i).unquote());
		return this;
	}
	
	public void quasiquote() throws ParserException {
		for (int i = 0; i < nodes.size(); i++) {
			if (nodes.get(i) instanceof VectorNode)
				((VectorNode)nodes.get(i)).quasiquote();
			else if (nodes.get(i) instanceof PairNode)
				((VectorNode)nodes.get(i)).quasiquote();
		}
	}
}
