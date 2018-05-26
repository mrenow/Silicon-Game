package util;
import java.util.ArrayList;


//used in sparse quad tree
public class ArraysX {
	// finds the dimensions of a rectangular 2d array.
	// returns -1,-1 if not rectangular
	public static <T> int[] dimensions(T[][] array) {
		int size_0 = array.length;
		int size_1 = array[0].length;
		boolean valid = true;
		for (T[] subarray : array) {
			if(size_1 != subarray.length) {
				valid = false;
			}
		}
		if(valid) {
			return new int[]{size_0, size_1};
		} else {
			return new int[] {-1,-1};
		}
		
		
		
	}
}
