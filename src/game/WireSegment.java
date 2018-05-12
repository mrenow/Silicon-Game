package game;

import static core.MainProgram.*;
import static util.DB.*;

import java.util.ListIterator;

import processing.core.PVector;
import util.DisjointSet;
import util.LinkedList;
import util.Pointer;
import util.SparseQuadTree;

public class WireSegment extends DisjointSet {
	
	public static SparseQuadTree<WireSegment> container;
	
	
	public static final byte METAL = 0;
	
	public static final byte N_TYPE = 1;
	public static final byte P_TYPE = 2;
	
	public static final byte N_GATE = 3;
	public static final byte P_GATE = 4;

	public static final byte VIA = 5;
	
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
	Pointer<Integer> active;
	
	

	public WireSegment(byte mode, int x, int y) {
		this.mode = mode;
		this.x = x;
		this.y = y;
		this.active = new Pointer<Integer>(Integer.MAX_VALUE); 
		this.gates = new LinkedList<Gate>();
		this.connections = new LinkedList<WireSegment>();
		this.parent = this;
		

		// oinfo = new ObjectInfo(x, y);
	}
	
	
	
	protected void clearInfo() {
		gates.clear();
		connections.clear();
		active.val = Integer.MAX_VALUE;
	}

	// this becomes parent to that
	public void addInfo(DisjointSet that) {
		
		DB_U("added",that,"->",this);
		
		if(this == that) {
			DB_E("INFO OF THIS ADDED TO THIS IN", this, ", PANIC");
		}
		WireSegment child = (WireSegment) that;
		
		this.gates.add(child.gates);
		this.connections.add(child.connections);
		child.active = this.active;
		
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
		
		if(child.gates != null && !this.gates.empty()) {
		
		}
		if(child.connections != null && !this.connections.empty()) {
			
		}
	
	}
	
	public void updateSetInfo() {
		for(WireSegment w: getAdjacent()) {
			if(w != parent && !this.isSameSet(w)) {
				w.parent = this;
			}			
		}
	}
	

	public boolean isActive() {
		return active.val != Integer.MAX_VALUE;
	}
	
	public boolean canConnect(WireSegment that) {
		return (this.mode == that.mode) != (this.mode == VIA || that.mode == VIA);
	}
	public void updateConnections() {
		println(getAdjacent());
		ListIterator<WireSegment> iter = new LinkedList<WireSegment>(getAdjacent()).iterator();
		WireSegment w;
		while(iter.hasNext()) {
			w = iter.next();
			if(canConnect(w) && !isSameSet(w)) {
				w.union(this);
				break;
			}
		}
		while(iter.hasNext()) {
			w = iter.next();
			if(canConnect(w) && !isSameSet(w)){
				w.makeAncestor(this);
			}			
			
		}	
		DB_U("Connections Updateded");
	}
	public String toString() {
		switch(mode) {
		case  METAL: return "m";
		case P_TYPE: return "p";
		case N_TYPE: return "n";
		case P_GATE: return "P";
		case N_GATE: return "N"; 
		case    VIA: return "v";
		}
		return "";
		
		
	}
	public LinkedList<WireSegment> getAdjacent(Direction d) {
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
	
	
	
}
