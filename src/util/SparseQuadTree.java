package util;

import java.util.Arrays;

import static util.DB.*;
import static core.MainProgram.*;
import static processing.core.PApplet.println;

/* A range tree that stores pointers to elements.
 * Each element of the range tree stores a list of all
 * pointers to elements within that range. Children of
 * this range will more specifically inform the location
 * of these elements.
 * Null ranges that not have any elements within them
 * do not have any children either.
 */
@SuppressWarnings("unchecked")
public class SparseQuadTree<T> {

	int depth;
	int length;

	// 0 1
	// 2 3
	SparseQuadTree[] children = new SparseQuadTree[4];
	SparseQuadTree<T> parent = null;

	// linked list has ideal properties:
	// O(1) append list
	// O(N) search
	// O(1) remove element
	// O(1) update n parent lists
	public LinkedList<T> elements;

	public SparseQuadTree(int depth) {
		this.depth = depth;
		length = 1 << (depth);
		elements = new LinkedList<T>();
	}

	public SparseQuadTree(int depth, T[][] data) {
		this(depth);
		int[] dim = ArraysX.dimensions(data);
		DB_ASSERT(dim[0], length);
		DB_ASSERT(dim[1], length);
		//TODO
	}

	public SparseQuadTree(int depth, SparseQuadTree<T> parent) {
		this(depth);
		parent.elements.add(elements);
		this.parent = parent;

	}

	// updates a single element
	public void add(T element, int x, int y) {
		getPointerTo(x, y).elements.add(element);
	}

	public boolean remove(T element, int x, int y) {
		SparseQuadTree<T> t = getLowestSubtree(x, y);
		if (t.depth == 0) {
			DB_U(element, "removed from", t.elements, "in", this);
			boolean removed = t.elements.remove(element);
			
			if(t.elements.empty()) update(x,y);
			
			return removed;
		}
		return false;
	}

	// removes all elements from a specified unit square
	public void delete(int x, int y) {
		SparseQuadTree<T> t = getLowestSubtree(x, y);
		if (t.depth == 0) {
			DB_U(new LinkedList<T>(t.elements), " deleted in ", this);
			t.elements.clear();
		}
		update(x,y);
	}

	// removes all elements from a specfied range
	public void deleteRange(int x1, int x2, int y1, int y2) {
		
		for (int y = y1; y < y2; y++) {
			for (int x = x1; x < x2; x++) {
				delete(x, y);
			}
		}
	}

	

	// gets pointer if exists or creates new one

	public LinkedList<T> get(int x, int y) {
		// make a copy of the pointers.
		SparseQuadTree<T> t = getLowestSubtree(x, y);
		if(t.depth == 0) {
			return new LinkedList<T>(t.elements);
		}
		return new LinkedList<T>();
	}

	public LinkedList<T> get(int x1, int x2, int y1, int y2) {
		LinkedList<T> result = new LinkedList<T>();
		
		// match query range to bounds
		x1 = constrain(x1, 0, length);
		x2 = constrain(x2, 0, length);
		y1 = constrain(y1, 0, length);
		y2 = constrain(y2, 0, length);

		// zero width range
		if (x1 == x2 || y1 == y2)
			return result;
		// if bounds are matched perfectly
		if(abs(x1 - x2) == length && abs(y1 - y2) == length) {
			return new LinkedList<T>(elements);
		}
		int halflength = length / 2;
		// 0 1
		// 2 3
		// annoying warnings
		if (children[0] != null)
			result.add(children[0].get(x1, x2, y1, y2));
		if (children[1] != null)
			result.add(children[1].get(x1 - halflength, x2 - halflength, y1, y2));
		if (children[2] != null)
			result.add(children[2].get(x1, x2, y1 - halflength, y2 - halflength));
		if (children[3] != null)
			result.add(children[3].get(x1 - halflength, x2 - halflength, y1 - halflength, y2 - halflength));
		return result;
	}

	public int size() {
		return elements.size();

	}

	public String toString() {
		return super.toString();
	}
	private void update(int x, int y) {
		if (depth == 0) {
			return;
		}
		// ensure query remains within bounds.
		if (!DB_ASSERT(inRange(x, y), true)) {
			DB_E(x,y,"Not in range of quadtree of length",length);
			throw new ArrayIndexOutOfBoundsException();
		}
		
		int halflength = length / 2;
		int id =  (y < halflength ? 0 : 2) + (x < halflength ? 0 : 1);
		
		if (children[id] != null) {
			if(!children[id].elements.empty()) {
				// x & ~halflength replaces x % halflength
				children[id].update(x & ~halflength, y & ~halflength);
			} else {
				elements.remove(children[id].elements);
				//existing is for noobs
				children[id] = null;
			}
		}
	}

	private SparseQuadTree<T> getLowestSubtree(int x, int y) {
		if (depth == 0) {
			return this;
		}
		// ensure query remains within bounds.
		if (!DB_ASSERT(inRange(x, y), true)) {
			DB_E(x,y,"Not in range of quadtree of length",length);
			throw new ArrayIndexOutOfBoundsException();
		}
		int halflength = length / 2;
		
		int id =  (y < halflength ? 0 : 2) + (x < halflength ? 0 : 1);
		if (children[id] != null) {
			return children[id].getLowestSubtree(x & ~halflength, y & ~halflength);
		}
		
		return this;
	}

	// gets pointer if exists or creates new one
	
	private SparseQuadTree<T> getPointerTo(int x, int y) {
		if (depth == 0) {
			return this;
		}
		// ensure query remains within bounds.
		if (!DB_ASSERT(inRange(x, y), true)) {
			println(x,y);
			println(length);
			println(depth);
			throw new ArrayIndexOutOfBoundsException();
		}
		int halflength = length / 2;
		
		int id =  (y < halflength ? 0 : 2) + (x < halflength ? 0 : 1);
		if (children[id] == null) {
			children[id] = new SparseQuadTree<T>(depth - 1, this);
		}
		return children[id].getPointerTo(x & ~halflength, y & ~halflength);	
	}
	
	public SparseQuadTree<T> getAncestor(){
		if(parent == null) return this;
		return parent.getAncestor();
	}

	private boolean inRange(int x, int y) {
		return 0 <= x && x < length && 0 <= y && y < length;
	}

}
