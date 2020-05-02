package game;
import static core.MainProgram.*;
import static processing.core.PApplet.println;
import static util.DB.*;
import elements.Container;
import elements.MapNavigator;
import elements.MessageOverlay;
import events.ClickEvents;
import events.ClickListener;
import events.KeyEvents;
import events.KeyListener;
import events.MovementEvents;
import events.MovementListener;
import events.ScrollEvents;
import events.ScrollListener;
import processing.core.PVector;

import util.DB;
import util.LLinkedList;
import util.SparseQuadTree;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.ListIterator;

import async.ActiveAsyncEvent;
import async.AsyncEvent;
import async.Scheduler;
/* Contains the building grid
 * An x,y scrollable and zoomable pane which will have a set size. 
 * Does not reside in a scroll pane due to the need for drawing optimizations.
 * 
 */
public class GameArea extends MapNavigator implements KeyListener, ClickListener, MovementListener, ScrollListener {
	
	public static Scheduler tickscheduler = new Scheduler("GameThread");
	
	final static float MAX_SCALE = 500;
	final static float MIN_SCALE = 1;

	final static int VK_SILICON = KeyEvents.VK_SHIFT;
	final static int VK_METAL   = KeyEvents.VK_CONTROL;
	final static int VK_VIA     = KeyEvents.VK_SPACE;
	
	// ctrl + commands
	final static int VK_CTRL_UNDO  = KeyEvents.VK_Z;
	final static int VK_CTRL_REDO  = KeyEvents.VK_Y;
	final static int VK_CTRL_COPY  = KeyEvents.VK_C;
	final static int VK_CTRL_PASTE = KeyEvents.VK_V;	
	final static int VK_CTRL_CUT   = KeyEvents.VK_X;	
	
	final static int VK_EDIT_DESELECT = KeyEvents.VK_ESCAPE;
	final static int VK_EDIT_ROTATE = KeyEvents.VK_PERIOD;
	final static int VK_EDIT_ANTI_ROTATE = KeyEvents.VK_COMMA;
	final static int VK_EDIT_FLIP = KeyEvents.VK_SLASH;
	final static int VK_EDIT_DELETE = KeyEvents.VK_DELETE;

	final static byte MAKE_SILICON = 0;
	final static byte MAKE_METAL   = 1;
	final static byte MAKE_VIA     = 2;
	final static byte MAKE_POWER   = 3;
	final static byte EDIT         = 4;
	final static byte MAKE_SCOPE   = 5;

	final static int N_TYPE_INACTIVE_COLOR = p3.color(50,0,0);
	final static int P_TYPE_INACTIVE_COLOR = p3.color(100,100,0);
	final static int N_TYPE_ACTIVE_COLOR = p3.color(140,0,0);
	final static int P_TYPE_ACTIVE_COLOR = p3.color(220,220,0);
	final static int METAL_INACTIVE_COLOR = p3.color(100, 100, 100, 160);
	final static int METAL_ACTIVE_COLOR = p3.color(255,255,255,140);
	final static int METAL_ACCENT_COLOR = p3.color(200,200,200,70);
	final static int SCOPE_COLOR = p3.color(0,144,0,70);
	final static int SCOPE_TEXT_COLOR = p3.color(0,200,33);
	
	
	
	
	// temporary start points of selection
	int selectionx = -1;
	int selectiony = -1;
	
	byte makemode =  MAKE_SILICON;

	int dimx, dimy;
	
	
	private boolean db = false;
	public SparseQuadTree<WireSegment> tiles;
	
	public LLinkedList<Power> sources = new LLinkedList<Power>();
	
	public DataDisplay display;

	public MessageOverlay messageoverlay;
	
