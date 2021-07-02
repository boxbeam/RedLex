package redempt.redlex.token;

import redempt.redlex.data.LexContext;
import redempt.redlex.data.ParentToken;
import redempt.redlex.data.Token;
import redempt.redlex.data.TokenType;

import java.util.ArrayList;
import java.util.List;

public class RepeatingToken extends TokenType implements ParentToken {
	
	private TokenType token;
	
	public RepeatingToken(String name, TokenType token) {
		super(name);
		this.token = token;
	}
	
	@Override
	public boolean characterMatches(String input, int pos, int offset) {
		return false;
	}
	
	@Override
	public boolean stringMatches(String input) {
		if (input.length() == 0) {
			return token.findForward(input, 0, new LexContext()) != null;
		}
		int pos = 0;
		while (pos < input.length()) {
			Token inst = token.findForward(input, pos, new LexContext(pos));
			if (inst == null) {
				return false;
			}
			pos += inst.getValue().length();
			if (pos == 0) {
				break;
			}
		}
		return true;
	}
	
	@Override
	public Token findForward(String str, int pos, LexContext ctx) {
		ctx.update(pos, this);
		List<Token> list = new ArrayList<>();
		int start = pos;
		while (pos < str.length()) {
			Token inst = token.findForward(str, pos, ctx);
			if (inst == null) {
				break;
			}
			list.add(inst);
			pos += inst.length();
			if (inst.length() == 0) {
				break;
			}
		}
		if (list.size() > 0) {
			return new Token(this, str, start, pos, list.toArray(new Token[0]));
		}
		return null;
	}
	
	@Override
	public boolean lengthMatches(int length) {
		if (token.minLength() == token.maxLength()) {
			return length > 0 && length % token.maxLength() == 0;
		}
		return length >= token.minLength();
	}
	
	@Override
	public int minLength() {
		return token.minLength();
	}
	
	@Override
	public int maxLength() {
		return Integer.MAX_VALUE;
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
