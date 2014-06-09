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
import org.mike.sudoku.mask.FourTwo;
import org.mike.sudoku.mask.Fours;
import org.mike.util.Histo;
import org.mike.util.Range;

public class DifficultyHisto {
	@Rule
	public TestRule watcher = new TestWatcher() {
	   protected void starting(Description description) {
	      System.out.println("Starting test: " + description.getMethodName());
	   }
	};	
	
	
	final static int SampleSize = 100;
	
	@Test
	public void testFourTwo() {
		Histo<Difficulty> histo = new Histo<Difficulty>();
		for (int i : new Range(SampleSize)) {
			Builder b = new Builder(FourTwo.class);
			b.generate();
			histo.addElem(b.getDifficulty());
		}
		
		SortedSet<Difficulty> keys = new TreeSet<Difficulty>(histo.keySet());
		for (Difficulty k : keys) {
			System.out.println(String.format("%s: %d", k, histo.get(k)));
		}
	}

	@Test
	public void testFours() {
		Histo<Difficulty> histo = new Histo<Difficulty>();
		for (int i : new Range(SampleSize)) {
			Builder b = new Builder(Fours.class);
			b.generate();
			histo.addElem(b.getDifficulty());
		}
		
		SortedSet<Difficulty> keys = new TreeSet<Difficulty>(histo.keySet());
		for (Difficulty k : keys) {
			System.out.println(String.format("%s: %d", k, histo.get(k)));
		}
	}

}
