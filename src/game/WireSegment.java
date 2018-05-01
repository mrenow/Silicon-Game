package game;

import static core.MainProgram.*;
import static util.DB.*;

import processing.core.PVector;
import util.DisjointSet;
import util.LinkedList;
import util.SparseQuadTree;

public class WireSegment extends DisjointSet {
	public static final byte METAL = 0;
	public static final byte N_TYPE = 1;
	public static final byte P_TYPE = 2;
	byte mode;

	int x;
	int y;

	SparseQuadTree<WireSegment> container;

	WireSegment(int x, int y) {
		this.x = x;
		this.y = y;

		oinfo = new ObjectInfo(x, y);
	}

	LinkedList<WireSegment> getAdjacent(Direction d) {
		switch (d) {
		case NORTH:
			return container.get(x, y - 1);
		case EAST:
			return container.get(x + 1, y);
		case SOUTH:
			return container.get(x, y + 1);
		case WEST:
			return container.get(x - 1, y);
		case IN:
			LinkedList<WireSegment> l = container.get(x, y);
			l.remove(this);
			return l;
		}
		return null;
	}

	// index 0 contains input gates and index 1 contains output gates
	LinkedList<Gate>[] updateGates() {
		LinkedList<Gate>[] out = new LinkedList[] { new LinkedList<Gate>(), new LinkedList<Gate>() };
		for (Direction d : Direction.values()) {
			for (WireSegment w : getAdjacent(d)) {
				if (w instanceof Gate) {
					Gate g = (Gate) w;
					if (g.mode == this.mode) {
						out[1].add(g);
					} else {
						out[0].add(g);
					}
				}
			}
		}
		return out;
	}

	// Contains info about all gate objects it can trigger,
	// and all gates that trigger it.
	class SetInfo extends AbstractSetInfo {
		int size = 1;
		LinkedList<Gate> inputs = null;
		LinkedList<Gate> outputs = null;

		void add(SetInfo that) {
			if (this.inputs == null || this.inputs.empty()) {
				this.inputs = that.inputs;
			} else {
				this.inputs.add(that.inputs);
			}
			if (this.outputs == null || this.inputs.empty()) {
				this.outputs = that.outputs;
			} else {
				this.outputs.add(that.outputs);
			}
			this.size += that.size;
		}
		void sub(SetInfo that) {
			if(that.inputs != null && !this.inputs.empty()) {
				
			}
			if(that.outputs != null && !this.outputs.empty()) {
				
			}

		}
	}
}
