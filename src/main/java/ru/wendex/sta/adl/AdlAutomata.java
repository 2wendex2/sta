package ru.wendex.sta.adl;

import java.util.ArrayList;

public class AdlAutomata {
	private ArrayList<AdlRule> rules = new ArrayList<>();
	private ArrayList<AdlEpsilonRule> epsilonRules = new ArrayList<>();
	private ArrayList<Integer> finalStates = new ArrayList<>();
	private int stateCount = 0;
	
	public AdlAutomata(int stateCount, ArrayList<AdlRule> rules, ArrayList<AdlEpsilonRule> epsilonRules, ArrayList<Integer> finalStates) {
		this.stateCount = stateCount;
		this.rules = rules;
		this.epsilonRules = epsilonRules;
		this.finalStates = finalStates;
	}
	
	public ArrayList<AdlRule> getRules() {
		return rules;
	}
	
	public ArrayList<AdlEpsilonRule> getEpsilonRules() {
		return epsilonRules;
	}
	
	public ArrayList<Integer> getFinalStates() {
		return finalStates;
	}
	
	public void print() {
		System.out.println("State count: " + stateCount);
		System.out.println("Rules:");
		for (AdlRule rule : rules)
			System.out.println(rule);
		for (AdlEpsilonRule epsilonRule : epsilonRules)
			System.out.println(epsilonRule);
		String s = "Final states: ";
		for (int i : finalStates)
			s += i + " ";
		System.out.println(s);
	}
}
