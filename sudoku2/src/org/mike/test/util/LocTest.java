package org.mike.test.util;

import static org.junit.Assert.*;

import org.junit.Test;
import org.mike.sudoku.Puzzle;
import org.mike.util.Box;
import org.mike.util.Loc;

public class LocTest {

	@Test
	public void testRow() {
		System.out.println("row");
		Puzzle p = new Puzzle();
		for (Loc l : Loc.rowRange(1)) {
			p.setSquare(l.row, l.col, 1);
		}
		p.printBoard();
		System.out.println(p);
	}

	@Test
	public void testCol() {
		System.out.println("col");
		Puzzle p = new Puzzle();
		for (Loc l : Loc.colRange(1)) {
			p.setSquare(l.row, l.col, 1);
		}
		p.printBoard();
		System.out.println(p);
	}

	@Test
	public void testBox() {
		System.out.println("box");
		Puzzle p = new Puzzle();
		for (Loc l : Loc.boxRange(new Box(1))) {
			p.setSquare(l.row, l.col, 1);
		}
		p.printBoard();
		System.out.println(p);
	}

}
