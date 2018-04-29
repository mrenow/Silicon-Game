package events;

import java.util.ArrayList;

import elements.Element;

public class MovementEvents {
	public static ArrayList<MovementListener> list = new ArrayList<MovementListener>();

	public static void check() {
		for (int i = list.size() - 1; i >= 0; i--) {
			MovementListener e = list.get(i);
			if (((Element) e).cursorhover) {
				e.mouseMoved();
			}
		}
	}

	public static void add(MovementListener e) {
		list.add(e);
	}
}
