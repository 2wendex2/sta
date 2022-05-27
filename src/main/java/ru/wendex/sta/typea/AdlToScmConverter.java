package ru.wendex.sta.typea;

import ru.wendex.sta.adl.*;
import ru.wendex.sta.aut.*;

import java.util.ArrayList;
import java.util.HashMap;

public class AdlToScmConverter {
	private HashMap<Integer, Integer> stateMap = new HashMap<>();
	private HashMap<String, Integer> symbolStateMap = new HashMap<>();
	private AdlAutomata src;
	private Automata dest = Automata.createEmpty();
	private int nullAlgebraicState = -1;

	private AdlToScmConverter(AdlAutomata src) {
		this.src = src;
	}

	private void convertSrc() {
		stateMapping();
		convertEpsilonRules();
		convertFinalStates();
		convertRules();
	}

	private int convertNullState() {
		if (nullAlgebraicState >= 0)
			return nullAlgebraicState;
		nullAlgebraicState = dest.newState();
		ArrayList<Integer> args = new ArrayList<>();
		Rule rule = new Rule(KeySymbol.NULL, args, nullAlgebraicState);
		dest.addRule(rule);
		return nullAlgebraicState;
	}

	private void convertFinalStates() {
		for (int i : src.getFinalStates())
			dest.addFinalState(stateMap.get(i));
	}

	private void convertRules() {
		for (AdlRule rule : src.getRules())
			convertRule(rule);
	}

	private void stateMapping() {
		for (AdlRule rule : src.getRules()) {
			int res = rule.getRes();
			if (stateMap.get(res) == null){
				int ns = dest.newState();
				stateMap.put(res, ns);
			}
			
			for (int arg : rule.getArgs()) {
				if (stateMap.get(arg) == null){
					int ns = dest.newState();
					stateMap.put(arg, ns);
				}
			}
		}
		
		for (AdlEpsilonRule rule : src.getEpsilonRules()) {
			int res = rule.getRes();
			if (stateMap.get(res) == null){
				int ns = dest.newState();
				stateMap.put(res, ns);
			}
			
			int arg = rule.getArg();
			if (stateMap.get(arg) == null){
				int ns = dest.newState();
				stateMap.put(arg, ns);
			}
		}
		
		for (int i : src.getFinalStates()) {
			if (stateMap.get(i) == null){
				int ns = dest.newState();
				stateMap.put(i, ns);
			}
		}
	}
	
	private void convertEpsilonRules() {
		for (AdlEpsilonRule srcRule : src.getEpsilonRules()) {
			EpsilonRule destRule = new EpsilonRule(stateMap.get(srcRule.getArg()), stateMap.get(srcRule.getRes()));
			dest.addEpsilonRule(destRule);
		}
	}

	private int convertAlgebraicSymbol(AlgebraicSymbol sym) {
		String s = sym.getValue();
		Integer i = symbolStateMap.get(s);
		if (i != null) {
			return i;
		}
		i = dest.newState();
		IdentSymbol destSymbol = new IdentSymbol(s);
		ArrayList<Integer> destArgs = new ArrayList<>();
		Rule rule = new Rule(destSymbol, destArgs, i);
		dest.addRule(rule);
		return i;
	}

	private void convertAlgebraicRulePair(int carState, int cdrState, int resState) {
		ArrayList<Integer> pairArgs = new ArrayList<>();
		pairArgs.add(carState);
		pairArgs.add(cdrState);
		Rule rule = new Rule(KeySymbol.PAIR, pairArgs, resState);
		dest.addRule(rule);
	}

	private void convertRule(AdlRule srcRule) {
		AdlSymbol srcSymbol = srcRule.getSymbol();
		if (srcSymbol instanceof AdlKeySymbol) {
			Rule rule = new Rule(KeySymbol.NULL, new ArrayList<>(), stateMap.get(srcRule.getRes()));
			dest.addRule(rule);
			return;
		}
		AlgebraicSymbol sym = (AlgebraicSymbol)srcSymbol;
		int arity = sym.getArity();
		ArrayList<Integer> args = srcRule.getArgs();

		int resState = stateMap.get(srcRule.getRes());
		int carState = convertAlgebraicSymbol(sym);
		int cdrState;
		if (arity == 0)
			cdrState = convertNullState();
		else
			cdrState = dest.newState();
		convertAlgebraicRulePair(carState, cdrState, resState);
		resState = cdrState;

		for (int i = 0; i < arity; i++) {
			carState = stateMap.get(args.get(i));
			if (i == arity - 1)
				cdrState = convertNullState();
			else
				cdrState = dest.newState();
			convertAlgebraicRulePair(carState, cdrState, resState);
			resState = cdrState;
		}
	}
	
	public static Automata convert(AdlAutomata src) {
		AdlToScmConverter adlToScmConverter = new AdlToScmConverter(src);
		adlToScmConverter.convertSrc();
		return adlToScmConverter.dest;
	}
}
