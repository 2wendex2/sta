package ru.wendex.sta.aut;

import java.util.ArrayList;

public class Procedures {
	public static SchemeAutomata consProc(SchemeAutomata car, SchemeAutomata cdr) {
		SchemeAutomata pair = car.unionRules(cdr);
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
	
	private static SchemeAutomata pairElmProc(SchemeAutomata a, int index){
		SchemeAutomata b = a.cloneRules();
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
	
	public static SchemeAutomata carProc(SchemeAutomata a) throws NotSupportedProcedureException {
		SchemeAutomata b = pairElmProc(a, 0);
		if (b == null)
			throw new NotSupportedProcedureException("car of not pair");
		return b;
	}
	
	public static SchemeAutomata cdrProc(SchemeAutomata a) throws NotSupportedProcedureException {
		SchemeAutomata b = pairElmProc(a, 1);
		if (b == null)
			throw new NotSupportedProcedureException("cdr of not pair");
		return b;
	}
}
