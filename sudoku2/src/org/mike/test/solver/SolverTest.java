package org.mike.test.solver;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.junit.Test;
import org.mike.sudoku.Puzzle;
import org.mike.sudoku.Solver;

public class SolverTest {

	String bd2 ="8  6 3  4    7   2 6 4  7      85     2   37    72      8  4 1 4   3    1    8 93";

	@Test
	public void testPuzzleConstructor() throws IOException {
		Puzzle puzzle = new Puzzle();
		puzzle.readBoard(bd2);
		Solver solver = new Solver(puzzle);
		assertFalse(solver.madeProgress());
		solver.solve();
		assertTrue(solver.madeProgress());
	}
	
	@Test
	public void testStringConstructor() throws IOException {
		Solver solver = new Solver(bd2);
		assertFalse(solver.madeProgress());
		solver.solve();
		assertTrue(solver.madeProgress());
	}
	

}
