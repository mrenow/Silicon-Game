package elements;

import processing.core.PVector;
import static core.MainProgram.*;
import static util.DB.*;

//
public abstract class Screen extends Container {

	public Screen() {
		super(0, 0, p3.width, p3.height, null);
		requestUpdate();
	}

	@Override
	public void destroySelf() {
		destroyListeners();
	}

	@Override
	protected void requestUpdate() {
		updatable = UPDATE_DRAW;
	}

	@Override
	public PVector getGlobalPos() {
		return new PVector(0, 0);
	}

	@Override
	public void draw() {
		checkUpdates();
		p3.image(getGraphics(), pos.x, pos.y);
	}

	@Override
	public boolean isConcrete() {
		return true;
	}
}
