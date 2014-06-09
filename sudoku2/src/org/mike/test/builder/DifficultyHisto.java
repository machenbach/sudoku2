package org.mike.test.builder;

import java.util.SortedSet;
import java.util.TreeSet;

import org.junit.Test;
import org.mike.sudoku.Builder;
import org.mike.sudoku.Solver.Difficulty;
import org.mike.util.Histo;
import org.mike.util.Range;

public class DifficultyHisto {
	
	final static int SampleSize = 200;
	
	@Test
	public void test() {
		Histo<Difficulty> histo = new Histo<Difficulty>();
		for (int i : new Range(SampleSize)) {
			Builder b = new Builder();
			b.generate();
			histo.addElem(b.getDifficulty());
		}
		
		SortedSet<Difficulty> keys = new TreeSet<Difficulty>(histo.keySet());
		for (Difficulty k : keys) {
			System.out.println(String.format("%s: %d", k, histo.get(k)));
		}
	}

}
