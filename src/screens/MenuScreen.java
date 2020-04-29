package screens;

import static core.MainProgram.*;
import static processing.core.PConstants.CENTER;
import static util.DB.*;

import core.Images;
import elements.BasicButton;
import elements.Container;
import elements.ImageButton;
import elements.Screen;
import elements.ScrollPane;
import elements.Text;

public class MenuScreen extends Screen{
	
	BasicButton startbutton;
	BasicButton helpbutton;
	BasicButton exit;
	
	Text splash;
	
	Text title;
	
	
	public MenuScreen() {
		helpbutton = new BasicButton(500,200,200,50,"Letter", this) {
			@Override
			public void elementClicked() {
				LEVEL.destroySelf();
				LEVEL = new HelpScreen();
			}
			
		};
		helpbutton.setFill(p3.color(150,155,100));
		helpbutton.setFillPressed(p3.color(190,195,140));
		helpbutton.setStroke(p3.color(100,100,20));
		helpbutton.setStrokeHovered(p3.color(150,155,100));
		
		title = new Text(200,30,800,250,"Engineer of the Technocracy",this);
		title.setAlign(CENTER, CENTER);
		title.setFont("Courier New", 40);
		
		
		
		
		new LevelPane(200,300,800,400,this);
		splash = new Text(10,90,400,400,SPLASHES[floor(p3.random(0,SPLASHES.length))],this) {
			protected void applyTransform() {
				pg().rotate(-0.2f);
				super.applyTransform();
			}
		};
		
		
		
	}
	
	protected void update() {
		super.update();
		g.noFill();
		g.stroke(100,100,20);
		g.rect(200,300, 800, 400);
	}
	
	
	
	class LevelPane extends ScrollPane{
		public int numlevels;

		
		LevelPane(float x, float y, float w, float h, Container p){
			super(x,y,w,h,SCROLL_Y ,p);
			numlevels = LEVEL_SIZE.length;
		
			
			//stacks tabs on top of each other.
			float height = 0;
			for (int i = 0; i< numlevels ; i++) {
				LevelTab level = new LevelTab(0, height, i,this) ;
				height += level.getHeight();
			}
			setPaneHeight(height);
		}
		
	}
	
	class LevelTab extends Container{
		
		
		final static float WIDTH = 800;
		final static float HEIGHT = 60;
	
		ImageButton play;
		Text namelabel;
		
		
		private int id;
		
		LevelTab(float x, float y, int i, Container p){
			super(x,y,WIDTH,HEIGHT,p);
			id = i;
			namelabel = new Text(5,5,800,50,LEVEL_TEXT[id],this);
			namelabel.setAlign(CENTER, CENTER);
			play = new ImageButton(getWidth()-80,5,getHeight()-10,getHeight()-10,Images.FORWARD_ARROW,this) {
				public void elementReleased() {
					LEVEL.destroyAll();
					LEVEL = new GameScreen(LEVEL_TEXT[id],LEVEL_SIZE[id]);
					
				}
			};
			play.setMode(ImageButton.CENTERED);
			
		}
		
		//draw border.
		protected void update() {
			super.update();
			g.noFill();
			g.stroke(100,100,60);
			g.rect(0, 0, getWidth(), getHeight());
		}
	}	
	
	
	private final static String[] LEVEL_TEXT = {
			"Sandbox",
			"Diode",
			"Inverter",
			"Or Gate",
			"And Gate",
			"Rising Edge Monostable",
			"Falling Edge Monostable",
			"2 Tick Rising Edge Monostable",
			"1 Tick Clock",
			"2 Tick Clock",
			"20 Tick Delay",
			"Xor Gate",
			"Half Adder",
			"Latch",
			"Divide by 4 counter",
			"Toggle Latch",
			"8 Bit Input Adder",
			"8 Bit Read/Write Cells",
			"Seven Segment Display",
			"2 Bit Input Multiplier",
			"4 Bit Input Divider"
	};
	private final static int[] LEVEL_SIZE = {
		10,//"Sandbox",
		2,//"Diode",
		2,//"Inverter",
		2,//"Or Gate",
		2,//"And Gate",
		2,//"Rising Edge Monostable",
		2,//"Falling Edge Monostable",
		2,//"2 Tick Rising Edge Monostable",
		2,//"1 Tick Clock",
		2,//"2 Tick Clock",
		4,//"20 Tick Delay",
		3,//"Xor Gate",
		3,//"Half Adder",
		3,//"Latch",
		3,//"Divide by 4 counter",
		3,//"Toggle Latch",
		4,//"8 Bit Input Adder",
		4,//"8 Bit Read/Write Cells",
		5,//"Seven Segment Display",
		6,//"2 Bit Input Multiplier",
		6 //"4 Bit Input Divider"	
	};
	
	private final static String[] SPLASHES = {
		"Announcement: Welcome to T.A.Co Labs",
		"Announcement: Make sure you dont forget to tell your friends and family about our edible company food!",
		"Announcement: Please do not walk into the spinning blades positioned around the halls UNLESS asked.",
		"Announcement: Remember, you must make a sacrifice. For Science!",
		"Announcement: Will whoever dropped their spleen in the spinning blades please retrieve it, all operations involving those spinning blades have been halted.",
		"Announcement: There is now a free spot in the Face - Monitor transplant operation as we misplaced our test subject. Volunteers would be appreciated.",
		"Announcement: ZZZzzZZZZZZZgGGzZShHSSZZSSsZshSSHZSSSSSZSZSSShelpSHZhhSHZZZz",
		"Announcement: Please remember, in the unexpected case of death, please assume the Being Dead Position so staff can easily identify and dispose of you.",
		"Announcement: Do not panic. This is not a drill.",
		"Announcement: Seven. Six. Eight. One. Three. Two. Ninety nine. Twelve. Zero. Fourteen. Sixteen. Sixty Seven. Five. Ten. Fifteen. Twenty. Six point five five five five fXHZXSZHZZZZX"
		
	};
	
	
	
}
