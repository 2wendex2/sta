package ru.wendex.sta.adl;

import java.util.ArrayList;
import java.util.HashMap;
import ru.wendex.sta.langbase.ParserException;
import java.io.IOException;

public class Parser {
	private Lexer lexer;
	private HashMap<String, Integer> arityMap = new HashMap<>();
	
	private Parser(Lexer lexer) throws IOException {
		this.lexer = lexer;
	}
	
	public static FunctionList parse(Lexer lexer) throws ParserException, IOException {
		Parser parser = new Parser(lexer);
		FunctionList lst = parser.parseFunctions();
		if (lexer.peek().getTag() != Token.EOF) {
			throw new ParserException("Expected end of file\n" + lexer.peek().toString());
		}
		return lst;
	}
	
	private FunctionList parseFunctions() throws ParserException, IOException {
		ArrayList<Function> lst = new ArrayList<>();
		for (;;) {
			Token token = lexer.peek();
			if (token.getTag() != Token.DEF) {
				break;
			}
			lexer.next();
			Function function = parseFunction();
			lst.add(function);
		}
		return new FunctionList(lst);
	}
	
	private Function parseFunction() throws ParserException, IOException {
		Token token = lexer.peek();
		if (token.getTag() != Token.SCHEME_IDENT) {
			throw new ParserException("Expected function name\n" + lexer.peek().toString());
		}
		
		String name = ((StringToken)token).getValue();
		
		lexer.next();
		token = lexer.peek();
		ArrayList<Automata> args = new ArrayList<>();
		if (token.getTag() == Token.FROM) {
			lexer.next();
			token = lexer.peek();
			if (token.getTag() != Token.LSQUARE) {
				throw new ParserException("Expected automata after from\n" + lexer.peek().toString());
			}
			do {
				lexer.next();
				Automata automata = parseAutomata();
				args.add(automata);
				token = lexer.peek();
			} while (lexer.peek().getTag() == Token.LSQUARE);
		}
		if (token.getTag() != Token.TO) {
			throw new ParserException("Expected function result\n" + lexer.peek().toString());
		}
		lexer.next();
		token = lexer.peek();
		if (token.getTag() != Token.LSQUARE) {
			throw new ParserException("Expected automata after to\n" + lexer.peek().toString());
		}
		lexer.next();
		Automata res = parseAutomata();
		Function f = new Function(name, args, res);
		return f;
	}
	
	private Automata parseAutomata() throws ParserException, IOException {
		arityMap.clear();
		ArrayList<Rule> rules = new ArrayList<>();
		ArrayList<EpsilonRule> epsilonRules = new ArrayList<>();
		ArrayList<Integer> finalStates = new ArrayList<>();
		autoCycle: for (;;) {
			Token token = lexer.peek();
			switch (token.getTag()) {
				case Token.SEMICOLON:
					lexer.next();
					break;
				case Token.FINAL:
					lexer.next();
					parseFinalStates(finalStates);
					break;
				case Token.EPSILON:
					lexer.next();
					epsilonRules.add(parseEpsilonRule());
					break;
				case Token.SCHEME_IDENT:
					rules.add(parseRule());
					break;
				case Token.SPEC_IDENT:
					rules.add(parseSpecRule());
					break;
				case Token.RSQUARE:
					lexer.next();
					break autoCycle;
				default:
					throw new ParserException("Expected rule\n" + lexer.peek().toString());
			}
		}
		Automata a = new Automata(lexer.getIdentCount(), rules, epsilonRules, finalStates);
		return a;
	}
	
	private void parseFinalStates(ArrayList<Integer> finalStates) throws ParserException, IOException {
		for (;;) {
			Token token = lexer.peek();
			if (token.getTag() == Token.SEMICOLON) {
				lexer.next();
				return;
			} else if (token.getTag() != Token.IDENT) {
				throw new ParserException("Expected final state ident\n" + lexer.peek().toString());
			}
			int i = ((IntToken)token).getValue();
			finalStates.add(i);
			lexer.next();
		}
	}
	