	public GameArea(float x, float y, float w, float h, int size, Container p) {
		super(x, y, w, h, p);

		dimx = dimy = 1 << size;
		offset = new PVector(dimx / 2, dimy / 2);
		backgroundcolor = p3.color(230, 230, 230);
		zoomrate = 1.05f;

		zoom = 100;
		offset = new PVector(0, 0);

		tiles = new SparseQuadTree<WireSegment>(size);
		WireSegment.container = tiles;
		WireSegment.potentialdisconnects.clear();
		
		setZoomBounds(1, 100);
		int areapadding = 10;
		
		setOffsetBounds(-areapadding, -areapadding, dimx + areapadding, dimy + areapadding);
		
		messageoverlay = new MessageOverlay(this);

		// Add listeners
		KeyEvents.add(this);
		ScrollEvents.add(this);
		ClickEvents.add(this);
		MovementEvents.add(this);
		
		tickscheduler.start();
		setSpeed(10);
	}
	public GameArea(float x, float y, float w, float h, int size, Container p, Object loadtiles) {
		this(x, y, w, h, size, p);
		try {
			if(loadtiles != null) loadSave(loadtiles);
		} catch (ClassCastException e){
			println(e.getMessage());
			DB_W(this,"Corrupted data while loading.");
			tiles = new SparseQuadTree<WireSegment>(size);
			WireSegment.container = tiles;
			WireSegment.potentialdisconnects.clear();
		}
	}
	@Override
	protected void update() {
		resetGraphics();
		
		LLinkedList<WireSegment> objects = new LLinkedList<WireSegment>(tiles.get(floor(offset.x),
				floor(offset.y), ceil(offset.x + getWidth() / zoom), ceil(offset.y + getHeight() / zoom))); 

		g.pushMatrix();
		transformView();

		// Draw bounding box
		g.fill(200,205,210);
		g.noStroke();
		g.rect(0, 0, dimx, dimy);
		
		drawObjects(objects);
		drawScopes();
		g.popMatrix();
		
		drawPreview();
		
		if(canedit) {
			drawSelection();
			if(ispasting) {
				drawClipboard();
			}
		}
		
		 
		// debug overlay indication direction of WireSegment chain
		// debug overlay for grid
		if(db) drawDebug();
	
	
		drawChildren();
	}
	
