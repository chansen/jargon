package warrenfalk.util;

import java.util.EmptyStackException;

public class IntegerList {
	int[] buffer;
	int size;
	
	public final static int[] EMPTY = new int[0];
	
	public IntegerList() {
		this(10);
	}
	
	public IntegerList(int capacity) {
		buffer = new int[capacity];
	}
	
	public int get(int index) {
		return buffer[index];
	}
	
	public void set(int index, int value) {
		buffer[index] = value;
	}
	
	public int size() {
		return size;
	}
	
	public void add(int item) {
		if (size == buffer.length) {
			setCapacity(buffer.length << 1);
		}
		buffer[size++] = item;
	}
	
	public int pop() {
		if (size > 0)
			return buffer[--size];
		throw new EmptyStackException();
	}
	
	public void clear() {
		size = 0;
	}
	
	public void setCapacity(int capacity) {
		if (capacity <= buffer.length)
			return;
		int[] newBuffer = new int[capacity];
		if (size > 0)
			System.arraycopy(buffer, 0, newBuffer, 0, size);
		buffer = newBuffer;
	}

	public int[] toArray() {
		if (size == 0)
			return EMPTY;
		return toArray(new int[size]);
	}
	
	public int[] toArray(int[] dest) {
		if (size > 0)
			System.arraycopy(buffer, 0, dest, 0, size);
		return dest;
	}
}
