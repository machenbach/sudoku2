package org.mike.sudoku;

import java.io.IOException;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class Solver {
	PrintStream logger;
	
	
	Puzzle puzzle;
	
	int[] oneToNine = {0, 1, 2, 3, 4, 5, 6, 7, 8};
	int[] oneToThree = {0, 1, 2};
	
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
	 * There are nine elements in each set.  This is the number needed in
	 */

	@SuppressWarnings("unchecked")
	Set<Integer>[] colChoices = new Set [9];
	
	@SuppressWarnings("unchecked")
	Set<Integer>[] rowChoices = new Set[9];
	
	@SuppressWarnings("unchecked")
	Set<Integer>[][] boxChoices = new Set[3][3];
	
	@SuppressWarnings("unchecked")
	Set<Integer>[][] puzzleChoices = new Set[9][9]; 
	
	@SuppressWarnings("unchecked")
	Set<Integer>[][] colNeeds = new Set[9][9]; 
	
	@SuppressWarnings("unchecked")
	Set<Integer>[][] rowNeeds = new Set[9][9]; 
	
	@SuppressWarnings("unchecked")
	Set<Integer>[][] boxNeeds = new Set[9][9]; 
	
	@SuppressWarnings("unchecked")
	Set<Integer>[][] rowEquivalents = new Set[9][9]; 
	
	@SuppressWarnings("unchecked")
	Set<Integer>[][] colEquivalents = new Set[9][9]; 
	
	@SuppressWarnings("unchecked")
	Set<Integer>[][] boxEquivalents = new Set[9][9]; 

	
	/*
	 * An inner class to convert 2d box numbers to linear numbers and back
	 */
	class LinearBoxNo {
		// row and column of the box.  0, 1 or 2
		int row;
		int col;
		
		public LinearBoxNo(int row, int col) {
			this.row = row;
			this.col = col;
		}
		
		public LinearBoxNo (int boxNo) {
			this (boxNo / 3, boxNo % 3);
		}
		
		//convert the row and column of the square (r and c) into a linear box number
		public int box (int r, int c)
		{
			return 3 * (r - row) + (c - col);
		}
		
		// convert the linear box to a row
		public int row (int lb)
		{
			return 3 * row + lb/3;
		}
		
		//convert the linear box to a column
		public int col (int lb)
		{
			return 3 * col + lb % 3;
		}
		
		public int boxRow()
		{
			return row;
		}
		
		public int boxCol()
		{
			return col;
		}
	}
	
	/*
	 * Create a set of number 1 to 9. oneToNine really goes 0 to 8
	 */
	Set<Integer> initialSet()
	{
		Set<Integer> s = new HashSet<Integer>();
		for (int i : oneToNine) {
			s.add(i+1);
		}
		return s;
	}
	
	/*
	 * return a new set that is the intersect of the two sets
	 */
	public <E> Set<E> intersect(Set<E> set1, Set<E> set2) {
		Set<E> res = new HashSet<E>();
		for (E e1 : set1)  {
			if (set2.contains(e1)) {
				res.add(e1);
			}
		}
		return (res);
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
	 * set the row needs
	 */
	public void setRows()
	{
		for (int row : oneToNine) {
			rowChoices[row] = initialSet();
			for (int col : oneToNine) {
				rowChoices[row].remove(puzzle.getSquare(row, col));
			}
		}
	}
	
	/*
	 * set the column needs
	 */
	public void setCols()
	{
		for (int col : oneToNine) {
			colChoices[col] = initialSet();
			for (int row : oneToNine) {
				colChoices[col].remove(puzzle.getSquare(row, col));
			}
		}
	}
	
	/*
	 * set the needs in the boxes
	 */
	public void setBoxes()
	{
		for (int box : oneToNine) {
			Set<Integer> res = initialSet();
			LinearBoxNo lb = new LinearBoxNo(box);
			for (int b : oneToNine) {
				res.remove(puzzle.getSquare(lb.row(b), lb.col(b)));
			}
			boxChoices[lb.boxRow()][lb.boxCol()] = res;
			
		}
	}
	
	
	/*
	 * Now find all the choices we have in the puzzle
	 */
	
	public void fillChoices()
	{
		for (int col : oneToNine) {
			for (int row : oneToNine) {
				int boxCol = col / 3;
				int boxRow = row / 3;
				if (puzzle.isFilled(row, col)) {
					puzzleChoices[row][col] = oneElem(puzzle.getSquare(row, col));
				}
				else {
					puzzleChoices[row][col] = intersect(colChoices[col], intersect(rowChoices[row], boxChoices[boxRow][boxCol]));
				}
			}
		}
	}
	
	/*
	 * This finds the squares that have a unique entry.  Must be called after fillChoices
	 */
	public void setNeeds()
	{
		// remove row dups
		for (int row : oneToNine) {
			for (int col : oneToNine) {
				
				// remove anything in our row
				rowNeeds[row][col] = new HashSet<Integer>(puzzleChoices[row][col]);
				for (int r : oneToNine) {
					if (r != row) {
						rowNeeds[row][col].removeAll(puzzleChoices[r][col]);
					}
				}
				// remove anything in our column
				colNeeds[row][col] = new HashSet<Integer>(puzzleChoices[row][col]);
				for (int c : oneToNine) {
					if (c != col) {
						colNeeds[row][col].removeAll(puzzleChoices[row][c]);
					}
				}
				// remove anything in our box
				boxNeeds[row][col] = new HashSet<Integer>(puzzleChoices[row][col]);
				LinearBoxNo lb = new LinearBoxNo(row/3, col/3);
				for (int b : oneToNine) {
					if (row != lb.row(b) | col != lb.col(b)) {
						boxNeeds[row][col].removeAll(puzzleChoices[lb.row(b)][lb.col(b)]);
					}
				}
			}
		}
	}
	
	/*
	 * This section deals with equivalent squares.  Equivalent squares are in a box that have the same set of choices.
	 * the number of boxes is equal to the length of the set, we can remove the equivalences.  For example, suppose 
	 * that four boxes have the choice set {6, 7, 8, 9}.  This is an equivalency, and this set can be removed from
	 * the other choices.
	 */
	
	public void findRowEquivalence()
	{
		for (int row : oneToNine) {
			// This is the map we use to count the equal sets
			Map<Set<Integer>, Integer> equalSets = new HashMap<Set<Integer>, Integer>();
			for (int col : oneToNine) {
				// initialize the equivalency sets, and start counting possible equivalent sets
				Set<Integer> s = new HashSet<Integer>(puzzleChoices[row][col]);
				rowEquivalents[row][col] = new HashSet<Integer>(s);
				if (! equalSets.containsKey(s)) {
					equalSets.put(s, 0);
				}
				equalSets.put(s, equalSets.get(s) + 1);
			}
			
			// Now go through the equivalency map.  If the set legnth is the same as the square count,
			// we can remove that set from the others
			for (Set<Integer> s : equalSets.keySet()) {
				if (s.size() == equalSets.get(s)) {
					// this is an equivalent set.  Remove all from all the sets
					for (int col : oneToNine) {
						rowEquivalents[row][col].removeAll(s);
					}
				}
			}
			
		}
	}
	
	public void findColEquivalence()
	{
		for (int col : oneToNine) {
			// This is the map we use to count the equal sets
			Map<Set<Integer>, Integer> equalSets = new HashMap<Set<Integer>, Integer>();
			for (int row : oneToNine) {
				// initialize the equivalency sets, and start counting possible equivalent sets
				Set<Integer> s = new HashSet<Integer>(puzzleChoices[row][col]);
				colEquivalents[row][col] = new HashSet<Integer>(s);
				if (! equalSets.containsKey(s)) {
					equalSets.put(s, 0);
				}
				equalSets.put(s, equalSets.get(s) + 1);
			}
			
			// Now go through the equivalency map.  If the set legnth is the same as the square count,
			// we can remove that set from the others
			for (Set<Integer> s : equalSets.keySet()) {
				if (s.size() == equalSets.get(s)) {
					// this is an equivalent set.  Remove all from all the sets
					for (int row : oneToNine) {
						colEquivalents[row][col].removeAll(s);
					}
				}
			}
			
		}
	}
	
	
	
	
	public void findBoxEquivalence()
	{
		for (int boxNo : oneToNine) {
			// get our linear box mapping
			LinearBoxNo lb = new LinearBoxNo(boxNo);
			
			// This is the map we use to count the equal sets
			Map<Set<Integer>, Integer> equalSets = new HashMap<Set<Integer>, Integer>();
			for (int i : oneToNine) {
				// initialize the equivalency sets, and start counting possible equivalent sets
				Set<Integer> s = new HashSet<Integer>(puzzleChoices[lb.row(i)][lb.col(i)]);
				boxEquivalents[lb.row(i)][lb.col(i)] = new HashSet<Integer>(s);
				if (! equalSets.containsKey(s)) {
					equalSets.put(s, 0);
				}
				equalSets.put(s, equalSets.get(s) + 1);
			}
			
			// Now go through the equivalency map.  If the set legnth is the same as the square count,
			// we can remove that set from the others
			for (Set<Integer> s : equalSets.keySet()) {
				if (s.size() == equalSets.get(s)) {
					// this is an equivalent set.  Remove all from all the sets
					for (int i : oneToNine) {
						boxEquivalents[lb.row(i)][lb.col(i)].removeAll(s);
					}
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
		if (puzzleChoices[row][col] == null) {
			return false;
		}
		return (puzzleChoices[row][col].size() == 1) ||
				(boxNeeds[row][col].size() == 1) ||
				(colNeeds[row][col].size() == 1) ||
				(rowNeeds[row][col].size() == 1) ||
				(rowEquivalents[row][col].size() == 1) ||
				(colEquivalents[row][col].size() == 1) ||
				(boxEquivalents[row][col].size() == 1);
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
		if (puzzleChoices[row][col].size() == 1) {
			return getElem(puzzleChoices, row, col);
		}
		if (rowNeeds[row][col].size() == 1) {
			return getElem(rowNeeds, row, col);
		}
		if (colNeeds[row][col].size() == 1) {
			return getElem(colNeeds, row, col);
		}
		if (boxNeeds[row][col].size() == 1) {
			return getElem(boxNeeds, row, col);
		}
		if (rowEquivalents[row][col].size() == 1) {
			return getElem(rowEquivalents, row, col);
		}
		if (colEquivalents[row][col].size() == 1) {
			return getElem(colEquivalents, row, col);
		}
		if (boxEquivalents[row][col].size() == 1) {
			return getElem(boxEquivalents, row, col);
		}
		return 0;
	}
	
	
	/*
	 * Look to see if we made any progress in this step.  This is defined has having found an answer anywhere.
	 */
	public boolean madeProgress()
	{
		for (int row : oneToNine) {
			for (int col : oneToNine) {
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
		setRows();
		setCols();
		setBoxes();
		fillChoices();
		setNeeds();
		findRowEquivalence();
		findColEquivalence();
		findBoxEquivalence();
	}
	
	public void fillAnswers()
	{
		for (int row : oneToNine) {
			for (int col : oneToNine) {
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
	
	public void printChoices()
	{
		logger.println("Row ---");
		printFlat(rowChoices);
		logger.println("Col ---");
		printFlat(colChoices);
		logger.println("Box ---");
		for (int r : oneToThree) {
			for (int c : oneToThree) {
				logger.println(r +", " + c + ": " + boxChoices[r][c]);
			}
		}
		logger.println("Choices ---");
		printArray(puzzleChoices);
	}
	
	public void printNeeds() {
		logger.println("Row needs ---");
		printArray(rowNeeds);
		
		logger.println("Col needs ---");
		printArray(colNeeds);
		
		logger.println("Box needs ---");
		printArray(boxNeeds);
	}

	public void printEquivalents()
	{
		logger.println("Row equivalents ---");
		printArray(rowEquivalents);
		
		logger.println("Col equivalents ---");
		printArray(colEquivalents);
		
		logger.println("Box equivalents ---");
		printArray(boxEquivalents);
	}
	
	public void printSolverInfo()
	{
		printChoices();
		printNeeds();
		printEquivalents();
	}
	
	public void printFlat(Set<Integer>[] ary) 
	{
		for (int i : oneToNine) {
			logger.println(i + ": " + ary[i]);
		}
	}

	public void printArray(Set<Integer>[][] ary)
	{
		logger.println("  ... by Row");
		for (int row : oneToNine) {
			for (int col : oneToNine) {
				logger.print(row + ", " + (col + ": "));
				logger.print(ary[row][col].toString());
				if (puzzle.isFilled(row, col)) {
					logger.print(" <= filled");
				}
				logger.println();
			}
		}

		logger.println("  ... by Col");

		for (int col : oneToNine) {
			for (int row : oneToNine) {
				logger.print(row + ", " + (col + ": "));
				logger.print(ary[row][col].toString());
				if (puzzle.isFilled(row, col)) {
					logger.print(" <= filled");
				}
				logger.println();
			}
		}
		
		logger.println("  ... by Box");
		
		for (int boxRow : oneToThree) {
			for (int boxCol : oneToThree) {
				LinearBoxNo lb = new LinearBoxNo(boxRow, boxCol);
				for (int b : oneToNine) {
					logger.print(lb.row(b) + ", " + (lb.col(b)) + ": ");
					logger.print(ary[lb.row(b)][lb.col(b)].toString());
					if (puzzle.isFilled(lb.row(b), lb.col(b))) {
						logger.print(" <== filled");
					}
					logger.println();
				}
				logger.println();
			}
		}
	}

}
