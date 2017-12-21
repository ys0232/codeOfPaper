package runtest;


import java.io.*;
import java.util.*;

import taskscheduling.Processor;
import taskscheduling.Task;
import taskscheduling.util.CalcCost;
import taskscheduling.util.CalcRunTime;

public class SchedulingInit {

    public ArrayList<Task> taskList = new ArrayList<>();
    public HashMap<String, Double> taskEdgeHashMap = new HashMap<>();

    //initial information of the task
    public ArrayList<Task> initTaskInfor(String computationCost, String inputGraph) throws IOException {

        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(new File(computationCost)), "utf-8"));
        String inputLine = bufferedReader.readLine();

        while (inputLine != null) {

            if (0 == inputLine.length()) {
                inputLine = bufferedReader.readLine();
                continue;
            }

            String[] data = inputLine.split("\t");
            Task task = new Task();
            task.taskId = Integer.parseInt(data[0]);
            for (int i = 1; i < data.length; ++i) {
                task.computationCost.add(Double.parseDouble(data[i]));
            }
            taskList.add(task);
            //System.out.println(task.taskId);
            inputLine = bufferedReader.readLine();
        }

        bufferedReader.close();
        bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(new File(inputGraph)), "utf-8"));
        inputLine = bufferedReader.readLine();

        //System.out.println(taskList.size());
        while (inputLine != null) {

            if (0 == inputLine.length()) {
                inputLine = bufferedReader.readLine();
                continue;
            }

            String[] data = inputLine.split("\t");
            int preNode = Integer.parseInt(data[0]);
            int suNode = Integer.parseInt(data[1]);
            double tranTime = Double.valueOf(data[2]);
            taskList.get(preNode).successorTaskList.add(suNode);
            taskList.get(suNode).predecessorTaskList.add(preNode);
            taskEdgeHashMap.put(preNode + "_" + suNode, tranTime);
            inputLine = bufferedReader.readLine();
        }

        bufferedReader.close();

        return taskList;
    }

    public ArrayList<Task> initTaskInfor1(String computationCost, String inputGraph) throws IOException {

        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(new File(computationCost)), "utf-8"));
        String inputLine = bufferedReader.readLine();

        while (inputLine != null) {

            if (0 == inputLine.length()) {
                inputLine = bufferedReader.readLine();
                continue;
            }

            String[] data = inputLine.split("\t");
            Task task = new Task();
            task.taskId = Integer.parseInt(data[0]);
            for (int i = 1; i < data.length; ++i) {
                task.computationCost.add(Double.parseDouble(data[i]));
            }
            taskList.add(task);
            inputLine = bufferedReader.readLine();
        }

        bufferedReader.close();
        bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(new File(inputGraph)), "utf-8"));
        inputLine = bufferedReader.readLine();

        while (inputLine != null) {

            if (0 == inputLine.length()) {
                inputLine = bufferedReader.readLine();
                continue;
            }

            String[] data = inputLine.split("\t");
            int preNode = Integer.parseInt(data[0]);
            int suNode = Integer.parseInt(data[1]);
            double tranTime = Double.valueOf(data[2]);
            taskList.get(preNode - 1).successorTaskList.add(suNode);
            taskList.get(suNode - 1).predecessorTaskList.add(preNode);
            taskEdgeHashMap.put(preNode + "_" + suNode, tranTime);  //����֮��Ĵ������ݣ�������_�����񣬴���ʱ�䣩
            inputLine = bufferedReader.readLine();
        }

        bufferedReader.close();

        return taskList;
    }

    public HashMap<String, Double> initTaskEdge() throws IOException {
        /*PrintWriter pw=new PrintWriter("./inputDir/test0.txt");
		Iterator iter=taskEdgeHashMap.keySet().iterator();
		while (iter.hasNext()){
			String it=(String)iter.next();
			String[] temp=it.split("_");
			pw.write(temp[0]+"\t"+temp[1]+"\t"+taskEdgeHashMap.get(it)+"\n");
			pw.flush();
		}*/

        return taskEdgeHashMap;
    }


    //��ʼ������������Ϣ
    public Processor[] initProcessorInfor(String processorInfor, int processorNums, int priceModel) throws IOException {

        Processor[] processorArray = new Processor[processorNums];
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(new File(processorInfor)), "utf-8"));
        String line = bufferedReader.readLine();
        int i = 0;

        while (null != line && i < processorNums) {

            String[] data = line.split("\t");
            processorArray[i] = new Processor();

            processorArray[i].processorId = i;
            processorArray[i].availableTime = 0;
            processorArray[i].fMax = Integer.parseInt(data[0]);
            processorArray[i].fMin = Integer.parseInt(data[2]);
            processorArray[i].fLevel = Integer.parseInt(data[1]);
            //processorArray[i].costMinUnit = Double.parseDouble(data[4]);
            processorArray[i].costModel = priceModel;
            processorArray[i].costMaxUnit = CalcCost.getCost(processorArray[i].fMax, processorArray[i]);
            processorArray[i].fre = Integer.parseInt(data[0]);

            line = bufferedReader.readLine();
            ++i;
        }

        bufferedReader.close();

        return processorArray;
    }
}
