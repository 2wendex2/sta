package ru.wendex.sta.aut;

public class IdentSymbol implements Symbol {
	private String s;
	
	public IdentSymbol(String s) {
		this.s = s;
	}
	
	public int getArity() {
		return 0;
	}
	
	public String getValue() {
		return s;
	}
	
	public boolean equals(IdentSymbol is) {
		return is.s.equals(s);
	}
	
	public String toString() {
		return "symbol " + s;
	}
}
