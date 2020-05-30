package game;

import static core.MainProgram.*;
import static util.DB.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.ListIterator;

import processing.core.PVector;
import util.DisjointSet;
import util.LLinkedList;
import util.SparseQuadTree;

public class WireSegment extends DisjointSet implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	//assigned depending on gameArea context.
	public static SparseQuadTree<WireSegment> container;
	public static LLinkedList<WireSegment> potentialdisconnects = new LLinkedList<WireSegment>(); 
	
	public static final byte METAL = 0;
	public static final byte N_TYPE = 1;
	public static final byte P_TYPE = 2;
	public static final byte VIA = 3;
	public static final byte N_GATE = 4;
	public static final byte P_GATE = 5;
	public static final byte POWER = 6;

	public static final byte[] SILICON_LAYER = {P_TYPE,N_TYPE,N_GATE,P_GATE};
	public static final byte[] METAL_LAYER = {METAL,POWER};	
	public static final byte[] VIA_LAYER = {VIA};	
	

	
	// individual info
	
	byte mode;

	int x;
	int y;
	
	//order 1 check for membership of lists.
	public boolean updatablecurrent;
	public boolean updatablenext;
	public boolean isupdating = false;
	
	public static int WIRE_OFF = Integer.MAX_VALUE;

	// Set Info
	public LLinkedList<Gate> gates;
	public LLinkedList<WireSegment> connections;
	// Priority value for signal propagation logic
	protected int active = WIRE_OFF;
	
	public boolean checkdisconnects = false;
	public int getActive(){
		return ((WireSegment)getAncestor()).active;
	}
	public void setActive(int val) {
		((WireSegment)getAncestor()).active = val;
	}
	
	public WireSegment(byte mode, int x, int y) {
		this.mode = mode;
		this.x = x;
		this.y = y;
		this.gates = new LLinkedList<Gate>();
		this.connections = new LLinkedList<WireSegment>();

		// oinfo = new ObjectInfo(x, y);
	}
	
	
	
	protected void clearInfo() {
		/*gates.clear();
		connections.clear();
		active = WIRE_OFF;
		*/
		for (WireSegment w : getAdjacent()) {
			remove(w);
		}
	}
	
	
	
	

	// this becomes parent to that
	public void addInfo(DisjointSet that) {
		
		DB_U("added",that,"->",this);
		
		if(this == that) {
			DB_E("INFO OF THIS ADDED TO THIS IN", this, ", PANIC");
			return;
		}
		WireSegment child = (WireSegment) that;
		this.gates.add(child.gates);
		this.connections.add(child.connections);
		
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
	public void subInfo(DisjointSet that) {
		WireSegment child = (WireSegment) that;
		if(!gates.contains(child.gates)) {
			DB_E("CHILD DID NOT CONTAIN EXPECTED INFORMATION", x, y);
		} else {
			this.gates.remove(child.gates);
			this.connections.remove(child.connections);
			this.active = WIRE_OFF;
		}
		
	}

	public boolean isActive() {
		return getActive() != Integer.MAX_VALUE;
	}
	// Wire is always permissive
	public boolean isPermissive() {
		return true;
		
	}
	
	public boolean canConnect(WireSegment that) {
		if(this.x == that.x && this.y == that.y)
			return (this.mode == VIA || that.mode == VIA);
		return (this.mode == that.mode && !(this.mode == VIA || that.mode == VIA));
	}
	
	public void updateConnections() {
		ListIterator<WireSegment> iter = new LLinkedList<WireSegment>(getAdjacent()).iterator();
		WireSegment w;
		connections.clear();
		gates.clear();
		
		while(iter.hasNext()) {
			w = iter.next();
			// Connection between two wire segments
			if(w instanceof Gate || w instanceof Power) {
				
				//connection
				if(canMakeConnection(w)) {
					connections.add(w);
					w.connections.add(this);
				}
				//gate input
				if(canMakeGate(w)) {
					gates.add((Gate)w);
					((Gate)w).inputs.add(this);	
				}
			}
			// take union of wire segments
			else if(canConnect(w)) {
				if(!isSameSet(w)) {
					// Add so that [w] inherits the set that [this] belongs to.
					union(w);
				} else {
					// adjacent wires which are not directly connected by a parent-child relation
					// are possible spots for future disconnects. These will be inspected
					// and fixed before the next time the circuit is run.
					potentialdisconnects.addFirst(w);
					w.checkdisconnects = true;
					potentialdisconnects.addFirst(this);
					checkdisconnects = true;
				}
			}
		}
		DB_U("Connections Updateded");
	}
	
	public boolean canMakeConnection(WireSegment that) {

		if((that.mode == VIA || this.mode == VIA)) {
		//Via on block
			return this.x == that.x && this.y == that.y;
		}
		//reduces type to P or N
		int this_mode = this.mode%3;
		int that_mode = that.mode%3;
		
		return this_mode == that_mode;
	}
	public boolean canMakeInput(WireSegment that) {
		if(that instanceof Gate ) return false;
		int this_mode = this.mode%3;
		int that_mode = that.mode%3;
		
		if(this_mode == METAL || that_mode == METAL) {
			return false;	
		}
		return (this_mode != that_mode);
	}
	
	public boolean canMakeGate(WireSegment that) {
		if(!(that instanceof Gate) || (this instanceof Gate)) return false;
		
		int this_mode = this.mode%3;
		int that_mode = that.mode%3;
		
		if(this_mode == METAL || that_mode == METAL) {
			return false;	
		}
		
		return this_mode != that_mode;
	}
	
	
	
	
	public String toString() {
		StringBuilder s = new StringBuilder() ;
	
		switch(mode) {
		case  METAL: s.append('m'); break;
		case P_TYPE: s.append('p'); break;
		case N_TYPE: s.append('n'); break;
		case P_GATE: s.append('P'); break;
		case N_GATE: s.append('N'); break; 
		case    VIA: s.append('v'); break;
		case  POWER: s.append('W'); break;
		}
		s.append('{');
		s.append(x);
		s.append(',');
		s.append(y);
		s.append('}');
		return s.toString();
	}
	private LLinkedList<WireSegment> getAdjacent(Direction d) {
		
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
			LLinkedList<WireSegment> l = container.get(x, y);
			l.remove(this);
			return l;
		}
		return null;
	}
	
	public LLinkedList<WireSegment> getAdjacent(){
		LLinkedList<WireSegment> out = new LLinkedList<WireSegment>();
		for(Direction d: Direction.values()) {
			out.add(getAdjacent(d));
		}
		return out;
	}
	
	public void delete() {
		for (WireSegment w : getAdjacent()) {
			if(w.remove(this)) {
			} else if(this.remove(w)) {
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
	
	public void updateActive( LLinkedList<WireSegment> current, LLinkedList<WireSegment> next) {
		isupdating = true;
		try {
			Thread.sleep((long) 500);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		DB_ASSERT(isAncestor(),true);
		//search for lowest neighbor
		int min = WIRE_OFF;
		for(WireSegment adj : connections) {
			if(adj.getActive() < min) {
				min = adj.getActive();	
			}
		}
		//update wire state and queue updates for appropriate neighbors.
		// Wire is the strongest power
		if(getActive() <= min || min == WIRE_OFF) {
			setActive(WIRE_OFF);
			// for each adjacent
			for(WireSegment output : connections) {
				// if adjacent is off/ cant be connected to, dont care
				// If adjacent is already part of an update list, also dont care/
				output = (WireSegment)output.getAncestor();
				if(output.getActive() != WIRE_OFF && output.isPermissive() && !output.updatablecurrent) {
					current.add(output);
					output.updatablecurrent = true;
				}
			}
		} else {
			// There is a wire stronger than this: make it one weaker than the current strongest
			setActive(min + 1);
			for(WireSegment output : connections) {
				output = (WireSegment)output.getAncestor();
				// If adjacent wire is on, then we dont need to worry about updates.
				if(output.getActive() == WIRE_OFF && output.isPermissive() && !output.updatablecurrent) {
					current.add(output);
					output.updatablecurrent = true;
				}
			}
		}
		for(Gate input : gates) {
			if(!input.updatablenext) {
				next.add(input); // Input's ancestor is always itself.
				input.updatablenext = true;
			}
		}
		
		updatablecurrent = false;
		isupdating = false;
	}
	
	
	public void updatePowered(){return;}
	
	public static String modeToString(byte mode) {
		switch(mode) {
		case WireSegment.METAL:
			return "METAL";
		case WireSegment.N_TYPE:
			return "N_TYPE";
		case WireSegment.P_TYPE:
			return "P_TYPE";
		case WireSegment.VIA:
			return "VIA";
		case WireSegment.N_GATE:
			return "N_GATE";
		case WireSegment.P_GATE:
			return "P_GATE";
		case WireSegment.POWER:
			return "POWER";
		default:
			return "INVALID";
		}
	}
	public String modeToString() {
		return modeToString(mode);
	}
	public static byte[] getLayer(byte mode) {
		switch(mode) {
		case WireSegment.N_TYPE:
		case WireSegment.P_TYPE:
		case WireSegment.N_GATE:
		case WireSegment.P_GATE:
			return SILICON_LAYER;
		case WireSegment.METAL:
		case WireSegment.POWER:
			return METAL_LAYER;
		case WireSegment.VIA:
			return VIA_LAYER;
		}
		return null;
	}
	public byte[] getLayer() {
		return getLayer(mode);
	}
}
