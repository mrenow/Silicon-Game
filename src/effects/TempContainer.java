package effects;

import effects.Effect.EventLoop;
import elements.Container;
import elements.Element;

// Displays a component which dissapears after a given number of ticks.
public class TempContainer extends Effect{
	TempContainer(float x, float y, float w, float h, int duration,  Container p) {
		super(x, y, w, h, duration, p);
	}
	TempContainer(float x, float y, int duration, Container p, Element ... children) {
		super(x, y, duration, p, children);
	}
}

