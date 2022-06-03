package ru.wendex.sta;

import org.junit.jupiter.api.Test;
import ru.wendex.sta.adl.FunctionList;
import ru.wendex.sta.aut.NotSupportedProcedureException;
import ru.wendex.sta.langbase.ParserException;
import ru.wendex.sta.langbase.Position;
import ru.wendex.sta.scm.Ast;
import ru.wendex.sta.scm.Lexer;
import ru.wendex.sta.scm.Parser;
import ru.wendex.sta.typea.*;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class MainTest {
    @Test
    public void mainTest() throws IOException, ParserException, ScmToAutException, NotSupportedProcedureException {
        FileReader fr1 = new FileReader("e.scm");
        Position pos1 = new Position(fr1);
        Lexer lexer1 = new Lexer(pos1);
        Ast ast = Parser.parse(lexer1);
        fr1.close();
        ScmData scmData = ScmDataBuilder.build(ast);

        FileReader fr2 = new FileReader("e.adl");
        Position pos2 = new Position(fr2);
        ru.wendex.sta.adl.Lexer lexer2 = new ru.wendex.sta.adl.Lexer(pos2);
        FunctionList fl = ru.wendex.sta.adl.Parser.parse(lexer2);
        fr2.close();
        TypeaData typeaData = TypeaDataBuilder.build(fl);

        TypeMatcherReport report = AutomataTypeMatcher.match(typeaData, scmData);
        report.print();
    }
}
