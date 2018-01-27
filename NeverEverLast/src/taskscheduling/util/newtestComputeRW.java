package taskscheduling.util;

import taskscheduling.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;


public class newtestComputeRW {
    public static boolean compute(double[][] RW, double[][] RT, double beta, HashMap<Integer, String> schedulerList,
                                   int[][] cpuFreq, Processor[] processorsArray, double sumCost, double maxTime,
                                   ArrayList<Task> taskList, int processorNums,
                                   HashMap<Integer, String> bestList, HashMap<Integer, String> jinji, String[] evalMax) {
        boolean flag = false;
        //初始化RT、RW矩阵
        for (int taskid = 0; taskid < taskList.size(); taskid++) {
            evalMax[taskid] = "";
            String[] str = schedulerList.get(taskid).split("_");
            int p = Integer.valueOf(str[0]);
            int q = Integer.valueOf(str[1]);
            // System.out.println(p+"\t"+q);
            bestList.put(taskid, p + "_" + q);
            //计算当前花费的成本
            double rt = CalcRunTime.calcRunTime(beta, q, processorsArray[p], taskList.get(taskid).computationCost.get(p));

            double tc = rt * CalcCost.getCost(q, processorsArray[p]);
            int k = 0;
            for (int i = 0; i < processorNums; i++) {
                for (int j = 0; j < cpuFreq[i].length; j++) {
                    if (cpuFreq[i][j] > 0) {
                     /* //
                        Task[] oldTaskInfor = new Task[taskList.size()];
                        for (int id = 0; id < taskList.size(); ++id) {
                            oldTaskInfor[id] = new Task();
                            oldTaskInfor[id].selectedProcessorId = taskList.get(id).selectedProcessorId;
                            oldTaskInfor[id].selectedFre = taskList.get(id).selectedFre;
                            oldTaskInfor[id].timeGap = new TimeGap();
                            oldTaskInfor[id].timeGap.startTime = taskList.get(id).timeGap.startTime;
                            oldTaskInfor[id].timeGap.endTime = taskList.get(id).timeGap.endTime;
                            oldTaskInfor[id].timeGap.gap = taskList.get(id).timeGap.gap;
                        }
                        double makespan1 = otherTestUpdate.Update(taskList, processorsArray, taskid, i + "_" + cpuFreq[i][j], taskEdgeHashMap, beta);
                        for (int id = 0; id < taskList.size(); ++id) {
                            taskList.get(id).selectedProcessorId = oldTaskInfor[id].selectedProcessorId;
                            taskList.get(id).selectedFre = oldTaskInfor[id].selectedFre;
                            taskList.get(id).timeGap.startTime = oldTaskInfor[id].timeGap.startTime;
                            taskList.get(id).timeGap.endTime = oldTaskInfor[id].timeGap.endTime;
                            taskList.get(id).timeGap.gap = oldTaskInfor[id].timeGap.gap;
                        }*/
                        // double rt1 = CalcRunTime.calcRunTime(beta, cpuFreq[i][j], processorsArray[i], taskList.get(taskid).computationCost.get(i));
                        double rt1 = CalcRunTime.calcRunTime(beta, cpuFreq[i][j], processorsArray[i], taskList.get(taskid).computationCost.get(i));
                        RT[taskid][k] = rt-rt1;
                        //时间减少量
                        //计算在该配置下需要花费的成本
                        double tc1 = rt1 * CalcCost.getCost(cpuFreq[i][j], processorsArray[i]);
                        //  System.out.println(tc+"\t"+tc1);
                        RW[taskid][k] = tc - tc1;//新配置下成本能够节约多少
                        boolean f = true;
                        if (jinji.size() > 0) {
                            String[] strings = {};
                            if (jinji.keySet().contains(taskid)) {
                                strings = jinji.get(taskid).split(",");
                            }
                            for (String str1 : strings) {
                                if (str1.equals(String.valueOf(i + "_" + cpuFreq[i][j])))
                                    f = false;
                            }
                        }

                        if (RW[taskid][k] > 0 && f) {
                            flag = true;
                            //double temp;
                            double metric = RT[taskid][k] / maxTime + RW[taskid][k] / sumCost;
                            //double metric = RW[taskid][k] ;
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
                                    metric = RW[taskid][k] / (-RT[taskid][k]);
                                    evalMax[taskid] = 3 + "," + metric;
                                    bestList.put(taskid, i + "_" + cpuFreq[i][j]);
                                }
                            }

                        }
                        k++;
                    }

                }

            }
            // System.out.println(taskid+"\t"+flag);
        }

      /*  System.out.println("========================================================================================");
      for (int i=0;i<taskList.size();i++){
           System.out.println(Arrays.toString(RW[i]));
        }*/


        return flag;


    }
    public static boolean computeTask(double[][] RW, double[][] RT, double beta, HashMap<Integer, String> schedulerList,
                                  int[][] cpuFreq, Processor[] processorsArray, double sumCost, double maxTime,
                                  ArrayList<Task> taskList, int processorNums,
                                  HashMap<Integer, String> bestList, HashMap<Integer, String> jinji, String[] evalMax,int taskid) {
        boolean flag = true;
        //初始化RT、RW矩阵
            evalMax[taskid] = "";
            String[] str = schedulerList.get(taskid).split("_");
            int p = Integer.valueOf(str[0]);
            int q = Integer.valueOf(str[1]);
            // System.out.println(p+"\t"+q);
            bestList.put(taskid, p + "_" + q);
            //计算当前花费的成本
            double rt = CalcRunTime.calcRunTime(beta, q, processorsArray[p], taskList.get(taskid).computationCost.get(p));

            double tc = rt * CalcCost.getCost(q, processorsArray[p]);
            int k = 0;
            for (int i = 0; i < processorNums; i++) {
                for (int j = 0; j < cpuFreq[i].length; j++) {
                    if (cpuFreq[i][j] > 0) {
                     /* //
                        Task[] oldTaskInfor = new Task[taskList.size()];
                        for (int id = 0; id < taskList.size(); ++id) {
                            oldTaskInfor[id] = new Task();
                            oldTaskInfor[id].selectedProcessorId = taskList.get(id).selectedProcessorId;
                            oldTaskInfor[id].selectedFre = taskList.get(id).selectedFre;
                            oldTaskInfor[id].timeGap = new TimeGap();
                            oldTaskInfor[id].timeGap.startTime = taskList.get(id).timeGap.startTime;
                            oldTaskInfor[id].timeGap.endTime = taskList.get(id).timeGap.endTime;
                            oldTaskInfor[id].timeGap.gap = taskList.get(id).timeGap.gap;
                        }
                        double makespan1 = otherTestUpdate.Update(taskList, processorsArray, taskid, i + "_" + cpuFreq[i][j], taskEdgeHashMap, beta);
                        for (int id = 0; id < taskList.size(); ++id) {
                            taskList.get(id).selectedProcessorId = oldTaskInfor[id].selectedProcessorId;
                            taskList.get(id).selectedFre = oldTaskInfor[id].selectedFre;
                            taskList.get(id).timeGap.startTime = oldTaskInfor[id].timeGap.startTime;
                            taskList.get(id).timeGap.endTime = oldTaskInfor[id].timeGap.endTime;
                            taskList.get(id).timeGap.gap = oldTaskInfor[id].timeGap.gap;
                        }*/
                        // double rt1 = CalcRunTime.calcRunTime(beta, cpuFreq[i][j], processorsArray[i], taskList.get(taskid).computationCost.get(i));
                        double rt1 = CalcRunTime.calcRunTime(beta, cpuFreq[i][j], processorsArray[i], taskList.get(taskid).computationCost.get(i));
                        RT[taskid][k] = rt-rt1;
                        //时间减少量
                        //计算在该配置下需要花费的成本
                        double tc1 = rt1 * CalcCost.getCost(cpuFreq[i][j], processorsArray[i]);
                        //  System.out.println(tc+"\t"+tc1);
                        RW[taskid][k] = tc - tc1;//新配置下成本能够节约多少
                        boolean f = true;
                        if (jinji.size() > 0) {
                            String[] strings = {};
                            if (jinji.keySet().contains(taskid)) {
                                strings = jinji.get(taskid).split(",");
                            }
                            for (String str1 : strings) {
                                if (str1.equals(String.valueOf(i + "_" + cpuFreq[i][j])))
                                    f = false;
                            }
                        }

                        if (RW[taskid][k] > 0 && f) {
                            flag = true;
                            //double temp;
                            double metric = RT[taskid][k] / maxTime + RW[taskid][k] / sumCost;
                            //double metric = RW[taskid][k] ;
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
                                    metric = RW[taskid][k] / (-RT[taskid][k]);
                                    evalMax[taskid] = 3 + "," + metric;
                                    bestList.put(taskid, i + "_" + cpuFreq[i][j]);
                                }
                            }

                        }
                        k++;
                    }

                }

            }
            // System.out.println(taskid+"\t"+flag);

      /*  System.out.println("========================================================================================");
      for (int i=0;i<taskList.size();i++){
           System.out.println(Arrays.toString(RW[i]));
        }*/


        return flag;


    }
}
