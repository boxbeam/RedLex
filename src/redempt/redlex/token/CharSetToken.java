package redempt.redlex.token;

import redempt.redlex.data.TokenType;

import java.util.HashSet;
import java.util.Set;

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
	
}
