package util;

import static core.MainProgram.*;
import static util.DB.*;

import java.io.Serializable;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import game.Gate;
/*
 * An empty linked list contains one node, the start node. The payload is null.
 * Also, welcome to Pointer Hell. Is it your first time here?
 */
public class LLinkedList<T> implements Iterable<T>{

	// only to be referenced by internal functions.
	protected Node<T> start;
	protected Node<T> end;

	// Pointer state variables
	// NOT THREAD SAFE AT ALL: MOVE TO ITERATORS.
	Node<T> focus;
	int index;
	
	ReentrantReadWriteLock lk;
	// lock for the lock... ugh why do I have to do this
	final ReentrantReadWriteLock lklk;

	// start -> end
	public LLinkedList() {
		start = new Node<T>(Node.START, end = new Node<T>(Node.END,null));
		index = 0;
		lk = new ReentrantReadWriteLock();
		lklk = new ReentrantReadWriteLock();
	}

	// linked list with one element.
	public LLinkedList(T e) {
		this();
		add(e);
	}

	// linked
	public LLinkedList(T... list) {
		this();
		for (int i = 0; i < list.length; i++) {
			add(list[i]);
		}
	}

	// makes a copy of a linked list down to node payload
	public LLinkedList(LLinkedList<T> list) {
		this();
		for(T e : list) {
			add(e);
		}
	}

	// O(n)
	// highly inefficient, would be better off using an iterator
	public T get(int i) {
		lockRead();
		resetFocus();
		for (; i > 0; i--) {
			focusTo(index + 1);
		}
		unlockRead();
		return focus.getNext().o;
	} 

	// *?*
	public int indexOf(T e) {
		lockRead();
		resetFocus();
		while (!isLast(focus)) {
			if (focus.getNext().o == e) {
				unlockRead();
				return index;
			}
			focusTo(index + 1);
		}
		unlockRead();
		return -1;
	}

	public boolean contains(T e) {
		return indexOf(e) != -1;
	}
	//checks for start node
	public boolean contains(LLinkedList<T> ls) {
		Node<T> curr = start;
		lockRead();
		while(curr != end) {
			if(curr == ls.start) {
				unlockRead();
				return true;
			}
			curr = curr.next;
		}
		unlockRead();
		
		return false;
	}


	public void addFirst(T e) {
		lockWrite();
		start.next = new Node<T>(e, start.next);
		unlockWrite();
	}

	// *y*
	public void addFirst(T... list) {
		lockWrite();
		for (int i =  list.length; i-- != 0 ;) {
			start.next = new Node<T>(list[i], start.next);
		}
		unlockWrite();
	}

	// *y*
	public void addFirst(LLinkedList<T> list) {
		lockWrite();
		list.lockWrite();
		list.end.setNext(start.getNext());
		start.setNext(list.start);
		list.unlockWrite();
		unlockWrite();
	}

	// *y*
	// append to end of list. End node transforms into payload node.
	// new end node is inserted.
	public void add(T e) {
		lockWrite();
		end.assume(new Node<T>(e,end = new Node<T>(Node.END,end.next)));
		unlockWrite();
	}

	public void add(T... list) {
		lockWrite();
		for (T e : list) {
			end.assume(new Node<T>(e,end = new Node<T>(Node.END,end.next)));
		}
		unlockWrite();
	}

	// *y*
	// End transforms into new lists start node
	public void add(LLinkedList<T> list) {
		Node<T> endnext = end.next;
		lockWrite();
		list.lockWrite();
		end.assume(list.start);
		list.start = end;
		list.end.setNext((end = new Node<T>(Node.END,endnext)));
		list.unlockWrite();
		unlockWrite();
		list.switchLock(lk);
	}

	// addAt will place elements before element at specified index.
	public void addAt(int i, T e) {
		lockWrite();
		resetFocus();
		focusTo(i);
		Node<T> e1 = new Node<T>(e, focus.getNext());
		focus.setNext(e1);
		unlockWrite();
	}

