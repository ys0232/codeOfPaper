package initialSchedulingAlgorithmJustmakespan;

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
public class MCT {
    public static double MctMakespan(int processorNums, int taskNums, double beta, int priceModel,
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
        HashMap<Integer, String> schedulerList = MCTS(processorNums, taskNums, taskList, taskEdgeHashMap, processorsArray);

      
        
        double makespan = Double.MIN_VALUE;
        for (int i = 0; i < processorNums; i++) {
            if (makespan < processorsArray[i].availableTime) {
                makespan = processorsArray[i].availableTime;
            }
        }
		return makespan;
       

    }

    public static HashMap<Integer, String> MCTS(int processorNums, int taskNums, ArrayList<Task> taskList, HashMap<java.lang.String, Double> taskEdgeHashMap,
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
       int[] completedParent=new int[taskNums];
        double[] transferTime = new double[taskNums];

        while (unscheduledNum > 0) {
            for (int i = 0; i < taskNums; i++) {
                Task task = taskList.get(i);
                int taskid=task.taskId;
                taskMinCTime[taskid] = Double.MAX_VALUE;
                if (!unscheduledTask[taskid] || task.predecessorTaskList.size() - completedParent[taskid] > 0) {
                    //check whether task is scheduled or not and all its parents tasks are scheduled or not.
                    continue;
                }
                for (int j = 0; j < processorNums; j++) {
                    Processor pro = processorsArray[j];
                    double Eij = task.computationCost.get(j) + transferTime[taskid];
                    CompletionTime[taskid][j] = pro.availableTime + Eij;
                  //  System.out.print(pro.availableTime+"\t");
                    if (taskMinCTime[taskid] > CompletionTime[taskid][j]) {
                        taskMinCTime[taskid] = CompletionTime[taskid][j];
                        proMinCTime[taskid] = j;
                    }
                }
                Processor schedPro = processorsArray[proMinCTime[taskid]];
                schedPro.availableTime = taskMinCTime[taskid];
               // System.out.println("\n"+taskMinCTime[i]+"\t"+i);
                for (int k = 0; k < taskNums; k++) {
                    // remove completed task
                    Task tp = taskList.get(k);
                    if (tp.predecessorTaskList.contains(taskid)) {
                        transferTime[k] += taskEdgeHashMap.get(String.valueOf(taskid + "_" + k));
                        completedParent[k] += 1;
                    }
                }
                unscheduledTask[taskid] = false;
                unscheduledNum -= 1;
            }
        }

        for (int i=0;i<taskNums;i++){
            Task tp=taskList.get(i);
            int proID = proMinCTime[i];
            tp.selectedFre = processorsArray[proID].fMax;
            tp.selectedProcessorId = proID;
            schedulerList.put(i,proID+"_"+tp.selectedFre);
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
        MCT.MctMakespan(processorNum, taskNums, beta, pricelModel,computationCostPath, inputGraphPath, processorInfor,1.5,0);

    }
}

