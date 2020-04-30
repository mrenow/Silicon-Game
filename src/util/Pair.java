package util;

import java.io.Serializable;

public class Pair <T, U> implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	public T val1;
	public U val2;
	public Pair(T val1, U val2) {
		this.val1 = val1;
		this.val2 = val2;
	}
}
