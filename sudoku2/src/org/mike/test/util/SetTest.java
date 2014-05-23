package org.mike.test.util;

import static org.junit.Assert.assertTrue;

import java.util.HashSet;
import java.util.Set;

import org.junit.Test;
import org.mike.util.Sets;

public class SetTest {
	
	Set<Integer> intset (int[] a)
	{
		Set<Integer> s = new HashSet<Integer>();
		for (int i : a) {
			s.add(i);
		}
		return s;
	}


	@Test
	public void testIntersect()
	{
		int[] s1 = {1, 2, 3};
		int[] s2 = {4, 5, 6};
		int[] s3 = {3, 4, 5};
		int[] s4 = {3};
		
		Set<Integer> set1 = new HashSet<Integer>();
		Set<Integer> set2 = new HashSet<Integer>();
		Set<Integer> set3 = new HashSet<Integer>();
		Set<Integer> set4 = new HashSet<Integer>();
		set1 = intset(s1);
		set2 = intset(s2);
		set3 = intset(s3);
		set4 = intset(s4);
		
		assertTrue(Sets.intersect(set1, set2).isEmpty());
		assertTrue(Sets.intersect(set2, set1).isEmpty());
		assertTrue(Sets.intersect(set1, set3).equals(set4));
		assertTrue(Sets.intersect(set3, set1).equals(set4));
		assertTrue(Sets.intersect(set1, set1).equals(set1));
		assertTrue(Sets.intersect(set2, Sets.intersect(set1, set3)).isEmpty());
		
		
		
	}
	

}
