package game;

import static core.MainProgram.*;
import static util.DB.*;
import elements.Container;
import elements.GridContainer;
import elements.MapNavigator;
import events.ClickEvents;
import events.ClickListener;
import events.KeyEvents;
import events.KeyListener;
import events.MovementEvents;
import events.MovementListener;
import events.ScrollEvents;
import events.ScrollListener;
import processing.core.PVector;
import util.LinkedList;
import util.SparseQuadTree;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.ListIterator;

import org.omg.PortableInterceptor.ACTIVE;

import async.ActiveAsyncEvent;
import async.AsyncEvent;
import async.Scheduler;

/* Contains the building grid
 * An x,y scrollable and zoomable pane which will have a set size. 
 * Does not reside in a scroll pane due to the need for drawing optimizations.
 * 
 */
public class GameArea extends MapNavigator implements KeyListener, ClickListener, MovementListener, ScrollListener {
	
	public static Scheduler tickscheduler = new Scheduler();
	
	final static float MAX_SCALE = 500;
	final static float MIN_SCALE = 1;

	final static int VK_SILICON = KeyEvents.VK_SHIFT;
	final static int VK_METAL   = KeyEvents.VK_CONTROL;
	final static int VK_VIA     = KeyEvents.VK_SPACE;
	
	final static int VK_DRAG   = KeyEvents.VK_CONTROL;
	final static int VK_SELECT = KeyEvents.VK_S;
	final static int VK_DELETE = KeyEvents.VK_D;

	// ctrl + commands
	final static int VK_CTRL_UNDO  = KeyEvents.VK_Z;
	final static int VK_CTRL_REDO  = KeyEvents.VK_Y;
	final static int VK_CTRL_COPY  = KeyEvents.VK_C;
	final static int VK_CTRL_PASTE = KeyEvents.VK_V;
	
	final static byte MAKE_SILICON = 0;
	final static byte MAKE_METAL   = 1;
	final static byte MAKE_VIA     = 2;
	final static byte MAKE_POWER   = 3;
	
	byte makemode =  MAKE_SILICON;

	int dimx, dimy;

	public SparseQuadTree<WireSegment> tiles;
	public LinkedList<Power> sources;
	ArrayList<Conductor> pendingupdate = new ArrayList<Conductor>();

	public GameArea(float x, float y, float w, float h, int size, Container p) {
		super(x, y, w, h, p);
		tiles = new SparseQuadTree<WireSegment>(size);
		dimx = dimy = 1 << size;
		offset = new PVector(dimx / 2, dimy / 2);
		backgroundcolor = p3.color(230, 230, 230);
		zoomrate = 1.05f;

		zoom = 100;
		offset = new PVector(0, 0);
		
		println("tt",tiles.depth);
		WireSegment.container = tiles;

		setZoomBounds(1, 100);
		setOffsetBounds(-500, -500, 800, 800);

		// Add listeners
		KeyEvents.add(this);
		ScrollEvents.add(this);
		ClickEvents.add(this);
		MovementEvents.add(this);

	}

