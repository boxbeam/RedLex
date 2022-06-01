package redempt.redlex.bnf;

import redempt.redlex.data.TokenType;
import redempt.redlex.processing.Lexer;
import redempt.redlex.token.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

//pain
class BNFLexer {
	
	public static Lexer getLexer() {
		return new Lexer(rootType());
	}
	
	private static TokenType commentPrefix = new StringToken("#", "#");
	private static TokenType escape = new StringToken("escape", "\\");
	private static TokenType anyChar = new CharGroupToken("anyChar", (char) 0, (char) 0, true);
	private static TokenType hexChar = new CharSetToken("hexChar", "0123456789abcdefABCDEF".toCharArray());
	private static TokenType unicodeSequence = new ListToken("unicodeEscape", new StringToken(null, "u"), new RepeatingToken("hexQuad", hexChar, 4, 4));
	private static TokenType escapeSequence = new ListToken("escapeSequence", escape, new ChoiceToken("escapeChoice", unicodeSequence, anyChar));
	private static TokenType obrack = new StringToken("[", "[");
	private static TokenType oparen = new StringToken(null, "(");
	private static TokenType caret = new StringToken("^", "^");
	private static TokenType optionalCaret = new RepeatingToken("^", caret, 0, 1);
	private static TokenType cbrack = new StringToken("]", "]");
	private static TokenType cparen = new StringToken(null, ")");
	private static TokenType whitespace = new CharSetToken("whitespace", ' ', '\t', '\r');
	private static TokenType newline = new StringToken("whitespace", "\n");
	private static TokenType newlineRep = new RepeatingToken("whitespace", newline);
	private static TokenType sep = new RepeatingToken("whitespace", whitespace);
	private static TokenType opsep = new RepeatingToken("whitespace", whitespace, 0, Integer.MAX_VALUE);
	private static TokenType lowerAlpha = new CharGroupToken(null, 'a', 'z');
	private static TokenType upperAlpha = new CharGroupToken(null, 'A', 'Z');
	private static TokenType digit = new CharGroupToken(null, '0', '9');
	private static TokenType number = new RepeatingToken("number", digit);
	private static TokenType underscore = new StringToken("_", "_");
	private static TokenType validChar = new ChoiceToken("validChar", lowerAlpha, upperAlpha, digit, underscore);
	private static TokenType word = new RepeatingToken("word", validChar);
	private static TokenType equals = new StringToken("::=", "::=");
	private static TokenType eofToken = new StringToken("eof", "<eof>");
	private static TokenType quantifier = quantifierType();
	private static TokenType basicModifier = new CharSetToken("modifier", '+', '?', '*');
	private static TokenType modifier = new ChoiceToken("modifierChoice", quantifier, basicModifier);
	private static TokenType modifierToken = new RepeatingToken("modifiers", modifier, 0, 1);
	private static TokenType notToken = new StringToken("!", "!");
	private static TokenType notOpt = new RepeatingToken("!", notToken, 0, 1);
	private static TokenType token = tokenType();
	private static TokenType statement = statementType();
	private static TokenType sentence = new ListToken("sentence", opsep, word, sep, equals, sep, statement);
	private static TokenType comment = commentType();
	
	private static TokenType stringType() {
		TokenType optI = new RepeatingToken("insensitive", new StringToken(null, "i"), 0, 1);
		TokenType notQuote = new CharGroupToken("notQuote", '"', '"', true);
		TokenType stringChar = new ChoiceToken("strChar", escapeSequence, notQuote);
		TokenType stringRep = new RepeatingToken("strOpt", stringChar, 0, Integer.MAX_VALUE);
		TokenType quote = new StringToken("quote", "\"");
		TokenType string = new ListToken("string", optI, quote, stringRep, quote);
		return string;
	}
	
	private static TokenType commentType() {
		TokenType notNewline = new CharSetToken("notNewline", true, '\n');
		TokenType notNewlineRep = new RepeatingToken("comment", notNewline, 0, Integer.MAX_VALUE);
		TokenType comment = new ListToken("comment", opsep, commentPrefix, notNewlineRep);
		return comment;
	}
	
	private static TokenType charSetType() {
		TokenType notBracket = new CharSetToken("notBracket", true, '[', ']');
		TokenType setChar = new ChoiceToken("setChar", escapeSequence, notBracket);
		TokenType setOpt = new RepeatingToken("setOpt", setChar, 0, Integer.MAX_VALUE);
		TokenType charSet = new ListToken("charset", obrack, optionalCaret, setOpt, cbrack);
		return charSet;
	}
	
	private static TokenType charGroupType() {
		TokenType notBracket = new CharSetToken("notBracket", true, '[', ']');
		TokenType setChar = new ChoiceToken("setChar", escapeSequence, notBracket);
		TokenType dash = new StringToken("-", "-");
		TokenType charSet = new ListToken("chargroup", obrack, optionalCaret, setChar, dash, setChar, cbrack);
		return charSet;
	}
	
	private static TokenType quantifierType() {
		TokenType ocbrack = new StringToken("{", "{");
		TokenType ccbrack = new StringToken("}", "}");
		TokenType comma = new StringToken(",", ",");
		TokenType commaOpt = new RepeatingToken(",", comma, 0, 1);
		TokenType numberOpt = new RepeatingToken("number", number, 0, 1);
		TokenType quantifier = new ListToken("modifier", ocbrack, opsep, numberOpt, opsep, commaOpt, opsep, numberOpt, opsep, ccbrack);
		return quantifier;
	}
	
	private static TokenType tokenType() {
		TokenType tokenBase = new ChoiceToken("tokenBase", stringType(), charGroupType(), charSetType(), word, eofToken);
		TokenType realToken = new ListToken("token", notOpt, tokenBase, modifierToken);
		return realToken;
	}
	
	private static TokenType statementType() {
		statement = new PlaceholderToken("statement");
		TokenType nested = new ListToken("nested", notOpt, oparen, statement, cparen, modifierToken);
		TokenType bar = new StringToken("|", "|");
		TokenType barSepOpt = new ListToken("|", opsep, bar, opsep);
		TokenType separatorChoice = new ChoiceToken("separator", barSepOpt, sep);
		TokenType tokenOrStatement = new ChoiceToken("tokenOrStatement", token, statement, nested);
		TokenType restList = new ListToken("statementList", separatorChoice, tokenOrStatement);
		TokenType restRep = new RepeatingToken("statementOpt", restList, 0, Integer.MAX_VALUE);
		TokenType tokenOrNestedStatement = new ChoiceToken("tokenOrNested", token, nested);
		TokenType statement = new ListToken("statement", tokenOrNestedStatement, restRep);
		Map<String, TokenType> map = new HashMap<>();
		map.put("statement", statement);
		statement.replacePlaceholders(map);
		return statement;
	}
	
	private static TokenType rootType() {
		TokenType sepOrEnd = new ChoiceToken("sep", newlineRep, new EndOfFileToken("eof"));
		TokenType line = new ChoiceToken("line", comment, sentence);
		TokenType sentenceList = new ListToken("sentences", line, sepOrEnd);
		TokenType sentenceRep = new RepeatingToken("sentencesRep", sentenceList);
		sentenceRep.replacePlaceholders(Collections.emptyMap());
		return sentenceRep;
	}
	
}
