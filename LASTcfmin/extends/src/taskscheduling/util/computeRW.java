package taskscheduling.util;

import taskscheduling.Processor;
import taskscheduling.Task;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;


public class computeRW {
    public static boolean compute(double[][] RW, double beta, HashMap<Integer, String> schedulerList, int[][] cpuFreq, Processor[] processorsArray,
                                  ArrayList<Task> taskList, int processorNums,HashMap<Integer, String> bestList, HashMap<Integer, String> jinji, double[] CFMax) {
        boolean flag = false;
        for (int taskid = 0; taskid < taskList.size(); taskid++) {
            CFMax[taskid] = 0;
            String[] str = schedulerList.get(taskid).split("_");
            int p = Integer.valueOf(str[0]);
            int q = Integer.valueOf(str[1]);
            // System.out.println(p+"\t"+q);
            //计算当前花费的成本
            double tc = CalcRunTime.calcRunTime(beta, q, processorsArray[p], taskList.get(taskid).computationCost.get(p)) * CalcCost.getCost(q, processorsArray[p]);
            bestList.put(taskid, p + "_" + q);
            String[] strings = {};
            if (jinji.keySet().contains(taskid)) {
                strings = jinji.get(taskid).split(",");
            }

            int k = 0;
            for (int i = 0; i < processorNums; i++) {
                for (int j = 0; j < cpuFreq[i].length; j++) {
                    if (cpuFreq[i][j] > 0) {
                        //计算在该配置下需要花费的成本
                        double tc1 = CalcRunTime.calcRunTime(beta, cpuFreq[i][j], processorsArray[i], taskList.get(taskid).computationCost.get(i)) * CalcCost.getCost(cpuFreq[i][j], processorsArray[i]);
                        //  System.out.println(tc+"\t"+tc1);
                        RW[taskid][k] = tc - tc1;//新配置下成本能够节约多少
                        if (RW[taskid][k] > 0) {
                            flag = true;
                        }
                        boolean f = true;
                        if (jinji.size() > 0) {
                            for (String str1 : strings) {
                                if (str1.equals(String.valueOf(i + "_" + cpuFreq[i][j])))
                                    f = false;
                            }
                        }
                        if (RW[taskid][k] > CFMax[taskid] && f) {
                            bestList.put(taskid, i + "_" + cpuFreq[i][j]);
                            // System.out.println(taskid+"  "+bestList.get(taskid));
                            CFMax[taskid] = RW[taskid][k];
                        }
                        k++;
                    }
                }
            }
        }
      /*  double max = 0;
        int t = -1;
       // 寻找变化最大的RW[i][j]
        for (int i = 0; i < taskNums; i++) {
            if (max < CFMax[i]) {
                max = CFMax[i];
                t = i;
            }
        }*/
       /* if (t != -1) {
            bestList = flag + "/" + t + "," + tempList.get(t);
        }*/

/*

     for (int i=0;i<taskNums;i++){
           System.out.println(Arrays.toString(RW[i]));
        }
        System.out.println("*******************************************************************************************************************************************************");
        System.out.println("输出CFMAX:     "+Arrays.toString(CFMax));*/

        return flag;
    }
    public static boolean computeTask(double[][] RW, double beta, HashMap<Integer, String> schedulerList, int[][] cpuFreq, Processor[] processorsArray,
                                  ArrayList<Task> taskList, int processorNums,HashMap<Integer, String> bestList, HashMap<Integer, String> jinji, double[] CFMax,int taskid) {
        boolean flag = false;
            CFMax[taskid] = 0;
            String[] str = schedulerList.get(taskid).split("_");
            int p = Integer.valueOf(str[0]);
            int q = Integer.valueOf(str[1]);
            // System.out.println(p+"\t"+q);
            //计算当前花费的成本
            double tc = CalcRunTime.calcRunTime(beta, q, processorsArray[p], taskList.get(taskid).computationCost.get(p)) * CalcCost.getCost(q, processorsArray[p]);
            bestList.put(taskid, p + "_" + q);
            String[] strings = {};
            if (jinji.keySet().contains(taskid)) {
                strings = jinji.get(taskid).split(",");
            }

            int k = 0;
            for (int i = 0; i < processorNums; i++) {
                for (int j = 0; j < cpuFreq[i].length; j++) {
                    if (cpuFreq[i][j] > 0) {
                        //计算在该配置下需要花费的成本
                        double tc1 = CalcRunTime.calcRunTime(beta, cpuFreq[i][j], processorsArray[i], taskList.get(taskid).computationCost.get(i)) * CalcCost.getCost(cpuFreq[i][j], processorsArray[i]);
                        //  System.out.println(tc+"\t"+tc1);
                        RW[taskid][k] = tc - tc1;//新配置下成本能够节约多少
                        if (RW[taskid][k] > 0) {
                            flag = true;
                        }
                        boolean f = true;
                        if (jinji.size() > 0) {
                            for (String str1 : strings) {
                                if (str1.equals(String.valueOf(i + "_" + cpuFreq[i][j])))
                                    f = false;
                            }
                        }
                        if (RW[taskid][k] > CFMax[taskid] && f) {
                            bestList.put(taskid, i + "_" + cpuFreq[i][j]);
                            // System.out.println(taskid+"  "+bestList.get(taskid));
                            CFMax[taskid] = RW[taskid][k];
                        }
                        k++;
                    }
                }
            }

      /*  double max = 0;
        int t = -1;
       // 寻找变化最大的RW[i][j]
        for (int i = 0; i < taskNums; i++) {
            if (max < CFMax[i]) {
                max = CFMax[i];
                t = i;
            }
        }*/
       /* if (t != -1) {
            bestList = flag + "/" + t + "," + tempList.get(t);
        }*/

/*

     for (int i=0;i<taskNums;i++){
           System.out.println(Arrays.toString(RW[i]));
        }
        System.out.println("*******************************************************************************************************************************************************");
        System.out.println("输出CFMAX:     "+Arrays.toString(CFMax));*/

        return flag;
    }

