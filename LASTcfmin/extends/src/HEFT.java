import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import taskscheduling.Processor;
import taskscheduling.Task;
import taskscheduling.util.CalcArrayListSum;
import taskscheduling.util.CalcTaskMaxCost;
import taskscheduling.util.CalcTaskRankValue;
import taskscheduling.util.ChangeFromMaxDisCost;
import taskscheduling.util.ChangeFromMinDisCost;
import taskscheduling.util.GetDisCostTaskOrder;
import taskscheduling.util.GetMinCostProcessorIdLevel;
import taskscheduling.util.TaskScheduling;


public class HEFT {

	public static void main(String[] args) throws IOException{
		
		int maxTime = 5;         //Maximum Time
		int processorNums = 3;    //Number of resources
		int taskNums = 10;        //Number of Tasks
		double beta = 0.2;        //Betta in the equation
		
		//computationCost.txt: taskId	p1runtime	p2runtime	p3runtime
		String computationCostPath = "computationCost.txt";//"D:\\workspace\\FrequenceHEFT\\computationCost.txt"
		
		//inputGraph.txt: taskId1		taskId2		transportTime
		String inputGraphPath = "inputGraph.txt";//"D:\\workspace\\FrequenceHEFT\\inputGraph.txt";
		
		//processorInfor.txt: processorId	fmax	fmin	flevel/Differance	cmin	costMode/Pricing Model
		String processorInfor = "processorInfor.txt";//"D:\\workspace\\FrequenceHEFT\\processorInfor.txt"
		
		
		//Initial schedule/initialize the schedule
		SchedulingInit sInit = new SchedulingInit();
		//��ʼ��������Ϣ
		ArrayList<Task> taskList = sInit.initTaskInfor(computationCostPath, inputGraphPath);
		//��ʼ������ʱ��
		HashMap<String, Double> taskEdgeHashMap = sInit.initTaskEdge();
		//��ʼ������������Ϣ
		Processor[] processorsArray = new Processor[processorNums];
		processorsArray = sInit.initProcessorInfor(processorInfor, processorNums);
		
		//�������������������ϵ����Ƶ��ִ��ʱ�䣬���Լ��������rankֵ
		for(int i = 0; i < taskNums; ++ i){
			Task tempTask = taskList.get(i);
			taskList.get(i).averageCost = CalcArrayListSum.calcArrayListSum(tempTask.computationCost) / processorNums;;
		}
		
		//����rankֵ����
		ArrayList<Integer> taskOrderList = CalcTaskRankValue.calcTaskRankValue(taskList, taskEdgeHashMap, taskNums);
		
		//�����Ѿ���������񣬰�˳����е���
		for(Integer taskId: taskOrderList){
			TaskScheduling.taskScheduling(taskId, taskList, taskEdgeHashMap, processorsArray);
		}
		
		CalcTaskMaxCost.calcTaskMaxCost(beta, taskList, processorsArray);
		
		//���HEFT����ĵ������
		System.out.println("��ʼת��֮ǰ��");
//		for(Task task: taskList){
//			System.out.println(task.taskId + "\t" + (task.selectedProcessorId + 1) + "\t" + task.timeGap.startTime + "\t" + task.timeGap.endTime);
//		}
		for(Integer taskId: taskOrderList){
			System.out.println(taskList.get(taskId - 1).taskId + "\t" + (taskList.get(taskId - 1).selectedProcessorId + 1) + "\t" + taskList.get(taskId - 1).timeGap.startTime + "\t" + taskList.get(taskId - 1).timeGap.endTime);
		}
		

		
		//��ȡÿ�������ȡ��С���۵Ĵ������������Ϣ
		GetMinCostProcessorIdLevel.getMinCostProcessorIdLevel(beta, taskList, processorsArray);
		System.out.println("maxcost\tmincost");
		for(Task task: taskList){
			System.out.println(task.maxCost + "\t" + task.minCost);
		}
		System.out.println();
		//��ȡ���ݴ��۲�Ӵ�С����������б�
		List<Map.Entry<Integer, Double>> taskDisCostList = GetDisCostTaskOrder.getDisCostTaskOrder(taskList);
		for(Map.Entry<Integer, Double> entry: taskDisCostList){
			System.out.println(entry.getKey() + "\t" + entry.getValue());
		}
		//���ݴ��۲�Ӵ�С��������
	//	ChangeFromMaxDisCost.changeFromMaxDisCost(maxTime, taskDisCostList, taskList, taskOrderList, taskEdgeHashMap, processorsArray);
		//���ݴ��۲��С�����������
		//ChangeFromMinDisCost.changeFromMinDisCost(maxTime, taskDisCostList, taskList, taskOrderList, taskEdgeHashMap, processorsArray);
		
		System.out.println("ת��֮��");
		for(Integer taskId: taskOrderList){
			System.out.println(taskList.get(taskId - 1).taskId + "\t" + (taskList.get(taskId - 1).selectedProcessorId + 1) + "\t" + taskList.get(taskId - 1).timeGap.startTime + "\t" + taskList.get(taskId - 1).timeGap.endTime);
		}
	}
}
