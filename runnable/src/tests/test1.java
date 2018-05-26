package tests;

import static core.MainProgram.*;
import static util.DB.*;
import java.util.LinkedList;

public class test1 {
	static int value = 10;

	public static void yell() {
		float a = -EPSILON;
		float b = EPSILON;
		
		println(a>b,a>=b, a<b,a<=b);
		println(a>0,a>=0, a<0, a<=0);
		println(b>0,b>=0, b<0, b<=0);
		
		
		
		
		
	}

}
