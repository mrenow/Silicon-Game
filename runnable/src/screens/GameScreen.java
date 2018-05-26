package screens;


import static core.MainProgram.*;

import core.Images;
import elements.BasicButton;
import elements.Container;
import elements.ImageButton;
import elements.Screen;
import elements.ScrollPane;
import elements.Text;
import game.DataDisplay;
import game.GameArea;
import processing.core.PImage;
import shapes_unused.Arrow;

public class GameScreen extends Screen{
	
	// final static int BUTTON_FILL = p3.Color(100);
	// final static int BUTTON_STROKE;
	// final static int BUTTON_FILLPRESSED;
	// final static int BUTTON_STROKEHOVERED;
	
	
	
	GameArea game;
	DataDisplay display;
	
	BasicButton startbutton,stopbutton;
	
	BasicButton scopepausebutton;
	
	ImageButton backbutton;
	
	
	public GameScreen(int depth) {
		super();
		game = new GameArea(0,50,1200,500,depth,this);
		display = new DataDisplay(0,550,1200,300,this);
		game.setDisplay(display);
		
		startbutton = new BasicButton(getWidth()-220, 0, 100, 50, "Run", this) {
			@Override
			public void elementClicked(){
				
				if(enabled) {
					super.elementClicked();
					game.run();
					startbutton.setEnabled(false);
					stopbutton.setEnabled(true);
				}
			}
		};
		
		startbutton.setFill(p3.color(100,170,120));
		startbutton.setFillPressed(p3.color(170,240,190));
		startbutton.setStroke(p3.color(50,150,50));
		startbutton.setStrokeHovered(p3.color(170,240,190));
		
		stopbutton = new BasicButton(getWidth()- 110, 0, 100, 50, "Stop", this) {
			@Override
			public void elementClicked(){
				if(enabled) {
					super.elementClicked();
					game.reset();
					stopbutton.setEnabled(false);
					startbutton.setEnabled(true);
				}
			}
		};
		
		stopbutton.setFillPressed(p3.color(255,160,160));
		stopbutton.setStroke(p3.color(100,10,0));
		stopbutton.setStrokeHovered(p3.color(255,160,160));
		stopbutton.setFill(p3.color(190,70,70));
		stopbutton.setEnabled(false);
		
		backbutton = new ImageButton(0,0,100,50,Images.BACK_ARROW, this) {
			public void elementClicked() {
				LEVEL.destroyAll();
				LEVEL = new MenuScreen();
			}
		};
		backbutton.setMode(ImageButton.CENTERED);
		
		
		
		
		
		
		
		
		//new ImageButton(100, 100, 100, 100, p3.loadImage("NavArrow.bmp"), this);
		//b = new Mandelbrot(0, 0, getWidth(), getHeight(), this);
		
	}

	protected void update() {
		super.update();
		//draw border around GameArea
		g.stroke(190,190,140);
		g.strokeWeight(1);
		g.noFill();
		g.rect(game.getPos().x, game.getPos().y, game.getWidth(),game.getHeight());
		
		
	}

	
	
}
