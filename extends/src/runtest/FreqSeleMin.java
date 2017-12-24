package runtest;

import taskscheduling.CalaCostofAll;
import taskscheduling.Processor;
import taskscheduling.Task;
import taskscheduling.TimeGap;
import taskscheduling.util.*;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.util.*;

public class FreqSeleMin {
    private static void freqSeleMin(double maxTimeParameter, int processorNums, int taskNums,
                                    double beta, String computationCostPath, String inputGraphPath, String processorInfor, PrintWriter PW) throws IOException

    {
        //the class used for initialing
        SchedulingInit sInit = new SchedulingInit();
        //initial task info
        ArrayList<Task> taskList = sInit.initTaskInfor(computationCostPath, inputGraphPath);
        //initial communication data
        HashMap<String, Double> taskEdgeHashMap = sInit.initTaskEdge();

        //initial processor info
        Processor[] processorsArray;
        processorsArray = sInit.initProcessorInfor(processorInfor, processorNums,2);

       //compute average computation time using for the rank，计算平均计算时间
        for (int i = 0; i < taskNums; ++i) {
            Task tempTask = taskList.get(i);
            taskList.get(i).averageCost = CalcArrayListSum.calcArrayListSum(tempTask.computationCost) / processorNums;
        }

        // for (int i = 0; i < processorNums; i++)
        //   System.out.println(Arrays.toString(cpuFreq[i]));
        HashMap<Integer, Integer> schedulerList = new HashMap<>();//记录调度列表
        HashMap<Integer, Integer> curPlan = new HashMap<>();//记录调度列表（处理器序号r，处理器r的频率）

        //compute rank,then sort，返回各节点的最长执行时间的降序排列
        ArrayList<Integer> taskOrderList = CalcTaskRankValue.calcTaskRankValue1(taskList, taskEdgeHashMap, taskNums);

        //scheduling using HEFT，对于每一个任务，选择一个合适的时间间隙插入
        //scheduling using HEFT at minimum frequency
        for (Integer taskId : taskOrderList) {
            //  System.out.print(taskId+"\t");
            TaskScheduling.taskScheduling1(taskId, taskList, beta, taskEdgeHashMap, processorsArray);
            Task tmp = taskList.get(taskId - 1);
            //记录调度列表（task序号，处理器序号r_频率f）
            schedulerList.put(taskId - 1, tmp.selectedProcessorId);
        }
        //计算HEFT调度后的每个任务的计算开销
        // CalcTaskMaxCost.calcTaskMaxCost(beta, taskList, processorsArray);

        //compute min cost in all processors，寻找代价最小的处理器的编号和level
        GetMinCostProcessorIdLevel.getMinCostProcessorIdLevel(beta, taskList, processorsArray);

        //输出HEFT求得的最大CPU频率下的执行时间与计算费用
        //compute sum cost and makespan,deadline(maxTime)
        double sumCost = CalaCostofAll.calcCostofAll(taskList, processorsArray, beta);
        double makespan = CalcMakeSpan.calcMakeSpan(taskList);
        double maxTime = makespan * maxTimeParameter;//估计的截止时间
        System.out.println("Using HEFT at minimum frequency \tmakespan: " + makespan + "\tsumCost: " + sumCost+"\tmaxTime: "+maxTime);
        //开始使用CSFS-Min算法，选择调度方案
        for (int i = 0; i < processorNums; i++) {
            curPlan.put(i, processorsArray[i].fMin);
        }
        int freq = processorsArray[0].fMin;
        int fmax = processorsArray[0].fMax;
        HashMap<Integer, Integer> candResources = new HashMap<>();
        double currentCost;
        double newCost;
        int freqStep = processorsArray[0].fLevel;
        double HEFTmakespan=makespan;
        double maxSumCost=sumCost;
        while (freq < fmax) {
            //cost of curPlan
            double[] currentMakespanfr = new double[processorNums];//当前各资源的计算时间总和
            double[] currentCostfr = new double[processorNums];//当前各资源的计算成本总和
            for (int i = 0; i < taskNums; i++) {
                int proid = schedulerList.get(i);//处理器序号
                currentMakespanfr[proid] += CalcRunTime.calcRunTime(beta, curPlan.get(proid), processorsArray[proid], taskList.get(i).computationCost.get(proid));
                currentCostfr[proid] += CalcRunTime.calcRunTime(beta, curPlan.get(proid), processorsArray[proid], taskList.get(i).computationCost.get(proid))
                        * CalcCost.getCost(curPlan.get(proid), processorsArray[proid]);
            }
            currentCost = 0;//当前计算总成本
            for (int i = 0; i < processorNums; i++) {
                currentCost += currentCostfr[i];
            }
            //System.out.println(currentCost);
            newCost = currentCost;//新调度方案的计算总成本
            freq += freqStep;//freq-=freqStep  处理器频率改变
            HashMap<Integer, Integer> newPlan = curPlan;//newPlan=curPlan
            //candResources=newPlan.keySet();
            for (int i = 0; i < newPlan.size(); i++) {
                candResources.put(i, i);
            }

            double[] diffT = new double[processorNums];//时间差
            double diffNewPlanTime = 0;
            int maxR;
            while (!candResources.isEmpty()) {
                double maxMakespanSaving = 0;
                maxR = -1;

                double[] newMakespanf = new double[processorNums];//新调度方案的各资源计算时间总和
                double[] newCostf = new double[processorNums];//新调度方案的各资源计算成本总和
                for (int i = 0; i < taskNums; i++) {
                    int proid = schedulerList.get(i);//处理器序号
                    newMakespanf[proid] += CalcRunTime.calcRunTime(beta, freq, processorsArray[proid], taskList.get(i).computationCost.get(proid));
                    newCostf[proid] += CalcRunTime.calcRunTime(beta, freq, processorsArray[proid], taskList.get(i).computationCost.get(proid))
                            * CalcCost.getCost(freq, processorsArray[proid]);
                }

                Iterator iter = candResources.keySet().iterator();
                int i;
                while (iter.hasNext()) {
                    i = candResources.get(iter.next());
                    //  System.out.println(i+"\t");
                    diffT[i] = 0;
                /*    //求运行时间
                    for (int taskid = 0; taskid < taskNums; taskid++) {
                        if (schedulerList.get(taskid) == i) {
                            double thisTime = CalcRunTime.calcRunTime(beta, freq, processorsArray[i], taskList.get(taskid).computationCost.get(i));
                            double lastTime = CalcRunTime.calcRunTime(beta, curPlan.get(i), processorsArray[i], taskList.get(taskid).computationCost.get(i));
                            diffT[i] += thisTime - lastTime;//运行的时间差
                        }
                    }*/
                    //System.out.println(i+"\t"+diffT[i]);

                    double timeSavings = currentMakespanfr[i] - newMakespanf[i];//各资源新旧调度方案的计算时间差
                    if (timeSavings < 0) {
                        iter.remove();
                    } else if (maxMakespanSaving < timeSavings) {
                        maxMakespanSaving = timeSavings;
                        maxR = i;
                    }

                }

                //System.out.println(newCost+"\t"+);

                if (maxR != -1) {
                    candResources.remove(maxR);
                    //update task runtimes for each task t belong to maxR,本程序没有保存任务运行时间的数据结构
                    //  processorsArray[maxR].fre = freq;
                    //System.out.println(candResources);
                    newPlan.put(maxR, freq);
                    diffNewPlanTime += maxMakespanSaving;
                    newCost = newCost - currentCostfr[maxR] + newCostf[maxR];

                    //  System.out.println( maxCostSaving+"\t" + diffT[maxR] + "\t" + (diffNewPlanTime+ makespan) + "\t" + maxR);
                    //update start time and finish time of every task
                }
            }

            if (newCost < currentCost) {

                curPlan = newPlan;
                makespan += diffNewPlanTime;
                 // System.out.println("new makespan: "+makespan+"    "+newCost);
            }
        }
        currentCost = 0;
        for (int i = 0; i < taskNums; i++) {
            int proid = schedulerList.get(i);//处理器序号
            currentCost += CalcRunTime.calcRunTime(beta, curPlan.get(proid), processorsArray[proid], taskList.get(i).computationCost.get(proid))
                    * CalcCost.getCost(curPlan.get(proid), processorsArray[proid]);
        }
       /* if (makespan > maxTime) {
            //scheduling using HEFT at maximum frequency
            for (Integer taskId : taskOrderList) {
                //  System.out.print(taskId+"\t");
                TaskScheduling.taskScheduling(taskId, taskList, taskEdgeHashMap, processorsArray);
                CalcTaskMaxCost.calcTaskMaxCost(beta, taskList, processorsArray);
                 currentCost = CalcSumMaxCost.calcSumMaxCost(taskList);
                 makespan = CalcMakeSpan.calcMakeSpan(taskList);
            }

        }*/
        System.out.println("Using CSFSMax \tmakespan: " + makespan + "\tsumCost: " + currentCost);
        DecimalFormat df = new DecimalFormat("#.00");
        PW.write(df.format(HEFTmakespan) + "\t" + df.format(maxSumCost) + "\t" + df.format(maxTime) +
                "\t" + df.format(makespan) + "\t" + df.format(currentCost) + "\t" + maxTimeParameter + "\n");
        PW.flush();
    }


