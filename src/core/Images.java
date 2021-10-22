package core;

import static core.MainProgram.*;
import processing.core.PImage;

// Contains all image files
public class Images {
	
	public static PImage BACK_ARROW, FORWARD_ARROW, DOCUMENT_1, DOCUMENT_2, TRASH_ICON;
	
	public static void init(){
		BACK_ARROW = p3.loadImage("BackArrow.bmp");
		FORWARD_ARROW = p3.loadImage("ForwardArrow.bmp");

		DOCUMENT_1 = p3.loadImage("Welcome-1.png");
		DOCUMENT_2 = p3.loadImage("Welcome-2.png");	
		TRASH_ICON = p3.loadImage("Trash.bmp");
	}
}
