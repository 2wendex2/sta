package ru.wendex.sta.aut;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashSet;

public class AutHashTest {
    @Test
    public void autHashSetTest() {
        HashSet<Integer> a = new HashSet<>();
        HashSet<Integer> b = new HashSet<>();
        a.add(3);
        b.add(5);
        a.add(5);
        b.add(3);
        System.out.println(a.hashCode());
        System.out.println(b.hashCode());
    }

    @Test
    public void autHashRuleTest() {
        ArrayList<Integer> ar = new ArrayList<>();
        ArrayList<Integer> br = new ArrayList<>();
        ar.add(2);
        ar.add(4);
        br.add(2);
        br.add(4);
        Symbol as = new IdentSymbol("qwerty");
        Symbol bs = new IdentSymbol("qwerty");

        RuleSignature rulea = new RuleSignature(as, ar);
        RuleSignature ruleb = new RuleSignature(bs, br);
        System.out.println(rulea.hashCode());
        System.out.println(ruleb.hashCode());

        System.out.println(rulea.equals(ruleb));
    }
}
