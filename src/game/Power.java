package game;

import util.LinkedList;

/* A non connectable wire that has modifeid conductance properties
 * only updates are permitte`d through external toggle, throught the 
 * toggle() function.
 * On update will update its neighbors.
 * update cannot ever change the state of the wire.
 */ 
public class Power extends WireSegment{
	
	
	private boolean requesttoggle = false;
	public boolean toggled = false;
	
	
	public Power(int x, int y) {
		super(POWER,x,y);
	}
	
	public void toggle() {
		requesttoggle = true;
	}
	
	
	@Override
	public void updateConnections() {
		// P gates receive input from P and connect to N
		connections.clear();
	
		for(WireSegment w : getAdjacent()) {
			if(w.mode == METAL) {
				w.connections.add(this);
				connections.add(w);
			}
		}
	}
	public void updatePowered() {
		if(requesttoggle) {
			requesttoggle = false;
			if(active == 0) {
				active = WIRE_OFF;
			}else {
				active = 0;
			}
		}
	}
	
	public void updateActive(LinkedList<WireSegment> current, LinkedList<WireSegment> next) {
		
		if(toggled) {
			setActive(Integer.MIN_VALUE);
		}
		
		//update wire state and queue updates for appropriate neighbors.
		if(toggled) {
			for(WireSegment output : connections) {
				if(output.getActive() == WireSegment.WIRE_OFF) current.add(output);
			}
		} else {
			for(WireSegment output : connections) {
				if(output.getActive() != WireSegment.WIRE_OFF) current.add(output);
			}
		}
		// No gate updates as power is a metal type and cannot connect to gates.
		
	}
	
	
	
	
	
	
}
