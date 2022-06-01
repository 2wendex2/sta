package ru.wendex.sta.typea;

import java.util.ArrayList;

public class TypeaData {
    private ArrayList<TypeaFunction> funcs;

    TypeaData(ArrayList<TypeaFunction> funcs) {
        this.funcs = funcs;
    }

    public ArrayList<TypeaFunction> getFuncs() {
        return funcs;
    }
}
