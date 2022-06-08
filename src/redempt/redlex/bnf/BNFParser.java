package redempt.redlex.bnf;

import redempt.redlex.data.Token;
import redempt.redlex.data.TokenType;
import redempt.redlex.exception.BNFException;
import redempt.redlex.processing.CullStrategy;
import redempt.redlex.processing.Lexer;
import redempt.redlex.processing.TokenFilter;
import redempt.redlex.processing.TraversalOrder;
import redempt.redlex.token.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringJoiner;
import java.util.stream.Collectors;

/**
 * A parser used to create lexers from BNF files
 */
public class BNFParser {
	
	private static Lexer lexer;
	
	static {
		lexer = BNFLexer.getLexer();
		lexer.setUnnamedRule(CullStrategy.DELETE_ALL);
		lexer.setRetainEmpty(false);
		lexer.setRuleByName(CullStrategy.DELETE_ALL, "whitespace", "::=", "comment", "validChar");
		lexer.setRuleByName(CullStrategy.LIFT_CHILDREN, "modifiers", "statementList", "tokenOrNested",
				"tokenOrStatement", "tokenBase", "sentencesRep", "separator", "modifierChoice", "strChar");
	}
	
	/**
	 * Parses the input String and returns a Lexer
	 *
	 * @param input The input String defining the format for the Lexer
	 * @return A Lexer for the given format
	 */
	public static Lexer createLexer(String input) {
		return new Lexer(parse(input));
	}
	
