package ru.wendex.sta.adl;

import java.util.ArrayList;
import ru.wendex.sta.langbase.ParserException;
import java.util.Iterator;

public class Automata {
	private ArrayList<Rule> rules = new ArrayList<>();
	private ArrayList<EpsilonRule> epsilonRules = new ArrayList<>();
	private ArrayList<Integer> finalStates = new ArrayList<>();
	private int stateCount = 0;
	
	public Automata(int stateCount, ArrayList<Rule> rules, ArrayList<EpsilonRule> epsilonRules, ArrayList<Integer> finalStates) {
		this.stateCount = stateCount;
		this.rules = rules;
		this.epsilonRules = epsilonRules;
		this.finalStates = finalStates;
	}
	
	public ArrayList<Rule> getRules() {
		return rules;
	}
	
	public ArrayList<EpsilonRule> getEpsilonRules() {
		return epsilonRules;
	}
	
	public ArrayList<Integer> getFinalStates() {
		return finalStates;
	}
	
	public void print() {
		System.out.println("State count: " + stateCount);
		System.out.println("Rules:");
		for (Rule rule : rules)
			System.out.println(rule);
		for (EpsilonRule epsilonRule : epsilonRules)
			System.out.println(epsilonRule);
		String s = "Final states: ";
		for (int i : finalStates)
			s += i + " ";
		System.out.println(s);
	}
}
