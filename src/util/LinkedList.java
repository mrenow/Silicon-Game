package util;

import static core.MainProgram.*;
import static util.DB.*;

import java.util.Iterator;
import java.util.ListIterator;

/*
 * An empty linked list contains one node, the start node. The payload is null.
 */
public class LinkedList<T> implements Iterable<T> {

	// only to be referenced by internal functions.
	protected Node start;
	protected Node end;

	// Pointer state variables
	// NOT THREAD SAFE AT ALL: MOVE TO ITERATORS.
	Node focus;
	int index;

	// start -> end
	public LinkedList() {
		start = new Node(Node.START, end = new Node(Node.END,null));
		index = 0;
	}

	// linked list with one element.
	public LinkedList(T e) {
		this();
		add(e);
	}

	// linked
	public LinkedList(T... e) {
		this();
		for (int i = 0; i < e.length; i++) {
			add(e[i]);
		}
	}

	// makes a copy of a linked list down to node payload
	public LinkedList(LinkedList<T> list) {
		this();
		Iterator<T> iterator = list.iterator();
		while (iterator.hasNext()) {
			add(iterator.next());
		}
	}

	// O(n)
	// highly inefficient, would be better off using an iterator
	public T get(int i) {
		resetFocus();
		for (; i > 0; i--) {
			focusTo(index + 1);
		}
		return focus.getNext().o;
	}

	// *?*
	public int indexOf(T e) {
		resetFocus();
		while (!isLast(focus)) {
			if (focus.getNext().o == e) {
				return index;
			}
			focusTo(index + 1);
		}
		return -1;
	}

	public boolean contains(T e) {
		return indexOf(e) != -1;
	}

	public void addFirst(T e) {
		start.next = new Node(e, start.next);
	}

	// *y*
	public void addFirst(T... list) {
		for (int i =  list.length; i > 0; i--) {
			addFirst(list[i-1]);		
		}
	}

	// *y*
	public void addFirst(LinkedList<T> list) {
		list.end.setNext(start.getNext());
		start.setNext(list.start);
	}

	// *y*
	// append to end of list. End node transforms into payload node.
	// new end node is inserted.
	public void add(T e) {
		end.assume(new Node(e,end = new Node(Node.END,end.next)));
	}

	public void add(T... list) {
		for (T e : list) {
			add(e);
		}
	}

	// *y*
	// End transforms into new lists start node
	public void add(LinkedList<T> list) {
		Node endnext = end.next;
		end.assume(list.start);
		list.start = end;
		list.end.setNext((end = new Node(Node.END,endnext)));

	}

	// addAt will place elements before element at specified index.
	public void addAt(int i, T e) {
		resetFocus();
		focusTo(i);
		Node e1 = new Node(e, focus.getNext());
		focus.setNext(e1);
	}

	public void addAt(int i, T... list) {
		resetFocus();
		focusTo(i);
		Node next = focus.getNext();
		Node prev = focus;
		for (T e : list) {
			prev.setNext(new Node(e, null));
			prev = prev.getNext();
		}
		prev.setNext(next);
	}

	public void addAt(int i, LinkedList<T> list) {
		resetFocus();
		focusTo(i);
		// Join end
		list.end.setNext(focus.getNext());
		// Join beginning
		focus.setNext(list.start);
	}

	public T take(int i) {
		resetFocus();
		focusTo(i);
		Node next = focus.getNext();
		if (isLast(focus)) {
			throw new ArrayIndexOutOfBoundsException(Integer.toString(i + 1) + ">" + Integer.toString(index));
		}
		focus.setNext(focus.getNext().next); // child = grandChild, skipping one element.

		return next.o;
	}

	public boolean remove(T o) {
		resetFocus();
		while (focus.getNext().o != o) {
			focusTo(index + 1);
			if (isLast(focus)) {
				return false;
			}
		}
		focus.setNext(focus.getNext().next); // parent = grandparent, skipping one element.
		return true;
	}

	// clear all contents of list.
	public void clear() {
		start.next = end;
	}

	// splits before element at specified index
	public LinkedList<T>[] split(int i) {
		if (i == 0)
			return new LinkedList[] { new LinkedList(), new LinkedList(start.getNext()) };
		resetFocus();
		focusTo(i);
		Node start2 = focus.switchNext(null);
		return new LinkedList[] { new LinkedList(start.getNext()), new LinkedList(start2) };
	}

	// Focus manupulation
	void resetFocus() {
		focus = start;
		index = 0;
	}

