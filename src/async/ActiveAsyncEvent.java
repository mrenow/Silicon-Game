package async;

import static core.MainProgram.*;
import static util.DB.*;

//Convention: Events to be named RepeatFunctionName
public abstract class ActiveAsyncEvent extends AsyncEvent {
	protected void start() {}
	protected void stop() {}
	protected abstract boolean condition();
}