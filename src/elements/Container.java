package elements;

import java.awt.Color;
import java.util.ArrayList;
import static core.MainProgram.*;
import static util.DB.*;

//Desc: can hold and draw multiple children elements. Getters and setters are a given.
public class Container extends Element {
	final ArrayList<Element> children;
	protected int backgroundcolor = -1;
	boolean hasbackground = false;

	public Container(float x, float y, float w, float h, Container p) {
		super(x, y, w, h, p);
		children = new ArrayList<Element>();
	}

	@Override
	protected
	void destroyListeners() {
		super.destroyListeners();
		for (Element e : children) {
			e.destroyListeners();
		}
	}

	protected void update() {
		resetGraphics();
		if (backgroundcolor != -1) {
			g.background(backgroundcolor);
		}
		drawChildren();
	}

	// first element behind, last element in front
	void drawChildren() {
		for (Element e : children) {
			// some components are spacer components, they are null and exist to gain more
			// control over depth
			if (e != null && e.isInParent()) {
				e.draw();
			}
		}
	}

	public ArrayList<Element> getChildren() {
		return new ArrayList<Element>(children);
	}

	public void add(Element object) {
		object.setParent(this);
		children.add(object);
	}

	// use is discouraged due to the very dynamic nature of the children array
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

	public int size() {
		return children.size();
	}

	// do I even have a grandson?
	public boolean isChild(Element e) {
		return children.contains(e);
	}
}