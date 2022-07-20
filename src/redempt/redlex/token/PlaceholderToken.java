package redempt.redlex.token;

import redempt.redlex.data.LexContext;
import redempt.redlex.data.Token;
import redempt.redlex.data.TokenType;

import java.util.List;

public class PlaceholderToken extends TokenType {
	
	public PlaceholderToken(String name) {
		super(name);
	}
	
	@Override
	protected Token findForward(String str, int pos, LexContext ctx) {
		return null;
	}
	
	@Override
	public int minLength() {
		return 0;
	}

	@Override
	protected List<Character> calcFirstCharacters() {
		return null;
	}

}
