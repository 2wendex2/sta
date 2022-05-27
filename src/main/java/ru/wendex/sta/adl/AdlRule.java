package ru.wendex.sta.adl;

import java.util.ArrayList;

public class AdlRule {
	private AdlSymbol symbol;
	private ArrayList<Integer> args;
	private int res;
	
	public AdlRule(AdlSymbol symbol, ArrayList<Integer> args, int res) {
		this.symbol = symbol;
		this.args = args;
		this.res = res;
	}
	
	public AdlSymbol getSymbol() {
		return symbol;
	}

	public ArrayList<Integer> getArgs() {
		return args;
	}
	
	public int getRes() {
		return res;
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
