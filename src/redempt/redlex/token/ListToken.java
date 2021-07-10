package redempt.redlex.token;

import redempt.redlex.data.LexContext;
import redempt.redlex.data.ParentToken;
import redempt.redlex.data.Token;
import redempt.redlex.data.TokenType;

public class ListToken extends TokenType implements ParentToken {
	
	private TokenType[] children;
	private int minLength = -1;
	private int maxLength = -1;
	
	public ListToken(String name, TokenType... children) {
		super(name);
		this.children = children;
	}
	
	private void initLength() {
		if (minLength != -1) {
			return;
		}
		for (TokenType child : children) {
			minLength += child.minLength();
			if (maxLength == Integer.MAX_VALUE || child.maxLength() == Integer.MAX_VALUE) {
				maxLength = Integer.MAX_VALUE;
				continue;
			}
			maxLength += child.maxLength();
		}
	}
	
	@Override
	public boolean characterMatches(String input, int pos, int offset) {
		return false;
	}
	
	@Override
	public Token findForward(String str, int pos, LexContext ctx) {
		if (!ctx.update(pos, this)) {
			return null;
		}
		Token[] arr = new Token[children.length];
		int start = pos;
		int i = 0;
		for (; i < children.length; i++) {
			Token inst = children[i].findForward(str, pos, ctx);
			if (inst == null) {
				return null;
			}
			arr[i] = inst;
			pos += arr[i].length();
		}
		return new Token(this, str, start, pos, arr);
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
