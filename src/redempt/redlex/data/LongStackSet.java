package redempt.redlex.data;

public class LongStackSet {

	private long[] stack = new long[100];
	private long[] set = new long[130];
	private int size;

	public boolean add(long value) {
		addToStack(value);
		boolean contained = addToSet(value, true);
		size++;
		return contained;
	}

	public long pop() {
		long last = stack[size - 1];
		size--;
		removeFromSet(last);
		return last;
	}

	public int size() {
		return size;
	}

	private void addToStack(long value) {
		if (size >= stack.length) {
			growStack();
		}
		stack[size] = value;
	}

	private void removeFromSet(long value) {
		int hash = (int) value % set.length;
		int index = hash;
		while (set[index] != value && set[index] != 0) {
			index = nextIndex(index);
		}
		if (set[index] == 0) {
			return;
		}
		set[index] = 0;
		index = nextIndex(index);
		while (set[index] != 0 && set[index] % set.length == hash) {
			set[previousIndex(index)] = set[index];
			set[index] = 0;
			index = nextIndex(index);
		}
	}

	public boolean contains(long value) {
		int hash = (int) value % set.length;
		while (set[hash] != 0 && set[hash] != value) {
			hash = nextIndex(hash);
		}
		return set[hash] == value;
	}

	private int previousIndex(int ind) {
		if (--ind < 0) {
			ind = set.length - 1;
		}
		return ind;
	}

	private int nextIndex(int ind) {
		if (++ind >= set.length) {
			ind = 0;
		}
		return ind;
	}

	private boolean addToSet(long value, boolean checkSize) {
		if (checkSize && (float) size / set.length > 0.7) {
			growSet();
		}
		int hash = (int) value % set.length;
		while (set[hash] != 0 && set[hash] != value) {
			hash = nextIndex(hash);
		}
		boolean contained = set[hash] == value;
		set[hash] = value;
		return !contained;
	}

	private void growSet() {
		long[] set = this.set;
		this.set = new long[this.set.length * 2];
		for (long num : set) {
			if (num != 0) {
				addToSet(num, false);
			}
		}
	}

	private void growStack() {
		long[] copy = new long[stack.length * 2];
		System.arraycopy(stack, 0, copy, 0, stack.length);
		stack = copy;
	}

}