	// Moves focus to index n and increments index counter.
	// Marker nodes are skipped.
	void focusTo(int n) {
		while (index < n) {
			if (isLast(focus)) {
				throw new ArrayIndexOutOfBoundsException(Integer.toString(n) + ">" + Integer.toString(index));
			}
			focus = focus.nextElement();
			index++;
		}
	}

	public ListIterator<T> iterator() {
		return new LinkedIterator();
	}

	public T[] toArray(T[] out) {
		resetFocus();
		while (!isLast(focus)) {
			out[index] = focus.getNext().o;
			focusTo(index + 1);
		}
		return out;
	}

	public int size() {
		resetFocus();
		while (!isLast(focus)) {
			focusTo(index + 1);
		}
		return index;
	}

	@Override
	public String toString() {
		StringBuilder out = new StringBuilder(5);
		out.append("LinkedList:[ ");
		
		Node curr = start;
		while(curr != null && curr != end.next) {
			if(curr.mode == Node.START) {
				out.append("+, ");
			}else if(curr.mode == Node.END) {
				out.append("-, ");
			} else {
				out.append(curr.o.toString());
				out.append(", ");
			}
			curr = curr.next;
		}
		out.deleteCharAt(out.length() - 1);
		out.append("]");
		return out.toString();
	}

	public boolean equals(Object that) {
		if (that instanceof LinkedList) {
			ListIterator<T> thisiterator = iterator();
			// Casting of that to LinkedList guaranteed.
			ListIterator<T> thatiterator = ((LinkedList<T>) that).iterator();
			while (thisiterator.hasNext() && thatiterator.hasNext()) {
				if (!thisiterator.next().equals(thatiterator.next()))
					return false;
			}
			if (thisiterator.hasNext() != thatiterator.hasNext())
				return false;

			return true;
		} else
			return false;
	}
	boolean isLast(Node n) {
		Node nextnode = n.next;
		while (nextnode != end) {
			if(nextnode.mode == Node.NODE) {
				return false;	
			}
			nextnode = nextnode.next;
		}
		return true;
	}
	/*
	 * public void destroy() { start.next = last = focus = null; index = -1; } //
	 * creates a new linked list starting at a certain node. If a loop is //
	 * encountered, it will break the loop forming a linked list that way. public
	 * LinkedList(Node n) { this(); start.next = n; last = n.next; while (last !=
	 * start.next && last.next != null) { last = last.next; } }
	 */

	// THREAD SAFE AF
	// MAYBE
	// ACTUALLY I KINDA DOUBT IT THIS WAS A WASTE OF MY TIME
	// I GUESS IF YOURE NOT WRITING ITS A OKAY
	private class LinkedIterator implements ListIterator<T> {
		Node curr;
		int id;

		public LinkedIterator() {
			curr = start;
			id = 0;
		}

		public boolean hasNext() {
			return !isLast(curr);
		}

		public T next() {
			return (curr = curr.nextElement()).o;
		}

		@Override
		//adds 
		public void add(T e) {
			curr = (curr.setNext(new Node(e, curr.getNext())));
			index++;
		}

		@Override
		//delete curr, become curr.next
		public void remove() {
			curr.assume(curr.getNext());
		}

		@Override
		public void set(T e) {
			curr.o = e;
		}

		@Override
		public int nextIndex() {
			return id;
		}

		@Override
		public T previous() {
			// TODO Auto-generated method stub
			return null;
		}

		// operations for previous are impossible.
		@Override
		public boolean hasPrevious() {
			return false;
		}

		@Override
		public int previousIndex() {
			return id - 1;
		}
	};

	private class Node {
		private Node next;
		public byte mode = 0;
		public static final byte START = 1;
		public static final byte END = 2;
		public static final byte NODE = 0;
		
		T o;

		Node(T o, Node next) {
			this.o = o;
			this.setNext(next);
		}

		Node(byte mode, Node next) {
			this.o = null;
			this.setNext(next);
			this.mode = mode;
		}

		Node switchNext(Node next) {
			Node old = this.getNext();
			this.setNext(next);
			return old;
		}

		void set(T e) {
			o = e;
		}

		// copy node into self
		void assume(Node n) {
			this.o = n.o;
			this.next = n.next;
			this.mode = n.mode;

		}

		Node nextElement() {
			Node nextnode = getNext();
			while (nextnode.mode!=NODE) {
				nextnode = nextnode.getNext();
			}
			return nextnode;
		}

		Node getNext() {
			Node nextnode = next;
			while (nextnode.mode!=NODE) {
				nextnode = nextnode.next;
			}
			return nextnode;
		}

		Node setNext(Node next) {
			Node node = this;
			while (node.next != null && node.next.mode!=NODE) {
				node = node.next;
			}
			return(node.next = next);
		}
	}
}
