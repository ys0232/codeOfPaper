package experiment;
import runtest.CFMaxTest;
import runtest.FreqSeleMax;
import runtest.newtest;
import runtest.otherTest;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
/**
 * Created by lenovo on 2017/7/21.
 */
public class experiment {
    public String inputdir = "./inputDir/";
    public String processordir = "/ProcessorFile/";
    public String outputdir = "./0728output/";

    public void MontageTest(int taskNums, int priceModel) throws IOException {
        double beta = 0.36;
        String DAGmodel = "Montage";
        String inputDAG = inputdir + DAGmodel + "/" + DAGmodel + "_" + taskNums;
        String inputGraphPath = inputDAG + "_" + (taskNums + 2) + ".txt";

        ArrayList<Double> maxTimeParatemerArray = new ArrayList<>();
        maxTimeParatemerArray.add(3.0);


        String Path = outputdir + DAGmodel + "/" + DAGmodel + taskNums + "_CFMAX_" + priceModel + ".txt";
        File File = new File(Path);
        PrintWriter PWcfmax = new PrintWriter(File, "utf-8");
        PWcfmax.write("HEFTmakespan:\tHEFTsumcost:\tmaxTime:\tCFMAXtestmakespan:\tCFMAXsumcost:\tMaxTimeParameter:\t\n");

        String dmPath = outputdir + DAGmodel + "/" + DAGmodel + taskNums + "_DM_" + priceModel + ".txt";
        File dmFile = new File(dmPath);
        PrintWriter PWdm = new PrintWriter(dmFile, "utf-8");
        PWdm.write("HEFTmakespan:\tHEFTsumcost:\tmaxTime:\tDMtestmakespan:\tDMsumcost:\tMaxTimeParameter:\t\n");

        String EMPath = outputdir + DAGmodel + "/" + DAGmodel + taskNums + "_EM_" + priceModel + ".txt";
        File emFile = new File(EMPath);
        PrintWriter PWem = new PrintWriter(emFile, "utf-8");
        PWem.write("HEFTmakespan:\tHEFTsumcost:\tmaxTime:\tEMetricmakespan:\tEMetricsumcost:\tMaxTimeParameter:\t\n");

        String csfsPath = outputdir + DAGmodel + "/" + DAGmodel + taskNums + "_CSFS_" + priceModel + ".txt";
        File csfsFile = new File(csfsPath);
        PrintWriter PWcsfs = new PrintWriter(csfsFile, "utf-8");
        PWcsfs.write("HEFTmakespan:\tHEFTsumcost:\tmaxTime:\tDMtestmakespan:\tDMsumcost:\tMaxTimeParameter:\t\n");

        String OtherPath = outputdir + DAGmodel + "/" + DAGmodel + taskNums + "_ThirdMetric_" + priceModel + ".txt";
        File File3th = new File(OtherPath);
        PrintWriter PW3th = new PrintWriter(File3th, "utf-8");
        PW3th.write("HEFTmakespan:\tHEFTsumcost:\tmaxTime:\tOthermakespan:\tOthersumcost:\tMaxTimeParameter:\t\n");
        long starttime, endtime;
        int processorNum = 15;
        for (Double maxTimeParameter : maxTimeParatemerArray) {
            String computationCostPath = inputDAG + "_" + processorNum + ".txt";
            String processorInfor = inputdir + processordir + processorNum + ".txt";
            starttime = System.currentTimeMillis();
            CFMaxTest.runHEFT(maxTimeParameter, processorNum, (taskNums + 2),
                    beta, computationCostPath, inputGraphPath, processorInfor, PWcfmax, priceModel, starttime);
            starttime = System.currentTimeMillis();
            newtest.runHEFT(maxTimeParameter, processorNum, (taskNums + 2),
                    beta, computationCostPath, inputGraphPath, processorInfor, PWdm, priceModel, false, starttime);
            starttime = System.currentTimeMillis();
            newtest.runHEFT(maxTimeParameter, processorNum, (taskNums + 2),
                    beta, computationCostPath, inputGraphPath, processorInfor, PWem, priceModel, true, starttime);
            starttime = System.currentTimeMillis();
            FreqSeleMax.freqSeleMax(maxTimeParameter, processorNum, (taskNums + 2),
                    beta, computationCostPath, inputGraphPath, processorInfor, PWcsfs, priceModel, starttime);
            starttime = System.currentTimeMillis();
            otherTest.runHEFT(maxTimeParameter, processorNum, (taskNums + 2),
                    beta, computationCostPath, inputGraphPath, processorInfor, PW3th, priceModel, starttime);

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
        PWcfmax.write("Deadline Ratio\tDeadline\tHEFT\tHEFT\tCFMax\tCFMax\tCRR\tCRR\tCBT-MD\tCBT-MD\tCBT-ED\tCBT-ED\t\n");

        String dmPath = outputdir + DAGmodel + "/" + DAGmodel + testname + "_" + taskNums +
                "_runtime_" + priceModel + ".txt";
        File dmFile = new File(dmPath);
        PrintWriter PWdm = new PrintWriter(dmFile, "utf-8");
        PWdm.write("CFMax\tCRR\tCBT-MD\tCBT-ED\t\n");
        PWdm.flush();

        long starttime, endtime, gaptime;
        for (int processorNum = 5; processorNum < 40; processorNum += 5) {
            for (Double maxTimeParameter : maxTimeParatemerArray) {
                String computationCostPath = inputDAG + "_" + processorNum + ".txt";
                String processorInfor = inputdir + processordir + processorNum + ".txt";
                //CSFS-Max
              /*  starttime = System.currentTimeMillis();
                FreqSeleMax.freqSeleMax(maxTimeParameter, processorNum, (taskNums + 2),
                        beta, computationCostPath, inputGraphPath, processorInfor, PWcfmax, priceModel, starttime);
                endtime = System.currentTimeMillis();
                gaptime = endtime - starttime;
                PWdm.write(gaptime + "\t");
                PWdm.flush();
*/
                //CFMax
                starttime = System.currentTimeMillis();
                CFMaxTest.runHEFT(maxTimeParameter, processorNum, (taskNums + 2),
                        beta, computationCostPath, inputGraphPath, processorInfor, PWcfmax, priceModel, starttime);
                endtime = System.currentTimeMillis();
                gaptime = endtime - starttime;
                PWdm.write(gaptime + "\t");
                PWdm.flush();
                //CRR
                starttime = System.currentTimeMillis();
                otherTest.runHEFT(maxTimeParameter, processorNum, (taskNums + 2),
                        beta, computationCostPath, inputGraphPath, processorInfor, PWcfmax, priceModel, starttime);
                endtime = System.currentTimeMillis();
                gaptime = endtime - starttime;
                PWdm.write(gaptime + "\t");
                PWdm.flush();
                //CBT-MD
                starttime = System.currentTimeMillis();
                newtest.runHEFT(maxTimeParameter, processorNum, (taskNums + 2),
                        beta, computationCostPath, inputGraphPath, processorInfor, PWcfmax, priceModel, false, starttime);
                endtime = System.currentTimeMillis();
                gaptime = endtime - starttime;
                PWdm.write(gaptime + "\t");
                PWdm.flush();
                //CBT-ED
                starttime = System.currentTimeMillis();
                newtest.runHEFT(maxTimeParameter, processorNum, (taskNums + 2),
                        beta, computationCostPath, inputGraphPath, processorInfor, PWcfmax, priceModel, true, starttime);
                endtime = System.currentTimeMillis();
                gaptime = endtime - starttime;
                PWdm.write(gaptime + "\n");
                PWdm.flush();
            }
        }

    }


    public static void main(String[] args) throws IOException {
        String testname = "resource";
        experiment example = new experiment();
        String DAGmodel ;
        DAGmodel = "Montage";
        for (int i = 1; i < 4; i++)
            example.tests(DAGmodel, 0.36, 25, i, testname);
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



