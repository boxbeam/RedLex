package redempt.redlex.debug;

import redempt.redlex.data.TokenType;

public class DebugEntry {

	private int status;
	private int row;
	private int col;
	private int depth;
	private TokenType owner;

	public DebugEntry(TokenType owner, int row, int col, int depth, int status) {
		this.owner = owner;
		this.row = row;
		this.col = col;
		this.status = status;
		this.depth = depth;
	}

	public TokenType getOwner() {
		return owner;
	}

	public int getRow() {
		return row;
	}

	public int getCol() {
		return getCol();
	}

	public int getStatus() {
		return status;
	}

	public int getDepth() {
		return depth;
	}

	private String getStatusString() {
		switch (status) {
			case 0:
				return "began tokenize";
			case 1:
				return "failed to tokenize";
			case 2:
				return "successfully tokenized";
			default:
				return null;
		}
	}

	@Override
	public String toString() {
		StringBuilder out = new StringBuilder();
		for (int i = 0; i < depth; i++) {
			out.append("  ");
		}
		out.append(owner.getName()).append(" ").append(getStatusString()).append(" at line ").append(row).append(", column ").append(col);
		return out.toString();
	}

}
