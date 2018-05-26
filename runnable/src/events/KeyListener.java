package events;

public interface KeyListener extends Listener{
	default void keyPressed() {};
	default void keyReleased() {};       
	default void keyTyped() {};
}
