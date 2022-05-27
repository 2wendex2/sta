package ru.wendex.sta.adl;

public class AlgebraicSymbol implements AdlSymbol {
	private String s;
	private int arity;
	
	public AlgebraicSymbol(String s, int arity) {
		this.s = s;
		this.arity = arity;
	}
	
	public int getArity() {
		return arity;
	}
	
	public String getValue() {
		return s;
	}
	
	public boolean equals(AlgebraicSymbol is) {
		return is.s.equals(s);
	}
	
	public String toString() {
		return s;
	}
}
