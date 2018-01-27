package runtest;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

public class RunSdss {

	public static void main(String[] args) throws IOException{
		// TODO Auto-generated method stub
		
		int taskNums = 124;
		double beta = 0.4;
		String dirPath = "";//D:\\workspaces\\FrequenceHEFT\\
		String graphModelName = "Sdss";
		String inputGraphPath = dirPath + "sdss.txt";
		
		ArrayList<Integer> processorNumsArray = new ArrayList<>();
		processorNumsArray.add(3);
		processorNumsArray.add(5);
		processorNumsArray.add(8);
		
		ArrayList<Double> maxTimeParatemerArray = new ArrayList<>();
		maxTimeParatemerArray.add(1.5);
		maxTimeParatemerArray.add(2.5);
		maxTimeParatemerArray.add(5.0);

		//学姐论文算法实现代码
		String dir = "./result/";
		String maxPath=dir+graphModelName+"_Result_0.txt";
		File max=new File(maxPath);
		PrintWriter PWCFmax=new PrintWriter(max,"utf-8");
		PWCFmax.write("HEFTmakespan:\tHEFTsumcost:\tmaxTime:\tmakespan:\tsumcost:\tMaxTimeParameter:\t\n");


		for(Integer processorNum: processorNumsArray){
			for(Double maxTimeParameter: maxTimeParatemerArray){
				String computationCostPath = dirPath + graphModelName + processorNum + ".txt";
				String processorInfor = dirPath + processorNum + ".txt";
				System.out.println(processorNum + "\t" + maxTimeParameter + "\t" + "Sdss change from max cost difference");
				RunHEFT.runHEFT(maxTimeParameter, processorNum, taskNums, beta, true, computationCostPath, inputGraphPath, processorInfor,PWCFmax);
				System.out.println(processorNum + "\t" + maxTimeParameter + "\t" + "Sdss change from min cost difference");
				RunHEFT.runHEFT(maxTimeParameter, processorNum, taskNums, beta, false, computationCostPath, inputGraphPath, processorInfor,PWCFmax);
			}
		}

	}

}
