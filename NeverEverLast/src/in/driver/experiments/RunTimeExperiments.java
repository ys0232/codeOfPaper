package in.driver.experiments;

import initialSchedulingAlgorithm.MAXmin;
import initialSchedulingAlgorithm.MCT;
import initialSchedulingAlgorithm.MET;
import initialSchedulingAlgorithm.MINMIN;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import runtest.CFMaxTest;
import runtest.CFMinTest;


public class RunTimeExperiments {
	public String inputdir = "./input/";
	public String processordir = "/ProcessorFile/";
	public String outputdir = "./0728output/";

	public void MontageTest(int taskNums, int priceModel) throws IOException {
		double beta = 0.04;
		String DAGmodel = "Montage";
		String inputDAG = inputdir + DAGmodel + "/" + DAGmodel + "_" + taskNums;
		String inputGraphPath = inputDAG + "_" + (taskNums + 2) + ".txt";

		ArrayList<Double> maxTimeParatemerArray = new ArrayList<>();
		maxTimeParatemerArray.add(3.0);


		String MinMinCFMAXPath = outputdir + DAGmodel + "/" + DAGmodel + taskNums + "_MINMINCFMAX_" + priceModel + ".txt";
		File FileMinmincfmax = new File(MinMinCFMAXPath);
		PrintWriter PWminmincfmax = new PrintWriter(FileMinmincfmax, "utf-8");
		PWminmincfmax.write("makespan:\tsumcost:\tCFMAXtestmakespan:\tCFMAXsumcost:\tMaxTimeParameter:\tmaxtime:\t\n");

		String MinMinCFMINPath = outputdir + DAGmodel + "/" + DAGmodel + taskNums + "_MINMINCFMIN_" + priceModel + ".txt";
		File FileMinmincfmin  = new File(MinMinCFMINPath);
		PrintWriter PWminmincfmin = new PrintWriter(FileMinmincfmin, "utf-8");
		PWminmincfmin.write("HEFTmakespan:\tHEFTsumcost:\tDMtestmakespan:\tDMsumcost:\tMaxTimeParameter:\tmaxTime:\t\n");
		//=======================================================================================================
		String MaxMinCFMAXPath = outputdir + DAGmodel + "/" + DAGmodel + taskNums + "_MAXMINCFMAX_" + priceModel + ".txt";
		File FileMinmaxcfmax = new File(MaxMinCFMAXPath);
		PrintWriter PWmaxmincfmax= new PrintWriter(FileMinmaxcfmax, "utf-8");
		PWmaxmincfmax.write("HEFTmakespan:\tHEFTsumcost:\tmaxTime:\tEMetricmakespan:\tEMetricsumcost:\tMaxTimeParameter:\t\n");

		String MaxMinCFMINPath = outputdir + DAGmodel + "/" + DAGmodel + taskNums + "_MAXMINCFMIN_" + priceModel + ".txt";
		File FileMaxmincfmin= new File(MaxMinCFMINPath);
		PrintWriter PWmaxmincfmin = new PrintWriter(FileMaxmincfmin, "utf-8");
		PWmaxmincfmin.write("HEFTmakespan:\tHEFTsumcost:\tmaxTime:\tDMtestmakespan:\tDMsumcost:\tMaxTimeParameter:\t\n");
		//===========================================================================================================
		String MetCFMAXPath = outputdir + DAGmodel + "/" + DAGmodel + taskNums + "_METCFMAX_" + priceModel + ".txt";
		File FileMetcfmax= new File(MetCFMAXPath);
		PrintWriter PWmetcfmax = new PrintWriter(FileMetcfmax, "utf-8");
		PWmetcfmax.write("HEFTmakespan:\tHEFTsumcost:\tmaxTime:\tOthermakespan:\tOthersumcost:\tMaxTimeParameter:\t\n");

		String MetCFMINPath = outputdir + DAGmodel + "/" + DAGmodel + taskNums + "_METCFMIN_" + priceModel + ".txt";
		File FileMetcfmin = new File(MetCFMINPath);
		PrintWriter PWmetcfmin = new PrintWriter(FileMetcfmin, "utf-8");
		PWmetcfmin.write("HEFTmakespan:\tHEFTsumcost:\tmaxTime:\tOthermakespan:\tOthersumcost:\tMaxTimeParameter:\t\n");
		//===========================================================================================================
		String MctCFMAXPath  = outputdir + DAGmodel + "/" + DAGmodel + taskNums + "_MCTCFMAX_" + priceModel + ".txt";
		File Filemctcfmax = new File(MctCFMAXPath);
		PrintWriter PWmctcfmax = new PrintWriter(Filemctcfmax, "utf-8");
		PWmctcfmax.write("HEFTmakespan:\tHEFTsumcost:\tmaxTime:\tOthermakespan:\tOthersumcost:\tMaxTimeParameter:\t\n");

		String MctpathPath = outputdir + DAGmodel + "/" + DAGmodel + taskNums + "_MCTCFMIN_" + priceModel + ".txt";
		File Filemctcfmin  = new File(MctpathPath);
		PrintWriter PWmctcfmin = new PrintWriter(Filemctcfmin, "utf-8");
		PWmctcfmin.write("HEFTmakespan:\tHEFTsumcost:\tmaxTime:\tOthermakespan:\tOthersumcost:\tMaxTimeParameter:\t\n");
		//===========================================================================================================
		String HeftcfmaxPath = outputdir + DAGmodel + "/" + DAGmodel + taskNums + "_HEFTCFMAX_" + priceModel + ".txt";
		File Fileheftcfmax = new File(HeftcfmaxPath);
		PrintWriter PWheftcfmax = new PrintWriter(Fileheftcfmax, "utf-8");
		PWheftcfmax.write("HEFTmakespan:\tHEFTsumcost:\tmaxTime:\tOthermakespan:\tOthersumcost:\tMaxTimeParameter:\t\n");

		String heftpathPath = outputdir + DAGmodel + "/" + DAGmodel + taskNums + "_HEFTCFMIN_" + priceModel + ".txt";
		File Fileheftcfmin  = new File(heftpathPath);
		PrintWriter PWheftcfmin = new PrintWriter(Fileheftcfmin, "utf-8");
		PWheftcfmin.write("HEFTmakespan:\tHEFTsumcost:\tmaxTime:\tOthermakespan:\tOthersumcost:\tMaxTimeParameter:\t\n");
		//============================================================================================================
		long starttime, endtime;
		int processorNum = 15;
		for (Double maxTimeParameter : maxTimeParatemerArray) {
			String computationCostPath = inputDAG + "_" + processorNum + ".txt";
			String processorInfor = inputdir + processordir + processorNum + ".txt";
			//MINMIN 
			starttime = System.currentTimeMillis();
			MINMIN.Minmin_CFMax(processorNum, taskNums+2, beta, priceModel, computationCostPath, inputGraphPath, processorInfor,PWminmincfmax, maxTimeParameter, starttime);
		//	MINMIN.Minmin_CFMax(processorNum, taskNums+2, beta, priceModel, computationCostPath, inputGraphPath, processorInfor,PWdm, maxTimeParameter, starttime);
			
			starttime = System.currentTimeMillis();
			MINMIN.Minmin_CFMin(processorNum, taskNums+2, beta, priceModel, computationCostPath, inputGraphPath, processorInfor,PWminmincfmin, maxTimeParameter, starttime);
			//MAXMIN
			starttime = System.currentTimeMillis();
			MAXmin.MaxMin_CFMax(processorNum, taskNums+2, beta, priceModel, computationCostPath, inputGraphPath, processorInfor,PWmaxmincfmax, maxTimeParameter, starttime);
			starttime = System.currentTimeMillis();
			MAXmin.MaxMin_CFMin(processorNum, taskNums+2, beta, priceModel, computationCostPath, inputGraphPath, processorInfor,PWmaxmincfmin, maxTimeParameter, starttime);
			//MET
			starttime = System.currentTimeMillis();
			MET.Met_CFMax(processorNum, taskNums+2, beta, priceModel, computationCostPath, inputGraphPath, processorInfor,PWmetcfmax, maxTimeParameter, starttime);
			starttime = System.currentTimeMillis();
			MET.Met_CFMin(processorNum, taskNums+2, beta, priceModel, computationCostPath, inputGraphPath, processorInfor,PWmetcfmin, maxTimeParameter, starttime);
			//MCT
			starttime = System.currentTimeMillis();
			MCT.Mct_CFMax(processorNum, taskNums+2, beta, priceModel, computationCostPath, inputGraphPath, processorInfor,PWmctcfmax, maxTimeParameter, starttime);
			starttime = System.currentTimeMillis();
			MCT.Mct_CFMax(processorNum, taskNums+2, beta, priceModel, computationCostPath, inputGraphPath, processorInfor,PWmctcfmin, maxTimeParameter, starttime);
			//HEFT
			starttime = System.currentTimeMillis();
			CFMaxTest.runHEFT(maxTimeParameter, processorNum, taskNums + 2,
					beta, computationCostPath, inputGraphPath, processorInfor, PWheftcfmax, priceModel, starttime);
			starttime = System.currentTimeMillis();
			CFMinTest.runHEFT(maxTimeParameter, processorNum, taskNums + 2,
					beta, computationCostPath, inputGraphPath, processorInfor, PWheftcfmin, priceModel, starttime);

		}

	}


