package redempt.redlex.bnf;

import redempt.redlex.data.TokenType;
import redempt.redlex.processing.Lexer;
import redempt.redlex.token.*;

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
	private static TokenType escapeSequence = new ListToken("escapeSequence", escape, anyChar);
	private static TokenType obrack = new StringToken("[", "[");
	private static TokenType oparen = new StringToken(null, "(");
	private static TokenType caret = new StringToken("^", "^");
	private static TokenType optionalCaret = new OptionalToken("^", caret);
	private static TokenType cbrack = new StringToken("]", "]");
	private static TokenType cparen = new StringToken(null, ")");
	private static TokenType whitespace = new CharSetToken("whitespace", ' ', '\t', '\r');
	private static TokenType newline = new StringToken("whitespace", "\n");
	private static TokenType newlineRep = new RepeatingToken("whitespace", newline);
	private static TokenType sep = new RepeatingToken("whitespace", whitespace);
	private static TokenType opsep = new OptionalToken("whitespace", sep);
	private static TokenType lowerAlpha = new CharGroupToken(null, 'a', 'z');
	private static TokenType upperAlpha = new CharGroupToken(null, 'A', 'Z');
	private static TokenType digit = new CharGroupToken(null, '0', '9');
	private static TokenType underscore = new StringToken("_", "_");
	private static TokenType validChar = new ChoiceToken("validChar", lowerAlpha, upperAlpha, digit, underscore);
	private static TokenType word = new RepeatingToken("word", validChar);
	private static TokenType equals = new StringToken("::=", "::=");
	private static TokenType eof = new EndOfFileToken("<eof>");
	private static TokenType eofToken = new StringToken("eof", "<eof>");
	private static TokenType modifier = new CharSetToken("modifier", '+', '?', '*');
	private static TokenType modifierToken = new OptionalToken("modifiers", modifier);
	private static TokenType notToken = new StringToken("!", "!");
	private static TokenType notOpt = new OptionalToken("!", notToken);
	private static TokenType token = tokenType();
	private static TokenType statement = statementType();
	private static TokenType sentence = new ListToken("sentence", opsep, word, sep, equals, sep, statement);
	private static TokenType comment = commentType();
	private static TokenType root = rootType();
	
	private static TokenType stringType() {
		TokenType notQuote = new CharGroupToken("notQuote", '"', '"', true);
		TokenType stringChar = new ChoiceToken("strChar", escapeSequence, notQuote);
		TokenType stringRep = new RepeatingToken("strRep", stringChar);
		TokenType stringOpt = new OptionalToken("strOpt", stringRep);
		TokenType quote = new StringToken("quote", "\"");
		TokenType string = new ListToken("string", quote, stringOpt, quote);
		return string;
	}
	
	private static TokenType commentType() {
		TokenType notNewline = new CharSetToken("notNewline", true, '\n');
		TokenType notNewlineRep = new RepeatingToken("comment", notNewline);
		TokenType notNewlineOpt = new OptionalToken("comment", notNewlineRep);
		TokenType comment = new ListToken("comment", opsep, commentPrefix, notNewlineOpt);
		return comment;
	}
	
	private static TokenType charSetType() {
		TokenType notBracket = new CharSetToken("notBracket", true, '[', ']');
		TokenType setChar = new ChoiceToken("setChar", escapeSequence, notBracket);
		TokenType setRep = new RepeatingToken("setRep", setChar);
		TokenType setOpt = new OptionalToken("setOpt", setRep);
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
		TokenType restRep = new RepeatingToken("statementRest", restList);
		TokenType restOpt = new OptionalToken("statementOpt", restRep);
		TokenType tokenOrNestedStatement = new ChoiceToken("tokenOrNested", token, nested);
		TokenType statement = new ListToken("statement", tokenOrNestedStatement, restOpt);
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
		return sentenceRep;
	}
	
}
