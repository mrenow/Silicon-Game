package events;

import java.util.ArrayList;
import static core.MainProgram.*;

public class KeyEvents {
	public static boolean[] key = new boolean[65536];
	public static int code = 0;
	public static ArrayList<KeyListener> list = new ArrayList<KeyListener>();

	public static void add(KeyListener e) {
		list.add(e);
	}

	public static void pressCheck() {
		code = p3.keyCode;
		key[p3.keyCode] = true;
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
