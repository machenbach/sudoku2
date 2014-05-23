package org.mike.util;


/*
 * Box is for handling the boxes.  Constructor takes a box position (0, 1, 2)(0, 1, 2)
 * and notes it.
 * rows() returns the 9x9 row positions for this box, and cols() the 9x9 columns
 * static boxAt takes a 9x9 row,col, and returns the box it's in
 */
class Box {
	// row and column of the box.  0, 1 or 2
	int boxRow;
	int boxCol;
	
	public Box(int boxRox, int boxCol) {
		this.boxRow = boxRox;
		this.boxCol = boxCol;
	}
	
	// This constructs a linear box number, useful for iterating through all the boxes
	public Box (int boxNo) {
		this (boxNo / 3, boxNo % 3);
	}
	
	public Range rows() {
		// row = 3 * boxRow
		return new Range(boxRow * 3, boxRow * 3 + 3);
	}
	
	public Range cols() {
		return new Range(boxCol * 3, boxCol * 3 + 3);
	}
	
	public static Box boxAt (int row, int col) {
		return new Box(row/3, col/3);
	}
	
}
