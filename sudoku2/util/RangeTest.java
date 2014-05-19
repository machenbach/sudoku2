package org.mike.test.util;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.Test;
import org.mike.util.Range;

public class RangeTest {

	@Test
	public void TestBasic() {
		int start = 0;
		for (int i : new Range(10)) {
			if (i != start) {
				fail("Wrong order");
			}
			start++;
		}
	}
	
	@Test
	public void TestStart() {
		int[] result = {2,3,4,5};
		int start = 0;
		for (int i : new Range(2, 6)) {
			if (i != result[start]) {
				fail("Wrong start");
			}
			start ++;
		}
	}
	
	@Test
	public void TestIncr() {
		int[] result = {2, 4, 6, 8};
		int start = 0;
		for (int i : new Range(2, 10, 2)) {
			if (i != result[start]) {
				fail("Wrong incr");
			}
			start++;
		}
	}
	
	@Test
	public void TestList() {
		ArrayList<Integer> l1 = new ArrayList<>();
		for (int i = 0; i < 10; i++) {
			l1.add(i);
		}
		
		ArrayList<Integer> l2 = Range.rangeList(10);
		
		if (! l1.equals(l2)) {
			fail("Range list bad");
		}
	}

}