	public void tests(String DAGmodel, double beta, int taskNums, int priceModel, String testname) throws IOException {
		//double beta = 0.36;
		//String DAGmodel = "Montage";
		String inputDAG = inputdir + DAGmodel + "/" + DAGmodel + "_" + taskNums;
		String inputGraphPath = inputDAG + "_" + (taskNums + 2) + ".txt";

		ArrayList<Double> maxTimeParatemerArray = new ArrayList<>();
		maxTimeParatemerArray.add(3.0);

		String Path = outputdir + DAGmodel + "/" + DAGmodel + testname + "_" + taskNums + "_ALL_" + priceModel + ".txt";
		File File = new File(Path);
		PrintWriter PWcfmax = new PrintWriter(File, "utf-8");
		//PrintWriter PWdm = new PrintWriter(File, "utf-8");
		//PWcfmax.write("Deadline Ratio\tDeadline\tHEFT\tHEFT\tCFMax\tCFMax\tCRR\tCRR\tCBT-MD\tCBT-MD\tCBT-ED\tCBT-ED\t\n");

		String dmPath = outputdir + DAGmodel + "/" + DAGmodel + testname + "_" + taskNums +
				"_runtime_" + priceModel + ".txt";
		File dmFile = new File(dmPath);
		PrintWriter PWdm = new PrintWriter(dmFile, "utf-8");
		PWdm.write("MINMINCFMax\tMINMINCFMin\tMAXMINCFMax\tMAXMINCFMin\tMETFMax\tMETFMin\tMCTNCFMax\tMCTNCFMin\tHEFTCFMax\tHEFTCFMin\t\n");
		PWdm.flush();
		long starttime, endtime, gaptime;
		for (int processorNum = 5; processorNum < 40; processorNum += 5) {
			for (Double maxTimeParameter : maxTimeParatemerArray) {
				String computationCostPath = inputDAG + "_" + processorNum + ".txt";
				String processorInfor = inputdir + processordir + processorNum + ".txt";
				//MINIMIN
				starttime = System.currentTimeMillis();
				MINMIN.Minmin_CFMax(processorNum, taskNums+2, beta, priceModel, computationCostPath, inputGraphPath, processorInfor,PWcfmax, maxTimeParameter, starttime);
				endtime = System.currentTimeMillis();
				gaptime = endtime - starttime;
				System.out.println("RunTime: "+gaptime);
				PWdm.write(gaptime + "\t");
				PWdm.flush();
				//===================================
				starttime = System.currentTimeMillis();
				MINMIN.Minmin_CFMin(processorNum, taskNums+2, beta, priceModel, computationCostPath, inputGraphPath, processorInfor,PWcfmax, maxTimeParameter, starttime);
				endtime = System.currentTimeMillis();
				gaptime = endtime - starttime;
				System.out.println("RunTime: "+gaptime);
				PWdm.write(gaptime + "\t");
				PWdm.flush();
				//MAXMIN
				starttime = System.currentTimeMillis();
				MAXmin.MaxMin_CFMax(processorNum, taskNums+2, beta, priceModel, computationCostPath, inputGraphPath, processorInfor,PWcfmax, maxTimeParameter, starttime);
				endtime = System.currentTimeMillis();
				gaptime = endtime - starttime;
				PWdm.write(gaptime + "\t");
				System.out.println("RunTime: "+gaptime);
				PWdm.flush();
				//===========================================
				starttime = System.currentTimeMillis();
				MAXmin.MaxMin_CFMin(processorNum, taskNums+2, beta, priceModel, computationCostPath, inputGraphPath, processorInfor,PWcfmax, maxTimeParameter, starttime);
				endtime = System.currentTimeMillis();
				gaptime = endtime - starttime;
				System.out.println("RunTime: "+gaptime);
				PWdm.write(gaptime + "\t");
				PWdm.flush();
				//MET
				starttime = System.currentTimeMillis();
				MET.Met_CFMax(processorNum, taskNums+2, beta, priceModel, computationCostPath, inputGraphPath, processorInfor,PWcfmax, maxTimeParameter, starttime);
				endtime = System.currentTimeMillis();
				gaptime = endtime - starttime;
				System.out.println("RunTime: "+gaptime);
				PWdm.write(gaptime + "\t");
				PWdm.flush();
				//===========================================
				starttime = System.currentTimeMillis();
				MET.Met_CFMin(processorNum, taskNums+2, beta, priceModel, computationCostPath, inputGraphPath, processorInfor,PWcfmax, maxTimeParameter, starttime);
				endtime = System.currentTimeMillis();
				gaptime = endtime - starttime;
				System.out.println("RunTime: "+gaptime);
				PWdm.write(gaptime + "\t");
				PWdm.flush();
				//MCT
				starttime = System.currentTimeMillis();
				MCT.Mct_CFMax(processorNum, taskNums+2, beta, priceModel, computationCostPath, inputGraphPath, processorInfor,PWcfmax, maxTimeParameter, starttime);
				endtime = System.currentTimeMillis();
				gaptime = endtime - starttime;
				System.out.println("RunTime: "+gaptime);
				PWdm.write(gaptime + "\n");
				PWdm.flush();
				//===========================================
				starttime = System.currentTimeMillis();
				MCT.Mct_CFMax(processorNum, taskNums+2, beta, priceModel, computationCostPath, inputGraphPath, processorInfor,PWcfmax, maxTimeParameter, starttime);
				endtime = System.currentTimeMillis();
				gaptime = endtime - starttime;
				System.out.println("RunTime: "+gaptime);
				PWdm.write(gaptime + "\n");
				PWdm.flush();
				//HEFT
				starttime = System.currentTimeMillis();
				CFMaxTest.runHEFT(maxTimeParameter, processorNum, taskNums + 2,
						beta, computationCostPath, inputGraphPath, processorInfor, PWdm, priceModel, starttime);
				endtime = System.currentTimeMillis();
				System.out.println("RunTime: "+gaptime);
				gaptime = endtime - starttime;
				PWdm.write(gaptime + "\t");
				PWdm.flush();
				//===========================================
				starttime = System.currentTimeMillis();
				CFMinTest.runHEFT(maxTimeParameter, processorNum, taskNums + 2,
						beta, computationCostPath, inputGraphPath, processorInfor, PWdm, priceModel, starttime);
				endtime = System.currentTimeMillis();
				System.out.println("RunTime: "+gaptime);
				gaptime = endtime - starttime;
				PWdm.write(gaptime + "\t");
				PWdm.flush();
			}
		}

	}


