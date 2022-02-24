package redempt.redlex.data;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class CharTree {
	
	private Node root = new Node();
	
	public void set(String str) {
		Node node = root;
		for (int i = 0; i < str.length(); i++) {
			node = node.addChild(str.charAt(i));
		}
		node.setMapped();
	}
	
	public int findForward(String str, int start) {
		Node node = root;
		int max = -1;
		int i = start;
		while (node != null && i < str.length()) {
			if (node.isMapped()) {
				max = i;
			}
			node = node.getChild(str.charAt(i));
			i++;
		}
		if (node != null && node.isMapped()) {
			return i;
		}
		return max;
	}

	public Set<Character> getFirstChars() {
		return root.children.keySet();
	}

	private static class Node {
		
		public Map<Character, Node> children;
		private boolean mapped = false;
		
		public Node getChild(char c) {
			if (children == null) {
				return null;
			}
			return children.get(c);
		}
		
		public void setMapped() {
			mapped = true;
		}
		
		public boolean isMapped() {
			return mapped;
		}
		
		public Node addChild(char c) {
			if (children == null) {
				children = new HashMap<>();
			}
			return children.computeIfAbsent(c, k -> new Node());
		}
		
	}
	
}
