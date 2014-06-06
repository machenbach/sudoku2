package org.mike.sudoku;

import java.io.IOException;
import java.util.HashSet;
import java.util.PriorityQueue;
import java.util.Random;

import org.mike.util.Range;

public class Builder {
	// the filled out puzzle
	Integer[][] puzzle;
	// whether or not to show the square
	boolean[][] show = new boolean[9][9];

	// Solution retry.  We try to construct a random puzzle.  If an attempt fails, reset and
	// try again.  Blow up after we hit this limit.  Measurements have shown this is about
	// twice as many tries as we need
	static int MAX_TRIES = 500; 
	int buildTries = 0;
	int solveTries = 0;
	int solverSolveTries = 0;
	
	// what percent (* 100) of squares to show
	static int SHOW_DEFAULT = 35;
	int showRatio;
	
	/**
	 * A new sodoku puzzle.  This will try to create a random puzzle, but if it exceeds the
	 * try limit it will fail
	 * @throws NoSolutionException Could not create a puzzle in maxium try count
	 */
	public Builder() throws NoSolutionException {
		this(SHOW_DEFAULT);
	}
	
	
	public Builder(int showRatio) throws NoSolutionException {
		// save the show ratio
		this.showRatio = showRatio;
		
		
		/*
		 * Build a puzzle. This proceeds as follows:
		 * There are two main routines: fillbox and fixbox
		 * fillbox fills a box with random numbers.  We will use this on the first box,
		 * then try combinations of fixbox for the rest, until we get a valid puzzle.
		 * If we can't get one in MAX_TRIES tries, give up
		 * 
		 */
		// The basics for this puzzle: box 0, 0
		puzzle = new Integer[9][9];
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
				Integer[][] oldPuzzle = puzzle;
				puzzle = new Integer[9][9];
				for (int r : new Range(3)) {
					for (int c : new Range(3)) {
						puzzle[r][c] = oldPuzzle[r][c];
					}
				}
				
				// blow up if we have to
				if (buildTries >= MAX_TRIES) {
					throw new NoSolutionException("Exceded max tries of " + MAX_TRIES, e);
				}
			}
		}
		
		// outer loop that tries a new show map
		while (true) {
			System.out.print("[");
			buildShow();
			try {
				Solver solver = new Solver(toPuzzleString());
				// inner loop that attempts to solve
				solver.solve();
				if (toSolutionString().equals(solver.toString())) {
					// We have a solution, so return
					solverSolveTries = solver.getSolveTries();
					break;
				}
				else {
					throw new CantSolveException("Bad Solution");
				}
			} catch (IOException e) {
				throw new NoSolutionException("Invalid Puzzle!! shouldn't happen!", e);
			}
			catch (CantSolveException e) {
				solveTries++;
				if (solveTries > MAX_TRIES *  2) {
					throw new NoSolutionException("Not a solvable puzzle", e);
				}
				System.out.println(e.getMessage());
				Throwable t = e.getCause();
				if (t != null) {
					System.out.print(" : " + t.getMessage());
				}
				System.out.println("]");
			}
		}
		System.out.println("puzzle]");
	}
	
	
	void buildShow() {
		// we hide each number at the same probability
		int[] numRatio = new int[9];
		for (int i : new Range(9)) {
			numRatio[i] = showRatio;
		}

		Random random = new Random();
		for (int r : new Range(9)) {
			for (int c : new Range(9)) {
				show[r][c] = random.nextInt(100) <= numRatio[puzzle[r][c] -1] ? true : false;
			}
		}
		
	}

	public int getBuildTries() {
		return buildTries;
	}
	
	public int getSolveTries() {
		return solveTries;
	}
	
	public int getSolverSolveTries() {
		return solverSolveTries;
	}


	void fillbox(int rb, int cb) {
		Integer[] boxnums = Range.shuffleRange(1,10).toArray(new Integer[0]);
		int cur = 0;
		for (int r : new Range(3)) {
			for (int c : new Range(3)) {
				puzzle[rc(rb,r)][rc(cb,c)] = boxnums[cur++];
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
			Random r = new Random();
			Integer[] vals = toArray(new Integer[0]);
			return vals[r.nextInt(vals.length)];
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
				if (puzzle[r][c] != null) {
					// if it has, for each column in this box on this row, we remove it as a possible value
					for (int pc : new Range(3)) {
						pelems[pr][pc].remove(puzzle[r][c]);
					}
				}
			}
		}
		
		// same thing, this time for each of the columns
		
		for (int pc : new Range(3)) {
			int c = rc(cb, pc);
			for (int r : new Range(9)) {
				// for each r/c, check if the puzzle has been set
				if (puzzle[r][c] != null) {
					// if it has, for each column in this box on this row, we remove it as a possible value
					for (int pr : new Range(3)) {
						pelems[pr][pc].remove(puzzle[r][c]);
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
			puzzle[elem.getRow()][elem.getColumn()] = val;
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
		return puzzle[row][column];
	}
	
	public String puzzleElement(int row, int column) {
		return show[row][column] ? puzzleInt(row, column).toString() : "  ";
	}

	public String puzzleSolution(int row, int column) {
		return puzzleInt(row, column).toString();
	}
	public Integer[][] toArray() {
		return puzzle;
	}

	/**
	 * convert the solution to a string
	 * @return String solution
	 */
	public String toSolutionString() {
		StringBuffer sb = new StringBuffer();
		for (int row : new Range(9)) {
			for (int col : new Range(9)) {
				sb.append(puzzle[row][col]);
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
				sb.append(show[row][col] ? puzzle[row][col] : " ");
			}
		}
		return sb.toString();
	}
}
