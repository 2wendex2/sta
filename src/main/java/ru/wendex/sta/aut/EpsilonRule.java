package ru.wendex.sta.aut;

import java.util.ArrayList;

public class EpsilonRule {
	private int arg;
	private int res;
	
	public int getRes() {
		return res;
	}
	
	public int getArg() {
		return arg;
	}
	
	public EpsilonRule(int arg, int res) {
		this.arg = arg;
		this.res = res;
	}
	
	public String toString() {
		return "(" + arg + ") -> " + res;
	}
}
