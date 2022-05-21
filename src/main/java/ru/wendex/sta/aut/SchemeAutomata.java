package ru.wendex.sta.aut;

import java.util.ArrayList;
import ru.wendex.sta.langbase.ParserException;
import java.util.Iterator;

public class SchemeAutomata implements Cloneable {
	private ArrayList<Rule> rules;
	private ArrayList<Integer> finalStates;
	private int stateCount;
	
	public SchemeAutomata() {}
	
	public static SchemeAutomata createEmpty() {
		SchemeAutomata a = new SchemeAutomata();
		a.rules = new ArrayList<>();
		a.finalStates = new ArrayList<>();
		a.stateCount = 0;
		return a;
	}
	
	public Object clone() {
		SchemeAutomata a = new SchemeAutomata();
		a.rules = (ArrayList<Rule>)rules.clone();
		a.finalStates = (ArrayList<Integer>)finalStates.clone();
		a.stateCount = stateCount;
		return a;
	}
	
	public SchemeAutomata cloneRules() {
		SchemeAutomata a = new SchemeAutomata();
		a.rules = (ArrayList<Rule>)rules.clone();
		a.stateCount = stateCount;
		a.finalStates = new ArrayList<>();
		return a;
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
	
	public SchemeAutomata unionRules(SchemeAutomata a) {
		SchemeAutomata r = SchemeAutomata.createEmpty();
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
		String s = "Final states: ";
		for (int i : finalStates)
			s += i + " ";
		System.out.println(s);
		System.out.println();
	}
	
	
}
