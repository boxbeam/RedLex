package redempt.redlex.processing;

import redempt.redlex.data.LexContext;
import redempt.redlex.data.Token;
import redempt.redlex.data.TokenType;
import redempt.redlex.exception.LexException;

import java.nio.file.Path;

/**
 * A lexer which will tokenize an input String. Best created with {@link redempt.redlex.bnf.BNFParser#createLexer(Path)}
 * @author Redempt
 */
public class Lexer {

	private TokenType root;
	
	/**
	 * Create a Lexer from a token. Not recommended unless you want to do everything by hand.
	 * @param root The root TokenType for this Lexer
	 */
	public Lexer(TokenType root) {
		this.root = root;
	}
	
	/**
	 * @return The root TokenType for this Lexer
	 */
	public TokenType getRoot() {
		return root;
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
	 * Tokenizes an input String
	 * @param str The string to tokenize
	 * @param errorOnFail Whether to throw a LexException on failure
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
