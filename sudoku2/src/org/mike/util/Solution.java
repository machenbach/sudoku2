package org.mike.util;

/**
 * @author mike
 * Class that extends Loc, and also has an answer
 */
public class Solution extends Loc {
	public int val;
	
	public Solution(int r, int c, int v) {
		super(r,c);
		val = v;
	}
	
	public String toString() {
		return "(" + row + ", " + col + ", " + val +")";
	}
}

