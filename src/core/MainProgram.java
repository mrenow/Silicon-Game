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
import sidequest.Mandelbrot;
import tests.*;

import static util.DB.*;
import static core.MainProgram.*;
import java.util.ArrayList;
import processing.core.PApplet;
import processing.core.PVector;
import processing.event.MouseEvent;

/**
 * @author Ezra Hui
 *
 */
public class MainProgram extends PApplet {

	public static MainProgram p3;
	public static Scheduler globalscheduler = new Scheduler();
	
	
	public static void main(String[] args) {
		PApplet.main("core.MainProgram");
	}

	public Screen LEVEL;

	public void setup() {
		p3 = this;
		globalscheduler.start();
		LEVEL = new TestScreen();

		//Scheduler.testScheduler();
		UnitTests.heapTest();
		UnitTests.linkedListTest();
		UnitTests.sparseQuadTreeTest();
		test1.yell();
		PointerSpeedTest.test();
		GameTests.testConnections();

	}

	public void settings() {
		size(1200, 800);
	}

	public void draw() {
		
		background(200);
		strokeWeight(2);
		fill(0);
		int eventtime = millis();
		Events.check();
		eventtime = millis() - eventtime;
		
		int drawtime = millis();
		LEVEL.draw();
		drawtime = millis()-drawtime;
		
		// draw debug overlay
		
		text(String.format("Draw: %dms",drawtime),10,10);
		text(String.format("Event: %dms",eventtime),10,40);
		
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
	public void mouseDragged() {
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

class TestScreen extends Screen implements MovementListener{
	Arrow a;
	MapNavigator b;
	Text c;
	ScrollPane osc;
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
		b = new GameArea(0,0,1200,600,6,this);
		b.
		

		//new BasicButton(0, 0, 100, 100, "hello", this);
		//new ImageButton(100, 100, 100, 100, p3.loadImage("NavArrow.bmp"), this);
		//b = new Mandelbrot(0, 0, getWidth(), getHeight(), this);
		c = new Text(10,10,400,80,"Location:",this);
		
		globalscheduler.call(new loop());
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
			PVector pos = b.localToMapPos(new PVector(p3.mouseX, p3.mouseY));
			c.setText(String.format("Location: [%.2f, %.2f]\nZoom: %.2f",pos.x,pos.y,b.getZoom()));
			//c.setText("loc: ["+ Float.toString(pos.x) + Float.toString(pos.y) + "]");
			
		}

	}


}
