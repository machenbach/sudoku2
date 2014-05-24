package org.mike.sudoku;

import java.io.IOException;
import java.io.PrintStream;
import java.util.HashSet;
import java.util.Set;

import org.mike.util.Box;
import org.mike.util.Range;
import org.mike.util.Sets;

/*
 * Starting work on a new solver, that I hope is a little simpler, and better.
 * Several phases.  First, subtract the known elements in row, column and box from
 * overall puzzle (the sieve)
 * Next find out if only one square has an answer remaining (the comb)
 */
public class Solver {
	PrintStream logger;
	
	// the puzzle we're working on
	Puzzle puzzle;
	
	// used by the sieve and comb to find solutions
	Set<Integer>[][] possible = new Set[9][9];
	
	// reset at the start of each step.  Shows whether or not we made progress in this step
	boolean _madeProgress;
	
	
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
	
	class Answer {
		public int row;
		public int col;
		public int val;
		
		public Answer(int r, int c, int v) {
			row = r;
			col = c;
			val = v;
		}
	}
	
	/*
	 * first apply the sieve.  For any square (r, c) that isn't solved, set possible[r][c] to the possible values.
	 * This will be all possible values minus the known row, column and box solutions
	 */
	public Set<Answer> sieve() {
		Set<Answer> answers = new HashSet<Answer>();
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
					// need to possible answers for comb set
					possible[r][c] = pos;
					if (pos.size() == 1) {
						answers.add(new Answer(r,c,Sets.elem(pos)));
					}
				}
			}
		}
		return answers;
	}
	/*
	 * Next is the comb.  We union the other possible answers, and remove them from the current set.
	 * If there's exactly one, we have a solution (i.e. this is the only sqaure with this value left
	 */
	public Set<Answer> comb() {
		Set<Answer> answers = new HashSet<Answer>();
		for (int r : new Range(9)) {
			for (int c : new Range(9)) {
				if (possible[r][c] != null) {
					// initialize to all possible values
					Set<Integer> cmb = new HashSet<Integer>();
					
					// union all possible row elements (except this row)
					for (int prow : new Range(9)) {
						if (prow != r && possible[prow][c] != null) {
							cmb.addAll(possible[prow][c]);
						}
					}
					
					// union all possible column elements, except this col
					for (int pcol : new Range(9)) {
						if (pcol != c && possible[r][pcol] != null) {
							cmb.addAll(possible[r][pcol]);
						}
					}
					
					// finally, union everything in the box, except this square
					Box b = Box.boxAt(r, c);
					for (int prow : b.rows()) {
						for (int pcol : b.cols()) {
							if (prow != r && pcol != c && possible[prow][pcol] != null) {
								cmb.addAll(possible[prow][pcol]);
							}
						}
					}
					Set<Integer> rem = Sets.diff(possible[r][c], cmb);
					if (rem.size() == 1) {
						answers.add(new Answer(r,c,Sets.elem(rem)));
					}
				}
			}
		}
		return answers;
	}
	
	
	
	public boolean isSolved()
	{
		return puzzle.isSolved();
	}
	
	
	
	/*
	 * Look to see if we made any progress in this step.  This is defined has having found an answer anywhere.
	 */
	public boolean madeProgress()
	{
		return _madeProgress;
	}
	
	/*
	 * Solver step. Fill the rows, columns and boxes, then get the choices
	 */
	public void step()
	{
		_madeProgress = false;
		// build up the answer array.  Start with the sieve
		Set<Answer> answers = sieve();
		// add in the comb
		answers.addAll(comb());
		
		
		// if answers isn't empty, we've made progress
		if (! answers.isEmpty()) {
			_madeProgress = true;
			fillAnswers(answers);
		}
	}
	
	void fillAnswers(Set<Answer> answers)
	{
		for (Answer a : answers) {
			puzzle.setSquare(a.row, a.col, a.val);
		}
	}
	
	int solveTries;
	
	public void solve() throws CantSolveException {
		solveTries = 0;
		while (true) {
			solveTries++;
			step();
			if (puzzle.isSolved()) {
				break;
			}
			if (!madeProgress()) {
				throw new CantSolveException("Sorry!!!");
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
