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

/* Contains the building grid
 * An x,y scrollable and zoomable pane which will have a set size. 
 * Does not reside in a scroll pane due to the need for drawing optimizations.
 * 
 */
public class GameArea extends MapNavigator implements KeyListener, ClickListener, MovementListener, ScrollListener {

	final static float MAX_SCALE = 500;
	final static float MIN_SCALE = 1;

	final static int VK_SILICON = KeyEvents.VK_SHIFT;
	final static int VK_METAL = KeyEvents.VK_CONTROL;
	final static int VK_VIA = KeyEvents.VK_SPACE;
	
	final static int VK_DRAG = KeyEvents.VK_CONTROL;
	final static int VK_SELECT = KeyEvents.VK_S;
	final static int VK_DELETE = KeyEvents.VK_D;

	// ctrl + commands
	final static int VK_CTRL_UNDO = KeyEvents.VK_Z;
	final static int VK_CTRL_REDO = KeyEvents.VK_Y;
	final static int VK_CTRL_COPY = KeyEvents.VK_C;
	final static int VK_CTRL_PASTE = KeyEvents.VK_V;

	public SparseQuadTree<WireSegment> tiles;

	int dimx, dimy;

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
		
		// Draw N silicon
		ListIterator<WireSegment> iter = objects.iterator();
		LinkedList<WireSegment> active = new LinkedList<WireSegment>();
		g.fill(50, 0, 0);
		while (iter.hasNext()) {
			w = iter.next();
			if (w.mode == WireSegment.N_TYPE) {
				if(w.isActive()) {
					active.add(w);
				} else {
					g.rect(w.x, w.y, 1, 1);
				}
				iter.remove();
			}
		}
		
		g.fill(150,0,0);
		for(WireSegment w1 : active) {
			g.rect(w1.x, w1.y, 1, 1);
		}
		active.clear();
		
		// Draw P silicon
		iter = objects.iterator();
		g.fill(100, 100, 0);
		while (iter.hasNext()) {
			w = iter.next();
			if (w.mode == WireSegment.P_TYPE) {
				if(w.isActive()) {
					active.add(w);
				} else {
					g.rect(w.x, w.y, 1, 1);
				}
				iter.remove();
			}
		}
		
		g.fill(220,220,0);
		for(WireSegment w1 : active) {
			g.rect(w1.x, w1.y, 1, 1);
		}
		active.clear();
		
		// Draw metal
		iter = objects.iterator();
		g.fill(100, 100, 100, 140);
		while (iter.hasNext()) {
			w = iter.next();
			if (w.mode == WireSegment.METAL) {
				if(w.isActive()) {
					active.add(w);
				} else {
					g.rect(w.x, w.y, 1, 1);
				}
				iter.remove();
			}
		}
		

		g.fill(200,200,140);
		for(WireSegment w1 : active) {
			g.rect(w1.x, w1.y, 1, 1);
		}
		active.clear();
		
		//draw vias
		iter = objects.iterator();
		g.noFill();
		g.stroke(100);
		g.strokeWeight(0.01f);
		while (iter.hasNext()) {
			w = iter.next();
			if (w.mode == WireSegment.VIA) {
				g.ellipse(w.x + 0.5f, w.y + 0.5f, 0.7f, 0.7f);
				iter.remove();
			}
		}
		
