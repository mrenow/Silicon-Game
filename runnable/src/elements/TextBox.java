package elements;

import static core.MainProgram.*;
import static util.DB.*;
//Fields: text, padding,border,line spacing, text color, font/size , ?text wrap , default values for all layout and int included.

public class TextBox extends Container {

	final float DEFAULT_PADDING = 10;
	final float DEFAULT_LINEGAP = 0;

	Text textarea;
	Box box;

	float padding;
	float border;
	float linegap;

	// basic constructor for rectangle bounds and text
	public TextBox(float x, float y, float w, float h, String t, Container parent) {
		super(x, y, w, h, parent);
		box = new Box(0, 0, w, h, this);
		textarea = new Text(0, 0, w, h, t, this);
		this.parent = parent;
		setPadding(DEFAULT_PADDING);
		// unused
		/*
		 * padding = DEFAULT_PADDING; linegap = DEFAULT_LINEGAP;
		 */
	}

	// DODGY FIX AFTER GAME
	public void setText(String text) {
		textarea.setText(text);
		requestUpdate();
	}

	public void setStroke(float r, float g, float b) {
		box.setStroke(r, g, b);
		requestUpdate();
	}

	public void setFill(float r, float g, float b) {
		box.setFill(r, g, b);
		requestUpdate();
	}

	public void setStroke(int c) {
		box.setStroke(c);
		requestUpdate();
	}

	public void setFill(int c) {
		box.setFill(c);
		requestUpdate();
	}

	public void setFont(String s, float size) {
		textarea.setFont(s, size);
		requestUpdate();
	}

	public void setPadding(float p) {
		textarea.setPos(p, p);
		textarea.setDimensions(w - 2 * p, h - 2 * p);
		requestUpdate();
	}
}