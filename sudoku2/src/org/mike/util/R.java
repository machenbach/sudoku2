package org.mike.util;

import java.util.Random;

public class R {
	static Random instance;
	
	public R() {
		
	}
	
	public static Random random() {
		if (instance == null) {
			synchronized (R.class) {
				if (instance == null) {
					instance = new Random();
				}
			}
		}
		return instance;
	}

}
