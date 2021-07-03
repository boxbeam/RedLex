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
	
	private static BNFParser bnf = BNFParser.getParser();
	
	@Test
	public void syntaxTest() {
		assertThrows(LexException.class, () -> bnf.createLexer("root::= [a-z]"));
		assertThrows(LexException.class, () -> bnf.createLexer("root ::= \"abc"));
		assertDoesNotThrow(() -> bnf.createLexer("root ::= \"abc\" | [a-z]"));
	}
	
	private void cullJSON(Token token) {
		token.cull(TokenFilter.removeEmpty(),
				TokenFilter.removeUnnamed(CullStrategy.LIFT_CHILDREN),
				TokenFilter.byName(CullStrategy.DELETE_ALL, "sep"),
				TokenFilter.removeStringLiterals());
	}
	
	@Test
	public void selfReferenceTest() {
		assertThrows(BNFException.class, () -> bnf.createLexer("root ::= root"));
		assertThrows(BNFException.class, () -> bnf.createLexer("root ::= test"));
	}
	
	@Test
	public void JSONTest() {
		Path path = Paths.get("res/json.bnf");
		Lexer lexer = bnf.createLexer(path);
		Token token = lexer.tokenize("123");
		cullJSON(token);
		assertEquals("integer [123]", token.toString());
		token = lexer.tokenize("{\"a\": [1, 2, 3, \"b\"]}");
		cullJSON(token);
		assertEquals("map {mapEntry {string [\"a\"], list {integer [1], integer [2], integer [3], string [\"b\"]}}}", token.toString());
		token = lexer.tokenize("[[[[[[]]]]]]");
		cullJSON(token);
		assertEquals("list {list {list {list {list {list [[]]}}}}}", token.toString());
	}
	
}
