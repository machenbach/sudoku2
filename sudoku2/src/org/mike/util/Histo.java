package org.mike.util;

import java.util.HashMap;

public class Histo<E> extends HashMap<E, Integer> {
	public Histo() {
		super();
	}
	
	public void addElem(E elem) {
		if (elem == null) {
			return;
		}
		
		Integer n = get(elem);
		if (n == null) {
			n = 0;
		}
		put(elem, n+1);
	}

}
