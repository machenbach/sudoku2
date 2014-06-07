package org.mike.test.solver;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;
import org.mike.sudoku.CantSolveException;
import org.mike.sudoku.DuplicateAnswerException;
import org.mike.sudoku.Puzzle;
import org.mike.sudoku.Solver;

public class FullPuzzles {
	@Rule
	public TestRule watcher = new TestWatcher() {
	   protected void starting(Description description) {
	      System.out.println("Starting test: " + description.getMethodName());
	   }
	};	
	

	@Before
	public void setUp() throws Exception {
	}

	boolean solvePuzzle (String board) {
		Puzzle puzzle = new Puzzle();
		boolean ret = true;
		try {
			puzzle.readBoard(board);
		} catch (IOException e) {
			fail(e.getMessage());
		}
		puzzle.printBoard();
		Solver solver = new Solver(puzzle);
		try {
			solver.solve();
		}
		catch (CantSolveException e) {
			System.out.println(e.getMessage());
			ret = false;
		}
		puzzle.printBoard();
		System.out.println(puzzle.toString());
		System.out.println(puzzle.checkPuzzle() ? "Puzzle is OK" : "Puzzle is bad");
		solver.printSolverInfo();
		return ret;
	}
	
	
	
	@Test
	public void puzzle1 () {
		String b  = " 3 7 5 8 15  9  2 4         6   8  3 9 6 7 5 5  3   7         8 4  8  39   1 2 4 ";
		assertTrue(solvePuzzle(b));
	}

	@Test
	public void puzzle2 () {
		String b  = "  6  981     8      26    3   9   324       925   1  657   49      2      8      ";
		assertTrue(solvePuzzle(b));
	}

	@Test
	public void puzzle3 () {
		String b  = "       7 1 5   6 9247  8   5   1 7   8  7  6   2 4   3   4  8373 9   5 2 2       ";
		assertTrue(solvePuzzle(b));
	}

	@Test
	public void puzzle4 () {
		String b  = "     5     7 8 3  9      2 6  29   8        37 8 31  9 1   3  4  5 7 1    65     ";
		assertTrue(solvePuzzle(b));
	}
	
	@Test
	public void puzzle5 () {
		String b  = " 392 17    7      8   6 4   1 85  4  9     5  2  39 6   2 1   4      8    43 862 ";
		assertTrue(solvePuzzle(b));
	}
	
	@Test
	public void puzzle6 () {
		String b  = "     5  75 7 8 3 19      256  29   8    5   37 8 31  9 1   3  4  5 7 1 6 7651   2";
		assertTrue(solvePuzzle(b));
	}
	

}
