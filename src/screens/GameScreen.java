package screens;


import static core.MainProgram.*;
import elements.BasicButton;
import elements.Screen;
import elements.Text;
import game.DataDisplay;
import game.GameArea;
import shapes.Arrow;

public class GameScreen extends Screen{
	
//	final static int BUTTON_FILL = p3.Color(100);
	//final static int BUTTON_STROKE;
//final static int BUTTON_FILLPRESSED;
//	final static int BUTTON_STROKEHOVERED;
	
	
	
	
	Arrow a;
	GameArea game;
	DataDisplay display;
	Text c;
	
	BasicButton startbutton,stopbutton;
	
	
	public GameScreen() {
		super();
		
		game = new GameArea(0,50,1200,500,10,this);
		display = new DataDisplay(0,550,1200,300,this);
		game.setDisplay(display);
		
		startbutton = new BasicButton(0, 0, 100, 50, "Run", this) {
			@Override
			public void elementClicked(){
				game.run();
				startbutton.setEnabled(false);
				stopbutton.setEnabled(true);
			}
		};
		stopbutton = new BasicButton(110, 0, 100, 50, "Stop", this) {
			@Override
			public void elementClicked(){
				game.reset();
				stopbutton.setEnabled(false);
				startbutton.setEnabled(true);
			}
		};
		stopbutton.setEnabled(false);
		
		startbutton.setFill(p3.color(130,240,160));
		startbutton.setFillPressed(p3.color(170,240,190));
		startbutton.setStroke(p3.color(50,150,50));
		startbutton.setStrokeHovered(p3.color(170,240,190));
		
		stopbutton.setFill(p3.color(233,100,100));
		stopbutton.setFillPressed(p3.color(255,160,160));
		stopbutton.setStroke(p3.color(100,10,0));
		stopbutton.setStrokeHovered(p3.color(255,160,160));
		
		
		
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
