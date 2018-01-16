package taskscheduling.util;

import java.util.ArrayList;

import taskscheduling.Task;

public class CalcMakeSpan {

	public static double calcMakeSpan(ArrayList<Task> taskList){
		
		double makeSpan = Double.MIN_VALUE;
		
		for(Task task: taskList){
			if(makeSpan < task.timeGap.endTime){
				makeSpan = task.timeGap.endTime;
			}
		}
		
		return makeSpan;
	}
}
