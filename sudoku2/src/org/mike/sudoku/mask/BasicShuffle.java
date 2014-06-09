package org.mike.sudoku.mask;

import org.mike.util.R;
import org.mike.util.Range;

public abstract class BasicShuffle implements PuzzleMask {
	
	boolean[][] showAry;
	

	public BasicShuffle() {
		showAry = mask();
		buildShow();
	}
	
	abstract boolean[][] mask();

	void shuffle(boolean[] ary) {
		int n = ary.length;
		for (int i = 0; i < n; i++) {
			int j = R.random().nextInt(9 - i) + i;
			boolean tmp = ary[i];
			ary[i] = ary[j];
			ary[j] = tmp;
		}
	}
	
	void buildShow() {
		for (int i : new Range(9)) {
			shuffle(showAry[i]);
		}
	}


	@Override
	public boolean show(int row, int col) {
		return showAry[row][col];
	}
	
	

}
