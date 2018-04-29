package effects;

import async.ActiveAsyncEvent;
import async.Scheduler;
import elements.Container;

//container which kills itself after a set period of time or a condition. 
//optimally extends container when that function becomes implemented
public abstract class Effect extends Container {
	int t;
	Object a ;
	Effect(float x, float y, float w, float h, int duration, boolean uniform, Container p) {
		super(x, y, w, h, p);
		t = duration;
		Scheduler.call(new eventLoop());

	}

	void tick() {
		t--;
	}

	// throws constant calculation onto async thread.
	class eventLoop extends ActiveAsyncEvent {
		public void start() {
		}

		public void run() {
			// stuff
		}

		public boolean condition() {
			return exists;
		}
	}

}