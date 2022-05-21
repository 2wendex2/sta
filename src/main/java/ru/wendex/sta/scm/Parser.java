package ru.wendex.sta.scm;

import java.util.ArrayList;
import java.util.HashMap;
import ru.wendex.sta.langbase.ParserException;
import ru.wendex.sta.scm.Token;
import java.io.IOException;

public class Parser {
	private Lexer lexer;
	
	private Parser(Lexer lexer) throws IOException {
		this.lexer = lexer;
	}
	
	public static Ast parse(Lexer lexer) throws ParserException, IOException {
		Parser parser = new Parser(lexer);
		Ast ast = parser.parseAst();
		if (lexer.peek().getTag() != Token.EOF) {
			throw new ParserException("Expected end of file\n" + lexer.peek().toString());
		}
		//ast.unquote();
		return ast;
	}
	
	private Ast parseAst() throws ParserException, IOException {
		ArrayList<Node> nodes = new ArrayList<>();
		for (;;) {
			Token token = lexer.peek();
			if (token.getTag() == Token.EOF) {
				break;
			}
			Node node = parseNode();
			nodes.add(node);
		}
		return new Ast(nodes);
	}
	
	private Node parsePairTail() throws ParserException, IOException {
		Token token = lexer.peek();
		if (token.getTag() == Token.IMPROPER_PERIOD) {
			lexer.next();
			Node r = parseNode();
			token = lexer.peek();
			if (token.getTag() == Token.RPAREN) {
				lexer.next();
				return r;
			}
			throw new ParserException(") expected at end of improper list\n" + token.toString());
		} else if (token.getTag() == Token.RPAREN) {
			lexer.next();
			return new NullNode();
		}
		
		Node car = parseNode();
		Node cdr = parsePairTail();
		return new PairNode(car, cdr);
	}
	
	private Node parseNode() throws ParserException, IOException {
		Token token = lexer.peek();
		switch (token.getTag()) {
			case Token.LPAREN: {
				lexer.next();
				token = lexer.peek();
				if (token.getTag() == Token.RPAREN) {
					lexer.next();
					return new NullNode();
				}
				Node car = parseNode();
				Node cdr = parsePairTail();
				return new PairNode(car, cdr);
			}
			case Token.VECTOR_PAREN:
				lexer.next();
				ArrayList<Node> nodes = new ArrayList<>();
				for (;;) {
					token = lexer.peek();
					if (token.getTag() == Token.RPAREN) {
						lexer.next();
						break;
					}
					Node node = parseNode();
					nodes.add(node);
				}
				return new VectorNode(nodes);
			case Token.QUOTE: {
				Node car = new SymbolNode(Lexer.toIdentToken("quote", token));
				lexer.next();
				Node cdr = parseNode();
				return new PairNode(car, new PairNode(cdr, new NullNode()));
			}
			case Token.QUASIQUOTE: {
				Node car = new SymbolNode(Lexer.toIdentToken("quasiquote", token));
				lexer.next();
				Node cdr = parseNode();
				return new PairNode(car, new PairNode(cdr, new NullNode()));
			}
			case Token.UNQUOTE: {
				Node car = new SymbolNode(Lexer.toIdentToken("unquote", token));
				lexer.next();
				Node cdr = parseNode();
				return new PairNode(car, new PairNode(cdr, new NullNode()));
			}
			case Token.UNQUOTE_SPLICING: {
				Node car = new SymbolNode(Lexer.toIdentToken("unquote-splicing", token));
				lexer.next();
				Node cdr = parseNode();
				return new PairNode(car, new PairNode(cdr, new NullNode()));
			}
			case Token.IDENT: {
				lexer.next();
				return new SymbolNode(token);
			}
			default:
				if (token.isObjectToken()) {
					lexer.next();
					return new ObjectNode(token);
				}
				else
					throw new ParserException("Not a expression\n" + token.toString());
		}
	}
}
