package org.mike.sudoku.mask;

import org.mike.util.R;
import org.mike.util.Range;

public class ShuffledMask implements PuzzleMask {

	boolean[] mask = new boolean[81];
	
	public ShuffledMask(int n) {
		for (int i : new Range(n)) {
			mask[i] = true;
			shuffle(mask);
		}
	}
	void shuffle(boolean[] ary) {
		int n = ary.length;
		for (int i = 0; i < n; i++) {
			int j = R.random().nextInt(n - i) + i;
			boolean tmp = ary[i];
			ary[i] = ary[j];
			ary[j] = tmp;
		}
	}
	

	@Override
	public boolean show(int row, int col) {
		return mask[row * 9 + col];
	}

}
