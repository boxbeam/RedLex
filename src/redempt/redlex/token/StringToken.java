package redempt.redlex.token;

import redempt.redlex.data.TokenType;

public class StringToken extends TokenType {
	
	private String string;
	
	public StringToken(String name, String string) {
		super(name);
		this.string = string;
	}
	
	@Override
	public boolean characterMatches(String input, int pos, int offset) {
		return input.charAt(pos) == string.charAt(offset);
	}
	
	@Override
	public boolean stringMatches(String input) {
		return input.equals(string);
	}
	
	@Override
	public boolean lengthMatches(int length) {
		return length == string.length();
	}
	
	@Override
	public int minLength() {
		return string.length();
	}
	
	@Override
	public int maxLength() {
		return string.length();
	}
	
}
