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
		//����������
		for(int i = 0; i < processorsArray.length; ++ i){
			
			double availableTime = processorsArray[i].availableTime;
		
			//Ѱ�ҿ�϶���룬�п�϶���Բ��룬��������
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
		
			//�����㷨����
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
		
		//��������������ӿ���ʱ��Ƭ��		
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
		//����������
		for(int i = 0; i < processorsArray.length; ++ i){

			double availableTime = processorsArray[i].availableTime;

			//Ѱ�ҿ�϶���룬�п�϶���Բ��룬��������
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

			//�����㷨����
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

		//��������������ӿ���ʱ��Ƭ��
		if(oldAvailableTime < task.timeGap.startTime){
			TimeGap timeGap = new TimeGap(oldAvailableTime, task.timeGap.startTime);
			processorsArray[selectedProcessorId].timeGapList.add(timeGap);
		}
	}
	//Ѱ�ҿ��е�ʱ��Ƭ�Σ�������Բ��뷵�ز������ʼʱ�䣬���򷵻�-1
	private static double getTimeGap(int processorId, int taskId, ArrayList<Task> taskList,
			HashMap<String, Double> taskEdgeHashMap, Processor[] processorsArray){
		//����ֵ
		double startTime = -1;
		
		//Ѱ�Ҹ��ڵ�������������ʱ��
		ArrayList<Integer> predTaskArrayList = taskList.get(taskId).predecessorTaskList;

		double timeThreshold = -1;//���ڵ���������ʱ��
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
		
		//�����������Ŀ���Ƭ�Σ�������������Բ���
		ArrayList<TimeGap> timeGapList = processorsArray[processorId].timeGapList;
		Collections.sort(timeGapList);
		double taskExcuteTime = taskList.get(taskId).computationCost.get(processorId);
		for(TimeGap timeGap: timeGapList){
			//��һ�ֲ������������֮����¿���ʱ��Ƭ��
			if(timeGap.startTime >= timeThreshold && timeGap.gap >= taskExcuteTime){
				startTime = timeGap.startTime;
				TimeGap newTimeGap = new TimeGap(startTime + taskExcuteTime, timeGap.endTime);
				timeGapList.remove(timeGap);
				timeGapList.add(newTimeGap);
				break;
			}
			//�ڶ��ֲ������������֮����¿���ʱ��Ƭ��
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
		//����ֵ
		double startTime = -1;

		//Ѱ�Ҹ��ڵ�������������ʱ��
		ArrayList<Integer> predTaskArrayList = taskList.get(taskId - 1).predecessorTaskList;

		double timeThreshold = -1;//���ڵ���������ʱ��
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

		//�����������Ŀ���Ƭ�Σ�������������Բ���
		ArrayList<TimeGap> timeGapList = processorsArray[processorId].timeGapList;
		Collections.sort(timeGapList);
		double taskExcuteTime = taskList.get(taskId - 1).computationCost.get(processorId);
		double excuTime=CalcRunTime.calcRunTime(beta, processorsArray[processorId].fre,
				processorsArray[processorId], taskList.get(taskId - 1).computationCost.get(processorId));
		for(TimeGap timeGap: timeGapList){
			//��һ�ֲ������������֮����¿���ʱ��Ƭ��
			if(timeGap.startTime >= timeThreshold && timeGap.gap >= excuTime){
				startTime = timeGap.startTime;
				TimeGap newTimeGap = new TimeGap(startTime + excuTime, timeGap.endTime);
				timeGapList.remove(timeGap);
				timeGapList.add(newTimeGap);
				break;
			}
			//�ڶ��ֲ������������֮����¿���ʱ��Ƭ��
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
