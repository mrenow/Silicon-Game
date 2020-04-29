package game;

import static core.MainProgram.*;
import static util.DB.*;
import util.LLinkedList;
import elements.Container;
import elements.ScrollPane;


public class DataDisplay extends ScrollPane{

	GameArea game;

	final static int MAX_SCOPES = 20;
	private int numscopes = 0;
	public LLinkedList<Oscilloscope> scopes;
	public DataDisplay(float x, float y, float w, float h, Container p) {
		this(x, y, w, h, p, new LLinkedList<Oscilloscope>());
	}
	public DataDisplay(float x, float y, float w, float h, Container p, LLinkedList<Oscilloscope> loaded_scopes) {
		super(x, y, w, h, ScrollPane.SCROLL_Y, p);
		scopes = loaded_scopes;
	}
	
	public void addScope(int x, int y) {
		if(numscopes < MAX_SCOPES) {
			scopes.add(new Oscilloscope(x,y,this));
			numscopes++;
			updateScopes();
		} else {
			
			
		}
	}
	
	public void removeScope(Oscilloscope o) {
		if(scopes.remove(o)) {
			this.remove(o);
			numscopes--;
			updateScopes();
		}
	}
	//stacks scopes one on top of another.
	public void updateScopes() {
		float y = 0;
		int i = 1;
		for(Oscilloscope o : scopes) {
			o.id = i;
			o.setPos(0, y);
			y += o.getHeight();
			i++;
		}
		setPaneHeight(y);
		requestUpdate();
	}
	// Turn all scopes on
	// Scopes will turn themselves off when the circuit stops.
	public void activate() {
		for(Oscilloscope o : scopes) {
			o.start();
		}
	}
	public void updateProbes() {
		for(Oscilloscope o : scopes) {
			o.updateProbe();
		}
	}
}
