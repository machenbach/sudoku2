package org.mike.test.solver;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;

import org.junit.Test;
import org.mike.sudoku.Puzzle;

public class PuzzleTest {


	@Test
	public void testReadBoard() {
		String bd = " 3 7 5 8 15  9  2 4         6  58  38936 7 525  3  8 6   9    8 4 586 39 8 1 2 4 ";
		Puzzle puzzle = new Puzzle();
		try {
			puzzle.readBoard(bd);
		} catch (IOException e) {
			fail("IO Exception" + e);
		}
		puzzle.printBoard();
		String bd2 = puzzle.toString();
		assertTrue(bd.equals(bd2));
	}

	@Test
	public void testPartialRead() {
		String bd = " 3 7 ";
		Puzzle puzzle = new Puzzle();
		try {
			puzzle.readBoard(bd);
		} catch (IOException e) {
			fail("IO Exception" + e);
		}
		puzzle.printBoard();
		String bd2 = puzzle.toString();
		assertTrue(bd2.length() == 81 && bd2.startsWith(bd));
	}


}
