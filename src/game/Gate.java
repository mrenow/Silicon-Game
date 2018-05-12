package game;
import static core.MainProgram.*;
import static util.DB.*;
import util.LinkedList;




// An input connection triggers a gate, affecting output connections.
// gates can only exist as single element sets. 
//
public class Gate extends WireSegment{
	
	
	LinkedList<WireSegment> inputs;
	
	
	public Gate(byte mode, int x, int y) {
		super(mode, x,y);
		parent = this;
	}
	
	public boolean isPermissive() {
		//N gate becomes permissive when powered
		for(WireSegment w : inputs) {
			if(w.active != WIRE_OFF) return mode == N_GATE;
		}
		return mode == P_GATE;
	}
	

	
	
	

}
