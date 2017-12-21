package taskscheduling.util;

import java.io.*;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import taskscheduling.Processor;
import taskscheduling.Task;
import taskscheduling.TimeGap;

public class ChangeFromMaxDisCost {
	
	public static void changeFromMaxDisCost(double maxTimeParameter, 
			List<Map.Entry<Integer, Double>> taskDisCostList,
			ArrayList<Task> taskList, ArrayList<Integer> taskOrderList,
			HashMap<String, Double> taskEdgeHashMap, Processor[] processorsArray,PrintWriter printWriter) throws IOException{
		
		//compute sum cost and makespan,deadline(maxTime)
		double sumCost = CalcSumMaxCost.calcSumMaxCost(taskList);
		double makespan = CalcMakeSpan.calcMakeSpan(taskList);
		double maxTime = makespan * maxTimeParameter;
		System.out.println("Using HEFT \tmakespan: " + makespan + "\tsumCost: " + sumCost);
		double mk=makespan;
		double sc=sumCost;
		//change from max cost
		for(int i = 0; makespan <= maxTime && i < taskList.size(); makespan = CalcMakeSpan.calcMakeSpan(taskList), ++ i){
			double tempCost = sumCost;
			sumCost = UpdateTaskInfor.updateTaskInfor(maxTime, i, sumCost, taskDisCostList, taskList, taskOrderList, taskEdgeHashMap, processorsArray);
			if(Math.abs(tempCost - sumCost) < 1e-10){
				break;
			}
		}
		System.out.println("After changing \tmakespan: "+ makespan + "\tsumCost: " + sumCost);
		DecimalFormat df = new DecimalFormat("#.00");
		printWriter.write(df.format(mk) + "\t" + df.format(sc) + "\t" + df.format(maxTime) + "\t" + df.format(makespan) + "\t" + df.format(sumCost) + "\t" + maxTimeParameter + "\n");
		printWriter.flush();

	}

}
