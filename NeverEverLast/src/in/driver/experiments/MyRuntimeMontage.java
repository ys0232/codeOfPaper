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



public class MyRuntimeMontage {
	public String inputdir = "./input/";
	public String processordir = "/ProcessorFile/";
	public String outputdir = "./0728output/";

	public void mytests(String DAGmodel, double beta, int taskNums, int priceModel, String testname) throws IOException {
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
		//PWdm.write("MINMINCFMax\tMINMINCFMin\tMAXMINCFMax\tMAXMINCFMin\tMETFMax\tMETFMin\tMCTNCFMax\tMCTNCFMin\tHEFTCFMax\tHEFTCFMin\t\n");
		PWdm.flush();

		//long starttime, endtime, gaptime;
		long starttimeMincfmax=0;long endtimeMincfmax=0;long starttimeMincfmin=0;long endtimeMincfmin=0;long gaptimeMincfmax=0;long gaptimeMincfmin=0;
		long starttimeMaxcfmax=0;long endtimeMaxcfmax=0;long starttimeMaxcfmin=0;long endtimeMaxcfmin=0;long gaptimeMaxcfmax=0;long gaptimeMaxcfmin=0;
		long starttimeMctcfmax=0;long endtimeMctcfmax=0;long starttimeMctcfmin=0;long endtimeMctcfmin=0;long gaptimeMctcfmax=0;long gaptimeMctcfmin=0;
		long starttimeMetcfmax=0;long endtimeMetcfmax=0;long starttimeMetcfmin=0;long endtimeMetcfmin=0;long gaptimeMetcfmax=0;long gaptimeMetcfmin=0;
		long starttimeHeftcfmax=0;long endtimeHeftcfmax=0;long starttimeHeftcfmin=0;long endtimeHeftcfmin=0;long gaptimeHeftcfmax=0;long gaptimeHeftcfmin=0;
		for (int processorNum = 5; processorNum < 40; processorNum += 5) {

			for (Double maxTimeParameter : maxTimeParatemerArray) {
				System.out.println("ProcessorNumber "+processorNum);


				long []mygapMincfmax=new long[10]; long totalMincfmax=0; long totalMincfmin=0;long []mygapMincfmin=new long[10];
				long []mygapMaxcfmax=new long[10]; long totalMaxcfmax=0; long totalMaxcfmin=0;long []mygapMaxcfmin=new long[10];
				long []mygapMctcfmax=new long[10]; long totalMctcfmax=0; long totalMctcfmin=0;long []mygapMctcfmin=new long[10];				
				long []mygapMetcfmax=new long[10]; long totalMetcfmax=0; long totalMetcfmin=0;long []mygapMetcfmin=new long[10];
				long []mygapHeftcfmax=new long[10]; long totalHeftcfmax=0; long totalHeftcfmin=0;long []mygapHeftcfmin=new long[10];


				for (int i=0;i<9;i++) {
					String computationCostPath = inputDAG + "_" + processorNum + ".txt";
					String processorInfor = inputdir + processordir + processorNum + ".txt";

					//MINIMIN
//
//					//System.out.println("MINIMIN CFMAX");
//					starttimeMincfmax = System.currentTimeMillis();
//					MINMIN.Minmin_CFMax(processorNum, taskNums+2, beta, priceModel, computationCostPath, inputGraphPath, processorInfor,PWcfmax, maxTimeParameter, 0);				
//					endtimeMincfmax = System.currentTimeMillis();
//					mygapMincfmax[i] = endtimeMincfmax - starttimeMincfmax;
//					totalMincfmax+=mygapMincfmax[i];						
//					//System.out.println(i+": "+mygapMincfmax[i]);
//					//=========================================
//					//System.out.println("MINMIN CFMIN");
//					starttimeMincfmin = System.currentTimeMillis();
//					MINMIN.Minmin_CFMin(processorNum, taskNums+2, beta, priceModel, computationCostPath, inputGraphPath, processorInfor,PWcfmax, maxTimeParameter, 0);						
//					endtimeMincfmin = System.currentTimeMillis();
//					mygapMincfmin[i] = endtimeMincfmin - starttimeMincfmin;
//					totalMincfmin+=mygapMincfmin[i];						
//					//System.out.println(i+": "+mygapMincfmin[i]);
//					//********************************************************************
//					//*******************************************************************
//					//********************************************************************
//					//System.out.println("MAXIMIN CFMAX");
//					starttimeMaxcfmax = System.currentTimeMillis();
//					MAXmin.MaxMin_CFMax(processorNum, taskNums+2, beta, priceModel, computationCostPath, inputGraphPath, processorInfor,PWcfmax, maxTimeParameter, 0);				
//					endtimeMaxcfmax = System.currentTimeMillis();
//					mygapMaxcfmax[i] = endtimeMaxcfmax - starttimeMaxcfmax;
//					totalMaxcfmax+=mygapMaxcfmax[i];						
//					//System.out.println(i+": "+mygapMaxcfmax[i]);
//					//=========================================
//					//System.out.println("MAXMIN CFMIN");
//					starttimeMaxcfmin = System.currentTimeMillis();
//					MAXmin.MaxMin_CFMin(processorNum, taskNums+2, beta, priceModel, computationCostPath, inputGraphPath, processorInfor,PWcfmax, maxTimeParameter, 0);						
//					endtimeMaxcfmin = System.currentTimeMillis();
//					mygapMaxcfmin[i] = endtimeMaxcfmin - starttimeMaxcfmin;
//					totalMaxcfmin+=mygapMaxcfmin[i];						
//					//System.out.println(i+": "+mygapMaxcfmin[i]);
//					//********************************************************************
//					//*******************************************************************
//					//********************************************************************
//					//System.out.println("MET CFMAX");
//					starttimeMetcfmax = System.currentTimeMillis();
//					MET.Met_CFMax(processorNum, taskNums+2, beta, priceModel, computationCostPath, inputGraphPath, processorInfor,PWcfmax, maxTimeParameter, 0);				
//					endtimeMetcfmax = System.currentTimeMillis();
//					mygapMetcfmax[i] = endtimeMetcfmax - starttimeMetcfmax;
//					totalMetcfmax+=mygapMetcfmax[i];						
//					//System.out.println(i+": "+mygapMetcfmax[i]);
//					//=========================================
//					//System.out.println("MET CFMIN");
//					starttimeMetcfmin = System.currentTimeMillis();
//					MET.Met_CFMin(processorNum, taskNums+2, beta, priceModel, computationCostPath, inputGraphPath, processorInfor,PWcfmax, maxTimeParameter, 0);						
//					endtimeMetcfmin = System.currentTimeMillis();
//					mygapMetcfmin[i] = endtimeMetcfmin - starttimeMetcfmin;
//					totalMetcfmin+=mygapMetcfmin[i];						
//					//System.out.println(i+": "+mygapMetcfmin[i]);
//					//********************************************************************
//					//*******************************************************************
//					//********************************************************************
//					//System.out.println("MCT CFMAX");
					starttimeMctcfmax = System.currentTimeMillis();
					MCT.Mct_CFMax(processorNum, taskNums+2, beta, priceModel, computationCostPath, inputGraphPath, processorInfor,PWcfmax, maxTimeParameter, 0);				
					endtimeMctcfmax = System.currentTimeMillis();
					mygapMctcfmax[i] = endtimeMctcfmax - starttimeMctcfmax;
					totalMctcfmax+=mygapMctcfmax[i];						
					//System.out.println(i+": "+mygapMctcfmax[i]);
					//=========================================
					//System.out.println("MCT CFMIN");
					starttimeMctcfmin = System.currentTimeMillis();
					MCT.Mct_CFMin(processorNum, taskNums+2, beta, priceModel, computationCostPath, inputGraphPath, processorInfor,PWcfmax, maxTimeParameter, 0)	;					
					endtimeMctcfmin = System.currentTimeMillis();
					mygapMctcfmin[i] = endtimeMctcfmin - starttimeMctcfmin;
					totalMctcfmin+=mygapMctcfmin[i];						
					//System.out.println(i+": "+mygapMctcfmin[i]);
					//********************************************************************
					//*******************************************************************
					//********************HEFT CFMAX");
//					starttimeHeftcfmax = System.currentTimeMillis();
//					CFMaxTest.runHEFT(maxTimeParameter, processorNum, taskNums + 2,
//							beta, computationCostPath, inputGraphPath, processorInfor, PWdm, priceModel, 0);				
//					endtimeHeftcfmax = System.currentTimeMillis();
//					mygapHeftcfmax[i] = endtimeHeftcfmax - starttimeHeftcfmax;
//					totalHeftcfmax+=mygapHeftcfmax[i];						
//					//System.out.println(i+": "+mygapHeftcfmax[i]);
//					//=========================================
//					//System.out.println("HEFT CFMIN");
//					starttimeHeftcfmin = System.currentTimeMillis();
//					CFMinTest.runHEFT(maxTimeParameter, processorNum, taskNums + 2,
//							beta, computationCostPath, inputGraphPath, processorInfor, PWdm, priceModel, 0);					
//					endtimeHeftcfmin = System.currentTimeMillis();
//					mygapHeftcfmin[i] = endtimeHeftcfmin - starttimeHeftcfmin;
//					totalHeftcfmin+=mygapHeftcfmin[i];						
//					//System.out.println(i+": "+mygapHeftcfmin[i]);
					//********************************************************************
					//*******************************************************************
					//********************************************************************

				}
				gaptimeMincfmax=totalMincfmax/10;
				System.out.println("==========MINCFMAX=========: "+gaptimeMincfmax);
				gaptimeMincfmin=totalMincfmin/10;
				System.out.println("==========MINCFMIN:========:"+gaptimeMincfmin);
				//============================================================
				gaptimeMaxcfmax=totalMaxcfmax/10;
				System.out.println("==========MAXCFMAX=========: "+gaptimeMaxcfmax);
				gaptimeMaxcfmin=totalMaxcfmin/10;
				System.out.println("==========MAXCFMIN:========:"+gaptimeMaxcfmin);
				//===========================================================
				gaptimeMetcfmax=totalMetcfmax/10;
				System.out.println("==========METCFMAX=========: "+gaptimeMetcfmax);
				gaptimeMetcfmin=totalMetcfmin/10;
				System.out.println("==========METCFMIN:========:"+gaptimeMetcfmin);
				//============================================================
				gaptimeMctcfmax=totalMctcfmax/10;
				System.out.println("==========MCTCFMAX=========: "+gaptimeMctcfmax);
				gaptimeMctcfmin=totalMctcfmin/10;
				System.out.println("==========MCTCFMIN:========:"+gaptimeMctcfmin);
				//============================================================
				gaptimeHeftcfmax=totalHeftcfmax/10;
				System.out.println("==========HEFTCFMAX=========: "+gaptimeHeftcfmax);
				gaptimeHeftcfmin=totalHeftcfmin/10;
				System.out.println("==========HEFTCFMIN:========:"+gaptimeHeftcfmin);
			}

		}


	}
	public static void main(String[] args) throws IOException {
		String testname = "resource";
		MyRuntimeMontage example = new MyRuntimeMontage();
		String DAGmodel ;
		DAGmodel = "Montage";
		for (int i = 1; i < 4; i++) {
			if(i==1) {
				System.out.println("Linear");
			}else if(i==2) {
				System.out.println("Super Linear");
			}else {
				System.out.println("SubLinear");
			}

			example.mytests(DAGmodel, 0.36, 1000, i, testname);
		}
		System.out.println("====Completed============bye");
	}

}
