package ru.wendex.sta;

import ru.wendex.sta.adl.FunctionList;
import ru.wendex.sta.adl.SemanticException;
import ru.wendex.sta.langbase.LexicError;
import ru.wendex.sta.langbase.ParserException;
import ru.wendex.sta.langbase.Position;
import ru.wendex.sta.scm.Ast;
import ru.wendex.sta.scm.Lexer;
import ru.wendex.sta.scm.Parser;
import ru.wendex.sta.typea.*;

import java.io.FileReader;
import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        if (args.length != 2) {
            System.out.println("usage: <type description file> <scheme source>");
            return;
        }
        ScmData scmData;
        TypeaData typeaData;

        try {
            FileReader fr1 = new FileReader(args[1]);
            Position pos1 = new Position(fr1);
            Lexer lexer1 = new Lexer(pos1);
            Ast ast = Parser.parse(lexer1);
            fr1.close();

            if (lexer1.getErrors().size() != 0) {
                System.out.println("SCHEME LEXICAL ERROR");
                for (LexicError lexicError : lexer1.getErrors())
                    System.out.println(lexicError);
                return;
            }
            scmData = ScmDataBuilder.build(ast);
        } catch (IOException ex) {
            System.out.println("SCHEME INPUT ERROR");
            System.out.println(ex.getMessage());
            return;
        } catch (ParserException ex) {
            System.out.println("SCHEME SYNTAX ERROR");
            System.out.println(ex.getMessage());
            return;
        } catch (ScmToAutException ex) {
            System.out.println("SCHEME SEMANTIC ERROR");
            System.out.println(ex.getMessage());
            return;
        }

        try {
            FileReader fr2 = new FileReader(args[0]);
            Position pos2 = new Position(fr2);
            ru.wendex.sta.adl.Lexer lexer2 = new ru.wendex.sta.adl.Lexer(pos2);
            FunctionList fl = ru.wendex.sta.adl.Parser.parse(lexer2);
            fr2.close();

            if (lexer2.getErrors().size() != 0) {
                System.out.println("ADL LEXICAL ERROR");
                for (LexicError lexicError : lexer2.getErrors())
                    System.out.println(lexicError);
                return;
            }
            typeaData = TypeaDataBuilder.build(fl);
        } catch (IOException ex) {
            System.out.println("ADL INPUT ERROR");
            System.out.println(ex.getMessage());
            return;
        } catch (ParserException ex) {
            System.out.println("ADL SYNTAX ERROR");
            System.out.println(ex.getMessage());
            return;
        } catch (SemanticException ex) {
            System.out.println("ADL SEMANTIC ERROR");
            System.out.println(ex.getMessage());
            return;
        }

        TypeMatcherReport report = AutomataTypeMatcher.match(typeaData, scmData);
        report.print();
    }
}
