package ru.wendex.sta.scm;

import ru.wendex.sta.langbase.ParserException;

public class PairNode extends Node {
	private Node car;
	private Node cdr;
	private boolean isExprFlag = false;
	
	public void printRec(String s, int k) {
		String h = "";
		if (isExprFlag)
			h = "EXPR ";
		System.out.println(Node.endPrefix(s, k) + h + "PAIR");
		car.printRec(Node.startPrefix(s, k), 1);
		cdr.printRec(Node.startPrefix(s, k), 2);
	}
	
	public Node getCar() {
		return car;
	}
	
	public Node getCdr() {
		return cdr;
	}
	
	public PairNode(Node car, Node cdr) {
		this.car = car;
		this.cdr = cdr;
	}
	
	void setExpr() {
		isExprFlag = true;
	}
	
	static final int EXPR_STATE = 0;
	static final int QUOTE_STATE = 1;
	static final int QUASIQUOTE_STATE = 2;

	private Node quoteExprTail() throws ParserException {
		if (!(cdr instanceof NullNode))
			throw new ParserException("Quote accept 1 parameter");
		return car;
	}

	public void unquoteTail() throws ParserException {
		isExprFlag = true;
		car = car.unquote();
		if (cdr instanceof PairNode)
			((PairNode)cdr).unquoteTail();
		else
			cdr = cdr.unquote();
	}

	public Node unquote() throws ParserException {
		if (car instanceof SymbolNode) {
			String op = ((SymbolNode)car).getValue();
			if (op.equals("quote")) {
				if (!(cdr instanceof PairNode))
					throw new ParserException("Quote expression must be not null list");
				return ((PairNode)cdr).quoteExprTail();
			} else if (op.equals("quasiquote")) {
				if (!(cdr instanceof PairNode))
					throw new ParserException("Quasiquote expression must be not null list");
				return ((PairNode)cdr).quasiquoteExprTail();
			} else if (op.equals("unquote") || op.equals("unquote-splicing")) {
				throw new ParserException("Unquote must be in quasiquote");
			}
		}
		unquoteTail();
		return this;
	}
	
	private Node quasiquoteExprTail() throws ParserException {
		if (!(cdr instanceof NullNode))
			throw new ParserException("Quasiquote accept 1 parameter");
		if (car instanceof VectorNode)
			((VectorNode)car).quasiquote();
		else if (car instanceof PairNode)
			return ((PairNode)car).quasiquote();
		return car;
	}
	
	private Node quasiquote() throws ParserException {
		if (car instanceof SymbolNode) {
			String op = ((SymbolNode)car).getValue();
			if (op.equals("unquote")) {
				if (!(cdr instanceof PairNode))
					throw new ParserException("Unquote expression must be not null list");
				return ((PairNode)cdr).unquoteExprTail();
			}
		} else if (car instanceof PairNode) {
			car = ((PairNode)car).quasiquote();
		} else if (car instanceof VectorNode){
			((VectorNode)car).quasiquote();
		}
		if (cdr instanceof PairNode) {
			((PairNode)cdr).quasiquoteTail();
		} else if (cdr instanceof VectorNode){
			((VectorNode)cdr).quasiquote();
		}
		return this;
	}
	
	private void quasiquoteTail() throws ParserException {
		if (car instanceof PairNode) {
			car = ((PairNode)car).quasiquote();
		} else if (car instanceof VectorNode){
			((VectorNode)car).quasiquote();
		}
		if (cdr instanceof PairNode) {
			((PairNode)cdr).quasiquoteTail();
		} else if (cdr instanceof VectorNode){
			((VectorNode)cdr).quasiquote();
		}
	}
	
	private Node unquoteExprTail() throws ParserException {
		if (!(cdr instanceof NullNode))
			throw new ParserException("Unquote accept 1 parameter");
		return car.unquote();
	}
}
