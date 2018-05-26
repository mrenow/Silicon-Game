package elements;

import processing.core.PImage;

public class Image extends Element{

	PImage img;
	
	public Image(float x, float y, float w, float h, PImage img, Container p) {
		super(x, y, w, h, p);
		this.img = img;
	}
	
	public Image(float x, float y, float scale, PImage img,Container p) {
		super(x, y, img.width*scale, img.height*scale, p);
		this.img = img;
		// TODO Auto-generated constructor stub
	}
	public Image(float x, float y, PImage img,Container p) {
		super(x, y, img.width, img.height, p);
		this.img = img;
	}
	
	protected void update() {
		resetGraphics();
		g.image(img, 0, 0, getWidth(), getHeight());
	}
	
	
	
	
}
