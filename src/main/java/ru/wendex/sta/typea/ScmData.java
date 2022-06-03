package ru.wendex.sta.typea;

import ru.wendex.sta.scm.Node;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;

public class ScmData {
	public static final HashSet<String> STANDART_FUNCTIONS = new HashSet<>(Arrays.asList("cons", "car", "cdr",
			"quote", "null?", "symbol?", "boolean?", "equals?", "if"));

	private HashMap<String, ScmFunction> funcs;
	private HashMap<String, Node> vars;
	private int stateCount;
	
	ScmData(HashMap<String, ScmFunction> funcs, HashMap<String, Node> vars, int stateCount) {
		this.funcs = funcs;
		this.stateCount = stateCount;
		this.vars = vars;
	}
	
	public HashMap<String, ScmFunction> getFuncs() {
		return funcs;
	}

	public HashMap<String, Node> getVars() {
		return vars;
	}

	public int getStateCount() {
		return stateCount;
	}
	
	public void print() {
		for (HashMap.Entry<String, ScmFunction> entry : funcs.entrySet()) {
			System.out.println(entry.getKey());
			entry.getValue().print();
		}
		System.out.println("VARS");
		for (HashMap.Entry<String, Node> entry : vars.entrySet()) {
			System.out.println(entry.getKey());
			entry.getValue().print();
		}
		System.out.println(stateCount + "\n");
	}
}
