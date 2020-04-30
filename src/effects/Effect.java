package effects;
import static core.MainProgram.globalscheduler;
import async.ActiveAsyncEvent;
import async.Scheduler;
import elements.Container;
import elements.Element;

// Container which continuously updates and then kills itself once a particular condition is met. 
// Optimally extends collection when that container becomes implemented
// For more compicated multistage effects, an effect should spawn a new effect upon death.
// Teeechnically you can use this class to implement a pane which destroys itself after the given duration,
// but that doesnt follow naming convention
public abstract class Effect extends Container {
	private Scheduler scheduler = globalscheduler;
	private int t, duration;
	Effect(float x, float y, float w, float h, int duration,  Container p) {
		super(x, y, w, h, p);
		this.t = duration;
		this.duration = duration;
		// begin servicing event
		scheduler.call(new EventLoop());
	}
	Effect(float x, float y, int duration, Container p, Element ... children) {
		super(x, y, p, children);
		this.t = duration;
		this.duration = duration;
		scheduler.call(new EventLoop());
	}
	// Should return a number between 0 and 1.
	public float getProgress() {
		return (float)t/duration;
	}
	
	// throws constant calculation onto async thread.
	class EventLoop extends ActiveAsyncEvent {
		public void start() {
			
		}
		public void run() {
			t--;
			requestUpdate();
			if(t < 0) {
				destroyAll();
			}
		}

		public boolean condition() {
			return exists;
		}
	}

}