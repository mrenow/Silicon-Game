package events;

public interface MovementListener extends HoverListener {
	default void mouseMoved() {}
}