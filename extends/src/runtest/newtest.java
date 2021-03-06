package runtest;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.util.*;
import java.io.File;

import taskscheduling.*;
import taskscheduling.util.*;


public class newtest {
    public static void runHEFT(double maxTimeParameter, int processorNums, int taskNums, double beta,
                               String computationCostPath, String inputGraphPath, String processorInfor,
                               PrintWriter PWnewtestFile, int priceModel, boolean em,long starttime) throws IOException {

        //the class used for initialing
        SchedulingInit sInit = new SchedulingInit();
        //initial task info
        ArrayList<Task> taskList = sInit.initTaskInfor(computationCostPath, inputGraphPath);
        //initial communication data
        HashMap<String, Double> taskEdgeHashMap = sInit.initTaskEdge();

        //initial processor info
        Processor[] processorsArray;
        processorsArray = sInit.initProcessorInfor(processorInfor, processorNums, priceModel);

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
        //澶勭悊鍣ㄧ殑cpu棰戠巼
        for (int i = 0; i < processorNums; i++) {
            for (int j = 0; processorsArray[i].fMin + j * processorsArray[i].fLevel <= processorsArray[i].fMax; j++) {
                cpuFreq[i][j] = processorsArray[i].fMin + j * processorsArray[i].fLevel;
                cfCount++;
            }
        }
        // for (int i = 0; i < processorNums; i++)
        //   System.out.println(Arrays.toString(cpuFreq[i]));
        HashMap<Integer, String> schedulerList = new HashMap<>();

        ArrayList<Integer> taskOrderList = CalcTaskRankValue.calcTaskRankValue1(taskList, taskEdgeHashMap, taskNums);

        //scheduling using HEFT
        for (Integer taskId : taskOrderList) {
            //  System.out.print(taskId+"\t");
            TaskScheduling.taskScheduling(taskId, taskList, taskEdgeHashMap, processorsArray);
            Task tmp = taskList.get(taskId);
            //璁板綍璋冨害鍒楄〃锛坱ask搴忓彿锛屽鐞嗗櫒搴忓彿r_棰戠巼f锛�
            schedulerList.put(taskId, tmp.selectedProcessorId + "_" + processorsArray[tmp.selectedProcessorId].fMax);
        }
        //璁＄畻HEFT璋冨害鍚庣殑姣忎釜浠诲姟鐨勮绠楀紑閿�
        CalcTaskMaxCost.calcTaskMaxCost(beta, taskList, processorsArray);

        //compute min cost in all processors锛屽鎵句唬浠锋渶灏忕殑澶勭悊鍣ㄧ殑缂栧彿鍜宭evel
        GetMinCostProcessorIdLevel.getMinCostProcessorIdLevel(beta, taskList, processorsArray);
        //rank task by cost difference
        List<Map.Entry<Integer, Double>> taskDisCostList = GetDisCostTaskOrder.getDisCostTaskOrder(taskList);

        //杈撳嚭HEFT姹傚緱鐨勬渶澶PU棰戠巼涓嬬殑鎵ц鏃堕棿涓庤绠楄垂鐢�
        //compute sum cost and makespan,deadline(maxTime)
        double sumCost = CalaCostofAll.calcCostofAll(taskList, processorsArray, beta);
        double makespan = CalcMakeSpan.calcMakeSpan(taskList);
        double maxTime = makespan * maxTimeParameter;//浼拌鐨勬埅姝㈡椂闂�
        System.out.println("Using HEFT \tmakespan: " + makespan + "\tsumCost: " + sumCost);
        //淇濆瓨缁撴灉
        //PrintWriter pw=new PrintWriter(newtestFile,"utf-8");
        //   pw.write("HEFTmakespan:\tHEFTsumcost:\tmaxTime:\tNEWTESTmakespan:\tNEWTESTsumcost:\n");
        //  pw.write(makespan+"\t"+sumCost+"\t");


        double[][] RW = new double[taskNums][cfCount];
        boolean flag;
        HashMap<Integer, String> bestList = new HashMap<>();//璁板綍姣忎釜task鏈�澶W鐨刬,j锛坱ask搴忓彿锛宨_j锛�
        HashMap<Integer, String> jinji = new HashMap<>();//璁板綍宸茬粡寰楀埌浣嗕笉婊¤冻鏃堕棿瑕佹眰鐨勮皟搴�
        double[] CFMax = new double[taskNums];
        double[] evalInd = new double[taskNums];
        String[] eval = new String[taskNums];

        //璁＄畻RW鐭╅樀锛屽苟涓斿姣忎竴涓换鍔℃眰涓�涓猂W鏈�澶х殑璋冨害
        // String tmp;
        flag = computeRW.compute(RW, beta, schedulerList, cpuFreq, processorsArray, taskList, processorNums, bestList, jinji, CFMax);
        double maxSumCost = sumCost;
        double HEFTmakespan = makespan;
        boolean t = true;
        double lastSumCost = sumCost;
        double lastTime = makespan;
        int count = 0;
        double lastCost=sumCost;
        long tcount=0;
        PrintWriter pw;
        //Date date=new Date();
        if (!em){
        pw=new PrintWriter("./outputDir/runtime_dm"+sumCost+".txt");
        pw.write("tcount: \t cost:\n");
        pw.flush();
        }
        else{
            pw=new PrintWriter("./outputDir/runtime_em"+sumCost+".txt");
            pw.write("tcount: \t cost:\n");
            pw.flush();
        }

        while (flag && t) {

        	long nowtime=System.currentTimeMillis();
        	long difftime=(nowtime-starttime)/500;
        	if(difftime>tcount){
        		tcount=difftime;
        		//System.out.println(lastSumCost);
        		pw.write(tcount+"\t"+lastSumCost+"\n");
        		pw.flush();
        		
        	}
        	
        	
            int tm = -1;
            for (int i = 0; i < taskNums; i++) {
                evalInd[0] = Double.MAX_VALUE;
            }
            //淇濆瓨褰撳墠鐘舵�佺敤浠ュ洖婊�
            Task[] oldTaskInfor = new Task[taskList.size()];
            for (int k = 0; k < taskNums; k++) {
                if (CFMax[k] == 0) {
                    evalInd[k] = Double.MAX_VALUE;
                    continue;
                }
                //淇濆瓨褰撳墠鐘舵�佺敤浠ュ洖婊�
                oldTaskInfor = new Task[taskList.size()];
                for (int i = 0; i < taskList.size(); ++i) {
                    oldTaskInfor[i] = new Task();
                    oldTaskInfor[i].selectedProcessorId = taskList.get(i).selectedProcessorId;
                    oldTaskInfor[i].selectedFre = taskList.get(i).selectedFre;
                    oldTaskInfor[i].timeGap = new TimeGap();
                    oldTaskInfor[i].timeGap.startTime = taskList.get(i).timeGap.startTime;
                    oldTaskInfor[i].timeGap.endTime = taskList.get(i).timeGap.endTime;
                    oldTaskInfor[i].timeGap.gap = taskList.get(i).timeGap.gap;
                }

                String[] str;
                //int taskid = Integer.valueOf(str[0]);
                str = bestList.get(k).split("_");
                //  Task task = taskList.get(taskid);
                // double lastTaskSumCost = CalaCostofAll.calcCostofAll(taskList, processorsArray, beta);

                String[] string = newtestUpdate.newtestUpdate(taskList, processorsArray, k, bestList, taskEdgeHashMap, beta).split("_");

                makespan = Double.valueOf(string[0].trim());
                sumCost = Double.valueOf(string[1].trim());
                if (!em)
                    evalInd[k] = sumCost / maxSumCost + 1 * makespan / maxTime;//  Method(1) metric = sumCost/maxSumCost +1* makespan / maxTime
                else
                    evalInd[k] = Math.sqrt(Math.pow(sumCost / maxSumCost, 2) + 1 * Math.pow(makespan / maxTime, 2));

                // System.out.println("makespan is:  "+makespan+"\t maxTime is:  "+maxTime+"sumCost is : "+sumCost);

                eval[k] = str[0] + "_" + str[1];
                //杩樺師tasklist
                for (int i = 0; i < taskList.size(); ++i) {
                    taskList.get(i).selectedProcessorId = oldTaskInfor[i].selectedProcessorId;
                    taskList.get(i).selectedFre = oldTaskInfor[i].selectedFre;
                    taskList.get(i).timeGap.startTime = oldTaskInfor[i].timeGap.startTime;
                    taskList.get(i).timeGap.endTime = oldTaskInfor[i].timeGap.endTime;
                    taskList.get(i).timeGap.gap = oldTaskInfor[i].timeGap.gap;
                }

            }

            double min = Double.MAX_VALUE;
            for (int i = 0; i < taskNums; i++) {
                if (evalInd[i] < min) {
                    tm = i;
                    min = evalInd[i];
                }
            }
            // System.out.println(tm);

            if (tm != -1) {
                String[] string = newtestUpdate.newtestUpdate(taskList, processorsArray, tm, bestList, taskEdgeHashMap, beta).split("_");

                // makespan = Double.valueOf(string[0].trim());
                sumCost = Double.valueOf(string[1].trim());
                double time = Double.valueOf(string[0].trim());
                //System.out.println(maxTime + " and " + time);
                // System.out.println(" runtime is: " + time + " all cost is: " + sumCost);

                // System.out.println("newtest  BESTid : " + tm + "\t" + bestList.get(tm));
                // System.out.println(" runtime is: " + time + "maxTime is: " + maxTime + " all cost is: " + sumCost);
               if((lastCost == sumCost)){
            	   flag=false;
               }
               lastCost=sumCost;
                 
                 
                 if (maxTime < time || ((lastTime <= time) && (lastSumCost <= sumCost))) {
                    for (int i = 0; i < taskList.size(); ++i) {
                        taskList.get(i).selectedProcessorId = oldTaskInfor[i].selectedProcessorId;
                        taskList.get(i).selectedFre = oldTaskInfor[i].selectedFre;
                        taskList.get(i).timeGap.startTime = oldTaskInfor[i].timeGap.startTime;
                        taskList.get(i).timeGap.endTime = oldTaskInfor[i].timeGap.endTime;
                        taskList.get(i).timeGap.gap = oldTaskInfor[i].timeGap.gap;
                    }

                    //鍔犲叆绂佸繉琛�
                    boolean isStrIn = false;
                    if (jinji.keySet().contains(tm)) {
                        String[] strs = jinji.get(tm).split(",");

                        for (int i = 0; i < strs.length; i++) {
                            if (strs[i].equals(eval[tm])) {
                                isStrIn = true;
                            }
                        }
                    }
                    if (!isStrIn) {
                        jinji.put(tm, jinji.get(tm) + "," + eval[tm]);//鍔犲叆绂佸繉
                    }
                    if (time > maxTime) {
                        count++;
                        // System.out.println(count + "\t" + time + "\t" + maxTime);
                    }
                    if (count > 100) {
                      //  flag = false;
                    }

                } else {
                    lastSumCost = sumCost;
                    lastTime = time;
                    schedulerList.put(tm, eval[tm]);
                    //System.out.println(" runtime is: " +lastTime  + " all cost is: " + lastSumCost);
                }

            } else {
                flag = false;
            }

            if (flag)
                computeRW.computeTask(RW, beta, schedulerList, cpuFreq, processorsArray, taskList, processorNums, bestList, jinji, CFMax, tm);


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
        double makespanNewtest = CalcMakeSpan.calcMakeSpan(taskList);
        double sumCostNewtest = CalaCostofAll.calcCostofAll(taskList, processorsArray, beta);
        // System.out.println(maxTime + " and " + makespan);
        System.out.println("Newtest runtime is: " + makespanNewtest + " all cost is: " + sumCostNewtest
                + "\n******************************************************************************************");
        DecimalFormat df = new DecimalFormat("#.00");
        PWnewtestFile.write( df.format(makespanNewtest) + "\t" + df.format(sumCostNewtest/1e5) + "\t" );
        PWnewtestFile.flush();

    }

}