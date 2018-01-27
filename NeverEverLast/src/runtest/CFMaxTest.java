package runtest;

import initialSchedulingAlgorithmJustmakespan.CalcAllMakespans;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.util.*;

import taskscheduling.*;
import taskscheduling.util.*;


public class CFMaxTest {
    public static void runHEFT(double maxTimeParameter, int processorNums, int taskNums, double beta,
                               String computationCostPath, String inputGraphPath, String processorInfor,
                               PrintWriter PWdm,int priceModel,long starttime) throws IOException {

        //the class used for initialing
        SchedulingInit sInit = new SchedulingInit();
        //initial task info
        ArrayList<Task> taskList = sInit.initTaskInfor(computationCostPath, inputGraphPath);
        //initial communication data
        HashMap<String, Double> taskEdgeHashMap = sInit.initTaskEdge();

        //initial processor info
        Processor[] processorsArray;
        processorsArray = sInit.initProcessorInfor(processorInfor, processorNums,priceModel);

        //compute average computation time using for the rank
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

        //compute rank,then 
        ArrayList<Integer> taskOrderList = CalcTaskRankValue.calcTaskRankValue1(taskList, taskEdgeHashMap, taskNums);
        //scheduling using HEFT
        System.out.println("====================================\nHFET SCHEDULER\n");
        for (Integer taskId : taskOrderList) {
            // System.out.print(taskId+"\t");
            TaskScheduling.taskScheduling(taskId, taskList, taskEdgeHashMap, processorsArray);
            Task tmp = taskList.get(taskId);
            schedulerList.put(taskId , tmp.selectedProcessorId + "_" + processorsArray[tmp.selectedProcessorId].fMax);
            int pro_id=tmp.selectedProcessorId;            
            System.out.println("task_id \t"+taskId+"\t"+schedulerList.get(taskId)+"\tstart time\t"+tmp.timeGap.startTime+"\tend time:\t"+tmp.timeGap.endTime);
            
            
        }
 
        CalcTaskMaxCost.calcTaskMaxCost(beta, taskList, processorsArray);

        //compute min cost in all processors
        GetMinCostProcessorIdLevel.getMinCostProcessorIdLevel(beta, taskList, processorsArray);
        //rank task by cost difference
        List<Map.Entry<Integer, Double>> taskDisCostList = GetDisCostTaskOrder.getDisCostTaskOrder(taskList);

        //compute sum cost and makespan,deadline(maxTime)
        double sumCost = CalaCostofAll.calcCostofAll(taskList, processorsArray, beta);
        double makespan = CalcMakeSpan.calcMakeSpan(taskList);
        
        //**Calculate the makespan for the deadline==> the maximum makespan among 5 makespan
       // double makespanforDeadline=CalcAllMakespans.calcmakespans(processorNums, taskNums, beta, priceModel, computationCostPath, inputGraphPath, processorInfor, maxTimeParameter, starttime);

      //  double maxTime = makespanforDeadline * maxTimeParameter;
        double maxTime = makespan * maxTimeParameter;
     //   System.out.println("HEFT schedulerList:\t"+schedulerList.toString());
        //System.out.println("Deadline:"+maxTime);
        //***********************End of deadline declaration
        System.out.println("HEFT makespan: " + makespan + "\tCost: " + sumCost);
       // System.out.println(makespan + "\t" + sumCost);
    	DecimalFormat df = new DecimalFormat("#.00");
		  PWdm.write( df.format(makespan) + "\t" + df.format(sumCost/1e5) + "\t");
		PWdm.flush();

        double[][] RW = new double[taskNums][cfCount];
        boolean flag;
        HashMap<Integer, String> bestList = new HashMap<>();
        HashMap<Integer, String> jinji = new HashMap<>();
        double[] CFMax = new double[taskNums];
        int[] parentNum=new int[taskNums];
        String tmp;
        flag = computeRW.computeNew(RW, beta, schedulerList, cpuFreq, processorsArray, taskList, processorNums, bestList, jinji, CFMax,parentNum);
        double lastTime = makespan;
        double lastSumCost = sumCost;
        boolean t = true;
        double tcount=0;
        taskOrderList.clear();

      // pw=new PrintWriter("./outputDir/runtime_cfmax"+sumCost+".txt");
      // pw.write("tcount: \t cost:\n");
      // pw.flush();
        System.out.println("+++++++++++++++++++++++++++++++++++\nCFMax_HEFT SCHEDULER\n");

        while (flag && t) {

            long nowtime=System.currentTimeMillis();
            long difftime=(nowtime-starttime)/500;
            if(difftime>tcount){
                tcount=difftime;
             //  System.out.println(lastSumCost);
             //  pw.write(tcount+"\t"+lastSumCost+"\n");
              //  pw.flush();

            }

            double max = 0;
            int tm = -1;
            //å¯»æ‰¾å�˜åŒ–æœ€å¤§çš„RW[i][j]
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
                /// Task task = taskList.get(taskid);
                //ä¿�å­˜å½“å‰�çŠ¶æ€�ç”¨ä»¥å›žæ»š
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

                String[] string = newtestUpdate.testUpdate(taskList, processorsArray, tm, bestList, taskEdgeHashMap, beta,taskOrderList).split("_");

                sumCost = Double.valueOf(string[1].trim());
                double time = Double.valueOf(string[0].trim());
                // System.out.println(maxTime + " and " + time);
               //  System.out.println("BESTid : "+tm+"\t"+bestList.get(tm));
                // System.out.println(" runtime is: " + time +"maxTime is: "+maxTime +" all cost is: " + sumCost);

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
                    Task task=taskList.get(taskid);
                    for (int id:task.successorTaskList){
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
                //computeRW.computeNew(RW, beta, schedulerList, cpuFreq, processorsArray, taskList, processorNums, bestList, jinji, CFMax, tm,parentNum);
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

        double makespanCFMax = CalcMakeSpan.calcMakeSpan(taskList);
        double sumCostCFMax = CalaCostofAll.calcCostofAll(taskList, processorsArray, beta);
      // System.out.println(maxTime + " and " + makespanCFMax);
        
       // System.out.println("CFMax makespan: " + makespanCFMax + "\tCost: " + sumCostCFMax);
        //System.out.println( makespanCFMax + "\t" + sumCostCFMax);
        for (Integer taskid : taskOrderList) {
        Task tp=taskList.get(taskid);
        System.out.println("task_id \t"+taskid+"\t"+schedulerList.get(taskid)+"\tstart time\t"+tp.timeGap.startTime+"\tend time:\t"+tp.timeGap.endTime);
        
        }
        System.out.println("CFMAX_HEFT schedulerList:\t"+schedulerList.toString());
        System.out.println("CFMax makespan: " + makespanCFMax + "\tCost: " + sumCostCFMax);
    
      PWdm.write( df.format(makespanCFMax) + "\t" + df.format(sumCostCFMax/1e5) + "\t");
        PWdm.flush();
    }
    public static void main(String[] args) throws IOException{
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
				CFMaxTest.runHEFT(maxTimeParameter, processorNums, taskNums, beta, computationCostPath, inputGraphPath, processorInfor, PWcfmax, priceModel, 0);


    }
}
