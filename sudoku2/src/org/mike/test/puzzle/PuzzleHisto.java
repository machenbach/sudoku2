package org.mike.test.puzzle;

import org.junit.Test;
import org.mike.sudoku.Builder;
import org.mike.sudoku.NoSolutionException;
import org.mike.sudoku.Puzzle;
import org.mike.util.Range;

public class PuzzleHisto {
	static int BUCKET_SIZE = 5;
	static int NUM_SAMPLES = 100;

	String histoBar(int val, int max, String stars) {
		String s = stars.substring(0, (val * 100)/max);
		if (val != 0 && s.length() == 0) {
			s = "*";
		}
		return s;
	}
	
	
	void printHisto(int[] histo, int max, int bucketSize) {
		// find max
		int maxVal = 0;
		int range = 100;
		
		for (int i : new Range(max/bucketSize)) {
			if (histo[i] > maxVal) {
				maxVal = histo[i];
			}
		}
		
		String stars = new String(new char[range]).replace('\0', '*');

		for (int i : new Range(max/bucketSize)) {
			System.out.printf("%4d: %s\n", i * bucketSize,histoBar(histo[i], maxVal, stars));
		}
		
	}
	@Test
	public void test() {
		int trymin = Integer.MAX_VALUE;
		int trymax = Integer.MIN_VALUE;
		int trytotal = 0;
		int tryerrors = 0;

		// Size of histogram is max number of retries / bucket size
		int[] tryhisto = new int[1000 / BUCKET_SIZE];

		int solvemin = Integer.MAX_VALUE;
		int solvemax = Integer.MIN_VALUE;
		int solvetotal = 0;
		int solveerrors = 0;

		// Size of histogram is max number of retries / bucket size
		int[] solvehisto = new int[1000];

		String solvemsg = "Not a solvable puzzle";
		for (int i : new Range(NUM_SAMPLES)) {
			try {
				Builder p = new Builder(25);
				trytotal += p.getBuildTries();
				if (trymax < p.getBuildTries()) {
					trymax = p.getBuildTries();
				}
				if (trymin > p.getBuildTries()) {
					trymin = p.getBuildTries();
				}
				tryhisto[p.getBuildTries()/BUCKET_SIZE]++;
	
				int solveTry = p.getSolverSolveTries();
				solvetotal += solveTry;
				if (solvemax < solveTry) {
					solvemax = solveTry;
				}
				if (solvemin > solveTry) {
					solvemin = solveTry;
				}
				solvehisto[solveTry]++;
				solvetotal += solveTry;
			} catch (NoSolutionException e) {
				if (solvemsg.equals(e.getMessage())) {
					solveerrors++;
				}
				else {
					tryerrors++;
				}
			}
		}
		System.out.println("Errors: " + tryerrors);
		System.out.println("Min: " + trymin);
		System.out.println("Max: " + trymax);
		System.out.println("Ave: " + trytotal/NUM_SAMPLES);
		printHisto(tryhisto, trymax, BUCKET_SIZE);
		
		System.out.println("Errors: " + solveerrors);
		System.out.println("Min: " + solvemin);
		System.out.println("Max: " + solvemax);
		System.out.println("Ave: " + solvetotal/NUM_SAMPLES);
		printHisto(solvehisto, solvemax, 1);
	}

}