package elements;

import processing.core.PVector;
import processing.core.PGraphics;
import static core.MainProgram.*;
import static util.DB.*;

import events.Events;
import events.Listener;
// Documentation fail
//Fields: position, size, graphics, parent ?visibility, ?cursorhovering(only when clickListener is implemented), ?updaterequest
//Functions: setters(pos,size,vis), request update, draw self, reset graphics
/*
 * Unfortunately, not the most thread safe system in existence.
 * A fix for this system is to give Elements a lock protected draw state buffer - all params 
 * that are required for drawing the object are copied to the buffer on request update.
 * This way we can have an arbitrary thread doing object state updates, while a universal drawing
 * thread can schedule draw operations. Cpu usage can further be optimized by writing a breadth-first
 * drawing protocol which can skip locked states where the buffer is being written to.
 * 
 * Heres a slightly ugly and unsafe, but somewhat implementable variant of the above. We define a
 * copy-for-draw function for the given element, which only copies the data needed for drawing
 * to the object, and sets a boolean to indicate its role. This object is then passed normally to
 * the drawing thread. This also means that each container needs to store two copies of each child...
 * Request update also needs to somehow write to that child list. 
 * 
 * More musings: What does requestUpdate mean?
 * I propose that it means "My state changed a little, but is now consistent. Could you change me to reflect that"
 * in which case copying the whole structure for each delta is kinda dumb.
 * So why not have function that service the appropriate deltas inside requestUpdate()?
 * We could have all the required data bundled in a data structure (damnit not this again)
 * where accessing its members causes it to queue deltas to a buffer structure.
 * requestUpdate loads these deltas into the buffer and signals the object for redrawing.
 * On draw, the parent calls update(), which reads the buffer contents. This operaton is mutually
 * exclusive with loading, and therefore is mutually exclusive with request update.
 * We can optimize this further with double buffering. Now we can continue loading deltas into the 
 * buffer that is not being read while drawing is occurring. 
 * THIS ALL HINGES ON HAVING A STATE OBJECT WHICH I DONT HAVE!!!
 * And the flutter structure is extremely sensible now.
 * 
 * 
 * Alas, tis but a dream. Back to reality:
 * I can queue updates through the
 */
public abstract class Element {

	public static final int UPDATE_NONE = 0;
	public static final int UPDATE_DRAW = 1;
	public static final int UPDATE_TRANSFORM = 2;
	
	// The parent container. If this is a screen object, parent will be null and it
	// will draw onto the physical screen.
	Container parent;

	public PVector pos;
	float w, h;
	
	protected PGraphics g;
	
	/*
	 *  VERY HACKY BECAUSE I DONT WANT TO REWORK **LITERALLY** EVERYTHING
	 *  Provides concurrency optimization.
	 *  Upon requestUpdate(), class contents are copied to the buffer (How pratical is this exactly?)
	 */
	
	
	// Indidcates whether the element has been destroyed
	protected boolean exists = true;

	protected boolean visible = true;

	// Passive listeners only active if the listener is implemented

	// passive component of Hover Listener
	public boolean cursorhover = false;

	// if > 0 tells the element that some data has changed and it should update
	// itself before the next draw cycle.
	// most element types only have one kind of update, but things like shape types,
	// which have regular updates and shape updates can use more than one update
	// state.
	protected int updatable;

	public Element(float x, float y, float w, float h, Container p) {
		pos = new PVector(x, y);
		this.w = w;
		this.h = h;
		
		
		resetGraphics();
		if (p != null) {
			p.add(this);
		}
	}

	// sudoku
	public void destroySelf() {
		if(exists) {
			DB_A(this, " apoptosis");
			
			parent.remove(this);
			destroyListeners();
			exists = false;
		}
	}
	
	// destroys all children
	public void destroyAll() {
		destroySelf();
	}

	// only listener objects may exist in listener lists
	protected void destroyListeners() {
		if (this instanceof Listener) {
			Events.remove(this);
		}
	}

	// method for updating the element's graphics.
	protected abstract void update();

	// Draw mode by default
	protected void requestUpdate() {
		requestUpdate(UPDATE_DRAW);
	}
	
	protected void requestUpdate(int level) {
		// if updatable is true, then that implies that ancestors are
		// pending an update too and we dont need to ask them to update.
		if (updatable == 0 && parent != null) {
			parent.requestUpdate();
		}
		updatable = max(updatable, level);
		
	}

	// generally resetting graphics is done with the intent of drawing onto that
	// graphics,so we begin draw.
	protected void resetGraphics() {
		g = p3.createGraphics((int) w + 1, (int) h + 1);
		g.beginDraw();
	}

