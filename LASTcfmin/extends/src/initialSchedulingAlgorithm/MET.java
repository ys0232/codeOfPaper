package initialSchedulingAlgorithm;

import runtest.SchedulingInit;
import taskscheduling.CalaCostofAll;
import taskscheduling.Processor;
import taskscheduling.Task;
import taskscheduling.util.CalcCost;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by lenovo on 2017/12/24.
 */
public class MET {
    public static void Met_CFMax(int processorNums, int taskNums, double beta, int priceModel,
                              String computationCostPath, String inputGraphPath, String processorInfor,
                           double maxTimeParameter, long starttime) throws IOException {

        //the class used for initialing
        SchedulingInit sInit = new SchedulingInit();
        //initial task info
        ArrayList<Task> taskList = sInit.initTaskInfor(computationCostPath, inputGraphPath);
        //initial communication data
        HashMap<String, Double> taskEdgeHashMap = sInit.initTaskEdge();

        //initial processor info
        Processor[] processorsArray;
        processorsArray = sInit.initProcessorInfor(processorInfor, processorNums, priceModel);
        HashMap<Integer, String> schedulerList=MET(processorNums, taskNums, taskList, taskEdgeHashMap, processorsArray);

        double makespan = Double.MIN_VALUE;
        for (int i = 0; i < processorNums; i++) {
            if (makespan < processorsArray[i].availableTime) {
                makespan = processorsArray[i].availableTime;
            }
        }
            double maxCost = CalaCostofAll.calcCostofAll(taskList, processorsArray, beta);
            System.out.println("MET makespan is:" + makespan + "\tmaxcost is: " + maxCost);
           // System.out.println(schedulerList);
        CFMax.runCFMax( processorNums,processorsArray,taskList,  taskNums,  beta,  taskEdgeHashMap,
                maxTimeParameter,starttime, schedulerList, maxCost,  makespan);

        }
    public static void Met_CFMin(int processorNums, int taskNums, double beta, int priceModel,
                                 String computationCostPath, String inputGraphPath, String processorInfor,
                                 double maxTimeParameter, long starttime) throws IOException {

        //the class used for initialing
        SchedulingInit sInit = new SchedulingInit();
        //initial task info
        ArrayList<Task> taskList = sInit.initTaskInfor(computationCostPath, inputGraphPath);
        //initial communication data
        HashMap<String, Double> taskEdgeHashMap = sInit.initTaskEdge();

        //initial processor info
        Processor[] processorsArray;
        processorsArray = sInit.initProcessorInfor(processorInfor, processorNums, priceModel);
        HashMap<Integer, String> schedulerList=MET(processorNums, taskNums, taskList, taskEdgeHashMap, processorsArray);

        double makespan = Double.MIN_VALUE;
        for (int i = 0; i < processorNums; i++) {
            if (makespan < processorsArray[i].availableTime) {
                makespan = processorsArray[i].availableTime;
            }
        }
        double maxCost = CalaCostofAll.calcCostofAll(taskList, processorsArray, beta);
        System.out.println("MET makespan is:" + makespan + "\tmaxcost is: " + maxCost);
        // System.out.println(schedulerList);
        CFMin.runCFMin( processorNums,processorsArray,taskList,  taskNums,  beta,  taskEdgeHashMap,
                maxTimeParameter,starttime, schedulerList, maxCost,  makespan);

    }
        public static HashMap<Integer, String> MET(int processorNums, int taskNums, ArrayList<Task> taskList, HashMap<java.lang.String, Double> taskEdgeHashMap,
                                           Processor[] processorsArray) throws IOException {
            HashMap<Integer, String> schedulerList = new HashMap<>();

            double[][] CompletionTime = new double[taskNums][processorNums];
            double[] taskMinCTime = new double[taskNums];
            int[] proMinCTime = new int[taskNums];
            int unscheduledNum = taskNums;
            boolean[] unscheduledTask = new boolean[taskNums];
            for (int i = 0; i < taskNums; i++) {
                //unscheduledTaskList is used to judge whether a task is scheduled or not
                unscheduledTask[i] = true;
            }

            for (int i = 0; i < taskNums; i++) {
                Task task = taskList.get(i);
                taskMinCTime[i] = Double.MAX_VALUE;
                for (int j = 0; j < processorNums; j++) {
                    Processor pro = processorsArray[j];
                    double Eij = task.computationCost.get(j);
                    CompletionTime[i][j] = Eij;
                    if (taskMinCTime[i] > CompletionTime[i][j]) {
                        taskMinCTime[i] = CompletionTime[i][j];
                        proMinCTime[i] = j;
                    }
                }
            }
            int[] completedParent=new int[taskNums];
            double[] transferTime=new double[taskNums];
            while(unscheduledNum>0){
                for (int taskid = 0; taskid < taskNums; taskid++) {
                    Task tp = taskList.get(taskid);
                    if (unscheduledTask[taskid] || tp.predecessorTaskList.size()-completedParent[taskid]>0){
                        int proID = proMinCTime[taskid];
                        processorsArray[proID].availableTime += taskMinCTime[taskid];
                        tp.selectedFre = processorsArray[proID].fMax;
                        tp.selectedProcessorId = proID;
                      schedulerList.put(taskid,proID+"_"+tp.selectedFre);
                        for (int i=0;i<taskNums;i++){
                            // remove completed task
                            Task task=taskList.get(i);
                            if (task.predecessorTaskList.contains(taskid)){
                                transferTime[i]+=taskEdgeHashMap.get(String.valueOf(taskid+"_"+i));
                                completedParent[i]+=1;
                            }
                        }

                        // delete task tp from unscheduledTask
                        unscheduledTask[taskid]=false;
                        unscheduledNum-=1;
                        //  System.out.println("remove task : " + taskid + "\tassigned processor :" + proMinCTime[taskid]
                            //        + "\nminCompletionTime is :" + taskMinCTime[taskid]);
                    }}
            }
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
        Met_CFMax(processorNum, taskNums, beta, pricelModel, computationCostPath, inputGraphPath, processorInfor,1.6,0);

    }
}