	private void drawObjects(Iterable<WireSegment> objects) {
		
		// Only loads objects within viewing pane
		// New linked list for optimization to remove start and end nodes.

		WireSegment w = null;		
		Gate g1 = null;
		// Draw N silicon
		ListIterator<WireSegment> witer = (ListIterator<WireSegment>) objects.iterator();
		ListIterator<Gate> giter;
		
		LLinkedList<WireSegment> active = new LLinkedList<WireSegment>();
		LLinkedList<Gate> gates = new LLinkedList<Gate>();

		g.noStroke();
		// inactive N silicon
		g.fill(N_TYPE_INACTIVE_COLOR);
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

		// Active N silicon
		g.fill(N_TYPE_ACTIVE_COLOR);
		for (WireSegment w1: active) {
			g.rect(w1.x, w1.y, 1, 1);
		}
		active.clear();
		
		//active gates
		giter = gates.iterator();
		g.fill(P_TYPE_ACTIVE_COLOR);
		while(giter.hasNext()) {
			g1 = giter.next();
			if(g1.powered()) {
				g.rect(g1.x + 0.1f,g1.y + 0.1f, 0.8f, 0.8f);
				giter.remove();
			}
		}
		
		
		// inactive gates
		g.fill(P_TYPE_INACTIVE_COLOR);
		for (Gate g2: gates) {
			g.rect(g2.x + 0.1f,g2.y+0.1f,0.8f,0.8f);
		}
		gates.clear();
		
		
		// Draw P silicon
		witer = (ListIterator<WireSegment>) objects.iterator();
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
		
		// Active Psilicon
		g.fill(P_TYPE_ACTIVE_COLOR);
		for (WireSegment w1: active) {
			g.rect(w1.x, w1.y, 1, 1);
		}
		active.clear();
		
		
		
		//active gates
		giter = gates.iterator();
		g.fill(N_TYPE_ACTIVE_COLOR);
		while(giter.hasNext()) {
			g1 = giter.next();
			if(g1.powered()) {
				g.rect(g1.x + 0.1f,g1.y+0.1f,0.8f,0.8f);
				giter.remove();
			}
		}
		// inactive gates
		g.fill(N_TYPE_INACTIVE_COLOR);
		for (Gate g2: gates) {
			g.rect(g2.x + 0.1f,g2.y+0.1f,0.8f,0.8f);
		}
		gates.clear();
		
		// Draw metal
		witer = (ListIterator<WireSegment>) objects.iterator();
		g.fill(METAL_INACTIVE_COLOR);
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
		g.fill(METAL_ACTIVE_COLOR);
		for(WireSegment w1 : active) {
			g.rect(w1.x, w1.y, 1, 1);
		}
		active.clear();
		

		
		// Draw Power
		g.stroke(METAL_ACCENT_COLOR);
		g.strokeWeight(0.06f);
		witer = (ListIterator<WireSegment>) objects.iterator();
		g.fill(METAL_INACTIVE_COLOR);
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
		g.fill(METAL_ACTIVE_COLOR);
		for(WireSegment w1 : active) {
			g.rect(w1.x, w1.y, 1, 1);
		}
		active.clear();
		
		//draw vias 
		witer = (ListIterator<WireSegment>) objects.iterator();
		g.noFill();
		while (witer.hasNext()) {
			w = witer.next();
			if (w.mode == WireSegment.VIA) {
				g.ellipse(w.x + 0.5f, w.y + 0.5f, 0.7f, 0.7f);
				witer.remove();
			}
		}
	}
	private void drawScopes() {
		//draw scopes
		g.noFill();
		g.stroke(SCOPE_COLOR);
		for(Oscilloscope o : display.scopes) {
			g.rect(o.x, o.y, 1, 1);
		}
		/*
		 *  Unfortunately, processing doesnt like doing things 
		 *  in non-integer multiples, even when the whole area
		 *  is scaled. Shame on you processing. Because of this
		 *  we now resort to aesthetic displeasure
		 */
		
		// First save the old context
		g.pushMatrix();
		// Set matrix to identity and use MapNav functions to
		// get the text in the right location
		g.resetMatrix();
			
		g.textSize(zoom/2);
		g.textAlign(CENTER,CENTER);
		g.fill(SCOPE_TEXT_COLOR);
		for(Oscilloscope o : display.scopes) {
			g.text(Integer.toString(o.id), mapToLocalX(o.x), mapToLocalY(o.y), zoom,zoom );
		}
		// make sure to restore context
		g.popMatrix();
		// hey it worked first try
		
		
	}
	
	
	private void drawDebug() {
		
		g.pushMatrix();
		transformView();
		
		g.strokeWeight(0.01f);
		g.stroke(255,0,0);
		
		
		g.fill(255,0,0);
		g.textSize(0.5f);
		// red circles denote marked for rectification.
		for (WireSegment w1 : WireSegment.potentialdisconnects) {
			g.ellipse(w1.x+0.5f, w1.y+0.5f, 0.1f,0.1f);
		}
		
		// Red arrows denote gate inputs
		for(WireSegment w1 : tiles.elements) {
			if(w1.isGate()) {
				for (WireSegment w2 : ((Gate)w1).inputs) {
					PVector v = new PVector(w2.x - w1.x,w2.y - w1.y);
					
					//draws arrow between line and parent
					g.line(w1.x+0.5f, w1.y+0.5f, w2.x  + 0.5f,w2.y+0.5f);
					v.rotate(PI/6).mult(-0.3f);
					g.line(w2.x+0.5f,w2.y+0.5f,w2.x + v.x + 0.5f, w2.y + v.y + 0.5f);
					v.rotate(-PI/3);
					g.line(w2.x+0.5f,w2.y+0.5f,w2.x  + v.x + 0.5f, w2.y + v.y + 0.5f);
				
				}
			}
			
		}
		
		g.stroke(0);
		g.fill(0);
		for(WireSegment w1 : tiles.elements) {
			PVector v = new PVector(w1.getParent().x - w1.x,w1.getParent().y - w1.y);
			
			//draws arrow between line and parent
			g.line(w1.x+0.5f, w1.y+0.5f, w1.getParent().x  + 0.5f,w1.getParent().y+0.5f);
			v.rotate(PI/6).mult(-0.3f);
			g.line(w1.getParent().x+0.5f,w1.getParent().y+0.5f,w1.getParent().x + v.x +0.5f, w1.getParent().y + v.y+0.5f);
			v.rotate(-PI/3);
			g.line(w1.getParent().x+0.5f,w1.getParent().y+0.5f,w1.getParent().x + v.x +0.5f, w1.getParent().y + v.y+0.5f);

			// display power level, 0 for source and -1 for off
			g.text(w1.getActive()- Integer.MIN_VALUE,w1.x + 0.5f , w1.y+0.5f);
		}
		g.stroke(0,255,0); 
		
		//green lines denote connections
		for(WireSegment w1 : tiles.elements) {
				
			if(w1.isGate() || w1 instanceof Power){
				for (WireSegment w2: w1.connections) {
					PVector v = new PVector(w2.x - w1.x,w2.y - w1.y);
					
					//draws arrow between line and parent
					g.line(w1.x+0.5f, w1.y+0.5f, w2.x  + 0.5f,w2.y+0.5f);
					v.rotate(PI/6).mult(-0.3f);
					g.line(w2.x+0.5f,w2.y+0.5f,w2.x  +0.5f, w2.y +0.5f);
					v.rotate(-PI/3);
					g.line(w2.x+0.5f,w2.y+0.5f,w2.x  +0.5f, w2.y +0.5f);
				}
			}
		}
		
		

		
		// draw grid markers
		g.popMatrix();
		g.strokeWeight(1);
		g.textSize(11);
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
		
		// draw tile info at cursor:
		PVector mouse = localToMapPos(localMouseX(), localMouseY());
		StringBuilder str = new StringBuilder(100);
		g.textAlign(RIGHT,TOP);
		
		for (WireSegment w : tiles.get(floor(mouse.x),floor(mouse.y))) {
			str.append("mode: ");
			str.append(w.modeToString());
			str.append("\nActive: ");
			str.append(w.getActive() - Integer.MIN_VALUE);
			
			if(w instanceof Gate) {
				str.append("\nPermissive: ");
				str.append(w.isPermissive());
			}			
			str.append("\n----\n");
		}
		
		g.text(str.toString(),getWidth()-20,20);
		
	}
	
