package ru.wendex.sta.scm;

import java.util.ArrayList;
import ru.wendex.sta.langbase.ParserException;


public class Ast {
	private ArrayList<Node> nodes;
	
	public Ast(ArrayList<Node> nodes) {
		this.nodes = nodes;
	}
	
	public void print() {
		for (Node node : nodes) {
			node.printRec("", 0);
			System.out.println();
		}
	}
	
	public void unquote() throws ParserException {
		for (int i = 0; i < nodes.size(); i++) {
			nodes.set(i, nodes.get(i).unquote());
		}
	}
}
