package game;

import java.util.ArrayList;

import elements.Container;
import elements.Element;

import static core.MainProgram.*;
import static util.DB.*;

public class Conductor extends Element{

	
	
	// last tick it was updated on.
	int tick = 0;
	// indicates whether it should overwrite or assimilate a nearby conductor
	int priority = 0;
	boolean active = false;
	//list 
	Connection[] connections = new Connection[4];
	
	public Conductor(Tile p) {
		super(p.pos.x, p.pos.y, Tile.TILE_SIZE, Tile.TILE_SIZE, p);
	}
	

	@Override
	protected void update() {
		resetGraphics();
		g.ellipse(8,8,16,16);
	}


	// spreads 
	public ArrayList<Conductor> spread() {

		// check all surrounding conductors for if they have a higher priority value.
		// if so, set priority to one below that and update all surrounding conductors
		// with a lower priority value.

		boolean peak = true;
		ArrayList<Conductor> queuedupdates = new ArrayList<Conductor>();
		for (Connection c : connections) {
			if(c.mode == ConnectionMode.CONDUCT) {
				Conductor a = c.conductor;
				if (a.priority > priority) {
					peak = false;
					priority = max(Integer.MIN_VALUE, priority - 1);
				} else {
					queuedupdates.add(a);
				}
			}
		}

		if (peak) {
			active = false;
			priority = Integer.MIN_VALUE + 1;
		}

		return queuedupdates;
	}

	protected void separate( Direction dir) {
		if (connections[dir.val].mode != ConnectionMode.NONE) {
			Conductor b = connections[dir.val].conductor;
			connections[dir.val].mode = ConnectionMode.NONE;
			connections[dir.val].conductor = null;
			b.connections[dir.opposite().val].mode = ConnectionMode.NONE;	
			b.connections[dir.opposite().val].conductor = null;
		}
	}

	// remove connections and associations
	void destroy() {
		for (Direction d : Direction.values()) {
			separate(d);
		}
	}
	
	class Connection{
		Conductor conductor = null;
		ConnectionMode mode = ConnectionMode.NONE;
	}
}