package ru.wendex.sta.typea;

import java.util.ArrayList;
import java.util.HashMap;

public class ScmData {
	private HashMap<String, ScmFunction> funcs;
	private int stateCount;
	
	public ScmData(HashMap<String, ScmFunction> funcs, int stateCount) {
		this.funcs = funcs;
		this.stateCount = stateCount;
	}
	
	public HashMap<String, ScmFunction> getFuncs() {
		return funcs;
	}
	
	public int getStateCount() {
		return stateCount;
	}
	
	public void print() {
		for (HashMap.Entry<String, ScmFunction> entry : funcs.entrySet()) {
			System.out.println(entry.getKey());
			entry.getValue().print();
		}
		System.out.println(stateCount + "\n");
	}
}
