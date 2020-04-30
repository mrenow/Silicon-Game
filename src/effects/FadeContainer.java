package effects;

import elements.Container;
import elements.Element;

/*
 * Slowly fades and then destroys itself, eg in a  
 * 
 */
public class FadeContainer extends Effect {
	
	
	public FadeContainer(float x, float y, float w, float h, int duration, Container p) {
		super(x, y, w, h, duration, p);
	}
	public FadeContainer(float x, float y, int duration, Container p, Element ... children) {
		super(x, y, duration, p, children);
	}
	
	@Override
	protected void applyTransform() {
		super.applyTransform();
		pg().tint(255, 255 * getProgress());
	}

	
}
