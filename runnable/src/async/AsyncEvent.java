package async;

import static core.MainProgram.*;
import static util.DB.*;


// Convention: Events to be named RunFunctionName
public abstract class AsyncEvent {
	protected abstract void run();
}
