package ru.wendex.sta.adl;

import java.util.ArrayList;

public class Function {
	private String name;
	private AdlAutomata res;
	private ArrayList<AdlAutomata> args;
	
	public Function(String name, ArrayList<AdlAutomata> args, AdlAutomata res) {
		this.name = name;
		this.args = args;
		this.res = res;
	}
	
	public String getName() {
		return name;
	}
	
	public AdlAutomata getRes() {
		return res;
	}
	
	public ArrayList<AdlAutomata> getArgs() {
		return args;
	}
	
	public void print() {
		System.out.println("FUCTION " + name);
		System.out.println("FROM");
		for (AdlAutomata a : args)
			a.print();
		System.out.println("TO");
		res.print();
	}
}
