package events;

public interface HoverListener extends Listener{
	default public void elementHovered() {}
	default public void elementUnhovered() {}
}

