package redempt.redlex.data;

import redempt.redlex.processing.Lexer;
import redempt.redlex.token.PlaceholderToken;

import java.util.*;
import java.util.function.Consumer;

/**
 * Represents a type of token which can be used to create a Lexer
 */
public abstract class TokenType {

	private static final Map<TokenType, List<Character>> firstChars = new WeakHashMap<>();

	private String name;
	private Lexer lexer;
	private List<Character> firstCharacters;
	
	/**
	 * Create a new TokenType with the given name
	 * @param name The name for this TokenType
	 */
	public TokenType(String name) {
		this.name = name;
	}
	
	/**
	 * Sets the Lexer this TokenType and its children belong to
	 * @param lexer The Lexer
	 */
	public void setLexer(Lexer lexer) {
		setLexer(lexer, new HashSet<>());
	}
	
	private void setLexer(Lexer lexer, Set<TokenType> seen) {
		if (!seen.add(this)) {
			return;
		}
		this.lexer = lexer;
		if (this instanceof ParentToken) {
			for (TokenType child : ((ParentToken) this).getChildren()) {
				child.setLexer(lexer, seen);
			}
		}
	}
	
	/**
	 * @return The Lexer this TokenType belongs to
	 */
	public Lexer getLexer() {
		return lexer;
	}
	
	/**
	 * Check whether the name of this TokenType matches the given name
	 * @param name The name to compare
	 * @return Whether this TokenType's name matches the given name
	 */
	public boolean nameMatches(String name) {
		if (name == this.name) {
			return true;
		}
		return this.name != null && this.name.equals(name);
	}
	
	/**
	 * @return The name of this TokenType
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Sets the name of this TokenType
	 * @param name The name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	
	public Token findForward(String str, int pos, LexContext ctx) {
		ctx.update(pos, this);
		int start = pos;
		int offset = 0;
		while (pos < str.length() && offset < maxLength() && characterMatches(str, pos, offset)) {
			pos++;
			offset++;
		}
		if (!lengthMatches(offset)) {
			return null;
		}
		return new Token(this, str, start, pos);
	}
	
	public String getMessage() {
		return "Expected";
	}
	
	public void replacePlaceholders(Map<String, TokenType> tokens) {
		walk(t -> {
			if (!(t instanceof ParentToken)) {
				return;
			}
			ParentToken parent = (ParentToken) t;
			TokenType[] children = parent.getChildren();
			for (int i = 0; i < children.length; i++) {
				TokenType child = children[i];
				if (!(child instanceof PlaceholderToken) || !tokens.containsKey(child.getName())) {
					continue;
				}
				children[i] = tokens.get(child.getName());
			}
			parent.setChildren(children);
		});
	}
	
	public void walk(Consumer<TokenType> each) {
		walk(each, new HashSet<>());
	}
	
	private void walk(Consumer<TokenType> each, Set<TokenType> visited) {
		if (!visited.add(this)) {
			return;
		}
		each.accept(this);
		if (this instanceof ParentToken) {
			ParentToken parent = (ParentToken) this;
			for (TokenType child : parent.getChildren()) {
				child.walk(each, visited);
			}
		}
	}
	
	public abstract boolean characterMatches(String input, int pos, int offset);
	public abstract boolean lengthMatches(int length);
	public abstract int minLength();
	public abstract int maxLength();
	protected abstract List<Character> calcFirstCharacters();

	/**
	 * @return A list of all the characters which may appear as the first character of this token. Includes null if this token may be zero-length.
	 */
	public List<Character> getFirstCharacters() {
		if (firstCharacters == null) {
			if (!firstChars.containsKey(this)) {
				firstChars.put(this, calcFirstCharacters());
			}
			firstCharacters = firstChars.get(this);
		}
		return firstCharacters;
	}
	
	@Override
	public String toString() {
		return name == null ? "(unnamed token)" : name;
	}
	
}
