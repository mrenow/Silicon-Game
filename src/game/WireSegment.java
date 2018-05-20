package game;

import static core.MainProgram.*;
import static util.DB.*;

import java.util.ArrayList;
import java.util.ListIterator;

import processing.core.PVector;
import util.DisjointSet;
import util.LinkedList;
import util.Pointer;
import util.SparseQuadTree;

public class WireSegment extends DisjointSet {
	
	//assigned depending on gameArea context.
	public static SparseQuadTree<WireSegment> container;
	public static LinkedList<WireSegment> potentialdisconnects = new LinkedList<WireSegment>(); 
	
	public static final byte METAL = 0;
	public static final byte N_TYPE = 1;
	public static final byte P_TYPE = 2;
	public static final byte VIA = 3;
	public static final byte N_GATE = 4;
	public static final byte P_GATE = 5;
	public static final byte POWER = 6;
	

	
	// individual info
	
	byte mode;

	int x;
	int y;

	
	// Set Info
	public static int WIRE_OFF = Integer.MAX_VALUE;
	
	public LinkedList<Gate> gates;
	public LinkedList<WireSegment> connections;
	// Pointer to an int for instant update
	// Priority value for signal propagation logic
	protected int active = Integer.MAX_VALUE;
	
	public boolean checkdisconnects = false;
	public int getActive(){
		if(!isAncestor()) {
			return ((WireSegment) parent).getActive();
		} else {
			return active;
		}
	}
	public void setActive(int val) {
		if(!isAncestor()) {
			((WireSegment) parent).setActive(val);
		} else {
			active = val;
		}
	}
	

	public WireSegment(byte mode, int x, int y) {
		this.mode = mode;
		this.x = x;
		this.y = y;
		this.gates = new LinkedList<Gate>();
		this.connections = new LinkedList<WireSegment>();
		this.parent = this;
		

		// oinfo = new ObjectInfo(x, y);
	}
	
	
	
	protected void clearInfo() {
		gates.clear();
		connections.clear();
		active = WIRE_OFF;
	}
	
	
	
	

	// this becomes parent to that
	public void addInfo(DisjointSet that) {
		
		DB_U("added",that,"->",this);
		
		if(this == that) {
			DB_E("INFO OF THIS ADDED TO THIS IN", this, ", PANIC");
		}
		WireSegment child = (WireSegment) that;
		if(gates.contains(child.gates)) {
			DB_E("UNION OCCURS TWICE AT", x, y);
		}else {
			
			this.gates.add(child.gates);
			this.connections.add(child.connections);
		}
		/*
		if (this.gates == null || this.gates.empty()) {
			child.gates = this.gates;
		} else {
			this.gates.add(child.gates);
		}
		if (this.connections == null || this.gates.empty()) {
			child.connections = this.connections;
		} else {
			this.connections.add(child.connections);
		}*/
	}
 
	// remove that from this
	@Override
	public void subInfo(DisjointSet that) {
		WireSegment child = (WireSegment) that;
		if(!gates.contains(child.gates)) {
			DB_E("CHILD DID NOT CONTAIN EXPECTED INFORMATION", x, y);
		}else {
			
			this.gates.remove(child.gates);
			this.connections.remove(child.connections);
			this.active = WIRE_OFF;
		}
		
	}
	
	public void updateSetInfo() {
		for(WireSegment w: getAdjacent()) {
			if(canConnect(w) && w != parent && !this.isSameSet(w)) {
				w.parent = this;
			}			
		}
	}
	

	public boolean isActive() {
		return getActive() != Integer.MAX_VALUE;
	}
	
	public boolean canConnect(WireSegment that) {
		if(this.x == that.x && this.y == that.y)
			return (this.mode == VIA || that.mode == VIA);
		return (this.mode == that.mode);
	}
	public void updateConnections() {
		println(getAdjacent());
		ListIterator<WireSegment> iter = new LinkedList<WireSegment>(getAdjacent()).iterator();
		WireSegment w;
		connections.clear();
		gates.clear();
		
		while(iter.hasNext()) {
			w = iter.next();
			if(w.isGate()) {
				Gate g = (Gate)w;
				//connection
				if((g.mode == N_GATE && mode == P_TYPE) || (g.mode == P_GATE && mode == N_TYPE)) {
					connections.add(g);
					g.connections.add(this);
				}
				//gate input
				if((g.mode == P_GATE && mode == P_TYPE) || (g.mode == N_GATE && mode == N_TYPE)) {
					gates.add(g);
					g.inputs.add(this);					
				}
				
			}else if(canConnect(w)) {
				if(!isSameSet(w)) {
					if(isAncestor()) {
						w.add(this);
					}else {
						w.makeAncestor(this);
					}
				} else {
					potentialdisconnects.addFirst(w);
					w.checkdisconnects = true;
					potentialdisconnects.addFirst(this);
					checkdisconnects = true;
				}
			}
		}
		DB_U("Connections Updateded");
	}
	public String toString() {
		StringBuilder s = new StringBuilder() ;
	
		switch(mode) {
		case  METAL: s.append("m"); break;
		case P_TYPE: s.append("p"); break;
		case N_TYPE: s.append("n"); break;
		case P_GATE: s.append("P"); break;
		case N_GATE: s.append("N"); break; 
		case    VIA: s.append("v"); break;
		}
		s.append("{");
		s.append(x);
		s.append(",");
		s.append(y);
		s.append("}");
		return s.toString();
	}
	private LinkedList<WireSegment> getAdjacent(Direction d) {
		
		switch (d) {
		case NORTH:
			return container.get(x, y - 1);
		case EAST:
			return container.get(x + 1, y);
		case SOUTH:
			return container.get(x, y + 1);
		case WEST:
			return container.get(x - 1, y);
		case IN:
			LinkedList<WireSegment> l = container.get(x, y);
			l.remove(this);
			return l;
		}
		return null;
	}
	
	public LinkedList<WireSegment> getAdjacent(){
		LinkedList<WireSegment> out = new LinkedList<WireSegment>();
		for(Direction d: Direction.values()) {
			out.add(getAdjacent(d));
		}
		return out;
	}
	
	public WireSegment getParent() {
		return (WireSegment)parent;	
	}
	public void delete() {
		for (WireSegment w : getAdjacent()) {
			if(this.parent == w) {
				w.remove(this);
			} else if(w.parent == this) {
				this.remove(w);
			} else {
				if(w.isGate()) {
					((Gate)w).inputs.remove(this);
					((Gate)w).connections.remove(this);
				}else{
					w.connections.remove(this);
				}
				
			}
		}
		if(checkdisconnects) {
			potentialdisconnects.removeAll(this);
		}
	}
	
	public boolean isGate() {
		return false;
		
	}
	
	public void updateActive( LinkedList<WireSegment> current, LinkedList<WireSegment> next) {
		
		//search for lowest neighbor
		int min = Integer.MAX_VALUE;
		for(WireSegment adj : connections) {
			if(adj.getActive() < min) {
				min = adj.getActive();	
			}
		}
		//update wire state and queue updates for appropriate neighbors.
		if(getActive() < min) {
			setActive(WireSegment.WIRE_OFF);
			for(WireSegment output : connections) {
				if(output.getActive() != WireSegment.WIRE_OFF) current.add(output);
			}
		} else {
			setActive(min + 1);
			for(WireSegment output : connections) {
				if(output.getActive() == WireSegment.WIRE_OFF) current.add(output);
			}
		}
		for(Gate input : gates) {
			next.add(input);	
		}
	}
	
	
	public void updatePowered(){return;}
	
}
