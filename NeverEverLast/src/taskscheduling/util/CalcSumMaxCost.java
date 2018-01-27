package taskscheduling.util;

import java.util.ArrayList;

import taskscheduling.Processor;
import taskscheduling.Task;

public class CalcSumMaxCost {
	
	public static int calcSumMaxCost(ArrayList<Task> taskList){
		
		int sumMaxCost = 0;
		
		for(Task task: taskList){
			sumMaxCost += task.maxCost;
		}
		
		return sumMaxCost;
	}

}
