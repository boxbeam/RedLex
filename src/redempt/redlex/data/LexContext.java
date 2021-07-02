package redempt.redlex.data;

import redempt.redlex.token.ListToken;

import java.util.HashMap;
import java.util.Map;

public class LexContext {
	
	private int lastPos;
	private TokenType token;
	private Map<TokenType, Integer> map = new HashMap<>();
	
	public LexContext() {}
	
	public LexContext(int pos) {
		this.lastPos = pos;
	}
	
	public int getLastPos() {
		return lastPos;
	}
	
	public boolean update(int pos, TokenType type) {
		if (type.getName() == null) {
			return shouldRecurse(pos, type);
		}
		if (pos > lastPos) {
			lastPos = pos;
			token = type;
		}
		return shouldRecurse(pos, type);
	}
	
	private boolean shouldRecurse(int pos, TokenType type) {
		if (!(type instanceof ListToken)) {
			return false;
		}
		if (map.putIfAbsent(type, pos) == null) {
			return true;
		}
		int cur = map.get(type);
		if (pos > cur) {
			map.put(type, pos);
			return true;
		}
		return false;
	}
	
	public TokenType getLastToken() {
		return token;
	}
	
}
