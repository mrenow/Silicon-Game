package events;

import java.util.ArrayList;

import elements.Element;
import processing.event.MouseEvent;

public class ScrollEvents {

	public static ArrayList<ScrollListener> list = new ArrayList<ScrollListener>();

	public static void check(MouseEvent event) {
		for (ScrollListener e : list) {
			if (((Element) e).cursorhover) {
				e.elementScrolled(event.getCount());
				break;
			}
		}
	}

	public static void add(ScrollListener e) {
		HoverEvents.add(e);
		list.add(e);
	}
}
