package tests;

import static core.MainProgram.*;
import static util.DB.*;

import java.util.ArrayList;
import java.util.Iterator;

import events.*;
import shapes_unused.*;
import elements.*;
import util.*;
import async.*;

public class UnitTests {

	public static void heapTest() {
		Heap<String> a = new Heap<String>();
		for (int i = 0; i < 100; i++) {
			// payload ("e") does not matter, random priorities assigned
			a.add((int) p3.random(0, 300), "e");
			if (!DB_ASSERT(a.verify(), true)) {
				println(a);
			}
		}
		for (int i = 0; i < 100; i++) {
			a.pop();
			if (!DB_ASSERT(a.verify(), true)) {
				println(a, a.verify());
			}
		}

	}

	public static void linkedListTest() {
		LLinkedList<Integer> a = null, b = null, c = null,
				            d = null, e = null, f = null,
				            g = null, h = null, i = null,
						    j = null, k = null, l = null,
						    m = null;
		Iterator<Integer> miter;
		try {
		try {
			a = new LLinkedList<Integer>(new Integer[] { 1, 2, 3, 4, 5 });
			b = new LLinkedList<Integer>(new Integer[] { 3, 4, 5, 6, 7 });
			DB_ASSERT(a.size(), 5);
			a.add(6);
			DB_ASSERT(a.size(), 6);

			DB_ASSERT(a.get(5), 6);
			DB_ASSERT(a.get(0), 1);
			DB_ASSERT(a.indexOf(3), 2);
			DB_ASSERT(a.indexOf(6), 5);
			DB_ASSERT(b.indexOf(3), 0);
			a.addAt(3, b);
			DB_ASSERT(a.size(), 11);

			DB_ASSERT(a, new LLinkedList<Integer>(new Integer[] { 1, 2, 3, 3, 4, 5, 6, 7, 4, 5, 6 }));
			a.addAt(1, new Integer[] { 9, 8, 7, 6, 5, 4, 3 });
			DB_ASSERT(a.size(), 18);
			DB_ASSERT(a, new LLinkedList<Integer>(new Integer[] { 1, 9, 8, 7, 6, 5, 4, 3, 2, 3, 3, 4, 5, 6, 7, 4, 5, 6 }));
			DB_ASSERT(new LLinkedList<Integer>(a),new LLinkedList<Integer>(new Integer[] { 1, 9, 8, 7, 6, 5, 4, 3, 2, 3, 3, 4, 5, 6, 7, 4, 5, 6 }));
		} catch (Exception ex) {
			println(a);
			println(b);
			throw ex;
		}
		try {
			c = new LLinkedList<Integer>(new Integer[] { 9, 7, 5, 3, 1 });
			d = new LLinkedList<Integer>(new Integer[] { 3, 1, 4, 1, 5 });
			c.addAt(0, d);
			DB_ASSERT(c, new LLinkedList<Integer>(new Integer[] { 3, 1, 4, 1, 5, 9, 7, 5, 3, 1 }));
			c.add(77);
			DB_ASSERT(c, new LLinkedList<Integer>(new Integer[] { 3, 1, 4, 1, 5, 9, 7, 5, 3, 1, 77 }));
			c.add(new Integer[] { 1, 2, 3 });
			DB_ASSERT(c, new LLinkedList<Integer>(new Integer[] { 3, 1, 4, 1, 5, 9, 7, 5, 3, 1, 77, 1, 2, 3 }));
			c.addFirst(1);
			DB_ASSERT(c, new LLinkedList<Integer>(new Integer[] { 1, 3, 1, 4, 1, 5, 9, 7, 5, 3, 1, 77, 1, 2, 3 }));
			
		} catch (Exception ex) {
			println(c);
			println(d);
			throw ex;
		}
		
		//testing modification within self.
		try {
			e = new LLinkedList<Integer>(new Integer[] { 1, 2, 3, 4, 5 });
			f = new LLinkedList<Integer>(new Integer[] { 3, 4, 5, 6, 7 });
			e.addAt(3, f);

			f.addAt(4, 5);
			DB_ASSERT(f, new LLinkedList<Integer>(new Integer[] { 3, 4, 5, 6, 5, 7 }));

			f.addAt(4, new Integer[] { 9, 8, 9 });
			DB_ASSERT(f, new LLinkedList<Integer>(new Integer[] { 3, 4, 5, 6, 9, 8, 9, 5, 7 }));

			f.addAt(5, new LLinkedList<Integer>(new Integer[] { 9, 8, 9 }));
			DB_ASSERT(f, new LLinkedList<Integer>(new Integer[] { 3, 4, 5, 6, 9, 9, 8, 9, 8, 9, 5, 7 }));
			DB_ASSERT(e, new LLinkedList<Integer>(new Integer[] { 1, 2, 3, 3, 4, 5, 6, 9, 9, 8, 9, 8, 9, 5, 7, 4, 5 }));
			DB_ASSERT(e.size(), 17);

			DB_ASSERT(e.take(0), 1);
			DB_ASSERT(e, new LLinkedList<Integer>(new Integer[] { 2, 3, 3, 4, 5, 6, 9, 9, 8, 9, 8, 9, 5, 7, 4, 5 }));
			DB_ASSERT(e.size(), 16);

			DB_ASSERT(e.take(15), 5);
			DB_ASSERT(e, new LLinkedList<Integer>(new Integer[] { 2, 3, 3, 4, 5, 6, 9, 9, 8, 9, 8, 9, 5, 7, 4 }));
			DB_ASSERT(e.size(), 15);

			DB_ASSERT(e.remove(17), false);
			DB_ASSERT(e, new LLinkedList<Integer>(new Integer[] { 2, 3, 3, 4, 5, 6, 9, 9, 8, 9, 8, 9, 5, 7, 4 }));
			DB_ASSERT(e.size(), 15);


			DB_ASSERT(f.remove(7), true);
			DB_ASSERT(e, new LLinkedList<Integer>(new Integer[] { 2, 3, 3, 4, 5, 6, 9, 9, 8, 9, 8, 9, 5, 4 }));
			DB_ASSERT(e.size(), 14);

			DB_ASSERT(f.remove(7), false);
			DB_ASSERT(e, new LLinkedList<Integer>(new Integer[] { 2, 3, 3, 4, 5, 6, 9, 9, 8, 9, 8, 9, 5, 4 }));
			DB_ASSERT(e.size(), 14);

			DB_ASSERT(f, new LLinkedList<Integer>(new Integer[] { 3, 4, 5, 6, 9, 9, 8, 9, 8, 9, 5 }));
		} catch (Exception ex) {
			println("e: ", e);
			println("f: ", f);
			throw ex;
		}
		//testing more esoteric behaviour (list in lists in lists and changing the child from the parent)
		try {
			g = new LLinkedList<Integer>();
			h = new LLinkedList<Integer>();
			i = new LLinkedList<Integer>();
			j = new LLinkedList<Integer>();
			
			// + + - -
			g.add(h);
			DB_ASSERT(g, new LLinkedList<Integer>());
			DB_ASSERT(h, new LLinkedList<Integer>());
			DB_ASSERT(g.size(), 0);
			
			// + + - 1 -
			g.add(1);
			DB_ASSERT(g, new LLinkedList<Integer>(1));
			DB_ASSERT(g.size(), 1);
			
			// + 2 + - 1 -
			g.addFirst(2);
			DB_ASSERT(g, new LLinkedList<Integer>(2,1));
			DB_ASSERT(g.size(), 2);
			
			// + 2 + - 1 3 -
			g.add(3);
			DB_ASSERT(g, new LLinkedList<Integer>(2, 1, 3));
			DB_ASSERT(g.size(), 3);
			
			// + 2 + 3 4 - 1 3 -
			h.add(3,4);
			DB_ASSERT(h, new LLinkedList<Integer>(3, 4));
			DB_ASSERT(h.size(), 2);
			DB_ASSERT(g, new LLinkedList<Integer>(2, 3, 4, 1, 3));
			DB_ASSERT(g.size(), 5);
			
			// + 2 + 3 + - 4 - 1 3 - 
			g.addAt(2,i);
			DB_ASSERT(h, new LLinkedList<Integer>(3, 4));
			DB_ASSERT(h.size(), 2);
			DB_ASSERT(g, new LLinkedList<Integer>(2, 3, 4, 1, 3));
			DB_ASSERT(g.size(), 5);
			
			// + 2 + 3 + 4 5 - 4 - 1 3 -
			i.add(4,5);
			DB_ASSERT(g, new LLinkedList<Integer>(2, 3, 4, 5, 4, 1, 3));
			DB_ASSERT(g.size(), 7);
			
			// + 2 + 3 + 4 8 9 5 - 4 - 1 3 -
			i.addAt(1,8,9);
			DB_ASSERT(h, new LLinkedList<Integer>(3, 4, 8, 9, 5, 4));
			DB_ASSERT(h.size(), 6);
			DB_ASSERT(g, new LLinkedList<Integer>(2, 3, 4, 8, 9, 5, 4, 1, 3));
			DB_ASSERT(g.size(), 9);
	
			// + 2 + 3 + 4 8 9 5 - 4 + - - 1 3 -
			h.add(j);
			DB_ASSERT(g, new LLinkedList<Integer>(2, 3, 4, 8, 9, 5, 4, 1, 3));
			DB_ASSERT(g.size(), 9);
			
			// + 2 + 3 + 4 9 5 - 4 + - - 1 3 -
			g.remove(8);
			DB_ASSERT(g, new LLinkedList<Integer>(2, 3, 4, 9, 5, 4, 1, 3));
			DB_ASSERT(g.size(), 8);

			// + 2 + 3 + 4 9 5 - + - - 1 3 -
			g.take(5);
			DB_ASSERT(g, new LLinkedList<Integer>(2, 3, 4, 9, 5, 1, 3));
			DB_ASSERT(g.size(), 7);
			
			// + 2 + + 4 9 5 - + - - 1 3 -
			g.take(1);
			DB_ASSERT(g, new LLinkedList<Integer>(2, 4, 9, 5, 1, 3));
			DB_ASSERT(g.size(), 6);

			// + 2 + + 4 9 5 - + 1 2 3 - - 1 3 -
			j.add(1,2,3);
			DB_ASSERT(h, new LLinkedList<Integer>(4, 9, 5, 1, 2, 3));
			DB_ASSERT(h.size(), 6);
			DB_ASSERT(g, new LLinkedList<Integer>(2, 4, 9, 5, 1, 2, 3, 1, 3));
			DB_ASSERT(g.size(), 9);
			
			// + 2 + + 4 9 5 - - 1 3 -
			h.remove(j);

			DB_ASSERT(g, new LLinkedList<Integer>(2, 4, 9, 5, 1, 3));
			
			g.add(1);
			g.add(1);
			g.add(1);
			g.add(1);
			g.add(1);
			g.add(1);
			g.removeAll(1);
			DB_ASSERT(g, new LLinkedList<Integer>(2, 4, 9, 5, 3));
			
			
			
			
			
		} catch (Exception ex) {
			println("g: ", g);
			println("h: ", h);
			println("i: ", i);
			println("j: ", j);
			throw ex;
		}
		try {
			m = new LLinkedList<Integer>(1, 2, 3, 4, 5);
			miter = m.iterator();
			DB_ASSERT(miter.next(), 1);
			DB_ASSERT(miter.next(), 2);
			miter.remove();
			DB_ASSERT(m,new LLinkedList<Integer>(1,3,4,5));
			DB_ASSERT(miter.next(), 3);
			DB_ASSERT(miter.next(), 4);
			miter.remove();
			DB_ASSERT(m,new LLinkedList<Integer>(1,2,3,5));
			DB_ASSERT(miter.next(), 5);
		} catch( Exception ex) {
			println("m: ",m);
			throw ex;
		}
		
		} catch (Exception ex) {
			// Implement assert exception at some point pls
			DB_E("UNIT TESTS COMPLETED UNSUCCESSFULLY");
			println(ex.toString());
			println(ex.getStackTrace());
			
			System.exit(1);
		}
	}

