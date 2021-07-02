package redempt.redlex.processing;

/**
 * An enum of actions that can be taken by Cullers to act on a TokenInstance
 */
public enum CullStrategy {
	
	/**
	 * Delete this node, and lift its children into the parent node
	 */
	LIFT_CHILDREN(1),
	/**
	 * Delete this node and its children
	 */
	DELETE_ALL(3),
	/**
	 * Delete the children of this node and change its status
	 */
	DELETE_CHILDREN(2),
	/**
	 * Leave this node as is
	 */
	IGNORE(0);
	
	private int priority;
	
	private CullStrategy(int priority) {
		this.priority = priority;
	}
	
	public int getPriority() {
		return priority;
	}
	
}
