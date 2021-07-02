package redempt.redlex.processing;

import redempt.redlex.data.Token;

import java.util.function.Consumer;
import java.util.function.Predicate;

class TokenTransformer {
	
	private Predicate<Token> filter;
	private Consumer<Token> transformer;
	
	public TokenTransformer(Predicate<Token> filter,Consumer<Token> transformer) {
		this.filter = filter;
		this.transformer = transformer;
	}
	
	public boolean test(Token token) {
		return filter.test(token);
	}
	
	public void transform(Token token) {
		transformer.accept(token);
	}
	
}
