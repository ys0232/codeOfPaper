package initialSchedulingAlgorithm;

import runtest.SchedulingInit;
import taskscheduling.CalaCostofAll;
import taskscheduling.Processor;
import taskscheduling.Task;
import taskscheduling.util.CalcArrayListSum;
import taskscheduling.util.CalcCost;
import taskscheduling.util.CalcRunTime;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

/**
 * Created by lenovo on 2017/12/22.
 */
public class MINMIN {
    public static void Minmin(int processorNums, int taskNums, double beta, int priceModel,
                              String computationCostPath, String inputGraphPath, String processorInfor) throws IOException{

        //the class used for initialing
        SchedulingInit sInit = new SchedulingInit();
        //initial task info
        ArrayList<Task> taskList = sInit.initTaskInfor(computationCostPath, inputGraphPath);
        //initial communication data
        HashMap<String, Double> taskEdgeHashMap = sInit.initTaskEdge();

        //initial processor info
        Processor[] processorsArray;
        processorsArray = sInit.initProcessorInfor(processorInfor, processorNums,priceModel);

        double[][] CompletionTime=new double[taskNums][processorNums];

       boolean[] unscheduledTask=new boolean[taskNums];
        double[] taskMinCTime=new double[taskNums];
        int[] proMinCTime=new int[taskNums];
        int unscheduledNum=taskNums;
        for (int i=0;i<taskNums;i++){
            //unscheduledTaskList is used to judge whether a task is scheduled or not
            unscheduledTask[i]=true;
        }

        double cost=0.0;
        while (unscheduledNum>0) {
           // System.out.println("unScheduled task set is :\n"+ Arrays.toString(unscheduledTask));
            for (int i=0;i<taskNums;i++) {
                if (!unscheduledTask[i]){
                    continue;
                }
                Task task = taskList.get(i);
                taskMinCTime[i]=Double.MAX_VALUE;
                for (int j = 0; j < processorNums; j++) {
                    Processor pro= processorsArray[j];
                        double Eij = task.computationCost.get(j);
                        CompletionTime[i][j] = pro.availableTime+ Eij;
                        if (taskMinCTime[i]>CompletionTime[i][j]){
                            taskMinCTime[i]=CompletionTime[i][j];
                            proMinCTime[i]=j;
                        }
                }
            }
            double minCompletionTime= Double.MAX_VALUE;
            int minTp=-1;
            for(int taskid=0;taskid<taskNums;taskid++){
                //find the task tp with earliest completion time
                if (!unscheduledTask[taskid]){
                    continue;
                }
                if (minCompletionTime>taskMinCTime[taskid]){
                    minCompletionTime=taskMinCTime[taskid];
                    minTp=taskid;
                }
            }
            for (int id=0;id<processorNums;id++)
                System.out.print(processorsArray[id].availableTime+"\t");
            System.out.println();
            // assign task tp and update the ready time of assigned processor
            int proID=proMinCTime[minTp];
            processorsArray[proID].availableTime=minCompletionTime;
            Task tp=taskList.get(minTp);
            tp.selectedFre=processorsArray[proID].fMax;
            tp.selectedProcessorId=proID;
           cost+= CalcCost.getCost(tp.selectedFre, processorsArray[proID])
                   * taskList.get(minTp).computationCost.get(proID);
            // delete task tp from unscheduledTask
            unscheduledTask[minTp]=false;
            unscheduledNum-=1;
            System.out.println("remove task : "+minTp+"\tassigned processor :"+proMinCTime[minTp]
                    +"\nminCompletionTime is :"+minCompletionTime+"\tcost is : "+cost);
        }
        double makespan=Double.MIN_VALUE;
        for (int i=0;i<processorNums;i++){
            if (makespan<processorsArray[i].availableTime){
                makespan=processorsArray[i].availableTime;
            }
        }
        double maxCost= CalaCostofAll.calcCostofAll(taskList, processorsArray, beta);
        System.out.println("makespan is:"+makespan+"\tcost is: "+cost+"\tmaxcost is: "+maxCost);
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
        Minmin(processorNum, taskNums, beta, pricelModel,computationCostPath, inputGraphPath, processorInfor);

    }
}
