package ru.wendex.sta.typea;

import java.util.ArrayList;
import java.util.HashMap;
import ru.wendex.sta.scm.*;

public class ScmFunction {
	private String name;
	private ArrayList<String> args;
	private int state;
	private Node body;
	
	public ScmFunction(String name, ArrayList<String> args, Node body, int state) {
		this.name = name;
		this.args = args;
		this.state = state;
		this.body = body;
	}
	
	public String getName() {
		return name;
	}
	
	public ArrayList<String> getArgs() {
		return args;
	}
	
	public int getState() {
		return state;
	}
	
	public Node getBody() {
		return body;
	}
	
	public void print() {
		System.out.println("NAME " + name);
		System.out.println("ARGS");
		for (String s : args)
			System.out.println(s);
		System.out.println("STATE " + state);
		body.print();
	}
}
