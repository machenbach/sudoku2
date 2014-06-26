package org.mike.test.solver;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;

import org.junit.Test;
import org.mike.sudoku.Puzzle;
import org.mike.util.Range;

public class PuzzleTest {


	@Test
	public void testReadBoard() throws IOException {
		String bd = "X3X7X5X8X15XX9XX2X4XXXXXXXXX6XX58XX38936X7X525XX3XX8X6XXX9XXXX8X4X586X39X8X1X2X4X";
		Puzzle puzzle = new Puzzle(bd);
		puzzle.printBoard();
		String bd2 = puzzle.toString();
		assertTrue(bd.equals(bd2));
	}

	@Test
	public void testPartialRead() throws IOException {
		String bd = "X3X7X";
		Puzzle puzzle = new Puzzle(bd);
		puzzle.printBoard();
		String bd2 = puzzle.toString();
		assertTrue(bd2.length() == 81 && bd2.startsWith(bd));
	}
	
	@Test
	public void testSet() {
		Puzzle puzzle = new Puzzle();
		if (puzzle.isFilled(1,1)) {
			fail("Bad filled read");
		}
		
		puzzle.setSquare(1, 1, 1);
		if (!puzzle.isFilled(1, 1)) {
			fail("Didn't fill correctly");
		}
	}
	
	@Test
	public void testCopy() throws IOException {
		String bd = " 3 7 5 8 15  9  2 4         6  58  38936 7 525  3  8 6   9    8 4 586 39 8 1 2 4 ";
		Puzzle puzzle = new Puzzle(bd);
		Puzzle p2 = new Puzzle(puzzle);
		for (int r : new Range(9)) {
			for (int c : new Range(9)) {
				if (p2.getSquare(r, c) != puzzle.getSquare(r, c)) {
					fail("Invalid copy");
				}
			}
		}
	}
	
	// Test that spaces and Xs in the empty spots do the same thing.  Both these puzzles are the same
	@Test
	public void readTest() throws IOException {
		String bd1 = "X3X7X5X8X15XX9XX2X4XXXXXXXXX6XX58XX38936X7X525XX3XX8X6XXX9XXXX8X4X586X39X8X1X2X4X";
		String bd2 = " 3 7 5 8 15  9  2 4         6  58  38936 7 525  3  8 6   9    8 4 586 39 8 1 2 4 ";
		Puzzle p1 = new Puzzle(bd1);
		Puzzle p2 = new Puzzle(bd2);
		assert(p1.equals(p2));
		assert(p1.toString().equals(p2.toString()));
		
	}

}
