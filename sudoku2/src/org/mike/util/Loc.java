package org.mike.util;

/**
 * Location of a square.  Used for various range iterations
 * @author mike
 *
 */
public class Loc {
	public int row;
	public int col;
	
	public Loc (int r, int c) {
		row = r;
		col = c;
	}

	public String toString() {
		return "(" + row + ", " + col +")";
	}
	
	public static Loc[] rowRange(int col) {
		Loc[] range = new Loc[9];
		int ndx = 0;
		for (int row : new Range(9)) {
			range[ndx++] = new Loc(row, col);
		}
		return range;
	}
	
	public static Loc[] colRange(int row) {
		Loc[] range = new Loc[9];
		int ndx = 0;
		for (int col : new Range(9)) {
			range[ndx++] = new Loc(row, col);
		}
		return range;
	}
	
	public static Loc[] boxRange(Box box) {
		Loc[] range = new Loc[9];
		int ndx = 0;
		for (int row : box.rows()) {
			for (int col : box.cols()) {
				range[ndx++] = new Loc(row, col);
			}
		}
		return range;	
	}
}
	
