package shapes_unused;

import static core.MainProgram.*;
import static util.DB.*;

import elements.Element;
import processing.core.PShape;
import elements.Container;

//allows for update value of 2, signifies shape update.
public abstract class Shape extends Element {

	PShape shape;

	Shape(float x, float y, float w, float h, Container p) {
		super(x, y, w, h, p);

		updatable = 2;
	}

	// shapes are intended to be usually static, so most updates will not update the
	// shape. Activated when updatable == 2
	abstract void updateShape();
	
	

	@Override
	protected void checkUpdates() {
		// if an update has been queued for this element do the business

		if (updatable > 0) {
			if (updatable == 2) {
				updateShape();
				DB_U(this, "update2");
			}
			updatable = 0;
			g.beginDraw();
			update();
			g.endDraw();
		}

	}

}

// a flexible SVG drawing class.
class SVGImage extends Shape {

	SVGImage(float x, float y, float w, float h, PShape s, Container p) {
		super(x, y, w, h, p);
		shape = s;
	}

	void updateShape() {
		// dont know what to put here yet
	}

	// not done, fix shape positioning and scale
	protected void update() {
		resetGraphics();
		g.shape(shape, 0, 0);
	}

}