	protected void update() {
		super.update();
		g.pushMatrix();
		transformView();
		// Draw bounding box
		g.noFill();
		g.stroke(200);
		g.rect(0, 0, dimx, dimy);
		g.noStroke();		
		
		// Only loads objects within viewing pane
		// New linked list for optimization to remove start and end nodes.
		LinkedList<WireSegment> objects = new LinkedList<WireSegment>(tiles.get(floor(offset.x),
				ceil(offset.x + getWidth() / zoom), floor(offset.y), ceil(offset.y + getHeight() / zoom)));
		
		WireSegment w = null;		
		Gate g1 = null;
		// Draw N silicon
		ListIterator<WireSegment> witer = objects.iterator();
		ListIterator<Gate> giter;
		LinkedList<WireSegment> active = new LinkedList<WireSegment>();
		LinkedList<Gate> gates = new LinkedList<Gate>();
		g.fill(50, 0, 0);
		while (witer.hasNext()) {
			w = witer.next();
			if (w.mode == WireSegment.N_TYPE || w.mode == WireSegment.N_GATE) {
				if(w.mode == WireSegment.N_GATE) {
					gates.add((Gate)w);
				}
				if(w.isActive()) {
					active.add(w);
				} else {
					g.rect(w.x, w.y, 1, 1);
				}
				witer.remove();
			}
		}

		
		g.fill(140,0,0);
		for (WireSegment w1: active) {
			g.rect(w1.x, w1.y, 1, 1);
		}
		active.clear();
		
		//active gates
		giter = gates.iterator();
		g.fill(220,220,0);
		while(giter.hasNext()) {
			g1 = giter.next();
			if(g1.powered()) {
				g.rect(g1.x + 0.1f,g1.y+0.1f,0.8f,0.8f);
				giter.remove();
			}
		}
		// inactive gates
		g.fill(100,100,0);
		for (Gate g2: gates) {
			g.rect(g2.x + 0.1f,g2.y+0.1f,0.8f,0.8f);
		}
		gates.clear();
		
		
		
		
		
		// Draw P silicon
		witer = objects.iterator();
		g.fill(100, 100, 0);
		while (witer.hasNext()) {
			w = witer.next();
			if (w.mode == WireSegment.P_TYPE || w.mode == WireSegment.P_GATE) {
				if(w.mode == WireSegment.P_GATE) {
					gates.add((Gate)w);
				}
				if(w.isActive()) {
					active.add(w);
				} else {
					g.rect(w.x, w.y, 1, 1);
				}
				witer.remove();
			}
		}
		
		g.fill(220,220,0);
		for (WireSegment w1: active) {
			g.rect(w1.x, w1.y, 1, 1);
		}
		active.clear();
		
		
		
		//active gates
		giter = gates.iterator();
		g.fill(140,0,0);
		while(giter.hasNext()) {
			g1 = giter.next();
			if(g1.powered()) {
				g.rect(g1.x + 0.1f,g1.y+0.1f,0.8f,0.8f);
				giter.remove();
			}
		}
		// inactive gates
		g.fill(50,0,0);
		for (Gate g2: gates) {
			g.rect(g2.x + 0.1f,g2.y+0.1f,0.8f,0.8f);
		}
		gates.clear();
		
		// Draw metal
		witer = objects.iterator();
		g.fill(100, 100, 100, 140);
		while (witer.hasNext()) {
			w = witer.next();
			if (w.mode == WireSegment.METAL) {
				if(w.isActive()) {
					active.add(w);
				} else {
					g.rect(w.x, w.y, 1, 1);
				}
				witer.remove();
			}
		}
		
		// Active Metal
		g.fill(200,200,200,140);
		for(WireSegment w1 : active) {
			g.rect(w1.x, w1.y, 1, 1);
		}
		active.clear();
		

		
		// Draw Power
		g.stroke(200);
		g.strokeWeight(0.01f);
		witer = objects.iterator();
		g.fill(100, 100, 100, 140);
		while (witer.hasNext()) {
			w = witer.next();
			if(w.mode == WireSegment.POWER) {
				if(w.isActive()) {
					active.add(w);
				} else {
					g.rect(w.x, w.y, 1, 1);
				}
				witer.remove();
				
			}
			
		}
		// Active Power
		g.fill(200,200,200,140);
		for(WireSegment w1 : active) {
			g.rect(w1.x, w1.y, 1, 1);
		}
		active.clear();
		
		//draw vias
		witer = objects.iterator();
		g.noFill();
		while (witer.hasNext()) {
			w = witer.next();
			if (w.mode == WireSegment.VIA) {
				g.ellipse(w.x + 0.5f, w.y + 0.5f, 0.7f, 0.7f);
				witer.remove();
			}
		}
		
		// debug overlay indication direction of WireSegment chain
		// debug overlay for grid
		if(debug == 3) {
			g.stroke(0);
			for(WireSegment w1 : tiles.elements) {
				PVector v = new PVector(w1.getParent().x - w1.x,w1.getParent().y - w1.y);
				
				//draws arrow
				g.line(w1.x+0.5f, w1.y+0.5f, w1.getParent().x  + 0.5f,w1.getParent().y+0.5f);
				v.rotate(PI/6).mult(-0.3f);
				g.line(w1.getParent().x+0.5f,w1.getParent().y+0.5f,w1.getParent().x + v.x +0.5f, w1.getParent().y + v.y+0.5f);
				v.rotate(-PI/3);
				g.line(w1.getParent().x+0.5f,w1.getParent().y+0.5f,w1.getParent().x + v.x +0.5f, w1.getParent().y + v.y+0.5f);
				
			}
			g.fill(255,0,0);
			for (WireSegment w1 : WireSegment.potentialdisconnects) {
				g.ellipse(w1.x+0.5f, w1.y+0.5f, 0.3f,0.3f);
				
			}
			
			// draw grid markers
			g.popMatrix();
			g.strokeWeight(1);
			g.fill(0);
			g.rectMode(CENTER);
			g.textAlign(CENTER,CENTER);
			PVector topleft = localToMapPos(0,0);
			PVector bottomright = localToMapPos(getWidth(),getHeight());
			int linelength = 5;
			float x,y;
			for(int i = ceil(topleft.x); i< bottomright.x; i++) {
				x = mapToLocalX(i);
				
				g.line(x,0,x,linelength);
				if(i>=0) {
					g.text(Integer.toString(i),mapToLocalX(i + 0.5f),linelength);
				}
			}
			for(int i = ceil(topleft.y); i< bottomright.y; i++) {
				y = mapToLocalY (i);
				g.line(0,y,linelength, y);
				if(i >= 0) {
					g.text(Integer.toString(i),linelength,mapToLocalY(i + 0.5f));
				}
				
				
			}
			
			
		}
	}

