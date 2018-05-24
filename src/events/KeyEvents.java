package events;

import java.util.ArrayList;
import java.awt.Component;
import java.awt.event.KeyEvent;

import static core.MainProgram.*;

// Forgive my sins, its the only practical way I could think of to get the key constants.
public class KeyEvents extends KeyEvent{

	//dont use, just a hack
	public KeyEvents(Component arg0, int arg1, long arg2, int arg3, int arg4) {
		super(arg0, arg1, arg2, arg3, arg4);
		// TODO Auto-generated constructor stub
	}

	public static boolean[] key = new boolean[65536];
	public static int code = 0;
	public static ArrayList<KeyListener> list = new ArrayList<KeyListener>();

	public static void add(KeyListener e) {
		list.add(e);
	}

	public static void pressCheck() {		
		code = p3.keyCode;
		key[p3.keyCode] = true;
		
		// Stops processing from exiting on escape.
		// However this requires full reliance on the new system
		p3.keyCode = 0;
		p3.key = 0;
		
		for (KeyListener e : new ArrayList<KeyListener>(KeyEvents.list)) {
			e.keyPressed();
		}
	}

	public static void releaseCheck() {
		key[p3.keyCode] = false;
		for (KeyListener e : new ArrayList<KeyListener>(KeyEvents.list)) {
			e.keyReleased();
		}
	}

	public static void typedCheck() {
		for (KeyListener e : new ArrayList<KeyListener>(KeyEvents.list)) {
			e.keyTyped();
		}
	}
}
