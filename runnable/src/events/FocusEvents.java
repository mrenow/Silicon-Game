package events;

import static processing.core.PApplet.print;

import java.util.ArrayList;

import elements.Element;

public class FocusEvents{

	  public static ArrayList<FocusListener> list = new ArrayList<FocusListener>();

	  public static FocusListener focus = null;
	  public static void check(){

		  //the latest element in the list will gain focus. This is consistent with the order of drawing (last on top). 
		  for(FocusListener e: list){
		    if(((Element)e).cursorhover){
		      //if the focus changes, call unfocused on the old focus. Then set the new focus and call focused.
		      print("focuscheck",e);
		      if(focus != e){
		        if(focus != null) focus.elementUnfocused();
		        focus = e;
		        focus.elementFocused();
		      }
		    }
		  }
		}
	  public static void add(FocusListener e){
		  HoverEvents.add(e);
		  list.add(e);
		}
	
}