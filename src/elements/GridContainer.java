package elements;

import static core.MainProgram.*;
import static util.DB.*;


public class GridContainer extends Container {
	float boxw;
	float boxh;
	int columns;

	public GridContainer(float x, float y, float w, float h, float boxw, float boxh, Container p) {
		super(x, y, w, h, p);
		this.boxw = boxw;
		this.boxh = boxh;
		columns = floor(w / boxw);
		hasbackground = true;
		backgroundcolor = p3.color(0, 100, 0);
	}

	public void setDimensions(float x, float y) {
		super.setDimensions(x, y);
		if(this.getChildren().size() == 0) {
			requestUpdate();
			return;
		}
		
		// if number of columns has changed from previous value
		if (columns != (columns = floor(w / boxw))) {
			float col = 0, row = 0;
			for (int i = 0; i < size(); i++) {
				getChild(i).setPos(col * boxw, row * boxh);
				col++;
				if (col == columns) {
					row++;
					col = 0;
				}
			}
		}
		// update has already been requested by children.
	}

	public void add(Element object) {
		object.setPos((size() % columns) * boxw, (size() / columns) * boxh);
		super.add(object);
	}
}