package runtest;

import taskscheduling.CalaCostofAll;
import taskscheduling.Processor;
import taskscheduling.Task;
import taskscheduling.TimeGap;
import taskscheduling.newtestUpdate;
import taskscheduling.util.*;

import java.awt.geom.Arc2D;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.util.*;

public class FreqSeleMax {
    public static void freqSeleMax(double maxTimeParameter, int processorNums, int taskNums,
                                   double beta, String computationCostPath, String inputGraphPath,
                                   String processorInfor, PrintWriter PW, int priceModel,long starttime)
            throws IOException {

        //the class used for initialing
        SchedulingInit sInit = new SchedulingInit();
        //initial task info
        ArrayList<Task> taskList = sInit.initTaskInfor(computationCostPath, inputGraphPath);
        //initial communication data
        HashMap<String, Double> taskEdgeHashMap = sInit.initTaskEdge();

        //initial processor info
        Processor[] processorsArray;
        processorsArray = sInit.initProcessorInfor(processorInfor, processorNums, priceModel);

        //compute average computation time using for the rank
        for (int i = 0; i < taskNums; ++i) {
            Task tempTask = taskList.get(i);
            taskList.get(i).averageCost = CalcArrayListSum.calcArrayListSum(tempTask.computationCost) / processorNums;
        }

        // for (int i = 0; i < processorNums; i++)
        //   System.out.println(Arrays.toString(cpuFreq[i]));
        HashMap<Integer, String> schedulerList = new HashMap<>();
        HashMap<Integer, Integer> curPlan = new HashMap<>();

        //compute rank,then sort
        ArrayList<Integer> taskOrderList = CalcTaskRankValue.calcTaskRankValue1(taskList, taskEdgeHashMap, taskNums);

        //scheduling using HEFT
        for (Integer taskId : taskOrderList) {
            //  System.out.print(taskId+"\t");
            TaskScheduling.taskScheduling(taskId, taskList, taskEdgeHashMap, processorsArray);
            Task tmp = taskList.get(taskId);

            int t = tmp.selectedProcessorId;
            if (schedulerList.containsKey(t))
                schedulerList.put(t, schedulerList.get(t) + "," + (taskId));
            else schedulerList.put(t, String.valueOf(taskId));
        }

        CalcTaskMaxCost.calcTaskMaxCost(beta, taskList, processorsArray);

        //compute min cost in all processors
        GetMinCostProcessorIdLevel.getMinCostProcessorIdLevel(beta, taskList, processorsArray);
        //rank task by cost difference
        GetDisCostTaskOrder.getDisCostTaskOrder(taskList);


        //compute min cost in all processors
        // GetMinCostProcessorIdLevel.getMinCostProcessorIdLevel(beta, taskList, processorsArray);


        //compute sum cost and makespan,deadline(maxTime)
        double sumCost = CalaCostofAll.calcCostofAll(taskList, processorsArray, beta);
        double makespan = CalcMakeSpan.calcMakeSpan(taskList);
        double maxTime = makespan * maxTimeParameter;
        System.out.println("Using HEFT \tmakespan: " + makespan + "\tsumCost: " + sumCost);

        for (int i = 0; i < processorNums; i++) {
            curPlan.put(i, processorsArray[i].fMax);
        }
        //System.out.println(curPlan);
        HashMap<Integer, Integer> candResources = new HashMap<>();
        double currentCost = sumCost;
        double newCost;
        //int freqStep = processorsArray[0].fLevel;
        double HEFTmakespan = makespan;
        boolean flag = true;
        Iterator iter;
        double maxSumCost = sumCost;
        double[] newruntime = new double[processorNums];
        double[] tempcost=new double[processorNums];
        double makespanTEMP = makespan;
      //  double tcount=0;
        PrintWriter pw1;
        long tcount=0;
        pw1=new PrintWriter("./outputDir/runtime_csfs"+sumCost+".txt");
        pw1.write("tcount: \t cost:\n");
        pw1.flush();
        while (flag) {
            //cost of curPlan
            double[] currentCostfr = new double[processorNums];
            currentCost = 0;
            HashMap<Integer, Integer> newPlan = new HashMap<>();
            Iterator iter1 = curPlan.keySet().iterator();
            while (iter1.hasNext()) {
                int i = (int) iter1.next();
                String[] strings = schedulerList.get(i).split(",");
                for (String str : strings) {
                    int t = Integer.valueOf(str);
                    currentCostfr[i] += CalcRunTime.calcRunTime(beta, curPlan.get(i), processorsArray[i], taskList.get(t).computationCost.get(i))
                            * CalcCost.getCost(curPlan.get(i), processorsArray[i]);
                }
                currentCost += currentCostfr[i];
                newPlan.put(i, curPlan.get(i));//newPlan=curPlan
            }
           /* for (int i = 0; i < processorNums; i++)
                currentCost += currentCostfr[i];*/
            // System.out.println(currentCost);
            newCost = currentCost;
            // freq -= freqStep;//freq-=freqStep
            //candResources=newPlan.keySet();
            int maxR;
            //int count=0;
            // System.out.println(curPlan+"\t"+newPlan);
            flag = false;
            Iterator iter2 = newPlan.keySet().iterator();
            while (iter2.hasNext()) {
                int i = (int) iter2.next();
                if (newPlan.get(i) > processorsArray[i].fMin) {
                    flag = true;
                    newPlan.put(i, newPlan.get(i) - processorsArray[i].fLevel);//freq-=freqStep
                    candResources.put(i, i);
                    // System.out.println(i + "\t" + newPlan.get(i));
                } else iter2.remove();
            }
           //  System.out.println(curPlan+"\t"+newPlan+"\t"+candResources);
            Task[] oldTask = new Task[taskList.size()];
            for (int id = 0; id < taskList.size(); ++id) {
                oldTask[id] = new Task();
                oldTask[id].selectedProcessorId = taskList.get(id).selectedProcessorId;
                oldTask[id].selectedFre = taskList.get(id).selectedFre;
                oldTask[id].timeGap = new TimeGap();
                oldTask[id].timeGap.startTime = taskList.get(id).timeGap.startTime;
                oldTask[id].timeGap.endTime = taskList.get(id).timeGap.endTime;
                oldTask[id].timeGap.gap = taskList.get(id).timeGap.gap;
            }
            HashMap<Integer, Integer> tempPlan = curPlan;
            while (!candResources.isEmpty()) {
                double maxCostSaving = 0;
                maxR = -1;
                // int freq = 0;
                double[] newCostf = new double[processorNums];
                iter = newPlan.keySet().iterator();

                while (iter.hasNext()) {
                    int i = (int) iter.next();
                    String[] strings = schedulerList.get(i).split(",");
                    for (String str : strings) {
                        int t = Integer.valueOf(str);
                        //  System.out.println(newPlan.keySet());
                        //  System.out.println(t+"\t"+i+"\t"+newPlan.get(i)+"\t"+newPlan.size());
                        newCostf[i] += CalcRunTime.calcRunTime(beta, newPlan.get(i), processorsArray[i], taskList.get(t).computationCost.get(i))
                                * CalcCost.getCost(newPlan.get(i), processorsArray[i]);
                    }
                }
                //System.out.println(newPlan);
                // for( int i=0;i<candResources.size();i++) {
                iter = candResources.keySet().iterator();
                int i;
                //    System.out.println(candResources);
                while (iter.hasNext()) {
                    //  System.out.println(i+"\t");
                    //int i=candResources.get(j);
                    i = (int) (iter.next());
                    // System.out.println(candResources+"\t"+i);
                    String bestList = i + "_" + newPlan.get(i);
                    Task[] oldTaskInfor = new Task[taskList.size()];
                    for (int id = 0; id < taskList.size(); ++id) {
                        oldTaskInfor[id] = new Task();
                        oldTaskInfor[id].selectedProcessorId = taskList.get(id).selectedProcessorId;
                        oldTaskInfor[id].selectedFre = taskList.get(id).selectedFre;
                        oldTaskInfor[id].timeGap = new TimeGap();
                        oldTaskInfor[id].timeGap.startTime = taskList.get(id).timeGap.startTime;
                        oldTaskInfor[id].timeGap.endTime = taskList.get(id).timeGap.endTime;
                        oldTaskInfor[id].timeGap.gap = taskList.get(id).timeGap.gap;
                    }
                    String strs = "";
                    String[] tasks = schedulerList.get(i).split(",");
                    for (String task : tasks) {
                        int taskid = Integer.valueOf(task);
                        strs = newtestUpdate.newtestUpdateFreqSele(taskList, processorsArray, taskid, bestList, taskEdgeHashMap, beta);
                    }
                    String[] str = new String[2];
                    if (strs.length() > 0) {
                        str = strs.split("_");
                    }
                    newruntime[i] = Double.valueOf(str[0]);
                    tempcost[i]=Double.valueOf(str[1]);

                    for (int id = 0; id < taskList.size(); ++id) {
                        taskList.get(id).selectedProcessorId = oldTaskInfor[id].selectedProcessorId;
                        taskList.get(id).selectedFre = oldTaskInfor[id].selectedFre;
                        taskList.get(id).timeGap.startTime = oldTaskInfor[id].timeGap.startTime;
                        taskList.get(id).timeGap.endTime = oldTaskInfor[id].timeGap.endTime;
                        taskList.get(id).timeGap.gap = oldTaskInfor[id].timeGap.gap;
                    }


                    double costSavings = currentCostfr[i] - newCostf[i];
                    if (costSavings <= 0 || (newruntime[i]) > maxTime) {
                        iter.remove();
                    } else if (maxCostSaving < costSavings) {
                        maxCostSaving = costSavings;
                        maxR = candResources.get(i);
                    }
                   // System.out.println(i+"\t"+costSavings+"\t"+newruntime[i]);
                }

                //  System.out.println(candResources+"\t"+maxR);

                if (maxR != -1) {
                    candResources.remove(maxR);
                    //update task runtimes for each task t belong to maxR,
                    //  processorsArray[maxR].fre = freq;
                    //System.out.println(candResources);
                    tempPlan.put(maxR, newPlan.get(maxR));
                    // diffNewPlanTime += diffT[maxR];
                   // makespanTEMP = newruntime[maxR];
                  //  newCost = tempcost[maxR];
                    String bestList = maxR + "_" + tempPlan.get(maxR);
                    String[] tasks = schedulerList.get(maxR).split(",");
                    String string="";
                    for (String task : tasks) {
                        int taskid = Integer.valueOf(task);
                        string=newtestUpdate.newtestUpdateFreqSele(taskList, processorsArray, taskid, bestList, taskEdgeHashMap, beta);
                    }
                    String[] strs=string.split("_");
                    makespanTEMP= Double.valueOf(strs[0]);
                    newCost= Double.valueOf(strs[1]);

                }
                    long nowtime=System.currentTimeMillis();
                    long difftime=(nowtime-starttime)/500;
                    if(difftime>tcount){
                        tcount=difftime;
                        //System.out.println(newCost);
                        pw1.write(tcount+"\t"+newCost+"\n");
                        pw1.flush();
                    }
                    //  System.out.println( maxCostSaving+"\t" + diffT[maxR] + "\t" + (diffNewPlanTime+ makespan) + "\t" + maxR);
                    //update start time and finish time of every task
               //    System.out.println( maxR+"\t"+newCost+"\t"+currentCost+"\t"+makespanTEMP+"\t"+maxTime);

             //   System.out.println(tempPlan);
            }

            if (newCost < currentCost && makespanTEMP<maxTime) {
                curPlan = tempPlan;
                for (int i = 0; i < curPlan.size(); i++) {
                    String bestList = i+ "_" + curPlan.get(i);
                    String[] tasks = schedulerList.get(i).split(",");
                    String string="";
                    for (String task : tasks) {
                        int taskid = Integer.valueOf(task);
                        string=newtestUpdate.newtestUpdateFreqSele(taskList, processorsArray, taskid, bestList, taskEdgeHashMap, beta);
                    }
                    String[] strs=string.split("_");
                    makespan= makespanTEMP;
                    currentCost= Double.valueOf(strs[1]);
                }
           //     System.out.println("new makespan: " + makespan + "\t" +currentCost);
            } else {
                for (int id = 0; id < taskList.size(); ++id) {
                    taskList.get(id).selectedProcessorId = oldTask[id].selectedProcessorId;
                    taskList.get(id).selectedFre = oldTask[id].selectedFre;
                    taskList.get(id).timeGap.startTime = oldTask[id].timeGap.startTime;
                    taskList.get(id).timeGap.endTime = oldTask[id].timeGap.endTime;
                    taskList.get(id).timeGap.gap = oldTask[id].timeGap.gap;
                }

                break;
            }
        }
      /*  currentCost = 0;
        iter = curPlan.keySet().iterator();
        while (iter.hasNext()) {
            int i = (int) iter.next();
            String[] strings = schedulerList.get(i).split(",");
            for (String str : strings) {
                int t = Integer.valueOf(str);
                currentCost += CalcRunTime.calcRunTime(beta, curPlan.get(i), processorsArray[i], taskList.get(t).computationCost.get(i))
                        * CalcCost.getCost(curPlan.get(i), processorsArray[i]);
            }
        }*/
        System.out.println("Using CSFSMax \tmakespan: " + makespan + "\tsumCost: " + currentCost + "\t" + maxTime);
        DecimalFormat df = new DecimalFormat("#.00");
        PW.write(maxTimeParameter+"\t"+df.format(maxTime)+"\t"+df.format(HEFTmakespan) + "\t" + df.format(maxSumCost/1e5) +"\t");
        PW.write(df.format(makespan) + "\t" + df.format(currentCost/1e5)+"\n");
        PW.flush();
    }