	public void addAt(int i, T... list) {
		lockWrite();
		resetFocus();
		focusTo(i);
		Node<T> next = focus.getNext();
		Node<T> prev = focus;
		for (T e : list) {
			prev.setNext(new Node<T>(e, null));
			prev = prev.getNext();
		}
		prev.setNext(next);
		unlockWrite();
	}

	public void addAt(int i, LLinkedList<T> list) {
		lockWrite();
		resetFocus();
		focusTo(i);
		// Join end
		list.end.setNext(focus.getNext());
		// Join beginning
		focus.setNext(list.start);
		unlockWrite();
		list.switchLock(lk);
	}

	public T take(int i) {
		lockWrite();
		resetFocus();
		focusTo(i);
		Node<T> next = focus.getNext();
		if (isLast(focus)) {
			unlockWrite();
			throw new ArrayIndexOutOfBoundsException(Integer.toString(i + 1) + ">" + Integer.toString(index));
		}
		focus.setNext(focus.getNext().next); // child = grandChild, skipping one element.

		unlockWrite();
		return next.o;
	}

	public boolean remove(T o) {
		lockRead();
		Node<T> curr = start;
		
		while(curr.next.o != o) {
			if(curr.next == end) {
				unlockRead();
				return false;
			}
			curr = curr.next;
		}
		unlockRead();
		lockWrite();
		curr.next = curr.next.next;
		curr.next.prev = curr;
		unlockWrite();
		return true;
	}
	// Searches for the start node of list
	// Assumes that the existence of a start node implies an end node.
	public boolean remove(LLinkedList<T> list) {
		lockRead();
		Node<T> curr = start;
		
		while(curr.next != list.start) {
			if(curr == end) {
				unlockRead();
				return false;
			}
			curr = curr.next;
		}
		unlockRead();

		lockWrite();
		curr.next = list.end.next;
		curr.next.prev = curr;
		//separate list from parent for safety
		list.end.next = null;
		unlockWrite();
		list.switchLock(new ReentrantReadWriteLock());
		return true;
	}
	public boolean removeAll(T o) {
		lockWrite();
		Node<T> curr = start;
		boolean exists = false;
		while(curr.next != end) {
			if(curr.next.o == o) {
				
				curr.next = curr.next.next;
				curr.next.prev = curr;
				
				exists = true;
			}else {
				curr = curr.next;
			}
		}
		unlockWrite();
		return exists;
		
	}
	public boolean removeLast() {
		if(empty()) {
			return false;
		}
		lockWrite();
		end.setPrev(end.prevElement().prev);
		unlockWrite();
		return true;
	}
	
	// clear all contents of list.
	public void clear() {
		lockWrite();
		start.next = end;
		end.prev = start;
		unlockWrite();
	}
	public boolean empty() {
		return isLast(start);
	}
	//removes its existence from parent if exists.
	public void delete() {
		lockWrite();
		if(end.next != null) {
			end.next.prev = start.prev;
			start.assume(end.next);
		}
		unlockWrite();
	}
	
	// UNTESTED
	// splits before element at specified index
	/*public LLinkedList<T>[] split(int i) {
		if (i == 0)
			return new LLinkedList[] { new LLinkedList(), new LLinkedList(start.getNext()) };
		resetFocus();
		focusTo(i);
		Node<T> start2 = focus.switchNext(null);
		return new LLinkedList[] { new LLinkedList(start.getNext()), new LLinkedList(start2) };
	}*/

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
	public void lockRead() {
		lklk.readLock().lock();
		lk.readLock().lock();
	}
	public void unlockRead() {
		lk.readLock().unlock();
		lklk.readLock().unlock();
	}
	public void lockWrite() {
		lklk.readLock().lock();
		lk.writeLock().lock();
	}
	public void unlockWrite() {
		lk.writeLock().unlock();
		lklk.readLock().unlock();
	}
	/*
	 * Switches which lock we are using
	 * Do no use within a read/write section.
	 */
	void switchLock(ReentrantReadWriteLock newlock) {
		lklk.writeLock().lock();
		lk = newlock;
		lklk.writeLock().unlock();
	}

