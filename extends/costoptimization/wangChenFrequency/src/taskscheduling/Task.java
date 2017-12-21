package taskscheduling;

import java.util.ArrayList;

public class Task{
	
	public double maxCost;// the cost when this task is scheduled on maximum frequency
	public int selectedFre;// this task is scheduled on the frequency
	
	public int taskId;               //id
	public double averageCost;       //average computation cost
	public int selectedProcessorId;  // the id of processor  for executing time
	
	public double minCost;              //the min cost in all procesors
	public double minCostExcuteTime;    //the execution time when the cost is minimum
	public double minCostUnit;
	public int minCostProcessorId;   //the id of processot with min cost
	public int minCostProcessorLevel;//������С�����µĵڼ����������ô���������СƵ�ʼ��ϸ�ֵ*�ô������Ĳ�������ȡ����С���۵�Ƶ��       

	public TimeGap timeGap = new TimeGap(); //execution start time,finish time,execution time
	
	public ArrayList<Double> computationCost = new ArrayList<>();        //the execution time list at highest frequency of each frequency
	public ArrayList<Integer> predecessorTaskList = new ArrayList<>();    //predecessorTask
	public ArrayList<Integer> successorTaskList = new ArrayList<>();      //successorTask

}
