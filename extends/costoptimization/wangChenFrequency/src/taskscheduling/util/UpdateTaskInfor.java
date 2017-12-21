package taskscheduling.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import taskscheduling.Processor;
import taskscheduling.Task;
import taskscheduling.TimeGap;

public class UpdateTaskInfor {
	
	public static double updateTaskInfor(double maxTime, int ithToBeChangedTask, double sumCost, 
			List<Map.Entry<Integer, Double>> taskDisCostList,
			ArrayList<Task> taskList, ArrayList<Integer> taskOrderList,
			HashMap<String, Double> taskEdgeHashMap, Processor[] processorsArray){
		
		//保存当前状态用以回滚
		Task[] oldTaskInfor = new Task[taskList.size()];
		for(int i = 0; i < taskList.size(); ++ i){
			oldTaskInfor[i] = new Task();
			oldTaskInfor[i].selectedProcessorId = taskList.get(i).selectedProcessorId;
			oldTaskInfor[i].selectedFre=taskList.get(i).selectedFre;
			oldTaskInfor[i].timeGap = new TimeGap();
			oldTaskInfor[i].timeGap.startTime = taskList.get(i).timeGap.startTime;
			oldTaskInfor[i].timeGap.endTime = taskList.get(i).timeGap.endTime;
			oldTaskInfor[i].timeGap.gap = taskList.get(i).timeGap.gap;
		}
		
//		Processor[] oldProcessorInfor = new Processor[processorsArray.length];
//		for(int i = 0; i < processorsArray.length; ++ i){
//			oldProcessorInfor[i] = new Processor();
//			oldProcessorInfor[i].availableTime = processorsArray[i].availableTime;
//		}
		
		int taskId = taskDisCostList.get(ithToBeChangedTask).getKey();
		Task task = taskList.get(taskId - 1);
//		double taskStartTime = task.timeGap.startTime;
		
		/**更改部分**/
//		for(Task task1 :taskList){
//			task1.timeGap = new TimeGap();
//		}
		
		double disCost = taskDisCostList.get(ithToBeChangedTask).getValue(); 
		//调换任务之后代价会减少，减少的量就是更换前后处理器的代价差
		sumCost -= disCost;
		int updateIndex = 0;
		
		for(Processor processor: processorsArray){
			processor.availableTime = Double.MIN_VALUE;
		}
		
//		for(Integer taskIndex: taskOrderList){
//			if(taskIndex == taskId){
//				break;
//			}
//			Task tempTask = taskList.get(taskIndex - 1);
//			int processorId = tempTask.selectedProcessorId;
//			double tempAvailableTime = tempTask.timeGap.endTime;
//			processorsArray[processorId].availableTime = Math.max(tempAvailableTime, processorsArray[processorId].availableTime);
//		}
			
		//跳过要调换的任务之前的任务
		for(; updateIndex < taskList.size(); ++ updateIndex){
			if(taskOrderList.get(updateIndex) == taskId){
				break;
			}
			Task tempTask = taskList.get(taskOrderList.get(updateIndex) - 1);
			int processorId = tempTask.selectedProcessorId;
			double tempAvailableTime = tempTask.timeGap.endTime;
			processorsArray[processorId].availableTime = Math.max(tempAvailableTime, processorsArray[processorId].availableTime);
			/**更改处**/
//			TaskScheduling.taskScheduling(taskOrderList.get(updateIndex), taskList, taskEdgeHashMap, processorsArray);
		
//			System.out.println("Before task:" + "\t"
//			                   + taskOrderList.get(updateIndex)+ "\t"
//			                   + taskList.get(taskOrderList.get(updateIndex)-1).selectedProcessorId + "\t"
//			                   + taskList.get(taskOrderList.get(updateIndex)-1).timeGap.startTime + "\t"
//			                   + taskList.get(taskOrderList.get(updateIndex)-1).timeGap.endTime + "\t"
//			                   + taskList.get(taskOrderList.get(updateIndex)-1).timeGap.gap + "\t"
//			                   +  + CalcMakeSpan.calcMakeSpan(taskList) 
//					);
		}
		

		//更新当前要调换处理器的任务
		
		//寻找父节点任务的最晚结束时间
		ArrayList<Integer> predTaskArrayList = taskList.get(taskId - 1).predecessorTaskList;
		//父节点的最晚结束时间
		double timeThreshold = -1;
		for(Integer predTaskId: predTaskArrayList){
			double temp = 0;
			Task predTask = taskList.get(predTaskId - 1);
			if(predTask.selectedProcessorId == task.minCostProcessorId){
				temp = predTask.timeGap.endTime;
			}else{
				temp = predTask.timeGap.endTime + taskEdgeHashMap.get(predTaskId + "_" + taskId);
			}
			if(timeThreshold < temp){
				timeThreshold = temp;
			}
		}
		
		task.timeGap.startTime = Math.max(timeThreshold, processorsArray[task.minCostProcessorId].availableTime);
		task.selectedProcessorId = task.minCostProcessorId;
		task.timeGap.endTime = task.timeGap.startTime + task.minCostExcuteTime;
		task.timeGap.gap = task.minCostExcuteTime;
		processorsArray[task.minCostProcessorId].availableTime = task.timeGap.endTime;
		
		++ updateIndex;
		//保持任务的处理器不变，更新时间
		for(; updateIndex < taskList.size(); ++ updateIndex){
			UpdateTaskInforAfterChangeScheduling.updateTaskInfor(taskOrderList.get(updateIndex), taskList, taskEdgeHashMap, processorsArray);
			
//			System.out.println("after task:" + "\t"
//	                   + taskOrderList.get(updateIndex)+ "\t"
//	                   + taskList.get(taskOrderList.get(updateIndex)-1).selectedProcessorId + "\t"
//	                   + taskList.get(taskOrderList.get(updateIndex)-1).timeGap.startTime + "\t"
//	                   + taskList.get(taskOrderList.get(updateIndex)-1).timeGap.endTime + "\t"
//	                   + taskList.get(taskOrderList.get(updateIndex)-1).timeGap.gap + "\t"
//	                   + CalcMakeSpan.calcMakeSpan(taskList) 
//			);
		}
		
		//System.out.println(CalcMakeSpan.calcMakeSpan(taskList));
		
		//若是转换之后的makespan超过规定时间则回滚
//		double newMakeSpan = CalcMakeSpan.calcMakeSpan(taskList);
//		System.out.println("newmakespan: " + newMakeSpan);
		
		if(maxTime < CalcMakeSpan.calcMakeSpan(taskList)){
			for(int i = 0; i < taskList.size(); ++ i){
				taskList.get(i).selectedProcessorId = oldTaskInfor[i].selectedProcessorId;
				taskList.get(i).selectedFre=oldTaskInfor[i].selectedFre;
				taskList.get(i).timeGap.startTime = oldTaskInfor[i].timeGap.startTime;
				taskList.get(i).timeGap.endTime = oldTaskInfor[i].timeGap.endTime;
				taskList.get(i).timeGap.gap = oldTaskInfor[i].timeGap.gap;
			}
//			for(int i = 0; i < processorsArray.length; ++ i){
//				 processorsArray[i].availableTime = oldProcessorInfor[i].availableTime;
//			}
			sumCost += disCost;
		}
				
		return sumCost;
	}
	public static double updateTaskInforNew(double maxTime, int ithToBeChangedTask, double sumCost,
										 List<Map.Entry<Integer, Double>> taskDisCostList,
										 ArrayList<Task> taskList, ArrayList<Integer> taskOrderList,
										 HashMap<String, Double> taskEdgeHashMap, Processor[] processorsArray){

		//保存当前状态用以回滚
		Task[] oldTaskInfor = new Task[taskList.size()];
		for(int i = 0; i < taskList.size(); ++ i){
			oldTaskInfor[i] = new Task();
			oldTaskInfor[i].selectedProcessorId = taskList.get(i).selectedProcessorId;
			oldTaskInfor[i].selectedFre=taskList.get(i).selectedFre;
			oldTaskInfor[i].timeGap = new TimeGap();
			oldTaskInfor[i].timeGap.startTime = taskList.get(i).timeGap.startTime;
			oldTaskInfor[i].timeGap.endTime = taskList.get(i).timeGap.endTime;
			oldTaskInfor[i].timeGap.gap = taskList.get(i).timeGap.gap;
		}

//		Processor[] oldProcessorInfor = new Processor[processorsArray.length];
//		for(int i = 0; i < processorsArray.length; ++ i){
//			oldProcessorInfor[i] = new Processor();
//			oldProcessorInfor[i].availableTime = processorsArray[i].availableTime;
//		}

		int taskId = taskDisCostList.get(ithToBeChangedTask).getKey();
		Task task = taskList.get(taskId - 1);
//		double taskStartTime = task.timeGap.startTime;

		/**更改部分**/
//		for(Task task1 :taskList){
//			task1.timeGap = new TimeGap();
//		}

		double disCost = taskDisCostList.get(ithToBeChangedTask).getValue();
		//调换任务之后代价会减少，减少的量就是更换前后处理器的代价差
		sumCost -= disCost;
		int updateIndex = 0;

		for(Processor processor: processorsArray){
			processor.availableTime = Double.MIN_VALUE;
		}

//		for(Integer taskIndex: taskOrderList){
//			if(taskIndex == taskId){
//				break;
//			}
//			Task tempTask = taskList.get(taskIndex - 1);
//			int processorId = tempTask.selectedProcessorId;
//			double tempAvailableTime = tempTask.timeGap.endTime;
//			processorsArray[processorId].availableTime = Math.max(tempAvailableTime, processorsArray[processorId].availableTime);
//		}

		//跳过要调换的任务之前的任务
		for(; updateIndex < taskList.size(); ++ updateIndex){
			if(taskOrderList.get(updateIndex) == taskId){
				break;
			}
			Task tempTask = taskList.get(taskOrderList.get(updateIndex) - 1);
			int processorId = tempTask.selectedProcessorId;
			double tempAvailableTime = tempTask.timeGap.endTime;
			processorsArray[processorId].availableTime = Math.max(tempAvailableTime, processorsArray[processorId].availableTime);
			/**更改处**/
//			TaskScheduling.taskScheduling(taskOrderList.get(updateIndex), taskList, taskEdgeHashMap, processorsArray);

//			System.out.println("Before task:" + "\t"
//			                   + taskOrderList.get(updateIndex)+ "\t"
//			                   + taskList.get(taskOrderList.get(updateIndex)-1).selectedProcessorId + "\t"
//			                   + taskList.get(taskOrderList.get(updateIndex)-1).timeGap.startTime + "\t"
//			                   + taskList.get(taskOrderList.get(updateIndex)-1).timeGap.endTime + "\t"
//			                   + taskList.get(taskOrderList.get(updateIndex)-1).timeGap.gap + "\t"
//			                   +  + CalcMakeSpan.calcMakeSpan(taskList)
//					);
		}


		//更新当前要调换处理器的任务

		//寻找父节点任务的最晚结束时间
		ArrayList<Integer> predTaskArrayList = taskList.get(taskId - 1).predecessorTaskList;
		//父节点的最晚结束时间
		double timeThreshold = -1;
		for(Integer predTaskId: predTaskArrayList){
			double temp = 0;
			Task predTask = taskList.get(predTaskId - 1);
			if(predTask.selectedProcessorId == task.minCostProcessorId){
				temp = predTask.timeGap.endTime;
			}else{
				temp = predTask.timeGap.endTime + taskEdgeHashMap.get(predTaskId + "_" + taskId);
			}
			if(timeThreshold < temp){
				timeThreshold = temp;
			}
		}

		task.timeGap.startTime = Math.max(timeThreshold, processorsArray[task.minCostProcessorId].availableTime);
		task.selectedProcessorId = task.minCostProcessorId;
		task.timeGap.endTime = task.timeGap.startTime + task.minCostExcuteTime;
		task.timeGap.gap = task.minCostExcuteTime;
		processorsArray[task.minCostProcessorId].availableTime = task.timeGap.endTime;

		++ updateIndex;
		//保持任务的处理器不变，更新时间
		for(; updateIndex < taskList.size(); ++ updateIndex){
			UpdateTaskInforAfterChangeScheduling.updateTaskInfor(taskOrderList.get(updateIndex), taskList, taskEdgeHashMap, processorsArray);

//			System.out.println("after task:" + "\t"
//	                   + taskOrderList.get(updateIndex)+ "\t"
//	                   + taskList.get(taskOrderList.get(updateIndex)-1).selectedProcessorId + "\t"
//	                   + taskList.get(taskOrderList.get(updateIndex)-1).timeGap.startTime + "\t"
//	                   + taskList.get(taskOrderList.get(updateIndex)-1).timeGap.endTime + "\t"
//	                   + taskList.get(taskOrderList.get(updateIndex)-1).timeGap.gap + "\t"
//	                   + CalcMakeSpan.calcMakeSpan(taskList)
//			);
		}

		//System.out.println(CalcMakeSpan.calcMakeSpan(taskList));

		//若是转换之后的makespan超过规定时间则回滚
//		double newMakeSpan = CalcMakeSpan.calcMakeSpan(taskList);
//		System.out.println("newmakespan: " + newMakeSpan);

		if(maxTime < CalcMakeSpan.calcMakeSpan(taskList)){
			for(int i = 0; i < taskList.size(); ++ i){
				taskList.get(i).selectedProcessorId = oldTaskInfor[i].selectedProcessorId;
				taskList.get(i).selectedFre=oldTaskInfor[i].selectedFre;
				taskList.get(i).timeGap.startTime = oldTaskInfor[i].timeGap.startTime;
				taskList.get(i).timeGap.endTime = oldTaskInfor[i].timeGap.endTime;
				taskList.get(i).timeGap.gap = oldTaskInfor[i].timeGap.gap;
			}
//			for(int i = 0; i < processorsArray.length; ++ i){
//				 processorsArray[i].availableTime = oldProcessorInfor[i].availableTime;
//			}
			sumCost += disCost;
		}

		return sumCost;
	}

}
