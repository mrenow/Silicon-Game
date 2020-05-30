package util;

import static util.DB.DB_E;
import static util.DB.DB_ASSERT;

// 

/* ancestors will contain information about the set as a whole such as:
 * - group size
 * and in the case of a wire segment
 * - list of gates it can toggle
 * - list of gated wire connections
 * - powered/unpowered
 */
public abstract class DisjointSet{
	
	//pointer to parent
	private DisjointSet parent = this;
    /*
     * Every time an element in a set is updated, the ancestor will
     * increment its versionnum. When a child makes a call to getAncestor(),
     * it will check that it's version number matches the parent's. If it doesnt,
     * it proceeds to recursively obtain the ancestor and new version number.
     * 
     * What happens during set union? The ancestor of the merged element will now contain
     * the version number of its new ancestor. Ideally we dont want any global behaviour,
     * so each disjoint set ancestor will have to keep a local version number, and so
     * Its possible that version numbers can coincide.
     * To remedy this we could add an additional check as to whether ancestor.parent = ancestor,
     * but that would make the operation twice as slow. we could instead keep seperate counts
     * of the parent number and child number - in this situation, the old ancestor would
     * incerment it's parent number but set its child number to the number of the new ancestor.
     * 
     * In set subtraction? The element whose parent was removed becomes the ancestor of the new
     * set, and sets its parent and child nums to 0.
     * 
     * MakeAncestor functions similarly to union.
     */
    
    // Cached pointer to ancestor
	// public for debug only.
    public DisjointSet ancestor = this;
    
    public int childnum = 0;
    public int parentnum = 0;

    /*
     * Assumes that [that] is the ancestor of its set. If this is not true, then
     * the system becomes inconsistent.
     * 
     * returns true if successful
     */
    // THIS IS NOW DEPRICATED. Use union instead.
    public boolean add(DisjointSet that) {
		if(isSameSet(that)) return false;
		// Invalidate ancestor, but ancestor is valid to itself.
		ancestor.childnum = ++ancestor.parentnum;
		childnum = ancestor.parentnum;
		
		that.parent = this;
		this.addInfo(that);
		return true;
	}
    
    public boolean remove(DisjointSet that) {
    	DB_ASSERT(this == that, false);
    	if(that.parent != this)	return false;
		
		// that becomes the ancestor of its set.
    	that.parent = that;
    	// Invalidate old ancestor
    	DisjointSet anc = getAncestor();
    	anc.childnum = ++anc.parentnum;
    	this.cacheAncestor(anc);
    	
    	that.parentnum++;
		that.cacheAncestor(that);
    	this.subInfo(that);
    	return true;
    }
    
	
	//Ancestor is highest object in the hierarchy.
	public DisjointSet getAncestor() {
//		if(isAncestor()) return this;
//		return parent.getAncestor();
		
		// If we find that half way through that the cached copy has
		// been invalidated, then we need to propagate back.
		// This is guaranteed to complete if no changes are made
		// to the structure.
		// I trust that these operations will complete fast enough
		// to actually succeed eventually.
		if(childnum != ancestor.parentnum) {
			do{
				// Update cache
				// Do we actually need this?
				// I actually think we dont - ancestors are denoted now
				// by having cached ancestor = itself, and 
	
				if(isAncestor()) {
					DB_E("State is inconsistent???");
					return this;
				}
				
				DB_ASSERT(cacheAncestor(parent.getAncestor()), false);
			/*
			 *  A parent's childnum would only be different
			 *  to the child's childnum if 
			 *  a) The parent has cached a new update from ancestor
			 *  b) The parent is the new ancestor
			 *  c) the parent has changed
			 *  in any of these scenarios, we need to revalidate the cache
			 */
			
			} while(parent.childnum != childnum);
		}
		DB_ASSERT(ancestor.ancestor, ancestor);
		DB_ASSERT(ancestor, ancestor.parent);
		return ancestor;
	}
	
	// Makes anc an ancestor of all elements in the set [this] belongs to.
	// Note that this means the ancestor of anc will be the ancestor of the new set
	// Can be thought of as union.
	// this.union(this); Will make this the ancestor of its own set.
	// this.union(w);, where w is in same set as [this] will keep the set the same,
	// but rearrange the interior structure so that w is the set ancestor, and 
	// this is the direct child of w. For the purposes of the game engine, we
	// avoid this.
	public void union(DisjointSet anc) {
		// Make sure that anc has a valid cache !!
		// Its okay, this thread has exclusive modification privilege,
		// so we dont need to worry about the cache getting
		// invalidated.
		anc.getAncestor();
		_union(anc);
	}
	
	private void _union(DisjointSet anc) {
		DB_ASSERT(anc==this, false);
		DisjointSet oldparent = parent;

		parent = anc;
		// valid uncached
		DB_ASSERT(cacheAncestor(anc.ancestor), false);
		// If this is old ancestor
		if(this == oldparent) parentnum++;

		// valid cached
		if(this != oldparent) {
			oldparent.subInfo(this);
			oldparent._union(this);
		}
		anc.addInfo(this);
	}
	
	private synchronized boolean cacheAncestor(DisjointSet anc) {
		ancestor = anc;
		childnum = ancestor.parentnum;		
		return anc.ancestor != anc;
	}

	/* public void makeAncestor(DisjointSet s) {
		this.clearInfo();
		DisjointSet oldparent = parent;
		s.add(this);
		if(this != oldparent) {
			oldparent.makeAncestor(this);
		}
		updateSetInfo();
	}*/
	public boolean isSameSet(DisjointSet that) {
		return that.getAncestor() == this.getAncestor();
	}
	
	//Object is the ancestor if parent == self.
	public boolean isAncestor() {
		return this == parent;
	}
	
	public DisjointSet getParent() {
		return parent;
	}

	public abstract void addInfo(DisjointSet that);
	public abstract void subInfo(DisjointSet that);
	protected abstract void clearInfo();
}


