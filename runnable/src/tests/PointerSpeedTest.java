package tests;

import static core.MainProgram.*;


public class PointerSpeedTest {
	
	public static void test() {
		//construct ring of nodes:
		
		
		
		int loopsize = 1000000;
		
		
		Node first = new Node();
		Node curr = first;
		for(int i = 1; i< loopsize; i++) {
			curr.next = (curr = new Node());
		}
		curr.next = (curr = first);
		//test speed
		int t = p3.millis();
		int i =0;
		do {
			curr = curr.next;
			curr = curr.next;
			curr = curr.next;
			curr = curr.next;
			curr = curr.next;
			curr = curr.next;
			curr = curr.next;
			curr = curr.next;
			curr = curr.next;
			curr = curr.next;
			curr = curr.next;
			curr = curr.next;
			curr = curr.next;
			curr = curr.next;
			curr = curr.next;
			curr = curr.next;
		}while(curr != first);
		println(p3.millis() - t);
		
		
	}
	
}

class Node{
	public Node next;
	public Node() {
		
		
	}
}
