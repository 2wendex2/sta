package ru.wendex.sta.typea;

import org.junit.jupiter.api.Test;
import ru.wendex.sta.adl.FunctionList;
import ru.wendex.sta.aut.NotSupportedProcedureException;
import ru.wendex.sta.langbase.ParserException;
import ru.wendex.sta.langbase.Position;
import ru.wendex.sta.scm.Ast;
import ru.wendex.sta.scm.Lexer;
import ru.wendex.sta.scm.Parser;

import java.io.FileReader;
import java.io.IOException;

public class MatcherTest {
    @Test
    public void matcherTest() throws IOException, ParserException, ScmToAutException,
            TypeMatcherException, NotSupportedProcedureException {
        FileReader fr1 = new FileReader("testfiles/scmmain.scm");
        Position pos1 = new Position(fr1);
        Lexer lexer1 = new Lexer(pos1);
        Ast ast = Parser.parse(lexer1);
        fr1.close();
        ScmData scmData = ScmDataBuilder.build(ast);

        FileReader fr2 = new FileReader("testfiles/adlmain.txt");
        Position pos2 = new Position(fr2);
        ru.wendex.sta.adl.Lexer lexer2 = new ru.wendex.sta.adl.Lexer(pos2);
        FunctionList fl = ru.wendex.sta.adl.Parser.parse(lexer2);
        fr2.close();
        TypeaData typeaData = TypeaDataBuilder.build(fl);


        System.out.println(AutomataTypeMatcher.match(typeaData, scmData));
    }
}
