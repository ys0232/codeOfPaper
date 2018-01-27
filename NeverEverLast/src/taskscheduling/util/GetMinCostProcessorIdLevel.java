package taskscheduling.util;

import java.util.ArrayList;

import generateCost.GetCost;
import taskscheduling.Processor;
import taskscheduling.Task;

//Ѱ�Ҵ�����С�Ĵ������ı�ź�level
public class GetMinCostProcessorIdLevel {
	
	public static void getMinCostProcessorIdLevel(double beta, ArrayList<Task> taskList, Processor[] processorsArray){
		
		//��������
		for(Task task: taskList){		
			
			int minProcessorId = -1;
			int minLevel = -1;
			double minCost = Double.MAX_VALUE;
			double minUnitCost = Double.MAX_VALUE;
			double minRunTime = Double.MAX_VALUE;
			//����������
			for(int i = 0; i < processorsArray.length; ++ i){	
				//����Ƶ��
				for(int frequency = processorsArray[i].fMin; frequency <= processorsArray[i].fMax; frequency += processorsArray[i].fLevel){
					
					double tempUnitCost = CalcCost.getCost(frequency, processorsArray[i]);	
					double tempRuntime = CalcRunTime.calcRunTime(beta, frequency, processorsArray[i], task.computationCost.get(i));
					double tempMinCost = tempRuntime * tempUnitCost;
					
					if(minCost > tempMinCost){
						minCost = tempMinCost;
						minUnitCost = tempUnitCost;
						minProcessorId = i;
						minRunTime = tempRuntime;
						minLevel = (frequency - processorsArray[i].fMin) / processorsArray[i].fLevel;
					}					
				}				
			}	
			
			task.minCost = minCost;
			task.minCostProcessorId = minProcessorId;
			task.minCostProcessorLevel = minLevel;
			task.minCostExcuteTime = minRunTime;
			task.minCostUnit = minUnitCost;
		}

	}
	
	
	

	

}
