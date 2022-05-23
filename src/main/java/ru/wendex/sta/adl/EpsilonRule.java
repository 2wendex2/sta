package ru.wendex.sta.adl;

public class EpsilonRule {
	private int arg;
	private int res;
	
	public EpsilonRule(int arg, int res) {
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
