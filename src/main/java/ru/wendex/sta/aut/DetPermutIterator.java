package ru.wendex.sta.aut;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class DetPermutIterator implements Iterator<ArrayList<Integer>> {
    private ArrayList<ArrayList<Integer>> p;
    private ArrayList<Integer> indices;
    private boolean hasNextFlag;

    public DetPermutIterator(ArrayList<Integer> args, HashMap<Integer, ArrayList<Integer>> stateToNew) {
        p = new ArrayList<>(args.size());
        for (int i = 0; i < args.size(); i++) {
            p.add(stateToNew.get(i));
        }
        indices = new ArrayList<>(args.size());
        for (int i = 0; i < args.size(); i++)
            indices.add(0);
    }

    private void updateIndices() {
        for (int j = p.size() - 1; j >= 0; j--) {
            int e = indices.get(j);
            e++;
            if (e < p.get(j).size()) {
                indices.set(j, e);
                return;
            }
            indices.set(j, 0);
        }
        hasNextFlag = false;
    }

    @Override
    public ArrayList<Integer> next() {
        ArrayList<Integer> r = new ArrayList<>(p.size());
        return r;
    }

    @Override
    public boolean hasNext() {
        return hasNextFlag;
    }
}
