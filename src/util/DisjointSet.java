package util;

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
    protected DisjointSet parent = this;
    
	//combines two sets if they are different
	// attaches that to this.
    public void union(DisjointSet that) {
		that.parent = this;
		this.addInfo(that);
	}
	
	//Ancestor is highest object in the heirachy.
	public DisjointSet getAncestor() {
		if(isAncestor()) return this;
		return parent.getAncestor();
	}
	
	public void makeAncestor() {
		parent = this;
		if(!isAncestor()) parent.makeAncestor(this);
		updateSetInfo();
	}
	protected void makeAncestor(DisjointSet s) {
		this.clearInfo();
		DisjointSet oldparent = parent;
		this.parent = s;
		if(this != oldparent) {
			oldparent.makeAncestor(this);
		}
		updateSetInfo();
	}
	public boolean isSameSet(DisjointSet s) {
		return s.getAncestor() == this.getAncestor();
	}
	
	//Object is the ancestor if parent == self.
	public boolean isAncestor() {return this == parent;}

	public abstract void addInfo(DisjointSet that);
	public abstract void subInfo(DisjointSet that);	
	public abstract void updateSetInfo();
	protected abstract void clearInfo();
}


