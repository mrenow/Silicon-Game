package screens;


import static core.MainProgram.*;

import java.awt.event.KeyEvent;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import core.Images;
import effects.FadeContainer;
import elements.BasicButton;
import elements.Container;
import elements.ImageButton;
import elements.Screen;
import elements.ScrollPane;
import elements.Text;
import elements.ToggleButton;
import events.KeyListener;
import events.KeyEvents;
import events.Saveable;
import game.DataDisplay;
import game.GameArea;
import game.Oscilloscope;
import game.WireSegment;
import game.Power;
import processing.core.PConstants;
import processing.core.PImage;
import processing.core.PVector;
import shapes_unused.Arrow;
import util.LLinkedList;
import util.Pair;
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
	
	ToggleButton pausebutton;
	
	ImageButton backbutton;
	
	Text modetext;
	
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
			Object res1 = oistream.readObject();
			Object res2 = oistream.readObject();
			
			game = new GameArea(0, 50, 1200, 500, depth, this, (LLinkedList<WireSegment>)res1);
			display = new DataDisplay(0, 550, 1200, 300, this, game, (LLinkedList<Pair<Integer, Integer>>)res2);
		}catch(IOException | ClassNotFoundException | ClassCastException e) {
			println(e.getMessage());
			println("File not found | Corrupted data. Welp new slate then.");
			game = new GameArea(0, 50, 1200, 500, depth, this);
			display = new DataDisplay(0, 550, 1200, 300, this, game);	
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
		int buttonwidth = 100;
		int spacing = 10;
		int offset = 0;
		startbutton = new BasicButton(getWidth()-(offset+=spacing + buttonwidth), 0, buttonwidth, 50, "Run", this) {
			@Override
			public void elementClicked(){
				
				if(enabled) {
					super.elementClicked();
					game.run();
					startbutton.setEnabled(false);
					stopbutton.setEnabled(true);
					pausebutton.setVisibility(true);
					modetext.setText("Simulate Mode");
				}
			}
		};
		
		
		startbutton.setFill(p3.color(4, 94, 22));
		startbutton.setFillPressed(p3.color(89, 240, 119));
		startbutton.setStroke(p3.color(4, 94, 22));
		startbutton.setStrokeHovered(p3.color(89, 240, 119));
		
		stopbutton = new BasicButton(getWidth()- (offset+=spacing + buttonwidth), 0, buttonwidth, 50, "Stop", this) {
			@Override
			public void elementClicked(){
				if(enabled) {
					super.elementClicked();
					game.reset();
					stopbutton.setEnabled(false);
					startbutton.setEnabled(true);
					pausebutton.setVisibility(false);
					modetext.setText("Build Mode");
				}
			}
		};
		
		stopbutton.setFillPressed(p3.color(255,160,160));
		stopbutton.setStroke(p3.color(190,70,70));
		stopbutton.setStrokeHovered(p3.color(255,160,160));
		stopbutton.setFill(p3.color(190,70,70));
		stopbutton.setEnabled(false);
		
		pausebutton = new ToggleButton(getWidth()- (offset+=spacing + buttonwidth), 0, buttonwidth, 50, "Pause", this) {
			@Override
			public void elementClicked(){
				super.elementClicked();
				if(pressed) {
					game.stop();
				}else {
					game.run();
				}
			}
		};
		
		pausebutton.setFillPressed(p3.color(255, 210, 140));
		pausebutton.setStroke(p3.color(145, 113, 49));
		pausebutton.setStrokeHovered(p3.color(255,160,160));
		pausebutton.setFill(p3.color(145, 113, 49));
		pausebutton.setVisibility(false);
		
		modetext = new Text(0, 0, 0, 50, "Simulate Mode",this);
		
		modetext.expandFit();
		modetext.setPos(modetext.getWidth() + offset + spacing, modetext.pos.y);
		modetext.setAlign(CENTER, CENTER);
		modetext.setText("BuildMode");
		
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
	/*
	 *  Okay... so maybeee this should be in gameArea.
	 *  But my goodness, look at the thing
	 *  it has 1500 lines
	 *  give it a break
	 */
	@Override
	public boolean saveState() {
		try {
			ObjectOutputStream oostream = new ObjectOutputStream(new FileOutputStream("data/level_" + name + ".bin"));
			oostream.writeObject(game.tiles.elements);
			// pack up scopes
			LLinkedList<Pair<Integer, Integer>> scopes = new LLinkedList<Pair<Integer, Integer>>();
			for (Oscilloscope o : game.display.scopes) {
				scopes.add(new Pair<Integer, Integer>(o.x, o.y));
			}
			oostream.writeObject(scopes);
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
			if(saveState()) {
				game.messageoverlay.addMessage("Saved successfully.");
			}else {
				game.messageoverlay.addMessage("Save failed.");
			}
			
		}
	}
	
}
