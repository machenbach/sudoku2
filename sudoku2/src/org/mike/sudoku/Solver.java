package org.mike.sudoku;

import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.mike.util.Box;
import org.mike.util.Loc;
import org.mike.util.Range;
import org.mike.util.Sets;
import org.mike.util.Solution;

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
	
	void removeRange(Set<Integer> pos, Loc[] range)
	{
		for (Loc l : range) {
			if (puzzle.isFilled(l.row, l.col)) {
				pos.remove(puzzle.getSquare(l.row, l.col));
			}
		}
	}
	
	/*
	 * Create the base possible array from the puzzle.  This is the start of each
	 * step.  The base possible array is all possible values, minus the known values in this
	 * row, column and box.
	 */
	Set<Integer>[][] basePossibles(Puzzle puzzle) {
		Set<Integer>[][] possible = new Set[9][9];
		
		for (int r : new Range(9)) {
			for (int c : new Range(9)) {
				if (! puzzle.isFilled(r, c)) {
					// initialize to all possible values
					Set<Integer> pos = initialSet();
					
					// remove the elements that are known in the row
					removeRange(pos, Loc.rowRange(r));
					
					// remove the elements in the column
					removeRange(pos, Loc.colRange(c));

					// finally, remove all the known values in the box
					Box b = Box.boxAt(r, c);
					removeRange(pos, Loc.boxRange(b));

					// need to possible answers for comb set
					possible[r][c] = pos;
				}
			}
		}
		return possible;
	}
	
	void removeSieved(Set<Integer>[][] possible, int row, int col)
	{
		// we have an answer, so remove this from all the ranges in the possible array
		// assert possible[row][col].size == 1
		Integer e = Sets.elem(possible[row][col]);
		
		// we know the answer at row, col, so null out possible
		possible[row][col] = null;
		
		// reset the rows
		for (Loc l : Loc.rowRange(row)) {
			if (possible[l.row][l.col] != null) {
				possible[l.row][l.col].remove(e);
			}
		}

		// reset the columns
		for (Loc l : Loc.colRange(col)) {
			if (possible[l.row][l.col] != null) {
				possible[l.row][l.col].remove(e);
			}
		}
		// and the box
		for (Loc l : Loc.boxRange(Box.boxAt(row, col))) {
			if (possible[l.row][l.col] != null) {
				possible[l.row][l.col].remove(e);
			}
		}
	}
	/*
	 * first apply the sieve.  We look for single element possibles, which represent a 
	 * solution.  By removing them, we create a possible array.  Keep doing it until 
	 * nothing new happens
	 */
	public List<Solution> sieve(Set<Integer>[][] possible) {
		List<Solution> answers = new ArrayList<Solution>();
		boolean foundOne = true;
		
		// keep looking for sieved values until we don't find any more
		while (foundOne) {
			foundOne = false;
			for (int row : new Range(9)) {
				for (int col : new Range(9)) {
					// need to possible answers for comb set
					if (possible[row][col] != null && possible[row][col].size() == 1) {
						foundOne = true;
						answers.add(new Solution(row,col,Sets.elem(possible[row][col])));
						removeSieved(possible, row, col);
					}
				}
			}
		}
		return answers;
	}
	/*
	 * Next is the comb.  We union the other possible answers within a range, and remove them from the current set.
	 * If there's exactly one set in the range with one left, we have a solution (i.e. this is the only square with this value left
	 */
	
	// a tag for the combo that the try failed
	static final Solution NoSolution = new Solution(-1, -1, -1);
	
	// Comb the given range, and return all the solutions
	List<Solution> combRange(Set<Integer>[][] possible, Loc[] range)
	{
		// The tries map keeps track of the all the solutions we try
		Map<Integer, Solution> tries = new HashMap<Integer, Solution>();
		// for each element in the range
		for (Loc cmbLoc : range) {
			// if we know the solution (possible is null) continue
			if (possible[cmbLoc.row][cmbLoc.col] == null) {
				continue;
			}
			
			// We are going to remove all the common elements from the possibles at the given location, except for it's own elements
			// After that, if there is exactly one set in the range that this one value, then it's a solution
			Set<Integer> cmb = new HashSet<Integer>(possible[cmbLoc.row][cmbLoc.col]);
			for (Loc l : range) {
				if (!(l.row == cmbLoc.row && l.col == cmbLoc.col) && possible[l.row][l.col]!= null ) {
					cmb.removeAll(possible[l.row][l.col]);
				}
			}
			
			// If the cmb has one element, this may be a solution.
			// to check:  if this is the first time we've put it in, add as a solution.  If the
			// number is already there, mark as no solution
			if (cmb.size() == 1) {
				Integer e = Sets.elem(cmb);
				// if nothing was here, add a trial solution
				if (tries.get(e) == null) {
					tries.put(e, new Solution(cmbLoc.row, cmbLoc.col, e));
				}
				else {
					// if we seen this already, mark as no solution
					tries.put(e, NoSolution);
				}
			}
		}

		
		List<Solution> answers = new ArrayList<Solution>();
		// For this range, we now can see if any of the trials succeeded
		for (Integer i : tries.keySet()) {
			Solution s = tries.get(i);
			if (s != NoSolution) {
				answers.add(s);
			}
		}
		return answers;
	}
	
	
	/*
	 * Comb the different ranges, and get the answers
	 */
	public List<Solution> comb(Set<Integer>[][] possible) {
		List<Solution> answers = new ArrayList<Solution>();
		
		// first, comb all the rows
		for (int r : new Range(9)) {
			answers.addAll(combRange(possible, Loc.rowRange(r)));
		}
		
		// now the columns
		for (int c : new Range(9)) {
			answers.addAll(combRange(possible, Loc.colRange(c)));
		}
		
		// and finally the boxes
		for (int b : new Range(9)) {
			answers.addAll(combRange(possible, Loc.boxRange(b)));
		}
		
		return answers;
	}
	
	/*
	 * Now the methods to deal with combos.  Combos are squares that have exactly the same possiblities, the same number
	 * of times.  For example, if two squares in a column contain the possiblities of 1,2, the 1 and 2 must be in
	 * those squares.  We can temporarily consider those squares solved, and create new possibilies with them removed
	 * 
	 * To do this, we will create a histogram of all the possible sets in the range.  If the value of the histogram (how often
	 * they occur) is the same as the size, we can remove these sets from possibles
	 */
	
	
	// Utility: make a copy of the possibilities
	Set<Integer>[][] copyPossible(Set<Integer>[][] possible) {
		Set<Integer>[][] ret = new Set[9][9];
		for (int r : new Range(9)) {
			for (int c : new Range(9)) {
				if (possible[r][c] != null) {
					ret[r][c] = Sets.copy(possible[r][c]);
				}
				else {
					ret[r][c] = null;
				}
			}
		}
		return ret;
	}
	
	// Create a new possible array for this range.  If there is one, we will use this for more answers
	Set<Integer>[][] tryComboRange (Set<Integer>[][] possible, Loc[] range) {
		
		// For a given range, this will produce a new possibles array, with
		// combos removed.
		
		// Save the locations of each of the sets
		Map<Set<Integer>, List<Loc>> equivs = new HashMap<Set<Integer>, List<Loc>>();
		for (Loc l : range) {
			if (possible[l.row][l.col] != null) {
				List<Loc> locList = equivs.get(possible[l.row][l.col]);
				if (locList == null) {
					locList = new ArrayList<Loc>();
					equivs.put(possible[l.row][l.col], locList);
				}
				locList.add(l);
			}
		}
		
		// we need to see if we found any combos.  Scan the eqivs.  If a set size == loclist size, then
		// we have a combo.  Note that we found one, and remove the set from range in the new possibilities
		
		// create the new possibles.  First, copy the possibles
		Set<Integer>[][] p = copyPossible(possible);
		boolean foundone = false;
		
		for (Set<Integer> s : equivs.keySet()) {
			if (s.size() == equivs.get(s).size()) {
				// note that we found an equiv
				foundone = true;
				// remove the equivs from p
				for (Loc l : range) {
					if (p[l.row][l.col]!= null) {
						p[l.row][l.col].removeAll(s);
					}
				}
				
				// In this possibilites, the locations we just removed are known
				for (Loc l : equivs.get(s)) {
					p[l.row][l.col]= null; 
				}
				
			}
		}
		if (foundone) {
			return p;
		}
		else {
			return null;
		}
		
	}

	// get the solutions by seiving
	List<Solution> sieveComboRange(Set<Integer>[][] possible, Loc[] range) {
		// If we found one, we have a new possibles p.  Try getting answers from sieve and comb
		List<Solution> answers = new ArrayList<Solution>();
		Set<Integer>[][] p = tryComboRange(possible, range);
		if (p != null) {
			answers.addAll(sieve(p));
		}
		return answers;

	}
	// get the solutions by combing.  Note that this will produce bad answers occasionally
	List<Solution> combComboRange(Set<Integer>[][] possible, Loc[] range) {
		// If we found one, we have a new possibles p.  Try getting answers from sieve and comb
		List<Solution> answers = new ArrayList<Solution>();
		Set<Integer>[][] p = tryComboRange(possible, range);
		if (p != null) {
			answers.addAll(comb(p));
		}
		return answers;

	}
	
	List<Solution> sieveAllCombos(Set<Integer>[][] possible) {
		List<Solution> answers = new ArrayList<Solution>();
		// add the rows
		for (int r : new Range(9)) {
			answers.addAll(sieveComboRange(possible, Loc.rowRange(r)));
		}
		// add the cols
		for (int c : new Range(9)) {
			answers.addAll(sieveComboRange(possible, Loc.colRange(c)));
		}
		// add the boxes
		for (int b : new Range(9)) {
			answers.addAll(sieveComboRange(possible, Loc.boxRange(b)));
		}
		
		return answers;
	}
	

	
	List<Solution> combAllCombos(Set<Integer>[][] possible) {
		List<Solution> answers = new ArrayList<Solution>();
		// add the rows
		for (int r : new Range(9)) {
			answers.addAll(combComboRange(possible, Loc.rowRange(r)));
		}
		// add the cols
		for (int c : new Range(9)) {
			answers.addAll(combComboRange(possible, Loc.colRange(c)));
		}
		// add the boxes
		for (int b : new Range(9)) {
			answers.addAll(combComboRange(possible, Loc.boxRange(b)));
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
		// used by the sieve and comb to find solutions
		Set<Integer>[][] possible = basePossibles(puzzle);
		_madeProgress = step(possible);
	}
	
	/*
	 * Internal solver step.  This is designed to be recursive
	 */
	boolean step(Set<Integer>[][] possible) {
		// build up the answer array.  Start with the sieve
		List<Solution> answers = sieve(possible);
		
		// Superstition on my part.  Only try the next phase if the current one did nothing
		if (answers.isEmpty()) {
			answers.addAll(comb(possible));
		}
		
		// now look for sieve combos
		if (answers.isEmpty()) {
			answers.addAll(sieveAllCombos(possible));
		}

		// Here's where it gets dicey.  combCombos produces some good answers, some bad answers.
		// We will get the proposed solution list, and try each answer, with a backtrack if this answer leads to a bad solution
		if (answers.isEmpty()) {
			List<Solution> trials = combAllCombos(possible);
			for (Solution s : trials) {
				// save our current puzzle state
				Puzzle saved = new Puzzle(puzzle);
				// apply the current solution
				try {
					puzzle.setSquare(s.row, s.col, s.val);
					// and now recursively solve
					solve();
					// puzzle is solved!
					return true;
				}
				catch (CantSolveException e) {
					// This doesn't work. Roll back and try the next one
					puzzle = new Puzzle(saved);
				}
			}
		}
		
		// we've tried it all
		if (answers.isEmpty()) {
			return false;
		}
		
		// otherwise, fill in the answers we have, and return true
		fillAnswers(answers);
		return true;
	}
	
	void fillAnswers(List<Solution> answers)
	{
		for (Solution a : answers) {
			if (!puzzle.isFilled(a.row, a.col)) {
				puzzle.setSquare(a.row, a.col, a.val);
				if (! puzzle.checkPuzzle()) {
					// We had a bad answer - fail this try
					throw new DuplicateAnswerException(String.format("Bad Answer at %s, %s. Val = %s",
							a.row, a.col, a.val));
				}
			}
			else if (puzzle.getSquare(a.row, a.col) != a.val) {
				// another bad answer fail this try
				throw new DuplicateAnswerException(String.format("Duplicate wrong answer at %s, %s.  Old val = %s, new val = %s",
						a.row, a.col, puzzle.getSquare(a.row, a.col), a.val));
			}
		}
	}
	
	int solveTries = 0;
	int solveDepth = 0;

	public void solve() throws CantSolveException {
		solveDepth++;
		System.out.println(String.format("Solve Depth: %s", solveDepth));
		while (true) {
			solveTries++;
			try {
				step();
			}
			catch(DuplicateAnswerException e) {
				throw new CantSolveException("Step failed", e);
			}
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
