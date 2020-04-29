package screens;


import static core.MainProgram.*;

import java.awt.event.KeyEvent;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import core.Images;
import elements.BasicButton;
import elements.Container;
import elements.ImageButton;
import elements.Screen;
import elements.ScrollPane;
import elements.Text;
import events.KeyListener;
import events.KeyEvents;
import events.Saveable;
import game.DataDisplay;
import game.GameArea;
import game.Oscilloscope;
import game.WireSegment;
import processing.core.PImage;
import shapes_unused.Arrow;
import util.LLinkedList;
import util.SparseQuadTree;
import static util.DB.*;

public class GameScreen extends Screen implements Saveable, KeyListener{
	
	// final static int BUTTON_FILL = p3.Color(100);
	// final static int BUTTON_STROKE;
	// final static int BUTTON_FILLPRESSED;
	// final static int BUTTON_STROKEHOVERED;
	
	
	
	GameArea game;
	DataDisplay display;
	
	BasicButton startbutton,stopbutton;
	
	BasicButton scopepausebutton;
	
	ImageButton backbutton;
	
	String name;
	
	public GameScreen(String name, int depth) {
		super();
		KeyEvents.add(this);

		this.name = name;
		// Try load from file
		
		ObjectInputStream oistream = null;
		String location = "data/level_" + name + ".bin";
		try {
			println("Trying to load from " + location);
			oistream = new ObjectInputStream(new FileInputStream(location));
			game = new GameArea(0, 50, 1200, 500, depth, this, (SparseQuadTree<WireSegment>)oistream.readObject());
			display = new DataDisplay(0, 550, 1200, 300, this, (LLinkedList<Oscilloscope>)oistream.readObject());
		}catch(IOException | ClassNotFoundException e) {
			println("File not found | Corrupted data. Welp new slate then.");
			game = new GameArea(0, 50, 1200, 500, depth, this);
			display = new DataDisplay(0, 550, 1200, 300, this);	
			
		} finally {
			try {
				if(oistream != null) {
					oistream.close();
				}
			}catch(IOException e){
				System.err.println("IO error when trying to close object reader.");
				e.printStackTrace(System.err);
				System.exit(1);
			}
		}
		
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
		
		
	}

	protected void update() {
		super.update();
		//draw border around GameArea
		g.stroke(190,190,140);
		g.strokeWeight(1);
		g.noFill();
		g.rect(game.getPos().x, game.getPos().y, game.getWidth(),game.getHeight());
		
		
	}

	@Override
	public boolean saveState() {
		try {
			ObjectOutputStream oostream = new ObjectOutputStream(new FileOutputStream("data/level_" + name + ".bin"));
			oostream.writeObject(game.tiles);
			oostream.writeObject(game.display.scopes);
			oostream.close();
			return true;
		}catch(IOException e) {
			println("Save failed... :( Try again later.");
			println(e.getMessage());
			e.printStackTrace();
			return false;
		}
	}
	@Override
	public void keyPressed() {
		if(KeyEvents.key[KeyEvents.VK_S] && KeyEvents.key[KeyEvents.VK_CONTROL]) {
			DB_A("GameScreen Save");
			if(saveState());
		}
	}
	
}
