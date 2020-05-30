package tempUtil;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.concurrent.locks.ReentrantReadWriteLock;


public class LinkedList<T> implements Iterable<T>{
	// For thread safety
	int modcount = 0;
	// Start is exclusive
	Node<T> start;
	// End is inclusive
	Node<T> end;
	
	/*
	 * Lock corresponds to lock of the highest list in the hierarchy - 
	 * Each lock should exactly correspond with one head node.
	 */
	ReentrantReadWriteLock lk;
	
	
	// Creates head node by default
	public LinkedList() {
		start = end = Node.newHead();
		lk = new ReentrantReadWriteLock();
	}
	
	// linked list with one element.
	public LinkedList(T e) {
		start = Node.newHead();
		Node.join(start, end = new Node<T>(e));
		lk = new ReentrantReadWriteLock();
	}

	// linked
	public LinkedList(T... list) {
		end = start = Node.newHead();
		for (T e: list) {
			Node.join(end, end = new Node<T>(e));
		}
		lk = new ReentrantReadWriteLock();
	}
	public LinkedList(LinkedList<T> list) {
		list.lk.readLock().lock();
		for(T e: list) {
			Node.join(end, end = new Node<T>(e));
		}
		list.lk.readLock().unlock();
		lk = new ReentrantReadWriteLock();
	}
	// Adds to back of list
	public void add(LinkedList<T> list) {
		list.lk.writeLock().lock();
		Node.join(end, list.start);
		list.lk.writeLock().unlock();
	}
	public void add(Node<T> node) {
		Node.join(end, list.start);
		
		
	}
	public void add(T data) {
		
	}
	
	@Override
	public ListIterator<T> iterator() {
		// TODO Auto-generated method stub
		return null;
	}
	
	class LinkedReadIterator implements Iterator<T>{
		Node<T> curr;
		int itermodcount = modcount;
		
		LinkedReadIterator(Node<T> curr){
			this.curr = curr;
		}
		
		@Override
		public boolean hasNext() {
			return curr.next != null;
		}

		@Override
		public T next() {
			if(itermodcount != modcount) {
				throw new ConcurrentModificationException();
			}
			return (curr = curr.next).data;
		}
		
		
	}
	class LinkedWriteIterator extends LinkedReadIterator implements ListIterator<T>{

		LinkedWriteIterator(Node<T> curr) {
			super(curr);			
		}

		@Override
		public void add(T arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public boolean hasPrevious() {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public int nextIndex() {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public T previous() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public int previousIndex() {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public void remove() {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void set(T arg0) {
			// TODO Auto-generated method stub
			
		}
		
		
	}
	
	
	
	
	

}

// head node has no data, is only a placeholder
// for the start node.
// Head node is defined so that prev = null.
class Node<T>{
	T data;
	Node<T> next;
	Node<T> prev;
	boolean ishead;
	Node(T data){
		this.data = data;
		this.ishead = false;
	}
	private Node(){
		this.ishead = true;
	}
	static <S> void join(Node<S> n1, Node<S> n2){
		if(n1 != null) n1.next = sn2;
		if(n2 != null) n2.prev = n1;
	}
	static <S> Node<S> newHead(){
		return new Node<S>();
	}
}
