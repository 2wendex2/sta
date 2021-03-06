package ru.wendex.sta.typea;

import ru.wendex.sta.aut.*;
import ru.wendex.sta.scm.Node;
import ru.wendex.sta.scm.NullNode;
import ru.wendex.sta.scm.PairNode;
import ru.wendex.sta.scm.SymbolNode;

import java.util.*;

public class AutomataTypeMatcher {
    private Stack<HashMap<String, Automata>> varStack = new Stack<>();
    private HashMap<String, ScmFunction> funcs;
    private HashMap<String, Automata> vars = new HashMap<>();
    private HashMap<String, Node> varsNodes;

    private int autovarCount = 0;
    private HashMap<String, ArrayList<AutovarSignature>> autovars = new HashMap<>();

    private AutomataTypeMatcher(HashMap<String, ScmFunction> fs, HashMap<String, Node> vs) {
        this.funcs = fs;
        this.varsNodes = vs;
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
            ArrayList<Integer> args = new ArrayList<>();
            int carState = automata.newState();
            int cdrState = automata.newState();
            args.add(carState);
            args.add(cdrState);
            automata.addRule(new Rule(KeySymbol.PAIR, args, curState));
            nodeAutomataRec(automata, pairNode.getCar(), carState);
            nodeAutomataRec(automata, pairNode.getCdr(), cdrState);
        } else {
            throw new TypeMatcherException("Not supported scheme object");
        }
    }

    private Automata globalVarAutomata(String name) throws TypeMatcherException, NotSupportedProcedureException {
        Automata a = vars.get(name);
        if (a == null) {
            Node node = varsNodes.get(name);
            if (node == null)
                throw new TypeMatcherException("variable " + name + " not defined");
            a = exprAutomata(node);
        }
        return a;
    }

    private Automata exprAutomata(Node node) throws TypeMatcherException, NotSupportedProcedureException {
        if (node instanceof SymbolNode) {
            SymbolNode symNode = (SymbolNode)node;
            HashMap<String, Automata> localVars = varStack.peek();
            Automata a = localVars.get(symNode.getValue());
            if (a == null) {
                a = globalVarAutomata(symNode.getValue());
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
            return applyFunctionByNameToList(fs, pairNode.getCdr());
        } else {
            throw new TypeMatcherException("unsupported scheme expression");
        }
    }

    private Automata applyFunctionByNameToList(String name, Node node)
            throws TypeMatcherException, NotSupportedProcedureException {
        ScmFunction scmFunction = funcs.get(name);
        if (scmFunction != null) {
            return applyUserFunctionToList(scmFunction, node);
        }
        if (!ScmData.STANDARD_FUNCTIONS.contains(name)) {
            throw new TypeMatcherException("function " + name + " not defined");
        }
        return applyStandardFunctionByNameToList(name, node);
    }

    private Automata applyUserFunctionToAutomata(ScmFunction function, ArrayList<Automata> argsAutomata)
            throws TypeMatcherException, NotSupportedProcedureException {
        ArrayList<AutovarSignature> autovarLst = autovars.get(function.getName());
        if (autovarLst != null) {
            for (AutovarSignature autovarSignature : autovarLst)
                if (autovarSignature.checkArgs(argsAutomata))
                    return Automata.createVar(autovarSignature.getAutovar());
        } else {
            autovarLst = new ArrayList<>();
            autovars.put(function.getName(), autovarLst);
        }
        int curAutovar = autovarCount;
        autovarCount++;
        autovarLst.add(new AutovarSignature(curAutovar, argsAutomata));

        ArrayList<String> argsString = function.getArgs();
        if (argsString.size() != argsAutomata.size())
            throw new TypeMatcherException("apply function wrong arguments count");
        HashMap<String, Automata> argMap = new HashMap<>();
        for (int i = 0; i < argsAutomata.size(); i++) {
            String argString = argsString.get(i);
            if (funcs.containsKey(argString) || vars.containsKey(argString) ||
                    ScmData.STANDARD_FUNCTIONS.contains(argString) || argMap.containsKey(argString)) {
                throw new TypeMatcherException("Duplicate argument name " + argString);
            }
            argMap.put(argsString.get(i), argsAutomata.get(i));
        }
        varStack.push(argMap);
        Automata r = exprAutomata(function.getBody());
        varStack.pop();
        autovarLst.remove(autovarLst.size() - 1);
        r.substituteFinal(curAutovar);
        return r;
    }

    private Automata applyUserFunctionToList(ScmFunction function, Node node)
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
        return applyUserFunctionToAutomata(function, argsAutomata);
    }

    private Automata applyStandardFunctionByNameToList(String name, Node node)
            throws TypeMatcherException, NotSupportedProcedureException {
        ArrayList<Node> args = new ArrayList<>();
        Node curNode = node;
        for (;;) {
            if (curNode instanceof NullNode)
                break;
            if (!(curNode instanceof PairNode))
                throw new TypeMatcherException("wrong function arguments list");
            PairNode pairNode = (PairNode)curNode;
            args.add(pairNode.getCar());
            curNode = pairNode.getCdr();
        }
        switch (name) {
            case "cons":
                if (args.size() != 2)
                    throw new TypeMatcherException("cons required 2 arguments");
                return Procedures.consProc(exprAutomata(args.get(0)), exprAutomata(args.get(1)));
            case "car":
                if (args.size() != 1)
                    throw new TypeMatcherException("car required 2 arguments");
                return Procedures.carProc(exprAutomata(args.get(0)));
            case "cdr":
                if (args.size() != 1)
                    throw new TypeMatcherException("cdr required 2 arguments");
                return Procedures.cdrProc(exprAutomata(args.get(0)));
            case "quote":
                if (args.size() != 1)
                    throw new TypeMatcherException("quote required 1 arguments");
                return nodeAutomata(args.get(0));
            case "if":
                if (args.size() != 3)
                    throw new TypeMatcherException("if required 3 arguments");
                return applyIf(args.get(0), args.get(1), args.get(2));
            default:
                throw new TypeMatcherException("Standard function " + name + " not supported");
        }
    }

    public Automata applyIf(Node cond, Node trueNode, Node falseNode)
            throws TypeMatcherException, NotSupportedProcedureException {
        if (!(cond instanceof PairNode))
            throw new TypeMatcherException("if condition must be pair expression");
        Node condCar = ((PairNode)cond).getCar();
        if (!(condCar instanceof SymbolNode))
            throw new TypeMatcherException("if condition expression car must be symbol");
        ArrayList<Node> args = new ArrayList<>();
        Node curNode = ((PairNode)cond).getCdr();
        while (!(curNode instanceof NullNode)) {
            if (!(curNode instanceof PairNode))
                throw new TypeMatcherException("wrong if condition arguments list");
            PairNode pairNode = (PairNode) curNode;
            args.add(pairNode.getCar());
            curNode = pairNode.getCdr();
        }
        String name = ((SymbolNode)condCar).getValue();
        switch (name) {
            case "null?": {
                if (args.size() != 1)
                    throw new TypeMatcherException("null? required 1 argument");
                Node nullArg = args.get(0);
                if (!(nullArg instanceof SymbolNode))
                    throw new TypeMatcherException("null? argument must be symbol");
                String nullArgName = ((SymbolNode)nullArg).getValue();
                HashMap<String, Automata> vars = varStack.peek();
                Automata nullArgAutomata = vars.get(nullArgName);
                if (nullArgAutomata == null)
                    throw new TypeMatcherException("null? argument must function parameter");
                Automata notNullAutomata = (Automata)nullArgAutomata.clone();
                notNullAutomata.remove0Symbol(KeySymbol.NULL);
                if (nullArgAutomata.isPresent(KeySymbol.NULL)) {
                    if (notNullAutomata.isLanguageEmpty()) {
                        return exprAutomata(trueNode);
                    } else {
                        vars.put(nullArgName, Automata.createNull());
                        Automata trueAutomata = exprAutomata(trueNode);
                        vars.put(nullArgName, notNullAutomata);
                        Automata falseAutomata = exprAutomata(falseNode);
                        vars.put(nullArgName, nullArgAutomata);
                        return trueAutomata.unionN(falseAutomata);
                    }
                } else {
                    return exprAutomata(falseNode);
                }
            }
            case "equals?": {
                if (args.size() != 2)
                    throw new TypeMatcherException("equals? required 2 arguments");
                Node arg1 = args.get(0);
                Node arg2 = args.get(1);
                HashMap<String, Automata> vars = varStack.peek();
                if (!(arg1 instanceof SymbolNode) || !vars.containsKey(((SymbolNode)arg1).getValue())) {
                    if (!(arg2 instanceof SymbolNode) || !vars.containsKey(((SymbolNode)arg2).getValue())) {
                        throw new TypeMatcherException("one of equals? argument must be function parameter");
                    }
                    Node argt = arg1;
                    arg1 = arg2;
                    arg2 = argt;
                }

                String argName = ((SymbolNode)arg1).getValue();
                Automata argAutomata = vars.get(argName);
                vars.remove(argName);
                Automata ptAutomata = exprAutomata(arg2);
                vars.put(argName, argAutomata);

                Automata substractAutomata = (Automata)ptAutomata.clone();
                substractAutomata.substractFrom(argAutomata);
                Automata intersectAutomata = (Automata)argAutomata.clone();
                intersectAutomata.intersect(ptAutomata);
                if (intersectAutomata.isLanguageEmpty()) {
                    return exprAutomata(falseNode);
                } else {
                    if (substractAutomata.isLanguageEmpty()) {
                        return exprAutomata(trueNode);
                    } else {
                        vars.put(argName, intersectAutomata);
                        Automata trueAutomata = exprAutomata(trueNode);
                        vars.put(argName, substractAutomata);
                        Automata falseAutomata = exprAutomata(falseNode);
                        vars.put(argName, argAutomata);
                        return trueAutomata.unionN(falseAutomata);
                    }
                }
            }
            default:
                throw new TypeMatcherException("Conditional function " + name + " not supported");
        }
    }

    public static TypeMatcherReport match(TypeaData typeaData, ScmData scmData) {
        AutomataTypeMatcher matcher = new AutomataTypeMatcher(scmData.getFuncs(), scmData.getVars());
        ArrayList<FunctionReport> functionReports = new ArrayList<>();
        boolean isGlobalMatch = true;
        for (TypeaFunction tf : typeaData.getFuncs()) {
            try {
                String name = tf.getName();
                ScmFunction sf = matcher.funcs.get(name);
                if (sf == null) {
                    if (ScmData.STANDARD_FUNCTIONS.contains(name))
                        throw new TypeMatcherException("function " + name + " is standard function");
                    else
                        throw new TypeMatcherException("function " + name + " not defined");
                }

                Automata a = matcher.applyUserFunctionToAutomata(sf, tf.getArgs());
                Automata b = tf.getRes();

                boolean isMatch = a.isSubAutomata(b);
                functionReports.add(new FunctionReport(tf, a, isMatch, null));
                isGlobalMatch = isGlobalMatch && isMatch;
            } catch (TypeMatcherException | NotSupportedProcedureException exception) {
                functionReports.add(new FunctionReport(tf, null, false, exception));
                isGlobalMatch = false;
            }
        }
        return new TypeMatcherReport(isGlobalMatch, functionReports);
    }
}
