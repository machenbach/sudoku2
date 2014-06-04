package org.mike.sudoku;

import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
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
				}
			}
		}
		return possible;
	}
	
	/*
	 * first apply the sieve.  The sieve is simple:  look for squares with a single possible value
	 */
	public Set<Solution> sieve(Set<Integer>[][] possible) {
		Set<Solution> answers = new HashSet<Solution>();
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
	
	/*
	 * Next is the comb.  We union the other possible answers, and remove them from the current set.
	 * If there's exactly one left, we have a solution (i.e. this is the only square with this value left
	 */
	public Set<Solution> comb(Set<Integer>[][] possible) {
		Set<Solution> answers = new HashSet<Solution>();
		for (int r : new Range(9)) {
			for (int c : new Range(9)) {
				if (possible[r][c] != null) {
					
					// This checks to see if a column has an answer
					Set<Integer> cmb = Sets.copy(possible[r][c]);
					for (int prow : new Range(9)) {
						if (prow != r && possible[prow][c] != null) {
							cmb.removeAll(possible[prow][c]);
						}
					}
					// if this is the only answer for a column, we're done
					if (cmb.size() == 1) {
						answers.add(new Solution(r,c,Sets.elem(cmb)));
					}
					
					// try the row
					cmb = Sets.copy(possible[r][c]);
					for (int pcol : new Range(9)) {
						if (pcol != c && possible[r][pcol] != null) {
							cmb.removeAll(possible[r][pcol]);
						}
					}
					if (cmb.size() == 1) {
						answers.add(new Solution(r,c,Sets.elem(cmb)));
					}
					
					// finally, finally, see if there's anything in the box
					cmb = Sets.copy(possible[r][c]);
					Box b = Box.boxAt(r, c);
					for (int prow : b.rows()) {
						for (int pcol : b.cols()) {
							if (prow != r && pcol != c && possible[prow][pcol] != null) {
								cmb.removeAll(possible[prow][pcol]);
							}
						}
					}
					if (cmb.size() == 1) {
						answers.add(new Solution(r,c,Sets.elem(cmb)));
					}
				}
			}
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
		Set<Solution> answers = sieve(possible);
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
	
	void fillAnswers(Set<Solution> answers)
	{
		for (Solution a : answers) {
			if (!puzzle.isFilled(a.row, a.col)) {
				puzzle.setSquare(a.row, a.col, a.val);
				if (! puzzle.checkPuzzle()) {
					puzzle.clearSquare(a.row, a.col);
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
