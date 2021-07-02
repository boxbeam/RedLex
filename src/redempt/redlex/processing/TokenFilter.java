package redempt.redlex.processing;

import redempt.redlex.data.ParentToken;
import redempt.redlex.data.Token;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Filters Tokens to remove them from a tree
 */
public interface TokenFilter {
	
	/**
	 * @return A token filter that removes all empty tokens
	 */
	public static TokenFilter removeEmpty() {
		return t -> t.length() == 0 ? CullStrategy.DELETE_ALL : CullStrategy.IGNORE;
	}
	
	/**
	 * @return A token filter that removes all tokens which are only containers, lifting their children out to flatten the tree a bit
	 */
	public static TokenFilter flatten() {
		return t -> t.getType() instanceof ParentToken ? CullStrategy.LIFT_CHILDREN : CullStrategy.IGNORE;
	}
	
	/**
	 * @return A token filter that removes all string literal tokens
	 */
	public static TokenFilter removeStringLiterals() {
		return t -> t.getType().getName() != null && t.getType().getName().startsWith("'") ? CullStrategy.DELETE_ALL : CullStrategy.IGNORE;
	}
	
	/**
	 * @param strategy The strategy to use to delete all tokens matching one of the given names
	 * @param names The token names to process
	 * @return A token filter to delete tokens with the given names
	 */
	public static TokenFilter byName(CullStrategy strategy, String... names) {
		Set<String> set = new HashSet<>();
		Collections.addAll(set, names);
		return t -> set.contains(t.getType().getName()) ? strategy : CullStrategy.IGNORE;
	}
	
	/**
	 * @param strategy The strategy to use to delete all tokens which are unnamed
	 * @return A token filter to delete all unnamed tokens
	 */
	public static TokenFilter removeUnnamed(CullStrategy strategy) {
		return t -> t.getType().getName() == null ? strategy : CullStrategy.IGNORE;
	}
	
	public CullStrategy test(Token token);
	
}
