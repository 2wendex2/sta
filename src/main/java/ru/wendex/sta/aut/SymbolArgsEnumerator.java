package ru.wendex.sta.aut;

import java.util.ArrayList;
import java.util.Iterator;

public class SymbolArgsEnumerator {
    private int stateCount;
    private ArrayList<Integer> current;

    public SymbolArgsEnumerator(int arity, int stateCount) {
        this.stateCount = stateCount;
        if (stateCount > 0) {
            current = new ArrayList<>(arity);
            for (int i = 0; i < arity; i++)
                current.add(0);
        } else {
            this.current = null;
        }
    }

    public void next() {
        for (int i = current.size() - 1; i >= 0; i--) {
            int st = current.get(i);
            st++;
            if (st < stateCount) {
                current.set(i, st);
                return;
            } else {
                current.set(i, 0);
            }
        }
        current = null;
    }

    public ArrayList<Integer> peek() {
        return current;
    }
}