    public static void main(String[] args) throws IOException {

        int taskNums = 53;
        double beta = 0.4;
        String dirPath = "";//D:\\workspaces\\FrequenceHEFT\\
        String graphModelName = "Airsn";
        String inputGraphPath = dirPath + "airsn.txt";

        ArrayList<Integer> processorNumsArray = new ArrayList<>();
        processorNumsArray.add(3);
        processorNumsArray.add(5);
        processorNumsArray.add(8);

        ArrayList<Double> maxTimeParatemerArray = new ArrayList<>();
        //maxTimeParatemerArray.add(0.9);
        maxTimeParatemerArray.add(1.1);
        maxTimeParatemerArray.add(1.3);
        maxTimeParatemerArray.add(1.5);
        maxTimeParatemerArray.add(1.6);
        maxTimeParatemerArray.add(1.7);
        maxTimeParatemerArray.add(1.8);
        maxTimeParatemerArray.add(2.0);
        maxTimeParatemerArray.add(2.3);
        maxTimeParatemerArray.add(2.5);
        maxTimeParatemerArray.add(5.0);
        //保存结果文件
        String dir = "./result/";
        String OtherPath = dir + graphModelName + "_CSFS-MIN_000temp.txt";
        File emFile = new File(OtherPath);
        PrintWriter PWother = new PrintWriter(emFile, "utf-8");
        PWother.write("HEFTmakespan:\tHEFTsumcost:\tmaxTime:\tCSFS-Maxmakespan:\tCSFS-Maxsumcost:\tMaxTimeParameter:\t\n");
        for (Integer processorNum : processorNumsArray) {
            for (Double maxTimeParameter : maxTimeParatemerArray) {
                String computationCostPath = dirPath + graphModelName + processorNum + ".txt";
                String processorInfor = dir + processorNum + ".txt";
                FreqSeleMin.freqSeleMin(maxTimeParameter, processorNum, taskNums,
                        beta, computationCostPath, inputGraphPath, processorInfor,PWother);
            }
        }
    }
}
