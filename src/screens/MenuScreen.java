package screens;

import static core.MainProgram.*;
import static util.DB.*;


import elements.BasicButton;
import elements.Screen;

public class MenuScreen extends Screen{
	
	BasicButton startbutton;
	BasicButton helpbutton;
	BasicButton exit;
	
	
	
	public MenuScreen() {
		startbutton = new BasicButton(300,300,100,50,"Start", this) {
			
			@Override
			public void elementClicked() {
				LEVEL = new GameScreen();
				destroySelf();
			}
		};
		helpbutton = new BasicButton(800,300,100,50,"Help", this) {
			@Override
			public void elementClicked() {
				LEVEL = new HelpScreen();
				destroySelf();
			}
			
		};
		
		
	}
}
