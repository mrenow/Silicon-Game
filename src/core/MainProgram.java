/**
 * 
 */
package core;

import events.*;
import game.*;
import elements.*;
import util.*;
import async.*;
import shapes.*;
import tests.*;

import static util.DB.*;
import static core.MainProgram.*;
import java.util.ArrayList;
import processing.core.PApplet;
import processing.event.MouseEvent;

/**
 * @author Ezra Hui
 *
 */
public class MainProgram extends PApplet {

	public static MainProgram p3;

	public static void main(String[] args) {
		PApplet.main("core.MainProgram");
	}

	public Screen LEVEL;

	public void setup() {
		p3 = this;
		Scheduler.start();
		LEVEL = new TestScreen();

		//Scheduler.testScheduler();
		UnitTests.heapTest();
		UnitTests.linkedListTest();
		UnitTests.sparseQuadTreeTest();
		test1.yell();
		
		exit();

	}

	public void settings() {
		size(10, 10);
	}

	public void draw() {
		background(200);
		strokeWeight(2);
		fill(0);
		Events.check();
		LEVEL.draw();
	}

	public void mousePressed() {
		FocusEvents.check();
		ClickEvents.clickCheck();
	}

	public void mouseReleased() {
		ClickEvents.releaseCheck();
	}

	public void mouseMoved() {
		MovementEvents.check();
	}

	public void mouseWheel(MouseEvent event) {
		ScrollEvents.check(event);
	}

	public void keyPressed() {
		KeyEvents.pressCheck();
	}

	public void keyReleased() {
		KeyEvents.releaseCheck();
	}

	public void keyTyped() {
		for (KeyListener e : new ArrayList<KeyListener>(KeyEvents.list)) {
			e.keyTyped();
		}
	}
}

class TestScreen extends Screen {
	Arrow a;
	GridContainer b;

	TestScreen() {
		super();
		/*
		 * Box b1, b2, b3, b4; b = new GridContainer(330, 0, 1000, 1000,50,50 ,this);
		 * for(int i = 0; i<120;i++){ new Box(0,0, 50, 50, b).setFill(i*2, 100, 100); }
		 * Box b5 = new Box(0,0,200,1200,null){ void update(){ super.update();
		 * g.ellipse(w/2,h/2,300,300); }
		 * 
		 * 
		 * };
		 * 
		 * b5.setFill(255,0,0); //ScrollPane e = new ScrollPane(0,0,600,300,b,2,this);
		 */
		//a = new Arrow(200, 200, 40, 30, 20, 200, 50, this);
		//new GameArea(100,100,800,800,200,200,this);

		//new BasicButton(0, 0, 100, 100, "hello", this);
		//new ImageButton(100, 100, 100, 100, p3.loadImage("NavArrow.bmp"), this);
		//Scheduler.call(new loop());

	}

	protected void update() {
		super.update();
	}

	class loop extends ActiveAsyncEvent {
		@Override
		public void start() {
			// TODO Auto-generated method stub

		}

		@Override
		public boolean condition() {
			// TODO Auto-generated method stub
			return exists;
		}

		@Override
		public void run() {
			a.addRotation(0.1f);
		}

	}

}