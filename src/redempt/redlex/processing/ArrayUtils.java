package redempt.redlex.processing;

import java.util.function.Function;

public class ArrayUtils {
	
	/**
	 * Remove elements from an array
	 * @param arr The array to remove elements from
	 * @param arrayConstructor A function to create a new array of the given type with the given size
	 * @param toRemove The elements to remove, must all map to elements already in the array
	 * @param <T> The type of the array
	 * @return The array with elements removed
	 */
	public static <T> T[] remove(T[] arr, Function<Integer, T[]> arrayConstructor, T... toRemove) {
		int size = arr.length - toRemove.length;
		if (size < 0) {
			throw new IllegalArgumentException("Cannot remove more elements than array has");
		}
		T[] newArr = arrayConstructor.apply(size);
		int pos = 0;
		loop:
		for (int i = 0; i < arr.length && pos < newArr.length; i++) {
			for (T elem : toRemove) {
				if (elem.equals(arr[i])) {
					continue loop;
				}
			}
			newArr[pos] = arr[i];
			pos++;
		}
		if (pos != newArr.length) {
			throw new IllegalArgumentException("Duplicate elements or elements not present in the original array were passed");
		}
		return newArr;
	}
	
	/**
	 * Concatenates two arrays
	 * @param first The first array
	 * @param second The second array
	 * @param arrayConstructor A function to create a new array of the given type with the given size
	 * @param <T> The type of the array
	 * @return The concatenated array
	 */
	public static <T> T[] concat(T[] first, T[] second, Function<Integer, T[]> arrayConstructor) {
		int size = first.length + second.length;
		T[] arr = arrayConstructor.apply(size);
		System.arraycopy(first, 0, arr, 0, first.length);
		System.arraycopy(second, 0, arr, first.length, second.length);
		return arr;
	}
	
	/**
	 * Replaces a range of an array with the contents of another array
	 * @param first The array to replace the range in
	 * @param second The array to insert in place of the replaced range
	 * @param start The starting index to replace from in the first array, inclusive
	 * @param end The ending index to replace to in the first array, exclusive
	 * @param arrayConstructor A function to create a new array of the given type with the given size
	 * @param <T> The type of the array
	 * @return The resulting array
	 */
	public static <T> T[] replaceRange(T[] first, T[] second, int start, int end, Function<Integer, T[]> arrayConstructor) {
		int size = first.length - (end - start) + second.length;
		T[] newArr = arrayConstructor.apply(size);
		System.arraycopy(first, 0, newArr, 0, start);
		System.arraycopy(second, 0, newArr, start, second.length);
		int to = start + second.length;
		System.arraycopy(first, end, newArr, to, newArr.length - to);
		return newArr;
	}
	
	/**
	 * Creates a new array with a single element removed from the input array
	 * @param arr The array to remove the element from
	 * @param index The index of the element to remove
	 * @param arrayConstructor A function to create a new array of the given type with the given size
	 * @param <T> The type of the array
	 * @return The array with the element removed
	 */
	public static <T> T[] remove(T[] arr, int index, Function<Integer, T[]> arrayConstructor) {
		T[] newArr = arrayConstructor.apply(arr.length - 1);
		System.arraycopy(arr, 0, newArr, 0, index);
		System.arraycopy(arr, index + 1, newArr, index, newArr.length - index);
		return newArr;
	}
	
	/**
	 * Removes a range of elements from an array
	 * @param arr The array to remove elements from
	 * @param start The start index to remove elements from, inclusive
	 * @param end The end index to remove elements to, exclusive
	 * @param arrayConstructor A function to create a new array of the given type with the given size
	 * @param <T> The type of the array
	 * @return The array with the range removed
	 */
	public static <T> T[] removeRange(T[] arr, int start, int end, Function<Integer, T[]> arrayConstructor) {
		int size = arr.length - (end - start);
		T[] newArr = arrayConstructor.apply(size);
		System.arraycopy(arr, 0, newArr, 0, start);
		System.arraycopy(arr, end, newArr, start, newArr.length - start);
		return newArr;
	}
	
}