	/**
	 * Parses the input String and returns a Lexer
	 *
	 * @param path The path to a file containing the format for the Lexer
	 * @return A Lexer for the given format
	 */
	public static Lexer createLexer(Path path) {
		try {
			String contents = Files.lines(path).collect(Collectors.joining("\n"));
			return createLexer(contents);
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * Parses the input String and returns a Lexer
	 *
	 * @param stream The InputStream the bnf contents can be read from
	 * @return A Lexer for the given format
	 */
	public static Lexer createLexer(InputStream stream) {
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
			String line = "";
			StringJoiner joiner = new StringJoiner("\n");
			while ((line = reader.readLine()) != null) {
				joiner.add(line);
			}
			return createLexer(joiner.toString());
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	private static TokenType parse(String input) {
		Token token = lexer.tokenize(input);
		Map<String, List<Token>> map = token.allByNames(TraversalOrder.DEPTH_LEAF_FIRST,
				"escapeSequence", "statementOpt", "token", "sentence", "nested");
		for (Token escape : map.get("escapeSequence")) {
			String value = escape.getValue();
			escape.setChildren(Token.EMPTY);
			switch (value.charAt(1)) {
				case 'n':
					escape.setValue("\n");
					break;
				case 'r':
					escape.setValue("\r");
					break;
				case 't':
					escape.setValue("\t");
					break;
				case 'u':
					int num = Integer.parseInt(value.substring(2), 16);
					escape.setValue((char) num + "");
					break;
				default:
					escape.setValue(value.charAt(1) + "");
					break;
			}
		}
		for (Token statementOpt : map.get("statementOpt")) {
			statementOpt.liftChildren();
		}
		Map<String, TokenType> tokens = new HashMap<>();
		for (Token t : map.get("token")) {
			TokenType type = createToken(t);
			t.replaceWith(type);
		}
		for (Token t : map.get("sentence")) {
			String name = t.firstByName("word").getValue();
			TokenType type = processSentence(t);
			if (!(type instanceof PlaceholderToken)) {
				type.setName(name);
			}
			tokens.put(name, type);
		}
		TokenType root = tokens.get("root");
		if (root == null) {
			throw new BNFException("No root node specified");
		}
		Set<String> used = new HashSet<>();
		while (root instanceof WrapperToken) {
			TokenType wrapped = ((WrapperToken) root).getChildren()[0];
			if (!tokens.containsKey(wrapped.getName()) || !used.add(wrapped.getName())) {
				throw new BNFException("Circular reference or undefined token: " + String.join(", ", wrapped.getName()));
			}
			root = tokens.get(wrapped.getName());
		}
		root.replacePlaceholders(tokens);
		return root;
	}
	
	private static TokenType createToken(Token input) {
		Token[] children = input.getChildren();
		boolean not = children[0].getType().getName().equals("!");
		Token token = children[not ? 1 : 0];
		TokenType type = null;
		switch (token.getType().getName()) {
			case "string":
				type = createString(token.firstByName("strOpt"), token.firstByName("insensitive") == null);
				break;
			case "charset":
				type = createCharset(token);
				break;
			case "chargroup":
				type = createCharGroup(token);
				break;
			case "word":
				type = createTokenReference(token);
				break;
			case "eof":
				type = new EndOfFileToken(null);
				break;
		}
		Token modifier = input.firstByName("modifier");
		type = processModifier(type, modifier);
		if (not) {
			type = new NotToken(null, type);
		}
		return type;
	}
	
	private static TokenType processModifier(TokenType type, Token modifier) {
		if (modifier == null) {
			return type;
		}
		switch (modifier.getValue().charAt(0)) {
			case '+':
				type = new RepeatingToken(null, type);
				break;
			case '*':
				type = new RepeatingToken(null, type, 0, Integer.MAX_VALUE);
				break;
			case '?':
				type = new RepeatingToken(null, type, 0, 1);
				break;
			case '{':
				int[] times = parseQuantifier(modifier.getValue());
				type = new RepeatingToken(null, type, times[0], times[1]);
				break;
		}
		return type;
	}
	
	private static int[] parseQuantifier(String quantifier) {
		int commaIndex = quantifier.indexOf(',');
		if (commaIndex == -1) {
			int num = Integer.parseInt(quantifier.substring(1, quantifier.length() - 1));
			return new int[] {num, num};
		}
		String before = quantifier.substring(1, commaIndex);
		String after = quantifier.substring(commaIndex + 1, quantifier.length() - 1);
		int min = before.length() == 0 ? 0 : Integer.parseInt(before);
		int max = after.length() == 0 ? Integer.MAX_VALUE : Integer.parseInt(after);
		return new int[] {min, max};
	}
	
	private static TokenType processSentence(Token sentence) {
		for (Token t : sentence.allByName(TraversalOrder.DEPTH_LEAF_FIRST, "nested")) {
			Token statement = t.firstByName("statement");
			TokenType token = createStatement(statement);
			Token mod = t.firstByName("modifier");
			if (mod != null) {
				token = processModifier(token, mod);
			}
			if (t.firstByName("!") != null) {
				token = new NotToken(null, token);
			}
			t.replaceWith(token);
		}
		TokenType token = createStatement(sentence.firstByName("statement"));
		if (token instanceof PlaceholderToken) {
			token = new WrapperToken(null, token);
		}
		return token;
	}
	
	private static TokenType createStatement(Token statement) {
		statement.cull(TokenFilter.byName(CullStrategy.LIFT_CHILDREN, "statement"));
		List<List<Token>> split = statement.splitChildren("|");
		List<TokenType> merged = new ArrayList<>();
		for (List<Token> list : split) {
			if (list.size() == 1) {
				merged.add((TokenType) list.get(0).getObject());
				continue;
			}
			TokenType[] arr = new TokenType[list.size()];
			for (int i = 0; i < list.size(); i++) {
				arr[i] = (TokenType) list.get(i).getObject();
			}
			merged.add(new ListToken(null, arr));
		}
		if (merged.size() == 1) {
			return merged.get(0);
		}
		if (merged.size() >= 3 && merged.stream().allMatch(t -> t instanceof StringToken && ((StringToken) t).isCaseSensitive())) {
			return new StringChoiceToken(null, merged.stream().map(StringToken.class::cast).map(StringToken::getString).toArray(String[]::new));
		}
		return new ChoiceToken(null, merged.toArray(new TokenType[merged.size()]));
	}
	
	private static TokenType createString(Token strOpt, boolean caseSensitive) {
		if (strOpt == null) {
			return new StringToken(null, "");
		}
		String val = strOpt.joinChildren("");
		return new StringToken("'" + val, val, caseSensitive);
	}
	
	private static TokenType createTokenReference(Token t) {
		return new PlaceholderToken(t.getValue());
	}
	
	private static TokenType createCharset(Token token) {
		Token caret = token.firstByName("^");
		if (caret != null) {
			caret.remove();
		}
		Token setOpt = token.firstByName("setOpt");
		if (setOpt == null) {
			return new CharGroupToken(null, -1, -1, caret != null);
		}
		String str = setOpt.joinLeaves("");
		if (str.length() == 1) {
			char c = str.charAt(0);
			return new CharGroupToken(null, c, c, caret != null);
		}
		return new CharSetToken(null, caret != null, str.toCharArray());
	}
	
	private static TokenType createCharGroup(Token charGroup) {
		Token caret = charGroup.firstByName("^");
		if (caret != null) {
			caret.remove();
		}
		String set = charGroup.joinLeaves("");
		return new CharGroupToken(null, set.charAt(1), set.charAt(3), caret != null);
	}
	
}
