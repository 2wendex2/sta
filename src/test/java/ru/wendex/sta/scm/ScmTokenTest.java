package ru.wendex.sta.scm;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import ru.wendex.sta.scm.*;

public class ScmTokenTest {
	@Test
	public void rightToken() {
		Token token = new Token(2, 1, 1, 1, 1);
		System.out.println(token);
	}
	
	@Test
	public void lessToken() {
		try {
			Token token = new Token(Token.MIN_TAG - 1, 1, 1, 1, 1);
		} catch (IllegalArgumentException e) {
			System.out.println(e);
		}
	}
	
	@Test
	public void greaterToken() {
		try {
			Token token = new Token(Token.MAX_TAG + 1, 1, 1, 1, 1);
		} catch (IllegalArgumentException e) {
			System.out.println(e);
		}
	}
	
	@Test
	public void rightStringToken() {
		Token token = new StringToken(Token.NUMBER_LITERAL, 2, 1, 4, 6, "123");
		System.out.println(token);
	}
}
