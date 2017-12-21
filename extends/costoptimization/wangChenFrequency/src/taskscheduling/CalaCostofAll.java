package taskscheduling;

import taskscheduling.util.CalcCost;
import taskscheduling.util.CalcRunTime;

import java.util.ArrayList;

/**
 * Created by lenovo on 2017/7/9.
 */
public class CalaCostofAll {
    public static double calcCostofAll(ArrayList<Task> taskList,Processor[] processors,double beta){

        double sumMaxCost = 0;


        for(Task task: taskList){
            sumMaxCost += CalcCost.getCost(task.selectedFre,processors[task.selectedProcessorId])* CalcRunTime.calcRunTime(beta,task.selectedFre, processors[task.selectedProcessorId], task.computationCost.get(task.selectedProcessorId));
        }

        return sumMaxCost;
    }

}
