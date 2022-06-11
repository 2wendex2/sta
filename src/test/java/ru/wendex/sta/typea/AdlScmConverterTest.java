package ru.wendex.sta.adl;

import org.junit.jupiter.api.Test;
import ru.wendex.sta.langbase.Position;
import ru.wendex.sta.langbase.ParserException;

import java.io.*;

public class AdlScmConverterTest {
	@Test
	public void adlScmConverterTest() throws IOException, ParserException, SemanticException {
		FileReader fr = new FileReader("testfiles/adlmain.txt");
		Position pos = new Position(fr);
		Lexer lexer = new Lexer(pos);
		FunctionList fl = Parser.parse(lexer);
		fr.close();

		for (Function f : fl.getList()) {
			f.getRes().print();
		}
	}
}
