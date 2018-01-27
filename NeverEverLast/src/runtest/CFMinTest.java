package runtest;

import initialSchedulingAlgorithmJustmakespan.CalcAllMakespans;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import taskscheduling.CalaCostofAll;
import taskscheduling.Processor;
import taskscheduling.Task;
import taskscheduling.TimeGap;
import taskscheduling.newtestUpdate;
import taskscheduling.util.CalcArrayListSum;
import taskscheduling.util.CalcMakeSpan;
import taskscheduling.util.CalcTaskMaxCost;
import taskscheduling.util.CalcTaskRankValue;
import taskscheduling.util.GetDisCostTaskOrder;
import taskscheduling.util.GetMinCostProcessorIdLevel;
import taskscheduling.util.TaskScheduling;
import taskscheduling.util.computeRW;

public class CFMinTest {
	public static void runHEFT(double maxTimeParameter, int processorNums,
			int taskNums, double beta, String computationCostPath,
			String inputGraphPath, String processorInfor, PrintWriter PWcfmax,
			int priceModel, long starttime) throws IOException {

		// the class used for initialing
		SchedulingInit sInit = new SchedulingInit();
		// initial task info
		ArrayList<Task> taskList = sInit.initTaskInfor(computationCostPath,
				inputGraphPath);
		// initial communication data
		HashMap<String, Double> taskEdgeHashMap = sInit.initTaskEdge();

		// initial processor info
		Processor[] processorsArray;
		processorsArray = sInit.initProcessorInfor(processorInfor,
				processorNums, priceModel);

		// compute average computation time using for the
		// 
		for (int i = 0; i < taskNums; ++i) {
			Task tempTask = taskList.get(i);
			taskList.get(i).averageCost = CalcArrayListSum
					.calcArrayListSum(tempTask.computationCost) / processorNums;
		}

		// 
		int cfCount = 0;
		int freqcount = 0;
		for (int i = 0; i < processorNums; i++) {
			int tmp = (processorsArray[i].fMax - processorsArray[i].fMin)
					/ processorsArray[i].fLevel + 1;
			if (tmp > freqcount) {
				freqcount = tmp;
			}
		}
		int[][] cpuFreq = new int[processorNums][freqcount];
		// System.out.println(processorNums+"\t"+freqcount);
		for (int i = 0; i < processorNums; i++) {
			for (int j = 0; processorsArray[i].fMin + j
					* processorsArray[i].fLevel <= processorsArray[i].fMax; j++) {
				cpuFreq[i][j] = processorsArray[i].fMin + j
						* processorsArray[i].fLevel;
				cfCount++;
			}
		}
		// for (int i = 0; i < processorNums; i++)
		// System.out.println(Arrays.toString(cpuFreq[i]));
		HashMap<Integer, String> schedulerList = new HashMap<>();

		// compute rank,then
		ArrayList<Integer> taskOrderList = CalcTaskRankValue
				.calcTaskRankValue1(taskList, taskEdgeHashMap, taskNums);

		// scheduling using
		System.out.println("===============================\nHEFT SCHEDULER\n");
		for (Integer taskId : taskOrderList) {
			// System.out.print(taskId+"\t");
			TaskScheduling.taskScheduling(taskId, taskList, taskEdgeHashMap,
					processorsArray);
			Task tmp = taskList.get(taskId);
			schedulerList.put(taskId, tmp.selectedProcessorId + "_"
					+ processorsArray[tmp.selectedProcessorId].fMax);
			
			  System.out.println("task_id \t"+taskId+"\t"+schedulerList.get(taskId)+"\tstart time\t"+tmp.timeGap.startTime+"\tend time:\t"+tmp.timeGap.endTime);
	            
		}
		CalcTaskMaxCost.calcTaskMaxCost(beta, taskList, processorsArray);

		// compute min cost in all
		GetMinCostProcessorIdLevel.getMinCostProcessorIdLevel(beta, taskList,
				processorsArray);
		// rank task by cost difference
		List<Map.Entry<Integer, Double>> taskDisCostList = GetDisCostTaskOrder
				.getDisCostTaskOrder(taskList);
		// compute sum cost and makespan,deadline(maxTime)
		double sumCost = CalaCostofAll.calcCostofAll(taskList, processorsArray,	beta);
		double localmakespan = CalcMakeSpan.calcMakeSpan(taskList);
		
		//*******************************************
		//*******************************************
	//	double makespan=CalcAllMakespans.calcmakespans(processorNums, taskNums, beta, priceModel, computationCostPath, inputGraphPath, processorInfor, maxTimeParameter, starttime);//makespan with ay calculations
		//System.out.println("Maximin Makespan: "+makespan);
		//double maxTime = makespan * maxTimeParameter;
		double maxTime = localmakespan * maxTimeParameter;
		//System.out.println("Deadline:"+maxTime);
		//System.out.println("Deadline: "+maxTime);
		//*********************************************
		//*******************************************
		System.out.println("HEFT schedulerList:\t"+schedulerList.toString()+"\n"+localmakespan+"\t"+sumCost);
		
		//double maxTime = makespan * maxTimeParameter;
		//System.out.println("HEFT \tmakespan " + localmakespan + "\tsumCost: " +sumCost);

		double[][] RW = new double[taskNums][cfCount];
		boolean flag;
		HashMap<Integer, String> bestList = new HashMap<>();
		HashMap<Integer, String> jinji = new HashMap<>();
		double[] CFMin = new double[taskNums];
		int[] parentNum=new int[taskNums];
		String tmp;
		flag = computeRW.computeMIN(RW, beta, schedulerList, cpuFreq,
				processorsArray, taskList, processorNums, bestList, jinji,
				CFMin,parentNum);
		double lastTime = localmakespan;
		double lastSumCost = sumCost;
		boolean t = true;
		double tcount = 0;
		PrintWriter pw;

		// pw=new PrintWriter("./outputDir/runtime_cfmax"+sumCost+".txt");
		// pw.write("tcount: \t cost:cfminheft\n");
		// pw.flush();
		taskOrderList.clear();
		while (flag && t) {

			long nowtime = System.currentTimeMillis();
			long difftime = (nowtime - starttime) / 500;
			if (difftime > tcount) {
				tcount = difftime;
				// System.out.println(lastSumCost);
				// pw.write(tcount+"\t"+lastSumCost+"\n");
				// pw.flush();
			}


			double min = Double.MAX_VALUE;
			int tm = -1;
			for (int i = 0; i < taskNums; i++) {
				if (min > CFMin[i]) {
					min = CFMin[i];
					tm = i;
				}
			}
			// System.out.println(Arrays.toString(CFMin));
			if (tm != -1) {
				tmp = tm + "," + bestList.get(tm);
				String[] str = tmp.split(",");
				int taskid = Integer.valueOf(str[0]);
				str = str[1].split("_");
				// / Task task = taskList.get(taskid);
				Task[] oldTaskInfor = new Task[taskList.size()];
				for (int i = 0; i < taskList.size(); ++i) {
					oldTaskInfor[i] = new Task();
					oldTaskInfor[i].selectedProcessorId = taskList.get(i).selectedProcessorId;
					oldTaskInfor[i].selectedFre = taskList.get(i).selectedFre;
					oldTaskInfor[i].timeGap = new TimeGap();
					oldTaskInfor[i].timeGap.startTime = taskList.get(i).timeGap.startTime;
					oldTaskInfor[i].timeGap.endTime = taskList.get(i).timeGap.endTime;
					oldTaskInfor[i].timeGap.gap = taskList.get(i).timeGap.gap;
				}

				String[] string = newtestUpdate.testUpdate(taskList,
						processorsArray, tm, bestList, taskEdgeHashMap, beta,taskOrderList)
						.split("_");

				sumCost = Double.valueOf(string[1].trim());
				double time = Double.valueOf(string[0].trim());
				// System.out.println(maxTime + " and " + time);
				// System.out.println("BESTid : " + tm + "\t" +
				// bestList.get(tm));
				// System.out.println(" runtime is: " + time
				// +"maxTime is: "+maxTime +" all cost is: " + sumCost);

				if (maxTime < time
						|| ((lastTime <= time) && (lastSumCost <= sumCost))) {
					for (int i = 0; i < taskList.size(); ++i) {
						taskList.get(i).selectedProcessorId = oldTaskInfor[i].selectedProcessorId;
						taskList.get(i).selectedFre = oldTaskInfor[i].selectedFre;
						taskList.get(i).timeGap.startTime = oldTaskInfor[i].timeGap.startTime;
						taskList.get(i).timeGap.endTime = oldTaskInfor[i].timeGap.endTime;
						taskList.get(i).timeGap.gap = oldTaskInfor[i].timeGap.gap;
					}
					boolean isStrIn = false;
					if (jinji.keySet().contains(taskid)) {
						String[] strs = jinji.get(taskid).split(",");

						for (int i = 0; i < strs.length; i++) {
							if (strs[i].equals(str[0] + "_" + str[1])) {
								isStrIn = true;
							}
						}
					}
					if (!isStrIn) {
						jinji.put(taskid, jinji.get(taskid) + "," + str[0]
								+ "_" + str[1]);// åŠ å…¥ç¦�å¿Œ
					}

				} else {
					lastSumCost = sumCost;
					lastTime = time;
					schedulerList.put(taskid, str[0] + "_" + str[1]);
					newtestUpdate.newtestUpdate(taskList,processorsArray, tm, bestList, taskEdgeHashMap, beta);
					if (!taskOrderList.contains(taskid)) {
						taskOrderList.add(taskid);
						Task task = taskList.get(taskid);
						for (int id : task.successorTaskList) {
							parentNum[id] += 1;
							//System.out.println(id+"\t"+parentNum[id]+"\t"+taskList.get(id).predecessorTaskList.size());
						}
					}
					// System.out.println(" runtime is: " +lastTime +
					// " all cost is: " + lastSumCost);
				}

			} else {
				flag = false;
			}
			// System.out.println(jinji);
			if (flag)
				computeRW.computeMIN(RW, beta, schedulerList, cpuFreq,
						processorsArray, taskList, processorNums, bestList, jinji,
						CFMin,parentNum);

			for (int i = 0; i < taskNums; i++) {
				if (jinji.keySet().contains(i)) {
					t = false;
					String[] strings = jinji.get(i).split(",");
					if (strings.length != (cfCount + 1)) {
						t = true;
						break;
					}
				}

			}

		}
		/*
		 * for (int i = 0; i < taskNums; i++) { Task task=taskList.get(i);
		 * System
		 * .out.println(i+"   "+task.selectedProcessorId+" "+task.selectedFre);
		 * }
		 */
		System.out.println("+++++++++++++++++++++++++++++++\nCFMin_HEFT SCHEDULER\n");
		for (Integer taskid : taskOrderList) {
	        Task tp=taskList.get(taskid);
	        System.out.println("task_id \t"+taskid+"\t"+schedulerList.get(taskid)+"\tstart time\t"+tp.timeGap.startTime+"\tend time:\t"+tp.timeGap.endTime);
	        
	        }
		System.out.println("CFMIN_HFET schedulerList:\t"+schedulerList.toString());
		double makespanCFMax = CalcMakeSpan.calcMakeSpan(taskList);
		double sumCostCFMax = CalaCostofAll.calcCostofAll(taskList,
				processorsArray, beta);
		// System.out.println(maxTime + " and " + makespanCFMax);
		// ************************************************************************
		System.out.println(makespanCFMax + "\t" + sumCostCFMax);
		// *************************************************************************************
		//System.out.println("CFMin Makespan: " + makespanCFMax + "\tCost: " + sumCostCFMax);
		// ************************************************************************************
		 DecimalFormat df = new DecimalFormat("#.00");
		 PWcfmax.write( df.format(makespanCFMax) + "\t" + df.format(sumCostCFMax/1e5) + "\n");
		 PWcfmax.flush();
	}

	public static void main(String[] args) throws IOException {
		String inputdir = "./input/";

		String outputdir = "./0728output/";

		String inputDAG=inputdir+"testDAG/";
		String inputGraphPath = inputDAG +  "DAGtransfer.txt";

		int priceModel=2;
		String Path = outputdir +"testDAG/" + priceModel + ".txt";
		File File = new File(Path);
		PrintWriter PWcfmax = new PrintWriter(File, "utf-8");

		int processorNums=3;double maxTimeParameter=3.0;int taskNums=10;double beta=0.4;

		String computationCostPath = inputDAG +  "DAGruntime.txt";
		String processorInfor =inputDAG +  "resource.txt";
		CFMaxTest.runHEFT(maxTimeParameter, processorNums, taskNums, beta, computationCostPath, inputGraphPath, processorInfor, PWcfmax, priceModel, 0);

		CFMinTest.runHEFT(maxTimeParameter, processorNums, taskNums, beta, computationCostPath, inputGraphPath, processorInfor, PWcfmax, priceModel, 0);



	}
}