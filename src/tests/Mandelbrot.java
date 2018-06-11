package tests;
import static core.MainProgram.*;

import java.awt.Color;
import java.awt.event.KeyEvent;

import elements.Container;
import elements.MapNavigator;
import elements.Screen;
import events.*;
import processing.core.PVector;

//               ooo it lines up :) \/t
// Functioned as a test for the MapNavigator module.
public class Mandelbrot extends MapNavigator implements KeyListener, ScrollListener, MovementListener{
	
	public Mandelbrot(float x, float y, float w, float h, Container p) {
		super(x, y, w, h, p);
		KeyEvents.add(this);
		ScrollEvents.add(this);
		MovementEvents.add(this);
		zoomrate = 1.05f;
		zoom = 100;
		offset.x = 0;
		offset.y = 0;

		setZoomBounds(10,Float.POSITIVE_INFINITY);
		setOffsetBounds(-2,-2,4,4);
		// TODO Auto-generated constructor stub
	}

	float z = -1;
	static int maxsteps = 256;
	@Override
	protected void update() {
		if(p3.keyPressed) {
			if(KeyEvents.key[KeyEvents.VK_D]) {
				addOffset(5,0);
			}
			if(KeyEvents.key[KeyEvents.VK_A]){
				addOffset(-5,0);
			}
			if(KeyEvents.key[KeyEvents.VK_W]) {
				addOffset(0,-5);
			}
			if(KeyEvents.key[KeyEvents.VK_S]) {
				addOffset(0,5);
			}
			if(KeyEvents.key[KeyEvents.VK_EQUALS]) {
				z+=0.02;
			}
			if(KeyEvents.key[KeyEvents.VK_MINUS]) {
				z-=0.02;
			}
			if(KeyEvents.key[KeyEvents.VK_COMMA]) {
				maxsteps *= 1.01;
			}
			if(KeyEvents.key[KeyEvents.VK_PERIOD]) {
				maxsteps /= 1.01;
			}
			requestUpdate();
		}
		resetGraphics();
		g.translate(offset.x,offset.y);
		//g.scale(zoom);
		//g.translate(offset.x,offset.y);
		
		
		g.noStroke();
		PVector start = localToMapPos(0,0);
		PVector end = localToMapPos(getWidth(), getHeight());
		float x = start.x;
		float y = start.y;
		for(int i = 0; i < 255; i ++) {
			for (int j = 0; j < 255; j++) {
				PVector v = localToMapPos(j,i);
				g.fill(i,j,150);
				g.rect(j,i,1,1);
			}
		}
		
		
	}
	
	static int escapes(float a, float b, float z) {
		float a_0 = a;
		float b_0 = b;
		int steps = 1;
		while(steps<maxsteps && a*a + b*b < 10) {
			float c = a*a + z*b*b;
			b = 2*a*b + b_0;
			a = c + a_0;
			steps++;
		}
		if(steps == maxsteps) {
			return p3.color(0);
			
		}
		
		return p3.color(0,floor(128* (1 - cos(log(0.17f*steps)))),floor(128* (1 - cos(log(steps)))));
	}
	
	
	@Override
	public void keyPressed() {
		requestUpdate();
	}

	@Override
	public void keyReleased() {}

	@Override
	public void keyTyped() {}

	@Override
	public void elementHovered() {}

	@Override
	public void elementUnhovered() {}

	@Override
	public void elementScrolled(int value) {
		addZoom(-value,p3.mouseX, p3.mouseY);
		requestUpdate();
	}
	public void mouseMoved() {
		if(p3.mousePressed) {
			addOffset(p3.mouseX - p3.pmouseX, p3.mouseY - p3.pmouseY);
			requestUpdate();
		}
	}

}
