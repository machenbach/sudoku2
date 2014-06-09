package org.mike.sudoku;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

import org.mike.util.Box;
import org.mike.util.Histo;
import org.mike.util.Loc;
import org.mike.util.Range;

public class Puzzle implements Comparable<Puzzle>{
	
	Integer [][] board = new Integer[9][9];

	public Puzzle() {
		
	}
	
	public Puzzle (Puzzle p) {
		for (int r : new Range(9)) {
			for (int c : new Range(9)) {
				board[r][c] = p.board[r][c];
			}
		}
	}
	
	public void setSquare (int row, int col, int val) {
		board[row][col] = val;
	}
	
	public int getSquare(int row, int col)
	{
		return board[row][col] == null ? 0 : board[row][col];
	}
	
	public void clearSquare(int row, int col) {
		board[row][col] = null;
	}
	
	public boolean isFilled(int row, int col) {
		return board[row][col] != null;
	}
	
	public boolean isSolved()
	{
		for (int row : new Range(9)) {
			for (int col : new Range(9)) {
				if (!isFilled(row,col)) {
					return false;
				}
			}
		}
		return true;
	}
	
	
	public String printSquare(int row, int col)
	{
		if (isFilled(row, col) && board[row][col] >= 0) {
			return(Integer.toString(board[row][col]));
		}
		else {
			return(" ");
		}
	}
	
	public void printBoard()
	{
		String header = "+---+---+---+";
		for (int r : new Range(9)) {
			if (r % 3 == 0) {
				System.out.println(header);
			}
			for (int c : new Range(9)) {
				if (c % 3 == 0) {
					System.out.print("|");
				}
				System.out.print(printSquare(r,c));
			}
			System.out.println("|");
		}
		System.out.println(header);
	}
	
	
	public void readBoard(String boardSetup) throws IOException {
		readBoard(new StringReader(boardSetup));
		
	}
	
	public void readBoard(Reader boardReader) throws IOException {
		for (int row : new Range(9)) {
			for (int col : new Range(9)) {
				int c = boardReader.read();
				if (Character.isDigit(c)) {
					board[row][col] = (c - '0');
				}
				else {
					board[row][col] = null;
				};
			}
		}
		
	}
	
	public String toString() {
		StringBuffer sb = new StringBuffer();
		for (int row = 0; row < 9; row++) {
			for (int col = 0; col < 9; col++) {
				Integer sq = board[row][col];
				if (sq == null) {
					sb.append(" ");
				}
				else {
					sb.append(sq.toString());
				}
			}
		}
		return sb.toString();
	}
	
	public boolean checkRange(Loc[] range) {
		// we are going to histogram the range.
		Histo<Integer> h = new Histo<Integer>();
		for (Loc l : range) {
			if (board[l.row][l.col] != null) {
				h.addElem(board[l.row][l.col]);
			}
		}
		// if any element has more than 1 entry, it's bad
		for (int i : h.values()) {
			if (i > 1) {
				return false;
			}
		}
		return true;
	}
	
	
	public boolean checkPuzzle() {
		// first, check the columns
		for (int c : new Range(9)) {
			if (! checkRange(Loc.colRange(c))) {
				return false;
			}
		}
		for (int r : new Range(9)) {
			if (! checkRange(Loc.rowRange(r))) {
				return false;
			}
		}
		for (int b : new Range(9)) {
			if (! checkRange(Loc.boxRange(new Box(b)))) {
				return false;
			}
		}
		return true;
	}

	// open count returns the number open squares.  It is used to prioritize which puzzle to
	// try next (i.e. pick the one with the fewest open cells first
	int openCount()
	{
		int cnt = 0;
		for (int r : new Range(9)) {
			for (int c : new Range(9)) {
				if (board[r][c] == null) {
					cnt++;
				}
			}
		}
		return cnt;
	}
	@Override
	public int compareTo(Puzzle o) {
		return Integer.compare(openCount(), o.openCount());
	}
	
	

}
