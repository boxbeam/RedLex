package redempt.redlex.token;

import redempt.redlex.data.CharTree;
import redempt.redlex.data.LexContext;
import redempt.redlex.data.Token;
import redempt.redlex.data.TokenType;

import java.util.ArrayList;
import java.util.List;

public class StringChoiceToken extends TokenType {

	private CharTree tree = new CharTree();
	private int min = Integer.MAX_VALUE;
	private int max = 0;
	
	public StringChoiceToken(String name, String[] choices) {
		super(name);
		for (String choice : choices) {
			tree.set(choice);
			min = Math.min(min, choice.length());
			max = Math.max(max, choice.length());
		}
	}
	
	@Override
	protected Token findForward(String str, int pos, LexContext ctx) {
		int len = tree.findForward(str, pos);
		if (len == -1) {
			return null;
		}
		return new Token(this, str, pos, len, null);
	}
	
	@Override
	public boolean characterMatches(String input, int pos, int offset) {
		return false;
	}
	
	@Override
	public boolean lengthMatches(int length) {
		return length >= min && length <= max;
	}
	
	@Override
	public int minLength() {
		return min;
	}
	
	@Override
	public int maxLength() {
		return max;
	}

	@Override
	protected List<Character> calcFirstCharacters() {
		return new ArrayList<>(tree.getFirstChars());
	}

}
