package redempt.redlex.token;

import redempt.redlex.data.LexContext;
import redempt.redlex.data.ParentToken;
import redempt.redlex.data.Token;
import redempt.redlex.data.TokenType;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class RepeatingToken extends TokenType implements ParentToken {
	
	private TokenType token;
	private int minMatches;
	private int maxMatches;
	
	public RepeatingToken(String name, TokenType token, int minMatches, int maxMatches) {
		super(name);
		this.token = token;
		this.minMatches = minMatches;
		this.maxMatches = maxMatches;
	}
	
	public RepeatingToken(String name, TokenType token) {
		this(name, token, 1, Integer.MAX_VALUE);
	}
	
	@Override
	public boolean characterMatches(String input, int pos, int offset) {
		return false;
	}
	
	@Override
	protected Token findForward(String str, int pos, LexContext ctx) {
		List<Token> list = new ArrayList<>();
		int start = pos;
		while (pos < str.length() && list.size() < maxMatches) {
			Token inst = token.tryTokenize(str, pos, ctx);
			if (inst == null) {
				break;
			}
			list.add(inst);
			pos += inst.length();
			if (inst.length() == 0) {
				break;
			}
		}
		if (list.size() >= minMatches) {
			return new Token(this, str, start, pos, list.toArray(Token.EMPTY));
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
	protected List<Character> calcFirstCharacters() {
		Set<Character> chars = new HashSet<>();
		if (minMatches == 0) {
			chars.add(null);
		}
		chars.addAll(token.getFirstCharacters());
		return new ArrayList<>(chars);
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
