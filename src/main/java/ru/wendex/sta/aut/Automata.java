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

	public static Automata createVar(int var) {
		Automata a = createEmpty();
		a.addRuleSafe(new Rule(new VarSymbol(var), new ArrayList<>(), 0));
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
		a.epsilonRules = (ArrayList<EpsilonRule>)epsilonRules.clone();
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
		for (int i = 0; i < rules.size(); i++) {
			Rule rule = rules.get(i);
			Integer newState = statesMap.get(rule.getRes());
			if (newState == null) {
				statesMap.put(rule.getRes(), stateCount);
				newState = stateCount;
				stateCount++;
			}
			rule = new Rule(rule.getSymbol(), rule.getArgs(), newState);
			rules.set(i, rule);
			for (int j = 0; j < rule.getArgs().size(); j++) {
				int arg = rule.getArgs().get(j);
				newState = statesMap.get(arg);
				if (newState == null) {
					statesMap.put(arg, stateCount);
					newState = stateCount;
					stateCount++;
				}
				rule.getArgs().set(j, newState);
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

	public HashSet<Symbol> getSymbolsSet() {
		HashSet<Symbol> symbolsSet = new HashSet<>();
		for (Rule rule : rules) {
			symbolsSet.add(rule.getSymbol());
		}
		return  symbolsSet;
	}

	public void complete(HashSet<Symbol> requiredSymbols) {
		HashSet<RuleSignature> signatureSet = new HashSet<>();
		HashSet<Symbol> symbolsSet = new HashSet<>(requiredSymbols);
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

			//newResStates -- map из сигнатуры правила в множество состояний. Состоит из всех ещё не добавленных правил
			//таких, что их аргументы уже построены. В качестве результата выступает пока что множество старых состояний
			//Цикл строит новую итерацию newResStates
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
							newResStates.put(new RuleSignature(rule.getSymbol(), (ArrayList<Integer>)newArgs.clone()), newResState);
						}
						newResState.add(rule.getRes());
					}
					ait.next();
					newArgs = ait.peek();
				}
			}

			//Проходимся по каждому правилу, что должно быть добавлено на итерации (из newResStates)

			for (Map.Entry<RuleSignature, HashSet<Integer>> entry : newResStates.entrySet()) {
				RuleSignature signature = entry.getKey();
				HashSet<Integer> oldStateSet = entry.getValue();

				//Для состояния-множества, в которое переходит правило вычисляем номер в новом автомате
				//Добавляем его в 2 соответствующих map'a:
				//stateToNew -- по старому состоянию получаем ArrayList новых
				//newStatesMap -- по состоянию-множеству получаем соответствующее состояние-число в новом автомате
				Integer newStateInt = newStatesMap.get(oldStateSet);
				if (newStateInt == null) {
					changed = true;
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

	public void complement(HashSet<Symbol> requiredSymbolsSet) {
		determine();
		complete(requiredSymbolsSet);
		HashSet<Integer> set = new HashSet<>(finalStates);
		finalStates.clear();
		for (int i = 0; i < stateCount; i++)
			if (!set.contains(i))
				finalStates.add(i);
	}

	public void intersect(Automata a) {
		int newStateCount = stateCount * a.stateCount;
		ArrayList<Integer> newFinalStates = new ArrayList<>();
		ArrayList<Rule> newRules = new ArrayList<>();
		ArrayList<EpsilonRule> newEpsilonRules = new ArrayList<>();

		for (int i : finalStates)
			for (int j : a.finalStates)
				newFinalStates.add(i * a.stateCount + j);
		for (Rule rule1 : rules)
			for (Rule rule2 : a.rules) {
				if (rule1.getArgs().size() == rule2.getArgs().size() && rule1.getSymbol().equals(rule2.getSymbol())) {
					ArrayList<Integer> newArgs = new ArrayList<>(rule1.getArgs().size());
					for (int k = 0; k < rule1.getArgs().size(); k++)
						newArgs.add(rule1.getArgs().get(k) * a.stateCount + rule2.getArgs().get(k));
					newRules.add(new Rule(rule1.getSymbol(), newArgs, rule1.getRes() * a.stateCount + rule2.getRes()));
				}
			}
		for (EpsilonRule epsilonRule1 : epsilonRules) {
			for (int state2 = 0; state2 < a.stateCount; state2++) {
				newEpsilonRules.add(new EpsilonRule(epsilonRule1.getArg() * a.stateCount + state2,
						epsilonRule1.getRes() * a.stateCount + state2));
			}
		}

		for (EpsilonRule epsilonRule2 : a.epsilonRules) {
			for (int state1 = 0; state1 < stateCount; state1++) {
				newEpsilonRules.add(new EpsilonRule(state1 * a.stateCount + epsilonRule2.getArg(),
						state1 * a.stateCount + epsilonRule2.getRes()));
			}
		}

		stateCount = newStateCount;
		finalStates = newFinalStates;
		rules = newRules;
		epsilonRules = newEpsilonRules;
	}

	private HashSet<Integer> constructAccessible() {
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

			for (EpsilonRule epsilonRule : epsilonRules){
				if (accessible.contains(epsilonRule.getRes()))
					continue;
				if (!accessible.contains(epsilonRule.getArg()))
					continue;
				accessible.add(epsilonRule.getRes());
				changed = true;
			}
		}
		return accessible;
	}

	public void eliminateNotAccessible() {
		ArrayList<Integer> newFinalStates = new ArrayList<>();
		ArrayList<Rule> newRules = new ArrayList<>();
		ArrayList<EpsilonRule> newEpsilonRules = new ArrayList<>();

		HashSet<Integer> accessible = constructAccessible();

		ruleCycle:
		for (Rule rule : rules) {
			if (!accessible.contains(rule.getRes()))
				continue;
			for (int arg : rule.getArgs())
				if (!accessible.contains(arg))
					continue ruleCycle;
			newRules.add(rule);
		}

		for (EpsilonRule epsilonRule : epsilonRules) {
			if (!accessible.contains(epsilonRule.getRes()))
				continue;
			if (!accessible.contains(epsilonRule.getArg()))
				continue;
			newEpsilonRules.add(epsilonRule);
		}

		for (int finalState : finalStates)
			if (accessible.contains(finalState))
				newFinalStates.add(finalState);

		finalStates = newFinalStates;
		rules = newRules;
		epsilonRules = newEpsilonRules;
		eliminateNotUsedStates();
	}

	public boolean isLanguageEmpty() {
		HashSet<Integer> accessible = constructAccessible();
		for (int f : finalStates)
			if (accessible.contains(f))
				return false;
		return true;
	}

	public Automata cloneRules() {
		Automata a = new Automata();
		a.rules = (ArrayList<Rule>)rules.clone();
		a.stateCount = stateCount;
		a.finalStates = new ArrayList<>();
		a.epsilonRules = new ArrayList<>();
		return a;
	}
	
	public Automata unionN(Automata a) {
		Automata r = unionRulesN(a);
		r.finalStates.addAll(finalStates);
		for (int finalState : a.finalStates)
			r.finalStates.add(finalState + stateCount);
		return r;
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

	public ArrayList<Integer> getClosedFinalStates() {
		epsilonCloseFinalStates();
		return finalStates;
	}

	public ArrayList<Rule> getRules() {
		return rules;
	}
	
	public Iterable<Rule> getStateRules(int state) {
		return () -> new RuleResIterator(rules, state);
	}
	
	public Automata unionRulesN(Automata a) {
		Automata r = Automata.createEmpty();
		r.rules = (ArrayList<Rule>)rules.clone();
		r.stateCount = stateCount + a.stateCount;
		
		for (Rule rule : a.rules) {
			ArrayList<Integer> args = (ArrayList<Integer>)rule.getArgs().clone();
			for (int i = 0; i < args.size(); i++)
				args.set(i, args.get(i) + stateCount);
			
			r.addRule(new Rule(rule.getSymbol(), args, rule.getRes() + stateCount));
		}

		r.epsilonRules.addAll(epsilonRules);
		for (EpsilonRule epsilonRule : a.epsilonRules)
			r.addEpsilonRule(new EpsilonRule(epsilonRule.getArg() + stateCount,
					epsilonRule.getRes() + stateCount));
		return r;
	}
	
	public int stateOffset(int state) {
		return state += stateCount;
	}
	
	public void print() {
		System.out.println("State count: " + stateCount);
		if (rules.size() != 0 || epsilonRules.size() != 0) {
			System.out.println("Rules:");
			for (Rule rule : rules)
				System.out.println(rule);
			for (EpsilonRule e : epsilonRules)
				System.out.println(e);
		}
		String s = "Final states: ";
		for (int i : finalStates)
			s += i + " ";
		System.out.println(s);
		System.out.println();
	}
	
	public void addEpsilonRule(EpsilonRule epsilonRule) {
		epsilonRules.add(epsilonRule);
	}

	public boolean isPresent(Symbol symbol) {
		HashSet<Integer> accessible = new HashSet<>();
		for (Rule rule : rules)
			if (rule.getSymbol().equals(symbol))
				accessible.add(rule.getRes());
		boolean changed = true;
		while (changed) {
			changed = false;
			ruleCycle:
			for (Rule rule : rules) {
				if (accessible.contains(rule.getRes()) || rule.getArgs().size() == 0)
					continue;
				for (int s : rule.getArgs()) {
					if (!accessible.contains(s)) {
						continue ruleCycle;
					}
				}

				accessible.add(rule.getRes());
				changed = true;
			}

			for (EpsilonRule epsilonRule : epsilonRules){
				if (accessible.contains(epsilonRule.getRes()))
					continue;
				if (!accessible.contains(epsilonRule.getArg()))
					continue;
				accessible.add(epsilonRule.getRes());
				changed = true;
			}
		}
		for (int i : finalStates)
			if (accessible.contains(i))
				return true;
		return false;
	}

	public void remove0Symbol(Symbol symbol) {
		eliminateEpsilonRules();
		HashSet<Integer> symbolStates = new HashSet<>();
		for (Rule rule : rules)
			if (rule.getSymbol().equals(symbol))
				symbolStates.add(rule.getRes());
		HashSet<Integer> newFinalComplement = new HashSet<>();
		for (int finalState : finalStates)
			if (symbolStates.contains(finalState)) {
				newFinalComplement.add(finalState);
				int n = rules.size();
				for (int i = 0; i < n; i++)
					if (rules.get(i).getRes() == finalState && !rules.get(i).getSymbol().equals(symbol))
						rules.add(new Rule(rules.get(i).getSymbol(), rules.get(i).getArgs(), stateCount));
			}
		ArrayList<Integer> newFinalStates = new ArrayList<>();
		for (int i : finalStates)
			if (!newFinalComplement.contains(i))
				newFinalStates.add(i);
		newFinalStates.add(stateCount);
		stateCount++;
		finalStates = newFinalStates;
	}

	public void substractFrom(Automata b) {
		complement(b.getSymbolsSet());
		intersect(b);
	}

	public boolean isSubAutomata(Automata other) {
		Automata c = (Automata)other.clone();
		c.substractFrom(this);
		return c.isLanguageEmpty();
	}

	public boolean isEquivalent(Automata other) {
		boolean t1 = this.isSubAutomata(other);
		boolean t2 = other.isSubAutomata(this);
		return this.isSubAutomata(other) && other.isSubAutomata(this);
	}

	public void substitute(int var, Automata a) {
		for (Rule rule : a.rules) {
			ArrayList<Integer> args = new ArrayList<>();
			for (int i : rule.getArgs())
				args.add(i + stateCount);
			rules.add(new Rule(rule.getSymbol(), args, rule.getRes() + stateCount));
		}
		for (EpsilonRule epsilonRule : a.epsilonRules)
			epsilonRules.add(new EpsilonRule(epsilonRule.getArg() + stateCount, epsilonRule.getRes() + stateCount));
		int n = rules.size();
		for (int k = 0; k < n; k++) {
			Symbol symbol = rules.get(k).getSymbol();
			if (symbol instanceof VarSymbol && ((VarSymbol)symbol).getVar() == var) {
				for (int i : a.finalStates) {
					epsilonRules.add(new EpsilonRule(i, rules.get(k).getRes()));
				}
				rules.set(k, rules.get(rules.size() - 1));
				rules.remove(rules.size() - 1);
			}
		}
		stateCount += a.stateCount;
	}

	public void substituteFinal(int var) {
		for (int k = 0; k < rules.size(); k++) {
			Symbol symbol = rules.get(k).getSymbol();
			if (symbol instanceof VarSymbol && ((VarSymbol)symbol).getVar() == var) {
				for (int i : finalStates) {
					epsilonRules.add(new EpsilonRule(i, rules.get(k).getRes()));
				}
				rules.set(k, rules.get(rules.size() - 1));
				rules.remove(rules.size() - 1);
				k--;
			}
		}
	}
}
