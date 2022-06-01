package ru.wendex.sta.aut;

public enum KeySymbol implements Symbol {
	NULL(0, "null"),
	PAIR(2, "pair"),
	TRUE(0, "true"),
	FALSE(0, "false");
	
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
