package screens;

import static core.MainProgram.*;
import static util.DB.*;
import core.Images;


import elements.BasicButton;
import elements.Container;
import elements.Image;
import elements.ImageButton;
import elements.Screen;
import elements.ScrollPane;

public class HelpScreen  extends Screen{
	
	public HelpScreen() {
		
		new DocumentPane(0,0,getWidth(),getHeight(),this).setPaneHeight(45 + Images.DOCUMENT_1.height);
		new ImageButton(0,0,100,50,Images.BACK_ARROW, this) {
			public void elementClicked() {
				LEVEL.destroyAll();
				LEVEL = new MenuScreen();
			}
		}.setMode(ImageButton.CENTERED);
		
	}
	
	class DocumentPane extends ScrollPane{
		DocumentPane(float x, float y, float w, float h, Container p){
			super(x, y, w, h, SCROLL_Y, p);
			Image i1 = new Image(0, 15, 0.5f, Images.DOCUMENT_1, this);
			Image i2 = new Image(0, 0, 0.5f, Images.DOCUMENT_2, this);
			
			i1.setPos(getWidth()/2 -i1.getWidth()/2, 15);
			i2.setPos(getWidth()/2 -i2.getWidth()/2, 45 + i1.getHeight());
		}
		
		
	}
	
	
}
