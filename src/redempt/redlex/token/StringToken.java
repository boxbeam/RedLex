package redempt.redlex.token;

import redempt.redlex.data.TokenType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class StringToken extends TokenType {
	
	private String string;
	private boolean caseSensitive;
	
	public StringToken(String name, String string) {
		this(name, string, true);
	}
	
	public StringToken(String name, String string, boolean caseSensitive) {
		super(name);
		this.string = string;
		this.caseSensitive = caseSensitive;
		if (!caseSensitive) {
			this.string = this.string.toLowerCase(Locale.ROOT);
		}
	}
	
	public boolean isCaseSensitive() {
		return caseSensitive;
	}
	
	@Override
	public boolean characterMatches(String input, int pos, int offset) {
		if (!caseSensitive) {
			return Character.toLowerCase(input.charAt(pos)) == string.charAt(offset);
		}
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
		if (!caseSensitive && string.length() > 0) {
			List<Character> chars = new ArrayList<>();
			chars.add(string.charAt(0));
			chars.add(Character.toUpperCase(string.charAt(0)));
			return chars;
		}
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
