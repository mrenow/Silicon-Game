package elements;

import static core.MainProgram.*;
import static util.DB.*;

import processing.core.PFont;
import processing.core.PVector;

public class Text extends Element {

	final int DEFAULT_TEXTFILL = p3.color(0);

	final PFont DEFAULT_FONT = p3.createFont("Arial", 20);

	String text;
	int	textfill = DEFAULT_TEXTFILL;
	PFont font = DEFAULT_FONT;
	int alignx = LEFT, aligny = TOP;

	// does nothing atm
	boolean textwrapping;

	public Text(float x, float y, float w, float h, String text, Container parent) {
		super(x, y, w, h, parent);
		this.text = text;
		
	}
	public Text(float x, float y, String text, Container p) {
		this(x,y,0,0, text, p);
		tightFit();
	}
	
	public Text(String text, Container p) {
		this(0,0, text, p);
	}
	
	@Override
	public PVector getExtent() {
		float currwidth = 0, maxwidth = 0;
		float currheight = 1;
		// Return max line length and count number of newlines
		for (int i=0; i<text.length(); i++) {
			if (text.charAt(i) == '\n') {
				if (currwidth > maxwidth) maxwidth = currwidth;
				currwidth = 0;
				currheight += 1;
				continue;
			}
			currwidth += font.width(text.charAt(i));
		}
		if (currwidth > maxwidth) maxwidth = currwidth;
		// +4 required to accomodate for weird undocumented padding with the textbox rendering.
		return new PVector(maxwidth*font.getSize() + 4, currheight*(font.getSize()+font.ascent()+ font.descent()) + 4);
	}
	
	@Override
	protected void update() {
		resetGraphics();

		g.fill(textfill);
		g.textFont(font);
		g.textAlign(alignx, aligny);
		g.text(text, 0, 0, getWidth(), getHeight());

	}
	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
		requestUpdate();

	}

	public void setFont(String fontname, float size) {
		font = p3.createFont(fontname, size);
		requestUpdate();
	}

	public void setAlign(int x, int y) {
		alignx = x;
		aligny = y;
		requestUpdate();

	}
}
