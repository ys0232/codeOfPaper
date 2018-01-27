package initialSchedulingAlgorithm;

import runtest.CFMaxTest;
import runtest.SchedulingInit;
import taskscheduling.CalaCostofAll;
import taskscheduling.Processor;
import taskscheduling.Task;
import taskscheduling.newtestUpdate;
import taskscheduling.util.CalcArrayListSum;
import taskscheduling.util.CalcCost;
import taskscheduling.util.CalcRunTime;

import initialSchedulingAlgorithmJustmakespan.CalcAllMakespans;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

/**
 * Created by lenovo on 2017/12/22.
 */
public class MINMIN {
	public static void Minmin_CFMax(int processorNums, int taskNums, double beta, int priceModel,
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

		schedulerList = Minmin(processorNums, taskNums, taskList, taskEdgeHashMap, processorsArray,taskOrderList,beta);//use min-min to get a schedule list
		

		double localmakespan = Double.MIN_VALUE;
		for (int i = 0; i < processorNums; i++) {
			if (localmakespan < processorsArray[i].availableTime) {
				localmakespan = processorsArray[i].availableTime;
			}
		}
		double maxCost = CalaCostofAll.calcCostofAll(taskList, processorsArray, beta);

		System.out.println("MIN-MIN makespan: " + localmakespan + "\tCost: " + maxCost);
		DecimalFormat df = new DecimalFormat("#.00");
		PWdm.write( "min_min makespan is:\t"+df.format(localmakespan) + "\t" +"min_min cost is:\t"+ df.format(maxCost/1e5) + "\n");
		PWdm.flush();
		
		//System.out.println(localmakespan + "\t" + maxCost);
		
		double makespan=CalcAllMakespans.calcmakespans(processorNums, taskNums, beta, priceModel, computationCostPath, inputGraphPath, processorInfor, maxTimeParameter, starttime);
		
		CFMax.runCFMax(processorNums, processorsArray, taskList, taskNums, beta, taskEdgeHashMap,
				maxTimeParameter, starttime, schedulerList, maxCost, makespan,PWdm,taskOrderList);
	}
	public static void Minmin_CFMin(int processorNums, int taskNums, double beta, int priceModel,
			String computationCostPath, String inputGraphPath, String processorInfor,
			PrintWriter PWdm ,double maxTimeParameter, long starttime) throws IOException {

		//the class used for initialing
		SchedulingInit sInit = new SchedulingInit();
		//initial task info
		ArrayList<Task> taskList = sInit.initTaskInfor(computationCostPath, inputGraphPath);
		ArrayList<Integer> taskOrderList=new ArrayList<Integer>();
		//initial communication data
		HashMap<String, Double> taskEdgeHashMap = sInit.initTaskEdge();
		//initial processor info
		Processor[] processorsArray;
		processorsArray = sInit.initProcessorInfor(processorInfor, processorNums, priceModel);
		HashMap<Integer, String> schedulerList;//record scheduled list（taskid，processor_frequency）

		schedulerList = Minmin(processorNums, taskNums, taskList, taskEdgeHashMap, processorsArray,taskOrderList,beta);//use min-min to get a schedule list

		
		double localmakespan = Double.MIN_VALUE;
		for (int i = 0; i < processorNums; i++) {
			if (localmakespan < processorsArray[i].availableTime) {
				localmakespan = processorsArray[i].availableTime;
			}
		}
		double maxCost = CalaCostofAll.calcCostofAll(taskList, processorsArray, beta);
		System.out.println("MIN-MIN makespan is:" + localmakespan + "\tmaxcost is: " + maxCost);


		double makespan=CalcAllMakespans.calcmakespans(processorNums, taskNums, beta, priceModel, computationCostPath, inputGraphPath, processorInfor, maxTimeParameter, starttime);
		
		DecimalFormat df = new DecimalFormat("#.00");
		PWdm.write( "min_min makespan is:\t"+df.format(localmakespan) + "\t" +"min_min cost is:\t"+ df.format(maxCost/1e5) + "\n");
		PWdm.flush();
		System.out.println(localmakespan+"\t"+maxCost);
		CFMin.runCFMin(processorNums, processorsArray, taskList, taskNums, beta, taskEdgeHashMap,
				maxTimeParameter, starttime, schedulerList, maxCost, makespan,PWdm,taskOrderList);
	}
	public static HashMap<Integer, String> Minmin(int processorNums, int taskNums, ArrayList<Task> taskList,
			HashMap<String, Double> taskEdgeHashMap, Processor[] processorsArray,ArrayList<Integer> taskOrderList,double beta) throws IOException {
		HashMap<Integer, String> schedulerList = new HashMap<>();

		double[][] CompletionTime = new double[taskNums][processorNums];
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
					//check whether task is scheduled or not and all its parents tasks are scheduled or not.
					continue;
				}
				//    System.out.println(task.predecessorTaskList.size()+"\t"+completedParent[i]);

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
				//	System.out.println(transferTime[i]+"\t"+i);
					CompletionTime[i][j] =  Eij + Math.max(pro.availableTime, transferTime[i]);
					if (taskMinCTime[i] > CompletionTime[i][j]) {
						taskMinCTime[i] = CompletionTime[i][j];
						proMinCTime[i] = j;
					}
				}
			//	System.out.println(Arrays.toString(CompletionTime[i]));
			}
			//System.out.println(Arrays.toString(CompletionTime[i]));
			double minCompletionTime = Double.MAX_VALUE;
			int minTp = -1;
			for (int taskid = 0; taskid < taskNums; taskid++) {
				//find the task tp with earliest completion time
				Task task = taskList.get(taskid);
				if (!unscheduledTask[taskid] || task.predecessorTaskList.size() - completedParent[taskid] > 0) {
					continue;
				}
				if (minCompletionTime > taskMinCTime[taskid]) {
					minCompletionTime = taskMinCTime[taskid];
					minTp = taskid;
				}
			}
			//System.out.println(minCompletionTime+"====");
			/*    for (int id=0;id<processorNums;id++)
                System.out.print(processorsArray[id].availableTime+"\t");
            System.out.println();
*/
			// assign task tp and update the ready time of assigned processor
			int proID = proMinCTime[minTp];
			processorsArray[proID].availableTime = minCompletionTime;//update processor's available time
			Task tp = taskList.get(minTp);
			tp.selectedFre = processorsArray[proID].fMax; //update task selected frequence
			tp.selectedProcessorId = proID; //update task selected processor ,both of them are used to compute total cost
			//tp.isCompleted = true;//now this task is completed
			schedulerList.put(minTp, proID + "_" + tp.selectedFre);
			for (int i = 0; i < taskNums; i++) {
				// remove completed task
				Task task = taskList.get(i);
				if (task.predecessorTaskList.contains(minTp)) {
					//transferTime[i] += taskEdgeHashMap.get(String.valueOf(minTp + "_" + i));
					// data from parent task sends to child task needed time is stored in transferTime.
					completedParent[i] += 1;

				}
			}

			// delete task tp from unscheduledTask
			taskOrderList.add(minTp);
			unscheduledTask[minTp] = false;
			unscheduledNum -= 1;
			newtestUpdate.testUpdate(taskList, processorsArray, minTp, schedulerList, taskEdgeHashMap, beta,taskOrderList);
		/*	for (int id=0;id<processorNums;id++)
				System.out.print(processorsArray[id].availableTime+"\t");
			System.out.println();*/
			
			/*    System.out.println("remove task : "+minTp+"\tassigned processor :"+proMinCTime[minTp]
			 *          +"\nminCompletionTime is :"+minCompletionTime+"\tcost is : "+cost);
			 */
		}
		System.out.println("+++++++++++++++++++++++++++\nMIN_MIN SCHEDULER\n");
		for (Integer taskid : taskOrderList) {
	        Task tp=taskList.get(taskid);
	        System.out.println("task_id \t"+taskid+"\t"+schedulerList.get(taskid)+"\tstart time\t"+tp.timeGap.startTime+"\tend time:\t"+tp.timeGap.endTime);
	        
	        }
		
		System.out.println("MIN_MIN schedulerList:\t"+schedulerList.toString());
		return schedulerList;
	}

	public static void main(String[] args) throws IOException {

		int taskNums = 53;
		double beta = 0.4;
		String dirPath = "";//D:\\workspaces\\FrequenceHEFT\\
		String graphModelName = "Airsn";
		String inputGraphPath = dirPath + graphModelName + "transfer.txt";
		int processorNum = 3;
		int pricelModel = 2;
		String processorInfor = dirPath + processorNum + ".txt";
		String computationCostPath = dirPath + graphModelName + "runtime.txt";
		double maxTimeParameter = 1.5;

		String Path = "1.txt";
		File File = new File(Path);
		PrintWriter PWcfmax = new PrintWriter(File, "utf-8");
		PrintWriter PWd0m = new PrintWriter(File, "utf-8");

		Minmin_CFMax(processorNum, taskNums, beta, pricelModel, computationCostPath, inputGraphPath,processorInfor, PWd0m, maxTimeParameter, 0);
		MAXmin.MaxMin_CFMax(processorNum, taskNums, beta, pricelModel, computationCostPath, inputGraphPath, processorInfor,PWd0m, maxTimeParameter, 0);
		CFMaxTest.runHEFT(maxTimeParameter, processorNum, taskNums, beta, computationCostPath,
				inputGraphPath, processorInfor, PWcfmax, pricelModel, 0);

	}
}
