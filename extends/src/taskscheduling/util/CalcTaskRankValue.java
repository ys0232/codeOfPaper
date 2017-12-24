package taskscheduling.util;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;
import java.util.Map.Entry;

import taskscheduling.Task;

public class CalcTaskRankValue {
	
	public static ArrayList<Integer> calcTaskRankValue(ArrayList<Task> taskList, 
			HashMap<String, Double> taskEdgeHashMap, int beginTask){
		
		HashMap<Integer, Double> taskIdRankValueMap = new HashMap<>();
		Queue<Integer> taskIdQueue = new LinkedList<>();
		taskIdQueue.offer(beginTask);
		
		while(taskIdQueue.size() > 0){
			
			int taskId = taskIdQueue.poll();
			ArrayList<Integer> succArrayList = taskList.get(taskId - 1).successorTaskList;
			
			//判断子节点都算过了
			boolean allSuccCalced = true;
			for(int succTask: succArrayList){
				if(taskIdRankValueMap.get(succTask) == null){
					taskIdQueue.add(taskId);
					allSuccCalced = false;
					break;
				}
			}
			
			if(allSuccCalced){
				for(Integer predTask: taskList.get(taskId - 1).predecessorTaskList){
					if(!taskIdQueue.contains(predTask)){
						taskIdQueue.add(predTask);
					}		
				}
				
				double rankValue = 0.0;
				
				for(int succTask: succArrayList){
					Double succRankValue = taskIdRankValueMap.get(succTask);
					double newRankValue = succRankValue + taskEdgeHashMap.get(taskId + "_" + succTask);
					if(rankValue < newRankValue){
						rankValue = newRankValue;
					}
				}			
				taskIdRankValueMap.put(taskId, rankValue + taskList.get(taskId - 1).averageCost);
			}
			
		}
		
		//按rank值从大到小排序
		List<Map.Entry<Integer, Double>> taskIdRankValueList = new ArrayList<Map.Entry<Integer, Double>>(taskIdRankValueMap.entrySet());  
        Collections.sort(taskIdRankValueList, new Comparator<Map.Entry<Integer, Double>>() {

			@Override
			public int compare(Entry<Integer, Double> o1,
					Entry<Integer, Double> o2) {
				// TODO Auto-generated method stub
				return o2.getValue().compareTo(o1.getValue());//降序排序
			}  
              
  
        });  
        
        ArrayList<Integer> taskOrderList = new ArrayList<>();
        for (Map.Entry<Integer, Double> map : taskIdRankValueList) {  
            taskOrderList.add(map.getKey()); 
        } 
        
		return taskOrderList;
	}

	
	public static ArrayList<Integer> calcTaskRankValue1(ArrayList<Task> taskList, 
			HashMap<String, Double> taskEdgeHashMap, int beginTask)throws IOException{
		
		HashMap<Integer, Double> taskIdRankValueMap = new HashMap<>();
		for(int i = beginTask-1; i >=0; i--){
			taskIdRankValueMap.put(i, taskList.get(i).averageCost);//从下向上
			
			ArrayList<Integer> succArrayList = taskList.get(i).successorTaskList;
			
			double rankValue = 0.0;
			if (succArrayList.size()>0){
			for(int succTask: succArrayList){
				//System.out.println(i+"\t"+succTask);
				Double succRankValue = taskIdRankValueMap.get(succTask);
				//if (i==944)
				//System.out.println(i + "_" + succTask);
				double newRankValue = succRankValue + taskEdgeHashMap.get(i + "_" + succTask);
				if(rankValue < newRankValue){
					rankValue = newRankValue;
				}
			}
			}
			taskIdRankValueMap.put(i, rankValue + taskList.get(i).averageCost);
			
		}
		
		//按rank值从大到小排序
				List<Map.Entry<Integer, Double>> taskIdRankValueList = new ArrayList<Map.Entry<Integer, Double>>(taskIdRankValueMap.entrySet());  
		        Collections.sort(taskIdRankValueList, new Comparator<Map.Entry<Integer, Double>>() {

					@Override
					public int compare(Entry<Integer, Double> o1,
							Entry<Integer, Double> o2) {
						// TODO Auto-generated method stub
						return o2.getValue().compareTo(o1.getValue());//降序排序
					}  
		              
		  
		        });  
		        
		        ArrayList<Integer> taskOrderList = new ArrayList<>();
		        for (Map.Entry<Integer, Double> map : taskIdRankValueList) {  
		            taskOrderList.add(map.getKey()); 
		        } 
		        
				return taskOrderList;
	}

}
