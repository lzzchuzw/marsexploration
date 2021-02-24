package com.exploration.utils.math;

import java.util.Random;

public class MathUtils {
	
	public static void main(String[] args) {
		generateRandomNumBetweenBounday(693,776);
	}
	public static int generateRandomNumBetweenBounday(int min,int max) {
		Random random = new Random();
		int ret = random.nextInt(max-min+1)+min;
		System.out.println("min = "+min+"---max = "+max+"--ret = "+ret);
		return ret;
	}

}
