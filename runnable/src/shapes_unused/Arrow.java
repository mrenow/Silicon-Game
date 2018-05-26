package shapes_unused;

import static core.MainProgram.*;
import static util.DB.*;

import elements.Container;

//arrow is drawn from point
public class Arrow extends Shape {

	// in radians
	float direction;

	// other dimensions.
	// if the head width is too large it will draw off the graphics context.
	// No I am not handling that exception. Your bad design taste, your loss.

	float linelength;
	float headlength;
	float linewidth;
	float headwidth;
	// distance of point from specified destination.
	float offset;

	float DEFAULT_OFFSET = 10;

	public Arrow(float x, float y, float hw, float hl, float lw, float ll, float tilt, Container p) {
		super(x, y, ll + 10, hw + lw * 2, p);
		direction = tilt;
		linelength = ll;
		headlength = hl;
		linewidth = lw;
		headwidth = hw;
		offset = DEFAULT_OFFSET;

	}

	// will rotate the component before drawing.
	@Override
	protected void applyTransform() {
		// translate 0,0 to tip
		pg().translate(pos.x + getWidth(), pos.y + getHeight() / 2);
		// rotate around tip
		pg().rotate(direction);
		pg().translate(-getWidth() - offset, -getHeight() / 2);
	}

	@Override
	protected void update() {
		resetGraphics();
		g.fill(255, 0, 0);
		g.shape(shape, 0, 0);
	}

	protected void updateShape() {
		// It draws an arrow.
		// trust me.
		shape = p3.createShape();
		shape.beginShape();

		shape.fill(0);
		shape.strokeWeight(12);
		shape.vertex(0, (headwidth - linewidth) / 2);
		shape.vertex(linelength - headlength, (headwidth - linewidth) / 2);
		shape.vertex(linelength - headlength, 0);
		shape.vertex(linelength, headwidth / 2);
		shape.vertex(linelength - headlength, headwidth);
		shape.vertex(linelength - headlength, (headwidth + linewidth) / 2);
		shape.vertex(0, (headwidth + linewidth) / 2);
		shape.endShape(CLOSE);
		DB_U("updateshape");

	}

	// modify transform
	public void addRotation(float ang) {
		direction += ang;
		requestUpdate();
	}

	public void setRotation(float ang) {
		direction = ang;
		requestUpdate();
	}

	public void setOffset(float dist) {
		offset = dist;
		requestUpdate();

	}
}