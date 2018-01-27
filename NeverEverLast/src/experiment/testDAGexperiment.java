package experiment;

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

public class testDAGexperiment {
    public String inputdir = "./input/";

    public String outputdir = "./0728output/";

    public void mytests(String DAGmodel, double beta, int taskNums, int priceModel, String testname) throws IOException {

        String inputDAG = inputdir + "testDAG/";
        String inputGraphPath = inputDAG + "DAGtransfer.txt";

        String Path = outputdir + DAGmodel + "/" + DAGmodel + testname + "_" + taskNums + "_ALL_" + priceModel + ".txt";
        File File = new File(Path);
        PrintWriter PWcfmax = new PrintWriter(File, "utf-8");
        //PrintWriter PWdm = new PrintWriter(File, "utf-8");
        //PWcfmax.write("Deadline Ratio\tDeadline\tHEFT\tHEFT\tCFMax\tCFMax\tCRR\tCRR\tCBT-MD\tCBT-MD\tCBT-ED\tCBT-ED\t\n");

        String dmPath = outputdir + DAGmodel + "/" + DAGmodel + testname + "_" + taskNums + "_runtime_" + priceModel + ".txt";
        File dmFile = new File(dmPath);
        PrintWriter PWdm = new PrintWriter(dmFile, "utf-8");
        //PWdm.write("MINMINCFMax\tMINMINCFMin\tMAXMINCFMax\tMAXMINCFMin\tMETFMax\tMETFMin\tMCTNCFMax\tMCTNCFMin\tHEFTCFMax\tHEFTCFMin\t\n");
        PWdm.flush();

        int processorNum = 3;
        double maxTimeParameter = 3.0;

        String computationCostPath = inputDAG + "DAGruntime.txt";
        String processorInfor = inputDAG + "resource.txt";
        MINMIN.Minmin_CFMax(processorNum, taskNums, beta, priceModel, computationCostPath, inputGraphPath, processorInfor, PWcfmax, maxTimeParameter, 0);
        MINMIN.Minmin_CFMin(processorNum, taskNums, beta, priceModel, computationCostPath, inputGraphPath, processorInfor, PWcfmax, maxTimeParameter, 0);

        MAXmin.MaxMin_CFMax(processorNum, taskNums, beta, priceModel, computationCostPath, inputGraphPath, processorInfor, PWcfmax, maxTimeParameter, 0);
        MAXmin.MaxMin_CFMin(processorNum, taskNums, beta, priceModel, computationCostPath, inputGraphPath, processorInfor, PWcfmax, maxTimeParameter, 0);

        MET.Met_CFMax(processorNum, taskNums, beta, priceModel, computationCostPath, inputGraphPath, processorInfor, PWcfmax, maxTimeParameter, 0);
        MET.Met_CFMin(processorNum, taskNums, beta, priceModel, computationCostPath, inputGraphPath, processorInfor, PWcfmax, maxTimeParameter, 0);

        MCT.Mct_CFMax(processorNum, taskNums, beta, priceModel, computationCostPath, inputGraphPath, processorInfor, PWcfmax, maxTimeParameter, 0);
        MCT.Mct_CFMin(processorNum, taskNums, beta, priceModel, computationCostPath, inputGraphPath, processorInfor, PWcfmax, maxTimeParameter, 0);

        CFMaxTest.runHEFT(maxTimeParameter, processorNum, taskNums,beta, computationCostPath, inputGraphPath, processorInfor, PWcfmax, priceModel, 0);
        CFMinTest.runHEFT(maxTimeParameter, processorNum, taskNums,beta, computationCostPath, inputGraphPath, processorInfor, PWcfmax, priceModel, 0);

    }


    public static void main(String[] args) throws IOException {
        String testname = "resource";
        testDAGexperiment example = new testDAGexperiment();
        String DAGmodel;
        DAGmodel = "testDAG";
        for (int i = 1; i < 4; i++) {
            if (i == 1) {
                System.out.println("Linear");
            } else if (i == 2) {
                System.out.println("Super Linear");
            } else {
                System.out.println("SubLinear");
            }

            //	example.mytests(DAGmodel, 0.4, 10, i, testname);
        }
        example.mytests(DAGmodel, 0.4, 10, 1, testname);

        System.out.println("====Completed============bye");
    }

}
