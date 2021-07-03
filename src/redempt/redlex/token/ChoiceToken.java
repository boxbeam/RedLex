package redempt.redlex.token;

import redempt.redlex.data.LexContext;
import redempt.redlex.data.ParentToken;
import redempt.redlex.data.Token;
import redempt.redlex.data.TokenType;

public class ChoiceToken extends TokenType implements ParentToken {
	
	private TokenType[] children;
	private int minLength = -1;
	private int maxLength = -1;
	
	public ChoiceToken(String name, TokenType... children) {
		super(name);
		this.children = children;
	}
	
	private void initLength() {
		if (minLength != -1) {
			return;
		}
		for (TokenType child : children) {
			minLength = Math.min(child.minLength(), minLength);
			maxLength = Math.max(child.maxLength(), maxLength);
		}
	}
	
	@Override
	public boolean characterMatches(String input, int pos, int offset) {
		return false;
	}
	
	@Override
	public boolean stringMatches(String input) {
		for (TokenType child : children) {
			if (child.stringMatches(input)) {
				return true;
			}
		}
		return false;
	}
	
	@Override
	public Token findForward(String str, int pos, LexContext ctx) {
		ctx.update(pos, this);
		for (TokenType child : children) {
			Token inst = child.findForward(str, pos, ctx);
			if (inst != null) {
				return new Token(this, inst.getBaseString(), inst.getStart(), inst.getEnd(), new Token[] {inst});
			}
		}
		return null;
	}
	
	@Override
	public boolean lengthMatches(int length) {
		initLength();
		return length >= minLength && length <= maxLength;
	}
	
	@Override
	public int minLength() {
		initLength();
		return minLength;
	}
	
	@Override
	public int maxLength() {
		initLength();
		return maxLength;
	}
	
	@Override
	public TokenType[] getChildren() {
		return children;
	}
	
	@Override
	public void setChildren(TokenType[] children) {
		this.children = children;
	}
	
}
