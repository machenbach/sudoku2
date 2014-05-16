package org.mike.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

public class Range implements Iterable<Integer>, Iterator<Integer> {
	int current = 0;
	int stop;
	int incr = 1;

	public Range(int stop) {
		this.stop = stop;
	}
	
	public Range(int start, int stop) {
		this(stop);
		this.current = start;
	}
	
	public Range(int start, int stop, int incr) {
		this(start, stop);
		this.incr = incr;
	}
	
	public boolean hasNext() {
		return current < stop;
	}

	public Integer next() {
		int ret = current;
		current += incr;
		return ret;
	}

	public void remove() {
		// does nothing
	}

	public Iterator<Integer> iterator() {
		return this;
	}
	
	public static ArrayList<Integer> rangeList(int start, int stop, int incr) {
		ArrayList<Integer> list = new ArrayList<Integer>();
		for (int i : new Range(start, stop, incr)) {
			list.add(i);
		}
		return list;
	}
	
	public static ArrayList<Integer> rangeList(int start, int stop) {
		return rangeList(start, stop, 1);
	}
	
	public static ArrayList<Integer> rangeList(int stop) {
		return rangeList(0, stop);
	}
	
	public static ArrayList<Integer> shuffleRange(int start, int stop, int incr) {
		ArrayList<Integer> l = rangeList(start, stop, incr);
		Collections.shuffle(l);
		return l;
	}

	public static ArrayList<Integer> shuffleRange(int start, int stop) {
		return shuffleRange(start, stop, 1);
	}
	
	public static ArrayList<Integer> shuffleRange(int stop) {
		return shuffleRange(0, stop);
	}

}
