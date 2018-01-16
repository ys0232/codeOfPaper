package taskscheduling.util;

import java.util.ArrayList;

import taskscheduling.Processor;
import taskscheduling.Task;

public class CalcTaskMaxCost {
	
	public static void calcTaskMaxCost(double beta, ArrayList<Task> taskList, Processor[] processorArray){
		
		for(Task task: taskList){
			task.maxCost = CalcRunTime.calcRunTime(beta, processorArray[task.selectedProcessorId].fMax, processorArray[task.selectedProcessorId], task.computationCost.get(task.selectedProcessorId)) * processorArray[task.selectedProcessorId].costMaxUnit;
		}
		
	}

}
