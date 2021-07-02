package redempt.redlex.processing;

import redempt.redlex.data.Token;

public class ObjectToken extends Token {
	
	private Token token;
	private Object obj;
	
	public ObjectToken(Token token, Object value) {
		super(token.getType(), token.getBaseString(), token.getStart(), token.getEnd());
		this.obj = value;
		this.token = token;
	}
	
	@Override
	public Object getObject() {
		return obj;
	}
	
	public Token getWrappedToken() {
		return token;
	}
	
}
