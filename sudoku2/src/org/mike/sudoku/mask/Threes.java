package org.mike.sudoku.mask;

public class Threes extends BasicShuffle {

	public Threes() {
		// TODO Auto-generated constructor stub
	}

	@Override
	boolean[][] mask() {
		boolean[][] show = {
				{true, true, true, false, false,false, false, false, false},
				{true, true, true, false, false,false, false, false, false},
				{true, true, true, false, false,false, false, false, false},
				{true, true, true, false, false,false, false, false, false},
				{true, true, true, false, false,false, false, false, false},
				{true, true, true, false, false,false, false, false, false},
				{true, true, true, false, false,false, false, false, false},
				{true, true, true, false, false,false, false, false, false},
				{true, true, true, false, false,false, false, false, false}
		};

		return show;
	}

}
