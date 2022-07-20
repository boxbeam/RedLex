package redempt.redlex.token;

import redempt.redlex.data.LexContext;
import redempt.redlex.data.Token;
import redempt.redlex.data.TokenType;

import java.util.*;

public class CharSetToken extends TokenType {

	private Set<Character> chars = new HashSet<>();
	private boolean inverted;
	
	public CharSetToken(String name, boolean inverted, char... chars) {
		super(name);
		for (char c : chars) {
			this.chars.add(c);
		}
		this.inverted = inverted;
	}
	
	public CharSetToken(String name, char... chars) {
		this(name, false, chars);
	}
	
	@Override
	public Token findForward(String input, int pos, LexContext context) {
		return pos < input.length() && (inverted ^ chars.contains(input.charAt(pos)))
				? new Token(this, input, pos, pos + 1)
				: null;
	}
	
	@Override
	public int minLength() {
		return 1;
	}

	@Override
	protected List<Character> calcFirstCharacters() {
		return inverted ? Collections.singletonList(null) : new ArrayList<>(chars);
	}

	@Override
	public int hashCode() {
		return chars.hashCode() + (inverted ? 1 : 0);
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof CharSetToken)) {
			return false;
		}
		CharSetToken token = (CharSetToken) o;
		return token.chars.equals(chars) && token.inverted == inverted;
	}

}
