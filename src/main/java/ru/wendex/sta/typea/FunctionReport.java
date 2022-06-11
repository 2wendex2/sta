package ru.wendex.sta.typea;

import ru.wendex.sta.aut.Automata;

import java.util.ArrayList;

public class FunctionReport {
    private boolean isMatch;
    private Automata defRes;
    private String name;
    private ArrayList<Automata> args;
    private Automata declRes;
    private Exception exception;

    public FunctionReport(TypeaFunction f, Automata res, boolean isMatch, Exception exception) {
        this.isMatch = isMatch;
        this.exception = exception;
        this.defRes = res;
        this.name = f.getName();
        this.args = f.getArgs();
        this.declRes = f.getRes();
    }

    public boolean isMatch() {
        return isMatch;
    }

    public Automata getDefRes() {
        return defRes;
    }

    public String getName() {
        return name;
    }

    public ArrayList<Automata> getArgs() {
        return args;
    }

    public Automata getDeclRes() {
        return declRes;
    }

    public Exception getException() {
        return exception;
    }

    public void print() {
        String yesno;
        if (isMatch)
            yesno = "YES";
        else
            yesno = "NO";
        System.out.println("FUNCTION " + name + " " + yesno);
        if (defRes != null)
            defRes.print();
        else
            System.out.println(exception.getMessage() + "\n");
    }
}
