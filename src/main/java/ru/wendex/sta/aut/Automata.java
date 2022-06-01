package ru.wendex.sta.aut;

import java.util.*;

public class Automata implements Cloneable {
	private ArrayList<Rule> rules;
	private ArrayList<Integer> finalStates;
	private ArrayList<EpsilonRule> epsilonRules;
	private int stateCount;
	
	private Automata() {}

	public Automata(int stateCount, ArrayList<Rule> rules, ArrayList<EpsilonRule> epsilonRules,
					ArrayList<Integer> finalStates) {
		this.stateCount = stateCount;
		this.rules = rules;
		this.epsilonRules = epsilonRules;
		this.finalStates = finalStates;
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

	public static Automata createTrue() {
		Automata a = createEmpty();
		a.addRuleSafe(new Rule(KeySymbol.TRUE, new ArrayList<>(), 0));
		a.addFinalState(0);
		return a;
	}

	public static Automata createFalse() {
		Automata a = createEmpty();
		a.addRuleSafe(new Rule(KeySymbol.TRUE, new ArrayList<>(), 0));
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

	private static void epsilonCloseStateArray(ArrayList<Integer> states, ArrayList<EpsilonRule> epsilonRules) {
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

	public void epsilonCloseFinalStates() {
		epsilonCloseStateArray(finalStates, epsilonRules);
	}

	public void eliminateEpsilonRules() {
		HashSet<Rule> rulesSet = new HashSet<>();
		ArrayList<Rule> newRules = (ArrayList<Rule>)rules.clone();
		for (Rule rule : rules)
			rulesSet.add(rule);
		boolean changed = true;
		while (changed) {
			changed = false;
			int n = rules.size();
			for (EpsilonRule epsilonRule : epsilonRules) {
				for (Rule oldRule : rules) {
					if (oldRule.getRes() == epsilonRule.getArg()) {
						Rule newRule = new Rule(oldRule.getSymbol(), oldRule.getArgs(), epsilonRule.getRes());
						if (rulesSet.contains(newRule)) {
							continue;
						}
						changed = true;
						rulesSet.add(newRule);
						newRules.add(newRule);
					}
				}
				rules = (ArrayList<Rule>)newRules.clone();
			}
		}
		epsilonCloseFinalStates();
		epsilonRules.clear();
	}

	public void eliminateNotUsedStates() {
		HashMap<Integer, Integer> statesMap = new HashMap<>();
		stateCount = 0;
		for (Rule rule : rules) {
			Integer newState = statesMap.get(rule.getRes());
			if (newState == null) {
				statesMap.put(rule.getRes(), stateCount);
				newState = stateCount;
				stateCount++;
			}
			rule.setRes(newState);
			for (int i = 0; i < rule.getArgs().size(); i++) {
				int arg = rule.getArgs().get(i);
				newState = statesMap.get(arg);
				if (newState == null) {
					statesMap.put(arg, stateCount);
					newState = stateCount;
					stateCount++;
				}
				rule.getArgs().set(i, newState);
			}
		}

		for (int i = 0; i < finalStates.size(); i++) {
			Integer newState = statesMap.get(finalStates.get(i));
			if (newState == null) {
				statesMap.put(finalStates.get(i), stateCount);
				newState = stateCount;
				stateCount++;
			}
			finalStates.set(i, newState);
		}

		for (EpsilonRule rule : epsilonRules) {
			Integer newState = statesMap.get(rule.getRes());
			if (newState == null) {
				statesMap.put(rule.getRes(), stateCount);
				newState = stateCount;
				stateCount++;
			}
			rule.setRes(newState);
			newState = statesMap.get(rule.getArg());
			if (newState == null) {
				statesMap.put(rule.getArg(), stateCount);
				newState = stateCount;
				stateCount++;
			}
			rule.setArg(newState);
		}
	}

	public void complete() {
		HashSet<RuleSignature> signatureSet = new HashSet<>();
		HashSet<Symbol> symbolsSet = new HashSet<>();
		for (Rule rule : rules) {
			symbolsSet.add(rule.getSymbol());
			signatureSet.add(new RuleSignature(rule.getSymbol(), rule.getArgs()));
		}

		int dummyState = stateCount;
		stateCount++;
		for (Symbol symbol : symbolsSet) {
			SymbolArgsEnumerator enumerator = new SymbolArgsEnumerator(symbol.getArity(), stateCount);
			ArrayList<Integer> args = enumerator.peek();
			while (args != null) {
				RuleSignature signature = new RuleSignature(symbol, (ArrayList<Integer>) args.clone());
				if (!signatureSet.contains(signature)) {
					rules.add(new Rule(symbol, (ArrayList<Integer>) args.clone(), dummyState));
				}
				enumerator.next();
				args = enumerator.peek();
			}
		}
	}

	public void determine() {
		eliminateEpsilonRules();
		HashMap<HashSet<Integer>, Integer> newStatesMap = new HashMap<>();
		HashMap<Integer, ArrayList<Integer>> stateToNew = new HashMap<>();

		ArrayList<Rule> resultRules = new ArrayList<>();
		HashSet<RuleSignature> newRulesSignatures = new HashSet<>();

		int newStateCount = 0;

		boolean changed = true;
		while (changed) {
			changed = false;
			HashMap<RuleSignature, HashSet<Integer>> newResStates = new HashMap<>();
			for (Rule rule : rules) {
				DetArgsEnumerator ait = DetArgsEnumerator.createFromOldArgs(rule.getArgs(), stateToNew);
				if (ait == null)
					continue;
				ArrayList<Integer> newArgs = ait.peek();
				while (newArgs != null) {
					RuleSignature signature = new RuleSignature(rule.getSymbol(), newArgs);
					if (!newRulesSignatures.contains(signature)) {
						HashSet<Integer> newResState = newResStates.get(signature);
						if (newResState == null) {
							newResState = new HashSet<>();
							newResStates.put(signature, newResState);
						}
						newResState.add(rule.getRes());
					}
					ait.next();
					newArgs = ait.peek();
				}
			}

			for (Map.Entry<RuleSignature, HashSet<Integer>> entry : newResStates.entrySet()) {
				changed = true;
				RuleSignature signature = entry.getKey();
				HashSet<Integer> oldStateSet = entry.getValue();

				Integer newStateInt = newStatesMap.get(oldStateSet);
				if (newStateInt == null) {
					newStateInt = newStateCount;
					newStateCount++;
					newStatesMap.put(oldStateSet, newStateInt);
					for (int oldState : oldStateSet) {
						ArrayList<Integer> newStateArray = stateToNew.get(oldState);
						if (newStateArray == null) {
							newStateArray = new ArrayList<>();
							stateToNew.put(oldState, newStateArray);
						}
						newStateArray.add(newStateInt);
					}
				}

				newRulesSignatures.add(signature);
				resultRules.add(new Rule(signature.getSymbol(), signature.getArgs(), newStateInt));
			}
		}

		HashSet<Integer> newFinalStates = new HashSet<>();
		for (int finalState : finalStates) {
			ArrayList<Integer> newFinalStateSet = stateToNew.get(finalState);
			if (newFinalStateSet == null)
				continue;
			newFinalStates.addAll(newFinalStateSet);
		}

		rules = resultRules;
		stateCount = newStateCount;
		finalStates = new ArrayList<>(newFinalStates);
	}
	
	public Automata cloneRules() {
		Automata a = new Automata();
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







	public void complement() {
		determine();
		complete();
		HashSet<Integer> set = new HashSet<>(finalStates);
		finalStates.clear();
		for (int i = 0; i < stateCount; i++)
			if (!set.contains(i))
				finalStates.add(i);
	}

	public Automata intersect(Automata a) {
		Automata r = createEmpty();
		r.stateCount = stateCount * a.stateCount;
		for (int i : finalStates)
			for (int j : a.finalStates)
				r.addFinalState(i * stateCount + j);
		for (Rule rule1 : rules)
			for (Rule rule2 : a.rules) {
				if (rule1.getArgs().size() == rule2.getArgs().size() && rule1.getSymbol().equals(rule2.getSymbol())) {
					ArrayList<Integer> newArgs = new ArrayList<>(rule1.getArgs().size());
					for (int k = 0; k < rule1.getArgs().size(); k++)
						newArgs.add(rule1.getArgs().get(k) * stateCount + rule2.getArgs().get(k));
					r.rules.add(new Rule(rule1.getSymbol(), newArgs, rule1.getRes() * stateCount + rule2.getRes()));
				}
			}
		return r;
	}

	public boolean isLanguageEmpty() {
		HashSet<Integer> accessible = new HashSet<>();
		boolean changed = true;
		while (changed) {
			changed = false;
			ruleCycle:
			for (Rule rule : rules) {
				if (accessible.contains(rule.getRes()))
					continue;
				for (int s : rule.getArgs()) {
					if (!accessible.contains(s)) {
						continue ruleCycle;
					}
				}

				accessible.add(rule.getRes());
				changed = true;
			}
		}
		for (int f : finalStates)
			if (accessible.contains(f))
				return false;
		return true;
	}
}
