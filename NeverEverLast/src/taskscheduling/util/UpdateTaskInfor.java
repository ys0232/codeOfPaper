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
		
		//���浱ǰ״̬���Իع�
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
		Task task = taskList.get(taskId );
//		double taskStartTime = task.timeGap.startTime;
		
		/**���Ĳ���**/
//		for(Task task1 :taskList){
//			task1.timeGap = new TimeGap();
//		}
		
		double disCost = taskDisCostList.get(ithToBeChangedTask).getValue(); 
		//��������֮����ۻ���٣����ٵ������Ǹ���ǰ�������Ĵ��۲�
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
			
		//����Ҫ����������֮ǰ������
		for(; updateIndex < taskList.size(); ++ updateIndex){
			if(taskOrderList.get(updateIndex) == taskId){
				break;
			}
			Task tempTask = taskList.get(taskOrderList.get(updateIndex) );
			int processorId = tempTask.selectedProcessorId;
			double tempAvailableTime = tempTask.timeGap.endTime;
			processorsArray[processorId].availableTime = Math.max(tempAvailableTime, processorsArray[processorId].availableTime);
			/**���Ĵ�**/
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
		

		//���µ�ǰҪ����������������
		
		//Ѱ�Ҹ��ڵ�������������ʱ��
		ArrayList<Integer> predTaskArrayList = taskList.get(taskId ).predecessorTaskList;
		//���ڵ���������ʱ��
		double timeThreshold = -1;
		for(Integer predTaskId: predTaskArrayList){
			double temp = 0;
			Task predTask = taskList.get(predTaskId );
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
		//��������Ĵ��������䣬����ʱ��
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
		
		//����ת��֮���makespan�����涨ʱ����ع�
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

		//���浱ǰ״̬���Իع�
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

		/**���Ĳ���**/
//		for(Task task1 :taskList){
//			task1.timeGap = new TimeGap();
//		}

		double disCost = taskDisCostList.get(ithToBeChangedTask).getValue();
		//��������֮����ۻ���٣����ٵ������Ǹ���ǰ�������Ĵ��۲�
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

		//����Ҫ����������֮ǰ������
		for(; updateIndex < taskList.size(); ++ updateIndex){
			if(taskOrderList.get(updateIndex) == taskId){
				break;
			}
			Task tempTask = taskList.get(taskOrderList.get(updateIndex) - 1);
			int processorId = tempTask.selectedProcessorId;
			double tempAvailableTime = tempTask.timeGap.endTime;
			processorsArray[processorId].availableTime = Math.max(tempAvailableTime, processorsArray[processorId].availableTime);
			/**���Ĵ�**/
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


		//���µ�ǰҪ����������������

		//Ѱ�Ҹ��ڵ�������������ʱ��
		ArrayList<Integer> predTaskArrayList = taskList.get(taskId - 1).predecessorTaskList;
		//���ڵ���������ʱ��
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
		//��������Ĵ��������䣬����ʱ��
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

		//����ת��֮���makespan�����涨ʱ����ع�
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
