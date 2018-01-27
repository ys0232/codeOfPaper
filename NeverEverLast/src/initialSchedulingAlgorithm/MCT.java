package initialSchedulingAlgorithm;

import runtest.SchedulingInit;
import taskscheduling.CalaCostofAll;
import taskscheduling.Processor;
import taskscheduling.Task;
import taskscheduling.newtestUpdate;
import taskscheduling.util.CalcCost;

import initialSchedulingAlgorithmJustmakespan.CalcAllMakespans;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

/**
 * Created by lenovo on 2017/12/24.
 */
public class MCT {
	public static void Mct_CFMax(int processorNums, int taskNums, double beta, int priceModel,
			String computationCostPath, String inputGraphPath, String processorInfor,
			PrintWriter PWdm ,double maxTimeParameter, long starttime) throws IOException {

		//the class used for initialing
		SchedulingInit sInit = new SchedulingInit();
		//initial task info
		ArrayList<Task> taskList = sInit.initTaskInfor(computationCostPath, inputGraphPath);
		//initial communication data
		HashMap<String, Double> taskEdgeHashMap = sInit.initTaskEdge();
		ArrayList<Integer> taskOrderList=new ArrayList<Integer>();

		//initial processor info
		Processor[] processorsArray;
		processorsArray = sInit.initProcessorInfor(processorInfor, processorNums, priceModel);
		HashMap<Integer, String> schedulerList = MCTS(processorNums, taskNums, taskList, taskEdgeHashMap, processorsArray,taskOrderList,beta);

		//PrintWriter PWdm = new PrintWriter(File, "utf-8");
		double localmakespan = Double.MIN_VALUE;
		for (int i = 0; i < processorNums; i++) {
			if (localmakespan < processorsArray[i].availableTime) {
				localmakespan = processorsArray[i].availableTime;
			}
		}
		double maxCost = CalaCostofAll.calcCostofAll(taskList, processorsArray, beta);

		System.out.println("MCT makespan: " + localmakespan + "\tCost:  " + maxCost);
		//System.out.println( localmakespan + "\t" + maxCost);

		double makespan=CalcAllMakespans.calcmakespans(processorNums, taskNums, beta, priceModel, computationCostPath, inputGraphPath, processorInfor, maxTimeParameter, starttime);
		
		DecimalFormat df = new DecimalFormat("#.00");
		PWdm.write( df.format(localmakespan) + "\t" + df.format(maxCost/1e5) + "\t");
		PWdm.flush();
		CFMax.runCFMax(processorNums, processorsArray, taskList, taskNums, beta, taskEdgeHashMap,
				maxTimeParameter, starttime, schedulerList, maxCost, makespan,PWdm,taskOrderList);

	}
	public static void Mct_CFMin(int processorNums, int taskNums, double beta, int priceModel,
			String computationCostPath, String inputGraphPath, String processorInfor,
			PrintWriter PWdm ,double maxTimeParameter, long starttime) throws IOException {

		//the class used for initialing
		SchedulingInit sInit = new SchedulingInit();
		//initial task info
		ArrayList<Task> taskList = sInit.initTaskInfor(computationCostPath, inputGraphPath);
		//initial communication data
		HashMap<String, Double> taskEdgeHashMap = sInit.initTaskEdge();
		ArrayList<Integer> taskOrderList=new ArrayList<Integer>();

		//initial processor info
		Processor[] processorsArray;
		processorsArray = sInit.initProcessorInfor(processorInfor, processorNums, priceModel);
		HashMap<Integer, String> schedulerList = MCTS(processorNums, taskNums, taskList, taskEdgeHashMap, processorsArray,taskOrderList,beta);

		double localmakespan = Double.MIN_VALUE;
		for (int i = 0; i < processorNums; i++) {
			if (localmakespan < processorsArray[i].availableTime) {
				localmakespan = processorsArray[i].availableTime;
			}
		}
		double maxCost = CalaCostofAll.calcCostofAll(taskList, processorsArray, beta);
		System.out.println("MCT makespan is:" + localmakespan + "\tmaxcost is: " + maxCost);

		double makespan=CalcAllMakespans.calcmakespans(processorNums, taskNums, beta, priceModel, computationCostPath, inputGraphPath, processorInfor, maxTimeParameter, starttime);
		
		DecimalFormat df = new DecimalFormat("#.00");
		PWdm.write( df.format(localmakespan) + "\t" + df.format(maxCost/1e5) + "\t");
		PWdm.flush();
		CFMin.runCFMin(processorNums, processorsArray, taskList, taskNums, beta, taskEdgeHashMap,
				maxTimeParameter, starttime, schedulerList, maxCost, makespan,PWdm,taskOrderList);

	}
	public static HashMap<Integer, String> MCTS(int processorNums, int taskNums, ArrayList<Task> taskList, HashMap<java.lang.String, Double> taskEdgeHashMap,
			Processor[] processorsArray,ArrayList<Integer> taskOrderList,double beta) throws IOException {
		HashMap<Integer, String> schedulerList = new HashMap<>();
		double[][] CompletionTime = new double[taskNums][processorNums];
		double[] taskMinCTime = new double[taskNums];
		int[] proMinCTime = new int[taskNums];
		int unscheduledNum = taskNums;
		boolean[] unscheduledTask = new boolean[taskNums];
		for (int i = 0; i < taskNums; i++) {
			//unscheduledTaskList is used to judge whether a task is scheduled or not
			unscheduledTask[i] = true;
		}
		int[] completedParent=new int[taskNums];
		double[][] transferTime = new double[taskNums][processorNums];

		while (unscheduledNum > 0) {
			for (int i = 0; i < taskNums; i++) {
				Task task = taskList.get(i);
				taskMinCTime[i] = Double.MAX_VALUE;
				if (!unscheduledTask[i] || task.predecessorTaskList.size() - completedParent[i] > 0) {
					//check whether task is scheduled or not and all its parents tasks are scheduled or not.
					continue;
				}
				taskOrderList.add(i);
				for (int j = 0; j < processorNums; j++) {
					Processor pro = processorsArray[j];
					ArrayList<Integer> pareTask=task.predecessorTaskList;
					transferTime[i][j]=0;
					for (int pare:pareTask){
						Task paretask=taskList.get(pare);
						double trans=0;
						if (paretask.selectedProcessorId!=j){
							trans=taskEdgeHashMap.get(String.valueOf(pare + "_" + i))+paretask.timeGap.endTime;
						}else {
							trans=paretask.timeGap.endTime;
						}
						if (transferTime[i][j]<trans){
							transferTime[i][j]=trans;
						}
					}

					double Eij = task.computationCost.get(j);
					CompletionTime[i][j] = Math.max(processorsArray[j].availableTime, transferTime[i][j])+ Eij;
					//  System.out.print(pro.availableTime+"\t");
					if (taskMinCTime[i] > CompletionTime[i][j]) {
						taskMinCTime[i] = CompletionTime[i][j];
						proMinCTime[i] = j;
					}
				}
				//System.out.println(Arrays.toString(CompletionTime[i])+"\t"+i);
				Processor schedPro = processorsArray[proMinCTime[i]];
				schedPro.availableTime = taskMinCTime[i];
				// System.out.println("\n"+taskMinCTime[i]+"\t"+i);
				for (int k = 0; k < taskNums; k++) {
					// remove completed task
					Task tp = taskList.get(k);
					if (tp.predecessorTaskList.contains(i)) {
						//transferTime[k] += taskEdgeHashMap.get(String.valueOf(taskid + "_" + k));
						completedParent[k] += 1;
					}
				}
				task.selectedProcessorId=proMinCTime[i];
				task.selectedFre=schedPro.fMax;
				unscheduledTask[i] = false;
				unscheduledNum -= 1;
				schedulerList.put(i,proMinCTime[i]+"_"+task.selectedFre);
				newtestUpdate.testUpdate(taskList, processorsArray, i, schedulerList, taskEdgeHashMap, beta,taskOrderList);
			}
		}

//		for (int i=0;i<taskNums;i++){
//			Task tp=taskList.get(i);
//			int proID = proMinCTime[i];
//			tp.selectedFre = processorsArray[proID].fMax;
//			tp.selectedProcessorId = proID;
//			schedulerList.put(i,proID+"_"+tp.selectedFre);
//			newtestUpdate.newtestUpdate(taskList, processorsArray, i, schedulerList, taskEdgeHashMap, beta);
//		}
		System.out.println("+++++++++++++++++++++++++++\nMCT SCHEDULER\n");
		for (Integer taskid : taskOrderList) {
			Task tp=taskList.get(taskid);
			System.out.println("task_id \t"+taskid+"\t"+schedulerList.get(taskid)+"\tstart time\t"+tp.timeGap.startTime+"\tend time:\t"+tp.timeGap.endTime);

		}
		System.out.println("MCT schedulerList:\t"+schedulerList.toString());
		return schedulerList;

	}

	public static void main(String[] args) throws IOException {

		String inputdir = "./input/";

		String outputdir = "./0728output/";

		String inputDAG=inputdir+"testDAG/";
		String inputGraphPath = inputDAG +  "DAGtransfer.txt";

		int priceModel=1;
		String Path = outputdir +"testDAG/" + priceModel + ".txt";
		File File = new File(Path);
		PrintWriter PWcfmax = new PrintWriter(File, "utf-8");

		int processorNums=3;double maxTimeParameter=3.0;int taskNums=10;double beta=0.4;

		String computationCostPath = inputDAG +  "DAGruntime.txt";
		String processorInfor =inputDAG +  "resource.txt";
		Mct_CFMax(processorNums,taskNums,beta,priceModel,computationCostPath,inputGraphPath,processorInfor,PWcfmax,maxTimeParameter,0);
		Mct_CFMin(processorNums,taskNums,beta,priceModel,computationCostPath,inputGraphPath,processorInfor,PWcfmax,maxTimeParameter,0);

	}
}

