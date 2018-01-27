package initialSchedulingAlgorithmJustmakespan;


import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

public class CalcAllMakespans {
public static double calcmakespans(int processorNums, int taskNums, double beta,
		int priceModel, String computationCostPath, String inputGraphPath,
		String processorInfor, double maxTimeParameter, long starttime)
		throws IOException {
	double []makespans=new double[6];
	
	double Maxmakespan = Double.MIN_VALUE;
	
		makespans[0]=MINMIN.MinminMakespan(processorNums, taskNums, beta, priceModel, computationCostPath, inputGraphPath, processorInfor, maxTimeParameter, starttime);
		//System.out.println(makespans[0]);
		makespans[1]=MAXmin.MaxMinMakespan(processorNums, taskNums, beta, priceModel, computationCostPath, inputGraphPath, processorInfor, maxTimeParameter, starttime);
		//System.out.println(makespans[1]);
		makespans[2]=MET.MetMakespan(processorNums, taskNums, beta, priceModel, computationCostPath, inputGraphPath, processorInfor, maxTimeParameter, starttime);
		//System.out.println(makespans[2]);
		makespans[3]=MCT.MctMakespan(processorNums, taskNums, beta, priceModel, computationCostPath, inputGraphPath, processorInfor, maxTimeParameter, starttime);
		//System.out.println(makespans[3]);
		makespans[4]=HEFT.HEFTMakespan(maxTimeParameter, processorNums, taskNums, beta, computationCostPath, inputGraphPath, processorInfor, priceModel, starttime);
		//System.out.println(makespans[4]);
		for(int i=0;i<makespans.length;i++) {
		if(Maxmakespan<makespans[i]) {
			Maxmakespan=makespans[i];
			
		}
		
	}
		//System.out.println("\nMax Makespan: "+Maxmakespan);
	return Maxmakespan;
}
public static void main(String[] args)throws IOException {
	int taskNums = 53;
	double beta = 0.4;
	String dirPath = "";//D:\\workspaces\\FrequenceHEFT\\
	String graphModelName = "Airsn";
	String inputGraphPath = dirPath +graphModelName+ "transfer.txt";
	int processorNum=3;
	int priceModel=2;
	double maxTimeParameter=1.5;
	String Path="1.txt";
	File File = new File(Path);
	PrintWriter PWcfmax=new PrintWriter(File, "utf-8");
	String processorInfor = dirPath + processorNum + ".txt";
	String computationCostPath = dirPath + graphModelName + "runtime.txt";
	
calcmakespans(processorNum, taskNums, beta, priceModel, computationCostPath, inputGraphPath, processorInfor, maxTimeParameter, 0);
	}

}
