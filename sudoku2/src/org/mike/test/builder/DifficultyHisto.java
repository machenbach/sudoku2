package org.mike.test.builder;

import java.util.SortedSet;
import java.util.TreeSet;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;
import org.mike.sudoku.Builder;
import org.mike.sudoku.Solver.Difficulty;
import org.mike.sudoku.mask.FourThree;
import org.mike.sudoku.mask.FourTwo;
import org.mike.sudoku.mask.Fours;
import org.mike.sudoku.mask.PuzzleMask;
import org.mike.sudoku.mask.Shuff30;
import org.mike.sudoku.mask.Shuff35;
import org.mike.sudoku.mask.Threes;
import org.mike.util.Histo;
import org.mike.util.Range;

public class DifficultyHisto {
	@Rule
	public TestRule watcher = new TestWatcher() {
	   protected void starting(Description description) {
	      System.out.println("Starting test: " + description.getMethodName());
	   }
	};	
	
	
	final static int SampleSize = 50;
	
	@Test
	public void testFours() {
		runTest(Fours.class);
	}

	@Test
	public void testFourThree() {
		runTest(FourThree.class);
	}

	@Test
	public void testFourTwo() {
		runTest(FourTwo.class);
	}

	@Test
	public void testShuff30() {
		runTest(Shuff30.class);
	}

	@Test
	public void testShuff35() {
		runTest(Shuff35.class);
	}

	@Test
	public void testThrees() {
		runTest(Threes.class);
	}

	/**
	 * @param clazz
	 */
	public void runTest(Class<? extends PuzzleMask> clazz) {
		Histo<Difficulty> histo = new Histo<Difficulty>();
		for (@SuppressWarnings("unused") int i : new Range(SampleSize)) {
			Builder b = new Builder(clazz);
			b.generate();
			histo.addElem(b.getDifficulty());
		}
		
		SortedSet<Difficulty> keys = new TreeSet<Difficulty>(histo.keySet());
		for (Difficulty k : keys) {
			System.out.println(String.format("%s: %d", k, histo.get(k)));
		}
	}

}
