package ru.wendex.sta.aut;

import java.util.ArrayList;

public class Rule {
	private Symbol symbol;
	private ArrayList<Integer> args;
	private int res;
	
	public int getRes() {
		return res;
	}
	
	public Symbol getSymbol() {
		return symbol;
	}
	
	public ArrayList<Integer> getArgs() {
		return args;
	}
	
	public Rule(Symbol symbol, ArrayList<Integer> args, int res) {
		this.symbol = symbol;
		this.args = args;
		this.res = res;
	}
	
	public String toString() {
		String s = symbol.toString();
		if (args.size() > 0) {
			s += "(" + args.get(0);
			for (int i = 1; i < args.size(); i++) {
				s += ", "+ args.get(i);
			}
			s += ")";
		}
		s += " -> " + res;
		return s;
	}
}