	public void keyPressed() {
		if(KeyEvents.key[KeyEvents.VK_1]) {
			makemode = MAKE_SILICON;
		}else if (KeyEvents.key[KeyEvents.VK_2]){
			makemode = MAKE_METAL;	
		}else if (KeyEvents.key[KeyEvents.VK_3]){
			makemode = MAKE_VIA;
		}else if (KeyEvents.key[KeyEvents.VK_4]){
			makemode = MAKE_POWER;
		}
		
		if(KeyEvents.key[KeyEvents.VK_ENTER]) {
			rectifyMap();
		}

	}

	public void keyReleased() {

	}

	public void keyTyped() {

	}

	public void mouseMoved() {
		// if control pressed move screen.
		if (p3.mousePressed) {
			//if (KeyEvents.key[VK_DRAG]) {
			println(p3.mouseButton);
			if(p3.mouseButton == 3) {
				println("drag");
				addOffset(p3.pmouseX - p3.mouseX, p3.pmouseY - p3.mouseY);
			} else if (p3.mouseButton == LEFT || p3.mouseButton == RIGHT) {
				PVector gpos = getGlobalPos();
				println(gpos);
				drawLine(p3.mouseX - gpos.x, p3.mouseY - gpos.y, p3.pmouseX - gpos.x, p3.pmouseY - gpos.y);
			}
			requestUpdate();
		}
	}

	
	public void elementClicked() {
		if(p3.mouseButton != 3) {
			activateSquare(localToMapPos(p3.mouseX, p3.mouseY));
			requestUpdate();
		}
	}

	public void elementReleased() {

	}

	public void elementHovered() {

	}

	public void elementUnhovered() {

	}

	public void elementScrolled(int value) {
		addZoom(-value, p3.mouseX, p3.mouseY);
		requestUpdate();
	}

	private void drawLine(float x1, float y1, float x2, float y2) {
		globalscheduler.call(new RunDrawLine(x1,y1,x2,y2));

	}
	//connects squares in a manner similar to the update connections function
	private void rectifyMap() {
		ListIterator<WireSegment> disconnectiter = WireSegment.potentialdisconnects.iterator();
		WireSegment w1, w2;
		
		while(disconnectiter.hasNext()) {

			w1 = disconnectiter.next();
			ListIterator<WireSegment> iter = new LinkedList<WireSegment>(w1.getAdjacent()).iterator();
			//if the wire has a potential adjacent connection it is not directly connected to.
			boolean isvalid = false;
			while(iter.hasNext()) {
				w2 = iter.next();
			
				if(w1.canConnect(w2)) {
					if( !w1.isSameSet(w2)) { 
						DB_U("rectified", w2,w1);
						w1.makeAncestor(w2);
						WireSegment.potentialdisconnects.remove(w2);
						WireSegment.potentialdisconnects.remove(w1);
					}
					if(w1.getParent() != w2 && w2.getParent() != w1) {
						isvalid = true;
						
					}
				}
				
				//disconnectiter.remove();
				
			}	
			if(!isvalid) {
				WireSegment.potentialdisconnects.remove(w1);
			}
		}
		requestUpdate();
		
	}

	private void activateSquare(PVector v) {
		activateSquare(floor(v.x), floor(v.y));
	}

