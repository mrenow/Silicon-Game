package elements;

import static core.MainProgram.*;
import static processing.core.PApplet.constrain;

import processing.core.PVector;

// contains values and functions that make drawing to a zoomable, movable map convenient
// functionality can be interfaced with using any event listeners.
public abstract class MapNavigator extends Container{

	
	// translation of screen
	// offset resides in top left corner.
	protected PVector offset;
	private PVector minoffset;
	private PVector maxoffset;
	
	// magnification of objects
	protected float zoom;
	
	public static final float DEFAULT_ZOOMRATE = 1.01f;
	
	protected float zoomrate;
	
	private float minzoom = 10;
	private float maxzoom = 0.1f;
	
	
	
	public MapNavigator(float x, float y, float w, float h, Container p) {
		super(x, y, w, h, p);
		zoomrate = DEFAULT_ZOOMRATE;
		zoom = 1;
		offset = new PVector(0,0);
		minoffset = new PVector(0,0);
		maxoffset = new PVector(w,h);
		
		// TODO Auto-generated constructor stub
	}
	
	public void setOffsetBounds(float x1, float y1, float x2, float y2) {
		minoffset.x = x1;
		minoffset.y = y1;
		maxoffset.x = x2;
		maxoffset.y = y2;
		constrainOffset();
	}
	
	//copies values of objects p1 and p2.
	public void setOffsetBounds(PVector p1, PVector p2) {
		setOffsetBounds(p1.x, p1.y, p2.x, p2.y);
	}
	//zoom will be corrected by zoom action.
	public void setZoomBounds(float z1, float z2) {
		minzoom = z1;
		maxzoom = z2;
	}

	public void addOffset(float x, float y) {
		offset.x += x/zoom;
		offset.y += y/zoom;
		constrainOffset();
	}
	
	//translate screen
	public void addOffset(PVector v) {
		offset.sub(v.div(zoom));
		constrainOffset();
	}
	//zoom at a specific location.
	public void addZoom(float z, PVector v) {
		println("zooming");
		
		float oldzoom = zoom;
		PVector centre = localToMapPos(v);
		zoom *= pow(zoomrate, z);
		if(zoom > maxzoom) {
			zoom = maxzoom;
		}
		if(zoom < minzoom) {
			zoom = minzoom;
		}
		//zoom change
		float ratio = oldzoom/zoom;
		
		//zooming affects offset
		offset = PVector.lerp(centre, offset, ratio);
		
		constrainOffset();
	}

	public void addZoom(float z, float x ,float y) {
		addZoom(z,new PVector(x,y));
	}

	public float getMaxZoom() {
		return maxzoom;
		
	}
	public float getMinZoom() {
		return minzoom;
	}
	public float getZoom() {
		return zoom;
	}

	public PVector getOffset() {
		return offset.copy();
	}
	
	public PVector getMaxoffset() {
		return maxoffset.copy();
	}
	public PVector getMinOffset() {
		return minoffset.copy();
	}
	//local -> map position transformations
	public PVector localToMapPos(float x, float y) {
		return localToMapPos(new PVector(x,y));	
	}
	public PVector localToMapPos(PVector pos) {
		return PVector.div(pos, zoom).add(offset);
	}
	public float localToMapX(float x) {
		return x/zoom + offset.x;
	}
	public float localToMapY(float y) {
		return y/zoom + offset.y;
	}
	
	public PVector mapToLocalPos(float x, float y) {
		return mapToLocalPos(new PVector(x, y));
	}
	public PVector mapToLocalPos(PVector pos) {
		return PVector.sub(pos, offset).mult(zoom);
	}
	public float mapToLocalX(float x) {
		return (x - offset.x)*zoom;
	}
	public float mapToLocalY(float y) {
		return (y - offset.y)*zoom;
	}
	
	public float mapToLocalDist(float d) {
		return d*zoom;	
	}
	public PVector mapToLocalDist(PVector v) {
		return PVector.mult(v,zoom);	
	}
	public float localToMapDist(float d) {
		return d/zoom;
	}
	public PVector localToMapDist(PVector v) {
		return PVector.div(v,zoom);
	}
	

	private void constrainOffset() {
		offset.x = constrain(offset.x, minoffset.x, maxoffset.x);
		offset.y = constrain(offset.y, minoffset.y, maxoffset.y);
	}

	// Transforms to zoomed and translated context
	protected void transformView() {
		g.scale(zoom);
		g.translate(-offset.x, -offset.y);
	}

}
