package taskscheduling.util;

import java.util.ArrayList;
import java.util.HashMap;

import taskscheduling.Task;

public class CalcEST {

    //最早开始时间
    public static double calcEST(int processorId, int taskId, double availableTime, ArrayList<Task> taskList,
                                 HashMap<String, Double> taskEdgeHashMap) {

        ArrayList<Integer> predTaskArrayList = taskList.get(taskId ).predecessorTaskList;
        double maxFinishTime = -1.0;

        for (Integer predTaskId : predTaskArrayList) {
            Task predTask = taskList.get(predTaskId);
            double tempTime = 0.0;
            if (predTask.selectedProcessorId == processorId) {
                tempTime = predTask.timeGap.endTime;
            } else {
                tempTime = predTask.timeGap.endTime + taskEdgeHashMap.get(predTaskId + "_" + (taskId));
            }
            if (maxFinishTime < tempTime) {
                maxFinishTime = tempTime;
            }
        }

        return Math.max(availableTime, maxFinishTime);
    }

}
