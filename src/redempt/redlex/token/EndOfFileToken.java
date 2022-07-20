package redempt.redlex.token;

import redempt.redlex.data.LexContext;
import redempt.redlex.data.Token;
import redempt.redlex.data.TokenType;

import java.util.Collections;
import java.util.List;

public class EndOfFileToken extends TokenType {
	
	public EndOfFileToken(String name) {
		super(name);
	}
	
	@Override
	protected Token findForward(String str, int pos, LexContext ctx) {
		return pos == str.length() ? new Token(this, "", 0, 0) : null;
	}
	
	@Override
	public int minLength() {
		return 0;
	}

	@Override
	protected List<Character> calcFirstCharacters() {
		return Collections.singletonList(null);
	}

}
