package taskscheduling.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import taskscheduling.Processor;
import taskscheduling.Task;
import taskscheduling.TimeGap;

public class TaskScheduling {
	
	public static void taskScheduling(int taskId, ArrayList<Task> taskList, 
			HashMap<String, Double> taskEdgeHashMap, Processor[] processorsArray){
		
		Task task = taskList.get(taskId);
		int selectedProcessorId = -1;
		double minEFT = Double.MAX_VALUE;
		//遍历处理器
		for(int i = 0; i < processorsArray.length; ++ i){
			
			double availableTime = processorsArray[i].availableTime;
		
			//寻找空隙插入，有空隙可以插入，立即结束
			double gapInsertStartTime = getTimeGap(i, taskId, taskList, taskEdgeHashMap, processorsArray);
			if(gapInsertStartTime > 0){
//				processorsArray[i].taskList.add(taskId);
				task.selectedProcessorId = i;
				task.selectedFre=processorsArray[i].fMax;
				task.timeGap.startTime = gapInsertStartTime;
				task.timeGap.endTime = gapInsertStartTime + task.computationCost.get(i);
				task.timeGap.gap = task.timeGap.endTime - task.timeGap.startTime;
				return;
			}
		
			//正常算法查找
			double est = CalcEST.calcEST(i, taskId, availableTime, taskList, taskEdgeHashMap);
			double tempEFT = est + task.computationCost.get(i);
			if(minEFT > tempEFT){
				minEFT = tempEFT;
				selectedProcessorId = i;
			}
		}
		
		double oldAvailableTime = processorsArray[selectedProcessorId].availableTime;
		
		task.selectedProcessorId = selectedProcessorId;
		task.selectedFre=processorsArray[selectedProcessorId].fMax;
		task.timeGap.endTime = minEFT;
		task.timeGap.startTime = minEFT - task.computationCost.get(selectedProcessorId);
		task.timeGap.gap = task.timeGap.endTime - task.timeGap.startTime;
		
//		processorsArray[selectedProcessorId].taskList.add(taskId);
		processorsArray[selectedProcessorId].availableTime = minEFT;
		
		//正常插入可能增加空闲时间片段		
		if(oldAvailableTime < task.timeGap.startTime){
			TimeGap timeGap = new TimeGap(oldAvailableTime, task.timeGap.startTime);
			processorsArray[selectedProcessorId].timeGapList.add(timeGap);
		}
	}

