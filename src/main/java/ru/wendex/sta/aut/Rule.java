package ru.wendex.sta.aut;

import java.util.ArrayList;
import java.util.Objects;

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

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Rule rule = (Rule)o;
		if (res != rule.res || args.size() != rule.args.size() || !symbol.equals(rule.symbol))
			return false;
		for (int i = 0; i < args.size(); i++)
			if (!args.get(i).equals(rule.args.get(i)))
				return false;
		return true;
	}

	@Override
	public int hashCode() {
		return Objects.hash(symbol, args, res);
	}
}
