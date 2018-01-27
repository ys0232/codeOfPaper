package initialSchedulingAlgorithm;

import runtest.CFMaxTest;
import runtest.CFMinTest;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Created by lenovo on 2017/12/28.
 */
public class NewExperiment {
	public static void main(String[] args) throws IOException {
		int taskNums = 1000;
		double beta = 0.4;
		String dirPath = "./input/";
		String graphModelName = "Montage";
		String inputGraphPath = dirPath + graphModelName +"/"+ graphModelName+"_"+ taskNums+"_"+(taskNums+2)+".txt";
		int processorNum = 5;
		int pricelModel = 1;
		String processorInfor = dirPath +"ProcessorFile/"+ processorNum + ".txt";
		String computationCostPath = dirPath + graphModelName+"/"+ graphModelName+"_"+  + taskNums+"_"+processorNum+ ".txt";
		double maxTimeParameter = 1.5;
		String Path = "1.txt";
		File File = new File(Path);
		PrintWriter PWcfmax = new PrintWriter(File, "utf-8");
		PrintWriter PWdm = new PrintWriter(File, "utf-8");
		taskNums+=2;
		MINMIN.Minmin_CFMax(processorNum, taskNums, beta, pricelModel, computationCostPath, inputGraphPath, processorInfor,PWdm, maxTimeParameter, 0);
		MINMIN.Minmin_CFMin(processorNum, taskNums, beta, pricelModel, computationCostPath, inputGraphPath, processorInfor,PWdm, maxTimeParameter, 0);
		System.out.println("==========================================================");

		MAXmin.MaxMin_CFMax(processorNum, taskNums, beta, pricelModel, computationCostPath, inputGraphPath, processorInfor,PWdm, maxTimeParameter, 0);
		MAXmin.MaxMin_CFMin(processorNum, taskNums, beta, pricelModel, computationCostPath, inputGraphPath, processorInfor,PWdm, maxTimeParameter, 0);
		System.out.println("==========================================================");

		MET.Met_CFMax(processorNum, taskNums, beta, pricelModel, computationCostPath, inputGraphPath, processorInfor,PWdm, maxTimeParameter, 0);
		MET.Met_CFMin(processorNum, taskNums, beta, pricelModel, computationCostPath, inputGraphPath, processorInfor,PWdm, maxTimeParameter, 0);
		System.out.println("==========================================================");

		MCT.Mct_CFMax(processorNum, taskNums, beta, pricelModel, computationCostPath, inputGraphPath, processorInfor,PWdm, maxTimeParameter, 0);
		MCT.Mct_CFMin(processorNum, taskNums, beta, pricelModel, computationCostPath, inputGraphPath, processorInfor,PWdm, maxTimeParameter, 0);
		System.out.println("==========================================================");
		CFMaxTest.runHEFT(maxTimeParameter, processorNum, taskNums, beta, computationCostPath, inputGraphPath, processorInfor, PWcfmax, pricelModel,0);
		CFMinTest.runHEFT(maxTimeParameter, processorNum, taskNums, beta, computationCostPath, inputGraphPath, processorInfor, PWcfmax, pricelModel,0);
		System.out.println("*****************Completed***************************");

		//   CFMaxTest.runHEFT(maxTimeParameter, processorNum, taskNums, beta, computationCostPath,
		//       inputGraphPath, processorInfor, PWcfmax, pricelModel, 0);


	}
}
