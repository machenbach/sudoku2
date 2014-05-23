package org.mike.util;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.Set;

public class Sets {
	/**
	 * some set utilities.  Union, diff, intersect that don't affect the original set, and copy to make a copy
	 * of the set
	 */

	/**
	 * make a copy of this set.  Does not do a deep copy
	 * @param set
	 * @return
	 */
	public static <E> Set<E> copy (Set<E> set) {
		try {
			return set.getClass().getDeclaredConstructor(Collection.class).newInstance(set);
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
		
	}
	
	/**
	 * set interesect
	 * @param set1
	 * @param set2
	 * @return intersection of set1 and set2
	 */
	public static <E> Set<E> intersect(Set<E> set1, Set<E> set2) {
			Set<E> res = copy(set1);
			res.retainAll(set2);
			return res;
	}
	
	/**
	 * set Union
	 * @param set1
	 * @param set2
	 * @return union of set1 and set2
	 */
	public static <E> Set<E> union(Set<E> set1, Set<E> set2) {
		Set<E> res = copy(set1);
		res.addAll(set2);
		return res;
}

	/**
	 * Set difference
	 * @param set1
	 * @param set2
	 * @return set1 - set2
	 */
	public static <E> Set<E> diff(Set<E> set1, Set<E> set2) {
		Set<E> res = copy(set1);
		res.removeAll(set2);
		return res;
}


}
