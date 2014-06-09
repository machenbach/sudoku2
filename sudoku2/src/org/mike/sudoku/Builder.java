package org.mike.sudoku;

import java.io.IOException;
import java.util.HashSet;
import java.util.PriorityQueue;
import java.util.Random;

import org.mike.util.Range;

public class Builder {
	// this will be the solution of of the puzzle we are building
	Integer[][] solution;
	
	// whether or not to show the square
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

	// Solution retry.  We try to construct a random puzzle.  If an attempt fails, reset and
	// try again.  Blow up after we hit this limit.  Measurements have shown this is about
	// twice as many tries as we need
	static int MAX_TRIES = 500; 
	int buildTries = 0;
	int solveTries = 0;
	int solverTries = 0;
	int solverCoverage = 0;
	int solverGuessLevel = 0;
	int solverDepth = 0;
	int solverQueue = 0;
	Solver.Difficulty difficulty;
	
	
	static final Random random = new Random();
	
	/**
	 * A the builder of a new sudoku puzzle. 
	 */
	public Builder(){
	}


	/**
	 * Generate the puzzle.  First, build a solution.  Then generate a mask that
	 * will create the puzzle. 
	 * Finally, validate the puzzle by solving it.  This will also give us a hint at
	 * how hard it is.
	 * 
	 * @return true if the puzzle has been created, false otherwise
	 */
	public boolean generate(){
		// build a new valid solution
		try {
			buildSolution();
		} catch (NoSolutionException e) {
			// no luck this time, return false
			return false;
		}

		// create a new show mask until we have a solvable puzzle
		do {
			if (solveTries > MAX_TRIES * 2) {
				return false;
			}
			buildShow();
		}
		while (!isSolvablePuzzle());
		return true;
	}


	/**
	 * validatePuzzle tries to solve the puzzle we just made to show it's good puzzle,
	 * and collect some statistic
	 * @throws NoSolutionException
	 */
	private boolean isSolvablePuzzle() {
		// outer loop that tries a new show map
		try {
			Solver solver = new Solver(toPuzzleString());
			// inner loop that attempts to solve
			Puzzle p = solver.solve();
			if (toSolutionString().equals(p.toString())) {
				// We have a solution, so return
				solverTries = solver.getSolveTries();
				solverCoverage = solver.getCoverage();
				solverQueue = solver.getMaxQueueSize();
				solverDepth = solver.getSolveDepth();
				solverGuessLevel = solver.getGuessLevel();
				difficulty = solver.getDifficulty();
				return true;
			}
		} catch (IOException e) {
		}
		finally {
			solveTries++;
		}
		return false;
	}


	/**
	 * Build the solution of a puzzle. This proceeds as follows:
	 * There are two main routines: fillbox and fixbox
	 * fillbox fills a box with random numbers.  We will use this on the first box,
	 * then try combinations of fixbox for the rest, until we get a valid puzzle.
	 * If we can't get one in MAX_TRIES tries, give up
	 * 
	 * @throws NoSolutionException
	 */
	private void buildSolution() throws NoSolutionException {
		// The basics for this puzzle: box 0, 0
		solution = new Integer[9][9];
		fillbox(0,0);
		
		// Now try filling in the other boxes
		while (true) {
			try {
				
				fixbox(1,0);
				fixbox(0,1);
				fixbox(1,1);
				
				fixbox(2,0);
				fixbox(2,1);
				fixbox(0,2);
				fixbox(1,2);
				fixbox(2,2);
				
				break;
			}
			catch (NoSolutionException e) {
				// If we can't fill in this time, increment the try counter
				buildTries++;
				
				// reset the puzzle.  Copy only the box at 0,0
				Integer[][] oldPuzzle = solution;
				solution = new Integer[9][9];
				for (int r : new Range(3)) {
					for (int c : new Range(3)) {
						solution[r][c] = oldPuzzle[r][c];
					}
				}
				
				// blow up if we have to
				if (buildTries >= MAX_TRIES) {
					throw new NoSolutionException("Exceded max tries of " + MAX_TRIES, e);
				}
			}
		}
	}
	
	void shuffle(boolean[] ary) {
		int n = ary.length;
		for (int i = 0; i < n; i++) {
			int j = random.nextInt(9 - i) + i;
			boolean tmp = ary[i];
			ary[i] = ary[j];
			ary[j] = tmp;
		}
	}
	
	void buildShow() {
		for (int i : new Range(9)) {
			shuffle(show[i]);
		}
	}

	public int getBuildTries() {
		return buildTries;
	}
	
	public int getSolveTries() {
		return solveTries;
	}
	
	public int getSolverTries() {
		return solverTries;
	}


	public int getSolverCoverage() {
		return solverCoverage;
	}


	public int getSolverGuessLevel() {
		return solverGuessLevel;
	}


	public int getSolverDepth() {
		return solverDepth;
	}


	public int getSolverQueue() {
		return solverQueue;
	}
	
	public Solver.Difficulty getDifficulty() {
		return difficulty;
	}


	void fillbox(int rb, int cb) {
		Integer[] boxnums = Range.shuffleRange(1,10).toArray(new Integer[0]);
		int cur = 0;
		for (int r : new Range(3)) {
			for (int c : new Range(3)) {
				solution[rc(rb,r)][rc(cb,c)] = boxnums[cur++];
			}
		}
				
	}
	
