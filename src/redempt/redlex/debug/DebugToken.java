package redempt.redlex.debug;

import redempt.redlex.data.LexContext;
import redempt.redlex.data.ParentToken;
import redempt.redlex.data.Token;
import redempt.redlex.data.TokenType;

import java.util.List;

/**
 * A wrapper for tokens that reports additional information for debug purposes
 */
public class DebugToken extends TokenType implements ParentToken {

	private TokenType child;
	private DebugHistory history;

	public DebugToken(TokenType child, DebugHistory history) {
		super(child.getName());
		this.child = child;
		this.history = history;
	}

	/**
	 * @return The DebugHistory this token is reporting to
	 */
	public DebugHistory getHistory() {
		return history;
	}

	@Override
	public boolean characterMatches(String input, int pos, int offset) {
		return child.characterMatches(input, pos, offset);
	}

	@Override
	public Token tryTokenize(String str, int pos, LexContext ctx) {
		history.begin(this, pos);
		Token value = child.tryTokenize(str, pos, ctx);
		history.result(this, pos, value == null ? 0 : value.length(), value != null);
		return value;
	}

	@Override
	public boolean lengthMatches(int length) {
		return child.lengthMatches(length);
	}

	@Override
	public int minLength() {
		return child.minLength();
	}

	@Override
	public int maxLength() {
		return child.maxLength();
	}

	@Override
	protected List<Character> calcFirstCharacters() {
		return child.getFirstCharacters();
	}

	@Override
	public TokenType[] getChildren() {
		return new TokenType[] {child};
	}

	@Override
	public void setChildren(TokenType[] children) {
		if (children.length > 0) {
			child = children[0];
		}
	}
}
