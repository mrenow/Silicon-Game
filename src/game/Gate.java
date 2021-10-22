package game;
import static core.MainProgram.*;
import static util.DB.*;

import util.Heap;
import util.LLinkedList;




// An input connection triggers a gate, affecting output connections.
// gates can only exist as single element sets. 
//
public class Gate extends WireSegment{
	
	LLinkedList<WireSegment> inputs;
	boolean permissive;
	
	public Gate(byte mode, int x, int y) {
		super(mode, x,y);
		inputs = new LLinkedList<WireSegment>();
		permissive = (mode == P_GATE);
	}
	
	// Checks if a wiresegment state is valid.
	@Override
	public boolean updateActive() {
		
		if (!permissive) {
			if(active != WIRE_OFF) {
				active = WIRE_OFF;
				return true;
			}else {
				return false;		
			}
		}
		return super.updateActive();
	}
	@Override
	public boolean isPermissive() {
		//N gate becomes permissive when powered
		return permissive;
	}

	@Override
	public boolean updatePowered() {
		boolean newpermissive = (mode == N_GATE) == getPowered();
		if(permissive == newpermissive) return false;
		permissive = newpermissive;
		return true;
	}
	
	
	//gates act as their own segments, their parent is always themselves.
	@Override
	public void updateConnections() {
		// P gates receive input from P and connect to N
		inputs.clear();
		connections.clear();
	
		for(WireSegment w : getAdjacent()) {

			
			// silicon connections
			if(canMakeConnection(w)) {
				w.connections.add(this);
				connections.add(w);
			} 
			//gate input
			if(canMakeInput(w)) {
				w.gates.add(this);
				inputs.add(w);					
			}
			if(canMakeGate(w)) {
				gates.add((Gate)w);
				((Gate)w).inputs.add(this);						
			}
		}
	}

	


	
	
	
	public void delete() {
		for (WireSegment w : getAdjacent()) {
			w.connections.remove(this);
			w.gates.remove(this);
		}
		if(checkdisconnects) {
			potentialdisconnects.removeAll(this);
		}
	}
	

	
	public boolean powered() {
		return (mode == N_GATE) == permissive;
	}
	

	public boolean getPowered() {
		for(WireSegment w : inputs) {
			if(w.isOn()) return true;
		}
		return false;
	}
	@Override
	public boolean isGate() {
		return true;
	}
	
}
