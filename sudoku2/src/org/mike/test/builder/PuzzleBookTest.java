package org.mike.test.builder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.AfterClass;
import org.junit.BeforeClass;
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
import org.mike.util.Range;

public class PuzzleBookTest {
	@Rule
	public TestRule watcher = new TestWatcher() {
	   protected void starting(Description description) {
	      System.out.println("Starting test: " + description.getMethodName());
	   }
	};	
	
	
	final static int SampleSize = 20;
	static Map<Integer, List<String>> allPuzzles;
	
	@BeforeClass
	static public void Init() {
		allPuzzles = new HashMap<Integer, List<String>>();
		for (int i : new Range(5)) {
			allPuzzles.put(i, new ArrayList<String>());
		}
		
	}
	@AfterClass
	static public void Dumpit() {
		for (int i : new Range(5)) {
			for (String s : allPuzzles.get(i)) {
				System.out.println(s);
			}
		}
	}
	
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

	Difficulty EasyMed = new Difficulty(1, 0, 0);
	Difficulty Hard = new Difficulty(2, 1, 0);
	
	int getLevel (Difficulty d) {
		if (d.equals(Difficulty.UNSOLVABLE)) {
			return 0;
		}
		if (d.equals(EasyMed)) {
			if (d.getTries() <= 9) {
				return 1;
			}
			else {
				return 2;
			}
		}
		if (d.equals(Hard)) {
			return 3;
		}
		return 4;
	}
	
	String entryString(Builder b) {
		return (String.format("%d:%s:%s", getLevel(b.getDifficulty()), b.toPuzzleString(), b.toSolutionString()));
	}
	
	void addEntry(Builder b) {
		List<String> entry = allPuzzles.get(getLevel(b.getDifficulty()));
		entry.add(entryString(b));
	}
	
	/**
	 * @param clazz
	 */
	public void runTest(Class<? extends PuzzleMask> clazz) {
		for (@SuppressWarnings("unused") int i : new Range(SampleSize)) {
			Builder b = new Builder(clazz);
			b.generate();
			addEntry(b);
		}
	}

}
