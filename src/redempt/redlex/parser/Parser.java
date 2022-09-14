package redempt.redlex.parser;

import redempt.redlex.data.Token;
import redempt.redlex.data.TokenType;
import redempt.redlex.processing.Lexer;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/**
 * A parser which will parse an input string and convert it according to predefined rules
 * @author Redempt
 */
public class Parser {
	
	/**
	 * Creates a parser from a lexer and components
	 * @param lexer The Lexer used to tokenize input
	 * @param components The components used to parse tokens
	 * @return A Parser using the given components and lexer
	 */
	public static Parser create(Lexer lexer, ParserComponent... components) {
		Map<String, ParserComponent> map = new HashMap<>();
		for (ParserComponent component : components) {
			map.put(component.getName(), component);
		}
		return new Parser(lexer, map);
	}
	
	private ParserComponent[] components;
	private Lexer lexer;
	
	private Parser(Lexer lexer, Map<String, ParserComponent> components) {
		this.lexer = lexer;
		Set<TokenType> tokens = new TreeSet<>(Comparator.comparingInt(TokenType::getId));
		lexer.getRoot().walk(tokens::add);
		this.components = new ParserComponent[tokens.size() + 2];
		for (TokenType type : tokens) {
			this.components[type.getId()] = components.get(type.getName());
		}
	}
	
	/**
	 * @return The Lexer this Parser wraps
	 */
	public Lexer getLexer() {
		return lexer;
	}
	
	/**
	 * Parses the input string and returns the result
	 * @param input The string to parse
	 * @param errorOnFail Whether to throw a LexException for invalid format, returns null if false
	 * @return The parsed object
	 */
	public Object parse(String input, boolean errorOnFail) {
		Token token = lexer.tokenize(input, errorOnFail);
		if (token == null) {
			return null;
		}
		return parse(token);
	}
	
	/**
	 * Parses the input string and returns the result
	 * @param input The string to parse
	 * @return The parsed object
	 */
	public Object parse(String input) {
		return parse(input, true);
	}
	
	private Object parse(Token token) {
		ParserComponent comp;
		try {
			comp = components[token.getType().getId()];
			comp.getName();
		} catch (ArrayIndexOutOfBoundsException | NullPointerException e) {
			throw new IllegalStateException("No parser component for token type " + token.getType().getName());
		}
		switch (comp.getType()) {
			case RAW_TOKEN:
				return comp.parse(token);
			case STRING_CONTENTS:
				return comp.parse(token.getValue());
			case CHILD_OBJECTS:
				Token[] children = token.getChildren();
				Object[] objs = new Object[children.length];
				for (int i = 0; i < objs.length; i++) {
					objs[i] = parse(children[i]);
				}
				return comp.parse(objs);
			default:
				return null;
		}
	}

}
