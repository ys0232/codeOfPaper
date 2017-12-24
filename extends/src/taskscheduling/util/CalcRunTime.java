package taskscheduling.util;

import taskscheduling.Processor;

public class CalcRunTime {

	public static double calcRunTime(double beta, int frequency, Processor processor, double runTimeMax) {
		
		double runTime = 0;
		
		runTime = (beta * (processor.fMax * 1.0 / frequency - 1) + 1) * runTimeMax;
		
		return runTime;
	}
}
