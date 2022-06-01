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

	void setArg(int arg) {
		this.arg = arg;
	}

	void setRes(int res) {
		this.res = res;
	}

	public EpsilonRule(int arg, int res) {
		this.arg = arg;
		this.res = res;
	}
	
	public String toString() {
		return "(" + arg + ") -> " + res;
	}
}
