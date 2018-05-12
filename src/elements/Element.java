package elements;

import processing.core.PVector;

import processing.core.PGraphics;
import static core.MainProgram.*;
import static util.DB.*;

import events.Events;
import events.Listener;

//Fields: position, size, graphics, parent ?visibility, ?cursorhovering(only when clickListener is implemented), ?updaterequest
//Functions: setters(pos,size,vis), request update, draw self, reset graphics
public abstract class Element {
	// The parent container. If this is a screen object, parent will be null and it
	// will draw onto the physical screen.
	Container parent;

	public PVector pos;
	float w, h;

	protected PGraphics g;

	protected boolean exists = true;

	boolean visible = true;

	// Passive listeners only active if the listener is implemented

	// passive component of Hover Listener
	public boolean cursorhover = false;

	// if >0 tells the element that some data has changed and it should update
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
		DB_A(this, " apoptosis");
		parent.remove(this);
		destroyListeners();
		exists = false;
	}

	// only listener objects may exist in listener lists
	protected void destroyListeners() {
		if (this instanceof Listener) {
			Events.remove(this);
		}
	}

	// method for updating the element's graphics.
	protected abstract void update();

	protected void requestUpdate() {
		// if updatable is true, then that implies that all the parent objects are
		// pending an update too.
		updatable = max(1, updatable);
		// considering conditional call if parent is not currently pending an update.
		if (isConcrete()) {
			parent.requestUpdate();
		}
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
	}

	protected void checkUpdates() {
		// if an update has been queued for this element do the business
		if (updatable > 0) {
			updatable = 0;
			if (visible) {
				g.beginDraw();
				update();
				g.endDraw();
			}
		}
	}

	protected void applyTransform() {
		pg().translate(pos.x, pos.y);
	}

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
		return new PVector(w, h);
	
	}

	public float getWidth() {
		return w;
	}

	public float getHeight() {
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
		requestUpdate();
	}

	public void setPos(PVector pos) {
		this.pos = pos;
		parent.requestUpdate();
	}

	public void setParent(Container p) {
		if (parent != null && parent != p)
			parent.remove(this);
		parent = p;
		requestUpdate();
	}

	public Container getParent() {
		return parent;
	}

	// returns true if element has the ability to exist.
	// checks if the highest element in the parent heirachy is a screen object.
	public boolean isConcrete() {
		if (parent == null)
			return false;
		return parent.isConcrete();
	}

	// Checks if component's drawing pane is visible within the parent.
	public boolean isInParent() {
		Container parent = getParent();
		if (getPos().x > parent.getWidth() || getPos().y > parent.getHeight()) {
			return false;
		} else if (getPos().x + getWidth() < 0 || getPos().y + getHeight() < 0) {
			return false;
		}
		return true;
	}

	public float localMouseX() {
		return p3.mouseX - getGlobalPos().x;
		
	}
	public float localMouseY() {
		return p3.mouseY - getGlobalPos().y;
		
	}


	@Override
	public String toString() {
		String classname = this.getClass().toString();
		int i = 0;
		while (classname.charAt(i) != '.') {
			i++;
		}
		classname = classname.substring(i + 1);
		println(classname);
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