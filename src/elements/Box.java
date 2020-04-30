package elements;

import static core.MainProgram.*;
import static util.DB.*;

// A plain rectangle
// We all gotta start somewhere

public class Box extends Element {

	final int DEFAULT_STROKE = p3.color(0);
	final int DEFAULT_BGFILL = p3.color(255);

	// int data
	int stroke;
	int bgfill;

	public Box(float x, float y, float w, float h, Container parent) {
		super(x, y, w, h, parent);
		stroke = DEFAULT_STROKE;
		bgfill = DEFAULT_BGFILL;
	}

	protected void update() {
		resetGraphics();

		g.stroke(stroke);
		g.fill(bgfill);
		g.rect(0, 0, w, h);

	}

	public void setStroke(float r, float g, float b) {
		stroke = p3.color(r, g, b);
		requestUpdate();
	}

	public void setFill(float r, float g, float b) {
		bgfill = p3.color(r, g, b);
		requestUpdate();
	}

	public void setStroke(int c) {
		stroke = p3.color(c);
		requestUpdate();
	}

	public void setFill(int c) {
		bgfill = p3.color(c);
		requestUpdate();
	}

}
