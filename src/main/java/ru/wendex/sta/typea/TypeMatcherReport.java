package ru.wendex.sta.typea;

public class TypeMatcherReport {
    private boolean isMatch;

    public TypeMatcherReport(boolean isMatch) {
        this.isMatch = isMatch;
    }

    public String toString() {
        return String.valueOf(isMatch);
    }

    public boolean getSuccess() {
        return isMatch;
    }
}