	private void drawSelection() {
		if(!hasSelection()) return;
		g.pushMatrix();
		transformView();
		g.noFill();
		if(iscopying) {
			g.stroke(0,0,0,50 + 50*abs(sin(0.003f*p3.millis())));
			requestUpdate();
		}else {
			g.stroke(0,0,0,100);
		}
		g.rect(region.x1, region.y1, region.x2-region.x1, region.y2-region.y1);
		
		g.popMatrix();
		
	}
	
	// First flip around X axis, then rotate
	private void drawClipboard() {
		PVector mousepos = localToMapPos(localMouseX(), localMouseY());
		int mousex = floor(mousepos.x);
		int mousey = floor(mousepos.y);
		
		g.pushMatrix();
		transformView();

		//move 0,0 to mousex, mousey 
		g.translate(mousex, mousey);

		g.rotate(rotation*PI/2);

		if(flipped) {
			g.scale(-1,1);
		}
		switch(flipped? (3 - rotation)%4 : rotation) {
		case 0:
			//do nothing 
			break;
		case 1:
			//bottom left
			g.translate(0,-clipboardregion.getHeight());
			break;
		case 2:
			//bottom right
			g.translate(-clipboardregion.getWidth(),-clipboardregion.getHeight());
			break;
		case 3:
			//top right
			g.translate(-clipboardregion.getWidth(),0);
			break;
		}
		//move startx, starty to 0,0
		g.translate(-clipboardregion.x1, -clipboardregion.y1);
		drawObjects(clipboard);
		g.popMatrix();
	
	}
	
	private void drawPreview() {

		PVector mousepos = localToMapPos(localMouseX(), localMouseY());
		int mousex = floor(mousepos.x);
		int mousey = floor(mousepos.y);
		if(mousex < 0 || mousex >= dimx || mousey < 0 || mousey >= dimy) return;
		
		g.pushMatrix();
		transformView();
		if(canedit) {
			switch(makemode) {
			case MAKE_SILICON:
				g.noStroke();
				if(KeyEvents.key[VK_SILICON]) {
					// N_TYPE
					g.fill(140,0,0,70);
					// if silicon at tile
					if(tileContains(mousex, mousey, WireSegment.P_TYPE)) {
						g.rect(mousex + 0.1f,mousey+0.1f,0.8f,0.8f);
					}else {
						g.rect(mousex, mousey, 1,1);
					}
				}else {
					// P_TYPE
					g.fill(220, 220, 0, 70);
					if(tileContains(mousex, mousey, WireSegment.N_TYPE)) {
						g.rect(mousex + 0.1f, mousey + 0.1f, 0.8f, 0.8f);
					}else {
						g.rect(mousex, mousey, 1,1);
					}
				}
				
				break;
			case MAKE_METAL:
				g.noStroke();
				g.fill(100, 100, 100, 70);
				g.rect(mousex, mousey, 1,1);
				break;
			case MAKE_VIA:
				g.strokeWeight(0.1f);
				g.stroke(100,100,100,70);
				g.noFill();
				g.ellipse(mousex + 0.5f, mousey + 0.5f, 0.7f, 0.7f);
				break;
			case MAKE_POWER:
				g.strokeWeight(0.1f);
				g.fill(100, 100, 100, 70);
				g.stroke(100,100,100,70);
				g.rect(mousex, mousey, 1,1);
				break;
			case EDIT:
				if(selectionx != -1 &&  p3.mousePressed && p3.mouseButton == LEFT) {
					int selectionx0 = selectionx;
					int selectiony0 = selectiony;
					int selectionx1 = mousex;
					int selectiony1 = mousey;
			
					//ensure that selectionx1 > selectionx
					if(selectionx1 < selectionx0) {
						int s = selectionx0;
						selectionx0 = selectionx1;
						selectionx1 = s;
					}
					
					//ensure that selectiony1 > selectiony
					if(selectiony1 < selectiony) {
						int s = selectiony0;
						selectiony0 = selectiony1;
						selectiony1 = s;
					}
					g.noFill();
					g.stroke(0,0,0,50);
					g.rect(selectionx0, selectiony0, selectionx1-selectionx0+1, selectiony1-selectiony0+1);
				}
				break;
			}
		}
		if(makemode == MAKE_SCOPE) {
			g.strokeWeight(0.1f);
			g.noFill();
			g.stroke(0,144,0,70);
			g.rect(mousex, mousey, 1,1);
		}
		g.popMatrix();
	}
	
	
	
	

