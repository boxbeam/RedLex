package redempt.redlex.token;

import redempt.redlex.data.TokenType;

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
	public boolean characterMatches(String input, int pos, int offset) {
		char c = input.charAt(pos);
		return inverted ^ (c <= max && c >= min);
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
