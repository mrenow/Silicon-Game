package elements;

import static core.MainProgram.*;
import static util.DB.*;
import processing.core.PImage;

public class ImageButton extends AbstractButton {

	
	
	public final static byte NORMAL = 0;
	public final static byte CENTERED = 1;
	public final static byte FILL = 2;
//	final static byte SCALE = 3;
	
	byte mode = 0;
	
	PImage image;
	float imx = 0, imy = 0;
	
	boolean flipped;

	public ImageButton(float x, float y, float w, float h, PImage image, Container p) {
		super(x, y, w, h, p);
		this.image = image;
	}

	protected void update() {
		resetGraphics();
		
		switch (mode) {
		case NORMAL:
			g.image(image, imx, imy);
			break;
		case CENTERED:
			g.imageMode(CENTER);
			g.image(image, getWidth()/2, getHeight()/2);
			break;
		case FILL:	
			g.image(image, 0, 0, getWidth(), getHeight());
			break;
		
			}
	}

	public void setImage(PImage image) {
		this.image = image;
		requestUpdate();
	}

/*	public void setImagePos(float x, float y) {
		fill = false;
		requestUpdate();
	}*/
	public void setMode(byte m) {
		mode = m;
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