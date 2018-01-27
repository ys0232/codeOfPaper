package initialSchedulingAlgorithm;

import taskscheduling.*;
import taskscheduling.util.*;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by lenovo on 2017/12/28.
 */
public class CFMax {
	public static void runCFMax(int processorNums, Processor[] processorsArray, ArrayList<Task> taskList, int taskNums, double beta,
			HashMap<String, Double> taskEdgeHashMap, double maxTimeParameter, long starttime,
			HashMap<Integer, String> schedulerList, double sumCost, double makespan,PrintWriter PWcfmax,ArrayList<Integer> taskOrderList) throws IOException {
		
		
		double maxTime = makespan * maxTimeParameter;//ä¼°è®¡çš„æˆªæ­¢æ—¶é—´
		//System.out.println("Deadline:"+maxTime);
		
		
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
		//è¾“å‡ºHEFTæ±‚å¾—çš„æœ€å¤§CPUé¢‘çŽ‡ä¸‹çš„æ‰§è¡Œæ—¶é—´ä¸Žè®¡ç®—è´¹ç”¨
		//compute sum cost and makespan,deadline(maxTime)
//		double maxTime = makespan * maxTimeParameter;//ä¼°è®¡çš„æˆªæ­¢æ—¶é—´
//		System.out.println("Deadline:"+maxTime);
		//System.out.println("Deadline: "+maxTime);
		int[] parentNum=new int[taskNums];
		
		double[][] RW = new double[taskNums][cfCount];
		boolean flag;
		HashMap<Integer, String> bestList = new HashMap<>();//è®°å½•æ¯�ä¸ªtaskæœ€å¤§RWçš„i,jï¼ˆtaskåº�å�·ï¼Œi_jï¼‰
		HashMap<Integer, String> jinji = new HashMap<>();//è®°å½•å·²ç»�å¾—åˆ°ä½†ä¸�æ»¡è¶³æ—¶é—´è¦�æ±‚çš„è°ƒåº¦
		double[] CFMax = new double[taskNums];

		//è®¡ç®—RWçŸ©é˜µï¼Œå¹¶ä¸”å¯¹æ¯�ä¸€ä¸ªä»»åŠ¡æ±‚ä¸€ä¸ªRWæœ€å¤§çš„è°ƒåº¦
		String tmp;
		flag =  computeRW.computeNew(RW, beta, schedulerList, cpuFreq, processorsArray, taskList, processorNums, bestList, jinji, CFMax,parentNum);
		double lastTime = makespan;
		double lastSumCost = sumCost;
		boolean t = true;
		double tcount = 0;
		//   PrintWriter pw;
		//  pw=new PrintWriter("./outputDir/runtime_cfmax"+sumCost+".txt");
		// pw.write("tcount: \t cost:\n");
		//  pw.flush();
	taskOrderList.clear();
		while (flag && t) {
			long nowtime = System.currentTimeMillis();
			long difftime = (nowtime - starttime) / 500;
			if (difftime > tcount) {
				tcount = difftime;
				// System.out.println(lastSumCost);
				//  pw.write(tcount+"\t"+lastSumCost+"\n");
				// pw.flush();
			}

			double max = 0;
			int tm = -1;

			for (int i = 0; i < taskNums; i++) {
				if (max < CFMax[i]) {
					max = CFMax[i];
					tm = i;
				}
			}
			//System.out.println(tm);
			if (tm != -1) {
				tmp = tm + "," + bestList.get(tm);
				String[] str = tmp.split(",");
				int taskid = Integer.valueOf(str[0]);
				str = str[1].split("_");
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

				String[] string = newtestUpdate.newtestUpdate(taskList, processorsArray, tm, bestList, taskEdgeHashMap, beta).split("_");

				sumCost = Double.valueOf(string[1].trim());
				double time = Double.valueOf(string[0].trim());
				//   System.out.println(maxTime + " and " + time);
				//   System.out.println("BESTid : "+tm+"\t"+bestList.get(tm));
				//  System.out.println(" runtime is: " + time +"maxTime is: "+maxTime +" all cost is: " + sumCost);

				if (maxTime < time || ((lastTime <= time) && (lastSumCost <= sumCost))) {
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
						jinji.put(taskid, jinji.get(taskid) + "," + str[0] + "_" + str[1]);//åŠ å…¥ç¦�å¿Œ
					}
				} else {
					lastSumCost = sumCost;
					lastTime = time;
					schedulerList.put(taskid, str[0] + "_" + str[1]);
					taskOrderList.add(taskid);
					Task tp=taskList.get(taskid);
					for (int id:tp.successorTaskList){
						parentNum[id]+=1;
						// System.out.println(id+"\t"+parentNum[id]+"\t"+taskList.get(id).predecessorTaskList.size());
					}
					//System.out.println(" runtime is: " +lastTime  + " all cost is: " + lastSumCost);
				}

			} else {
				flag = false;
			}
			//System.out.println(bestList);
			if (flag)
				computeRW.computeNew(RW, beta, schedulerList, cpuFreq, processorsArray, taskList, processorNums, bestList, jinji, CFMax,parentNum);

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
		System.out.println("+++++++++++++++++++++++++++\nCFMAX SCHEDULER\n");
		for (Integer taskid : taskOrderList) {
	        Task tp=taskList.get(taskid);
	        System.out.println("task_id \t"+taskid+"\t"+schedulerList.get(taskid)+"\tstart time\t"+tp.timeGap.startTime+"\tend time:\t"+tp.timeGap.endTime);
	        
	        }	
		
		
		double makespanCFMax = CalcMakeSpan.calcMakeSpan(taskList);
		double sumCostCFMax = CalaCostofAll.calcCostofAll(taskList, processorsArray, beta);

		//  System.out.println(maxTime + " and " + makespanCFMax);
		
		
		System.out.println( makespanCFMax + "\t" + sumCostCFMax);
		// System.out.println(schedulerList);
		
		
		
		System.out.println("CFMAX schedulerList:\t"+schedulerList.toString());
		System.out.println("CFMax makespan: " + makespanCFMax + "\tCost:" + sumCostCFMax);
		DecimalFormat df = new DecimalFormat("#.00");
		  PWcfmax.write( df.format(makespanCFMax) + "\t" + df.format(sumCostCFMax/1e5) + "\t");
		PWcfmax.flush();
	}
}
