package tests;

import static core.MainProgram.*;
import static util.DB.*;
import java.util.LinkedList;

public class test1 {
	static int value = 10;

	public static void yell() {
		LinkedList<LinkedList<Integer>> a = new LinkedList<LinkedList<Integer>>();
		LinkedList<Integer> b = new LinkedList<Integer>();
		b.add(4);
		b.add(5);
		b.add(6);
		b.add(7);
		b.add(8);
		b.add(9);
		b.add(10);
		LinkedList<Integer> c = new LinkedList<Integer>();
		c.add(1);
		c.add(2);
		c.add(3);
		c.add(4);
		c.add(5);
		c.add(6);
		c.add(7);
		
		a.add(b);
		a.add(c);
		
		
	}

}