	public static void main(String[] args) throws IOException {
		String testname = "resource";
		RunTimeExperiments example = new RunTimeExperiments();
		String DAGmodel ;
		DAGmodel = "Montage";
		for (int i = 1; i < 4; i++)
		//	example.tests(DAGmodel, 0.36, 1000, i, testname);
		//int taskNums=1000;
		//int priceModel=1;
		example.MontageTest(1000, 1);
		//example.tests(DAGmodel, 0.36, 1000, 1, testname);
		// DAGmodel="Montage";
		// example.tests(DAGmodel,0.36,1000,1);
		/*        ArrayList<Double> maxTimeParatemerArray = new ArrayList<>();
        maxTimeParatemerArray.add(1.5);
        maxTimeParatemerArray.add(2.0);
        maxTimeParatemerArray.add(3.0);
        maxTimeParatemerArray.add(5.0);
        maxTimeParatemerArray.add(7.0);
        maxTimeParatemerArray.add(10.0);

   double maxTimeParameter = 3;
        int processorNum = 15;
        int taskNums = 1000;
        double beta = 0.4;
        String DAGmodel = "Inspiral";
       // String computationCostPath;
        String inputdir = "./inputDir/";
        //String DAGmodel = "Montage";
        String processordir = "/ProcessorFile/";
        String inputDAG = inputdir + DAGmodel + "/" + DAGmodel + "_" + taskNums;
        String inputGraphPath = inputDAG + "_" + (taskNums + 2) + ".txt";
        String dmPath = DAGmodel + taskNums + "Inspiral_CSFS-MAX_resource"  + ".txt";
        File dmFile = new File(dmPath);
        PrintWriter PWdm = new PrintWriter(dmFile, "utf-8");
        PWdm.write("HEFTmakespan:\tHEFTsumcost:\tmaxTime:\tDMtestmakespan:\tDMsumcost:\tMaxTimeParameter:\t\n");


        for (int priceModel=1;priceModel<=3;priceModel++) {
            for (processorNum=5;processorNum<40;processorNum+=5) {
            //for (Double maxTimeParameter : maxTimeParatemerArray) {
                PWdm.write(priceModel+"\t"+processorNum+"\t");
                String computationCostPath = inputDAG + "_" + processorNum + ".txt";
                String processorInfor = inputdir + processordir + processorNum + ".txt";
                long starttime = System.currentTimeMillis();
                FreqSeleMax.freqSeleMax(maxTimeParameter, processorNum, (taskNums + 2),
                        beta, computationCostPath, inputGraphPath, processorInfor, PWdm, priceModel, starttime);
            }
        }*/
	}

}
