package redempt.redlex.processing;

public enum TraversalOrder {
	
	/**
	 * Traverse starting at the deepest node, from left to right leaf nodes in the tree followed by their parents
	 */
	DEPTH_LEAF_FIRST,
	/**
	 * Traverse starting at the root, followed by the entire left subtree, then the entire right subtree
	 */
	DEPTH_ROOT_FIRST,
	/**
	 * Traverse starting at the current node, from left to right internal nodes followed by their leaf nodes
	 */
	BREADTH_FIRST,
	/**
	 * Traverse only the children of the current node, from left to right
	 */
	SHALLOW
	
}
