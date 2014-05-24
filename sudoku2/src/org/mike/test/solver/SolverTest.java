package org.mike.test.solver;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;
import org.mike.sudoku.Puzzle;
import org.mike.sudoku.Solver;

public class SolverTest {

	Puzzle puzzle = new Puzzle();
	int[] oneToNine = {0, 1, 2, 3, 4, 5, 6, 7, 8};

	@Before
	public void setUp() throws Exception {
		//String bd = " 3 7 5 8 15  9  2 4         6  58  38936 7 525  3  8 6   9    8 4 586 39 8 1 2 4 ";
		String bd2 ="8  6 3  4    7   2 6 4  7      85     2   37    72      8  4 1 4   3    1    8 93";
		puzzle.readBoard(bd2);
	}

	@Test
	public void testInitial() {
		Solver solver = new Solver(puzzle);
		assertFalse(solver.madeProgress());
		for (int r : oneToNine) {
			for (int c : oneToNine) {
				assertFalse(solver.hasAnswer(r, c));
			}
		}
		solver.step();
		assertTrue(solver.madeProgress());
		
	}
	
	@Test
	public void testPrintChoices() {
		Solver solver = new Solver(puzzle);
		solver.step();
		solver.printSolverInfo();
		puzzle.printBoard();
		assertTrue(solver.madeProgress());
		solver.fillAnswers();
		puzzle.printBoard();
		assertFalse(solver.madeProgress());
		
	}

}
