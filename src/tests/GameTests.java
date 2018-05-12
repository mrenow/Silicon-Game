package tests;

import static core.MainProgram.*;
import static util.DB.*;
import game.*;
import util.SparseQuadTree;


public class GameTests {
	
	public static void testConnections() {
		SparseQuadTree<WireSegment> tiles = new SparseQuadTree<WireSegment>(3);
		//WireSegment.container = tiles;
		
		
		WireSegment a = new WireSegment(WireSegment.METAL,0,1),
					b = new WireSegment(WireSegment.METAL,1,1),
					c = new WireSegment(WireSegment.METAL,1,2),
					d = new WireSegment(WireSegment.METAL,2,1),
					e = new WireSegment(WireSegment.METAL,3,1),
					f = new WireSegment(WireSegment.METAL,1,3),
					g = new WireSegment(WireSegment.METAL,1,4),
					h = new WireSegment(WireSegment.METAL,1,5),
					i = new WireSegment(WireSegment.METAL,2,5),
					j = new WireSegment(WireSegment.METAL,3,5),
					k = new WireSegment(WireSegment.METAL,4,5);
		tiles.add(a,0,1);
		tiles.add(b,1,1);
		tiles.add(c,1,2);
		tiles.add(d,2,1);
		tiles.add(e,3,1);
		tiles.add(f,1,3);
		tiles.add(g,1,4);
		tiles.add(h,1,5);
		tiles.add(i,2,5);
		tiles.add(j,3,5);
		tiles.add(k,4,5);
	
	
		
		
		
		

		Gate z = new Gate(WireSegment.N_GATE, 0, 1),
			 y = new Gate(WireSegment.P_GATE, 0, 1),
			 x = new Gate(WireSegment.N_GATE, 0, 1),
			 w = new Gate(WireSegment.N_GATE, 0, 1);
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		println("All Tests Passed! WOOOO");
	}
	

}
