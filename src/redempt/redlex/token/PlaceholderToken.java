package redempt.redlex.token;

import redempt.redlex.data.TokenType;

import java.util.List;

public class PlaceholderToken extends TokenType {
	
	public PlaceholderToken(String name) {
		super(name);
	}
	
	@Override
	public boolean characterMatches(String input, int pos, int offset) {
		return false;
	}
	
	@Override
	public boolean lengthMatches(int length) {
		return false;
	}
	
	@Override
	public int minLength() {
		return 0;
	}
	
	@Override
	public int maxLength() {
		return 0;
	}

	@Override
	public List<Character> calcFirstCharacters() {
		return null;
	}

}
