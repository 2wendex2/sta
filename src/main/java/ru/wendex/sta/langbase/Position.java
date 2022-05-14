package ru.wendex.sta.langbase;

import java.io.InputStreamReader;
import java.io.IOException;

public class Position {
	private int line = 1, column = 1;
	private InputStreamReader src;
	private int index = 0;
	private int nextChar;
	private boolean crFlag = false;
	
	public static final int EOF_CHAR = -1;
	public static final int LF_CHAR = 0xA;
	public static final int CR_CHAR = 0xD;
	public static final int NEL_CHAR = 0x85;
	public static final int LS_CHAR = 0x2028;
	public static final int PS_CHAR = 0x2029;
	
	public static boolean isNewline(int c) {
		return c == LF_CHAR || c == NEL_CHAR || c == LS_CHAR || c == PS_CHAR || c == CR_CHAR;
	}
	
	public Position(InputStreamReader src) throws IOException {
		this.src = src;
		next();
	}
	
	public int getLine() {
		return line;
	}
	
	public int getColumn() {
		return column;
	}
	
	public int peek() {
		return nextChar;
	}
	
	public void next() throws IOException {
		if (nextChar == -1)
			return;
		int prevChar = nextChar;
		nextChar = src.read();
		if (prevChar == LF_CHAR || prevChar == NEL_CHAR || prevChar == LS_CHAR || prevChar == PS_CHAR || prevChar == CR_CHAR && nextChar != LF_CHAR) {
			column = 1;
			line++;
		} else
			column++;
	}
}
