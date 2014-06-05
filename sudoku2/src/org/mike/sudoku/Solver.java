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
	
	/*
	 * first apply the sieve.  The sieve is simple:  look for squares with a single possible value
	 */
	public List<Solution> sieve(Set<Integer>[][] possible) {
		List<Solution> answers = new ArrayList<Solution>();
		for (int row : new Range(9)) {
			for (int col : new Range(9)) {
				// need to possible answers for comb set
				if (possible[row][col] != null && possible[row][col].size() == 1) {
					answers.add(new Solution(row,col,Sets.elem(possible[row][col])));
				}
			}
		}
		return answers;
	}
	
	// a tag for the comb that the try failed
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
			Set<Integer> cmb = new HashSet<Integer>();
			for (Loc l : range) {
				if (l.row != cmbLoc.row && l.col != cmbLoc.col && possible[l.row][l.col]!= null ) {
					cmb.addAll(possible[l.row][l.col]);
				}
			}
			// Try set is possible at this r,c minus all the others
			Set<Integer> trial = Sets.diff(possible[cmbLoc.row][cmbLoc.col], cmb);
			
			// If the trail has one element, this may be a solution.
			// to check:  if this is the first time we've put it in, add as a solution.  If the
			// number is already there, mark as no solution
			if (trial.size() == 1) {
				Integer e = Sets.elem(trial);
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
	 * Next is the comb.  We union the other possible answers, and remove them from the current set.
	 * If there's exactly one left, we have a solution (i.e. this is the only square with this value left
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
			answers.addAll(combRange(possible, Loc.boxRange(new Box(b))));
		}
		
		return answers;
	}
	
	/*
	 * Now the methods to deal with combos.  Combos are squares that have exactly the same possiblities, the same number
	 * of times.  For example, if two squares in a column contain the possiblities of 1,2, the 1 and 2 must be in
	 * those squares.  We can temporarily consider those squares solved, and create new possibilies with them removed
	 * 
	 * The first step is to get all the locations in a range with sets equal to a particular count (2, 3 or 4)
	 */
	
	List<Loc> equivs(Set<Integer>[][] possible, Loc[] range, int count) {
		for (int i : new Range(9)) {
			int r = range[i].row;
			int c = range[i].col;
			// we are comparing the other sets against this one
			Set<Integer> s = possible[r][c];
			
			// if the length of s is equal to count, we have a possible match
			if (s != null && s.size() == count) {
				// clear the array, and add this
				List<Loc> ret = new ArrayList<Loc>();
				ret.add(range[i]);
				
				// Compare this set against all remaining entries
				for (int j : new Range(i+1, 9)) {
					Set<Integer> t = possible[range[j].row][range[j].col];
					// if the set at this point is equal to s, add it's location to ret
					if (t != null && s.equals(t)) {
						ret.add(range[j]);
						// if we have found match.  If we have exactly as
						// many matches as count, it's a combo
						if (ret.size() == count){
							return ret;
						}
					}
				}
			}
			
		}
		// return an empty list if no equivs
		return new ArrayList<Loc>();
	}
	
	// make a copy of the possibilities
	Set<Integer>[][] copyPossible(Set<Integer>[][] possible) {
		Set<Integer>[][] ret = new Set[9][9];
		for (int r : new Range(9)) {
			for (int c : new Range(9)) {
				if (possible[r][c] != null) {
					ret[r][c] = Sets.copy(possible[r][c]);
				}
			}
		}
		return ret;
	}
	
	Set<Integer>[][] comboPossible (Set<Integer>[][] possible, Loc[] range, int count) {
		// see if we have an equivs
		List<Loc> locs = equivs(possible, range, count);
		
		// if we didn't get anything, return null
		if (locs.isEmpty()) {
			return null;
		}
		// Other wise, copy the possible. 		
		Set<Integer>[][] p = copyPossible(possible);
		
		// get the set that's the combo
		Loc setLoc = locs.get(0);
		Set<Integer> cset = p[setLoc.row][setLoc.col];
		
		// set the possibles of this set locations to null (mark it as if it were known)
		for (Loc l : locs) {
			p[l.row][l.col] = null; 
		}
		
		// now go through the rest of the range, and remove the combo set from the possibles
		for (Loc l : range) {
			if (p[l.row][l.col] != null) {
				p[l.row][l.col].removeAll(cset);
			}
		}
		
		// new possibilities with these combos
		return p;
	}
	
	// now start splitting out the combos.  Row Combos, Col Combos and Box Combos
	List<Set<Integer>[][]> rowCombos(Set<Integer>[][] possible, int count) {
		List<Set<Integer>[][]> possibles = new ArrayList();
		for (int c : new Range(9)) {
			Set<Integer>[][] s = comboPossible(possible, Loc.rowRange(c), count);
			if (s != null) {
				possibles.add(s);
			}
		}
		return possibles;
	}
	
	// now start splitting out the combos.  Row Combos, Col Combos and Box Combos
	List<Set<Integer>[][]> colCombos(Set<Integer>[][] possible, int count) {
		List<Set<Integer>[][]> possibles = new ArrayList();
		for (int r : new Range(9)) {
			Set<Integer>[][] s = comboPossible(possible, Loc.colRange(r), count);
			if (s != null) {
				possibles.add(s);
			}
		}
		return possibles;
	}
	
	// now start splitting out the combos.  Row Combos, Col Combos and Box Combos
	List<Set<Integer>[][]> boxCombos(Set<Integer>[][] possible, int count) {
		List<Set<Integer>[][]> possibles = new ArrayList();
		for (int b : new Range(9)) {
			Set<Integer>[][] s = comboPossible(possible, Loc.boxRange(new Box(b)), count);
			if (s != null) {
				possibles.add(s);
			}
		}
		return possibles;
	}
	
	List<Set<Integer>[][]> allCombos(Set<Integer>[][] possible) {
		List<Set<Integer>[][]> ret = new ArrayList();
		// for count 2, 3, 4
		for (int count : new Range(2,5)) {
			ret.addAll(rowCombos(possible, count));
			ret.addAll(colCombos(possible, count));
			ret.addAll(boxCombos(possible, count));
		}
		return ret;
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
		boolean ret = false;
		// build up the answer array.  Start with the sieve
		List<Solution> answers = sieve(possible);
		// add in the comb
		answers.addAll(comb(possible));
		
		// if answers isn't empty, we've made progress
		if (! answers.isEmpty()) {
			fillAnswers(answers);
			return true;
		}
		
		// The easy things didn't work, so let's try combos
		for (Set<Integer>[][] p : allCombos(possible)) {
			boolean b = step(p);
			ret = ret || b;
		}

		// return what progress we made
		return ret;
	}
	
	void fillAnswers(List<Solution> answers)
	{
		for (Solution a : answers) {
			if (!puzzle.isFilled(a.row, a.col)) {
				puzzle.setSquare(a.row, a.col, a.val);
				if (! puzzle.checkPuzzle()) {
					puzzle.clearSquare(a.row, a.col);
				}
			}
			else if (puzzle.getSquare(a.row, a.col) != a.val) {
				throw new DuplicateAnswerException("Duplicate wrong answer");
			}
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
