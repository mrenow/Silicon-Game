package elements;

import static core.MainProgram.*;
import static util.DB.*;

import processing.core.PFont;

public class Text extends Element {

	final int DEFAULT_TEXTFILL = p3.color(0);

	final PFont DEFAULT_FONT = p3.createFont("Arial", 20);

	String text;
	int textfill;
	PFont font;
	int alignx, aligny;

	// does nothing atm
	boolean textwrapping;

	public Text(float x, float y, float w, float h, String text, Container parent) {
		super(x, y, w, h, parent);

		this.text = text;
		this.parent = parent;
		textfill = DEFAULT_TEXTFILL;
		font = DEFAULT_FONT;
		alignx = LEFT;
		aligny = TOP;

	}

	@Override
	protected
	void update() {
		resetGraphics();

		g.fill(textfill);
		g.textFont(font);
		g.textAlign(alignx, aligny);
		g.text(text, 0, 0, w, h);

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
