package ru.wendex.sta.typea;

import java.util.ArrayList;

public class TypeMatcherReport {
    private boolean isMatch;
    private ArrayList<FunctionReport> functionReports;

    public TypeMatcherReport(boolean isMatch, ArrayList<FunctionReport> functionReports) {
        this.isMatch = isMatch;
        this.functionReports = functionReports;
    }

    public void print() {
        for (FunctionReport functionReport : functionReports)
            functionReport.print();
        if (isMatch)
            System.out.println("YES");
        else
            System.out.println("NO");
    }

    public boolean getSuccess() {
        return isMatch;
    }
}