	private EpsilonRule parseEpsilonRule() throws ParserException, IOException {
		Token token = lexer.peek();
		if (token.getTag() != Token.IDENT) {
			throw new ParserException("Expected epsilon rule state ident\n" + lexer.peek().toString());
		}
		int arg = ((IntToken)token).getValue();
		lexer.next();
		
		token = lexer.peek();
		if (token.getTag() != Token.ARROW) {
			throw new ParserException("Expected arrow in epsilon rule\n" + lexer.peek().toString());
		}
		lexer.next();
		
		token = lexer.peek();
		if (token.getTag() != Token.IDENT) {
			throw new ParserException("Expected epsilon rule result state ident\n" + lexer.peek().toString());
		}
		int res = ((IntToken)token).getValue();
		lexer.next();
		
		token = lexer.peek();
		if (token.getTag() == Token.SEMICOLON) {
			lexer.next();
			return new EpsilonRule(arg, res);
		}
		throw new ParserException("Expected ; at end of epsilon rule\n" + lexer.peek().toString());
	}
	
	private Rule parseSpecRule() throws ParserException, IOException {
		Token token = lexer.peek();
		String s = ((StringToken)token).getValue();
		if (!s.equals("null"))
			throw new ParserException("Unknown spec symbol\n" + lexer.peek().toString());
		lexer.next();
		
		token = lexer.peek();
		if (token.getTag() != Token.LPAREN) {
			throw new ParserException("Expected lparen in spec rule\n" + lexer.peek().toString());
		}
		lexer.next();
		
		token = lexer.peek();
		if (token.getTag() != Token.RPAREN) {
			throw new ParserException("Expected rparen in spec rule\n" + lexer.peek().toString());
		}
		lexer.next();
		
		token = lexer.peek();
		if (token.getTag() != Token.ARROW) {
			throw new ParserException("Expected arrow in spec rule\n" + lexer.peek().toString());
		}
		lexer.next();
		
		token = lexer.peek();
		if (token.getTag() != Token.IDENT) {
			throw new ParserException("Expected state in spec rule\n" + lexer.peek().toString());
		}
		int res = ((IntToken)token).getValue();
		lexer.next();
		
		token = lexer.peek();
		if (token.getTag() == Token.SEMICOLON) {
			lexer.next();
			return new Rule(KeySymbol.NULL, new ArrayList<>(), res);
		}
		throw new ParserException("Expected ; at end of spec rule\n" + lexer.peek().toString());
	}
	
	private Rule parseRule() throws ParserException, IOException {
		Token token = lexer.peek();
		String s = ((StringToken)token).getValue();
		lexer.next();
		
		token = lexer.peek();
		if (token.getTag() != Token.LPAREN) {
			throw new ParserException("Expected lparen in rule\n" + lexer.peek().toString());
		}
		lexer.next();
		
		ArrayList<Integer> args = new ArrayList<>();
		for (;;) {
			token = lexer.peek();
			if (token.getTag() == Token.RPAREN) {
				lexer.next();
				break;
			}
			if (token.getTag() != Token.IDENT)
				throw new ParserException("Expected ident state in args rule\n" + lexer.peek().toString());
			int arg = ((IntToken)token).getValue();
			args.add(arg);
			lexer.next();
		}
		
		Integer i = arityMap.get(s);
		if (i == null) {
			i = args.size();
			arityMap.put(s, i);
		}
		if (i != args.size())
			throw new ParserException("Wrong arity\n" + s);
		
		AlgebraicSymbol symbol = new AlgebraicSymbol(s, i);
		
		token = lexer.peek();
		if (token.getTag() != Token.ARROW) {
			throw new ParserException("Expected arrow in rule\n" + lexer.peek().toString());
		}
		lexer.next();
		
		token = lexer.peek();
		if (token.getTag() != Token.IDENT) {
			throw new ParserException("Expected state in rule\n" + lexer.peek().toString());
		}
		int res = ((IntToken)token).getValue();
		lexer.next();
		
		token = lexer.peek();
		if (token.getTag() == Token.SEMICOLON) {
			lexer.next();
			return new Rule(symbol, args, res);
		}
		throw new ParserException("Expected ; at end of rule\n" + lexer.peek().toString());
	}
}
