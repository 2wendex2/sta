package ru.wendex.sta.aut;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.HashSet;

public class Automata implements Cloneable {
	private ArrayList<Rule> rules;
	private ArrayList<Integer> finalStates;
	private ArrayList<EpsilonRule> epsilonRules;
	private int stateCount;
	
	public Automata() {}
	
	public static void epsilonClosure(ArrayList<Integer> states, ArrayList<EpsilonRule> epsilonRules) {
		HashSet<Integer> clsSet = new HashSet<>();
		for (int i : states)
			clsSet.add(i);
		boolean chngd = true;
		while (chngd) {
			chngd = false;
			for (EpsilonRule e : epsilonRules) {
				if (clsSet.contains(e.getRes()) && !clsSet.contains(e.getArg())) {
					chngd = true;
					clsSet.add(e.getArg());
					states.add(e.getArg());
				}
			}
		}
	}
	
	public static Automata createEmpty() {
		Automata a = new Automata();
		a.rules = new ArrayList<>();
		a.finalStates = new ArrayList<>();
		a.epsilonRules = new ArrayList<>();
		a.stateCount = 0;
		return a;
	}

	public static Automata createNull() {
		Automata a = createEmpty();
		a.addRuleSafe(new Rule(KeySymbol.NULL, new ArrayList<>(), 0));
		a.addFinalState(0);
		return a;
	}

	public static Automata createSymbol(String name) {
		Automata a = createEmpty();
		a.addRuleSafe(new Rule(new IdentSymbol(name), new ArrayList<>(), 0));
		a.addFinalState(0);
		return a;
	}

	public Object clone() {
		Automata a = new Automata();
		a.rules = (ArrayList<Rule>)rules.clone();
		a.finalStates = (ArrayList<Integer>)finalStates.clone();
		a.stateCount = stateCount;
		return a;
	}
	
	public Automata cloneRules() {
		Automata a = new Automata();
		a.rules = (ArrayList<Rule>)rules.clone();
		a.stateCount = stateCount;
		a.finalStates = new ArrayList<>();
		return a;
	}
	
	public void closeFinalEpsilon() {
		epsilonClosure(finalStates, epsilonRules);
	}
	
	public int newState() {
		stateCount++;
		return stateCount - 1;
	}
	
	public void addRule(Rule rule) {
		rules.add(rule);
	}
	
	public void addRuleSafe(Rule rule) {
		rules.add(rule);
		if (rule.getRes() >= stateCount)
			stateCount = rule.getRes() + 1;
		for (int i : rule.getArgs())
			if (i >= stateCount)
				stateCount = i +1;
	}
	
	public void addFinalState(int state) {
		finalStates.add(state);
	}
	
	public void addFinalStateSafe(int state) {
		finalStates.add(state);
		if (state >= stateCount)
			stateCount = state + 1;
	}
	
	public ArrayList<Integer> getFinalStates() {
		return finalStates;
	}
	
	public ArrayList<Rule> getRules() {
		return rules;
	}
	
	public Iterable<Rule> getStateRules(int state) {
		return new Iterable<Rule>() {
			public Iterator<Rule> iterator() {
				return new RuleResIterator(rules, state);
			}
		};
	}
	
	public Automata unionRules(Automata a) {
		Automata r = Automata.createEmpty();
		r.rules = (ArrayList<Rule>)rules.clone();
		r.stateCount = stateCount + a.stateCount;
		
		for (Rule rule : a.rules) {
			ArrayList<Integer> args = (ArrayList<Integer>)rule.getArgs().clone();
			for (int i = 0; i < args.size(); i++)
				args.set(i, args.get(i) + stateCount);
			
			r.addRule(new Rule(rule.getSymbol(), args, rule.getRes() + stateCount));
		}
		
		return r;
	}
	
	public int stateOffset(int state) {
		return state += stateCount;
	}
	
	public void print() {
		System.out.println("State count: " + stateCount);
		System.out.println("Rules:");
		for (Rule rule : rules)
			System.out.println(rule);
		for (EpsilonRule e : epsilonRules)
			System.out.println(e);
		String s = "Final states: ";
		for (int i : finalStates)
			s += i + " ";
		System.out.println(s);
		System.out.println();
	}
	
	public void addEpsilonRule(EpsilonRule epsilonRule) {
		epsilonRules.add(epsilonRule);
	}
}
