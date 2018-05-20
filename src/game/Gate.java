package game;
import static core.MainProgram.*;
import static util.DB.*;
import util.LinkedList;




// An input connection triggers a gate, affecting output connections.
// gates can only exist as single element sets. 
//
public class Gate extends WireSegment{
	
	
	LinkedList<WireSegment> inputs;
	
	boolean permissive = (mode == N_GATE);
	
	public Gate(byte mode, int x, int y) {
		super(mode, x,y);
		inputs = new LinkedList<WireSegment>();
	}
	
	public boolean isPermissive() {
		//N gate becomes permissive when powered
		return permissive;
	}
	
	public void updatePowered() {
		permissive = (mode == N_GATE) == powered();
	}
	
	
	//gates act as their own segments, their parent is always themselves.
	@Override
	public void updateConnections() {
		// P gates receive input from P and connect to N
		inputs.clear();
		connections.clear();
	
		for(WireSegment w : getAdjacent()) {
			if((mode == N_GATE && w.mode == P_TYPE) || (mode == P_GATE && w.mode == N_TYPE)) {
				w.connections.add(this);
				connections.add(w);
			} 
			//gate input
			if((mode == P_GATE && w.mode == P_TYPE) || (mode == N_GATE && w.mode == N_TYPE)) {
				w.gates.add(this);
				inputs.add(w);					
			}
			/*
			if((mode == N_GATE && w.mode%3 == P_TYPE) || (mode == P_GATE && w.mode%3 == N_TYPE)) {
				w.connections.add(this);
				connections.add(w);
			}
			//gate input
			if((mode == P_GATE && w.mode%3 == P_TYPE) || (mode == N_GATE && w.mode%3 == N_TYPE)) {
				w.gates.add(this);
				inputs.add(w);					
			}*/
		}
	}
	public void delete() {
		for (WireSegment w : getAdjacent()) {
			if(!isGate()) {
				w.connections.remove(this);
				w.gates.remove(this);
			}
		}
		if(checkdisconnects) {
			potentialdisconnects.removeAll(this);
		}
	}
	
	public void updateActive(LinkedList<WireSegment> current, LinkedList<WireSegment> next) {

		//if segment is gate, do not permit flow based on gate type
		if(!isPermissive()) 
		super.updateActive(current,next);
		
	}
	
	
	

	public boolean powered() {
		for(WireSegment w : inputs) {
			if(w.getActive() != WIRE_OFF) return true;
		}
		return false;
	}
	@Override
	public boolean isGate() {
		return true;
	}
	
}
