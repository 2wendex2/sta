package ru.wendex.sta.typea;

import ru.wendex.sta.aut.Automata;

import java.util.ArrayList;
import java.util.HashMap;

public class TypeaFunction {
	private String name;
	private ArrayList<Automata> args;
	private Automata res;

	public TypeaFunction(String name, ArrayList<Automata> args, Automata res) {
		this.name = name;
		this.args = args;
		this.res = res;
	}

	public String getName() {
		return name;
	}

	public ArrayList<Automata> getArgs() {
		return args;
	}

	public Automata getRes() {
		return res;
	}
}
