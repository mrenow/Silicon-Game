package async;

import static core.MainProgram.*;
import static util.DB.*;

public abstract class ActiveAsyncEvent extends AsyncEvent {
	public abstract void start();

	public abstract boolean condition();
}
