package ru.wendex.sta.langbase;

import java.util.ArrayList;
import java.util.HashMap;
import ru.wendex.sta.langbase.LexicError;
import java.io.IOException;

public interface Lexer {
	Token peek();
	ArrayList<LexicError> getErrors();
	void next() throws IOException;
}
