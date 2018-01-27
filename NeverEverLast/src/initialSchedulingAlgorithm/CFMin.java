package initialSchedulingAlgorithm;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import taskscheduling.CalaCostofAll;
import taskscheduling.Processor;
import taskscheduling.Task;
import taskscheduling.TimeGap;
import taskscheduling.newtestUpdate;
import taskscheduling.util.CalcMakeSpan;
import taskscheduling.util.computeRW;

public class CFMin {
	public static void runCFMin(int processorNums, Processor[] processorsArray, ArrayList<Task> taskList, int taskNums, double beta,

			HashMap<String, Double> taskEdgeHashMap, double maxTimeParameter, long starttime,
			HashMap<Integer, String> schedulerList, double sumCost, double makespan,PrintWriter PWcfmin,ArrayList<Integer> taskOrderList) throws IOException {

		// System.out.println(schedulerList);
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
		//å¤„ç�†å™¨çš„cpué¢‘çŽ‡
		for (int i = 0; i < processorNums; i++) {
			for (int j = 0; processorsArray[i].fMin + j * processorsArray[i].fLevel <= processorsArray[i].fMax; j++) {
				cpuFreq[i][j] = processorsArray[i].fMin + j * processorsArray[i].fLevel;
				cfCount++;
			}
		}
		//compute sum cost and makespan,deadline(maxTime)
		double maxTime = makespan * maxTimeParameter;
		//System.out.println("Deadline:"+maxTime);
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
		double lastTime = makespan;
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
						/*for (Integer id : taskOrderList) {
							Task tp=taskList.get(id);
							System.out.println("task_id \t"+id+"\t"+schedulerList.get(id)+"\tstart time\t"+tp.timeGap.startTime+"\tend time:\t"+tp.timeGap.endTime);

						}
						System.out.println(taskid+"+++++++++==========++++++++++++++++");*/

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
		/*for (int i = 0; i < taskNums; i++) {
Task task=taskList.get(i);
System.out.println(i+"   "+task.selectedProcessorId+" "+task.selectedFre);
}*/
		System.out.println("+++++++++++++++++++++++++++\nCFMIN SCHEDULER\n");
		for (Integer taskid : taskOrderList) {
	        Task tp=taskList.get(taskid);
	        System.out.println("task_id \t"+taskid+"\t"+schedulerList.get(taskid)+"\tstart time\t"+tp.timeGap.startTime+"\tend time:\t"+tp.timeGap.endTime);
	        
	        }

		double makespanCFMax = CalcMakeSpan.calcMakeSpan(taskList);
		double sumCostCFMax = CalaCostofAll.calcCostofAll(taskList, processorsArray, beta);
		//  System.out.println(maxTime + " and " + makespanCFMax);
		//****************************************************************************
		//System.out.println("CFMin makespan: " + makespanCFMax + "\tCost: " + sumCostCFMax);
		//****************************************************************************
		//System.out.println(makespanCFMax + " \t" + sumCostCFMax);
		//******************************************************
		// System.out.println(schedulerList);
		System.out.println("CFMIN schedulerList:\t"+schedulerList.toString()+"\n"+makespanCFMax+"\t"+sumCostCFMax);
		
		DecimalFormat df = new DecimalFormat("#.00");
		PWcfmin.write( "cfmin makespan is:\t"+df.format(makespan) + "\t" +"cfmin cost is:\t"+ df.format(sumCostCFMax/1e5) + "\n");
		PWcfmin.flush();
	}
}