package redempt.redlex.token;

import redempt.redlex.data.TokenType;

import java.util.Collections;
import java.util.List;

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
	public boolean lengthMatches(int length) {
		return length == string.length();
	}
	
	public String getString() {
		return string;
	}
	
	@Override
	public int minLength() {
		return string.length();
	}
	
	@Override
	public int maxLength() {
		return string.length();
	}

	@Override
	public List<Character> calcFirstCharacters() {
		return Collections.singletonList(string.length() == 0 ? null : string.charAt(0));
	}

	@Override
	public int hashCode() {
		return string.hashCode();
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof StringToken)) {
			return false;
		}
		StringToken token = (StringToken) o;
		return token.string.equals(string);
	}

}
