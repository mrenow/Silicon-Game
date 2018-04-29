package game;

import static core.MainProgram.*;
import static util.DB.*;
import elements.Container;
import elements.GridContainer;
import events.ClickListener;
import events.KeyEvents;
import events.KeyListener;
import events.MovementListener;
import events.ScrollListener;
import processing.core.PVector;

import java.util.ArrayList;

/* Contains the building grid
 * An x,y scrollable and zoomable pane which will have a set size. 
 * Does not reside in a scroll pane due to the need for drawing optimizations.
 * 
 */
public class GameArea extends GridContainer implements KeyListener, ClickListener, MovementListener, ScrollListener {

	Tile[][] tiles;
	int dimx, dimy;

	float scale = 1;
	final static float MAX_SCALE = 10;
	final static float MIN_SCALE = 0.1f;
	PVector offset = new PVector(0, 0);

	ArrayList<Conductor> pendingupdate = new ArrayList<Conductor>();

	public GameArea(float x, float y, float w, float h, int dimx, int dimy, Container p) {
		super(x, y, w, h, h, h, p);
		this.dimx = dimx;
		this.dimy = dimy;
		tiles = new Tile[dimy][dimx];
		for (int i = 0; i < dimy; i++) {
			for (int j = 0; j < dimx; j++) {
				tiles[i][j] = new Tile(j, i, this);
			}
		}
		backgroundcolor = p3.color(255,255,200);
	}

	// attempt to connect adjacent vertical squares
	// passing y = 1 at x = 5.4 causes a connection at x = 5, y = 0
	// usage: connectHorizontal(floor(mousex),floor(mousey-1));
	void connectVertcial(int x, int y) {
		Tile t = tiles[y][x];
		if (t.hasWire() && t.hasSilicon()) {
			connect(t.getSilicon(), t.getWire());
		}
	}

	// attempt to connect adjacent horizontal squares: on line x at box y.
	// passing x = 1, at y = 4.1 causes a connection between x = 0, y = 4 and x = 1,
	// y = 4
	// usage: connectHorizontal(floor(mousex-1),floor(mousey));
	void connectHorizontal(int x, int y, boolean metal) {
		Tile t1 = tiles[y][x];
		Tile t2 = tiles[y][x - 1];
		if (metal) {
			if (t1.hasWire() && t2.hasWire())
				;
		} else {
			if (t1.hasSilicon() && t2.hasSilicon())
				;
		}
	}

	// forms connections between all components on depth
	void connectDepth(int x, int y) {
		connect(tiles[y][x].getSilicon(), tiles[y][x].getWire());
	}

	void connect(Conductor a, Conductor b) {
		/*
		 * if (a != null && b != null) { a.connections.add(b); b.connections.add(a); }
		 */
	}

	protected void update() {
		super.update();
	}

	protected void applyTransform() {
		pg().translate(offset.x - getWidth() / 2, offset.y - getHeight() / 2);
		pg().scale(scale);
	}

	// runs signal spreading logic
	void iterate() {
		while (pendingupdate.size() != 0) {
			ArrayList<Conductor> nextupdatelist = new ArrayList<Conductor>();
			for (Conductor c : pendingupdate) {
				for (Conductor d : c.spread()) {
					// queue for update.
					if (nextupdatelist.indexOf(d) != -1)
						nextupdatelist.add(d);
				}
			}
			pendingupdate = nextupdatelist;
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
			if (KeyEvents.key[CONTROL]) {
				offset.add((p3.pmouseX - p3.mouseX) / scale, (p3.pmouseY - p3.mouseY) / scale);
			} else {
				drawLine(p3.mouseX, p3.mouseY, p3.pmouseX, p3.pmouseY);
			}
		}
	}

	public void elementClicked() {

	}

	public void elementReleased() {

	}

	public void elementHovered() {

	}

	public void elementUnhovered() {

	}

	@Override
	public void elementScrolled(int value) {
		scale *= pow(0.01f, value);
		constrain(scale, MIN_SCALE, MAX_SCALE);
	}

	private void drawLine(float x1, float y1, float x2, float y2) {
		PVector pos1 = PVector.mult(screenPosToGridPos(x1, y1), 1 / Tile.TILE_SIZE);
		PVector pos2 = PVector.mult(screenPosToGridPos(x2, y2), 1 / Tile.TILE_SIZE);

		makeConductor(floor(pos1.x), floor(pos1.y));
		makeConductor(floor(pos2.x), floor(pos2.y));

		int minx = ceil(min(pos1.x, pos2.x));
		int maxx = ceil(max(pos1.x, pos2.x));
		int miny = ceil(min(pos1.x, pos2.x));
		int maxy = ceil(max(pos1.y, pos2.y));
		// vertical line intersections
		for (int x = minx; x < maxx; x++) {
			float y = (pos1.y - pos2.y) / (pos1.x - pos2.x) * pos1.x + pos1.y;
			makeConductor(x, floor(y));
			makeConductor(x - 1, floor(y));
			// ActivateIntersection(x-1)
		}
		// horizontal line intersections
		for (int y = miny; y < maxy; y++) {
			float x = (pos1.x - pos2.x) / (pos1.y - pos2.y) * pos1.y + pos1.x;
			makeConductor(y, floor(x));
			makeConductor(y - 1, floor(x));
		}

	}

	private void makeConductor(int x, int y) {
		tiles[y][x].makeWire();

		/*
		 * // N silicon tiles[y][x].makeSilicon(true); // P silicon
		 * tiles[y][x].makeSilicon(false); // via
		 */

	}

	private PVector screenPosToGridPos(float x, float y) {
		PVector globalpos = getGlobalPos();
		x -= globalpos.x + getWidth() / 2;
		y -= globalpos.y + getHeight() / 2;
		x /= scale;
		y /= scale;
		return new PVector(x, y);
	}

	private PVector screenPosToGridPos(PVector pos) {
		return screenPosToGridPos(pos.x, pos.y);
	}

	void conductorTest() {
		int size = 30;
		Integer[][] grid = new Integer[30][30];
		boolean peak;
		ArrayList<PVector> queuedupdates = new ArrayList<PVector>();

	}

}

// only one silicon can be placed per tile
// only one conductor may be placed per tile
// vias may be placed anywhere
// gates are silicon, may have any number of connections and a base. Gates are
// not objects, they are just pieces of silicon
// which have been based by any number of silicons
//
