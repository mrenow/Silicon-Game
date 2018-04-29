package elements;

import static core.MainProgram.*;

import static util.DB.*;

import events.KeyEvents;
import events.KeyListener;
import events.ScrollEvents;
import events.ScrollListener;
import processing.core.PVector;

//Default usage of srcoll pane involves placing a large sized component into a smaller space. Scrolling in x, y or both must be specified.
//Draws a transparent grey rectangle for the scrollbar 
public class ScrollPane extends Container implements ScrollListener, KeyListener {

	// x,y,xy
	int mode;
	Element pane;
	float maxscrollx;
	float maxscrolly;
	// may implement velocity scrolling later, currently is useless.
	/*
	 * float scrollvel; float SCROLLDRAG;
	 */

	PVector offset = new PVector(0, 0);

	public ScrollPane(float x, float y, float w, float h, Element pane, int mode, Container p) {
		super(x, y, w, h, p);
		this.mode = mode;
		maxscrollx = pane.w - w;
		maxscrolly = pane.h - h;
		this.pane = pane;
		add(this.pane);
		this.pane.setPos(0, 0);
		ScrollEvents.add(this);
		KeyEvents.add(this);
	}

	public void elementScrolled(int value) {
		DB_U(this, "scrolled", value);
		switch (mode) {
		case 0:
			movePane(0, value);
			break;
		case 1:
			movePane(value, 0);
			break;
		case 2:
			if (KeyEvents.key[SHIFT]) {

				movePane(value, 0);
			} else {

				movePane(0, value);
			}
			break;
		}
	}

	public void movePane(float x, float y) {
		offset.x += x;
		offset.y += y;
		DB_U(offset, maxscrollx, maxscrolly);
		offset.x = min(offset.x, maxscrollx);
		offset.y = min(offset.y, maxscrolly);
		offset.x = max(offset.x, 0);
		offset.y = max(offset.y, 0);

		pane.setPos(-offset.x, -offset.y);
	}

	public void elementHovered() {
	}

	public void elementUnhovered() {
	}

	public void keyPressed() {
		if (cursorhover) {
			if (KeyEvents.key[UP]) {

				movePane(0, -4);
			}
			if (KeyEvents.key[DOWN]) {
				movePane(0, 4);
			}
			if (KeyEvents.key[LEFT]) {
				movePane(-4, 0);
			}
			if (KeyEvents.key[RIGHT]) {
				movePane(4, 0);
			}
			requestUpdate();
		}
	}

	public void keyReleased() {
	}

	public void keyTyped() {
	}

	protected void update() {
		super.update();
		g.noStroke();
		g.fill(0, 0, 0, 60);
		DB_U(offset);
		switch (mode) {
		case 0:
			g.rect(2, 2 + offset.y * (h - 4) / pane.h, 10, h * h / pane.h);
			break;
		case 1:
			g.rect(2 + offset.x * (w - 4) / pane.h, 2, w * w / pane.w, 10);
			break;
		case 2:
			g.rect(2, 2 + offset.y * (h - 4) / pane.h, 10, (h - 4) * (h - 4) / pane.h);
			g.rect(2 + offset.x * (w - 4) / pane.h, 2, (w - 4) * (w - 4) / pane.w, 10);
			break;
		}
	}
}