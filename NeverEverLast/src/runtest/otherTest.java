package runtest;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.util.*;
import java.io.File;

import taskscheduling.*;
import taskscheduling.util.*;

public class otherTest {
    public static void runHEFT(double maxTimeParameter, int processorNums, int taskNums, double beta,
                               String computationCostPath, String inputGraphPath, String processorInfor,
                               PrintWriter PWnewtestFile,int priceModel,long starttime) throws IOException {

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
            int tmp = (processorsArray[i].fMax - processorsArray[i].fMin) / processorsArray[i].fLevel+1;
            if (tmp > freqcount) {
                freqcount = tmp ;
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
            Task tmp = taskList.get(taskId );

            schedulerList.put(taskId , tmp.selectedProcessorId + "_" + processorsArray[tmp.selectedProcessorId].fMax);
        }
        //璁＄畻HEFT璋冨害鍚庣殑姣忎釜浠诲姟鐨勮绠楀紑閿�
        CalcTaskMaxCost.calcTaskMaxCost(beta, taskList, processorsArray);

        //compute min cost in all processors锛屽鎵句唬浠锋渶灏忕殑澶勭悊鍣ㄧ殑缂栧彿鍜宭evel
        GetMinCostProcessorIdLevel.getMinCostProcessorIdLevel(beta, taskList, processorsArray);
        //rank task by cost difference
        //List<Map.Entry<Integer, Double>> taskDisCostList = GetDisCostTaskOrder.getDisCostTaskOrder(taskList);

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
        double[][] RT = new double[taskNums][cfCount];
        boolean flag = false;
        HashMap<Integer, String> bestList = new HashMap<>();
        HashMap<Integer, String> jinji = new HashMap<>();
        String[] evalMax = new String[taskNums];
        //鍒濆鍖朢T銆丷W鐭╅樀
        for (int taskid = 0; taskid < taskNums; taskid++) {
            evalMax[taskid] = "";
            String[] str = schedulerList.get(taskid).split("_");
            int p = Integer.valueOf(str[0]);
            int q = Integer.valueOf(str[1]);

            bestList.put(taskid, p + "_" + q);
            double rt = CalcRunTime.calcRunTime(beta, q, processorsArray[p], taskList.get(taskid).computationCost.get(p));
             //璁＄畻褰撳墠鑺辫垂鐨勬垚鏈�
            double tc = rt * CalcCost.getCost(q, processorsArray[p]);

            int k = 0;
            for (int i = 0; i < processorNums; i++) {
                for (int j = 0; j < cpuFreq[i].length; j++) {
                    if (cpuFreq[i][j] > 0) {
                      double rt1 = CalcRunTime.calcRunTime(beta, cpuFreq[i][j], processorsArray[i], taskList.get(taskid).computationCost.get(i));
                        RT[taskid][k] = rt - rt1;
                        
                        double tc1 = rt1 * CalcCost.getCost(cpuFreq[i][j], processorsArray[i]);
                        //  System.out.println(tc+"\t"+tc1);
                        RW[taskid][k] = tc - tc1;
                        if (RW[taskid][k] > 0) {
                            flag = true;
                            //double temp;
                            double metric = RT[taskid][k] / maxTime + RW[taskid][k] / sumCost;
                            // double metric =  RW[taskid][k] ;
                            if (evalMax[taskid].length() > 0) {
                                String[] strs = evalMax[taskid].split(",");
                                int tmp = Integer.valueOf(strs[0]);

                                switch (tmp) {
                                    case 1:
                                        if (RT[taskid][k] > 0 && metric > Double.valueOf(strs[1])) {
                                            // temp = RT[taskid][k] / maxTime + RW[taskid][k] / sumCost;
                                            evalMax[taskid] = 1 + "," + metric;
                                            bestList.put(taskid, i + "_" + cpuFreq[i][j]);
                                        }
                                        break;
                                    case 2:
                                        if (RT[taskid][k] > 0) {
                                            evalMax[taskid] = 1 + "," + metric;
                                            bestList.put(taskid, i + "_" + cpuFreq[i][j]);
                                        } else if ((RT[taskid][k] == 0 && metric > Double.valueOf(strs[1]))) {
                                            //temp = RW[taskid][k] / sumCost;
                                            evalMax[taskid] = 2 + "," + metric;
                                            bestList.put(taskid, i + "_" + cpuFreq[i][j]);
                                        }
                                        break;
                                    case 3:
                                        if (RT[taskid][k] > 0) {
                                            // temp = RT[taskid][k] / maxTime + RW[taskid][k] / sumCost;
                                            evalMax[taskid] = 1 + "," + metric;
                                            bestList.put(taskid, i + "_" + cpuFreq[i][j]);
                                        }
                                        if (RT[taskid][k] == 0) {
                                            // temp = RW[taskid][k] / sumCost;
                                            evalMax[taskid] = 2 + "," + metric;
                                            bestList.put(taskid, i + "_" + cpuFreq[i][j]);
                                        }
                                        if (RT[taskid][k] < 0) {
                                            metric = (-1 * RW[taskid][k]) / RT[taskid][k];
                                            //temp=RW[taskid][k];
                                            if (metric < Double.valueOf(strs[1])) {
                                                evalMax[taskid] = 3 + "," + metric;
                                                bestList.put(taskid, i + "_" + cpuFreq[i][j]);
                                            }
                                        }
                                        break;

                                }
                            } else {
                                if (RT[taskid][k] > 0) {
                                    //temp = RT[taskid][k] / maxTime + RW[taskid][k] / sumCost;
                                    evalMax[taskid] = 1 + "," + metric;
                                    bestList.put(taskid, i + "_" + cpuFreq[i][j]);
                                }
                                if (RT[taskid][k] == 0) {
                                    // temp = RW[taskid][k] / sumCost;
                                    evalMax[taskid] = 2 + "," + metric;
                                    bestList.put(taskid, i + "_" + cpuFreq[i][j]);
                                }
                                if (RT[taskid][k] < 0) {
                                    metric = RW[taskid][k] / (-1*RT[taskid][k]);
                                    // metric=RW[taskid][k];
                                    evalMax[taskid] = 3 + "," + metric;
                                    bestList.put(taskid, i + "_" + cpuFreq[i][j]);
                                }
                            }

                        }
                        //   System.out.println("k  :"+k+"i  :"+i+"j  :"+j);
                        k++;
                    }

                }

            }
        }


        double maxSumCost = sumCost;
        double HEFTmakespan = makespan;
        boolean t = true;
        double lastSumCost = sumCost;
        double lastTime = makespan;
        PrintWriter pw;
       // Date date=new Date();
        long tcount=0;
        pw=new PrintWriter("./outputDir/runtime_3th"+sumCost+".txt");
        pw.write("tcount: \t cost:\n");
        pw.flush();
        while (flag && t) {
            long nowtime=System.currentTimeMillis();
            long difftime=(nowtime-starttime)/500;
            if(difftime>tcount){
                tcount=difftime;
               // System.out.println(lastSumCost);
                pw.write(tcount+"\t"+lastSumCost+"\n");
                pw.flush();

            }


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

            int bestid = 0;
            String bestSche = evalMax[0];
            String[] bestScheTemp;
           // flag = false;
            //姹備竴涓皟搴︽柟妗�
            for (int i = 1; i < taskNums; i++) {
                if (bestSche.length() == 0) {
                    bestSche = evalMax[i];
                    bestid = i;
                    //  flag=false;
                } else if (evalMax[i].length() > 0) {
                    String[] strings = evalMax[i].split(",");
                    bestScheTemp = bestSche.split(",");

                    if (Double.valueOf(strings[0]) < Double.valueOf(bestScheTemp[0])
                            || (strings[0].equals(bestScheTemp[0])
                            && Double.valueOf(strings[1]) > Double.valueOf(bestScheTemp[1]))) {
                        bestSche = evalMax[i];
                        bestid = i;
                    }

                }

            }

            if (bestSche.length() > 0){
                flag=true;
            }else
                flag=false;
       
            String[] string = newtestUpdate.newtestUpdate(taskList, processorsArray, bestid, bestList, taskEdgeHashMap, beta).split("_");
            sumCost = Double.valueOf(string[1].trim());
            double time = Double.valueOf(string[0].trim());

           //  System.out.println("other test BESTid : "+bestid+"\t"+bestList.get(bestid));
            // System.out.println(" runtime is: " + time +"maxTime is: "+maxTime +" all cost is: " + sumCost);

            if (maxTime < time || ((lastTime < time) && (lastSumCost < sumCost))) {
                for (int i = 0; i < taskList.size(); ++i) {
                    taskList.get(i).selectedProcessorId = oldTaskInfor[i].selectedProcessorId;
                    taskList.get(i).selectedFre = oldTaskInfor[i].selectedFre;
                    taskList.get(i).timeGap.startTime = oldTaskInfor[i].timeGap.startTime;
                    taskList.get(i).timeGap.endTime = oldTaskInfor[i].timeGap.endTime;
                    taskList.get(i).timeGap.gap = oldTaskInfor[i].timeGap.gap;
                    //double makespanOthertest = CalcMakeSpan.calcMakeSpan(taskList);
                    // System.out.println(" 杩樺師鍚庣殑杩愯鏃堕棿鏄細 "+makespanOthertest);
                }

                //鍔犲叆绂佸繉琛�
                boolean isStrIn = false;
                if (jinji.keySet().contains(bestid)) {
                    String[] strs = jinji.get(bestid).split(",");

                    for (int i = 0; i < strs.length; i++) {
                        if (strs[i].equals(bestList.get(bestid))) {
                            isStrIn = true;
                        }
                    }
                }
                if (!isStrIn) {
                    jinji.put(bestid, jinji.get(bestid) + "," + bestList.get(bestid));
                }
            } else {
                lastSumCost = sumCost;
                lastTime = time;
                schedulerList.put(bestid, bestList.get(bestid));
                //  System.out.println(bestid+"   "+time+"   "+sumCost+"  "+bestList.get(bestid));
                //System.out.println(" runtime is: " +lastTime  + " all cost is: " + lastSumCost);
            }

            if (flag)
                 newtestComputeRW.computeTask(RW, RT, beta, schedulerList, cpuFreq, processorsArray,
                        maxSumCost,  maxTime, taskList, processorNums,  bestList, jinji, evalMax,bestid);

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
            //System.out.println(flag+"\t"+t);
        }
        double makespanOthertest = CalcMakeSpan.calcMakeSpan(taskList);
        double sumCostOthertest = CalaCostofAll.calcCostofAll(taskList, processorsArray, beta);
        // System.out.println(maxTime + " and " + makespan);
        System.out.println("OtherTest runtime is: " + makespanOthertest + " all cost is: " + sumCostOthertest+
        "\n#######################################################################################");
        DecimalFormat df = new DecimalFormat("#.00");
        PWnewtestFile.write( df.format(makespanOthertest) + "\t" + df.format(sumCostOthertest/1e5) + "\n");
        PWnewtestFile.flush();

    }


}
