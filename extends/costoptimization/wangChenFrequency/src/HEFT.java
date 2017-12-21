import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import taskscheduling.Processor;
import taskscheduling.Task;
import taskscheduling.util.CalcArrayListSum;
import taskscheduling.util.CalcTaskMaxCost;
import taskscheduling.util.CalcTaskRankValue;
import taskscheduling.util.ChangeFromMaxDisCost;
import taskscheduling.util.ChangeFromMinDisCost;
import taskscheduling.util.GetDisCostTaskOrder;
import taskscheduling.util.GetMinCostProcessorIdLevel;
import taskscheduling.util.TaskScheduling;


public class HEFT {

	public static void main(String[] args) throws IOException{
		
		int maxTime = 5;         //Maximum Time
		int processorNums = 3;    //Number of resources
		int taskNums = 10;        //Number of Tasks
		double beta = 0.2;        //Betta in the equation
		
		//computationCost.txt: taskId	p1runtime	p2runtime	p3runtime
		String computationCostPath = "computationCost.txt";//"D:\\workspace\\FrequenceHEFT\\computationCost.txt"
		
		//inputGraph.txt: taskId1		taskId2		transportTime
		String inputGraphPath = "inputGraph.txt";//"D:\\workspace\\FrequenceHEFT\\inputGraph.txt";
		
		//processorInfor.txt: processorId	fmax	fmin	flevel/Differance	cmin	costMode/Pricing Model
		String processorInfor = "processorInfor.txt";//"D:\\workspace\\FrequenceHEFT\\processorInfor.txt"
		
		
		//Initial schedule/initialize the schedule
		SchedulingInit sInit = new SchedulingInit();
		//初始化任务信息
		ArrayList<Task> taskList = sInit.initTaskInfor(computationCostPath, inputGraphPath);
		//初始化传输时间
		HashMap<String, Double> taskEdgeHashMap = sInit.initTaskEdge();
		//初始化处理器的信息
		Processor[] processorsArray = new Processor[processorNums];
		processorsArray = sInit.initProcessorInfor(processorInfor, processorNums);
		
		//计算任务在所有任务上的最大频率执行时间，用以计算任务的rank值
		for(int i = 0; i < taskNums; ++ i){
			Task tempTask = taskList.get(i);
			taskList.get(i).averageCost = CalcArrayListSum.calcArrayListSum(tempTask.computationCost) / processorNums;;
		}
		
		//计算rank值排序
		ArrayList<Integer> taskOrderList = CalcTaskRankValue.calcTaskRankValue(taskList, taskEdgeHashMap, taskNums);
		
		//根据已经排序的任务，按顺序进行调度
		for(Integer taskId: taskOrderList){
			TaskScheduling.taskScheduling(taskId, taskList, taskEdgeHashMap, processorsArray);
		}
		
		CalcTaskMaxCost.calcTaskMaxCost(beta, taskList, processorsArray);
		
		//输出HEFT任务的调度情况
		System.out.println("开始转换之前：");
//		for(Task task: taskList){
//			System.out.println(task.taskId + "\t" + (task.selectedProcessorId + 1) + "\t" + task.timeGap.startTime + "\t" + task.timeGap.endTime);
//		}
		for(Integer taskId: taskOrderList){
			System.out.println(taskList.get(taskId - 1).taskId + "\t" + (taskList.get(taskId - 1).selectedProcessorId + 1) + "\t" + taskList.get(taskId - 1).timeGap.startTime + "\t" + taskList.get(taskId - 1).timeGap.endTime);
		}
		

		
		//获取每个任务获取最小代价的处理器的相关信息
		GetMinCostProcessorIdLevel.getMinCostProcessorIdLevel(beta, taskList, processorsArray);
		System.out.println("maxcost\tmincost");
		for(Task task: taskList){
			System.out.println(task.maxCost + "\t" + task.minCost);
		}
		System.out.println();
		//获取根据代价差从大到小排序的任务列表
		List<Map.Entry<Integer, Double>> taskDisCostList = GetDisCostTaskOrder.getDisCostTaskOrder(taskList);
		for(Map.Entry<Integer, Double> entry: taskDisCostList){
			System.out.println(entry.getKey() + "\t" + entry.getValue());
		}
		//根据代价差从大到小调换任务
	//	ChangeFromMaxDisCost.changeFromMaxDisCost(maxTime, taskDisCostList, taskList, taskOrderList, taskEdgeHashMap, processorsArray);
		//根据代价差从小到大调换任务
		//ChangeFromMinDisCost.changeFromMinDisCost(maxTime, taskDisCostList, taskList, taskOrderList, taskEdgeHashMap, processorsArray);
		
		System.out.println("转换之后：");
		for(Integer taskId: taskOrderList){
			System.out.println(taskList.get(taskId - 1).taskId + "\t" + (taskList.get(taskId - 1).selectedProcessorId + 1) + "\t" + taskList.get(taskId - 1).timeGap.startTime + "\t" + taskList.get(taskId - 1).timeGap.endTime);
		}
	}
}