	public static void taskScheduling1(int taskId, ArrayList<Task> taskList,double beta,
									  HashMap<String,Double> taskEdgeHashMap, Processor[] processorsArray){

		Task task = taskList.get(taskId - 1);
		int selectedProcessorId = -1;
		double minEFT = Double.MAX_VALUE;
		//遍历处理器
		for(int i = 0; i < processorsArray.length; ++ i){

			double availableTime = processorsArray[i].availableTime;

			//寻找空隙插入，有空隙可以插入，立即结束
			double gapInsertStartTime = getTimeGap1(i, taskId, taskList, beta,taskEdgeHashMap, processorsArray);
			if(gapInsertStartTime > 0){
//				processorsArray[i].taskList.add(taskId);
				task.selectedProcessorId = i;
				task.selectedFre=processorsArray[i].fMin;
				task.timeGap.startTime = gapInsertStartTime;
				task.timeGap.endTime = gapInsertStartTime + CalcRunTime.calcRunTime(beta, task.selectedFre,
						processorsArray[i], task.computationCost.get(i));
				task.timeGap.gap = task.timeGap.endTime - task.timeGap.startTime;
				return;
			}

			//正常算法查找
			double est = CalcEST.calcEST(i, taskId, availableTime, taskList, taskEdgeHashMap);
			double excuTime=CalcRunTime.calcRunTime(beta, processorsArray[i].fMin,
					processorsArray[i], task.computationCost.get(i));
			double tempEFT = est + excuTime;
			//System.out.println(minEFT+"\t"+tempEFT+"\t"+excuTime);
			if(minEFT > tempEFT){
				minEFT = tempEFT;
				selectedProcessorId = i;
			}
		}

		double oldAvailableTime = processorsArray[selectedProcessorId].availableTime;
		double excuTime=CalcRunTime.calcRunTime(beta, processorsArray[selectedProcessorId].fMin,
				processorsArray[selectedProcessorId], task.computationCost.get(selectedProcessorId));
		task.selectedProcessorId = selectedProcessorId;
		task.selectedFre=processorsArray[selectedProcessorId].fMin;
		task.timeGap.endTime = minEFT;
		task.timeGap.startTime = minEFT -excuTime ;
		task.timeGap.gap = task.timeGap.endTime - task.timeGap.startTime;

//		processorsArray[selectedProcessorId].taskList.add(taskId);
		processorsArray[selectedProcessorId].availableTime = minEFT;

		//正常插入可能增加空闲时间片段
		if(oldAvailableTime < task.timeGap.startTime){
			TimeGap timeGap = new TimeGap(oldAvailableTime, task.timeGap.startTime);
			processorsArray[selectedProcessorId].timeGapList.add(timeGap);
		}
	}
	//寻找空闲的时间片段，如果可以插入返回插入的起始时间，否则返回-1
	private static double getTimeGap(int processorId, int taskId, ArrayList<Task> taskList,
			HashMap<String, Double> taskEdgeHashMap, Processor[] processorsArray){
		//返回值
		double startTime = -1;
		
		//寻找父节点任务的最晚结束时间
		ArrayList<Integer> predTaskArrayList = taskList.get(taskId).predecessorTaskList;

		double timeThreshold = -1;//父节点的最晚结束时间
		for(Integer predTaskId: predTaskArrayList){
			double temp = 0;
			Task predTask = taskList.get(predTaskId );
			if(predTask.selectedProcessorId == processorId){
				temp = predTask.timeGap.endTime;
			}else{
				temp = predTask.timeGap.endTime + taskEdgeHashMap.get(predTaskId + "_" + (taskId));
			}
			if(timeThreshold < temp){
				timeThreshold = temp;
			}
		}
		
		//遍历处理器的空闲片段，有两种情况可以插入
		ArrayList<TimeGap> timeGapList = processorsArray[processorId].timeGapList;
		Collections.sort(timeGapList);
		double taskExcuteTime = taskList.get(taskId).computationCost.get(processorId);
		for(TimeGap timeGap: timeGapList){
			//第一种插入情况，插入之后更新空闲时间片段
			if(timeGap.startTime >= timeThreshold && timeGap.gap >= taskExcuteTime){
				startTime = timeGap.startTime;
				TimeGap newTimeGap = new TimeGap(startTime + taskExcuteTime, timeGap.endTime);
				timeGapList.remove(timeGap);
				timeGapList.add(newTimeGap);
				break;
			}
			//第二种插入情况，插入之后更新空闲时间片段
			if(timeGap.endTime - taskExcuteTime >= timeThreshold && timeGap.endTime - timeThreshold <= timeGap.gap){
				startTime = timeThreshold;
				TimeGap newTimeGap1 = new TimeGap(timeGap.startTime, timeThreshold);
				timeGapList.add(newTimeGap1);
				TimeGap newTimeGap2 = new TimeGap(timeThreshold + taskExcuteTime, timeGap.endTime);
				timeGapList.add(newTimeGap2);
				timeGapList.remove(timeGap);
				break;
			}					
		}
		
		return startTime;
	}
	private static double getTimeGap1(int processorId, int taskId, ArrayList<Task> taskList,double beta,
									 HashMap<String, Double> taskEdgeHashMap, Processor[] processorsArray){
		//返回值
		double startTime = -1;

		//寻找父节点任务的最晚结束时间
		ArrayList<Integer> predTaskArrayList = taskList.get(taskId - 1).predecessorTaskList;

		double timeThreshold = -1;//父节点的最晚结束时间
		for(Integer predTaskId: predTaskArrayList){
			double temp = 0;
			Task predTask = taskList.get(predTaskId - 1);
			if(predTask.selectedProcessorId == processorId){
				temp = predTask.timeGap.endTime;
			}else{
				temp = predTask.timeGap.endTime + taskEdgeHashMap.get(predTaskId + "_" + taskId);
			}
			if(timeThreshold < temp){
				timeThreshold = temp;
			}
		}

		//遍历处理器的空闲片段，有两种情况可以插入
		ArrayList<TimeGap> timeGapList = processorsArray[processorId].timeGapList;
		Collections.sort(timeGapList);
		double taskExcuteTime = taskList.get(taskId - 1).computationCost.get(processorId);
		double excuTime=CalcRunTime.calcRunTime(beta, processorsArray[processorId].fre,
				processorsArray[processorId], taskList.get(taskId - 1).computationCost.get(processorId));
		for(TimeGap timeGap: timeGapList){
			//第一种插入情况，插入之后更新空闲时间片段
			if(timeGap.startTime >= timeThreshold && timeGap.gap >= excuTime){
				startTime = timeGap.startTime;
				TimeGap newTimeGap = new TimeGap(startTime + excuTime, timeGap.endTime);
				timeGapList.remove(timeGap);
				timeGapList.add(newTimeGap);
				break;
			}
			//第二种插入情况，插入之后更新空闲时间片段
			if(timeGap.endTime - excuTime >= timeThreshold && timeGap.endTime - timeThreshold <= timeGap.gap){
				startTime = timeThreshold;
				TimeGap newTimeGap1 = new TimeGap(timeGap.startTime, timeThreshold);
				timeGapList.add(newTimeGap1);
				TimeGap newTimeGap2 = new TimeGap(timeThreshold + excuTime, timeGap.endTime);
				timeGapList.add(newTimeGap2);
				timeGapList.remove(timeGap);
				break;
			}
		}

		return startTime;
	}
}