	public ListIterator<T> iterator() {
		return new LinkedIterator();
	}

	public T[] toArray(T[] out) {
		lockRead();
		resetFocus();
		while (!isLast(focus)) {
			out[index] = focus.getNext().o;
			focusTo(index + 1);
		}
		unlockRead();
		return out;
	}

	public int size() {
		lockRead();
		resetFocus();
		while (!isLast(focus)) {
			focusTo(index + 1);
		}
		unlockRead();
		return index;
	}

	@Override
	public String toString() {
		lockRead();
		StringBuilder out = new StringBuilder(5);
		// 
		out.append("LLinkedList:[ ");
		
		Node<T> curr = start;
		int index = 0;
		while(curr != null && curr != end.next) {
			if(index > 100) {
				out.append("...");
				break;
			}
			
			if(curr.mode == Node.START) {
				if(curr == start) {
					out.append("S, ");
				}
				else {
					out.append("+, ");
				}
			}else if(curr.mode == Node.END) {
				if(curr == end) {
					out.append("E, ");
				}else {
					out.append("-, ");
					
				}
				
			} else {
				out.append(curr.o.toString());
				out.append(", ");
			}
			curr = curr.next;
			index ++;
		}
		unlockRead();
		out.deleteCharAt(out.length() - 1);
		out.append("]");
		return out.toString();
	}

	public boolean equals(Object that) {
		if (that instanceof LLinkedList) {
			lockRead();
			ListIterator<T> thisiterator = iterator();
			// Casting of that to LLinkedList guaranteed.
			((LLinkedList<T>) that).lockRead();
			ListIterator<T> thatiterator = ((LLinkedList<T>) that).iterator();
			while (thisiterator.hasNext() && thatiterator.hasNext()) {
				if (!thisiterator.next().equals(thatiterator.next())) {
					((LLinkedList<T>) that).lockRead();		
					unlockRead();
					return false;
				}
			}
			if (thisiterator.hasNext() != thatiterator.hasNext()) {
				((LLinkedList<T>) that).lockRead();
				unlockRead();
				return false;
			}
			((LLinkedList<T>) that).lockRead();
			unlockRead();
			return true;
		} else
			return false;
	}
	boolean isLast(Node<T> n) {
		lockRead();
		Node<T> nextnode = n.next;
		while (nextnode != end) {
			if(nextnode.next == null) {
				println("broken ",this);
				println(nextnode.o);
				println(nextnode.mode);
			}
			if(nextnode.mode == Node.NODE) {
				unlockRead();
				return false;	
			}
			nextnode = nextnode.next;
		}
		unlockRead();
		return true;
	}
	boolean isFirst(Node<T> n) {
		lockRead();
		Node<T> prevnode = n.prev;
		while (prevnode != start) {
			if(prevnode.prev == null) {
				println("broken ",this);
				println(prevnode.o);
				println(prevnode.mode);
				
			}
			if(prevnode.mode == Node.NODE) {
				unlockRead();
				return false;	
			}
			prevnode = prevnode.prev;
		}
		unlockRead();
		return true;
	}
	/*
	 * public void destroy() { start.next = last = focus = null; index = -1; } //
	 * creates a new linked list starting at a certain node. If a loop is //
	 * encountered, it will break the loop forming a linked list that way. public
	 * LLinkedList(Node<T> n) { this(); start.next = n; last = n.next; while (last !=
	 * start.next && last.next != null) { last = last.next; } }
	 */

	// THREAD SAFE AF
	// MAYBE
	// ACTUALLY I KINDA DOUBT IT THIS WAS A WASTE OF MY TIME
	// I GUESS IF YOURE NOT WRITING ITS A OKAY
	// WAIT NO ITS GREAT JOKES
	public class LinkedIterator implements ListIterator<T> {
		Node<T> curr;
		int id;

		public LinkedIterator() {
			curr = start;
			id = 0;
		}

