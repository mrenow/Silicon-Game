package elements;

import static core.MainProgram.*;

import events.FocusEvents;
import events.FocusListener;
import events.KeyEvents;
import events.KeyListener;
import util.DB;

public class TextField extends Text implements FocusListener, KeyListener {

	int cursorblinkvalue = 59;

	public TextField(float x, float y, float w, float h, Container parent) {
		super(x, y, w, h, "", parent);
		FocusEvents.add(this);
		KeyEvents.add(this);

	}

	@Override
	protected
	void update() {
		resetGraphics();

		g.fill(textfill);
		g.textFont(font);
		if (cursorblinkvalue % 60 <= 30) {
			g.text(text + '|', 0, 0, w, h);

		} else {
			g.text(text, 0, 0, w, h);
		}

		// will continue to update every tick as long as item is focused.
		if (FocusEvents.focus == this) {
			cursorblinkvalue++;
			requestUpdate();
		}
	}

	public void elementHovered() {
	}

	public void elementUnfocused() {
		cursorblinkvalue = 59;
		requestUpdate();
	}

	public void elementUnhovered() {
	}

	public void elementFocused() {
		requestUpdate();
	}

	public void keyPressed() {
	}

	public void keyReleased() {
	}

	public void keyTyped() {
		if (FocusEvents.focus == this) {
			if (KeyEvents.code == 8) {
				if (text.length() > 0) {
					text = text.substring(0, text.length() - 1);
				}
			} else {
				text += p3.key;
				cursorblinkvalue = 59;
				requestUpdate();
			}
		}

	}
}
/*
 * //deals with text wrapping. void update() { String[] words = text.split(" ");
 * g = createGraphics((int)w+1,(int)h+1 );
 * 
 * 
 * g.fill(bgfill); g.rect(0,0,w,h); g.fill(textfill); g.textFont(font);
 * 
 * //maximum text height + line gap float lineheight =
 * font.descent()+font.ascent()+font.getSize() + linegap;
 * 
 * float linewidth = w - padding*2;
 * 
 * //index in words int i = 0;
 * 
 * int row = 1; while (i<words.length && lineheight*(row-1)<h) { String line =
 * "";
 * 
 * 
 * //if the current word exceeds box width, the word will be wrapped to the next
 * line. if(linewidth<g.textWidth(words[i])){
 * 
 * //index in string; int j = 0;
 * 
 * //counts up each letter until the length of the line is exceeded. Draws to
 * screen and moves to next row. //Repeats until word is exhausted of
 * characters. while(lineheight*(row-1)<h){ String nextline = line +
 * words[i].charAt(j); do{ line = nextline; j++; }while(j<words[i].length() &&
 * linewidth>g.textWidth(nextline = (line+words[i].charAt(j))));
 * 
 * line += " ";
 * 
 * //when word finishes writing, break out of the loop without resetting line as
 * we have not yet reached the end of the line. if(j>=words[i].length()){ break;
 * } g.text(line, padding, lineheight*row+padding); row++; line = "";
 * 
 * } i++; }
 * 
 * //then normal text wrapping to the end of the line. while (i<words.length&&
 * linewidth>g.textWidth(words[i]+line)) { line += words[i]+" "; i++; }
 * g.text(line, padding, lineheight*row+padding); row++; }
 * 
 * }
 */