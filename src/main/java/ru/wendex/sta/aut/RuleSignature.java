package ru.wendex.sta.aut;

import java.util.ArrayList;
import java.util.Objects;

public class RuleSignature {
    private Symbol symbol;
    private ArrayList<Integer> args;

    public Symbol getSymbol() {
        return symbol;
    }

    public ArrayList<Integer> getArgs() {
        return args;
    }

    public RuleSignature(Symbol symbol, ArrayList<Integer> args) {
        this.symbol = symbol;
        this.args = args;
    }

    public String toString() {
        String s = symbol.toString();
        if (args.size() > 0) {
            s += "(" + args.get(0);
            for (int i = 1; i < args.size(); i++) {
                s += ", "+ args.get(i);
            }
            s += ")";
        }
        return s;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RuleSignature rule = (RuleSignature) o;
        if (args.size() != rule.args.size() || !symbol.equals(rule.symbol))
            return false;
        for (int i = 0; i < args.size(); i++)
            if (!args.get(i).equals(rule.args.get(i)))
                return false;
        return true;
    }

    @Override
    public int hashCode() {
        return Objects.hash(symbol, args);
    }
}
