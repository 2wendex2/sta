package ru.wendex.sta.typea;

import ru.wendex.sta.aut.Automata;

import java.util.ArrayList;

public class AutovarSignature {
    private ArrayList<Automata> args;
    private int autovar;

    public AutovarSignature(int autovar, ArrayList<Automata> args) {
        this.args = args;
        this.autovar = autovar;
    }

    public int getAutovar() {
        return autovar;
    }

    public boolean checkArgs(ArrayList<Automata> brgs) {
        if (args.size() != brgs.size())
            return false;
        for (int i = 0; i < args.size(); i++) {
            if (!args.get(i).isEquivalent(brgs.get(i)))
                return false;
        }
        return true;
    }
}
