package redempt.redlex.debug;

import redempt.redlex.data.ParentToken;
import redempt.redlex.data.Token;
import redempt.redlex.data.TokenType;
import redempt.redlex.processing.Lexer;

import java.util.HashSet;
import java.util.Set;

/**
 * A specialized Lexer used for debugging
 * @author Redempt
 */
public class DebugLexer extends Lexer {

	private static TokenType debug(TokenType root, DebugHistory history) {
		Set<ParentToken> parents = new HashSet<>();
		root.walk(t -> {
			if (t instanceof ParentToken) {
				parents.add((ParentToken) t);
			}
		});
		for (ParentToken token : parents) {
			TokenType[] children = token.getChildren();
			for (int i = 0; i < children.length; i++) {
				children[i] = new DebugToken(children[i], history);
			}
			token.setChildren(children);
		}

		return new DebugToken(root, history);
	}

	private DebugHistory history;

	/**
	 * Create a Lexer from a token. Not recommended unless you want to do everything by hand.
	 *
	 * @param root The root TokenType for this Lexer
	 */
	public DebugLexer(TokenType root) {
		super(debug(root, new DebugHistory()));
		history = ((DebugToken) getRoot()).getHistory();
	}

	@Override
	public Token tokenize(String str) {
		return tokenize(str, false);
	}

	@Override
	public Token tokenize(String str, boolean errorOnFail) {
		history.setString(str);
		return super.tokenize(str, errorOnFail);
	}

	/**
	 * @return The DebugHistory representing all steps for the previous call to tokenize
	 */
	public DebugHistory getDebugHistory() {
		return history;
	}
}
