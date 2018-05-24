package elements;
import static core.MainProgram.*;
import static util.DB.*;

import events.ClickEvents;
import events.ClickListener;
//A button is a container without graphics that allows for a click and hover event.
//All visible parts of a button are derived from its children.
//Functions: classes that extend must implement ClickListener
public abstract class AbstractButton extends Container implements ClickListener{
	
	protected boolean enabled = true;
	boolean pressed = false;
	
	protected AbstractButton(float x, float y, float w, float h, Container parent){
		super(x,y,w,h,parent);
		ClickEvents.add(this);
	}
	
	public void setEnabled(boolean val) {
		if(enabled != val) {
			if(enabled) {
				if(pressed) {
					elementReleased();
				}
				buttonDisabled();				
			}else {
				buttonEnabled();
			}
			enabled = val;
		}
	}
	public void buttonDisabled() {}
	public void buttonEnabled() {}
	
}
