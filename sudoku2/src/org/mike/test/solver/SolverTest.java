package org.mike.test.solver;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

import java.util.HashSet;
import java.util.Set;

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

	Set<Integer> intset (int[] a)
	{
		Set<Integer> s = new HashSet<Integer>();
		for (int i : a) {
			s.add(i);
		}
		return s;
	}

	@Test
	public void testIntersect()
	{
		int[] s1 = {1, 2, 3};
		int[] s2 = {4, 5, 6};
		int[] s3 = {3, 4, 5};
		int[] s4 = {3};
		
		Solver solver = new Solver();
		
		Set<Integer> set1 = new HashSet<Integer>();
		Set<Integer> set2 = new HashSet<Integer>();
		Set<Integer> set3 = new HashSet<Integer>();
		Set<Integer> set4 = new HashSet<Integer>();
		set1 = intset(s1);
		set2 = intset(s2);
		set3 = intset(s3);
		set4 = intset(s4);
		
		assertTrue(solver.intersect(set1, set2).isEmpty());
		assertTrue(solver.intersect(set2, set1).isEmpty());
		assertTrue(solver.intersect(set1, set3).equals(set4));
		assertTrue(solver.intersect(set3, set1).equals(set4));
		assertTrue(solver.intersect(set1, set1).equals(set1));
		assertTrue(solver.intersect(set2,  solver.intersect(set1, set3)).isEmpty());
		
		
		
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
