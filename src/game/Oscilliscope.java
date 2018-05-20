package game;
import static core.MainProgram.*;
import static util.DB.*;

import java.util.ListIterator;

import async.ActiveAsyncEvent;
import elements.Container;
import elements.Element;
import util.LinkedList;


public class Oscilliscope extends Element{
	public static float HEIGHT = 30;
	public static float WIDTH = -EPSILON;
	
	public static int DEFAULT_CAPACITY = 50;
	//queue
	public int capacity = 50; 
	public int size = 0;
	private LinkedList<Boolean> data;
	
	//monitor
	public WireSegment probe;
	private float datawidth;
	
	// 
	private boolean stop = false;
	
	public boolean stopped = true;
	
	public Oscilliscope(WireSegment w, Container p) {
		super(0,0,WIDTH,HEIGHT,p);
		data = new LinkedList<Boolean>();
		datawidth = getWidth()/(capacity-1);
		
	}
	
	public void setProbe(WireSegment w) {
		probe = w;
		reset();
	}
	

	public void reset() {
		data.clear();
		requestUpdate();
	}
	public void stop() {
		//reset upon stopping
		stop = false;
	}
	public void start() {
		GameArea.tickscheduler.call(new RepeatUpdateData());
		
	}
	
	@Override
	protected void update() {
		resetGraphics();
		//old paper colour
		g.background(255,255,240);
		ListIterator<Boolean> iter = data.iterator();
		for(int i = 0; i<capacity; i++) {
			g.fill(iter.next()?0:200);
			g.rect(i*datawidth, HEIGHT/2-4, (i+1)*datawidth, HEIGHT/2+4);
		}
	}
	public class RepeatUpdateData extends ActiveAsyncEvent {
		int nextupdate;
		int period = 100; 
		
		
		public void run() {
			if(p3.millis()>nextupdate) {
				data.addFirst(probe.isActive());
				size++;
				if(size >= capacity) {
					data.removeLast();
					size--;
				}
				nextupdate += period;
				requestUpdate();
			}
		}

		@Override
		public void start() {
			nextupdate = p3.millis();
		}

		@Override
		public boolean condition() {
			if(!exists) return false;
			if(stop) {
				stop = true;
				return false;
			}
			return true;
		}
		
		
	}
	
	
	
	
	
			
}
