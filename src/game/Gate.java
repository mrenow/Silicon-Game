package game;


// An input connection triggers a gate, affecting output connections.
public class Gate extends WireSegment{
	
	
	
	public Gate(int x, int y) {
		super(x,y);
		parent = this;
	}
	
	
	

}
