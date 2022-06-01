package redempt.redlex.test;

import org.junit.jupiter.api.Test;
import redempt.redlex.bnf.BNFParser;
import redempt.redlex.data.Token;
import redempt.redlex.debug.DebugLexer;
import redempt.redlex.exception.BNFException;
import redempt.redlex.exception.LexException;
import redempt.redlex.processing.CullStrategy;
import redempt.redlex.processing.Lexer;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;

import static org.junit.jupiter.api.Assertions.*;

public class RedLexTest {

	@Test
	public void syntaxTest() {
		assertThrows(LexException.class, () -> BNFParser.createLexer("root::= [a-z]"));
		assertThrows(LexException.class, () -> BNFParser.createLexer("root ::= \"abc"));
		assertDoesNotThrow(() -> BNFParser.createLexer("root ::= \"abc\" | [a-z]"));
	}
	
	private Token cullJSON(Token token) {
		return token.getChildren()[0];
	}
	
	@Test
	public void selfReferenceTest() {
		assertThrows(BNFException.class, () -> BNFParser.createLexer("root ::= root"));
		assertThrows(BNFException.class, () -> BNFParser.createLexer("root ::= test"));
	}

	@Test
	public void unicodeEscapeTest() {
		Lexer lexer = BNFParser.createLexer("root ::= \"\\u0061\"");
		assertDoesNotThrow(() -> lexer.tokenize("a"));
		assertThrows(LexException.class, () -> lexer.tokenize("b"));
		assertThrows(LexException.class, () -> lexer.tokenize("u0061"));
	}

	@Test
	public void JSONTest() {
		Lexer lexer = BNFParser.createLexer(getClass().getClassLoader().getResourceAsStream("json.bnf"));
		lexer.setRetainEmpty(false);
		lexer.setRetainStringLiterals(false);
		lexer.setUnnamedRule(CullStrategy.LIFT_CHILDREN);
		lexer.setRuleByName(CullStrategy.DELETE_ALL, "sep");
		lexer.setRuleByName(CullStrategy.LIFT_CHILDREN, "object");
		
		Token token = lexer.tokenize("123");
		token = cullJSON(token);
		assertEquals("integer [123]", token.toString());
		token = lexer.tokenize("{\"a\": [1, 2, 3, \"b\"]}");
		token = cullJSON(token);
		assertEquals("map {mapEntry {string [\"a\"], list {integer [1], integer [2], integer [3], string [\"b\"]}}}", token.toString());
		token = lexer.tokenize("[[[[[[]]]]]]");
		token = cullJSON(token);
		assertEquals("list {list {list {list {list {list [[]]}}}}}", token.toString());
	}

	@Test
	public void similarTokenAfterRepeatingToken() {
		Lexer lexer = BNFParser.createLexer(getClass().getClassLoader().getResourceAsStream("a_after_repeating_ab.bnf"));
		assertDoesNotThrow(() -> lexer.tokenize("aa"));
		assertThrows(LexException.class, () -> lexer.tokenize("ab"));
		assertDoesNotThrow(() -> lexer.tokenize("ababaa"));
		assertThrows(LexException.class, () -> lexer.tokenize("bababb"));
	}

	@Test
	public void similarTokenAfterOptionalToken() {
		Lexer lexer = BNFParser.createLexer(getClass().getClassLoader().getResourceAsStream("a_after_optional_ab.bnf"));
		assertDoesNotThrow(() -> lexer.tokenize("a"));
		assertThrows(LexException.class, () -> lexer.tokenize("b"));
		assertDoesNotThrow(() -> lexer.tokenize("aa"));
		assertThrows(LexException.class, () -> lexer.tokenize("ab"));
		assertDoesNotThrow(() -> lexer.tokenize("ba"));
		assertThrows(LexException.class, () -> lexer.tokenize("aaa"));

	}

	@Test
	public void notNewlineTest() {
		Lexer lexer = BNFParser.createLexer("root ::= [^\\n]");
		assertThrows(LexException.class, () -> lexer.tokenize("\n"));
		assertDoesNotThrow(() -> lexer.tokenize("a"));
	}

	@Test
	public void recursionStopTest() {
		Lexer lexer = BNFParser.createLexer(getClass().getClassLoader().getResourceAsStream("recursive_list.bnf"));
		assertDoesNotThrow(() -> lexer.tokenize("a b c"));

		Lexer lexer2 = BNFParser.createLexer(getClass().getClassLoader().getResourceAsStream("numbers.bnf"));
		assertDoesNotThrow(() -> lexer2.tokenize("1.23"));
		assertDoesNotThrow(() -> lexer2.tokenize("-4"));
		assertThrows(LexException.class, () -> lexer2.tokenize("abc"));
	}

	@Test
	public void endOfFileRequired() {
		Lexer lexer = BNFParser.createLexer(getClass().getClassLoader().getResourceAsStream("eof_token.bnf"));

		assertDoesNotThrow(() -> lexer.tokenize("a"));
		assertDoesNotThrow(() -> lexer.tokenize("ba"));
		assertDoesNotThrow(() -> lexer.tokenize("bba"));

		assertThrows(LexException.class, () -> lexer.tokenize("ab"));
		assertThrows(LexException.class, () -> lexer.tokenize("aba"));
	}

	@Test
	public void parenTest() {
		Lexer lexer = BNFParser.createLexer(getClass().getClassLoader().getResourceAsStream("parens.bnf"));
		
		assertDoesNotThrow(() -> lexer.tokenize(""));
		assertDoesNotThrow(() -> lexer.tokenize("()()()"));
		assertDoesNotThrow(() -> lexer.tokenize("()()(()())"));
		assertThrows(LexException.class, () -> lexer.tokenize(")("));
		assertThrows(LexException.class, () -> lexer.tokenize("())(()"));
		assertThrows(LexException.class, () -> lexer.tokenize("(()()("));
	}
	
	@Test
	public void quantifierTest() {
		Lexer lexer = BNFParser.createLexer("root ::= [0-9]{3,16}");
		
		assertDoesNotThrow(() -> lexer.tokenize("123456"));
		assertThrows(LexException.class, () -> lexer.tokenize("12"));
		
		Lexer lexer2 = BNFParser.createLexer("root ::= [0-9]{,16}");
		
		assertDoesNotThrow(() -> lexer2.tokenize("12"));
		assertThrows(LexException.class, () -> lexer2.tokenize("12345678901234567"));

		Lexer lexer3 = BNFParser.createLexer("name ::= \"a\"\nroot ::= name{3}");
		assertDoesNotThrow(() -> lexer3.tokenize("aaa"));
		assertThrows(LexException.class, () -> lexer3.tokenize("aa"));
	}
	
	@Test
	public void caseInsensitiveTest() {
		Lexer lexer = BNFParser.createLexer("root ::= i\"abc\"");
		assertDoesNotThrow(() -> lexer.tokenize("abc"));
		assertDoesNotThrow(() -> lexer.tokenize("aBc"));
		assertDoesNotThrow(() -> lexer.tokenize("Abc"));
		assertDoesNotThrow(() -> lexer.tokenize("ABC"));
		
		Lexer lexer2 = BNFParser.createLexer("root ::= \"abc\"");
		assertDoesNotThrow(() -> lexer2.tokenize("abc"));
		assertThrows(LexException.class, () -> lexer2.tokenize("Abc"));
		assertThrows(LexException.class, () -> lexer2.tokenize("aBc"));
		assertThrows(LexException.class, () -> lexer2.tokenize("aBC"));
	}
	
}
