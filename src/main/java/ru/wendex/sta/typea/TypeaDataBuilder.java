package ru.wendex.sta.typea;

import ru.wendex.sta.adl.AdlAutomata;
import ru.wendex.sta.adl.Function;
import ru.wendex.sta.adl.FunctionList;
import ru.wendex.sta.aut.Automata;

import java.util.ArrayList;

public class TypeaDataBuilder {
    public static TypeaData build(FunctionList lst) {
        ArrayList<TypeaFunction> tfs = new ArrayList<>();
        for (Function f : lst.getList()) {
            Automata res = AdlToScmConverter.convert(f.getRes());
            String s = f.getName();
            ArrayList<Automata> args = new ArrayList<>();
            for (AdlAutomata a : f.getArgs()) {
                args.add(AdlToScmConverter.convert(a));
            }
            tfs.add(new TypeaFunction(s, args, res));
        }
        return new TypeaData(tfs);
    }
}
