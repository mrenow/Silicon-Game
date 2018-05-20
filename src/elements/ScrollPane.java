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
	public static final byte SCROLL_X = 0;
	public static final byte SCROLL_Y = 1;
	public static final byte SCROLL_XY = 2;
	
	byte mode;
	float panewidth;
	float paneheight;
	// may implement velocity scrolling later, currently is useless.
	/*
	 * float scrollvel; float SCROLLDRAG;
	 */

	PVector offset = new PVector(0, 0);

	public ScrollPane(float x, float y, float w, float h, byte mode, Container p) {
		super(x, y, w, h, p);
		this.mode = mode;
		panewidth = w;
		paneheight = h;
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
		offset.x = constrain(offset.x, 0, panewidth - getWidth());
		offset.y = constrain(offset.y, 0, paneheight - getHeight());
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
		DB_U(offset);

		g.pushMatrix();
		g.translate(-offset.x, -offset.y);
		super.update();
		g.popMatrix();
		
		g.noStroke();
		g.fill(0, 0, 0, 60);
		switch (mode) {
		case 0:
			g.rect(2, 2 + offset.y * (h - 4) / paneheight, 10, h * h / paneheight);
			break;
		case 1:
			g.rect(2 + offset.x * (w - 4) / panewidth, 2, w * w / panewidth, 10);
			break;
		case 2:
			g.rect(2, 2 + offset.y * (h - 4) / paneheight, 10, (h - 4) * (h - 4) / paneheight);
			g.rect(2 + offset.x * (w - 4) / panewidth, 2, (w - 4) * (w - 4) / panewidth, 10);
			break;
		}
	}
}