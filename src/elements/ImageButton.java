package elements;

import static core.MainProgram.*;
import static util.DB.*;
import processing.core.PImage;

public class ImageButton extends AbstractButton {

	PImage image;
	boolean fill;
	float imx = 0, imy = 0;

	public ImageButton(float x, float y, float w, float h, PImage image, Container p) {
		super(x, y, w, h, p);
		this.image = image;
	}

	protected void update() {
		resetGraphics();
		if (fill) {
			g.image(image, 0, 0, w, h);
		} else {
			g.image(image, imx, imy);
		}
	}

	public void setImage(PImage image) {
		this.image = image;
		requestUpdate();
	}

	public void setImagePos(float x, float y) {
		fill = false;
		requestUpdate();
	}

	public void fill() {
		fill = true;
		requestUpdate();
	}

	public void elementClicked() {
	}

	public void elementReleased() {
	}

	public void elementHovered() {
	}

	public void elementUnhovered() {
	}
}