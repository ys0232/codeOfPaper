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
public class MCT {
    static void Mct(int processorNums, int taskNums, double beta, int priceModel,
                       String computationCostPath, String inputGraphPath, String processorInfor) throws IOException {

        //the class used for initialing
        SchedulingInit sInit = new SchedulingInit();
        //initial task info
        ArrayList<Task> taskList = sInit.initTaskInfor(computationCostPath, inputGraphPath);
        //initial communication data
        HashMap<String, Double> taskEdgeHashMap = sInit.initTaskEdge();

        //initial processor info
        Processor[] processorsArray;
        processorsArray = sInit.initProcessorInfor(processorInfor, processorNums, priceModel);

        double[][] CompletionTime = new double[taskNums][processorNums];
        double[] taskMinCTime = new double[taskNums];
        int[] proMinCTime = new int[taskNums];

        for (int i = 0; i < taskNums; i++) {
            Task task = taskList.get(i);
            taskMinCTime[i] = Double.MAX_VALUE;
            for (int j = 0; j < processorNums; j++) {
                Processor pro = processorsArray[j];
                double Eij = task.computationCost.get(j);
                CompletionTime[i][j] = pro.availableTime + Eij;
                if (taskMinCTime[i] > CompletionTime[i][j]) {
                    taskMinCTime[i] = CompletionTime[i][j];
                    proMinCTime[i] = j;
                }
            }
        }

        double cost = 0.0;

        for (int taskid = 0; taskid < taskNums; taskid++) {
            int proID = proMinCTime[taskid];
            processorsArray[proID].availableTime += taskMinCTime[taskid];
            Task tp = taskList.get(taskid);
            tp.selectedFre = processorsArray[proID].fMax;
            tp.selectedProcessorId = proID;
            cost += CalcCost.getCost(tp.selectedFre, processorsArray[proID])
                    * taskList.get(taskid).computationCost.get(proID);
            // delete task tp from unscheduledTask
            System.out.println("remove task : " + taskid + "\tassigned processor :" + proMinCTime[taskid]
                    + "\nminCompletionTime is :" + taskMinCTime[taskid] + "\tcost is : " + cost);


        }
        double makespan = Double.MIN_VALUE;
        for (int i = 0; i < processorNums; i++) {
            if (makespan < processorsArray[i].availableTime) {
                makespan = processorsArray[i].availableTime;
            }
        }
        double maxCost = CalaCostofAll.calcCostofAll(taskList, processorsArray, beta);
        System.out.println("makespan is:" + makespan + "\tcost is: " + cost + "\tmaxcost is: " + maxCost);
    }
    public static void main(String[] args)throws IOException{

        int taskNums = 53;
        double beta = 0.4;
        String dirPath = "";//D:\\workspaces\\FrequenceHEFT\\
        String graphModelName = "Airsn";
        String inputGraphPath = dirPath +graphModelName+ "transfer.txt";
        int processorNum=3;
        int pricelModel=2;
        String processorInfor = dirPath + processorNum + ".txt";
        String computationCostPath = dirPath + graphModelName + "runtime.txt";
        Mct(processorNum, taskNums, beta, pricelModel,computationCostPath, inputGraphPath, processorInfor);

    }
}
