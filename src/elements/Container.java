package elements;

import java.util.ArrayList;
import java.util.Arrays;

import static core.MainProgram.*;
import static util.DB.*;

//Desc: can hold and draw multiple children elements. Getters and setters are a given.
public class Container extends Element {
	final ArrayList<Element> children;
	
	// 0 is transparent.
	protected int backgroundcolor = 0;
	boolean hasbackground = false;

	public Container(float x, float y, float w, float h, Container p) {
		super(x, y, w, h, p);
		children = new ArrayList<Element>();
		
	}

	public Container(float x, float y, Container p, Element ... children) {
		super(x, y, 0, 0, p); // Height and width to be decided once children are added.
		this.children = new ArrayList<Element>(Arrays.asList(children));
		for (Element e: children) {
			e.setParent(this);
		}
		tightFit();
	}
	
	@Override
	protected
	void destroyListeners() {
		super.destroyListeners();
		for (Element e : children) {
			e.destroyListeners();
		}
	}
	
	// destroys all children
	public void destroyAll() {
		for (Element e : new ArrayList<Element>(children)) {
			e.destroyAll();
		}
		destroySelf();
	}

	protected void update() {
		resetGraphics();
		if (backgroundcolor != 0) {
			g.background(backgroundcolor);
		}
		drawChildren();
	}
	
	protected void setBackgroundColor(int c) {
		backgroundcolor = c;
	}

	// first element behind, last element in front
	void drawChildren() {
		for (Element e : new ArrayList<Element>(children)) {
			// some components are spacer components, they are null and exist to gain more
			// control over depth
			if (e != null && isInParent(e)) {
				e.draw();
			}
		}
	}
	
	/* Checks if component's drawing pane is visible within the parent.
	 * does not account for transformations. To solve this we
	 * can write Element.boundingBox() and use that instead.
	 * That would be a function of the transformation.
	 */ 

	public boolean isInParent(Element e) {
		if (e.getPos().x > getWidth() || e.getPos().y > getHeight()) {
			return false;
		} else if (e.getPos().x + e.getWidth() < 0 || e.getPos().y + e.getHeight() < 0) {
			return false;
		}
		return true;
	}

	public ArrayList<Element> getChildren() {
		return new ArrayList<Element>(children);
	}

	public void add(Element object) {
		object.setParent(this);
		children.add(object);
	}

	// use is discouraged due to the very dynamic nature of the children array
	// I haven't used it yet but I still get nightmares in my sleep. Don't Use It.
	public void set_(int index, Element object) {

		DB_A(this, "set", object, "at", index);
		object.setParent(this);
		while (children.size() < index)
			children.add(null);
		Element child = children.get(index);
		children.set(index, object);
		requestUpdate();
	}

	// better than set_ because object is preserved.
	public void insert(int index, Element object) {
		object.setParent(this);
		children.add(min(index, children.size()), object);
		DB_A(this, "inserted", object, "at", index);
		requestUpdate();
	}

	// removal also involves removing them from all listener lists.
	public void remove(Element object) {

		DB_A(this, "removed", object);
		object.parent = null;

		if (children.remove(object)) {
			DB_A("success");
		}
		requestUpdate();
	}

	public void remove(int index) {
		DB_A(this, "removed", index);
		Element child = children.get(index);
		children.get(index).parent = null;
		children.remove(index);
		requestUpdate();
	}

	public void replace(Element oldo, Element newo) {
		int index = children.lastIndexOf(oldo);
		oldo.parent = null;
		newo.setParent(this);
		children.set(index, newo);
	}

	public void replace(int index, Element newo) {
		Element oldo = children.get(index);
		oldo.parent = null;
		newo.parent = this;
		newo.requestUpdate();
		children.set(index, newo);
	}

	public Element getChild(int i) {
		return children.get(i);
	}
	/*
	 * Increases size of container (In positive direction only) so
	 * that all children elements can be seen.
	 */
	public float getExtentX() {
		float maxx = 0;
		for (Element e : children) {
			maxx = max(maxx, e.pos.y + e.h); 	
		}
		return maxx;
	}
	public float getExtentY() {
		float maxy = 0;
		for (Element e : children) {
			maxy = max(maxy, e.pos.y + e.h); 	
		}
		return maxy;
	}
	public void expandFit() {
		w = max(getExtentX(), w);
		h = max(getExtentY(), h);
	}
	/*
	 * Shrinks to exactly accomodate children (In positive direction only)
	 */
	public void shrinkFit() {
		w = min(getExtentX(), w);
		h = min(getExtentY(), h);
	}
	/*
	 * Sets size to exactly accomodate children (In positive direction only)
	 */
	public void tightFit() {
		w = getExtentX();
		h = getExtentY();
	}
	
	public int size() {
		return children.size();
	}

	// do I even have a grandson?
	public boolean isChild(Element e) {
		return children.contains(e);
	}
}