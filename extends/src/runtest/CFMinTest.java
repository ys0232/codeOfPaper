package runtest;

import taskscheduling.*;
import taskscheduling.util.*;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;


public class CFMinTest {
    public static void runHEFT(double maxTimeParameter, int processorNums, int taskNums, double beta,
                               String computationCostPath, String inputGraphPath, String processorInfor,
                               PrintWriter PWcfmax, int priceModel, long starttime) throws IOException {

        //the class used for initialing
        SchedulingInit sInit = new SchedulingInit();
        //initial task info
        ArrayList<Task> taskList = sInit.initTaskInfor(computationCostPath, inputGraphPath);
        //initial communication data
        HashMap<String, Double> taskEdgeHashMap = sInit.initTaskEdge();

        //initial processor info
        Processor[] processorsArray;
        processorsArray = sInit.initProcessorInfor(processorInfor, processorNums, priceModel);

        //compute average computation time using for the rank，计算平均计算时间
        for (int i = 0; i < taskNums; ++i) {
            Task tempTask = taskList.get(i);
            taskList.get(i).averageCost = CalcArrayListSum.calcArrayListSum(tempTask.computationCost) / processorNums;
        }

        //上部分已完成初始化工作，以下是修改部分
        int cfCount = 0;
        int freqcount = 0;
        for (int i = 0; i < processorNums; i++) {
            int tmp = (processorsArray[i].fMax - processorsArray[i].fMin) / processorsArray[i].fLevel + 1;
            if (tmp > freqcount) {
                freqcount = tmp;
            }
        }
        int[][] cpuFreq = new int[processorNums][freqcount];
        //  System.out.println(processorNums+"\t"+freqcount);
        //处理器的cpu频率
        for (int i = 0; i < processorNums; i++) {
            for (int j = 0; processorsArray[i].fMin + j * processorsArray[i].fLevel <= processorsArray[i].fMax; j++) {
                cpuFreq[i][j] = processorsArray[i].fMin + j * processorsArray[i].fLevel;
                cfCount++;
            }
        }
        // for (int i = 0; i < processorNums; i++)
        //   System.out.println(Arrays.toString(cpuFreq[i]));
        HashMap<Integer, String> schedulerList = new HashMap<>();//记录调度列表（task序号，处理器序号r_频率f）

        //compute rank,then sort，返回各节点的最长执行时间的降序排列
        ArrayList<Integer> taskOrderList = CalcTaskRankValue.calcTaskRankValue1(taskList, taskEdgeHashMap, taskNums);

        //scheduling using HEFT，对于每一个任务，选择一个合适的时间间隙插入
        for (Integer taskId : taskOrderList) {
            // System.out.print(taskId+"\t");
            TaskScheduling.taskScheduling(taskId, taskList, taskEdgeHashMap, processorsArray);
            Task tmp = taskList.get(taskId);
            //记录调度列表（task序号，处理器序号r_频率f）
            schedulerList.put(taskId, tmp.selectedProcessorId + "_" + processorsArray[tmp.selectedProcessorId].fMax);
        }
        //计算HEFT调度后的每个任务的计算开销
        CalcTaskMaxCost.calcTaskMaxCost(beta, taskList, processorsArray);

        //compute min cost in all processors，寻找代价最小的处理器的编号和level
        GetMinCostProcessorIdLevel.getMinCostProcessorIdLevel(beta, taskList, processorsArray);
        //rank task by cost difference
        List<Map.Entry<Integer, Double>> taskDisCostList = GetDisCostTaskOrder.getDisCostTaskOrder(taskList);

        //输出HEFT求得的最大CPU频率下的执行时间与计算费用
        //compute sum cost and makespan,deadline(maxTime)
        double sumCost = CalaCostofAll.calcCostofAll(taskList, processorsArray, beta);
        double makespan = CalcMakeSpan.calcMakeSpan(taskList);
        double maxTime = makespan * maxTimeParameter;//估计的截止时间
        System.out.println("Using HEFT \tmakespan: " + makespan + "\tsumCost: " + sumCost);


        double[][] RW = new double[taskNums][cfCount];
        boolean flag;
        HashMap<Integer, String> bestList = new HashMap<>();//记录每个task最大RW的i,j（task序号，i_j）
        HashMap<Integer, String> jinji = new HashMap<>();//记录已经得到但不满足时间要求的调度
        double[] CFMin = new double[taskNums];

        //计算RW矩阵，并且对每一个任务求一个RW最大的调度
        String tmp;
        flag = computeRW.computeMIN(RW, beta, schedulerList, cpuFreq, processorsArray, taskList, processorNums, bestList, jinji, CFMin);
        double lastTime = makespan;
        double lastSumCost = sumCost;
        boolean t = true;
        double tcount = 0;
        PrintWriter pw;

        //  pw=new PrintWriter("./outputDir/runtime_cfmax"+sumCost+".txt");
        // pw.write("tcount: \t cost:\n");
        //  pw.flush();

        while (flag && t) {

            long nowtime = System.currentTimeMillis();
            long difftime = (nowtime - starttime) / 500;
            if (difftime > tcount) {
                tcount = difftime;
                // System.out.println(lastSumCost);
                //  pw.write(tcount+"\t"+lastSumCost+"\n");
                // pw.flush();

            }

            double min = Double.MAX_VALUE;
            int tm = -1;
            //寻找变化最小的RW[i][j]
            for (int i = 0; i < taskNums; i++) {
                if (min > CFMin[i]) {
                    min = CFMin[i];
                    tm = i;
                }
            }
          //  System.out.println(Arrays.toString(CFMin));
            if (tm != -1) {
                tmp = tm + "," + bestList.get(tm);
                String[] str = tmp.split(",");
                int taskid = Integer.valueOf(str[0]);
                str = str[1].split("_");
                /// Task task = taskList.get(taskid);
                //保存当前状态用以回滚
                Task[] oldTaskInfor = new Task[taskList.size()];
                for (int i = 0; i < taskList.size(); ++i) {
                    oldTaskInfor[i] = new Task();
                    oldTaskInfor[i].selectedProcessorId = taskList.get(i).selectedProcessorId;
                    oldTaskInfor[i].selectedFre = taskList.get(i).selectedFre;
                    oldTaskInfor[i].timeGap = new TimeGap();
                    oldTaskInfor[i].timeGap.startTime = taskList.get(i).timeGap.startTime;
                    oldTaskInfor[i].timeGap.endTime = taskList.get(i).timeGap.endTime;
                    oldTaskInfor[i].timeGap.gap = taskList.get(i).timeGap.gap;
                }

                String[] string = newtestUpdate.newtestUpdate(taskList, processorsArray, tm, bestList, taskEdgeHashMap, beta).split("_");

                sumCost = Double.valueOf(string[1].trim());
                double time = Double.valueOf(string[0].trim());
              //  System.out.println(maxTime + " and " + time);
               // System.out.println("BESTid : " + tm + "\t" + bestList.get(tm));
                // System.out.println(" runtime is: " + time +"maxTime is: "+maxTime +" all cost is: " + sumCost);

                if (maxTime < time || ((lastTime <= time) && (lastSumCost <= sumCost))) {
                    for (int i = 0; i < taskList.size(); ++i) {
                        taskList.get(i).selectedProcessorId = oldTaskInfor[i].selectedProcessorId;
                        taskList.get(i).selectedFre = oldTaskInfor[i].selectedFre;
                        taskList.get(i).timeGap.startTime = oldTaskInfor[i].timeGap.startTime;
                        taskList.get(i).timeGap.endTime = oldTaskInfor[i].timeGap.endTime;
                        taskList.get(i).timeGap.gap = oldTaskInfor[i].timeGap.gap;
                    }
                    boolean isStrIn = false;
                    if (jinji.keySet().contains(taskid)) {
                        String[] strs = jinji.get(taskid).split(",");

                        for (int i = 0; i < strs.length; i++) {
                            if (strs[i].equals(str[0] + "_" + str[1])) {
                                isStrIn = true;
                            }
                        }
                    }
                    if (!isStrIn) {
                        jinji.put(taskid, jinji.get(taskid) + "," + str[0] + "_" + str[1]);//加入禁忌
                    }


                } else {
                    lastSumCost = sumCost;
                    lastTime = time;
                    schedulerList.put(taskid, str[0] + "_" + str[1]);
                    //System.out.println(" runtime is: " +lastTime  + " all cost is: " + lastSumCost);
                }

            } else {
                flag = false;
            }
            //System.out.println(jinji);
            if (flag)
                computeRW.computeTaskMIN(RW, beta, schedulerList, cpuFreq, processorsArray, taskList, processorNums, bestList, jinji, CFMin, tm);

            for (int i = 0; i < taskNums; i++) {
                if (jinji.keySet().contains(i)) {
                    t = false;
                    String[] strings = jinji.get(i).split(",");
                    if (strings.length != (cfCount + 1)) {
                        t = true;
                        break;
                    }
                }

            }

        }
        /*for (int i = 0; i < taskNums; i++) {
            Task task=taskList.get(i);
            System.out.println(i+"   "+task.selectedProcessorId+" "+task.selectedFre);
        }*/

        double makespanCFMax = CalcMakeSpan.calcMakeSpan(taskList);
        double sumCostCFMax = CalaCostofAll.calcCostofAll(taskList, processorsArray, beta);
        //  System.out.println(maxTime + " and " + makespanCFMax);
        System.out.println("CFMin runtime is: " + makespanCFMax + " all cost is: " + sumCostCFMax);
        // DecimalFormat df = new DecimalFormat("#.00");
        //  PWcfmax.write( df.format(makespanCFMax) + "\t" + df.format(sumCostCFMax/1e5) + "\t");
        // PWcfmax.flush();
    }

    public static void main(String[] args) throws IOException {
        int taskNums = 53;
        double beta = 0.4;
        String dirPath = "";//D:\\workspaces\\FrequenceHEFT\\
        String graphModelName = "Airsn";
        String inputGraphPath = dirPath + graphModelName + "transfer.txt";
        int processorNum = 3;
        int pricelModel = 2;
        double maxTimeParameter = 3;
        String Path = "1.txt";
        File File = new File(Path);
        PrintWriter PWcfmax = new PrintWriter(File, "utf-8");
        String processorInfor = dirPath + processorNum + ".txt";
        String computationCostPath = dirPath + graphModelName + "runtime.txt";
        runHEFT(maxTimeParameter, processorNum, taskNums, beta, computationCostPath,
                inputGraphPath, processorInfor, PWcfmax, 2, 0);
        CFMaxTest.runHEFT(maxTimeParameter, processorNum, taskNums, beta, computationCostPath,
                inputGraphPath, processorInfor, PWcfmax, 2, 0);

    }
}
