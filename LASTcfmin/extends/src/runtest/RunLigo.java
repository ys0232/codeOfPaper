package runtest;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

public class RunLigo {

	public static void main(String[] args) throws IOException{
		// TODO Auto-generated method stub
		
		int taskNums = 77;
		double beta = 0.4;
		String dirPath = "";//D:\\workspaces\\FrequenceHEFT\\
		String graphModelName = "Ligo";
		String inputGraphPath = dirPath + "ligo.txt";
		
		ArrayList<Integer> processorNumsArray = new ArrayList<>();
		processorNumsArray.add(3);
		processorNumsArray.add(5);
		processorNumsArray.add(8);
		
		ArrayList<Double> maxTimeParatemerArray = new ArrayList<>();
		maxTimeParatemerArray.add(1.5);
		maxTimeParatemerArray.add(2.5);
		maxTimeParatemerArray.add(5.0);

		//保存结果文件
		String newtestPath=graphModelName+"_newtestResult.txt";
		String CFMaxPath=graphModelName+"_CFMaxResult.txt";
		File newtestFile=new File(newtestPath);
		File cfmax=new File(CFMaxPath);
		PrintWriter PWnewtest=new PrintWriter(newtestFile,"utf-8");
		PWnewtest.write("HEFTmakespan:\tHEFTsumcost:\tmaxTime:\tNEWTESTmakespan:\tNEWTESTsumcost:\n");


		for(Integer processorNum: processorNumsArray){
			for(Double maxTimeParameter: maxTimeParatemerArray){
				String computationCostPath = dirPath + graphModelName + processorNum + ".txt";
				String processorInfor = dirPath + processorNum + ".txt";
				//System.out.println(processorNum + "\t" + maxTimeParameter + "\t" + "Ligo change from max cost difference");
				//RunHEFT.runHEFT(maxTimeParameter, processorNum, taskNums, beta, true, computationCostPath, inputGraphPath, processorInfor);
				//System.out.println(processorNum + "\t" + maxTimeParameter + "\t" + "Ligo change from min cost difference");
			//	RunHEFT.runHEFT(maxTimeParameter, processorNum, taskNums, beta, false, computationCostPath, inputGraphPath, processorInfor);
		//	newtest.runHEFT(maxTimeParameter, processorNum, taskNums, beta, computationCostPath, inputGraphPath, processorInfor,PWnewtest);
				//CFMaxTest.runHEFT(maxTimeParameter, processorNum, taskNums, beta, true, computationCostPath, inputGraphPath, processorInfor);

			}
		}

	}

}
