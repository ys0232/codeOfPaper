package taskscheduling.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import taskscheduling.Processor;
import taskscheduling.Task;

public class ChangeFromMinDisCost {

	public static void changeFromMinDisCost(double maxTimeParameter, 
			List<Map.Entry<Integer, Double>> taskDisCostList,
			ArrayList<Task> taskList, ArrayList<Integer> taskOrderList,
			HashMap<String, Double> taskEdgeHashMap, Processor[] processorsArray){
		
		double sumCost = CalcSumMaxCost.calcSumMaxCost(taskList);
		double makespan = CalcMakeSpan.calcMakeSpan(taskList);
		double maxTime = makespan * maxTimeParameter;
		System.out.println("using HEFT \tmakespan: " + makespan + "\tsumCost: " + sumCost);
		//change from min cost
		for(int i = taskList.size() - 1; makespan <= maxTime && i >= 0; makespan = CalcMakeSpan.calcMakeSpan(taskList), -- i){
			if(taskDisCostList.get(i).getValue() < 1e-10){
				continue;
			}
			double tempCost = sumCost;
			sumCost = UpdateTaskInfor.updateTaskInfor(maxTime, i, sumCost, taskDisCostList, taskList, taskOrderList, taskEdgeHashMap, processorsArray);
			if(Math.abs(tempCost - sumCost) < 1e-10){
				break;
			}
		}
		System.out.println("+++++++++++++++++++++++++++++++\nCFMin_HEFT SCHEDULER\n");
		for (Integer taskid : taskOrderList) {
			Task tp=taskList.get(taskid);
			System.out.println("task_id \t"+taskid+"\t"+"\tstart time\t"+tp.timeGap.startTime+"\tend time:\t"+tp.timeGap.endTime);

		}
		System.out.println("After changing \tmakespan: " + makespan + "\tsumCost: " + sumCost);
	}
}