	public void keyPressed() {
		// debug
		if(KeyEvents.key[KeyEvents.VK_D]) {
			db = !db;
		}
		
		if (KeyEvents.key[KeyEvents.VK_5]) {
			makemode = EDIT;
			messageoverlay.addMessage("Using Edit tool.");
		}else if(KeyEvents.key[KeyEvents.VK_1]) {
			discardSelection();
			makemode = MAKE_SILICON;
			messageoverlay.addMessage("Placing Silicon.");
		}else if (KeyEvents.key[KeyEvents.VK_2]){
			discardSelection();
			makemode = MAKE_METAL;	
			messageoverlay.addMessage("Placing Metal.");
		}else if (KeyEvents.key[KeyEvents.VK_3]){
			discardSelection();
			makemode = MAKE_VIA;
			messageoverlay.addMessage("Placing Vias.");
		}else if (KeyEvents.key[KeyEvents.VK_4]){
			discardSelection();
			makemode = MAKE_POWER;
			messageoverlay.addMessage("Placing Power Sources.");
		}else if (KeyEvents.key[KeyEvents.VK_6]) {
			discardSelection();
			makemode = MAKE_SCOPE;			
			messageoverlay.addMessage("Placing Scopes.");
		}
		
	
		if(KeyEvents.key[KeyEvents.VK_CONTROL]) {
			// Pasting will generate a ghost overlay of the copied selection
			// keys will edit the pastemode as it is being previewed
			// a mouse click will paste the selection at the region.
			if(KeyEvents.key[VK_CTRL_PASTE]) {
				if(hasClipboard()) {
					ispasting = true;
					iscopying = false;
				}
			}else if(hasSelection() && !ispasting) {
				if(KeyEvents.key[VK_CTRL_COPY]) {
					copyRegion();
				}else if (KeyEvents.key[VK_CTRL_CUT]) {
					copyRegion();
					deleteSelection();
				}
			}
		}
		
		// Editing
		if(makemode == EDIT ) {
			if(KeyEvents.key[VK_EDIT_DESELECT])
				ispasting = false;
			}
			if(hasSelection()) {
				if(KeyEvents.key[VK_EDIT_DESELECT]) {
					discardSelection();
				}else if(KeyEvents.key[VK_EDIT_DELETE]) {
					deleteSelection();
				}
		}
		//rotate
		if(KeyEvents.key[VK_EDIT_ROTATE]) {
			rotateSelection(1);
		}
		if(KeyEvents.key[VK_EDIT_ANTI_ROTATE]) {
			rotateSelection(-1);
		}
		if(KeyEvents.key[VK_EDIT_FLIP]) {
			flipSelection();
			
		}
		
		requestUpdate();
		
	}

	public void keyReleased() {

		requestUpdate();
	}

	public void keyTyped() {

	}

	public void mouseMoved() {
		// if control pressed move screen.
		if (p3.mousePressed) {
			//if (KeyEvents.key[VK_DRAG]) {
			if(p3.mouseButton == 3) {
				addOffset(localPMouseX()- localMouseX(), localPMouseY() - localMouseY());
			} else if (p3.mouseButton == LEFT || p3.mouseButton == RIGHT) {
				PVector gpos = getGlobalPos();
				drawLine(localMouseX(), localMouseY(), localPMouseX(), localPMouseY());
			}
		}
		requestUpdate();
		
	}

	
	public void elementClicked() {
		
		//place down selection with parameters
		if(p3.mouseButton == LEFT && ispasting) {
			PVector mousepos = localToMapPos(localMouseX(),localMouseY());
			
			paste(floor(mousepos.x), floor(mousepos.y));
			if(!KeyEvents.key[KeyEvents.VK_CONTROL]) {
				endPaste();
			}
			
		}else if(p3.mouseButton == LEFT && makemode == EDIT && !ispasting) {
			// if selection already exists discard
			region = null;
			
			PVector mousepos = localToMapPos(localMouseX(),localMouseY());
			
			selectionx = floor(mousepos.x);
			selectiony = floor(mousepos.y);
			requestUpdate();
		
		} else if(p3.mouseButton != 3) {
			activateSquare(localToMapPos(localMouseX(), localMouseY()));
			requestUpdate();
		}
	
		
	}

