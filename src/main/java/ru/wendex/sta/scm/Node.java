package ru.wendex.sta.scm;

import java.util.ArrayList;

public interface Node {
	void printRec(String s, int k);
	
	public static String startPrefix(String s, int k) {
		if (k == 1) {
        		return s + "│";
    		} else if (k == 2) {
        		return s + " ";
    		} else {
    			return s;
    		}
	}
	
	public static String endPrefix(String s, int k) {
		if (k == 1) {
        		return s + "├";
    		} else if (k ==2) {
        		return s + "└";
    		} else {
    			return s;
    		}
	}
}
