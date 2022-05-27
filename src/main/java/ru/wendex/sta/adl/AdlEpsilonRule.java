package ru.wendex.sta.adl;

public class AdlEpsilonRule {
	private int arg;
	private int res;
	
	public AdlEpsilonRule(int arg, int res) {
		this.arg = arg;
		this.res = res;
	}
	
	public int getArg() {
		return arg;
	}
	
	public int getRes() {
		return res;
	}
	
	public String toString() {
		String s = "(" + arg + ")";
		s += " -> " + res;
		return s;
	}
}