	// parent graphics
	protected PGraphics pg() {
		return parent.getGraphics();
	}

	// draws onto its parent's graphics
	protected void draw() {
		checkUpdates();
		// saves current transformation context, applies transform and draws, then
		// restores previous transformation context.
		pg().pushMatrix();
		applyTransform();
		pg().image(getGraphics(), 0, 0);
		pg().popMatrix();
		pg().noTint();
	}

	protected void checkUpdates() {
		// if an update has been queued for this element do the business
		if (updatable >= UPDATE_DRAW) {
			updatable = 0;
			if (visible) {
				g.beginDraw();
				update(); // Update might invoke requestUpdate();
				g.endDraw();
			}
		}else {
			updatable = 0;
		}
	}

	protected void applyTransform() {
		pg().translate(pos.x, pos.y);
	}
	
	// PUBLIC FUNCTIONS
	/* Any functions that will change the graphics state must call request update
	 * once appropriate variables have been modified.
	 * 
	 */
 
	public PGraphics getGraphics() {
		return g;
	}

	public void setVisibility(boolean b) {
		visible = b;
		requestUpdate();
	}

	public boolean getVisibility() {
		return visible;
	}

	public void setDimensions(float w, float h) {
		this.w = Math.max(w, 0);
		this.h = Math.max(h, 0);
		requestUpdate();
	}

	public PVector getDimensions() {
		return new PVector(getWidth(),getHeight());
	
	}
	
	//if width is negative, component is set to the width offset by w
	public float getWidth() {
		if(w<0) return parent.getWidth()+w;
		return w;
	}

	public float getHeight() {
		if(h<0) return parent.getHeight()+h;
		return h;
	}

	public PVector getPos() {
		return new PVector(pos.x, pos.y);
	}

	public PVector getCenterPos() {
		return new PVector(pos.x + w / 2, pos.y + h / 2);
	}

	// returns the sums of it and all it's parent's positions.
	public PVector getGlobalPos() {
		return PVector.add(pos, parent.getGlobalPos());
	}

	public void setPos(float x, float y) {
		pos.x = x;
		pos.y = y;
		// transform updates do not require self to be updated
		parent.requestUpdate();
	}

	public void setPos(PVector pos) {
		this.pos = pos;
		parent.requestUpdate();
	}

	public void setParent(Container p) {
		if (parent == p) return;
		if (parent != null)
			parent.remove(this);
		parent = p;
		requestUpdate();
	}

	public Container getParent() {
		return parent;
	}
	

	// returns true if element has the ability to exist.
	// checks if the highest element in the parent hierarchy is a screen object.
	// Screen object overrides this function to return true.
	public boolean isConcrete() {
		if (parent == null)
			return false;
		return parent.isConcrete();
	}


	public float localMouseX() {
		return p3.mouseX - getGlobalPos().x;
		
	}
	public float localMouseY() {
		return p3.mouseY - getGlobalPos().y;
	}
	public float localPMouseX() {
		return p3.pmouseX - getGlobalPos().x;
	}
	public float localPMouseY() {

		return p3.pmouseY - getGlobalPos().y;
		
	}


	@Override
	public String toString() {
		String classname = this.getClass().toString();
		int i = 0;
		while (classname.charAt(i) != '.') {
			i++;
		}
		classname = classname.substring(i + 1);
		return String.format("{%s(%.0f,%.0f)(%.0f,%.0f)}", classname, pos.x, pos.y, w, h);

	}

	// performs rectangular collision between two elements.
	public static boolean boundsCollision(Element a, Element b) {
		PVector a_center = a.getCenterPos();
		PVector b_center = a.getCenterPos();
		if (2 * abs(a_center.x - b_center.x) < (a.getWidth() + b.getWidth())) {
			return true;
		}else if (2 * abs(a_center.y - b_center.y) < (a.getHeight() + b.getHeight())) {
			return true;
		}
		return false;
	}


}

// placeholder which does buttcrap nothing
class Blank extends Element {
	Blank(Container p) {
		super(0, 0, 0, 0, p);
	}

	protected void update() {
	}

	@Override
	protected void requestUpdate() {
	}

	@Override
	protected void resetGraphics() {
	}

	@Override
	protected void draw() {
	}

	@Override
	protected void checkUpdates() {
	}

	@Override
	protected void applyTransform() {
	}

	@Override
	public void setVisibility(boolean b) {
	}

	@Override
	public void setDimensions(float w, float h) {
	}

	@Override
	public void setPos(float x, float y) {
	}

	@Override
	public String toString() {
		return String.format("{%s}", this.getClass().toString().substring(25));
	}

}