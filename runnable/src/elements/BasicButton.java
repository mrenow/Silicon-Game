package elements;

import static core.MainProgram.*;
import static util.DB.*;

import events.ClickEvents;

//text functionality, hover highlighting, button highlighting 
public class BasicButton extends AbstractButton {

	int DEFAULT_BUTTSTROKE = p3.color(142, 169, 204);
	int DEFAULT_BUTTBGFILL = p3.color(153, 217, 234);

	final int DEFAULT_STROKEHOVER = p3.color(188, 230, 243);
	final int DEFAULT_FILLPRESSED = p3.color(188, 230, 243);

	/*
	 * int fillinactive; int strokeinactive;
	 */
	private int strokehover;
	private int fillpressed;
	private int stroke;
	private int fill;
	
	TextBox textbox;

	public BasicButton(float x, float y, float w, float h, String text, Container parent) {
		super(x, y, w, h, parent);

		strokehover = DEFAULT_STROKEHOVER;
		fillpressed = DEFAULT_FILLPRESSED;
		stroke = DEFAULT_BUTTSTROKE;
		fill = DEFAULT_BUTTBGFILL;
		
		
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
		if(enabled) {
			textbox.setFill(fillpressed);
			pressed = true;
		}
	}

	public void elementReleased() {
		if(enabled) {
			textbox.setFill(fill);
			pressed = false;
		}
	}

	public void elementHovered() {
		if(enabled) {
			textbox.setStroke(strokehover);
		}
	}

	public void elementUnhovered() {
		if(enabled) {
			textbox.setStroke(stroke);
			if (p3.mousePressed) {
				elementReleased();
			}
		}
	}
	public void buttonDisabled() {
		textbox.setFill(fillpressed);
		textbox.setStroke(strokehover);
	}
	public void buttonEnabled() {
		textbox.setFill(fill);
		textbox.setStroke(stroke);
	}
	public int getStroke() {
		return stroke;
	}

	public void setStroke(int stroke) {
		this.stroke = stroke;
		if(!cursorhover && enabled) {
			textbox.setStroke(stroke);
		}
	}
	public int getStrokeHovered() {
		return strokehover;
	}

	public void setStrokeHovered(int strokehover) {
		this.strokehover = strokehover;
		if(cursorhover && enabled) {
			textbox.setStroke(strokehover);
		}
	}
	
	public int getFill() {
		return fill;
	}

	public void setFill(int fill) {
		this.fill = fill;
		if(!(cursorhover && p3.mousePressed) && enabled) {
			textbox.setFill(fill);	
		}
	}
	
	public int getFillPressed() {
		return fillpressed;
	}

	public void setFillPressed(int fillpressed) {
		this.fillpressed = fillpressed;
		if(cursorhover && p3.mousePressed && enabled) {
			textbox.setStroke(fillpressed);
		}
	}




}
