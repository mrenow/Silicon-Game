package events;

import java.util.ArrayList;

import elements.Element;

public class Events {
	public static void check() {
		HoverEvents.check();
	}

	public static ArrayList[] alllisteners = { HoverEvents.list, ClickEvents.list, KeyEvents.list, MovementEvents.list,
			FocusEvents.list, ScrollEvents.list };

	public static void remove(Element object) {
		for (ArrayList list : alllisteners) {
			list.remove(object);
		}
	}
}
