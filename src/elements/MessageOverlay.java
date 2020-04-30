package elements;

import java.util.ArrayList;
import java.util.Arrays;

import effects.FadeContainer;

// Organizes messages to appear down the screen
public class MessageOverlay extends Container{
	//Ceebs enum
	public static final boolean MESSAGE_UP = false;
	public static final boolean MESSAGE_DOWN = true;
	public static final int DEFAULT_VERTSPACE = 5;
	public static final int DEFAULT_DURATION = 30;
	
	public boolean direction = MESSAGE_DOWN;
	public float vertspace = DEFAULT_VERTSPACE;
	public int duration = DEFAULT_DURATION;
	
	public MessageOverlay(float x, float y, float w, float h, Container p) {
		super(x, y, w, h, p);
	}
	public MessageOverlay(Container p) {
		super(p.pos.x ,p.pos.y, p.getWidth(), p.getHeight(), p);
	}
	
	
	@Override
	protected void update(){
		float nexty = 0;
		
		if(direction == MESSAGE_DOWN) {
			for(Element e: new ArrayList<Element>(children)) {
				e.setPos(0, nexty);
				nexty += e.getHeight() + vertspace;
			}
		}else {
			for(Element e: new ArrayList<Element>(children)) {
				nexty -= e.getHeight() + vertspace;
				e.setPos(0, nexty);
			}
		}
		super.update();
	}
	
	public void addMessage(String message) {
		// Padding of 50, bottom left
		insert(0,new FadeContainer(0,0, duration, null, new Text(message, null)));
	}
}
