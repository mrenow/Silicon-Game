package core;

import static core.MainProgram.*;
import processing.core.PImage;

// Contains all image files
public class Images {
	
	public static PImage BACK_ARROW;
	public static PImage FORWARD_ARROW;
	public static PImage DOCUMENT_1;
	public static PImage DOCUMENT_2;
	
	public static void init(){
		BACK_ARROW = p3.loadImage("BackArrow.bmp");
		FORWARD_ARROW = p3.loadImage("ForwardArrow.bmp");

		DOCUMENT_1 = p3.loadImage("Welcome-1.png");
		DOCUMENT_2 = p3.loadImage("Welcome-2.png");	
	}
}
