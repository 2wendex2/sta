package ru.wendex.sta.adl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import ru.wendex.sta.langbase.Position;
import ru.wendex.sta.langbase.ParserException;
import java.util.ArrayList;
import java.io.*;

public class AdlParserTest {
	@Test
	public void pstTest() throws IOException, ParserException {
		FileReader fr = new FileReader("testfiles/au.txt");
		Position pos = new Position(fr);
		Lexer lexer = new Lexer(pos);
		FunctionList fl = Parser.parse(lexer);
		fl.print();
		fr.close();
	}
}
