package ru.wendex.sta.adl;

import java.util.ArrayList;

public class Function {
	private String name;
	private Automata res;
	private ArrayList<Automata> args;
	
	public Function(String name, ArrayList<Automata> args, Automata res) {
		this.name = name;
		this.args = args;
		this.res = res;
	}
	
	public String getName() {
		return name;
	}
	
	public Automata getRes() {
		return res;
	}
	
	public ArrayList<Automata> getArgs() {
		return args;
	}
	
	public void print() {
		System.out.println("FUCTION " + name);
		System.out.println("FROM");
		for (Automata a : args)
			a.print();
		System.out.println("TO");
		res.print();
	}
}
