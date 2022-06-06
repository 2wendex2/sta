package ru.wendex.sta.aut;

import java.util.ArrayList;
import java.util.HashSet;

public class Procedures {
	private static Automata createBooleanAutomata(boolean isTrue, boolean isFalse) {
		Automata r = Automata.createEmpty();
		if (isTrue) {
			r.addRuleSafe(new Rule(KeySymbol.TRUE, new ArrayList<>(), 0));
		}
		if (isFalse) {
			r.addRuleSafe(new Rule(KeySymbol.FALSE, new ArrayList<>(), 0));
		}

		r.addFinalStateSafe(0);
		return r;
	}

	public static Automata isNull(Automata a) {
		boolean isTrue = false;
		boolean isFalse = false;
		a.eliminateNotAccessible();
		a.epsilonCloseFinalStates();
		for (int finalState : a.getFinalStates())
			for (Rule rule : a.getStateRules(finalState)) {
				if (rule.getSymbol() == KeySymbol.NULL)
					isTrue = true;
				else
					isFalse = true;
			}
		return createBooleanAutomata(isTrue, isFalse);
	}

	public static Automata isPair(Automata a) {
		boolean isTrue = false;
		boolean isFalse = false;
		a.eliminateNotAccessible();
		a.epsilonCloseFinalStates();
		for (int finalState : a.getFinalStates())
			for (Rule rule : a.getStateRules(finalState)) {
				if (rule.getSymbol() == KeySymbol.PAIR)
					isTrue = true;
				else
					isFalse = true;
			}

		return createBooleanAutomata(isTrue, isFalse);
	}

	public static Automata isBoolean(Automata a) {
		boolean isTrue = false;
		boolean isFalse = false;
		a.eliminateNotAccessible();
		a.epsilonCloseFinalStates();
		for (int finalState : a.getFinalStates())
			for (Rule rule : a.getStateRules(finalState)) {
				if (rule.getSymbol() == KeySymbol.TRUE || rule.getSymbol() == KeySymbol.FALSE)
					isTrue = true;
				else
					isFalse = true;
			}

		return createBooleanAutomata(isTrue, isFalse);
	}

	public static Automata isSymbol(Automata a) {
		boolean isTrue = false;
		boolean isFalse = false;
		a.eliminateNotAccessible();
		a.epsilonCloseFinalStates();
		for (int finalState : a.getFinalStates())
			for (Rule rule : a.getStateRules(finalState)) {
				if (rule.getSymbol() instanceof IdentSymbol)
					isTrue = true;
				else
					isFalse = true;
			}

		return createBooleanAutomata(isTrue, isFalse);
	}

	public static Automata consProc(Automata car, Automata cdr) {
		Automata pair = car.unionRules(cdr);
		int finalState = pair.newState();
		pair.addFinalState(finalState);
		car.epsilonCloseFinalStates();
		cdr.epsilonCloseFinalStates();
		for (int stateCar : car.getFinalStates())
			for (int stateCdr : cdr.getFinalStates()) {
				ArrayList<Integer> args = new ArrayList<>();
				args.add(stateCar);
				args.add(car.stateOffset(stateCdr));
				pair.addRule(new Rule(KeySymbol.PAIR, args, finalState));
			}
		return pair;
	}
	
	private static Automata pairElmProc(Automata a, int index){
		Automata b = a.cloneRules();
		for (int resState : a.getFinalStates()) {
			for (Rule rule : a.getStateRules(resState)) {
				Symbol symb = rule.getSymbol();
				if (!(symb instanceof KeySymbol) || ((KeySymbol)symb) != KeySymbol.PAIR)
					return null;
				b.addFinalState(rule.getArgs().get(index));
			}
		}
		return b;
	}
	
	public static Automata carProc(Automata a) throws NotSupportedProcedureException {
		Automata b = pairElmProc(a, 0);
		if (b == null)
			throw new NotSupportedProcedureException("car of not pair");
		return b;
	}
	
	public static Automata cdrProc(Automata a) throws NotSupportedProcedureException {
		Automata b = pairElmProc(a, 1);
		if (b == null)
			throw new NotSupportedProcedureException("cdr of not pair");
		return b;
	}

	public static Automata equalsProc(Automata a,Automata b) {
		Automata c = (Automata)a.clone();
		c.intersect(b);
		boolean isTrue = false;
		boolean isFalse = false;
		if (!c.isLanguageEmpty())
			isTrue = true;
		c.complement(a.getSymbolsSet());
		if (!c.isLanguageEmpty())
			isFalse = true;
		return createBooleanAutomata(isTrue, isFalse);
	}

	public static Automata isList(Automata a) {
		boolean isTrue = false;
		boolean isFalse = false;
		HashSet<Integer> cStates = new HashSet<>();
		HashSet<Integer> vStates = new HashSet<>();
		a.eliminateEpsilonRules();
		for (Integer finalState : a.getFinalStates())
			cStates.add(finalState);
		while (!cStates.isEmpty()) {
			for (Integer cState : cStates) {
				if (vStates.contains(cState))
					continue;
				for (Rule rule : a.getStateRules(cState)) {
					if (rule.getSymbol() == KeySymbol.NULL)
						isTrue = true;
					else if (rule.getSymbol() == KeySymbol.PAIR) {
						cStates.add(rule.getArgs().get(1));
					} else
						isFalse = true;
				}
				vStates.add(cState);
			}
		}
		return createBooleanAutomata(isTrue, isFalse);
	}

	public static Automata listProc(ArrayList<Automata> auts) {
		return listProcRec(auts, 0);
	}

	private static Automata listProcRec(ArrayList<Automata> auts, int ind) {
		if (ind == auts.size())
			return Automata.createNull();
		return consProc(auts.get(ind), listProcRec(auts, ind + 1));
	}
}
