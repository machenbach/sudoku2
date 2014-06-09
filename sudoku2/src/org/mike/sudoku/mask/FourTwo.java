package org.mike.sudoku.mask;

public class FourTwo extends BasicShuffle {

	public FourTwo() {
		// TODO Auto-generated constructor stub
	}

	@Override
	boolean[][] mask() {
		boolean[][] show = {
				{true, true, true, true, false,false, false, false, false},
				{true, true, false, false, false,false, false, false, false},
				{true, true, true, true, false,false, false, false, false},
				{true, true, false, false, false,false, false, false, false},
				{true, true, true, true, false,false, false, false, false},
				{true, true, false, false, false,false, false, false, false},
				{true, true, true, true, false,false, false, false, false},
				{true, true, false, false, false,false, false, false, false},
				{true, true, true, true, false,false, false, false, false}
		};

		return show;
	}

}
