package redempt.redlex.data;

public class LexContext {
	
	private int lastPos;
	private TokenType token;
	private LongStackSet stackSet = new LongStackSet();
	
	public LexContext() {}
	
	public LexContext(int pos) {
		this.lastPos = pos;
	}
	
	public int getLastPos() {
		return lastPos;
	}
	
	public boolean update(int pos, TokenType type) {
		if (type.getName() != null && pos > lastPos) {
			lastPos = pos;
			token = type;
		}
		long id = ((long) pos << 32) + type.getId();
		return stackSet.add(id);
	}

	public void pop() {
		stackSet.pop();
	}
	
	public TokenType getLastToken() {
		return token;
	}
	
}
