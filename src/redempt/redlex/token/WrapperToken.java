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
	public boolean characterMatches(String input, int pos, int offset) {
		return child.characterMatches(input, pos, offset);
	}

	@Override
	protected Token findForward(String str, int pos, LexContext ctx) {
		return child.tryTokenize(str, pos, ctx);
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
}
