package elements;

import static core.MainProgram.*;
import static util.DB.*;

import events.ClickEvents;

//text functionality, hover highlighting, button highlighting 
public class BasicButton extends AbstractButton {
	boolean pressed = false;

	int DEFAULT_BUTTSTROKE = p3.color(142, 169, 204);
	int DEFAULT_BUTTBGFILL = p3.color(153, 217, 234);

	final int DEFAULT_STROKEHOVER = p3.color(188, 230, 243);
	final int DEFAULT_FILLPRESSED = p3.color(188, 230, 243);

	/*
	 * int fillinactive; int strokeinactive;
	 */
	int strokehover;
	int fillpressed;

	TextBox textbox;

	public BasicButton(float x, float y, float w, float h, String text, Container parent) {
		super(x, y, w, h, parent);

		strokehover = DEFAULT_STROKEHOVER;
		fillpressed = DEFAULT_FILLPRESSED;
		textbox = new TextBox(0, 0, w, h, text, this);
		textbox.setFill(DEFAULT_BUTTBGFILL);
		textbox.setStroke(DEFAULT_BUTTSTROKE);
		add(textbox);
		textbox.setPadding(0);
		textbox.textarea.setAlign(CENTER, CENTER);

		ClickEvents.add(this);
	}

	@Override
	protected
	void update() {
		super.update();
		// t.setStroke(t.DEFAULT_STROKE);
	}

	void setText(String text) {
		textbox.setText(text);
	}

	public void elementClicked() {
		textbox.setFill(fillpressed);
		pressed = true;
	}

	public void elementReleased() {
		textbox.setFill(DEFAULT_BUTTBGFILL);
		pressed = false;
	}

	public void elementHovered() {
		textbox.setStroke(strokehover);
	}

	public void elementUnhovered() {
		textbox.setStroke(DEFAULT_BUTTSTROKE);
		if (p3.mousePressed) {
			textbox.setFill(DEFAULT_BUTTBGFILL);
			pressed = false;
		}
	}
}
