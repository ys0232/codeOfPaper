package taskscheduling.util;

import java.util.ArrayList;
import java.util.HashMap;

import taskscheduling.Processor;
import taskscheduling.Task;

public class UpdateTaskInforAfterChangeScheduling {
	
	public static void updateTaskInfor(int taskId, ArrayList<Task> taskList,
			HashMap<String, Double> taskEdgeHashMap, Processor[] processorsArray){

		Task task = taskList.get(taskId - 1);
		int processorId = task.selectedProcessorId;
		double est = CalcEST.calcEST(processorId, taskId, processorsArray[processorId].availableTime, taskList, taskEdgeHashMap);
		double eft = est + task.computationCost.get(processorId);
		
		task.timeGap.endTime = eft;
		task.timeGap.startTime = eft - task.computationCost.get(processorId);
		task.timeGap.gap = task.timeGap.endTime - task.timeGap.startTime;
		
//		processorsArray[processorId].taskList.add(taskId);
		processorsArray[processorId].availableTime = eft;
	}

	public static void updateTaskInforNew(int taskId, ArrayList<Task> taskList,double beta,
									   HashMap<String, Double> taskEdgeHashMap, Processor[] processorsArray){

		Task task = taskList.get(taskId);
		int processorId = task.selectedProcessorId;
		double est = CalcEST.calcEST(processorId, taskId, processorsArray[processorId].availableTime, taskList, taskEdgeHashMap);
		double excuTime = CalcRunTime.calcRunTime(beta, task.selectedFre,
				processorsArray[processorId], task.computationCost.get(processorId));
		double eft = est + excuTime;

		task.timeGap.endTime = eft;
		task.timeGap.startTime = eft - task.computationCost.get(processorId);
		task.timeGap.gap = task.timeGap.endTime - task.timeGap.startTime;

//		processorsArray[processorId].taskList.add(taskId);
		processorsArray[processorId].availableTime = eft;
	}
}
