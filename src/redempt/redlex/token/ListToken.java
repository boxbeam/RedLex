package redempt.redlex.token;

import redempt.redlex.data.LexContext;
import redempt.redlex.data.ParentToken;
import redempt.redlex.data.Token;
import redempt.redlex.data.TokenType;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ListToken extends TokenType implements ParentToken {
	
	private TokenType[] children;
	private int minLength = -1;
	
	public ListToken(String name, TokenType... children) {
		super(name);
		this.children = children;
	}
	
	private void initLength() {
		if (minLength != -1) {
			return;
		}
		minLength = 0;
		for (TokenType child : children) {
			minLength += child.minLength();
		}
	}
	
	@Override
	protected Token findForward(String str, int pos, LexContext ctx) {
		Token[] arr = new Token[children.length];
		int start = pos;
		int i = 0;
		for (; i < children.length; i++) {
			Token inst = children[i].tryTokenize(str, pos, ctx);
			if (inst == null) {
				return null;
			}
			arr[i] = inst;
			pos += arr[i].length();
		}
		return new Token(this, str, start, pos, arr);
	}
	
	@Override
	public int minLength() {
		initLength();
		return minLength;
	}

	@Override
	protected List<Character> calcFirstCharacters() {
		Set<Character> chars = new HashSet<>();
		int i = 0;
		for (; i < children.length && children[i].minLength() == 0; i++) {
			chars.addAll(children[i].getFirstCharacters());
		}
		if (i < children.length) {
			chars.addAll(children[i].getFirstCharacters());
		}
		return new ArrayList<>(chars);
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
