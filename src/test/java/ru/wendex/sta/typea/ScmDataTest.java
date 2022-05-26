package ru.wendex.sta.typea;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import ru.wendex.sta.scm.*;
import ru.wendex.sta.langbase.Position;
import ru.wendex.sta.langbase.ParserException;
import java.util.ArrayList;
import java.io.*;

public class ScmDataTest {
	@Test
	public void scmDataTest() throws IOException, ParserException, ScmToAutException {
		FileReader fr = new FileReader("testfiles/scmmain.scm");
		Position pos = new Position(fr);
		Lexer lexer = new Lexer(pos);
		Ast ast = Parser.parse(lexer);
		fr.close();
		
		ScmData scmData = ScmDataBuilder.build(ast);
		scmData.print();
	}
}
