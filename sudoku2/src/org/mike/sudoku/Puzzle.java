package org.mike.sudoku;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

import org.mike.util.Range;

public class Puzzle {
	int [][] board = new int[9][9];
	
	public void setSquare (int row, int col, int val) {
		board[row][col] = val;
	}
	
	public int getSquare(int row, int col)
	{
		return board[row][col];
	}
	
	public boolean isFilled(int row, int col) {
		return board[row][col] > 0 && board[row][col] < 10;
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
		if (isFilled(row, col)) {
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
					board[row][col] = 0;
				}
			}
		}
		
	}
	
	public String toString() {
		StringBuffer sb = new StringBuffer();
		for (int row = 0; row < 9; row++) {
			for (int col = 0; col < 9; col++) {
				int sq = board[row][col];
				if (sq > 0 && sq < 10) {
					sb.append(Integer.toString(sq));
				}
				else {
					sb.append(" ");
				}
			}
		}
		return sb.toString();
	}
	
	

}
