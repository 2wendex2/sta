package ru.wendex.sta.adl;

import java.util.ArrayList;

public class FunctionList {
	private ArrayList<Function> list;
	
	public FunctionList(ArrayList<Function> list) {
		this.list = list;
	}
	
	public ArrayList<Function> getList() {
		return list;
	}
	
	public void print() {
		for (Function f : list) {
			f.print();
			System.out.println("\n");
		}
	}
}
