package redempt.redlex.data;

import redempt.redlex.processing.CullStrategy;
import redempt.redlex.processing.Lexer;
import redempt.redlex.processing.ObjectToken;
import redempt.redlex.processing.ArrayUtils;
import redempt.redlex.processing.TokenFilter;
import redempt.redlex.processing.TraversalOrder;

import java.util.*;
import java.util.function.Consumer;

/**
 * Represents a Token created by a Lexer. Acts as a node in a tree.
 */
public class Token {
	
	public static Token[] EMPTY = new Token[0];
	
	private Token parent;
	private int index;
	private TokenType type;
	protected String value;
	private String sub;
	private Token[] children;
	private int start;
	private int end;
	
	public Token(TokenType type, String value, int start, int end, Token[] children) {
		this.type = type;
		this.value = value;
		this.start = start;
		this.end = end;
		setChildren(processChildren(children));
	}
	
	private Token[] processChildren(Token[] children) {
		Lexer lexer = type.getLexer();
		if (lexer == null || children == null) {
			return children;
		}
		List<Token> list = new ArrayList<>();
		for (Token child : children) {
			switch (lexer.getStrategy(child)) {
				case DELETE_ALL:
					break;
				case IGNORE:
					list.add(child);
					break;
				case DELETE_CHILDREN:
					child.setChildren(null);
					list.add(child);
					break;
				case LIFT_CHILDREN:
					Collections.addAll(list, child.getChildren());
					break;
			}
		}
		return list.toArray(Token.EMPTY);
	}
	
	public Token(TokenType type, String value, int start, int end) {
		this(type, value, start, end, null);
	}
	
	/**
	 * Sets the children of this Token
	 * @param children The children of this Token
	 */
	public void setChildren(Token[] children) {
		if (children == null) {
			this.children = EMPTY;
			return;
		}
		this.children = children;
		for (int i = 0; i < children.length; i++) {
			Token child = children[i];
			child.index = i;
			child.parent = this;
		}
	}
	
	/**
	 * @return The children of this Token
	 */
	public Token[] getChildren() {
		return children;
	}
	
	/**
	 * @return The next sibling of this Token, or null if none exists
	 */
	public Token getNext() {
		if (parent == null) {
			return null;
		}
		Token[] siblings = parent.getChildren();
		if (index > siblings.length - 2) {
			return null;
		}
		return siblings[index + 1];
	}
	
	/**
	 * @return The previous sibling of this Token, or null if none exists
	 */
	public Token getPrevious() {
		if (parent == null) {
			return null;
		}
		Token[] siblings = parent.getChildren();
		if (index < 1) {
			return null;
		}
		return siblings[index - 1];
	}
	
	/**
	 * @return The parent of this Token, or null if none exists
	 */
	public Token getParent() {
		return parent;
	}
	
	/**
	 * @return The index of this Token in the array of its parent's children
	 */
	public int getIndex() {
		return index;
	}
	
	/**
	 * @return The type of this Token
	 */
	public TokenType getType() {
		return type;
	}
	
	/**
	 * @return The String value of this Token
	 */
	public String getValue() {
		if (sub == null) {
			sub = value.substring(start, end);
		}
		return sub;
	}
	
	/**
	 * @return The base String this token was parsed from
	 */
	public String getBaseString() {
		return value;
	}
	
	/**
	 * @return The length of this Token's String value
	 */
	public int length() {
		return end - start;
	}
	
	/**
	 * @return The ending index of this Token
	 */
	public int getEnd() {
		return end;
	}
	
	/**
	 * @return The starting index of this Token
	 */
	public int getStart() {
		return start;
	}
	
	/**
	 * Sets the String value of this Token
	 * @param value The String value to set
	 */
	public void setValue(String value) {
		this.sub = value;
	}
	
	/**
	 * Joins the String values of the children of this node separated by a delimiter
	 * @param sep The delimiter to separate values with
	 * @return The joined string
	 */
	public String joinChildren(String sep) {
		StringJoiner out = new StringJoiner(sep);
		for (Token child : children) {
			out.add(child.getValue());
		}
		return out.toString();
	}
	
