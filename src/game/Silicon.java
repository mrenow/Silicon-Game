package game;

import static core.MainProgram.*;
import static util.DB.*;

import java.util.ArrayList;

// adds functionality that blocks adjacent conductors
public abstract class Silicon extends Conductor {


	public Silicon(Tile p) {
		super(p);
	}

	// if any one of the bases are triggered, item is blocked or unblocked
	ArrayList<Silicon> base;
	ArrayList<Silicon> gate;
	
	
	

	boolean triggered() {
		for (Silicon s : base) {
			if (s.active) {
				return true;
			}
		}
		return false;
	}

}

class NSilicon extends Silicon {

	public NSilicon(Tile p) {
		super(p);
	}

/*	@Override
	public ArrayList<Conductor> update() {

		// NPN activates when base activates
		// if blocked, escape
		if (triggered())
			return new ArrayList<Conductor>();
		return super.update();
	}*/
}

class PSilicon extends Silicon {

	public PSilicon(Tile p) {
		super(p);
	}

/*	@Override
	public ArrayList<Conductor> update() {

		// PNP blocks when base actviates
		// if blocked, escape
		if (!triggered())
			return new ArrayList<Conductor>();

		return super.update();
	}*/
}
