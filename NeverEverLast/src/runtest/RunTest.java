package runtest;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

public class RunTest {
//run on Montage
	public static void main(String[] args) throws IOException{
		// TODO Auto-generated method stub
		
		int taskNums = 10;
		double beta = 0.4;
		String dirPath = "./input/";
		String graphModelName = "/testDAG/";
		String inputGraphPath = dirPath +graphModelName+ "DAGtransfer.txt";
		
		ArrayList<Integer> processorNumsArray = new ArrayList<>();
		processorNumsArray.add(3);

		
		ArrayList<Double> maxTimeParatemerArray = new ArrayList<>();
		maxTimeParatemerArray.add(3.0);

		//学姐论文算法实现代码
		String dir = "./0728output/";
		String maxPath=dir+graphModelName+"_Result_0.txt";
		File max=new File(maxPath);
		PrintWriter PWCFmax=new PrintWriter(max,"utf-8");
		PWCFmax.write("HEFTmakespan:\tHEFTsumcost:\tmaxTime:\tmakespan:\tsumcost:\tMaxTimeParameter:\t\n");


		for(Integer processorNum: processorNumsArray){
			for(Double maxTimeParameter: maxTimeParatemerArray){
				String computationCostPath = dirPath + graphModelName + "DAGruntime.txt";
				String processorInfor = dirPath + graphModelName + "resource.txt";
				System.out.println(processorNum + "\t" + maxTimeParameter + "\t" + "Montage change from max cost difference");
				RunHEFT.runHEFT(maxTimeParameter, processorNum, taskNums, beta, true, computationCostPath, inputGraphPath, processorInfor,PWCFmax);
				System.out.println(processorNum + "\t" + maxTimeParameter + "\t" + "Montage change from min cost difference");
				RunHEFT.runHEFT(maxTimeParameter, processorNum, taskNums, beta, false, computationCostPath, inputGraphPath, processorInfor,PWCFmax);
			}
		}

	}

}
