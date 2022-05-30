package redempt.redlex.token;

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
	public boolean characterMatches(String input, int pos, int offset) {
		return inverted ^ chars.contains(input.charAt(pos));
	}
	
	@Override
	public boolean lengthMatches(int length) {
		return length == 1;
	}
	
	@Override
	public int minLength() {
		return 1;
	}
	
	@Override
	public int maxLength() {
		return 1;
	}

	@Override
	public List<Character> calcFirstCharacters() {
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
