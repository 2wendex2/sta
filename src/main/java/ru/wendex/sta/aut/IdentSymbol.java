package ru.wendex.sta.aut;

import java.util.Objects;

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
	
	public String toString() {
		return "symbol " + s;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		IdentSymbol that = (IdentSymbol) o;
		return s.equals(that.s);
	}

	@Override
	public int hashCode() {
		return Objects.hash(s);
	}
}
