package redempt.redlex.test;

import org.junit.jupiter.api.Test;
import redempt.redlex.bnf.BNFParser;
import redempt.redlex.data.Token;
import redempt.redlex.exception.BNFException;
import redempt.redlex.exception.LexException;
import redempt.redlex.processing.CullStrategy;
import redempt.redlex.processing.Lexer;
import redempt.redlex.processing.TokenFilter;

import java.nio.file.Path;
import java.nio.file.Paths;

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
	
}