	class PossibleElem extends HashSet<Integer> implements Comparable<PossibleElem> {


		private static final long serialVersionUID = 2450400441412942120L;
		
		int row;
		int column;
		
		public PossibleElem(int row, int column)
		{
			super(Range.rangeList(1, 10));
			this.row = row;
			this.column = column;
		}

		public int compareTo(PossibleElem o) {
			return Integer.compare(this.size(), o.size());
		}

		public int getRow() {
			return row;
		}

		public int getColumn() {
			return column;
		}
		
		public Integer pickVal() {
			Integer[] vals = toArray(new Integer[0]);
			return vals[random.nextInt(vals.length)];
		}
		
	}
	void fixbox (int rb, int cb) throws NoSolutionException {
		// create the set elements for the possible values
		PossibleElem[][] pelems = new PossibleElem[3][3];
		PriorityQueue<PossibleElem> pqueue = new PriorityQueue<PossibleElem>();
		
		for (int r : new Range(3)) {
			for (int c : new Range(3)) {
				PossibleElem p = new PossibleElem(rc(rb,r), rc(cb,c));
				pelems[r][c] = p;
				pqueue.add(p);
			}
		}
		
		// find the possible values in each cell.  First, sort through the rows
		// in this loop pr, pc are box relative r and c are values in the puzzle
		for (int pr : new Range(3)) {
			int r = rc(rb, pr);
			for (int c : new Range(9)) {
				// for each r/c, check if the puzzle has been set
				if (solution[r][c] != null) {
					// if it has, for each column in this box on this row, we remove it as a possible value
					for (int pc : new Range(3)) {
						pelems[pr][pc].remove(solution[r][c]);
					}
				}
			}
		}
		
		// same thing, this time for each of the columns
		
		for (int pc : new Range(3)) {
			int c = rc(cb, pc);
			for (int r : new Range(9)) {
				// for each r/c, check if the puzzle has been set
				if (solution[r][c] != null) {
					// if it has, for each column in this box on this row, we remove it as a possible value
					for (int pr : new Range(3)) {
						pelems[pr][pc].remove(solution[r][c]);
					}
				}
			}
		}
		// First check:  make sure the union of the all the sets contains 9 elements
		HashSet<Integer> possibleUnion = new HashSet<Integer>();
		for (PossibleElem e : pqueue) {
			possibleUnion.addAll(e);
		}
		if (possibleUnion.size() != 9) {
			throw new NoSolutionException("The box does not have all the numbers");
		}
		
		// second check:  See if any of the cells have empty sets
		for (PossibleElem e : pqueue) {
			if (e.isEmpty()) {
				throw new NoSolutionException("Empty Cell at " + e.getRow() + ", " + e.getColumn());
			}
		}
		
		// we now know what's possible.  Create the set of elements we still need
		HashSet<Integer> boxNeeds = new HashSet<Integer>(Range.rangeList(1, 10));
		
		// Run through the queue, smallest set first
		while (! pqueue.isEmpty()) {
			PossibleElem elem = pqueue.poll();
			// intersect with our needs
			elem.retainAll(boxNeeds);
			// if set is empty, there's now way to fix this puzzle
			if (elem.isEmpty()) {
				throw new NoSolutionException("Puzzle not possible");
			}
			
			// pick an element.  I guess we'll get an iterator, and choose the first one
			Integer val = elem.pickVal();
			
			// set this in the puzzle, remove it from needed and the rest of the sets
			solution[elem.getRow()][elem.getColumn()] = val;
			boxNeeds.remove(val);
			for (PossibleElem e : pqueue) {
				e.remove(val);
			}
		}
		
		
		
	}
	
	/**
	 * Convert a row or column box + row or column box number to a row or column 
	 * @param rcb Row or Column Box
	 * @param rc Row or COlumn in that box
	 * @return row or column index
	 */
	int rc(int rcb, int rc) {
		return rcb * 3 + rc;
	}
	

	public Integer puzzleInt(int row, int column)  {
		return solution[row][column];
	}
	
	public String puzzleElement(int row, int column) {
		return show[row][column] ? puzzleInt(row, column).toString() : "  ";
	}

	public String puzzleSolution(int row, int column) {
		return puzzleInt(row, column).toString();
	}
	public Integer[][] toArray() {
		return solution;
	}

	/**
	 * convert the solution to a string
	 * @return String solution
	 */
	public String toSolutionString() {
		StringBuffer sb = new StringBuffer();
		for (int row : new Range(9)) {
			for (int col : new Range(9)) {
				sb.append(solution[row][col]);
			}
		}
		return sb.toString();
	}

	/**
	 * convert the puzzle (with missing squares) to a string
	 * Use the show array to convert the puzzle
	 * @return puzzle as a string
	 */
	public String toPuzzleString() {
		StringBuffer sb = new StringBuffer();
		for (int row : new Range(9)) {
			for (int col : new Range(9)) {
				sb.append(show[row][col] ? solution[row][col] : " ");
			}
		}
		return sb.toString();
	}
}
