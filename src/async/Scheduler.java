package async;

import static core.MainProgram.*;
import static processing.core.PApplet.println;
import static util.DB.*;

import java.util.ArrayList;

import util.Heap;

public class Scheduler {

	private float period = 50;
	public void setPeriod(float val) {period = val;}
	public float getPeriod() {return period;}
		
	
	private Thread schedulerThread;
	private Heap<AsyncEvent> schedule = new Heap<AsyncEvent>();
	private ArrayList<ActiveAsyncEvent> active = new ArrayList<ActiveAsyncEvent>();
	
	private boolean started = false;
	public boolean isStarted() {return started;}
	
	public void start() {
		if(!started) {
			schedulerThread = new Thread(new AsyncThread(), "AsyncThread");
			schedulerThread.start();
			started = true;
		}
	}
	// scheduler logic goes here
	void globalEventLoop() {
		checkSchedule();
		runActive();
	}

	// schedules call for async event as soon as possible
	public void call(AsyncEvent e) {
		addToSchedule(0, e);
	}

	// schedules call of an async event only after a specific system time.
	public void callAt(AsyncEvent e, int millis) {
		addToSchedule(millis, e);
	}

	// schedules call of an async event millis into the future
	public void callLater(AsyncEvent e, int millis) {
		addToSchedule(p3.millis() + millis, e);
	}

	void checkSchedule() {

		while (!schedule.isEmpty() && schedule.first() <= p3.millis()) {

			AsyncEvent event = schedule.pop();

			DB_U("Schedule Updated:", schedule);
			if (event instanceof ActiveAsyncEvent) {
				ActiveAsyncEvent activeEvent = (ActiveAsyncEvent) event;
				active.add(activeEvent);
				DB_U("Active Added", activeEvent);
				activeEvent.start();
			} else {
				event.run();
			}
		}
	}

	// currently removes listener the moment condition is found to be false.
	// prehaps there could be some way to keep listeners in and only remove on
	// request.
	void runActive() {
		for (ActiveAsyncEvent e : new ArrayList<ActiveAsyncEvent>(active)) {
			if (e.condition())
				e.run();
			else {
				e.stop();
				active.remove(e);
			}
		}
	}

	void addToSchedule(int time, AsyncEvent event) {
		schedule.add(time, event);
	}
	
	

	public void testScheduler() {
		callLater(new testEvent(300), 2200);
		call(new testEvent(1200));
		callLater(new testEvent(1500), 1000);
		callAt(new testEvent(100), 3000);
		callAt(new testEvent(200), 3000);
	}
	// thread runs at 20 HZ on average.
	class AsyncThread implements Runnable {
		// Program time. Updates will be faster if behind system time.
		int time;
		
		AsyncThread() {
			time = p3.millis();
		}

		public void run() {
			println("start", time);
			while (true) {
				globalEventLoop();
				try {
					// sleep until period has elapsed since last call.
					Thread.sleep((long) max(time - p3.millis() + period, 0));
				} catch (InterruptedException e) {
					DB_W("AsyncThread Interrupted");
				}
				time += period;
			}
		}

	}
}



class testEvent extends ActiveAsyncEvent {
	int t;
	int period;

	testEvent(int period) {
		this.period = period;
	}

	public void start() {
		t = p3.millis() + period;
	}

	public void run() {
		println(period, p3.millis());
	}

	public boolean condition() {
		return t > p3.millis();
	}
}

/*
 package async;

import static core.MainProgram.*;
import static processing.core.PApplet.println;
import static util.DB.*;

import java.util.ArrayList;

import util.Heap;

public class Scheduler {
	static final int CYCLE_PERIOD = 50;
	
	static Heap<AsyncEvent> schedule = new Heap<AsyncEvent>();
	static ArrayList<ActiveAsyncEvent> active = new ArrayList<ActiveAsyncEvent>();
	static boolean started = false;
	static Thread schedulerThread;
	
	public static void start() {
		if(!started) {
			schedulerThread = new Thread(new AsyncThread(CYCLE_PERIOD), "AsyncThread");
			schedulerThread.start();
			started = true;
		}
	}
	// scheduler logic goes here
	static void globalEventLoop() {
		checkSchedule();
		runActive();
	}

	// schedules call for async event as soon as possible
	public static void call(AsyncEvent e) {
		addToSchedule(0, e);
	}

	// schedules call of an async event only after a specific system time.
	public static void callAt(AsyncEvent e, int millis) {
		addToSchedule(millis, e);
	}

	// schedules call of an async event millis into the future
	public static void callLater(AsyncEvent e, int millis) {
		addToSchedule(p3.millis() + millis, e);
	}

	static void checkSchedule() {

		while (!schedule.isEmpty() && schedule.first() <= p3.millis()) {

			AsyncEvent event = schedule.pop();

			DB_U("Schedule Updated:", schedule);
			if (event instanceof ActiveAsyncEvent) {
				ActiveAsyncEvent activeEvent = (ActiveAsyncEvent) event;
				active.add(activeEvent);
				DB_U("Active Added", activeEvent);
				activeEvent.start();
			} else {
				event.run();
			}
		}
	}

	// currently removes listener the moment condition is found to be false.
	// prehaps there could be some way to keep listeners in and only remove on
	// request.
	static void runActive() {
		for (ActiveAsyncEvent e : new ArrayList<ActiveAsyncEvent>(active)) {
			if (e.condition())
				e.run();
			else {
				active.remove(e);
				DB_U("Active Updated:", e);
			}
		}
	}

	static void addToSchedule(int time, AsyncEvent event) {
		schedule.add(time, event);
		DB_U("Schedule Updated:", schedule);
	}

	public static void testScheduler() {
		Scheduler.callLater(new testEvent(300), 2200);
		Scheduler.call(new testEvent(1200));
		Scheduler.callLater(new testEvent(1500), 1000);
		Scheduler.callAt(new testEvent(100), 3000);
		Scheduler.callAt(new testEvent(200), 3000);
	}

}

// thread runs at 20 HZ on average.
class AsyncThread implements Runnable {
	// Program time. Updates will be faster if behind system time.
	int time;
	final int period;
	
	AsyncThread(int period) {
		this.period = period;
		time = p3.millis();
	}

	public void run() {
		println("start", time);
		while (true) {
			Scheduler.globalEventLoop();
			try {
				// sleep until period has elapsed since last call.
				Thread.sleep(max(time + period - p3.millis(), 0));
			} catch (InterruptedException e) {
				DB_W("AsyncThread Interrupted");
			}
			time += period;
		}
	}

}

class testEvent extends ActiveAsyncEvent {
	int t;
	int period;

	testEvent(int period) {
		this.period = period;
	}

	public void start() {
		t = p3.millis() + period;
	}

	public void run() {
		println(period, p3.millis());
	}

	public boolean condition() {
		return t > p3.millis();
	}
}

 */
