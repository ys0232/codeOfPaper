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
public class MAXmin {
	public static void MaxMin_CFMax(int processorNums, int taskNums, double beta, int priceModel,
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
		HashMap<Integer, String> schedulerList;//record scheduled list（taskid，processor_frequency）
		schedulerList = MaxMin(processorNums, taskNums, taskList, taskEdgeHashMap, processorsArray,taskOrderList,beta);//use max-min to get a schedule list


		double localmakespan = Double.MIN_VALUE;
		for (int i = 0; i < processorNums; i++) {
			if (localmakespan < processorsArray[i].availableTime) {
				localmakespan = processorsArray[i].availableTime;
			}
		}
		double maxCost = CalaCostofAll.calcCostofAll(taskList, processorsArray, beta);

		//System.out.println("MAX-MIN makespan: " + localmakespan + "\tCost: " + maxCost);
		//System.out.println(localmakespan + "\t" + maxCost);


		double makespan=CalcAllMakespans.calcmakespans(processorNums, taskNums, beta, priceModel, computationCostPath, inputGraphPath, processorInfor, maxTimeParameter, starttime);
		
		DecimalFormat df = new DecimalFormat("#.00");
		PWdm.write( df.format(localmakespan) + "\t" + df.format(maxCost/1e5) + "\t");
		PWdm.flush();
		///System.out.println("Ahahahahhah delete"+makespan);
		CFMax.runCFMax(processorNums, processorsArray, taskList, taskNums, beta, taskEdgeHashMap,
				maxTimeParameter, starttime, schedulerList, maxCost, makespan,PWdm,taskOrderList);


	}
	public static void MaxMin_CFMin(int processorNums, int taskNums, double beta, int priceModel,
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
		HashMap<Integer, String> schedulerList;//record scheduled list（taskid，processor_frequency）
		schedulerList = MaxMin(processorNums, taskNums, taskList, taskEdgeHashMap, processorsArray,taskOrderList,beta);//use max-min to get a schedule list

		String Path = "1.txt";
		File File = new File(Path);
		PrintWriter PWcfmin = new PrintWriter(File, "utf-8");

		double localmakespan = Double.MIN_VALUE;
		for (int i = 0; i < processorNums; i++) {
			if (localmakespan < processorsArray[i].availableTime) {
				localmakespan = processorsArray[i].availableTime;
			}
		}
		double maxCost = CalaCostofAll.calcCostofAll(taskList, processorsArray, beta);
		//System.out.println("MAX-MIN makespan " + localmakespan + "\tmaxcost is: " + maxCost);
		double makespan=CalcAllMakespans.calcmakespans(processorNums, taskNums, beta, priceModel, computationCostPath, inputGraphPath, processorInfor, maxTimeParameter, starttime);
		//System.out.println(makespan);
		DecimalFormat df = new DecimalFormat("#.00");
		PWdm.write( df.format(localmakespan) + "\t" + df.format(maxCost/1e5) + "\t");
		PWdm.flush();
		CFMin.runCFMin(processorNums, processorsArray, taskList, taskNums, beta, taskEdgeHashMap,
				maxTimeParameter, starttime, schedulerList, maxCost, makespan,PWcfmin,taskOrderList);


	}
	public static HashMap<Integer, String> MaxMin(int processorNums, int taskNums, ArrayList<Task> taskList, HashMap<String, Double> taskEdgeHashMap,
			Processor[] processorsArray,ArrayList<Integer> taskOrderList,double beta) {
		double[][] CompletionTime = new double[taskNums][processorNums];
		HashMap<Integer, String> schedulerList = new HashMap<>();

		boolean[] unscheduledTask = new boolean[taskNums];
		double[] taskMinCTime = new double[taskNums];
		int[] proMinCTime = new int[taskNums];
		int unscheduledNum = taskNums;
		for (int i = 0; i < taskNums; i++) {
			//unscheduledTaskList is used to judge whether a task is scheduled or not
			unscheduledTask[i] = true;
		}
		int[] completedParent = new int[taskNums];
		double[] transferTime = new double[taskNums];
		while (unscheduledNum > 0) {
			// System.out.println("unScheduled task set is :\n"+ Arrays.toString(unscheduledTask));
			for (int i = 0; i < taskNums; i++) {
				Task task = taskList.get(i);
				if (!unscheduledTask[i] || task.predecessorTaskList.size() - completedParent[i] > 0) {
					continue;
				}
				taskMinCTime[i] = Double.MAX_VALUE;
				for (int j = 0; j < processorNums; j++) {
					Processor pro = processorsArray[j];
					double Eij = task.computationCost.get(j);
					ArrayList<Integer> pareTask=task.predecessorTaskList;
					transferTime[i]=0;
					for (int pare:pareTask){
						Task paretask=taskList.get(pare);
						double trans=0;
						if (paretask.selectedProcessorId!=j){
							trans=taskEdgeHashMap.get(String.valueOf(pare + "_" + i))+paretask.timeGap.endTime;
						}else {
							trans=paretask.timeGap.endTime;
						}
						if (transferTime[i]<trans){
							transferTime[i]=trans;
						}
					}
					//System.out.println(transferTime[i]+"\t"+i);
					CompletionTime[i][j] =  Eij +Math.max(pro.availableTime, transferTime[i]);

				// Task task = taskList.get(i);
					if (taskMinCTime[i] > CompletionTime[i][j]) {
						taskMinCTime[i] = CompletionTime[i][j];
						proMinCTime[i] = j;
					}
				}
				//System.out.println(Arrays.toString(CompletionTime[i]));
			}
			double maxCompletionTime =-1;
			int minTp = -1;
			for (int taskid = 0; taskid < taskNums; taskid++) {
				//find the task tp with earliest completion time
				Task task = taskList.get(taskid);
				if (!unscheduledTask[taskid] || task.predecessorTaskList.size() - completedParent[taskid] > 0) {
					continue;
				}
				//System.out.println(maxCompletionTime+"\t"+ Arrays.toString(taskMinCTime));
				if (maxCompletionTime < taskMinCTime[taskid]) {
					maxCompletionTime = taskMinCTime[taskid];
					minTp = taskid;
				}
			}
			/*     for (int id=0;id<processorNums;id++)
                System.out.print(processorsArray[id].availableTime+"\t");
            System.out.println();*/
			// assign task tp and update the ready time of assigned processor
			int proID = proMinCTime[minTp];
			processorsArray[proID].availableTime = maxCompletionTime;
			Task tp = taskList.get(minTp);
			tp.selectedFre = processorsArray[proID].fMax;
			tp.selectedProcessorId = proID;
			schedulerList.put(minTp, proID + "_" + tp.selectedFre);
			// tp.isCompleted=true;//now this task is completed
			for (int i = 0; i < taskNums; i++) {
				// remove completed task
				Task task = taskList.get(i);
				if (task.predecessorTaskList.contains(minTp)) {
					transferTime[i] += taskEdgeHashMap.get(String.valueOf(minTp + "_" + i));
					completedParent[i] += 1;
				}
			}

			// delete task tp from unscheduledTask
			taskOrderList.add(minTp);
			unscheduledTask[minTp] = false;
			unscheduledNum -= 1;
			newtestUpdate.testUpdate(taskList, processorsArray, minTp, schedulerList, taskEdgeHashMap, beta,taskOrderList);
	/*		for (int id=0;id<processorNums;id++)
				System.out.print(processorsArray[id].availableTime+"\t");
			System.out.println();
*/
			//    System.out.println("remove task : "+minTp+"\tassigned processor :"+proMinCTime[minTp]
			//          +"\nminCompletionTime is :"+maxCompletionTime+"\tcost is : "+cost);
		}
		System.out.println("+++++++++++++++++++++++++++\nMax_MIN SCHEDULER\n");
		for (Integer taskid : taskOrderList) {
	        Task tp=taskList.get(taskid);
	        System.out.println("task_id \t"+taskid+"\t"+schedulerList.get(taskid)+"\tstart time\t"+tp.timeGap.startTime+"\tend time:\t"+tp.timeGap.endTime);
	        
	        }
		
		System.out.println("MAX_MIN schedulerList:\t"+schedulerList.toString());
		return schedulerList;
	}

	public static void main(String[] args) throws IOException {

		int taskNums = 53;
		double beta = 0.4;
		String dirPath = "";//D:\\workspaces\\FrequenceHEFT\\
		String graphModelName = "Airsn";
		String inputGraphPath = dirPath + graphModelName + "transfer.txt";
		int processorNum = 3;
		int priceModel = 2;
		double maxTimeParameter=1.5;
		String Path = "1.txt";
		File File = new File(Path);
		PrintWriter PWcfmax = new PrintWriter(File, "utf-8");
		PrintWriter PWdm = new PrintWriter(File, "utf-8");
		String processorInfor = dirPath + processorNum + ".txt";
		String computationCostPath = dirPath + graphModelName + "runtime.txt";
		MAXmin.MaxMin_CFMax(processorNum, taskNums, beta, priceModel, computationCostPath, inputGraphPath, processorInfor,PWdm,maxTimeParameter, 0);
		MaxMin_CFMin(processorNum, taskNums, beta, priceModel, computationCostPath, inputGraphPath, processorInfor,PWdm, maxTimeParameter, 0);

	}
}

