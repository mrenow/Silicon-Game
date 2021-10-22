package game;

import static core.MainProgram.*;
import static util.DB.*;

import java.io.Serializable;
import java.util.ArrayList;

import util.LLinkedList;
import util.Pair;
import elements.Container;
import elements.ScrollPane;


public class DataDisplay extends ScrollPane{

	GameArea game;

	final static int MAX_SCOPES = 20;
	private int numscopes = 0;
	// For drawing 2-4-8-16 tick markers
	public int time = 0;
	
	public LLinkedList<Oscilloscope> scopes = new LLinkedList<Oscilloscope>();
	public DataDisplay(float x, float y, float w, float h, Container p, GameArea g) {
		super(x, y, w, h, ScrollPane.SCROLL_Y, p);
		game = g;
		g.display = this;
	}
	public DataDisplay(float x, float y, float w, float h, Container p, GameArea g, Object loadedscopes) {
		this(x, y, w, h, p, g);
		try {
			loadSave(loadedscopes);
		}catch(ClassCastException e) {
			DB_W(this, " Scopes not successfully loaded:", e.getMessage());
			scopes = new LLinkedList<Oscilloscope>();
		}
		
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
	// Collects data and writes to oscilliscopes.
	public void collectAll() {
		for(Oscilloscope o : scopes) {
			o.collect();
		}
		time++;
	}
	public void updateProbes() {
		for(Oscilloscope o : scopes) {
			o.updateProbe();
		}
	}
	public Object getSave() {
		ArrayList<ScopeData> data = new ArrayList<ScopeData>();
		for (Oscilloscope o: scopes) {
			data.add(new ScopeData(o.x, o.y));
		}
		return data;
	}
	private void loadSave(Object data) throws ClassCastException{
		for (ScopeData o: (ArrayList<ScopeData>)data) {
			addScope(o.x, o.y);
		}
	}

}
class ScopeData implements Serializable{
	private static final long serialVersionUID = 1L;
	int x,y;
	ScopeData(int x, int y){
		this.x = x;
		this.y = y;
	}
}
