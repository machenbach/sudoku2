package org.mike.sudoku.mask;

public class FourThree extends BasicShuffle {

	public FourThree() {
		// TODO Auto-generated constructor stub
	}

	@Override
	boolean[][] mask() {
		boolean[][] show = {
				{true, true, true, true, false,false, false, false, false},
				{true, true, true, false, false,false, false, false, false},
				{true, true, true, true, false,false, false, false, false},
				{true, true, true, false, false,false, false, false, false},
				{true, true, true, true, false,false, false, false, false},
				{true, true, true, false, false,false, false, false, false},
				{true, true, true, true, false,false, false, false, false},
				{true, true, true, false, false,false, false, false, false},
				{true, true, true, true, false,false, false, false, false}
		};

		return show;
	}

}