	/**
	 * Joins the leaf nodes of this subtree separated by a delimiter
	 * @param sep The delimiter to separate values with
	 * @return The joined string
	 */
	public String joinLeaves(String sep) {
		if (children.length > 0) {
			StringJoiner out = new StringJoiner(sep);
			for (Token child : children) {
				out.add(child.joinLeaves(sep));
			}
			return out.toString();
		}
		return getValue();
	}
	
	/**
	 * Removes this token from its parent
	 */
	public void remove() {
		if (parent == null) {
			throw new IllegalStateException("Token has no parent");
		}
		Token[] children = ArrayUtils.remove(parent.children, index, Token[]::new);
		parent.setChildren(children);
	}
	
	/**
	 * Removes the given children from this token
	 * @param children The children to remove
	 */
	public void removeChildren(Token... children) {
		setChildren(ArrayUtils.remove(this.children, Token[]::new, children));
	}
	
	/**
	 * Removes a range of children from this token by index
	 * @param start The starting index to remove children from, inclusive
	 * @param end The ending index to remove children to, exclusive
	 */
	public void removeChildren(int start, int end) {
		setChildren(ArrayUtils.removeRange(this.children, start, end, Token[]::new));
	}
	
	/**
	 * Replaces this token in its parent with a token wrapper for the given object, or a token if the passed object is a token
	 * @param o The object to replace this token with
	 * @return The Token this one was replaced with
	 */
	public Token replaceWith(Object o) {
		if (parent == null) {
			throw new IllegalStateException("Token has no parent");
		}
		Token[] children = parent.children;
		Token toReplace = o instanceof Token ? (Token) o : new ObjectToken(this, o);
		toReplace.index = index;
		toReplace.parent = parent;
		children[index] = toReplace;
		return toReplace;
	}
	
	/**
	 * Replaces this token's parent with itself
	 */
	public void replaceParent() {
		if (parent == null || parent.parent == null) {
			throw new IllegalStateException("Node does not have a grandparent");
		}
		parent.parent.children[parent.index] = this;
		this.index = parent.index;
		this.parent = parent.parent;
	}
	
	/**
	 * Removes this token from its parent, replacing it with its children
	 */
	public void liftChildren() {
		if (parent == null) {
			throw new IllegalStateException("Token has no parent");
		}
		Token[] children = parent.children;
		Token[] newChildren = ArrayUtils.replaceRange(children, this.children, this.index, this.index + 1, Token[]::new);
		parent.setChildren(newChildren);
	}
	
	/**
	 * Find the first token in the tree by name (breadth-first)
	 * @param name The name of the token to search for
	 * @return The first token found, or null
	 */
	public Token firstByName(String name) {
		Queue<Token> queue = new ArrayDeque<>();
		queue.add(this);
		while (queue.size() > 0) {
			Token child = queue.poll();
			if (child.getType().nameMatches(name)) {
				return child;
			}
			Collections.addAll(queue, child.getChildren());
		}
		return null;
	}
	
	/**
	 * Find all tokens in the tree by name
	 * @param order The order to traverse the tree in
	 * @param name The name of the token to search for
	 * @return All tokens found
	 */
	public List<Token> allByName(TraversalOrder order, String name) {
		List<Token> matching = new ArrayList<>();
		walk(order, child -> {
			if (child.getType().nameMatches(name)) {
				matching.add(child);
			}
		});
		return matching;
	}
	
	/**
	 * Find all tokens in the tree by name, breadth-first
	 * @param name The name of the token to search for
	 * @return All tokens found
	 */
	public List<Token> allByName(String name) {
		return allByName(TraversalOrder.BREADTH_FIRST, name);
	}
	
	/**
	 * Find all tokens in the tree by any number of names and create a map, breadth-first
	 * @param names The names to look for
	 * @return A map of names to the tokens with those names
	 */
	public Map<String, List<Token>> allByNames(String... names) {
		return allByNames(TraversalOrder.BREADTH_FIRST, names);
	}
	
