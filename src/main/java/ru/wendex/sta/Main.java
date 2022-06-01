package ru.wendex.sta;

import ru.wendex.sta.adl.FunctionList;
import ru.wendex.sta.langbase.Position;
import ru.wendex.sta.scm.Ast;
import ru.wendex.sta.scm.Lexer;
import ru.wendex.sta.scm.Parser;
import ru.wendex.sta.typea.*;

import java.io.FileReader;

public class Main {
    public static void main(String[] args) throws Exception {
        if (args.length != 2) {
            System.out.println("usage: <type description file> <scheme source>");
            return;
        }
        FileReader fr1 = new FileReader(args[1]);
        Position pos1 = new Position(fr1);
        Lexer lexer1 = new Lexer(pos1);
        Ast ast = Parser.parse(lexer1);
        fr1.close();
        ScmData scmData = ScmDataBuilder.build(ast);

        FileReader fr2 = new FileReader(args[0]);
        Position pos2 = new Position(fr2);
        ru.wendex.sta.adl.Lexer lexer2 = new ru.wendex.sta.adl.Lexer(pos2);
        FunctionList fl = ru.wendex.sta.adl.Parser.parse(lexer2);
        fr2.close();
        TypeaData typeaData = TypeaDataBuilder.build(fl);

        TypeMatcherReport report = AutomataTypeMatcher.match(typeaData, scmData);
        if (report.getSuccess())
            System.out.println("YES");
        else
            System.out.println("NO");
    }
}