	public void elementReleased() {
		if(p3.mouseButton == LEFT && makemode == EDIT && !ispasting && selectionx != -1 && selectiony != -1) {
			

			PVector mousepos = localToMapPos(localMouseX(),localMouseY());
			int selectionx1 = floor(mousepos.x);
			int selectiony1 = floor(mousepos.y);

			//ensure that selectionx1 > selectionx
			if(selectionx1 < selectionx) {
				int s = selectionx;
				selectionx = selectionx1;
				selectionx1 = s;
			}
			
			//ensure that selectiony1 > selectiony
			if(selectiony1 < selectiony) {
				int s = selectiony;
				selectiony = selectiony1;
				selectiony1 = s;
			}
			
			//selection is inclusive
			selectRegion(selectionx, selectiony, selectionx1 + 1, selectiony1 + 1);
			
			selectionx = -1;
			selectiony = -1;
			requestUpdate();
			
		}
	}

	public void elementHovered() {

	}

	public void elementUnhovered() {

	}

	public void elementScrolled(int value) {
		addZoom(-value, localMouseX(), localMouseY());
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
			ListIterator<WireSegment> iter = new LLinkedList<WireSegment>(w1.getAdjacent()).iterator();
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

		
		
		for(WireSegment w : tiles.get(x, y)) {
			if(w instanceof Power) {
				((Power)w).toggle(nextwireupdates);
			}
		}
	
		
		if(canedit && makemode != MAKE_SCOPE) {
			if (p3.mouseButton == RIGHT) {
				delete(x,y);
				
			} else if (p3.mouseButton == LEFT){
				switch(makemode) {
					case MAKE_VIA:
						if(!tileContains(x,y,WireSegment.VIA)) {
							makeObject(x,y,WireSegment.VIA);
						}
						break;
					case MAKE_METAL:
						if(!tileContains(x,y,WireSegment.METAL_LAYER)) {
							makeObject(x,y,WireSegment.METAL);
						}
						break;
					case MAKE_POWER:
						if(!tileContains(x,y,WireSegment.METAL_LAYER)) {
							makeObject(x,y,WireSegment.POWER);
						}
						break;
					case MAKE_SILICON:
						if(KeyEvents.key[VK_SILICON]) {
							if(tileContains(x,y,WireSegment.P_TYPE)) {
								delete(x,y,WireSegment.P_TYPE);
								makeObject(x,y,WireSegment.P_GATE);
							}else if(!tileContains(x,y,WireSegment.SILICON_LAYER)){
								makeObject(x,y,WireSegment.N_TYPE);
							}
						}else {
							if(tileContains(x,y,WireSegment.N_TYPE)) {
								delete(x,y,WireSegment.N_TYPE);
								makeObject(x,y,WireSegment.N_GATE);
							} else if (!tileContains(x,y,WireSegment.SILICON_LAYER)) {
								makeObject(x,y,WireSegment.P_TYPE);
							}
							
						}
						break;
				}
			}
		}
		if(makemode == MAKE_SCOPE) {
			if(p3.mouseButton == RIGHT) {
				deleteScope(x,y);
			}else if(p3.mouseButton == LEFT) {
				if(!hasScope(x,y)) {
					makeScope(x,y);
				}	
			}
		}
		requestUpdate();
		
	}

	private void makeObject(int x, int y, byte mode) {
		
		//out of bounds.
		if(x < 0 || dimx <= x || y < 0 || dimy <= y) return;
		
		WireSegment w;
		Gate g;
		Power p;
		
		switch(mode) {
		case WireSegment.METAL:
		case WireSegment.P_TYPE:
		case WireSegment.N_TYPE:
		case WireSegment.VIA:
			tiles.add(w = new WireSegment(mode, x, y), x, y);
			w.updateConnections();
			break;
		case WireSegment.P_GATE:
		case WireSegment.N_GATE:
			tiles.add(g = new Gate(mode,x,y),x, y);
			g.updateConnections();
			break;
		case WireSegment.POWER:
			tiles.add(p = new Power(x, y), x, y);
			p.updateConnections();
			sources.add(p);
			break;
		}
	}
	
