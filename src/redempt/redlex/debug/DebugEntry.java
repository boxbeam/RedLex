package redempt.redlex.debug;

import redempt.redlex.data.TokenType;

/**
 * Represents one step in the tokenizing process
 * @author Redempt
 */
public class DebugEntry {

	private int status;
	private int row;
	private int col;
	private int depth;
	private int length;
	private TokenType owner;

	public DebugEntry(TokenType owner, int row, int col, int length, int depth, int status) {
		this.owner = owner;
		this.row = row;
		this.col = col;
		this.status = status;
		this.length = length;
		this.depth = depth;
	}

	/**
	 * @return The TokenType that performed this step
	 */
	public TokenType getOwner() {
		return owner;
	}

	/**
	 * @return The line number of the step
	 */
	public int getLine() {
		return row;
	}

	/**
	 * @return The column number of the step
	 */
	public int getCol() {
		return getCol();
	}

	public int getLength() {
		return length;
	}

	/**
	 * @return The status of the step - 0 for begin, 1 for failure, 2 for success
	 */
	public int getStatus() {
		return status;
	}

	/**
	 * @return The depth of this step in the token tree
	 */
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

	/**
	 * @return A String representation of this step
	 */
	@Override
	public String toString() {
		StringBuilder out = new StringBuilder();
		for (int i = 0; i < depth; i++) {
			out.append("  ");
		}
		out.append(owner.getName()).append(" ").append(getStatusString()).append(" at line ").append(row).append(", column ").append(col).append(status == 2 ? " (length " + length + ")" : "");
		return out.toString();
	}

}
