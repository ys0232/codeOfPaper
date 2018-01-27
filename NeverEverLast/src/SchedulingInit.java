

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;

import taskscheduling.Processor;
import taskscheduling.Task;
import taskscheduling.util.CalcCost;

public class SchedulingInit {
	
	public ArrayList<Task> taskList = new ArrayList<>();
	public HashMap<String, Double> taskEdgeHashMap = new HashMap<>();
	
	//初始化任务的信息
	public ArrayList<Task> initTaskInfor(String computationCost, String inputGraph) throws IOException{
		
		BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(new File(computationCost)), "utf-8"));
		String inputLine = bufferedReader.readLine();
		
		while(inputLine != null){
			
			if(0 == inputLine.length()){
				inputLine = bufferedReader.readLine();
				continue;
			}
			
			String[] data = inputLine.split("\t");
			Task task = new Task();
			task.taskId = Integer.parseInt(data[0]);
			for(int i = 1; i < data.length; ++ i){
				task.computationCost.add(Double.parseDouble(data[i]));//每个任务在每个cpu下最高频率下的执行时间
			}
			taskList.add(task);
			inputLine = bufferedReader.readLine();
		}
		
		bufferedReader.close();
		bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(new File(inputGraph)), "utf-8"));
		inputLine = bufferedReader.readLine();
		
		while(inputLine != null){
			
			if(0 == inputLine.length()){
				inputLine = bufferedReader.readLine();
				continue;
			}
			
			String[] data = inputLine.split("\t");
			int preNode = Integer.parseInt(data[0]);
			int suNode = Integer.parseInt(data[1]);
			double tranTime = Double.valueOf(data[2]);
			taskList.get(preNode - 1).successorTaskList.add(suNode);
			taskList.get(suNode - 1).predecessorTaskList.add(preNode);
			taskEdgeHashMap.put(preNode + "_" + suNode, tranTime);  //任务之间的传输数据（父任务_子任务，传输时间）
			inputLine = bufferedReader.readLine();
		}
		
		bufferedReader.close();
		
		return taskList;
	}

	public ArrayList<Task> initTaskInforNewTest(String computationCost, String inputGraph,double[][] runTimeMax) throws IOException{

		BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(new File(computationCost)), "utf-8"));
		String inputLine = bufferedReader.readLine();

		int k=0;
		while(inputLine != null){

			if(0 == inputLine.length()){
				inputLine = bufferedReader.readLine();
				continue;
			}
			String[] data = inputLine.split("\t");
			Task task = new Task();
			task.taskId = Integer.parseInt(data[0]);
			for(int i = 1; i < data.length; ++ i){
				task.computationCost.add(Double.parseDouble(data[i]));
			}

			taskList.add(task);
			inputLine = bufferedReader.readLine();
		}

		bufferedReader.close();
		bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(new File(inputGraph)), "utf-8"));
		inputLine = bufferedReader.readLine();

		while(inputLine != null){

			if(0 == inputLine.length()){
				inputLine = bufferedReader.readLine();
				continue;
			}

			String[] data = inputLine.split("\t");
			int preNode = Integer.parseInt(data[0]);
			int suNode = Integer.parseInt(data[1]);
			double tranTime = Double.valueOf(data[2]);
			taskList.get(preNode - 1).successorTaskList.add(suNode);
			taskList.get(suNode - 1).predecessorTaskList.add(preNode);
			taskEdgeHashMap.put(preNode + "_" + suNode, tranTime);  //任务之间的传输数据（父任务_子任务，传输时间）
			inputLine = bufferedReader.readLine();
		}

		bufferedReader.close();

		return taskList;
	}
	
	public HashMap<String, Double> initTaskEdge(){
		return taskEdgeHashMap;
	}
	
	
	//初始化处理器的信息
	public Processor[] initProcessorInfor(String processorInfor, int processorNums) throws IOException{
		
		Processor[] processorArray = new Processor[processorNums];
		BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(new File(processorInfor)), "utf-8"));
		String line = bufferedReader.readLine();
		int i = 0;
		
		while(null != line && i < processorNums){
			
			String[] data = line.split("\t");
			processorArray[i] = new Processor();
			
			processorArray[i].processorId = i;
			processorArray[i].availableTime = 0;
			processorArray[i].fMax = Integer.parseInt(data[1]);
			processorArray[i].fMin = Integer.parseInt(data[2]);
			processorArray[i].fLevel = Integer.parseInt(data[3]);
			processorArray[i].costMinUnit = Double.parseDouble(data[4]);
			processorArray[i].costModel = Integer.parseInt(data[5]);
			processorArray[i].costMaxUnit = CalcCost.getCost(processorArray[i].fMax, processorArray[i]);
			processorArray[i].fre=Integer.parseInt(data[1]);
			
			line = bufferedReader.readLine();
			++ i;
		}
		
		bufferedReader.close();
		
		return processorArray;
	}
}
