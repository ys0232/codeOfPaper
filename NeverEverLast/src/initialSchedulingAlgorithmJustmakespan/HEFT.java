package initialSchedulingAlgorithmJustmakespan;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import runtest.SchedulingInit;
import taskscheduling.CalaCostofAll;
import taskscheduling.Processor;
import taskscheduling.Task;
import taskscheduling.util.CalcArrayListSum;
import taskscheduling.util.CalcMakeSpan;
import taskscheduling.util.CalcTaskMaxCost;
import taskscheduling.util.CalcTaskRankValue;
import taskscheduling.util.GetDisCostTaskOrder;
import taskscheduling.util.GetMinCostProcessorIdLevel;
import taskscheduling.util.TaskScheduling;

public class HEFT {
	public static double HEFTMakespan(double maxTimeParameter, int processorNums, int taskNums, double beta,
			String computationCostPath, String inputGraphPath, String processorInfor
			,int priceModel,long starttime) throws IOException {

		//the class used for initialing
		SchedulingInit sInit = new SchedulingInit();
		//initial task info
		ArrayList<Task> taskList = sInit.initTaskInfor(computationCostPath, inputGraphPath);
		//initial communication data
		HashMap<String, Double> taskEdgeHashMap = sInit.initTaskEdge();

		//initial processor info
		Processor[] processorsArray;
		processorsArray = sInit.initProcessorInfor(processorInfor, processorNums,priceModel);

		//compute average computation time using for the rankï
		for (int i = 0; i < taskNums; ++i) {
			Task tempTask = taskList.get(i);
			taskList.get(i).averageCost = CalcArrayListSum.calcArrayListSum(tempTask.computationCost) / processorNums;
		}
		int cfCount = 0;
		int freqcount = 0;
		for (int i = 0; i < processorNums; i++) {
			int tmp = (processorsArray[i].fMax - processorsArray[i].fMin) / processorsArray[i].fLevel + 1;
			if (tmp > freqcount) {
				freqcount = tmp;
			}
		}
		int[][] cpuFreq = new int[processorNums][freqcount];
		//  System.out.println(processorNums+"\t"+freqcount);

		for (int i = 0; i < processorNums; i++) {
			for (int j = 0; processorsArray[i].fMin + j * processorsArray[i].fLevel <= processorsArray[i].fMax; j++) {
				cpuFreq[i][j] = processorsArray[i].fMin + j * processorsArray[i].fLevel;
				cfCount++;
			}
		}
		// for (int i = 0; i < processorNums; i++)
		//   System.out.println(Arrays.toString(cpuFreq[i]));
		HashMap<Integer, String> schedulerList = new HashMap<>();

		//compute rank,then sort
		ArrayList<Integer> taskOrderList = CalcTaskRankValue.calcTaskRankValue1(taskList, taskEdgeHashMap, taskNums);


		for (Integer taskId : taskOrderList) {
			// System.out.print(taskId+"\t");
			TaskScheduling.taskScheduling(taskId, taskList, taskEdgeHashMap, processorsArray);
			Task tmp = taskList.get(taskId);

			schedulerList.put(taskId , tmp.selectedProcessorId + "_" + processorsArray[tmp.selectedProcessorId].fMax);
		}

		CalcTaskMaxCost.calcTaskMaxCost(beta, taskList, processorsArray);


		GetMinCostProcessorIdLevel.getMinCostProcessorIdLevel(beta, taskList, processorsArray);
		//rank task by cost difference
		List<Map.Entry<Integer, Double>> taskDisCostList = GetDisCostTaskOrder.getDisCostTaskOrder(taskList);

		double makespan = CalcMakeSpan.calcMakeSpan(taskList);
		
		//System.out.println(makespan);
		return makespan;
	}
	/**
	 * @param args
	 */
	public static void main(String[] args)throws IOException {
		int taskNums = 53;
		double beta = 0.4;
		String dirPath = "";//D:\\workspaces\\FrequenceHEFT\\
		String graphModelName = "Airsn";
		String inputGraphPath = dirPath +graphModelName+ "transfer.txt";
		int processorNum=3;
		int pricelModel=2;
		double maxTimeParameter=1.5;
		String Path="1.txt";
		File File = new File(Path);
		PrintWriter PWcfmax=new PrintWriter(File, "utf-8");
		String processorInfor = dirPath + processorNum + ".txt";
		String computationCostPath = dirPath + graphModelName + "runtime.txt";
		HEFTMakespan(maxTimeParameter,processorNum, taskNums, beta, computationCostPath,
				inputGraphPath, processorInfor, 2,0);

	}

}