	private void activateSquare(int x, int y) {
		if (x < 0 || dimx <= x || y < 0 || dimy <= y) return;
		
		//globalscheduler.call(new RunActivateSquare(x,y));

		if (p3.mouseButton == RIGHT) {
			delete(x,y);
			return;
		}
		
		
		if(makemode == MAKE_POWER) {
			for(WireSegment w : tiles.get(x, y)) {
				if(w instanceof Power) {
					((Power)w).toggle();
				}
			}
		}
		
		if(!running) {
			switch(makemode) {
				case MAKE_VIA: 
					makeVia(x,y);
					break;
				case MAKE_METAL:
					makeMetal(x,y);
					break;
				case MAKE_POWER:
					// If already existing power, toggle it.
					
					makePower(x,y);
					
					break;
				case MAKE_SILICON:
					makeSilicon(KeyEvents.key[VK_SILICON] ? WireSegment.N_TYPE : WireSegment.P_TYPE, x, y);
					break;
			}
		}
		requestUpdate();
		
	}
	private void makeSilicon(byte mode , int x, int y) {
		DB_ASSERT(mode == WireSegment.N_TYPE || mode == WireSegment.P_TYPE,true);
		LinkedList<WireSegment> current = tiles.get(x, y);
		// If silicon already exists do nothing
		for (WireSegment w : current) {
			if ((w.mode == WireSegment.N_TYPE)||(w.mode == WireSegment.P_TYPE)) {
				if(w.mode != mode) { 
					tiles.remove(w, x, y);
					w.delete();
					println(tiles.get(x,y));
					makeGate((byte)(3+w.mode), x, y);
				}
				return;
			} else if (w.mode == WireSegment.N_GATE || w.mode == WireSegment.P_GATE){
				return;
			}
		}
		
		DB_U("Make", x, y);
		
		// If keyPressed change to P silicon
		WireSegment w;
		if (mode == WireSegment.N_TYPE) {
			tiles.add(w = new WireSegment(WireSegment.N_TYPE, x, y), x, y);
			w.updateConnections();
			
		} else if (mode == WireSegment.P_TYPE){
			
			tiles.add(w = new WireSegment(WireSegment.P_TYPE, x, y), x, y);
			w.updateConnections();
		}
	}

	private void makeMetal(int x, int y) {
		LinkedList<WireSegment> current = tiles.get(x, y);
		// If metal already exists do nothing
		for (WireSegment w : current) {
			if (w.mode == WireSegment.METAL || w.mode == WireSegment.POWER) return;	
		}
		DB_U("Make", x, y);
		
		WireSegment w;
		tiles.add(w = new WireSegment(WireSegment.METAL, x, y), x, y);
		w.updateConnections();
	}
	private void makeVia(int x, int y) {
		LinkedList<WireSegment> current = tiles.get(x, y);
		//only one via per square
		for (WireSegment w : current) {
			if (w.mode == WireSegment.VIA) return;	
		}
		DB_U("Make:",x,y);
		
		WireSegment w;
		tiles.add(w = new WireSegment(WireSegment.VIA, x, y), x, y);
		w.updateConnections();
	}
	
	//assumes area is already cleared
	private void makeGate(byte mode, int x, int y) {	
		DB_U("Make Gate:",x,y);
		Gate g;
		tiles.add(g = new Gate(mode,x,y),x, y);
		g.updateConnections();
	}
	private void makePower(int x, int y) {
		LinkedList<WireSegment> current = tiles.get(x, y);
		// If metal already exists do nothing
		for (WireSegment w : current) {
			if (w.mode == WireSegment.METAL || w.mode == WireSegment.POWER) return;	
		}
		DB_U("Make Power", x, y);
		
		Power p;
		tiles.add(p = new Power(x, y), x, y);
		p.updateConnections();
		sources.add(p);
	}
	
	
	
	

	private void delete(int x, int y) {
		LinkedList<WireSegment> current = tiles.get(x, y);
		for (WireSegment w : current) {
			if(((makemode == MAKE_VIA  ) == (w.mode == WireSegment.VIA  ))
	         &&((makemode == MAKE_METAL) == (w.mode == WireSegment.METAL))
	         &&((makemode == MAKE_POWER) == (w.mode == WireSegment.POWER))){
				if(w.mode == WireSegment.POWER) sources.remove((Power)w); 
				tiles.remove(w, x, y);
				w.delete();
			}	
		}
	}

	// index 0 contains input gates and index 1 contains output gates
	// 
	private LinkedList<Gate>[] addGates(WireSegment current) {
		LinkedList<Gate>[] out = new LinkedList[] { new LinkedList<Gate>(), new LinkedList<Gate>() };
		for (Direction d : Direction.values()) {
			for (WireSegment w : current.getAdjacent(d)) {
				if (w instanceof Gate) {
					Gate g = (Gate) w;
					if (g.mode == current.mode) {
						out[1].add(g);
					} else {
						out[0].add(g);
					}
				}
			}
		}
		return out;
	}	
	
	class RunActivateSquare extends AsyncEvent {
		
