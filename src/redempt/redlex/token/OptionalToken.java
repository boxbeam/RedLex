package redempt.redlex.token;

import redempt.redlex.data.LexContext;
import redempt.redlex.data.ParentToken;
import redempt.redlex.data.Token;
import redempt.redlex.data.TokenType;

public class OptionalToken extends TokenType implements ParentToken {
	
	private TokenType token;
	
	public OptionalToken(String name, TokenType token) {
		super(name);
		this.token = token;
	}
	
	@Override
	public boolean characterMatches(String input, int pos, int offset) {
		return token.characterMatches(input, pos, offset);
	}
	
	@Override
	public boolean lengthMatches(int length) {
		return length == 0 || token.lengthMatches(length);
	}
	
	@Override
	public int minLength() {
		return 0;
	}
	
	@Override
	public int maxLength() {
		return token.maxLength();
	}
	
	@Override
	public boolean stringMatches(String input) {
		return input.length() == 0 || token.stringMatches(input);
	}
	
	@Override
	public Token findForward(String str, int pos, LexContext ctx) {
		ctx.update(pos, this);
		Token instance = token.findForward(str, pos, ctx);
		if (instance == null) {
			return new Token(this, "", 0, 0);
		}
		Token inst = new Token(this, str, pos, instance.length() + pos);
		inst.setChildren(new Token[] {instance});
		return inst;
	}
	
	@Override
	public TokenType[] getChildren() {
		return new TokenType[] {token};
	}
	
	@Override
	public void setChildren(TokenType[] children) {
		this.token = children[0];
	}
	
}
