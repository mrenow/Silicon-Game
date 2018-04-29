package elements;

import static core.MainProgram.*;
import static util.DB.*;
import processing.core.PImage;
import java.util.ArrayList;

import events.KeyEvents;

//A container which has multiple pages which are navigated between using arrows or keypresses
public class MultiPage extends Container {
	ImageButton navleft;
	ImageButton navright;

	Element current;
	ArrayList<Element> pages = new ArrayList<Element>();
	int index;
	// displays index
	Text pagenumber;

	float buttonheight = 20;
	float buttonwidth = 40;

	PImage arrow = p3.loadImage("NavArrow.bmp");

	public MultiPage(float x, float y, float w, float h, Container p) {
		super(x, y, w, h, p);
		buttonheight = arrow.height;
		buttonwidth = arrow.width;

		current = new Blank(this);
		navleft = new ImageButton(0, h - buttonheight, buttonwidth, buttonheight, arrow, this) {

			@Override
			public void elementClicked() {
				prevPage();
				DB_A(parent, "index", index);
			}

			@Override
			protected
			void applyTransform() {

				super.applyTransform();

				pg().translate(w, 0);
				pg().scale(-1, 1);
			}
		};

		navright = new ImageButton(w - buttonwidth, h - buttonheight, buttonwidth, buttonheight, arrow, this) {
			@Override
			public void elementClicked() {
				nextPage();

				DB_A(parent, "index", index);
			}
		};
		pagenumber = new Text(w / 2 - 20, h - buttonheight, 40, buttonwidth, "", this);
		// pagenumber.setFont("")
	}

	protected void update() {
		super.update();
		pagenumber.setText(str(index + 1) + "/" + str(children.size()));
	}

	public void nextPage() {
		if (index + 1 < pages.size()) {
			display(index + 1);
		}
	}

	public void prevPage() {
		if (index > 0) {
			display(index - 1);
		}
	}

	public void addPage(Element... elements) {
		for (Element e : elements) {
			e.setDimensions(w, h);
			e.setPos(0, 0);
			pages.add(e);
		}
		display(this.index);
		requestUpdate();
	}

	public void display(int index) {
		this.index = index;
		current = getPage(index);
		replace(0, current);
		requestUpdate();
	}

	public void setPage(int index, Element e) {
		pages.set(index, e);
		// only if changing the current displayed page update is necessary
		if (this.index == index)
			display(this.index);
	}

	public Element getPage(int index) {
		return pages.get(index);
	}

	public Element getCurrentPage() {
		return current;
	}

	public int getIndex() {
		return index;
	}



}
// a type of container which formats the items within it

// formats an ordered list of components in a grid with predefined square size.