    public static void main(String[] args) throws IOException {
        String inputdir = "./inputDir/";
        String processordir = "/ProcessorFile/";
        String outputdir = "./outputDir/";

        int taskNums = 1000;
        int priceModel = 3;
        double beta = 0.36;
        String DAGmodel = "Montage";
        String inputDAG = inputdir + DAGmodel + "/" + DAGmodel + "_" + taskNums;
        String inputGraphPath = inputDAG + "_" + (taskNums + 2) + ".txt";

        ArrayList<Double> maxTimeParatemerArray = new ArrayList<>();
        //maxTimeParatemerArray.add(0.9);
        //maxTimeParatemerArray.add(1.1);
        //  maxTimeParatemerArray.add(1.3);
        // maxTimeParatemerArray.add(1.5);
        // maxTimeParatemerArray.add(1.6);
        // maxTimeParatemerArray.add(1.7);
        //maxTimeParatemerArray.add(1.8);
        maxTimeParatemerArray.add(3.0);
        // maxTimeParatemerArray.add(2.3);
        //  maxTimeParatemerArray.add(2.5);
        // maxTimeParatemerArray.add(2.8);
        //  maxTimeParatemerArray.add(3.0);
        // maxTimeParatemerArray.add(3.3);
        //   maxTimeParatemerArray.add(3.5);
       /* maxTimeParatemerArray.add(4.0);
        maxTimeParatemerArray.add(4.5);
        maxTimeParatemerArray.add(5.0);
        maxTimeParatemerArray.add(6.0);
        maxTimeParatemerArray.add(8.0);
        maxTimeParatemerArray.add(10.0);*/

        //  String Path = outputdir + DAGmodel + "/" + DAGmodel + taskNums + "_CFMAX_" + priceModel + ".txt";
      /*  File File = new File(Path);
        PrintWriter PWcfmax = new PrintWriter(File, "utf-8");
        PWcfmax.write("HEFTmakespan:\tHEFTsumcost:\tmaxTime:\tCFMAXtestmakespan:\tCFMAXsumcost:\tMaxTimeParameter:\t\n");
*/
        String dmPath = outputdir + DAGmodel + "/" + DAGmodel + taskNums + "_CSFS_test" + priceModel + ".txt";
        File dmFile = new File(dmPath);
        PrintWriter PWdm = new PrintWriter(dmFile, "utf-8");
        PWdm.write("HEFTmakespan:\tHEFTsumcost:\tmaxTime:\tDMtestmakespan:\tDMsumcost:\tMaxTimeParameter:\t\n");
/*  String EMPath = outputdir + DAGmodel + "/" + DAGmodel + taskNums + "_EM_" + priceModel + ".txt";
        File emFile = new File(EMPath);
        PrintWriter PWem = new PrintWriter(emFile, "utf-8");
        PWem.write("HEFTmakespan:\tHEFTsumcost:\tmaxTime:\tEMetricmakespan:\tEMetricsumcost:\tMaxTimeParameter:\t\n");
*/

    /*    String OtherPath = outputdir + DAGmodel + "/" + DAGmodel + taskNums + "_CSFS_" + priceModel + ".txt";
        File File3th = new File(OtherPath);
        PrintWriter PW3th = new PrintWriter(File3th, "utf-8");
        PW3th.write("HEFTmakespan:\tHEFTsumcost:\tmaxTime:\tOthermakespan:\tOthersumcost:\tMaxTimeParameter:\t\n");
*/
        FreqSeleMax fsm = new FreqSeleMax();

        long starttime, endtime;
        for (int processorNum = 15; processorNum < 20; processorNum += 5) {
            for (Double maxTimeParameter : maxTimeParatemerArray) {
                String computationCostPath = inputDAG + "_" + processorNum + ".txt";
                String processorInfor = inputdir + processordir + processorNum + ".txt";
                // CFMaxTest.runHEFT(maxTimeParameter, processorNum, (taskNums + 2),
                //       beta, computationCostPath, inputGraphPath, processorInfor, PWcfmax, priceModel);
                starttime = System.currentTimeMillis();
                //newtest.runHEFT(maxTimeParameter, processorNum, (taskNums + 2),
                //      beta, computationCostPath, inputGraphPath, processorInfor, PWdm, priceModel, false,starttime);

                fsm.freqSeleMax(maxTimeParameter, processorNum, (taskNums + 2),
                        beta, computationCostPath, inputGraphPath, processorInfor, PWdm, priceModel,starttime);

                endtime = System.currentTimeMillis();
                long runtime = endtime - starttime;
                System.out.println("运行时间是       " + runtime);
                //  newtest.runHEFT(maxTimeParameter, processorNum, (taskNums + 2),
                //  beta, computationCostPath, inputGraphPath, processorInfor, PWem, priceModel, true);
                // otherTest.runHEFT(maxTimeParameter, processorNum, (taskNums + 2),
                //  beta, computationCostPath, inputGraphPath, processorInfor, PW3th, priceModel);

            }
        }


    }
}
