package taskscheduling.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import taskscheduling.Processor;
import taskscheduling.Task;

public class GetDisCostTaskOrder {

	public static List<Entry<Integer, Double>> getDisCostTaskOrder(ArrayList<Task> taskList){
		
		HashMap<Integer, Double> taskIdMaxCostMap = new HashMap<>();
			
		for(Task task: taskList){
			taskIdMaxCostMap.put(task.taskId, task.maxCost  - task.minCost);
		}
						
		List<Map.Entry<Integer, Double>> taskIdMaxCostList = new ArrayList<Map.Entry<Integer, Double>>(taskIdMaxCostMap.entrySet());  
        Collections.sort(taskIdMaxCostList, new Comparator<Map.Entry<Integer, Double>>() {

			@Override
			public int compare(Entry<Integer, Double> o1,
					Entry<Integer, Double> o2) {
				// TODO Auto-generated method stub
				return o2.getValue().compareTo(o1.getValue());//Ωµ–Ú≈≈–Ú
			}  
              
  
        });  
        
        return taskIdMaxCostList;
	}
}
