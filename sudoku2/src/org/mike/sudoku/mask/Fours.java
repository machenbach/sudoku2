package org.mike.sudoku.mask;

public class Fours extends BasicShuffle {

	public Fours() {
		// TODO Auto-generated constructor stub
	}

	@Override
	boolean[][] mask() {
		boolean[][] show = {
				{true, true, true, true, false,false, false, false, false},
				{true, true, true, true, false,false, false, false, false},
				{true, true, true, true, false,false, false, false, false},
				{true, true, true, true, false,false, false, false, false},
				{true, true, true, true, false,false, false, false, false},
				{true, true, true, true, false,false, false, false, false},
				{true, true, true, true, false,false, false, false, false},
				{true, true, true, true, false,false, false, false, false},
				{true, true, true, true, false,false, false, false, false}
		};

		return show;
	}

}