		public boolean hasNext() {
			return !(curr == end || isLast(curr));
		}

		public T next() {
			return (curr = curr.nextElement()).o;
		}

		@Override
		//adds 
		public void add(T e) {
			curr = (curr.setNext(new Node<T>(e, curr.getNext())));
			id++;
		}

		@Override
		//delete curr, become curr.prev
		public void remove() {
			Node<T> prev = curr;
			curr.assume(curr.prev);
			if (prev == start) start = curr;
		}

		@Override
		public void set(T e) {
			curr.o = e;
		}

		@Override
		public int nextIndex() {
			return id;
		}

		// EXTREMELY IMPORTANT NOTE: DOES NOT MOVE POINTER
		@Override
		public T previous() {
			return (curr = curr.prevElement()).o;
		}

		// operations for previous are impossible.
		@Override
		public boolean hasPrevious() {
			return !(curr == start || isFirst(curr));
		}

		@Override
		public int previousIndex() {
			return id - 1;
		}
		public String toString() {
			StringBuilder out = new StringBuilder(5);
			// 
			out.append("LLinkedList:[ ");
			
			Node<T> curr = start;
			int index = 0;
			while(curr != null && curr != end.next) {
				if(index > 100) {
					out.append("...");
					break;
				}
				if(this.curr == curr) {
					out.append("^");
					
				}
				if(curr.mode == Node.START) {
					if(curr == start) {
						out.append("S, ");
					}
					else {
						out.append("+, ");
					}
				}else if(curr.mode == Node.END) {
					if(curr == end) {
						out.append("E, ");
					}else {
						out.append("-, ");
						
					}
					
				} else {
					out.append(curr.o.toString());
					out.append(", ");
				}
				curr = curr.next;
				index ++;
			}
			out.deleteCharAt(out.length() - 1);
			out.append("]");
			
			return out.toString();
		}
		
	}


}
class Node<T>{
	Node<T> next = null;
	Node<T> prev = null;
	public byte mode = 0;
	public static final byte START = 1;
	public static final byte END = 2;
	public static final byte NODE = 0;
	
	T o;

	Node(T o, Node<T> next) {
		this.o = o;
		this.setNext(next);
		if(next != null) next.setPrev(this);
	}

	Node(byte mode, Node<T> next) {
		this.o = null;
		this.setNext(next);
		if(next != null) next.setPrev(this);
		this.mode = mode;
	}
	/*
	Node<T> switchNext(Node<T> next) {
		Node<T> old = this.getNext();
		this.setNext(next);
		return old;
	}
	 */
	void set(T e) {
		o = e;
	}

	// copy node into self
	void assume(Node<T> n) {
		if(n == null) return;
		this.o = n.o;
		this.next = n.next;
		this.prev = n.prev;
		this.mode = n.mode;

	}

	Node<T> nextElement() {
		Node<T> nextnode = getNext();
		while (nextnode.mode!=NODE) {
			nextnode = nextnode.getNext();
		}
		return nextnode;
	}
	Node<T> prevElement() {
		Node<T> prevnode = getPrev();
		while (prevnode.mode!=NODE) {
			prevnode = prevnode.getPrev();
		}
		return prevnode;
	}
	Node<T> getNext() {
		Node<T> nextnode = next;
		while (nextnode.mode!=NODE) {
			nextnode = nextnode.next;
		}
		return nextnode;
	}
	Node<T> getPrev(){
		Node<T> prevnode = prev;
		while (prevnode.mode!=NODE) {
			prevnode = prevnode.prev;
		}
		return prevnode;
	}	

	Node<T> setNext(Node<T> next) {
		Node<T> node = this;
		while (node.next != null && node.next.mode!=NODE) {
			node = node.next;
		}
		if(next != null) next.prev = node;
		return(node.next = next);
	}
	Node<T> setPrev(Node<T> prev){

		Node<T> node = this;
		while (node.prev != null && node.prev.mode!=NODE) {
			node = node.prev;
		}
		if(prev != null) prev.next = node;
		return(node.prev = prev);
	}

}