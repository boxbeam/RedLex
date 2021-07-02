package redempt.redlex.token;

import redempt.redlex.data.TokenType;

public class CharGroupToken extends TokenType {
	
	private char min;
	private char max;
	private boolean inverted;
	
	public CharGroupToken(String name, char min, char max, boolean inverted) {
		super(name);
		this.min = min;
		this.max = max;
		this.inverted = inverted;
	}
	
	public CharGroupToken(String name, char min, char max) {
		this(name, min, max, false);
	}
	
	@Override
	public boolean characterMatches(String input, int pos, int offset) {
		char c = input.charAt(pos);
		return inverted ^ (c <= max && c >= min);
	}
	
	@Override
	public boolean stringMatches(String input) {
		if (input.length() != 1) {
			return false;
		}
		char c = input.charAt(0);
		return inverted ^ (c >= min && c <= max);
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
	
}