		// debug overlay indication direction of WireSegment chain
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
		}
	}

	public void keyPressed() {

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

	private void activateSquare(PVector v) {
		activateSquare(floor(v.x), floor(v.y));
	}

	private void activateSquare(int x, int y) {
		if (x < 0 || dimx <= x || y < 0 || dimy <= y) return;
	
		if (p3.mouseButton == RIGHT) {
			delete(x,y);
			return;
		}
		if (KeyEvents.key[KeyEvents.VK_SPACE]) {
			makeVia(x,y);
			return;
		}

		if (KeyEvents.key[VK_METAL]) {
			makeMetal(x,y);
		} else  {
			makeSilicon(KeyEvents.key[VK_SILICON] ? WireSegment.N_TYPE : WireSegment.P_TYPE, x, y);
		}
	}
	private void makeSilicon(byte mode , int x, int y) {
		LinkedList<WireSegment> current = tiles.get(x, y);
		// If silicon already exists do nothing
		for (WireSegment w : current) {
			if (w.mode == WireSegment.P_TYPE || w.mode == WireSegment.N_TYPE) return;
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
			if (w.mode == WireSegment.METAL) return;	
		}
		DB_U("Make", x, y);
		
		WireSegment w;
		tiles.add(w = new WireSegment(WireSegment.METAL, x, y), x, y);
		w.updateConnections();
	}
	private void delete(int x, int y) {
		LinkedList<WireSegment> current = tiles.get(x, y);
		for (WireSegment w : current) {
			if((KeyEvents.key[VK_VIA] == (w.mode == WireSegment.VIA))
	       	&&(KeyEvents.key[VK_METAL] == (w.mode == WireSegment.METAL))){
				tiles.remove(w, x, y);	
			}	
		}
	}
	private void updateAdjacent(WireSegment w) {
		LinkedList<WireSegment> adj = w.getAdjacent();

		//to Do: update
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

	/*
	 * // attempt to connect adjacent vertical squares // passing y = 1 at x = 5.4
	 * causes a connection at x = 5, y = 0 // usage:
	 * connectHorizontal(floor(mousex),floor(mousey-1)); void connectVertcial(int x,
	 * int y) { Tile t = tiles.get(x, y); if (t.hasWire() && t.hasSilicon()) {
	 * connect(t.getSilicon(), t.getWire()); } }
	 * 
	 * // attempt to connect adjacent horizontal squares: on line x at box y. //
	 * passing x = 1, at y = 4.1 causes a connection between x = 0, y = 4 and x = 1,
	 * // y = 4 // usage: connectHorizontal(floor(mousex-1),floor(mousey)); void
	 * connectHorizontal(int x, int y, boolean metal) { Tile t1 = tiles[y][x]; Tile
	 * t2 = tiles[y][x - 1]; if (metal) { if (t1.hasWire() && t2.hasWire()) ; } else
	 * { if (t1.hasSilicon() && t2.hasSilicon()) ; } }
	 * 
	 * // forms connections between all components on depth void connectDepth(int x,
	 * int y) { connect(tiles[y][x].getSilicon(), tiles[y][x].getWire()); }
	 * 
	 * void connect(Conductor a, Conductor b) { /* if (a != null && b != null) {
	 * a.connections.add(b); b.connections.add(a); }
	 */
	// }

	void conductorTest() {
		int size = 30;
		Integer[][] grid = new Integer[30][30];
		boolean peak;
		ArrayList<PVector> queuedupdates = new ArrayList<PVector>();

	}
	
	
	
	
	
	//to be updated in this tick
	private LinkedList<WireSegment> currentwireupdates;
	//to be updated next tick
	private LinkedList<Gate> nextgateupdates;
	
	
	// runs signal spreading logic
	void iterate() {
		/*while (pendingupdate.size() != 0) {
			ArrayList<Conductor> nextupdatelist = new ArrayList<Conductor>();
			for (Conductor c : pendingupdate) {
				for (Conductor d : c.spread()) {
					// queue for update.
					if (nextupdatelist.indexOf(d) != -1)
						nextupdatelist.add(d);
				}
			}
			pendingupdate = nextupdatelist;
		}*/
		for (Gate g : nextgateupdates) {
			currentwireupdates.add(new LinkedList<WireSegment>(g.inputs));
		}
		
		while(!currentwireupdates.empty()) {
			// Makes new iterator and continuously updates through queued updates
			ListIterator<WireSegment> iter = new LinkedList<WireSegment>(currentwireupdates).iterator();
			currentwireupdates.clear();
			while(iter.hasNext()) {
				//adds update requests to both queues.
				updateSegment(iter.next());
			}
		}
		
	}

	/*
	 *  
	 *  RUN GAME
	 * 
	 * 
	 */
	
	
	
	private void updateSegment(WireSegment current) {
		// if segment is gate, do not permit flow based on gate type
		if(current instanceof Gate && !((Gate)current).isPermissive()) return;
		
		//search for lowest neighbor
		int min = Integer.MAX_VALUE;
		for(WireSegment adj : current.connections) {
			if(adj.active.val < min) {
				min = adj.active.val;	
			}
		}
		
		//update wire state and queue updates for appropriate neighbors.
		if(current.active.val < min) {
			current.active.val = WireSegment.WIRE_OFF;
			for(WireSegment output : current.connections) {
				if(output.active.val != WireSegment.WIRE_OFF) currentwireupdates.add(output);
			}
			
		} else {
			current.active.val = min + 1;
			for(WireSegment output : current.connections) {
				if(output.active.val == WireSegment.WIRE_OFF) currentwireupdates.add(output);
			}
		}
		for( Gate input : current.gates) {
			nextgateupdates.add(input);	
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