	// If mode or list of modes at tile
	public boolean tileContains(int x , int y, byte ... mode) {
		for(WireSegment w : tiles.get(x, y)) {
			for(byte m : mode) {
				if(w.mode == m) return true;
			}
		}
		return false;
	}
	public WireSegment getObjectAt(int x, int y, byte... mode) {
		
		for(WireSegment w : tiles.get(x, y)) {
			for(byte m : mode) {
				if(w.mode == m) return w;
			}
		}
		return null;
	}
	
	
	
	

	private void delete(int x, int y) {
		LLinkedList<WireSegment> current = tiles.get(x, y);
		for (WireSegment w : current) {
			if(((makemode == MAKE_VIA  ) == (w.mode == WireSegment.VIA  ))
	         &&((makemode == MAKE_METAL) == (w.mode == WireSegment.METAL))
	         &&((makemode == MAKE_POWER) == (w.mode == WireSegment.POWER))){
				if(w.mode == WireSegment.POWER) sources.remove((Power)w); 
				delete(w);
			}	
		}
	}

	private void delete(int x, int y, byte ... mode) {
		LLinkedList<WireSegment> current = tiles.get(x, y);
		for (WireSegment w : current) {
			for(byte m: mode) {
				if(w.mode == m) {
					delete(w);
				}
			}
		}
	}
	// Brute force is viable because of low component quanity
	private void deleteScope(int x, int y) {
		Oscilloscope o = scopeAt(x,y);
		if(o != null) {
			display.removeScope(o);
			display.updateScopes();
		}
		requestUpdate();
	}
	private void makeScope(int x, int y) {
		display.addScope(x,y);
		display.updateScopes();
		requestUpdate();
	}
	
