package ru.wendex.sta.langbase;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import ru.wendex.sta.langbase.*;
import java.io.*;

class PositionTest {
	@Test
	void fileRead() throws IOException {
		FileReader fr = new FileReader("testfiles/position1.txt");
		Position pos = new Position(fr);
		while (pos.peek() != Position.EOF_CHAR) {
			System.out.println(pos.peek());
			pos.next();
		}		
		fr.close();
	}
}