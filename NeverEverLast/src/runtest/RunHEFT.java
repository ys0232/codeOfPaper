package runtest;

import java.io.IOException;
import java.io.PrintWriter;
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

public class RunHEFT {
	public static void runHEFT(double maxTimeParameter, int processorNums, int taskNums, double beta, boolean changeOrder,
							   String computationCostPath, String inputGraphPath, String processorInfor, PrintWriter printWriter) throws IOException{

				//the class used for initialing
				SchedulingInit sInit = new SchedulingInit();
				//initial task info
				ArrayList<Task> taskList = sInit.initTaskInfor(computationCostPath, inputGraphPath);
				//initial communication data
				HashMap<String, Double> taskEdgeHashMap = sInit.initTaskEdge();

				//initial processor info
				Processor[] processorsArray = new Processor[processorNums];
				processorsArray = sInit.initProcessorInfor(processorInfor, processorNums,2);
				
				//compute average computation time using for the rank，计算平均计算时间
				for(int i = 0; i < taskNums; ++ i){
					Task tempTask = taskList.get(i);
					taskList.get(i).averageCost = CalcArrayListSum.calcArrayListSum(tempTask.computationCost) / processorNums;
				}

				//compute rank,then sort，返回各节点的最长执行时间的降序排列
				ArrayList<Integer> taskOrderList = CalcTaskRankValue.calcTaskRankValue1(taskList, taskEdgeHashMap, taskNums);
				
				//scheduling using HEFT，对于每一个任务，选择一个合适的时间间隙插入
				for(Integer taskId: taskOrderList){
					TaskScheduling.taskScheduling(taskId, taskList, taskEdgeHashMap, processorsArray);
				}
				//计算HEFT调度后的每个任务的计算时间
				CalcTaskMaxCost.calcTaskMaxCost(beta, taskList, processorsArray);
				
				//compute min cost in all processors，寻找代价最小的处理器的编号和level
				GetMinCostProcessorIdLevel.getMinCostProcessorIdLevel(beta, taskList, processorsArray);
				//rank task by cost difference
				List<Map.Entry<Integer, Double>> taskDisCostList = GetDisCostTaskOrder.getDisCostTaskOrder(taskList);
				
				if(changeOrder){
					//change from max cost
					ChangeFromMaxDisCost.changeFromMaxDisCost(maxTimeParameter, taskDisCostList, taskList, taskOrderList, taskEdgeHashMap, processorsArray,printWriter);
				}else{
					//change from min cost
					ChangeFromMinDisCost.changeFromMinDisCost(maxTimeParameter, taskDisCostList, taskList, taskOrderList, taskEdgeHashMap, processorsArray);
				}
							
	}

}
