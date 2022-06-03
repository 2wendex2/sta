package ru.wendex.sta.aut;

import org.junit.jupiter.api.Test;
import ru.wendex.sta.adl.Function;
import ru.wendex.sta.adl.FunctionList;
import ru.wendex.sta.adl.Lexer;
import ru.wendex.sta.adl.Parser;
import ru.wendex.sta.langbase.ParserException;
import ru.wendex.sta.langbase.Position;

import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Scanner;

public class AutOperationsTest {
    @Test
    public void rdTest() throws IOException, ParserException {
        FileReader fr = new FileReader("testfiles/au.txt");
        Position pos = new Position(fr);
        Lexer lexer = new Lexer(pos);
        FunctionList fl = Parser.parse(lexer);
        fr.close();

        Function f = fl.getList().get(0);
        Automata b = f.getRes();

        b.print();
        b.eliminateNotAccessible();
        b.print();
        b.eliminateNotUsedStates();
        b.print();

        Automata a = f.getArgs().get(0);
        a.print();
        b.intersect(a);
        b.eliminateEpsilonRules();
        b.print();

        System.out.println(b.isLanguageEmpty());
        System.out.println(a.isLanguageEmpty());
        b.eliminateEpsilonRules();
        b.print();
        b.determine();
        b.print();
        b.complement(new HashSet<>());
        b.print();
    }

    @Test
    public void procTest() throws IOException, ParserException {
        FileReader fr = new FileReader("testfiles/au.txt");
        Position pos = new Position(fr);
        Lexer lexer = new Lexer(pos);
        FunctionList fl = Parser.parse(lexer);
        fr.close();

        Function f = fl.getList().get(0);
        Automata b = f.getRes();

        b.print();
        Automata c = Procedures.isNull(b);
        c.print();
    }
}
