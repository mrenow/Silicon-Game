package util;

import static core.MainProgram.*;
import static util.DB.*;


/*
 * Update: place new element at the top, then continually switch it with child
 * elements until it is higher than both its children. Query: take element from
 * the top and make higher child of lower
 */
// index zero stores length
// children(x) = 2x, 2x+1 eg 1 has 2 and 3
// last element is size()+1


//TO DO: Implement dynamic resizing. 
public class Heap<T> {
	boolean DECENDING;
	int[] data;
	T[] objects;

	final int DEFAULT_SIZE = 1024;

	public Heap() {
		DECENDING = true;
		data = new int[DEFAULT_SIZE];
		objects = (T[]) new Object[DEFAULT_SIZE];
	}

	public Heap(boolean descending) {
		DECENDING = descending;
		data = new int[DEFAULT_SIZE];
		objects = (T[]) new Object[DEFAULT_SIZE];
	}
	
	public Heap(int size, boolean descending) {
		DECENDING = descending;
		data = new int[size];
		objects = (T[]) new Object[size];
	}

	// add to bottom of heap
	public void add(int val, T object) {
		data[size() + 1] = val;
		objects[size() + 1] = object;
		data[0]++;
		updateUp(size());
	}

	public T pop() {
		T out = objects[1];
		data[1] = data[size()];
		objects[1] = objects[size()];
		data[0]--;
		updateDown(1);
		return out;
	}

	public int first() {
		return data[1];
	}

	public int size() {
		return data[0];
	}

	public boolean isEmpty() {
		return size() == 0;
	}

	public String toString() {
		String out = "[";
		for (int i = 1; i <= size(); i++) {
			out += Integer.toString(data[i]) + ":" + objects[i].toString() + ",";

		}
		out += "]";

		return out;
	}

	// will check above for inconsistency, else will swap.
	void updateUp(int index) {

		// if child<parent, order is not descending. runs if not in order.
		if (index == 1)
			return;
		if (data[index] < data[index / 2] == DECENDING) {
			swap(index, index / 2);
			updateUp(index / 2);
		}
		return;
	}

	void updateDown(int index) {
		// get higher priority child
		int priority = 2 * index;
		// if first child does not exist:
		if (priority > size()) {
			return;
		}
		// if second child exists:
		if (priority <= size() - 1 && data[2 * index] > data[2 * index + 1] == DECENDING) {
			priority++;
		}
		// if child<parent, order is not descending, swap and update at next node.
		// otherwise order exists and do nothing.
		if (data[priority] < data[index] == DECENDING) {
			swap(priority, index);
			updateDown(priority);
		}
		return;
	}

	void swap(int id1, int id2) {
		int a = data[id1];
		T b = objects[id1];
		data[id1] = data[id2];
		objects[id1] = objects[id2];
		data[id2] = a;
		objects[id2] = b;
	}

	public boolean verify() {
		return verify(1);
	}

	// tests if heap structure is preserved. Recurses for each level of the heap
	boolean verify(int i) {
		if (2 * i > size()) {
			return true;
		}
		if (DECENDING) {
			if (data[i] > data[2 * i])
				return false;
			if (2 * i + 1 <= size() && data[i] > data[2 * i + 1]) {
				return false;
			}
		} else {
			if (data[i] < data[2 * i]) {
				return false;
			}
			if (2 * i + 1 <= size() && data[i] < data[2 * i + 1]) {
				return false;
			}
		}
		return verify(2 * i) && verify(2 * i + 1);

	}


}