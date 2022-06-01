package redempt.redlex.debug;

import redempt.redlex.data.TokenType;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Represents the entire history of a tokenizing process
 * @author Redempt
 */
public class DebugHistory {

	private int[][] breaks;
	private Deque<TokenType> stack = new ArrayDeque<>();
	private List<DebugEntry> entries = new ArrayList<>();

	public void setString(String str) {
		stack.clear();
		entries.clear();
		int line = 1;
		int col = 1;
		breaks = new int[str.length() + 1][];
		for (int i = 0; i < str.length(); i++) {
			breaks[i] = new int[] {line, col};
			col++;
			if (str.charAt(i) == '\n') {
				line++;
				col = 1;
			}
		}
		breaks[breaks.length - 1] = breaks[breaks.length - 2];
	}

	public void begin(TokenType type, int pos) {
		if (type.getName() == null) {
			return;
		}
		entries.add(new DebugEntry(type, breaks[pos][0], breaks[pos][1], 0, stack.size(), 0));
		stack.add(type);
	}

	public void result(TokenType type, int pos, int length, boolean success) {
		if (type.getName() == null) {
			return;
		}
		stack.removeLast();
		entries.add(new DebugEntry(type, breaks[pos][0], breaks[pos][1], length, stack.size(), success ? 2 : 1));
	}

	public DebugHistory filter(String... names) {
		Set<String> matches = new HashSet<>(Arrays.asList(names));
		entries.removeIf(e -> matches.contains(e.getOwner().getName()));
		return this;
	}

	public DebugHistory filter(Predicate<String> nameFilter) {
		entries.removeIf(e -> nameFilter.test(e.getOwner().getName()));
		return this;
	}

	public List<DebugEntry> getEntries() {
		return entries;
	}

	/**
	 * @return A formatted description of all tokenizing actions
	 */
	@Override
	public String toString() {
		return entries.stream().map(DebugEntry::toString).collect(Collectors.joining("\n"));
	}

}
