package org.mike.test.builder;

import static org.junit.Assert.fail;
import static org.junit.Assert.assertTrue;

import java.util.HashSet;
import java.util.Set;

import org.junit.Test;
import org.mike.sudoku.Builder;
import org.mike.sudoku.NoSolutionException;
import org.mike.util.Range;

public class BuilderTests {

	Integer[][] puzzle;
	
	
	public void testColumns() {
		for (int c : new Range(9)) {
			Set<Integer> nums = new HashSet<Integer>(Range.rangeList(1,10));
			for (int r : new Range(9)) {
				nums.remove(puzzle[r][c]);
			}
			if (nums.size() != 0) {
				fail("Columns had duplicates");
			}
		}
	}

	public void testRows() {
		for (int r : new Range(9)) {
			Set<Integer> nums = new HashSet<Integer>(Range.rangeList(1,10));
			for (int c : new Range(9)) {
				nums.remove(puzzle[r][c]);
			}
			if (nums.size() != 0) {
				fail("Rows had duplicates");
			}
		}
	}

	public void testBoxes() {
		for (int tr : new Range(3)) {
			for (int tc : new Range(3)) {
				Set<Integer> nums = new HashSet<Integer>(Range.rangeList(1, 10));
				for (int r : new Range(3)) {
					for (int c : new Range(3)) {
						nums.remove(puzzle[tr * 3 + r][tc * 3 + c]);
					}
				}
				if (nums.size() != 0) {
					fail("Boxes had duplicates");
				}
			}
		}
	}
	
	@Test
	public void testBuilder()
	{
		boolean noFailure = true;
		int success = 0;
		for (int i : new Range(100)) {
			try {
				Builder p = new Builder();
				puzzle = p.toArray();
				testColumns();
				testRows();
				testBoxes();
				success++;
			} catch (NoSolutionException e) {
				noFailure = false;
				System.out.println(String.format("No Solution at try %d, %d successes", i, success));
			}
			assertTrue(noFailure);
		}
		
	}

}
