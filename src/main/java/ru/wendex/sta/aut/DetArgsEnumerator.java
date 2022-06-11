package ru.wendex.sta.aut;

import java.util.ArrayList;
import java.util.HashMap;

public class DetArgsEnumerator {
    private ArrayList<ArrayList<Integer>> p;
    private ArrayList<Integer> indices;
    private ArrayList<Integer> current;

    public static DetArgsEnumerator createFromOldArgs(ArrayList<Integer> args,
                                                      HashMap<Integer, ArrayList<Integer>> stateToNew) {
        for (int arg : args)
            if (!stateToNew.containsKey(arg))
                return null;
        ArrayList<ArrayList<Integer>> p = new ArrayList<>(args.size());
        for (int arg : args)
            p.add(stateToNew.get(arg));
        ArrayList<Integer> indices = new ArrayList<>(args.size());
        for (int i = 0; i < args.size(); i++)
            indices.add(0);
        return new DetArgsEnumerator(p, indices);
    }

    private DetArgsEnumerator(ArrayList<ArrayList<Integer>> p, ArrayList<Integer> indices) {
        this.p = p;
        this.indices = indices;
        current = new ArrayList<>(indices.size());
        for (int i = 0; i < indices.size(); i++)
            current.add(p.get(i).get(indices.get(i)));
    }

    public ArrayList<Integer> peek() {
        return current;
    }

    public void next() {
        for (int j = indices.size() - 1; j >= 0; j--) {
            int e = indices.get(j);
            e++;
            if (e < p.get(j).size()) {
                indices.set(j, e);
                current.set(j, p.get(j).get(e));
                return;
            }
            indices.set(j, 0);
            current.set(j, p.get(j).get(0));
        }
        current = null;
    }
}
