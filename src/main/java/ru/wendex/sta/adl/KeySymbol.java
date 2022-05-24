package ru.wendex.sta.adl;

public enum KeySymbol implements Symbol {
	NULL(0, "~null");
	
	private int arity;
	private String name;
	
	KeySymbol(int arity, String name) {
		this.arity = arity;
		this.name = name;
	}
	
	public String toString() {
		return name;
	}
	
	public int getArity() {
		return arity;
	}
}
