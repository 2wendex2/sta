package ru.wendex.sta.typea;

import java.util.ArrayList;
import java.util.HashMap;
import ru.wendex.sta.scm.*;

public class ScmDataBuilder {
	private HashMap<String, ScmFunction> funcs = new HashMap<>();
	private Ast ast;
	private int stateCount = 0;
	
	public static ScmData build(Ast ast) throws ScmToAutException {
		ScmDataBuilder builder = new ScmDataBuilder(ast);
		builder.procAst();
		return new ScmData(builder.funcs, builder.stateCount);
	}
	
	private ScmDataBuilder(Ast ast) {
		this.ast = ast;
	}
	
	private void procAst() throws ScmToAutException {
		for (Node node : ast.getNodes()) {
			if (isNodeDefine(node)) {
				procDefineTail1(((PairNode)node).getCdr());
			}
		}
	}
	
	private static boolean isNodeDefine(Node node) {
		return (node instanceof PairNode) && (((PairNode)node).getCar() instanceof SymbolNode) && ((SymbolNode)(((PairNode)node).getCar())).getValue().equals("define");
	} 
	
	private void procDefineTail1(Node node) throws ScmToAutException {
		if (!(node instanceof PairNode))
			throw new ScmToAutException("Incorrect define argument 1");
		PairNode a = (PairNode)node;
		Node body = procDefineTail2(a.getCdr());
		procFunctionDefine(a.getCar(), body);
	}
	
	private Node procDefineTail2(Node node) throws ScmToAutException {
		if (!(node instanceof PairNode))
			throw new ScmToAutException("Incorrect define argument 2");
		PairNode a = (PairNode)node;
		if (!(a.getCdr() instanceof NullNode))
			throw new ScmToAutException("Incorrect count of define arguments");
		return a.getCar();
	}
	
	private void procFunctionDefine(Node args, Node body) throws ScmToAutException {
		if (!(args instanceof PairNode))
			throw new ScmToAutException("Incorrect function signature");
		PairNode node = (PairNode)args;
		Node a = node.getCar();
		if (!(a instanceof SymbolNode))
			throw new ScmToAutException("Incorrect function name");
		String name = ((SymbolNode)a).getValue();
		ArrayList<String> as = procFunctionArgs(node.getCdr());
		stateCount++;
		
		ScmFunction func = new ScmFunction(name, as, body, stateCount - 1);
		funcs.put(name, func);
	}
	
	private ArrayList<String> procFunctionArgs(Node node) throws ScmToAutException {
		ArrayList<String> r = new ArrayList<>();
		for (;;) {
			if (node instanceof NullNode)
				break;
			else if (!(node instanceof PairNode))
				throw new ScmToAutException("Incorrect arguments list");
			PairNode a = (PairNode)node;
			Node b = a.getCar();
			if (!(b instanceof SymbolNode))
				throw new ScmToAutException("Incorrect argument name");
			String argName = ((SymbolNode)b).getValue();
			r.add(argName);
			node = a.getCdr();
		}
		return r;
	}
}
