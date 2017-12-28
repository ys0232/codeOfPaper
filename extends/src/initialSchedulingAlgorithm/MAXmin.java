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
public class MAXmin {
    public static void MaxMin_CFMax(int processorNums, int taskNums, double beta, int priceModel,
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
        HashMap<Integer, String> schedulerList;//record scheduled list（taskid，processor_frequency）
        schedulerList = MaxMin(processorNums, taskNums, taskList, taskEdgeHashMap, processorsArray);//use max-min to get a schedule list
        double makespan = Double.MIN_VALUE;
        for (int i = 0; i < processorNums; i++) {
            if (makespan < processorsArray[i].availableTime) {
                makespan = processorsArray[i].availableTime;
            }
        }
        double maxCost = CalaCostofAll.calcCostofAll(taskList, processorsArray, beta);
        System.out.println("MAX-MIN makespan is:" + makespan + "\tmaxcost is: " + maxCost);
        CFMax.runCFMax(processorNums, processorsArray, taskList, taskNums, beta, taskEdgeHashMap,
                maxTimeParameter, starttime, schedulerList, maxCost, makespan);


    }

    public static HashMap<Integer, String> MaxMin(int processorNums, int taskNums, ArrayList<Task> taskList, HashMap<String, Double> taskEdgeHashMap,
                                                  Processor[] processorsArray) {
        double[][] CompletionTime = new double[taskNums][processorNums];
        HashMap<Integer, String> schedulerList = new HashMap<>();

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
                    continue;
                }
                // Task task = taskList.get(i);
                taskMinCTime[i] = Double.MAX_VALUE;
                for (int j = 0; j < processorNums; j++) {
                    Processor pro = processorsArray[j];
                    double Eij = task.computationCost.get(j);
                    CompletionTime[i][j] = pro.availableTime + Eij + transferTime[i];
                    if (taskMinCTime[i] > CompletionTime[i][j]) {
                        taskMinCTime[i] = CompletionTime[i][j];
                        proMinCTime[i] = j;
                    }
                }
            }
            double maxCompletionTime = Double.MIN_VALUE;
            int minTp = -1;
            for (int taskid = 0; taskid < taskNums; taskid++) {
                //find the task tp with earliest completion time
                Task task = taskList.get(taskid);
                if (!unscheduledTask[taskid] || task.predecessorTaskList.size() - completedParent[taskid] > 0) {
                    continue;
                }
                if (maxCompletionTime < taskMinCTime[taskid]) {
                    maxCompletionTime = taskMinCTime[taskid];
                    minTp = taskid;
                }
            }
       /*     for (int id=0;id<processorNums;id++)
                System.out.print(processorsArray[id].availableTime+"\t");
            System.out.println();*/
            // assign task tp and update the ready time of assigned processor
            int proID = proMinCTime[minTp];
            processorsArray[proID].availableTime = maxCompletionTime;
            Task tp = taskList.get(minTp);
            tp.selectedFre = processorsArray[proID].fMax;
            tp.selectedProcessorId = proID;
            schedulerList.put(minTp, proID + "_" + tp.selectedFre);
            // tp.isCompleted=true;//now this task is completed
            for (int i = 0; i < taskNums; i++) {
                // remove completed task
                Task task = taskList.get(i);
                if (task.predecessorTaskList.contains(minTp)) {
                    transferTime[i] += taskEdgeHashMap.get(String.valueOf(minTp + "_" + i));
                    completedParent[i] += 1;
                }
            }

            // delete task tp from unscheduledTask
            unscheduledTask[minTp] = false;
            unscheduledNum -= 1;
            //    System.out.println("remove task : "+minTp+"\tassigned processor :"+proMinCTime[minTp]
            //          +"\nminCompletionTime is :"+maxCompletionTime+"\tcost is : "+cost);
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
        MAXmin.MaxMin_CFMax(processorNum, taskNums, beta, pricelModel, computationCostPath, inputGraphPath, processorInfor, 1.5, 0);

    }
}

