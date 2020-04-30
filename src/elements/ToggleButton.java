package elements;

import static core.MainProgram.p3;
import static processing.core.PConstants.CENTER;

import events.ClickEvents;

public class ToggleButton extends BasicButton{
	
	
	
	public ToggleButton(float x, float y, float w, float h, String text, Container parent) {
		super(x, y, w, h, text, parent);

		ClickEvents.add(this);
	}

	@Override
	protected
	void update() {
		super.update();
		// t.setStroke(t.DEFAULT_STROKE);
	}
	// Misuse of OOP... oh well, in the name of code reuse I guess
	@Override
	public void elementClicked() {
		if(enabled) {
			textbox.setFill(fillpressed);
			pressed = !pressed;
		}
		textbox.setFill(pressed ? fillpressed : fill);
		
	}
	@Override
	public void elementReleased() {
	}
	
	public void toggle() {
		pressed = !pressed;
	}
	public void setToggled(boolean t) {
		pressed = t;
	}
	public boolean isToggled(){
		return pressed;
	}

	void setText(String text) {
		textbox.setText(text);
	}

}
