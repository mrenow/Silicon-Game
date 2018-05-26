package util;

import static core.MainProgram.*;
import static processing.core.PApplet.print;
import static processing.core.PApplet.println;
import static util.DB.*;
import util.DB;

public class DB {
	public static int debug = 0;

	public static void DB_U(Object... message) {

		if (debug >= 3) {
			print("Update:");
			print(message);
			print(" @ ");
			println(p3.millis());
		}
	}

	// debug level
	/*
	 * n 3 Update 2 Action 1 Warn 0 Error
	 * 
	 * if(debug > n){println("message")}
	 */
	public static void DB_A(Object... message) {
		if (debug >= 2) {
			print("Action:");
			print(message);
			print(" @ ");
			println(p3.millis());
		}
	}

	public static void DB_W(Object... message) {
		if (debug >= 1) {
			print("  WARN:");
			print(message);
			print(" @ ");
			println(p3.millis());
		}
	}

	public static void DB_E(Object... message) {
		if (debug >= 0) {
			print(" ERROR:");
			print(message);
			print(" @ ");
			println(p3.millis());
		}
	}

	public static void DB_TRACE() {
		println(Thread.currentThread().getStackTrace());
	}

	static final int BEGIN_PRINTS = 2;
	static final int END_PRINTS = 4;

	public static boolean DB_ASSERT(Object a, Object b) {
		if (a.equals(b))
			return true;
		print("Assert Error: ");
		print(a);
		print(" != ");
		print(b);
		println(" @ ");
		StackTraceElement[] trace = Thread.currentThread().getStackTrace();
		// skip first two and last 4 prints
		for (int i = BEGIN_PRINTS; i < trace.length - END_PRINTS; i++) {
			println(trace[i]);
		}
		println(p3.millis());
		return false;
	}
}