		int x; 
		int y;
		protected RunActivateSquare(int x, int y){
			this.x = x;
			this.y = y;
			
		}
		@Override
		protected void run() {
			activateSquare(x,y);
		}
		
	}
	
	class RunDrawLine extends AsyncEvent{
		float x1,y1,x2,y2;
		
		RunDrawLine(float x1,float y1,float x2,float y2){
			this.x1 = x1;
			this.y1 = y1;
			this.x2 = x2;
			this.y2 = y2;
		}
		@Override
		protected void run() {
			PVector pos1 = localToMapPos(x1, y1);
			PVector pos2 = localToMapPos(x2, y2);
			activateSquare(floor(pos1.x), floor(pos1.y));
			activateSquare(floor(pos2.x), floor(pos2.y));

			if (abs(pos2.x - pos1.x) > abs(pos2.y - pos1.y)) {
				if (pos2.x < pos1.x) {
					PVector c = pos2;
					pos2 = pos1;
					pos1 = c;
				}
				for (int x = floor(pos1.x)+1; x < floor(pos2.x)+1; x++) {
					float yf = (pos2.y - pos1.y) / (pos2.x - pos1.x) * (x - pos1.x) + pos1.y;
					println("nyah", pos1,pos2,yf,x);
					int y = floor(yf);
					activateSquare(x, y);
					activateSquare(x-1, y);
					
				}
			} else {
				if (pos2.y < pos1.y) {
					PVector c = pos2;
					pos2 = pos1;
					pos1 = c;
				}
				for (int y = floor(pos1.y)+1; y < floor(pos2.y)+1; y++) {
					float xf = (pos2.x - pos1.x) / (pos2.y - pos1.y) * (y - pos1.y) + pos1.x;
					println("nyeh", pos1,pos2,xf,y);
					int x = floor(xf);
					activateSquare(x, y);
					activateSquare(x, y-1);
				}
			}	
		}
	}
	
	
	
	/*
	 * LOGIC ENGINE
 	 */
	
	//to be updated in this tick
	private LinkedList<WireSegment> currentwireupdates;
	//to be updated next tick
	private LinkedList<WireSegment> nextwireupdates;
	
	
	// Thread states
	public boolean running = false;
	public boolean stoppable = false;
	
	// make new process
	public void run() {
		rectifyMap();
		if(!running) {
			tickscheduler.call(new RepeatIterate());
		}
	}
	public void stop() {
		if(running) {
			// Event will detect this and remove itself on next iteration.
			stoppable = true;
		}
		
	}
	// only step if paused 
	public void step() {
		if(!running) {
			// Iterate once only
			tickscheduler.call(new RunIterate());
		}
	}
	
	public float getSpeed() {
		return 1000f/tickscheduler.getPeriod();	
	}
	
	public void setSpeed(float tickspersecond) {
		tickspersecond = max(1,tickspersecond);
		tickscheduler.setPeriod(1000f/tickspersecond);
	}
	
	public void reset() {
		// Guarantees that all wires set to off in next iteration
		for(Power p : sources) {
			p.setActive(WireSegment.WIRE_OFF);
		}
		step();
		
		// Destroy thread
		stop();
	}
	
	
	
	// runs signal spreading logic
	private void iterate() {
		
		for (WireSegment w : nextwireupdates) {
			w.updatePowered();
			currentwireupdates.add(w);
		}
		nextwireupdates.clear();
		
		while(!currentwireupdates.empty()) {
			// Makes new iterator and continuously updates through queued updates
			ListIterator<WireSegment> iter = new LinkedList<WireSegment>(currentwireupdates).iterator();
			currentwireupdates.clear();
			while(iter.hasNext()) {
				//adds update requests to both queues.
				iter.next().updateActive(currentwireupdates, nextwireupdates);;
			}
		}
		requestUpdate();
	}
	


	private class RepeatIterate extends ActiveAsyncEvent {
		@Override
		protected void run() {
			iterate();	
		}
		protected void start() {
			running = true;
		}
		protected void stop() {
			// Stop may be requested once thread runs again.
			stoppable = false;
			running = false;
		}
		@Override
		protected boolean condition() {
			return exists && stoppable;
		}
	}
	
	// Iterates once
	private class RunIterate extends AsyncEvent {
		@Override
		protected void run() {
			iterate();
		}
	}
}

// only one silicon can be placed per tile
// only one conductor may be placed per tile
// vias may be placed anywhere
// gates are silicon, may have any number of connections and a base. Gates are
// not objects, they are just pieces of silicon
// which have been based by any number of silicons
//
