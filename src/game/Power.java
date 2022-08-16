package game;

import util.LLinkedList;
import static util.DB.*;

import util.Heap;
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

		if(requesttoggle) {
			next.remove(this);
		} else {
			next.add(this);
		}

		requesttoggle = !requesttoggle;
	}
	public void toggle(LLinkedList<WireSegment> next,boolean val) {
		if(toggled != val) {
			toggle(next);
		}
	}
	@Override
	public boolean isOn() {
		return toggled != requesttoggle;
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
	@Override
	public boolean updatePowered() {
		if(!requesttoggle) return false;
		requesttoggle = false;
		toggled = !toggled;
		return true;
	}
	public boolean isToggled() {
		return toggled;
	}
	

	@Override
	public boolean updateActive() {
		if((active == Integer.MIN_VALUE) == toggled) return false; 
		active = toggled? Integer.MIN_VALUE: WIRE_OFF;
		return true;
	}

	@Override
	public boolean isPermissive() {
		return false;	
	}
	
	
	
	
	
}
