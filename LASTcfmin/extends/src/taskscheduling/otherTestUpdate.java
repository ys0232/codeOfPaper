package taskscheduling;

import taskscheduling.util.CalcMakeSpan;
import taskscheduling.util.CalcRunTime;
import taskscheduling.util.UpdateTaskInforAfterChangeScheduling;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by QYS on 2017/7/14.
 */
public class otherTestUpdate {

        public static double Update(ArrayList<Task> taskList, Processor[] processorsArray, int k, String changeR_F,
                                           HashMap<String, Double> taskEdgeHashMap, double beta) {

            Task task = taskList.get(k);
            String[] str=changeR_F.split("_");


            //重置处理器的可用时间信息
            for (Processor processor : processorsArray) {
                processor.availableTime = Double.MIN_VALUE;
            }
            //跳过要调换的任务之前的任务
            int updateIndex = 0;
            for (; updateIndex < taskList.size(); ++updateIndex) {
                if (updateIndex == k) {
                    break;
                }
                Task tempTask = taskList.get(updateIndex);
                int processorId = tempTask.selectedProcessorId;
                double tempAvailableTime = tempTask.timeGap.endTime;
                processorsArray[processorId].availableTime = Math.max(tempAvailableTime, processorsArray[processorId].availableTime);
            }
            //寻找父节点任务的最晚结束时间
            ArrayList<Integer> predTaskArrayList = taskList.get(k).predecessorTaskList;
            //父节点的最晚结束时间
            double timeThreshold = -1;
            for (Integer predk : predTaskArrayList) {
                double temp = 0;
                Task predTask = taskList.get(predk - 1);
                if (predTask.selectedProcessorId == task.minCostProcessorId) {
                    temp = predTask.timeGap.endTime;
                } else {
                    // System.out.println(predk + "_" + k);
                    temp = predTask.timeGap.endTime + taskEdgeHashMap.get(predk + "_" + (k + 1));
                }
                if (timeThreshold < temp) {
                    timeThreshold = temp;
                }
            }

            task.selectedProcessorId = Integer.valueOf(str[0]);
            task.selectedFre = Integer.valueOf(str[1]);
            task.timeGap.startTime = Math.max(timeThreshold, processorsArray[Integer.valueOf(str[0])].availableTime);
            double excuTime = CalcRunTime.calcRunTime(beta, task.selectedFre, processorsArray[task.selectedProcessorId], task.computationCost.get(task.selectedProcessorId));
            task.timeGap.endTime = task.timeGap.startTime + excuTime;
            task.timeGap.gap = excuTime;
            processorsArray[task.selectedProcessorId].availableTime = task.timeGap.endTime;

            ++updateIndex;
            //保持任务的处理器不变，更新时间
            for (; updateIndex < taskList.size(); ++updateIndex) {
                UpdateTaskInforAfterChangeScheduling.updateTaskInfor(updateIndex, taskList, taskEdgeHashMap, processorsArray);

            }
            double makespan = CalcMakeSpan.calcMakeSpan(taskList);
            //double sumCost = CalaCostofAll.calcCostofAll(taskList, processorsArray, beta);
            return makespan;


        }
    }

