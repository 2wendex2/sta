package ru.wendex.sta.typea;

import ru.wendex.sta.aut.*;
import ru.wendex.sta.scm.Node;
import ru.wendex.sta.scm.NullNode;
import ru.wendex.sta.scm.PairNode;
import ru.wendex.sta.scm.SymbolNode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Stack;

public class AutomataTypeMatcher {
    private Stack<HashMap<String, Automata>> varStack = new Stack<>();
    private HashMap<String, ScmFunction> funcs;

    private AutomataTypeMatcher(HashMap<String, ScmFunction> fs) {
        this.funcs = fs;
    }

    private Automata nodeAutomata(Node node) throws TypeMatcherException {
        Automata a = Automata.createEmpty();
        int f = a.newState();
        nodeAutomataRec(a, node, f);
        a.addFinalState(f);
        return a;
    }

    private void nodeAutomataRec(Automata automata, Node node, int curState) throws TypeMatcherException {
        if (node instanceof NullNode) {
            automata.addRule(new Rule(KeySymbol.NULL, new ArrayList<>(), curState));
        } else if (node instanceof SymbolNode) {
            automata.addRule(new Rule(new IdentSymbol(((SymbolNode)node).getValue()), new ArrayList<>(), curState));
        } else if (node instanceof PairNode) {
            PairNode pairNode = (PairNode)node;
            int f = automata.newState();
            ArrayList<Integer> args = new ArrayList<>();
            int carState = automata.newState();
            int cdrState = automata.newState();
            args.add(carState);
            args.add(cdrState);
            automata.addRule(new Rule(KeySymbol.PAIR, args, f));
            nodeAutomataRec(automata, pairNode.getCar(), carState);
            nodeAutomataRec(automata, pairNode.getCdr(), cdrState);
        } else {
            throw new TypeMatcherException("Not supported scheme object");
        }
    }

    private Automata exprAutomata(Node node) throws TypeMatcherException, NotSupportedProcedureException {
        if (node instanceof SymbolNode) {
            SymbolNode symNode = (SymbolNode)node;
            HashMap<String, Automata> vars = varStack.peek();
            Automata a = vars.get(symNode.getValue());
            if (a == null) {
                throw new TypeMatcherException("variable " + symNode.getValue() + " not defined");
            }
            return a;
        } else if (node instanceof PairNode) {
            PairNode pairNode = (PairNode)node;
            Node car = pairNode.getCar();
            if (!(car instanceof SymbolNode)) {
                throw new TypeMatcherException("expression car must be symbol");
            }
            SymbolNode exprSymbol = (SymbolNode)car;
            String fs = exprSymbol.getValue();
            return route(fs, pairNode.getCdr());
        } else {
            throw new TypeMatcherException("unsupported scheme expression");
        }
    }

    private Automata route(String name, Node node) throws TypeMatcherException, NotSupportedProcedureException {
        if (name.equals("cons")) {
            if (!(node instanceof PairNode)) {
                throw new TypeMatcherException("wrong cons argument 1");
            }
            Node car = ((PairNode)node).getCar();
            Node node1 = ((PairNode)node).getCdr();
            if (!(node1 instanceof PairNode)) {
                throw new TypeMatcherException("wrong cons argument 2");
            }
            Node cdr = ((PairNode)node1).getCar();
            Node node2 = ((PairNode)node1).getCdr();
            if (!(node2 instanceof NullNode)) {
                throw new TypeMatcherException("too many cons arguments");
            }
            Automata carAutomata = exprAutomata(car);
            Automata cdrAutomata = exprAutomata(cdr);
            return Procedures.consProc(carAutomata, cdrAutomata);
        } else if (name.equals("car")) {
            if (!(node instanceof PairNode)) {
                throw new TypeMatcherException("wrong car argument ");
            }
            Node arg = ((PairNode)node).getCar();
            Node node1 = ((PairNode)node).getCdr();
            if (!(node1 instanceof NullNode)) {
                throw new TypeMatcherException("too many car arguments");
            }
            Automata argAutomata = exprAutomata(arg);
            return Procedures.carProc(argAutomata);
        } else if (name.equals("cdr")) {
            if (!(node instanceof PairNode)) {
                throw new TypeMatcherException("wrong cdr argument ");
            }
            Node arg = ((PairNode)node).getCar();
            Node node1 = ((PairNode)node).getCdr();
            if (!(node1 instanceof NullNode)) {
                throw new TypeMatcherException("too many cdr arguments");
            }
            Automata argAutomata = exprAutomata(arg);
            return Procedures.cdrProc(argAutomata);
        } else if (name.equals("quote")) {
            if (!(node instanceof PairNode)) {
                throw new TypeMatcherException("wrong quote argument");
            }
            Node arg = ((PairNode)node).getCar();
            Node node1 = ((PairNode)node).getCdr();
            if (!(node1 instanceof NullNode)) {
                throw new TypeMatcherException("too many quote arguments");
            }
            return nodeAutomata(arg);
        } else {
            ScmFunction scmFunction = funcs.get(name);
            if (scmFunction == null) {
                throw new TypeMatcherException("unknown function " + name);
            }
            return applyFunctionToNode(scmFunction, node);
        }
    }

    private Automata applyFunction(ScmFunction function, ArrayList<Automata> argsAutomata)
            throws TypeMatcherException, NotSupportedProcedureException {
        ArrayList<String> argsString = function.getArgs();
        if (argsString.size() != argsAutomata.size())
            throw new TypeMatcherException("apply function wrong arguments count");
        HashMap<String, Automata> argMap = new HashMap<>();
        for (int i = 0; i < argsAutomata.size(); i++) {
            argMap.put(argsString.get(i), argsAutomata.get(i));
        }
        varStack.push(argMap);
        Automata r = exprAutomata(function.getBody());
        varStack.pop();
        return r;
    }

    private Automata applyFunctionToNode(ScmFunction function, Node node)
            throws TypeMatcherException, NotSupportedProcedureException {
        ArrayList<Automata> argsAutomata = new ArrayList<>();
        Node curNode = node;
        for (;;) {
            if (curNode instanceof NullNode)
                break;
            if (!(curNode instanceof PairNode))
                throw new TypeMatcherException("wrong function arguments list");
            PairNode pairNode = (PairNode)curNode;
            argsAutomata.add(exprAutomata(pairNode.getCar()));
            curNode = pairNode.getCdr();
        }
        return applyFunction(function, argsAutomata);
    }

    public static TypeMatcherReport match(TypeaData typeaData, ScmData scmData)
            throws TypeMatcherException, NotSupportedProcedureException {
        AutomataTypeMatcher matcher = new AutomataTypeMatcher(scmData.getFuncs());
        for (TypeaFunction tf : typeaData.getFuncs()) {
            String name = tf.getName();
            ScmFunction sf = matcher.funcs.get(name);
            if (sf == null) {
                throw new TypeMatcherException("function " + name + " not defined");
            }
            Automata a = matcher.applyFunction(sf, tf.getArgs());
            a.print();
        }
        return new TypeMatcherReport(true);
    }
}
