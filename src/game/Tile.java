package game;

import elements.Container;

import static core.MainProgram.*;
import static util.DB.*;
import elements.Container;


public class Tile extends Container {
	
	final static int TILE_SIZE = 16;

	// if s
	private Silicon silicon;
	private Conductor wire;

	// causes signal to flow between conductors on that tile.
	boolean via;
	boolean visible;

	public Tile(int x, int y, GameArea p) {
		super(x * TILE_SIZE, y * TILE_SIZE, TILE_SIZE, TILE_SIZE, p);
	}

	public void makeSilicon(boolean mode) {
		if(!hasSilicon()) {
			if(mode) {
				silicon = new NSilicon(this);
			}else {
				silicon = new PSilicon(this);
			}
		}
	}

	public void makeWire() {
		if(!hasWire()) {
			wire = new Conductor(this);	
		}
	}

	public boolean hasSilicon() {
		return getSilicon() != null;
	}

	public boolean hasWire() {
		return getWire() != null;
	}
	
	public Silicon getSilicon() {
		return silicon;
	}

	public Conductor getWire() {
		return wire;
	}

}
