package ru.wendex.sta.scm;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import ru.wendex.sta.scm.*;
import ru.wendex.sta.langbase.Position;
import ru.wendex.sta.langbase.LexicError;
import java.util.ArrayList;
import java.io.*;

public class LexerTest {
	private void printLexer(Lexer lexer) throws IOException {
		Token token = lexer.peek();
		while (token.getTag() != Token.EOF) {
			System.out.println(token);
			lexer.next();
			token = lexer.peek();
		}
		
		System.out.println("");
		
		ArrayList<LexicError> errs = lexer.getErrors();
		for (LexicError le : errs) {
			System.out.println(le);
		}
		System.out.println("");
		System.out.println("");
		System.out.println("");
	}
	
	@Test
	public void emptyLex() throws IOException {
		FileReader fr = new FileReader("testfiles/empty.txt");
		Position pos = new Position(fr);
		Lexer lexer = new Lexer(pos);
		printLexer(lexer);
		lexer.next();
		assertEquals(lexer.peek().getTag(), Token.EOF);
		lexer.next();
		assertEquals(lexer.peek().getTag(), Token.EOF);
		fr.close();
	}
	
	@Test
	public void nnumberLex() throws IOException {
		FileReader fr = new FileReader("testfiles/nnumlex.txt");
		Position pos = new Position(fr);
		Lexer lexer = new Lexer(pos);
		printLexer(lexer);
		fr.close();
	}
}