	public static void sparseQuadTreeTest() {
		
		// depth 4:, 8*8 grid.
		SparseQuadTree<Integer> tree = null;
		Integer a, b, c, d, e;
		
		a = 1;
		b = 2;
		c = 3;
		d = 4;
		e = 5;
		tree = new SparseQuadTree<Integer>(4);
		try {

			tree.add(a, 3, 1);
			tree.add(b, 3, 3);
			tree.add(a, 2, 2);
			tree.add(e, 4, 3);
			tree.add(d, 5, 5);
			tree.add(a, 0, 7);
			tree.add(d, 7, 0);
			tree.add(c, 0, 0);
			tree.add(e, 7, 7);
			DB_ASSERT(tree.size(), 9);

			DB_ASSERT(tree.get(3, 1).contains(a), true);
			DB_ASSERT(tree.get(3, 3).contains(b), true);
			DB_ASSERT(tree.get(2, 2).contains(a), true);
			DB_ASSERT(tree.get(4, 3).contains(e), true);
			DB_ASSERT(tree.get(5, 5).contains(d), true);
			DB_ASSERT(tree.get(0, 7).contains(a), true);
			DB_ASSERT(tree.get(7, 0).contains(d), true);
			DB_ASSERT(tree.get(0, 0).contains(c), true);
			DB_ASSERT(tree.get(7, 7).contains(e), true);

			DB_ASSERT(tree.get(3, 1).contains(b), false);
			DB_ASSERT(tree.get(3, 3).contains(a), false);
			DB_ASSERT(tree.get(2, 2).contains(e), false);
			DB_ASSERT(tree.get(4, 3).contains(d), false);
			DB_ASSERT(tree.get(5, 5).contains(a), false);
			DB_ASSERT(tree.get(0, 7).contains(d), false);
			DB_ASSERT(tree.get(7, 0).contains(c), false);
			DB_ASSERT(tree.get(0, 0).contains(e), false);
			DB_ASSERT(tree.get(7, 7).contains(a), false);
		} catch (Exception ex) {
			println("tree.elements: ",tree.elements);
			throw ex;
		}

		LLinkedList<Integer> get = null;
		try {
			get = tree.get(0, 0, 8, 8);

			DB_ASSERT(get.contains(a), true);
			DB_ASSERT(get.contains(b), true);
			DB_ASSERT(get.contains(c), true);
			DB_ASSERT(get.contains(d), true);
			DB_ASSERT(get.contains(e), true);
			get = tree.get(2, 1, 5, 4);
			DB_ASSERT(get.contains(a), true);
			DB_ASSERT(get.contains(b), true);
			DB_ASSERT(get.contains(e), true);
			get = tree.get(2, 2, 5, 4);
			DB_ASSERT(get.contains(a), true);
			DB_ASSERT(get.contains(b), true);
			DB_ASSERT(get.contains(e), true);
			get = tree.get(1, 1, 7, 7);
			DB_ASSERT(get.contains(a), true);
			DB_ASSERT(get.contains(b), true);
			DB_ASSERT(get.contains(e), true);
			DB_ASSERT(get.contains(d), true);
			DB_ASSERT(get.contains(c), false);
		} catch (Exception ex) {
			println("tree.elements: ",tree.elements);
			println("get: ", get);
			throw ex;
		}
		
		//deletion
		//ToDo: implement region removal after elements have been removed.
		try {
			tree.delete(0, 0);
			DB_ASSERT(tree.get(0,0).contains(c),false);

			tree.delete(0, 0, 4, 3);
			DB_ASSERT(tree.size(), 6);
			
			get = tree.get(0, 0, 4, 3);
			DB_ASSERT(tree.get(0, 0).contains(c), false);
			DB_ASSERT(tree.get(1, 3).contains(a), false);
			DB_ASSERT(tree.get(2, 2).contains(a), false);
			println(get);
	
			DB_ASSERT(get.contains(a), false);
			DB_ASSERT(get.contains(b), false);
			DB_ASSERT(get.contains(c), false);
			DB_ASSERT(get.contains(d), false);
			DB_ASSERT(get.contains(e), false);
	
			get = tree.get(0, 0, 5, 4);
			DB_ASSERT(get.contains(a), false);
			DB_ASSERT(get.contains(b), true);
			DB_ASSERT(get.contains(c), false);
			DB_ASSERT(get.contains(d), false);
			DB_ASSERT(get.contains(e), true);
			println("tree.elements: ", tree.elements);
			tree.delete(0, 0, 8, 8);
			DB_ASSERT(tree.elements.empty(),true);
		}catch(Exception ex) {
			println("tree.elements: ", tree.elements);
			println("get: ", get);
			throw ex;
		}
		
		
		SparseQuadTree<Integer> tree2 = new SparseQuadTree<Integer>(1);
		tree2.add( 5, 1, 1);
		tree2.add( 4, 1, 0);
		tree2.add( 3, 0, 0);
		tree2.add( 2, 0, 1);
		println(tree2.elements);
				
		

		DB_U("ALL TESTS PASSED - YOU ARE AWESSOOOMMEE!!");
	}
}
