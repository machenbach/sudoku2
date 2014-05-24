package org.mike.sudoku;

import java.io.IOException;
import java.io.PrintStream;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.mike.util.Box;
import org.mike.util.Range;
import org.mike.util.Sets;

/*
 * Starting work on a new solver, that I hope is a little simpler, and better.
 * Two phases.  First, subtract the known elements in row, column and box from
 * overall puzzle.  Currently called "choices", this is more complicated than it needs to be
 */
public class Solver {
	PrintStream logger;
	
	
	Puzzle puzzle;
	
	
	Set<Integer>[][] possible = new Set[9][9];
	
	
	public Solver()
	{
		logger = System.out;
		
	}

	public Solver (Puzzle puzzle) {
		this();
		this.puzzle = puzzle;
	}

	public Solver(PrintStream logger) 
	{
		this.logger = logger;
	}
	
	public Solver(Puzzle puzzle, PrintStream logger)
	{
		this(puzzle);
		this.logger = logger;
	}
	
	public Solver (String puzzleStr) throws IOException {
		this();
		puzzle = new Puzzle();
		puzzle.readBoard(puzzleStr);
	}
	
	
	
	/*
	 * Create a set of number 1 to 9. 
	 */
	Set<Integer> initialSet()
	{
		return new HashSet<Integer>(Range.rangeList(1, 10));
	}
	
	/*
	 * return a set of a single element
	 */
	Set<Integer> oneElem(int i) {
		Set<Integer> res = new HashSet<Integer>();
		res.add(i);
		return res;
	}
	
	Set<Integer> phi()
	{
		return new HashSet<Integer>();
	}
	
	/*
	 * first apply the sieve.  For any square (r, c) that isn't solved, set possible[r][c] to the possible values.
	 * This will be all possible values minus the known row, column and box solutions
	 */
	public void sieve() {
		for (int r : new Range(9)) {
			for (int c : new Range(9)) {
				if (! puzzle.isFilled(r, c)) {
					// initialize to all possible values
					Set<Integer> pos = initialSet();
					
					// remove the elements that are known in the row
					for (int prow : new Range(9)) {
						if (puzzle.isFilled(prow, c)) {
							pos.remove(puzzle.getSquare(prow, c));
						}
					}
					
					// remove the elements in the column
					for (int pcol : new Range(9)) {
						if (puzzle.isFilled(r, pcol)) {
							pos.remove(puzzle.getSquare(r, pcol));
						}
					}
					
					// finally, remove all the known values in the box
					Box b = Box.boxAt(r, c);
					for (int prow : b.rows()) {
						for (int pcol : b.cols()) {
							if (puzzle.isFilled(prow, pcol)) {
								pos.remove(puzzle.getSquare(prow, pcol));
							}
						}
					}
					possible[r][c] = pos;
				}
			}
		}
	}
	
	
	
	/*
	 *  Does this square have an answer?  This square has an answer if
	 * 1. it has not been filled in
	 * 2. There is only one choice or
	 * 3. there is only one need
	 */
	public boolean hasAnswer(int row, int col) 
	{
		if (puzzle.isFilled(row, col)) {
			return false;
		}
		// if puzzleChoices elements are null, step has not been run
		if (possible[row][col] == null) {
			return false;
		}
		return possible[row][col].size() == 1;
	}
	
	public boolean isSolved()
	{
		return puzzle.isSolved();
	}
	
	
	int getElem(Set<Integer>[][] set, int row, int col)
	{
		Iterator<Integer> itr = set[row][col].iterator();
		return itr.next().intValue();
	}
	
	/*
	 * getAnswer.  Get the answer to the puzzle, return 0 if it doesn't have one
	 */
	public int getAnswer(int row, int col)
	{
		if (!hasAnswer(row, col)) {
			return 0;
		}
		return Sets.elem(possible[row][col]);
	}
	
	
	/*
	 * Look to see if we made any progress in this step.  This is defined has having found an answer anywhere.
	 */
	public boolean madeProgress()
	{
		for (int row : new Range(9)) {
			for (int col : new Range(9)) {
				if (hasAnswer(row, col)) {
					return true;
				}
			}
		}
		return false;
	}
	
	/*
	 * Solver step. Fill the rows, columns and boxes, then get the choices
	 */
	public void step()
	{
		sieve();
	}
	
	public void fillAnswers()
	{
		for (int row : new Range(9)) {
			for (int col : new Range(9)) {
				if (hasAnswer(row, col)) {
					puzzle.setSquare(row, col, getAnswer(row, col));
				}
			}
		}
	}
	
	int solveTries;
	
	public void solve() throws CantSolveException {
		solveTries = 0;
		while (true) {
			solveTries++;
			step();
			if (!madeProgress()) {
				throw new CantSolveException("Sorry!!!");
			}
			fillAnswers();
			if (puzzle.isSolved()) {
				break;
			}
		}
	}
	
	public int getSolveTries() {
		return solveTries;
	}
	
	public String toString()
	{
		return(puzzle.toString());
	}
	
	
	public void printSolverInfo()
	{
	}
	
	public void printFlat(Set<Integer>[] ary) 
	{
		for (int i : new Range(9)) {
			logger.println(i + ": " + ary[i]);
		}
	}

	public void printArray(Set<Integer>[][] ary)
	{
	}

}
