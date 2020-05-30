package game;

import util.LLinkedList;
import static util.DB.*;
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
	
	public void toggle(LLinkedList<WireSegment> next) {
		if(!requesttoggle) {
			requesttoggle = true;
			next.add(this);
		}
	}
	public void toggle(LLinkedList<WireSegment> next,boolean val) {
		if(toggled != val) {
			toggle(next);
		}
	}
	
	
	@Override
	public void updateConnections() {
		// P gates receive input from P and connect to N
		connections.clear();
	
		for(WireSegment w : getAdjacent()) {
			if(canMakeConnection(w)) {
				w.connections.add(this);
				connections.add(w);
			}
		}
	}
	public void updatePowered() {
		if(requesttoggle) {
			requesttoggle = false;
			toggled = !toggled;
		}
	}
	public boolean isToggled() {
		return toggled;
	}
	
	@Override
	public void updateActive(LLinkedList<WireSegment> current, LLinkedList<WireSegment> next) {
		if(toggled) {
			setActive(Integer.MIN_VALUE);
		}else {
			setActive(WIRE_OFF);
		}
		
		//update wire state and queue updates for appropriate neighbors.
		if(toggled) {
			for(WireSegment connection : connections) {
				connection = (WireSegment)connection.getAncestor();
				if(!connection.isActive() && connection.isPermissive() && !connection.updatablecurrent) {
					current.add(connection);
					connection.updatablecurrent = true;
				}
			}
		} else {
			for(WireSegment connection : connections) {
				connection = (WireSegment)connection.getAncestor();
				if(connection.isActive() && connection.isPermissive() && !connection.updatablecurrent) {
					current.add(connection);
					connection.updatablecurrent = true;
				}
			}
		}
		updatablecurrent = false;
		// No gate updates as power is a metal type and cannot connect to gates.
		
	}
	
	public boolean isPermissive() {
		return false;	
	}
	
	
	
	
	
}
