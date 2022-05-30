package ru.wendex.sta.aut;

import javax.print.attribute.IntegerSyntax;
import java.util.*;

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
		return () -> new RuleResIterator(rules, state);
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

	public void eliminateEpsilonRules() {
		boolean changed = true;
		while (changed) {
			changed = false;
			int n = rules.size();
			for (EpsilonRule epsilonRule : epsilonRules) {
				for (int i = 0; i < n; i++) {
					Rule oldRule = rules.get(i);
					if (oldRule.getRes() == epsilonRule.getArg()) {
						Rule newRule = new Rule(oldRule.getSymbol(), oldRule.getArgs(), epsilonRule.getRes());
						for (Rule rule2 : rules) {
							if (newRule.equals(rule2)) {
								break;
							}
							changed = true;
							rules.add(newRule);
						}
					}
				}
			}
		}
		closeFinalEpsilon();
		epsilonRules.clear();
	}

	public void determine() {
		ArrayList<Rule> resultRules = new ArrayList<>();
		HashSet<RuleSignature> newRulesSignature = new HashSet<>();
		HashMap<Integer, ArrayList<Integer>> stateToNew = new HashMap<>();
		HashMap<Integer, ArrayList<Integer>> stateToOld = new HashMap<>();
		HashMap<HashSet<Integer>, Integer> newStates = new HashMap<>();
		int newStateCount = 0;

		HashMap<Symbol, HashSet<Integer>> nullaryStates = new HashMap<>();
		for (Rule rule : rules) {
			if (rule.getArgs().size() == 0) {
				int oldState = rule.getRes();
				ArrayList<Integer> a = stateToNew.get(oldState);
				if (a == null) {
					a = new ArrayList<>();
					stateToNew.put(oldState, a);
				}
				a.add(newStateCount);
				ArrayList<Integer> b = new ArrayList<>();
				b.add(oldState);
				stateToOld.put(newStateCount, b);
				newStateCount++;
			}
		}

		for (Map.Entry<Integer, ArrayList<Integer>> a : stateToOld.entrySet()) {
			newStates.put(new HashSet<>(a.getValue()), a.getKey());
		}

		boolean changed = true;
		while (changed) {
			changed = false;
			HashMap<RuleSignature, HashSet<Integer>> newSignatures = new HashMap<>();
			ruleCycle:
			for (Rule rule : rules) {
				if (rule.getArgs().size() == 0)
					continue;

				for (int argState : rule.getArgs()) {
					ArrayList<Integer> lst = stateToNew.get(argState);
					if (lst == null) {
						continue ruleCycle;
					}
				}

				Iterator<ArrayList<Integer>> ait = new DetPermutIterator(rule.getArgs(), stateToNew);
				for (ArrayList<Integer> a = ait.next(); ait.hasNext(); a = ait.next()) {
					RuleSignature sign = new RuleSignature(rule.getSymbol(), a);
					HashSet<Integer> signSet = newSignatures.get(sign);
					if (signSet == null) {
						signSet = new HashSet<>();
						newSignatures.put(sign, signSet);
					}
					signSet.add(rule.getRes());
				}
			}
		}
	}

	
}
