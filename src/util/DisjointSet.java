package util;

// 

/* ancestors will contain information about the set as a whole such as:
 * - group size
 * and in the case of a wire segment
 * - list of gates it can toggle
 * - list of gated wire connections
 * - powered/unpowered
 */
public abstract class DisjointSet {
	
	//pointer to parent
    DisjointSet parent;
    
    ObjectInfo oInfo;
    //only exists if object is ancestor.
    SetInfo sInfo = null;
	
	//combines two sets if they are different
	public void union(DisjointSet other) {}
	
	//creates a second disjoint set
	public void split() {}
	
	public void destroy() {}
	
	//Ancestor is highest object in the heirachy.
	public DisjointSet getAncestor() {return null;}
	
	//Object is the ancestor if parent == self.
	public boolean isAncestor() {return false;}
	
	// Implementation depeneds on the behaviour of the set.
	abstract class SetInfo{
		public SetInfo(DisjointSet ancestor) {}
		
		public void add(SetInfo other) {}
		
		public void sub(SetInfo other) {}
		
	}
	abstract class ObjectInfo{
	}
}


