package taskscheduling;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import taskscheduling.Processor;
import taskscheduling.Task;
import taskscheduling.util.*;

public class newtestUpdate {
    public static String newtestUpdate(ArrayList<Task> taskList, Processor[] processorsArray, int k, HashMap<Integer, String> bestList,
                                       HashMap<String, Double> taskEdgeHashMap, double beta) {

        String[] str;
        int taskid = k;
        str = bestList.get(k).split("_");
        Task task = taskList.get(taskid);

        //閲嶇疆澶勭悊鍣ㄧ殑鍙敤鏃堕棿淇℃伅
        for (Processor processor : processorsArray) {
            processor.availableTime = Double.MIN_VALUE;
        }
        //璺宠繃瑕佽皟鎹㈢殑浠诲姟涔嬪墠鐨勪换鍔�
        int updateIndex = 0;
        for (; updateIndex < taskList.size(); ++updateIndex) {
            if (updateIndex == taskid) {
                break;
            }
            Task tempTask = taskList.get(updateIndex);
            int processorId = tempTask.selectedProcessorId;
            double tempAvailableTime = tempTask.timeGap.endTime;
            processorsArray[processorId].availableTime = Math.max(tempAvailableTime, processorsArray[processorId].availableTime);
        }
        //瀵绘壘鐖惰妭鐐逛换鍔＄殑鏈�鏅氱粨鏉熸椂闂�
        ArrayList<Integer> predTaskArrayList = taskList.get(taskid).predecessorTaskList;
        //鐖惰妭鐐圭殑鏈�鏅氱粨鏉熸椂闂�
        double timeThreshold = -1;
        for (Integer predTaskId : predTaskArrayList) {
            double temp = 0;
            Task predTask = taskList.get(predTaskId);
            if (predTask.selectedProcessorId == Integer.valueOf(str[0])) {
                temp = predTask.timeGap.endTime;
            } else {
                // System.out.println(predTaskId + "_" + taskid);
                temp = predTask.timeGap.endTime + taskEdgeHashMap.get(predTaskId + "_" + (taskid));
            }
            if (timeThreshold < temp) {
                timeThreshold = temp;
            }
        }

        task.selectedProcessorId = Integer.valueOf(str[0]);
        task.selectedFre = Integer.valueOf(str[1]);
        task.timeGap.startTime = Math.max(timeThreshold, processorsArray[Integer.valueOf(str[0])].availableTime);
        double excuTime = CalcRunTime.calcRunTime(beta, task.selectedFre,
                processorsArray[task.selectedProcessorId], task.computationCost.get(task.selectedProcessorId));
        task.timeGap.endTime = task.timeGap.startTime + excuTime;
        task.timeGap.gap = excuTime;
        processorsArray[task.selectedProcessorId].availableTime = task.timeGap.endTime;

        ++updateIndex;
        //淇濇寔浠诲姟鐨勫鐞嗗櫒涓嶅彉锛屾洿鏂版椂闂�
        for (; updateIndex < taskList.size(); ++updateIndex) {
            UpdateTaskInforAfterChangeScheduling.updateTaskInforNew(updateIndex, taskList, beta, taskEdgeHashMap, processorsArray);

        }
        double makespan = CalcMakeSpan.calcMakeSpan(taskList);
        double sumCost = CalaCostofAll.calcCostofAll(taskList, processorsArray, beta);
        return makespan + " _" + sumCost;


    }

    public static String newtestUpdateFreqSele(ArrayList<Task> taskList, Processor[] processorsArray, int k, String bestList,
                                               HashMap<String, Double> taskEdgeHashMap, double beta) {

        String[] str;
        int taskid = k;
        str = bestList.split("_");
        Task task = taskList.get(taskid);


        for (Processor processor : processorsArray) {
            processor.availableTime = Double.MIN_VALUE;
        }
        int updateIndex = 0;
        for (; updateIndex < taskList.size(); ++updateIndex) {
            if (updateIndex == taskid) {
                break;
            }
            Task tempTask = taskList.get(updateIndex);
            int processorId = tempTask.selectedProcessorId;
            double tempAvailableTime = tempTask.timeGap.endTime;
            processorsArray[processorId].availableTime = Math.max(tempAvailableTime, processorsArray[processorId].availableTime);
        }

        ArrayList<Integer> predTaskArrayList = taskList.get(taskid).predecessorTaskList;

        double timeThreshold = -1;
        for (Integer predTaskId : predTaskArrayList) {
            double temp = 0;
            Task predTask = taskList.get(predTaskId);
            if (predTask.selectedProcessorId == Integer.valueOf(str[0])) {
                temp = predTask.timeGap.endTime;
            } else {
// System.out.println(predTaskId + "_" + taskid);
                temp = predTask.timeGap.endTime + taskEdgeHashMap.get(predTaskId + "_" + (taskid));
            }
            if (timeThreshold < temp) {
                timeThreshold = temp;
            }
        }

        task.selectedProcessorId = Integer.valueOf(str[0]);
        task.selectedFre = Integer.valueOf(str[1]);
        task.timeGap.startTime = Math.max(timeThreshold, processorsArray[Integer.valueOf(str[0])].availableTime);
        double excuTime = CalcRunTime.calcRunTime(beta, task.selectedFre,
                processorsArray[task.selectedProcessorId], task.computationCost.get(task.selectedProcessorId));
        task.timeGap.endTime = task.timeGap.startTime + excuTime;
        task.timeGap.gap = excuTime;
        processorsArray[task.selectedProcessorId].availableTime = task.timeGap.endTime;

        ++updateIndex;
        for (; updateIndex < taskList.size(); ++updateIndex) {
            UpdateTaskInforAfterChangeScheduling.updateTaskInforNew(updateIndex, taskList, beta, taskEdgeHashMap, processorsArray);

        }
        double makespan = CalcMakeSpan.calcMakeSpan(taskList);
        double sumCost = CalaCostofAll.calcCostofAll(taskList, processorsArray, beta);
        return makespan + " _" + sumCost;


    }

}
