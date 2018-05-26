package events;

import static util.DB.*;

import java.util.ArrayList;

import elements.Element;

public class ClickEvents {
	public static ArrayList<ClickListener> list = new ArrayList<ClickListener>();

	public static void clickCheck() {
		for (int i = list.size() - 1; i >= 0; i--) {
			ClickListener e = list.get(i);
			if (((Element) e).cursorhover) {
				e.elementClicked();
				DB_A(e, "pressed", ((Element) e).isConcrete());
				break;
			}
		}
	}

	public static void releaseCheck() {
		for (int i = list.size() - 1; i >= 0; i--) {
			ClickListener e = list.get(i);
			if (((Element) e).cursorhover) {
				e.elementReleased();
				break;
			}
		}
	}

	public static void add(ClickListener e) {
		HoverEvents.list.add(e);
		list.add(e);
	}
}