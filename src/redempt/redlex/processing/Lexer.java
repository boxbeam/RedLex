package redempt.redlex.processing;

import redempt.redlex.data.LexContext;
import redempt.redlex.data.Token;
import redempt.redlex.data.TokenType;
import redempt.redlex.debug.DebugLexer;
import redempt.redlex.exception.LexException;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

/**
 * A lexer which will tokenize an input String. Best created with {@link redempt.redlex.bnf.BNFParser#createLexer(Path)}
 * @author Redempt
 */
public class Lexer {

	private TokenType root;
	private boolean retainEmpty = false;
	private boolean retainStringLiterals = true;
	private CullStrategy unnamedRule = CullStrategy.IGNORE;
	private Map<String, CullStrategy> byName = new HashMap<>();
	
	/**
	 * Create a Lexer from a token. Not recommended unless you want to do everything by hand.
	 * @param root The root TokenType for this Lexer
	 */
	public Lexer(TokenType root) {
		this.root = root;
		root.setLexer(this);
	}

	/**
	 * Create a DebugLexer to diagnose issues with your bnf. Warning: Irreversibly mutates all tokens, even using this Lexer.
	 * @return A DebugLexer which can be used to follow the lexing process and determine where something went wrong
	 */
	public DebugLexer debug() {
		return new DebugLexer(root);
	}

	/**
	 * @return The root TokenType for this Lexer
	 */
	public TokenType getRoot() {
		return root;
	}
	
	public CullStrategy getStrategy(Token token) {
		if (token.length() == 0 && !retainEmpty) {
			return CullStrategy.DELETE_ALL;
		}
		if (token.getType().getName() == null) {
			return unnamedRule;
		}
		if (token.getType().getName().startsWith("'") && !retainStringLiterals) {
			return CullStrategy.DELETE_ALL;
		}
		return byName.getOrDefault(token.getType().getName(), CullStrategy.IGNORE);
	}
	
	private int[] cursorPos(String s, int pos) {
		int newlines = 0;
		int cpos = 0;
		for (int i = 0; i < pos && i < s.length(); i++) {
			if (s.charAt(i) == '\n') {
				newlines++;
				cpos = 0;
			}
			cpos++;
		}
		return new int[] {newlines, cpos};
	}
	
	/**
	 * Sets whether this Lexer will retain empty tokens
	 * @param retainEmpty Whether this Lexer should retain empty tokens
	 */
	public void setRetainEmpty(boolean retainEmpty) {
		this.retainEmpty = retainEmpty;
	}
	
	/**
	 * Sets whether this Lexer will retain string literal tokens
	 * @param retainStringLiterals Whether this Lexer should retain string literal tokens
	 */
	public void setRetainStringLiterals(boolean retainStringLiterals) {
		this.retainStringLiterals = retainStringLiterals;
	}
	
	/**
	 * Sets how this Lexer will handle unnamed tokens
	 * @param unnamedRule What should be done with unnamed tokens
	 */
	public void setUnnamedRule(CullStrategy unnamedRule) {
		this.unnamedRule = unnamedRule;
	}
	
	/**
	 * Sets how tokens with given names should be handled
	 * @param strategy The strategy to use on the tokens with the given names
	 * @param names The names to apply the rule to
	 */
	public void setRuleByName(CullStrategy strategy, String... names) {
		for (String name : names) {
			byName.put(name, strategy);
		}
	}
	
	/**
	 * Tokenizes an input String
	 * @param str The string to tokenize
	 * @param errorOnFail Whether to throw a LexException on failure, returns null if false
	 * @return The root Token of the tokenized tree
	 */
	public Token tokenize(String str, boolean errorOnFail) {
		LexContext ctx = new LexContext();
		Token inst = root.findForward(str, 0, ctx);
		if (inst == null || inst.length() != str.length()) {
			if (!errorOnFail) {
				return null;
			}
			TokenType type = ctx.getLastToken();
			type = type == null ? root : type;
			int[] pos = cursorPos(str, ctx.getLastPos());
			String[] split = str.split("\n");
			throw new LexException(type.getMessage() + " token on line " + (pos[0] + 1) + ", column " + pos[1] + ": " + type
					+ "\n" + split[pos[0]]
					+ "\n" + repeat(" ", pos[1]) + "^");
		}
		return inst;
	}
	
	/**
	 * Tokenizes an input String
	 * @param str The string to tokenize
	 * @return The root Token of the tokenized tree
	 */
	public Token tokenize(String str) {
		return tokenize(str, true);
	}
	
	private String repeat(String str, int times) {
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < times; i++) {
			builder.append(str);
		}
		return builder.toString();
	}
	
}
