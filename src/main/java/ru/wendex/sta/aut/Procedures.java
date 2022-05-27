package ru.wendex.sta.aut;

import java.util.ArrayList;

public class Procedures {
	public static Automata consProc(Automata car, Automata cdr) {
		Automata pair = car.unionRules(cdr);
		int finalState = pair.newState();
		pair.addFinalState(finalState);
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
}
