package in.driver.experiments;

import initialSchedulingAlgorithm.MAXmin;
import initialSchedulingAlgorithm.MCT;
import initialSchedulingAlgorithm.MET;
import initialSchedulingAlgorithm.MINMIN;
import initialSchedulingAlgorithmJustmakespan.CalcAllMakespans;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import runtest.CFMaxTest;
import runtest.CFMinTest;

public class RunMontageExperiment {
	public static void main(String[] args) throws IOException {
		int taskNums = 1000;
		double beta = 0.4;
		String dirPath = "./input/";
		String graphModelName = "Montage";
		String inputGraphPath = dirPath + graphModelName +"/"+ graphModelName+"_"+ taskNums+"_"+(taskNums+2)+".txt";
		//String processorInfor = dirPath +"ProcessorFile/"+ processorNum + ".txt";
		//String computationCostPath = dirPath + graphModelName+"/"+ graphModelName+"_"+  + taskNums+"_"+processorNum+ ".txt";
		ArrayList<Integer> processorNumsArray = new ArrayList<>();
		processorNumsArray.add(5);
		processorNumsArray.add(10);
		processorNumsArray.add(15);
		processorNumsArray.add(20);
		processorNumsArray.add(25);
		processorNumsArray.add(30);
		processorNumsArray.add(35);	
		ArrayList<Double> maxTimeParatemerArray = new ArrayList<>();
		maxTimeParatemerArray.add(1.5);
		//maxTimeParatemerArray.add(2.5);
		maxTimeParatemerArray.add(5.0);
		ArrayList<Integer> PricelModels= new ArrayList<>();
		PricelModels.add(1);
		//PricelModels.add(2);
		//PricelModels.add(3);

		String Path = "1.txt";
		File File = new File(Path);
		PrintWriter PWdm= new PrintWriter(File, "utf-8");
		//taskNums+=2;
		long starttimecfmax=0;long endtimecfmax=0;long starttimecfmin=0;long endtimecfmin=0;long gaptimeheftcfmax=0;long gaptimeheftcfmin=0;
		for(Integer processorNum: processorNumsArray){
			for(Double maxTimeParameter: maxTimeParatemerArray){
				String processorInfor = dirPath +"ProcessorFile/"+ processorNum + ".txt";
				String computationCostPath = dirPath + graphModelName+"/"+ graphModelName+"_"+  + taskNums+"_"+processorNum+ ".txt";
				//taskNums+=2;
				for(Integer pricelModel:PricelModels) {
					//double makespan=CalcAllMakespans.calcmakespans(processorNum, taskNums, beta, pricelModel, computationCostPath, inputGraphPath, processorInfor, maxTimeParameter, 0);
					//double deadline=makespan*maxTimeParameter;
					System.out.println("N Of R: "+processorNum + "\tD.R: " + maxTimeParameter + "\tP.M: "+ pricelModel +"\t");
					//					System.out.println("MINMIN");
					//					MINMIN.Minmin_CFMax(processorNum, taskNums+2, beta, pricelModel, computationCostPath, inputGraphPath, processorInfor,PWdm, maxTimeParameter, 0);
					//					MINMIN.Minmin_CFMin(processorNum, taskNums+2, beta, pricelModel, computationCostPath, inputGraphPath, processorInfor, PWdm,maxTimeParameter, 0);

					//System.out.println("MAXMIN");
					//MAXmin.MaxMin_CFMax(processorNum, taskNums+2, beta, pricelModel, computationCostPath, inputGraphPath, processorInfor,PWdm, maxTimeParameter, 0);
					//MAXmin.MaxMin_CFMin(processorNum, taskNums+2, beta, pricelModel, computationCostPath, inputGraphPath, processorInfor,PWdm, maxTimeParameter, 0);

					System.out.println("MET");
					MET.Met_CFMax(processorNum, taskNums+2, beta, pricelModel, computationCostPath, inputGraphPath, processorInfor,PWdm, maxTimeParameter, 0);
					MET.Met_CFMin(processorNum, taskNums+2, beta, pricelModel, computationCostPath, inputGraphPath, processorInfor,PWdm, maxTimeParameter, 0);

					//System.out.println("MCT");
					//MCT.Mct_CFMax(processorNum, taskNums+2, beta, pricelModel, computationCostPath, inputGraphPath, processorInfor,PWdm, maxTimeParameter, 0);
					//MCT.Mct_CFMin(processorNum, taskNums+2, beta, pricelModel, computationCostPath, inputGraphPath, processorInfor,PWdm, maxTimeParameter, 0);
					//System.out.println("HEFT");

					//CFMaxTest.runHEFT(maxTimeParameter, processorNum, taskNums+2, beta, computationCostPath, inputGraphPath, processorInfor, PWdm, pricelModel,0);
					//CFMinTest.runHEFT(maxTimeParameter, processorNum, taskNums+2, beta, computationCostPath, inputGraphPath, processorInfor, PWdm, pricelModel,0);
					//System.out.println("*****************Completed***************************");

				}
			}
		}
		System.out.println("*****************Completed***************************");

	}

}
