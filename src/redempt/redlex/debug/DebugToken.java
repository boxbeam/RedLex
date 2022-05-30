package redempt.redlex.debug;

import redempt.redlex.data.LexContext;
import redempt.redlex.data.ParentToken;
import redempt.redlex.data.Token;
import redempt.redlex.data.TokenType;

import java.util.List;

public class DebugToken extends TokenType implements ParentToken {

	private TokenType child;
	private DebugHistory history;

	public DebugToken(TokenType child, DebugHistory history) {
		super(child.getName());
		this.child = child;
		this.history = history;
	}

	public DebugHistory getHistory() {
		return history;
	}

	@Override
	public boolean characterMatches(String input, int pos, int offset) {
		return child.characterMatches(input, pos, offset);
	}

	@Override
	public Token findForward(String str, int pos, LexContext ctx) {
		history.begin(this, pos);
		Token value = child.findForward(str, pos, ctx);
		history.result(this, pos, value != null);
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
	public List<Character> calcFirstCharacters() {
		return child.calcFirstCharacters();
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
