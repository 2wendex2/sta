package ru.wendex.sta.aut;

import java.util.Scanner;
import org.junit.jupiter.api.Test;
import java.io.*;
import java.util.ArrayList;

public class AutTest {
	@Test
	public void consTest() throws IOException {
		FileInputStream fr = new FileInputStream("testfiles/schemeaut.txt");
		
		Scanner sc = new Scanner(fr);
	
		Automata aut1 = readAutomata(sc);
		Automata aut2 = readAutomata(sc);
		Automata aut = Procedures.consProc(aut1, aut2);
		aut.print();
		
		fr.close();
	}
	
	@Test
	public void carTest() throws IOException, NotSupportedProcedureException {
		FileInputStream fr = new FileInputStream("testfiles/schemeaut.txt");
		
		Scanner sc = new Scanner(fr);
	
		Automata aut1 = readAutomata(sc);
		Automata aut = Procedures.carProc(aut1);
		aut1.print();
		aut.print();
		
		fr.close();
	}
	
	@Test
	public void cdrTest() throws IOException, NotSupportedProcedureException {
		FileInputStream fr = new FileInputStream("testfiles/schemeaut.txt");
		
		Scanner sc = new Scanner(fr);
	
		Automata aut1 = readAutomata(sc);
		Automata aut = Procedures.cdrProc(aut1);
		aut1.print();
		aut.print();
		
		fr.close();
	}
	
	public Automata readAutomata(Scanner sc) throws IOException {
		Automata aut = Automata.createEmpty();
		int rlsNumber = sc.nextInt();
		int maxState = 0;
		
		for (int i = 0; i < rlsNumber; i++) {
			int symb = sc.nextInt();
			ArrayList<Integer> args = new ArrayList<>();
			for (int j = 0; j < symb; j++) {
				int c = sc.nextInt();
				args.add(c);
			}
			int res = sc.nextInt();
			
			Symbol symbol;
			if (symb == 0)
				symbol = KeySymbol.NULL;
			else
				symbol = KeySymbol.PAIR;
			Rule rule = new Rule(symbol, args, res);
			aut.addRuleSafe(rule);
		}
		int finalCount = sc.nextInt();
		
		for (int i = 0; i < finalCount; i++) {
			aut.addFinalStateSafe(sc.nextInt());
		}
		return aut;
	}
}
