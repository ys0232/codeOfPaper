package taskscheduling.util;

import java.util.ArrayList;

public class CalcArrayListSum {
	
	public static double calcArrayListSum(ArrayList<Double> arrayList){
		double sum = 0.0;
		for(Double i: arrayList){
			sum += i;
		}
		return sum;
	}

}
