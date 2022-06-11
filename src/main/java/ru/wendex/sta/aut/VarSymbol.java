package ru.wendex.sta.aut;

import java.util.Objects;

public class VarSymbol implements Symbol {
    int var;

    @Override
    public int getArity() {
        return 0;
    }

    public VarSymbol(int var) {
        this.var = var;
    }

    @Override
    public String toString() {
        return "var " + var;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        VarSymbol varSymbol = (VarSymbol) o;
        return var == varSymbol.var;
    }

    public int getVar() {
        return var;
    }

    @Override
    public int hashCode() {
        return Objects.hash(var);
    }
}
