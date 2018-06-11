/**
 * @author Ezra Hui
 *  Email: other.ezhui@gmail.com
 *  Phone: 0452 413 358
 * 
 * Last modified: 5/25/2018
 * Engineer of the Technocracy Version 1.1 
 * Changelog:
 * - Added editing rotation and flipping of clipboard.
 * - Fixed some visual effects.
 * - Modified some controls
 * - Added more splash screen text
 * 
 * Current Bugs:
 *  - Highly connected wires tend to create infinite update loops 
 *  - A few visual bugs with Oscilloscopes and the copy paste feature
 *  
 */



package core;

import events.*;
import game.*;
import elements.*;
import util.*;
import async.*;
import screens.*;
import shapes_unused.*;
import tests.*;

import static util.DB.*;
import static core.MainProgram.*;
import java.util.ArrayList;
import processing.core.PApplet;
import processing.core.PVector;
import processing.event.MouseEvent;

public class MainProgram extends PApplet {

	public static MainProgram p3;
	public static Scheduler globalscheduler = new Scheduler("GlobalThread");
	
	
	public static void main(String[] args) {
		PApplet.main("core.MainProgram");
	}

	public static Screen LEVEL;

	public void setup() {
		p3 = this;
		Images.init();
		globalscheduler.start();
		LEVEL = new MenuScreen();
		
		//Ensure main data structures are in working condition.
		globalscheduler.testScheduler();
		UnitTests.heapTest();
		UnitTests.linkedListTest();
		UnitTests.sparseQuadTreeTest();

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
		if(debug >=2) {
			text(String.format("Draw: %dms",drawtime),10,10);
			text(String.format("Event: %dms",eventtime),10,40);
		}
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
	GameArea game;
	DataDisplay display;
	Text c;
	
	BasicButton startbutton,stopbutton;
	
	
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
		
		
		game = new GameArea(0,0,1200,600,6,this);
		display = new DataDisplay(0,600,1200,200,this);
		game.setDisplay(display);
		
		

		startbutton = new BasicButton(0, 650, 100, 100, "Run", this) {
			@Override
			public void elementClicked(){
				game.run();
				startbutton.setEnabled(false);
				stopbutton.setEnabled(true);
			}
		};
		stopbutton = new BasicButton(100, 650, 100, 100, "Stop", this) {
			@Override
			public void elementClicked(){
				game.reset();
				stopbutton.setEnabled(false);
				startbutton.setEnabled(true);
			}
		}; 
		stopbutton.setEnabled(false);
		
		//new ImageButton(100, 100, 100, 100, p3.loadImage("NavArrow.bmp"), this);
		//b = new Mandelbrot(0, 0, getWidth(), getHeight(), this);
		c = new Text(10,10,400,80,"Location:",this);
		
	}

	protected void update() {
		super.update();
	}


}
