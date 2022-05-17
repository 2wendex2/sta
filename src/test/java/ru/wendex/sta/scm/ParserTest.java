package ru.wendex.sta.scm;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import ru.wendex.sta.scm.*;
import ru.wendex.sta.langbase.Position;
import ru.wendex.sta.langbase.ParserException;
import java.util.ArrayList;
import java.io.*;

public class ParserTest {
	@Test
	public void pstTest() throws IOException, ParserException {
		FileReader fr = new FileReader("testfiles/pst.txt");
		Position pos = new Position(fr);
		Lexer lexer = new Lexer(pos);
		Pst pst = Parser.parse(lexer);
		pst.print();
		fr.close();
	}
}