    public static boolean computeMIN(double[][] RW, double beta, HashMap<Integer, String> schedulerList, int[][] cpuFreq, Processor[] processorsArray,
                                  ArrayList<Task> taskList, int processorNums,HashMap<Integer, String> bestList, HashMap<Integer, String> jinji, double[] CFMin) {
        boolean flag = false;
        for (int taskid = 0; taskid < taskList.size(); taskid++) {
            CFMin[taskid] = Double.MAX_VALUE;
            String[] str = schedulerList.get(taskid).split("_");
            int p = Integer.valueOf(str[0]);
            int q = Integer.valueOf(str[1]);
            // System.out.println(p+"\t"+q);
            //计算当前花费的成本
            double tc = CalcRunTime.calcRunTime(beta, q, processorsArray[p], taskList.get(taskid).computationCost.get(p)) * CalcCost.getCost(q, processorsArray[p]);
            bestList.put(taskid, p + "_" + q);
            String[] strings = {};
            if (jinji.keySet().contains(taskid)) {
                strings = jinji.get(taskid).split(",");
            }

            int k = 0;
            for (int i = 0; i < processorNums; i++) {
                for (int j = 0; j < cpuFreq[i].length; j++) {
                    if (cpuFreq[i][j] > 0) {
                        //计算在该配置下需要花费的成本
                        double tc1 = CalcRunTime.calcRunTime(beta, cpuFreq[i][j], processorsArray[i], taskList.get(taskid).computationCost.get(i)) * CalcCost.getCost(cpuFreq[i][j], processorsArray[i]);
                        //  System.out.println(tc+"\t"+tc1);
                        RW[taskid][k] = tc - tc1;//新配置下成本能够节约多少
                        if (RW[taskid][k] > 0) {
                            flag = true;
                        }
                        boolean f = true;
                        if (jinji.size() > 0) {
                            for (String str1 : strings) {
                                if (str1.equals(String.valueOf(i + "_" + cpuFreq[i][j])))
                                    f = false;
                            }
                        }
                        if (flag && RW[taskid][k] < CFMin[taskid] && f) {
                            bestList.put(taskid, i + "_" + cpuFreq[i][j]);
                            // System.out.println(taskid+"  "+bestList.get(taskid));
                            CFMin[taskid] = RW[taskid][k];
                        }
                        k++;
                    }
                }
            }
        }
      /*  double max = 0;
        int t = -1;
       // 寻找变化最大的RW[i][j]
        for (int i = 0; i < taskNums; i++) {
            if (max < CFMax[i]) {
                max = CFMax[i];
                t = i;
            }
        }*/
       /* if (t != -1) {
            bestList = flag + "/" + t + "," + tempList.get(t);
        }*/

/*

     for (int i=0;i<taskNums;i++){
           System.out.println(Arrays.toString(RW[i]));
        }
        System.out.println("*******************************************************************************************************************************************************");
        System.out.println("输出CFMAX:     "+Arrays.toString(CFMax));*/

        return flag;
    }
    public static boolean computeTaskMIN(double[][] RW, double beta, HashMap<Integer, String> schedulerList, int[][] cpuFreq, Processor[] processorsArray,
                                      ArrayList<Task> taskList, int processorNums,HashMap<Integer, String> bestList, HashMap<Integer, String> jinji, double[] CFMin,int taskid) {
        boolean flag = false;
        CFMin[taskid] = Double.MAX_VALUE;
        String[] str = schedulerList.get(taskid).split("_");
        int p = Integer.valueOf(str[0]);
        int q = Integer.valueOf(str[1]);
        // System.out.println(p+"\t"+q);
        //计算当前花费的成本
        double tc = CalcRunTime.calcRunTime(beta, q, processorsArray[p], taskList.get(taskid).computationCost.get(p)) * CalcCost.getCost(q, processorsArray[p]);
        bestList.put(taskid, p + "_" + q);
        String[] strings = {};
        if (jinji.keySet().contains(taskid)) {
            strings = jinji.get(taskid).split(",");
        }

        int k = 0;
        for (int i = 0; i < processorNums; i++) {
            for (int j = 0; j < cpuFreq[i].length; j++) {
                if (cpuFreq[i][j] > 0) {
                    //计算在该配置下需要花费的成本
                    double tc1 = CalcRunTime.calcRunTime(beta, cpuFreq[i][j], processorsArray[i], taskList.get(taskid).computationCost.get(i)) * CalcCost.getCost(cpuFreq[i][j], processorsArray[i]);
                    //  System.out.println(tc+"\t"+tc1);
                    RW[taskid][k] = tc - tc1;//新配置下成本能够节约多少
                    if (RW[taskid][k] > 0) {
                        flag = true;
                    }
                    boolean f = true;
                    if (jinji.size() > 0) {
                        for (String str1 : strings) {
                            if (str1.equals(String.valueOf(i + "_" + cpuFreq[i][j])))
                                f = false;
                        }
                    }
                    if (flag && RW[taskid][k] < CFMin[taskid] && f) {
                        bestList.put(taskid, i + "_" + cpuFreq[i][j]);
                        // System.out.println(taskid+"  "+bestList.get(taskid));
                        CFMin[taskid] = RW[taskid][k];
                    }
                    k++;
                }
            }
        }

      /*  double max = 0;
        int t = -1;
       // 寻找变化最大的RW[i][j]
        for (int i = 0; i < taskNums; i++) {
            if (max < CFMax[i]) {
                max = CFMax[i];
                t = i;
            }
        }*/
       /* if (t != -1) {
            bestList = flag + "/" + t + "," + tempList.get(t);
        }*/

/*

     for (int i=0;i<taskNums;i++){
           System.out.println(Arrays.toString(RW[i]));
        }
        System.out.println("*******************************************************************************************************************************************************");
        System.out.println("输出CFMAX:     "+Arrays.toString(CFMax));*/

        return flag;
    }


}
