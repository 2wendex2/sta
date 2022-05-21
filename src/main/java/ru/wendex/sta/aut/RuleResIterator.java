package ru.wendex.sta.aut;

import java.util.Iterator;

public class RuleResIterator implements Iterator<Rule> {
	private int state;
	private Iterator<Rule> ruleIterator;
	private Rule nextRule = null;
	
	public RuleResIterator(Iterable<Rule> ruleIterable, int state) {
		this.state = state;
		ruleIterator = ruleIterable.iterator();
		next();
	}
	
	public boolean hasNext() {
		return nextRule != null;
	}
	
	public Rule next() {
		while (ruleIterator.hasNext()) {
			Rule rule = ruleIterator.next();
			if (rule.getRes() == state) {
				Rule curRule = nextRule;
				nextRule = rule;
				return curRule;
			}
		}
		Rule curRule = nextRule;
		nextRule = null;
		return curRule;
	}
}