	private void delete(WireSegment w) {		
		tiles.remove(w, w.x, w.y);
		w.delete();
	}
	public boolean hasScope(int x, int y) {
		return scopeAt(x,y) != null;
	}
	
	
	public Oscilloscope scopeAt(int x, int y) {
		for (Oscilloscope o : display.scopes) {
			if(o.x == x && o.y == y) {
				return o;
			}
		}
		return null;
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
					int x = floor(xf);
					activateSquare(x, y);
					activateSquare(x, y-1);
				}
			}	
		}
	}
		
	/*
	 * SAVING STATE
	 */


	public Object getSave() {
		ArrayList<TileData> tiledatalist = new ArrayList<TileData>();
		for (WireSegment w: tiles.elements) {
			tiledatalist.add(new TileData(w.mode,w.x,w.y));
		}
		return tiledatalist;
	}
	
	public void loadSave(Object tiledatalist) throws ClassCastException{
		for (TileData w: (ArrayList<TileData>) tiledatalist) {
			delete(w.x,w.y,WireSegment.getLayer(w.mode));
			makeObject(w.x,w.y, w.mode);	
		}
	}
	
	/*
	 * SELECTION AND EDITING
	 */
	
	// Assume x2 > x1, y2 > y1 
	private class Selection {
		int x1;
		int y1;
		int x2;
		int y2;
		private Selection(int x1, int y1, int x2, int y2){
			this.x1 = constrain(x1,0,dimx);
			this.y1 = constrain(y1,0,dimy);
			this.x2 = constrain(x2,0,dimx);
			this.y2 = constrain(y2,0,dimy);
		}
		public Selection(Selection that) {
			this.x1 = that.x1;
			this.y1 = that.y1;
			this.x2 = that.x2;
			this.y2 = that.y2;
		}
		private int getWidth() {
			return x2 - x1;
		}
		public int getHeight() {
			return y2 - y1;
		}
		
	}
	Selection region = null;
	// positive -> clockwise
	int rotation = 0;
	boolean flipped = false;
	
	// upon select and copy all wires in the range will be placed in here.
	private Iterable<WireSegment> clipboard;
	Selection clipboardregion = null;
	// paste mode
	// CTRL to place without ending paste mode
	// < > to Rotate selection
	// Escape to exit
	boolean ispasting = false;
	boolean iscopying = false;
	
	boolean canedit = true;
	
	public boolean hasSelection() {
		return region != null;
	}
	public boolean hasClipboard() {
		return clipboard != null;
	}
	
	public void deleteSelection() {
		for (WireSegment w : tiles.get(region.x1, region.y1, region.x2, region.y2)){
			tiles.remove(w, w.x, w.y);
			w.delete();
		}
		tiles.delete(region.x1, region.y1, region.x2, region.y2);
	}
	
	
	public void selectRegion(int x1, int y1, int x2, int y2) {
		region = new Selection(x1,y1,x2,y2);
	}
	
	public void rotateSelection(int rot) {
		rotation += rot + 4;
		rotation %= 4;
		
	}
	public void flipSelection() {
		flipped = !flipped;
	}
	
	public void copyRegion() {
		clipboard = tiles.get(region.x1,region.y1,region.x2,region.y2);
		clipboardregion = new Selection(region);
		iscopying = true;
		rotation = 0;
		flipped = false;
	}
	
	public void discardSelection() {
		region = null;
		iscopying = false;
	}

	public void beginPaste() {
		ispasting = true;
	}

	// Flips around y axis then clockwise rotation of 90 degrees.
	// Pastes with top left corner at mouse square.
	public void paste(int x, int y) {
		
		for (WireSegment w : clipboard) {
			int newx;
			int newy;
			
			int localx = w.x-clipboardregion.x1;
			int localy = w.y-clipboardregion.y1;
			
			
			if(flipped) {
				localx = clipboardregion.getWidth() - localx - 1;
			}
			int newlocalx;
			int newlocaly;
			switch((rotation + 4)% 4) {
				case 0:
					newlocalx = localx;
					newlocaly = localy;
					break;
				case 1:
					newlocalx = clipboardregion.getHeight() - localy-1;
					newlocaly = localx;
					break;
				case 2:
					newlocalx = clipboardregion.getWidth() - localx-1;
					newlocaly = clipboardregion.getHeight() - localy-1;
					break;
				case 3:
					newlocalx = localy;
					newlocaly = clipboardregion.getWidth() - localx-1;
					break;
				default:
					newlocalx = localx;
					newlocaly = localy;
					break;
			}
			
			newx = x + newlocalx;
			newy = y + newlocaly;
			
			delete(newx,newy,w.getLayer());
			makeObject(newx,newy, w.mode);	
		}
	}
	
	
	public void endPaste() {
		ispasting = false;
	}
	
	
	
	
	
	
	/*
	 * LOGIC ENGINE
 	 */
	
	//to be updated in this tick
	private LLinkedList<WireSegment> currentwireupdates = new LLinkedList<WireSegment>();
	//to be updated next tick
	private LLinkedList<WireSegment> nextwireupdates = new LLinkedList<WireSegment>();
	
	
	// Thread states
	public boolean running = false;
	public boolean stoppable = false;
	
	// make new process
	public void run() {
		if(canedit == true) {
			messageoverlay.addMessage("Run mode.");
			canedit = false;
			
		}
		rectifyMap();
		display.updateProbes();
		if(!running) {
			tickscheduler.call(new RepeatIterate());
			display.activate();
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
			p.toggle(nextwireupdates,false);
		}
		// Destroy thread
		stop();
		// Run once so that are updated appropriately
		tickscheduler.call(new RunIterate());
		canedit = true;
		messageoverlay.addMessage("Build mode.");
	}
	
	
	
	// runs signal spreading logic
	private void iterate() {
		
		for (WireSegment w : nextwireupdates) {
			w.updatePowered();
			currentwireupdates.add(w);
			w.updatablenext = false;
		}
		nextwireupdates.clear();
		
		while(!currentwireupdates.empty()) {
			// Makes new iterator and continuously updates through queued updates
			ListIterator<WireSegment> iter = new LLinkedList<WireSegment>(currentwireupdates).iterator();
			for(WireSegment w : currentwireupdates) {
				w.updatablecurrent = false;
			}			
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
			messageoverlay.addMessage("Simulation started.");
			running = true;
			DB_U("TickThread started");
		}
		protected void stop() {
			// Stop may be requested once thread runs again.
			messageoverlay.addMessage("Simulation stopped.");
			stoppable = false;
			running = false;
			DB_U("TickThread stopped");
		}
		@Override
		protected boolean condition() {
			return exists && !stoppable;
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
// Must be located here so it does not contain an instance of GameArea
class TileData implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	byte mode;
	int x, y;
	public TileData(byte mode, int x, int y) {
		this.mode = mode;
		this.x = x;
		this.y = y;
	}
}
