package events;

import static core.MainProgram.p3;

import java.util.ArrayList;

import elements.Element;
import processing.core.PVector;

public class HoverEvents {
	public static ArrayList<HoverListener> list = new ArrayList<HoverListener>();

	public static void add(HoverListener e) {
		list.add(e);
	}

	public static void check() {
		// copy is made of list since list can change
		boolean blocked = false; // Block all other hover events if an element has already been seen
		for (HoverListener e : new ArrayList<HoverListener>(list)) {
			boolean nextcursorhover = blocked? false : cursorWithinBounds((Element) e);
			// if cursorhover changes state, one of the hover events will activate.
			if (((Element) e).cursorhover != nextcursorhover) {
				// if cursor hover is initially false, it will change to true and activate the
				// hover event.
				if (((Element) e).cursorhover)
					e.elementUnhovered();
				else
					e.elementHovered();
				((Element) e).cursorhover = nextcursorhover;
			}
		}
	}

	static boolean cursorWithinBounds(Element e) {
		// if the element is not visible, do not listen for cursor events
		if (!e.isConcrete() || !e.isVisible())
			return false;
		PVector pos = e.getGlobalPos();
		return (p3.mouseX >= pos.x && p3.mouseX <= e.getWidth() + pos.x)
				&& (p3.mouseY >= pos.y && p3.mouseY <= e.getHeight() + pos.y);
	}
}
