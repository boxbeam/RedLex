package redempt.redlex.token;

import redempt.redlex.data.LexContext;
import redempt.redlex.data.ParentToken;
import redempt.redlex.data.Token;
import redempt.redlex.data.TokenType;

import java.util.List;

public class WrapperToken extends TokenType implements ParentToken {

	private TokenType child;

	/**
	 * Create a new TokenType with the given name
	 *
	 * @param name The name for this TokenType
	 */
	public WrapperToken(String name, TokenType child) {
		super(name);
		this.child = child;
	}

	@Override
	public TokenType[] getChildren() {
		return new TokenType[] {child};
	}

	@Override
	public void setChildren(TokenType[] children) {
		child = children[0];
	}

	@Override
	protected Token findForward(String str, int pos, LexContext ctx) {
		Token token = child.tryTokenize(str, pos, ctx);
		if (token == null) {
			return null;
		}
		return new Token(this, str, token.getStart(), token.getEnd(), new Token[] {token});
	}

	@Override
	public int minLength() {
		return child.minLength();
	}

	@Override
	protected List<Character> calcFirstCharacters() {
		return child.getFirstCharacters();
	}
}
