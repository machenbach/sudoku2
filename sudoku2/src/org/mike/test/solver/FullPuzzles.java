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
		try {
			puzzle.readBoard(board);
		} catch (IOException e) {
			fail(e.getMessage());
		}
		puzzle.printBoard();
		Solver solver = new Solver(puzzle);
		puzzle = solver.solve();
		puzzle.printBoard();
		System.out.println(
				String.format("coverage: %s, depth: %s, tries: %s, queue: %s, guess level: %s", 
						solver.getCoverage(), solver.getSolveDepth(), solver.getSolveTries(), 
						solver.getMaxQueueSize(), solver.getGuessLevel()));
		System.out.println(puzzle.toString());
		System.out.println(puzzle.checkPuzzle() ? "Puzzle is OK" : "Puzzle is bad");
		return puzzle.isSolved();
	}
	
	
	
	@Test
	public void puzzle1 () {
		String b  = "  92   1    8 1 3  51    2   31 4     2 9 5     5  6   9    37  4 3 9    7   29  ";
		assertTrue(solvePuzzle(b));
	}

	@Test
	public void puzzle2 () {
		String b  = " 8   7 3 54  2  18      6    62 17   5     8   17  3    4      93  8  71 7 1   4 ";
		assertTrue(solvePuzzle(b));
	}

	@Test
	public void puzzle3 () {
		String b  = " 9     8   4  3   372     4 5 2 1  62       91  9 6 4 5   2  31   3  5   4     9 ";
		assertTrue(solvePuzzle(b));
	}

	@Test
	public void puzzle4 () {
		String b  = "1 54  9 7 8  5   1  3  2           92 4 1 6 85           2  1  3   7  9 4 6  92 3";
		assertTrue(solvePuzzle(b));
	}
	
	@Test
	public void puzzle5 () {
		String b  = " 7 3 9  24 6     5 2         31    6  4   1  16   54     9     6     2 15  7 1 4 ";
		assertTrue(solvePuzzle(b));
	}
	
	@Test
	public void puzzle6 () {
		String b  = "           46 18 31  25 4   46     5    7    7     32   8 1   4  18 72  2        ";
		assertTrue(solvePuzzle(b));
	}
	
	

}
