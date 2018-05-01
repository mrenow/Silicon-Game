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
    protected DisjointSet parent;
    
    // sinfo of ancestor holds info for entire set.
    protected AbstractSetInfo sinfo;
	
	//combines two sets if they are different
	// attaches that to this.
    public void union(DisjointSet that) {
		DisjointSet that_ancestor = that.getAncestor();
		DisjointSet this_ancestor = this.getAncestor();
		if(this_ancestor != that_ancestor ) {
			that_ancestor.parent = this;
		}
		this.sinfo.add(that_ancestor.sinfo);
	}
	
	//Ancestor is highest object in the heirachy.
	public DisjointSet getAncestor() {
		if(isAncestor()) return this;
		return parent.getAncestor();
		
	}
	
	public void makeAncestor() {
		parent.makeAncestor(this);
		parent = this;
		//do set info calculations
	}
	private void makeAncestor(DisjointSet s) {
		parent.makeAncestor(this);
		parent = s;
		//do set info calculations
	}
	
	//Object is the ancestor if parent == self.
	public boolean isAncestor() {return this == parent;}
	
	// Implementation depeneds on the behaviour of the set.
	public abstract class AbstractSetInfo{
		public void add(AbstractSetInfo that) {}
		
		public void sub(AbstractSetInfo that) {}
		
	}
}