	/**
	 * Find all tokens in the tree by any number of names and create a map
	 * @param order The order to traverse the tree in
	 * @param names The names to look for
	 * @return A map of names to the tokens with those names
	 */
	public Map<String, List<Token>> allByNames(TraversalOrder order, String... names) {
		Map<String, List<Token>> map = new HashMap<>();
		for (String name : names) {
			map.put(name, new ArrayList<>());
		}
		walk(order, child -> {
			List<Token> list = map.get(child.getType().getName());
			if (list != null) {
				list.add(child);
			}
		});
		return map;
	}
	
	/**
	 * Walks this Token tree in the given order
	 * @param order The order to walk the tree in
	 * @param forEachNode The lambda to apply to each token
	 */
	public void walk(TraversalOrder order, Consumer<Token> forEachNode) {
		switch (order) {
			case SHALLOW:
				for (Token child : children) {
					forEachNode.accept(child);
				}
				break;
			case BREADTH_FIRST:
				Queue<Token> queue = new ArrayDeque<>();
				queue.add(this);
				while (!queue.isEmpty()) {
					Token next = queue.poll();
					forEachNode.accept(next);
					Collections.addAll(queue, next.getChildren());
				}
				break;
			case DEPTH_ROOT_FIRST:
				Stack<Token> stack = new Stack<>();
				stack.add(this);
				while (!stack.isEmpty()) {
					Token next = stack.pop();
					forEachNode.accept(next);
					Collections.addAll(stack, next.getChildren());
				}
				break;
			case DEPTH_LEAF_FIRST:
				for (Token child : children) {
					child.walk(TraversalOrder.DEPTH_LEAF_FIRST, forEachNode);
				}
				forEachNode.accept(this);
				break;
		}
	}
	
	/**
	 * @return Nothing, unless this is an ObjectToken
	 */
	public Object getObject() {
		return null;
	}
	
	/**
	 * Splits child tokens on a given token by name
	 * @param name The name of the token to split on
	 * @return A 2d list of tokens split by the given token
	 */
	public List<List<Token>> splitChildren(String name) {
		List<List<Token>> split = new ArrayList<>();
		List<Token> current = new ArrayList<>();
		for (Token token : children) {
			if (token.getType().nameMatches(name)) {
				split.add(current);
				current = new ArrayList<>();
				continue;
			}
			current.add(token);
		}
		if (current.size() > 0) {
			split.add(current);
		}
		return split;
	}
	
	/**
	 * Culls tokens in the tokens which are descendants of this one using filters.
	 * @param filters The filters to cull tokens with
	 */
	public void cull(TokenFilter... filters) {
		List<Token> list = new ArrayList<>();
		for (Token child : children) {
			CullStrategy strategy = CullStrategy.IGNORE;
			for (TokenFilter filter : filters) {
				CullStrategy action = filter.test(child);
				strategy = action.getPriority() > strategy.getPriority() ? action : strategy;
			}
			switch (strategy) {
				case IGNORE:
					list.add(child);
					break;
				case DELETE_ALL:
					break;
				case DELETE_CHILDREN:
					child.setChildren(new Token[] {});
					list.add(child);
					break;
				case LIFT_CHILDREN:
					child.cull(filters);
					Collections.addAll(list, child.getChildren());
					break;
			}
		}
		
		setChildren(list.toArray(Token.EMPTY));
		for (Token child : children) {
			child.cull(filters);
		}
	}
	
	/**
	 * Appends the given tokens to this token's children
	 * @param tokens The tokens to add
	 */
	public void addChildren(Token... tokens) {
		setChildren(ArrayUtils.concat(this.children, tokens, Token[]::new));
	}
	
	@Override
	public String toString() {
		if (children.length == 0) {
			return type.getName() + " [" + getValue() + "]";
		}
		String name = type.getName();
		name = name == null ? "(unnamed)" : name;
		StringBuilder builder = new StringBuilder(name).append(" {");
		for (int i = 0; i < children.length; i++) {
			builder.append(children[i].toString());
			if (i != children.length - 1) {
				builder.append(", ");
			}
		}
		builder.append("}");
		return builder.toString();
	}
	
}
