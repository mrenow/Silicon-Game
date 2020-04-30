package game;
import static core.MainProgram.*;
import static util.DB.*;

import java.io.Serializable;
import java.util.ListIterator;

import async.ActiveAsyncEvent;
import elements.Container;
import elements.Element;
import util.LLinkedList;


public class Oscilloscope extends Element implements Serializable{
	private static final long serialVersionUID = 1L;
	
	public static float HEIGHT = 30;

	
	public static int DEFAULT_CAPACITY = 50;
	
	public GameArea game;
	
	//queue
	public int capacity = 50; 
	public int size = 0;
	private LLinkedList<Boolean> data;
	
	//monitor
	public WireSegment probe;
	public int x;
	public int y;	
	private float datawidth;
	
	// Stop request
	private boolean requeststop = false;
	
	public boolean stopped = true;
	
	public int id = 1;
	
	public Oscilloscope(int x, int y, DataDisplay p) {
		super(0,0,p.getWidth(),HEIGHT,p);
		this.x = x;
		this.y = y;
		game = p.game;
		data = new LLinkedList<Boolean>();
		datawidth = (getWidth()- 100)/(capacity-1);
	}
	
	
	public void updateProbe() {
		probe = game.getObjectAt(x,y,WireSegment.METAL_LAYER);
		if(probe == null) probe = game.getObjectAt(x, y, WireSegment.SILICON_LAYER);
		requestUpdate();
	}
	public boolean hasProbe() {
		return probe != null;
	}
	

	public void reset() {
		data.clear();
		size = 0;
		requestUpdate();
	}
	
	public void stop() {
		// rest is performed by async thread
		requeststop = true;
	}
	
	public void start() {
		updateProbe();
		GameArea.tickscheduler.call(new RepeatUpdateData()); 
	}
	
	public boolean getData() {
		if(hasProbe()) {
			return probe.isActive();
		}
		return false;
	}
	
	@Override
	protected void update() {
		resetGraphics();
		//old paper colour
		g.background(255,255,210);
		//make border
		g.noFill();
		g.strokeWeight(3);
		g.stroke(195,195,160);
		g.rect(0, 0, getWidth(), getHeight());
		
		
		//draw data rects
		g.noStroke();
		g.fill(0);
		ListIterator<Boolean> iter = data.iterator();
		
		for(int i = 0; iter.hasNext(); i++) {
			if(iter.next()) {
				g.rect(100 + i*datawidth, getHeight()/2-5, datawidth, 10);
			}
		}
		g.stroke(200,200,200,50);
		for(int i = 0; i< capacity; i++) {
			g.line(100+ i*datawidth, 0, 100+ i*datawidth, getHeight());
		}
		
		
		//Display num
		g.textAlign(CENTER,CENTER);
		g.text(id, getHeight()/2,getHeight()/2);
		
		
		
	}
	
	public class RepeatUpdateData extends ActiveAsyncEvent {
		
		@Override
		public void run() {
			// Records the value at probe destination.
			data.addFirst(getData());
			size++;
			
			// Take off excess elements
			if(size >= capacity) {
				data.removeLast();
				size--;
			}
			requestUpdate();
		}

		@Override
		public boolean condition() {
			if(!exists) return false;
			if(!game.running) {
				requeststop = false;
				return false;
			}
			if(requeststop) {
				reset();
				return false;
			}
			return true;
		}
	}
}
