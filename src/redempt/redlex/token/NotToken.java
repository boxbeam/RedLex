package redempt.redlex.token;

import redempt.redlex.data.LexContext;
import redempt.redlex.data.ParentToken;
import redempt.redlex.data.Token;
import redempt.redlex.data.TokenType;

public class NotToken extends TokenType implements ParentToken {
	
	private TokenType token;
	
	public NotToken(String name, TokenType token) {
		super(name);
		this.token = token;
	}
	
	@Override
	public boolean characterMatches(String input, int pos, int offset) {
		return false;
	}
	
	@Override
	public Token findForward(String str, int pos, LexContext ctx) {
		ctx.update(pos, this);
		Token inst = token.findForward(str, pos, new LexContext());
		if (inst == null) {
			return new Token(this, "", 0, 0);
		}
		return null;
	}
	
	@Override
	public boolean lengthMatches(int length) {
		return length == 0;
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
	public String getMessage() {
		return "Unexpected";
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
