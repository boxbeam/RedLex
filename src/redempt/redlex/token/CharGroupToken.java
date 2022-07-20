package redempt.redlex.token;

import redempt.redlex.data.LexContext;
import redempt.redlex.data.Token;
import redempt.redlex.data.TokenType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class CharGroupToken extends TokenType {
	
	private int min;
	private int max;
	private boolean inverted;
	
	public CharGroupToken(String name, int min, int max, boolean inverted) {
		super(name);
		this.min = min;
		this.max = max;
		this.inverted = inverted;
	}
	
	public CharGroupToken(String name, int min, int max) {
		this(name, min, max, false);
	}
	
	@Override
	protected Token findForward(String str, int pos, LexContext ctx) {
		if (pos >= str.length()) {
			return null;
		}
		char c = str.charAt(pos);
		return inverted ^ (c <= max && c >= min)
				? new Token(this, str, pos, pos + 1)
				: null;
	}
	
	@Override
	public int minLength() {
		return 1;
	}

	@Override
	protected List<Character> calcFirstCharacters() {
		if (inverted) {
			return Collections.singletonList(null);
		}
		List<Character> list = new ArrayList<>();
		for (int i = min; i < max; i++) {
			list.add((char) i);
		}
		return list;
	}

	@Override
	public int hashCode() {
		return Objects.hash(min, max, inverted);
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof CharGroupToken)) {
			return false;
		}
		CharGroupToken token = (CharGroupToken) o;
		return token.inverted == inverted && token.min == min && token.max == max;
	}

}
