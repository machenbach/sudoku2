package org.mike.test.solver;

import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;
import org.mike.sudoku.Puzzle;
import org.mike.sudoku.Solver;

public class SolutionTests {

	@Rule
	public TestRule watcher = new TestWatcher() {
	   protected void starting(Description description) {
	      System.out.println("Starting test: " + description.getMethodName());
	   }
	};	
	
	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void SimplePattern1() {
		Puzzle puzzle = new Puzzle();
		puzzle.setSquare(0, 3, 4);
		puzzle.setSquare(7, 2, 4);
		puzzle.setSquare(3, 0, 4);
		puzzle.setSquare(2, 7, 4);
		puzzle.printBoard();
		
		Solver solver = new Solver(puzzle);
		solver.step();
		assertTrue(puzzle.getSquare(1, 1) == 4);
		
	}

	@Test
	public void SimplePattern2() {
		Puzzle puzzle = new Puzzle();
		puzzle.setSquare(0, 3, 4);
		puzzle.setSquare(2, 7, 4);
		puzzle.setSquare(1, 0, 3);
		puzzle.setSquare(1, 1, 5);
		puzzle.clearSquare(1, 2);
		puzzle.printBoard();
		
		Solver solver = new Solver(puzzle);
		solver.step();
		assertTrue(puzzle.getSquare(1, 2) == 4);
		
	}

	@Test
	public void SimplePattern3() {
		Puzzle puzzle = new Puzzle();
		puzzle.setSquare(0, 3, 4);
		puzzle.setSquare(2, 7, 4);
		puzzle.setSquare(7, 2, 4);
		puzzle.setSquare(1, 1, 5);
		puzzle.printBoard();
		
		Solver solver = new Solver(puzzle);
		solver.step();
		assertTrue(puzzle.getSquare(1, 0) == 4);
		
	}

	@Test
	public void SimplePattern4() {
		Puzzle puzzle = new Puzzle();
		puzzle.setSquare(0, 3, 4);
		puzzle.setSquare(7, 0, 4);
		puzzle.setSquare(1, 1, 5);
		puzzle.setSquare(1, 2, 3);
		puzzle.setSquare(2, 1, 6);
		puzzle.printBoard();
		
		Solver solver = new Solver(puzzle);
		solver.step();
		assertTrue(puzzle.getSquare(2, 2) == 4);
	}

	@Test
	public void SimplePattern5() {
		Puzzle puzzle = new Puzzle();
		puzzle.setSquare(1, 3, 4);
		puzzle.setSquare(7, 1, 4);
		puzzle.setSquare(0, 0, 5);
		puzzle.setSquare(0, 2, 3);
		puzzle.setSquare(2, 0, 6);
		puzzle.printBoard();
		
		Solver solver = new Solver(puzzle);
		solver.step();
		assertTrue(puzzle.getSquare(2, 2) == 4);
		
	}

	@Test
	public void SimplePattern6() {
		Puzzle puzzle = new Puzzle();
		for (int c = 1; c < 9; c++) {
			puzzle.setSquare(0, c, c+1);
		}
		puzzle.printBoard();
		
		Solver solver = new Solver(puzzle);
		solver.step();
		assertTrue(puzzle.getSquare(0, 0) == 1);
		
	}

	@Test
	public void AdvancedPattern1() {
		Puzzle puzzle = new Puzzle();
		puzzle.setSquare(0, 1, 2);
		puzzle.setSquare(0, 2, 3);
		puzzle.setSquare(0, 8, 1);
		puzzle.setSquare(2, 1, 4);
		puzzle.setSquare(2, 2, 5);
		puzzle.setSquare(2, 3, 2);
		puzzle.setSquare(2, 4, 6);
		puzzle.setSquare(5, 0, 1);
		puzzle.printBoard();
		
		Solver solver = new Solver(puzzle);
		solver.step();
		solver.printSolverInfo();
		assertTrue(puzzle.getSquare(2, 5) == 1);
	}

	@Test
	public void AdvancedPattern2() throws IOException {
		String bd = "1                    23456 ";
		Puzzle puzzle = new Puzzle();
		puzzle.readBoard(bd);
		puzzle.printBoard();
		
		Solver solver = new Solver(puzzle);
		solver.step();
		assertTrue(puzzle.getSquare(2,8) == 1);
	}

	@Test
	public void AdvancedPattern3() throws IOException {
		String bd = "         23  1    45     6 ";
		Puzzle puzzle = new Puzzle();
		puzzle.readBoard(bd);
		puzzle.setSquare(5, 2, 1);
		puzzle.setSquare(4, 6, 1);
		puzzle.printBoard();
		
		Solver solver = new Solver(puzzle);
		solver.step();
		assertTrue(puzzle.getSquare(2,8) == 1);
	}

	@Test
	public void AdvancedPattern4() throws IOException {
		String bd = "  2345678";
		Puzzle puzzle = new Puzzle();
		puzzle.readBoard(bd);
		puzzle.setSquare(4, 1, 1);
		puzzle.printBoard();
		
		Solver solver = new Solver(puzzle);
		solver.step();
		solver.printSolverInfo();
		assertTrue(puzzle.getSquare(0,0) == 1);
		assertTrue(puzzle.getSquare(0, 1) == 9);
	}

	@Test
	public void AdvancedPattern5() throws IOException {
		String bd = "   234567";
		Puzzle puzzle = new Puzzle();
		puzzle.readBoard(bd);
		puzzle.setSquare(4, 0, 8);
		puzzle.setSquare(5, 0, 9);
		puzzle.printBoard();
		
		Solver solver = new Solver(puzzle);
		solver.step();
		assertTrue(puzzle.getSquare(0,0) == 1);
	}

	@Test
	public void MasterPattern1() throws IOException {
		String bd = "   23   1 4         51";
		Puzzle puzzle = new Puzzle();
		puzzle.readBoard(bd);
		puzzle.setSquare(3, 0, 2);
		puzzle.setSquare(4, 0, 3);
		puzzle.printBoard();
		
		Solver solver = new Solver(puzzle);
		solver.step();
		solver.printSolverInfo();
		assertTrue("No Answer", puzzle.isFilled(1, 0));
		int answer = puzzle.getSquare(1, 0);
		assertTrue("Wrong Answer", answer == 1);
	}

	@Test
	public void MasterPattern2() throws IOException {
		String bd = "     567  23       4 ";
		Puzzle puzzle = new Puzzle();
		puzzle.readBoard(bd);
		puzzle.setSquare(3, 0, 8);
		puzzle.setSquare(4, 0, 9);
		puzzle.printBoard();
		
		Solver solver = new Solver(puzzle);
		solver.step();
		assertTrue(puzzle.getSquare(0,0) == 1);
	}
}
