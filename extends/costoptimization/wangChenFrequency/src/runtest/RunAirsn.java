package runtest;


import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

public class RunAirsn {

    public static void main(String[] args) throws IOException {
        // TODO Auto-generated method stub

        int taskNums = 53;
        double beta = 0.4;
        String dirPath = "";//D:\\workspaces\\FrequenceHEFT\\
        String graphModelName = "Airsn";
        String inputGraphPath = dirPath + "airsn.txt";

        ArrayList<Integer> processorNumsArray = new ArrayList<>();
        processorNumsArray.add(3);
        processorNumsArray.add(5);
        processorNumsArray.add(8);

        ArrayList<Double> maxTimeParatemerArray = new ArrayList<>();
        //maxTimeParatemerArray.add(0.9);
        maxTimeParatemerArray.add(1.1);
        maxTimeParatemerArray.add(1.3);
        maxTimeParatemerArray.add(1.5);
        maxTimeParatemerArray.add(1.6);
        maxTimeParatemerArray.add(1.7);
        maxTimeParatemerArray.add(1.8);
        maxTimeParatemerArray.add(2.0);
        maxTimeParatemerArray.add(2.3);
        maxTimeParatemerArray.add(2.5);
        maxTimeParatemerArray.add(5.0);
        //保存结果文件
        String dir = "./result/";

     /*   //用绝对距离作为标准的算法
		String newtestPath=dir+graphModelName+"_newtestResult01test.txt";
		File newtestFile=new File(newtestPath);
		PrintWriter PWnewtest=new PrintWriter(newtestFile,"utf-8");
		PWnewtest.write("HEFTmakespan:\tHEFTsumcost:\tmaxTime:\tNEWTESTmakespan:\tNEWTESTsumcost:\tMaxTimeParameter:\t\n");
*/
   /*   //用欧式距离作为标准的算法
        String EMPath = dir + graphModelName + "_EMetricResult_0100000temp.txt";
        File emFile = new File(EMPath);
        PrintWriter PWem = new PrintWriter(emFile, "utf-8");
        PWem.write("HEFTmakespan:\tHEFTsumcost:\tmaxTime:\tEMetricmakespan:\tEMetricsumcost:\tMaxTimeParameter:\t\n");

*/

        //分三种情况的算法
        String OtherPath = dir + graphModelName + "_Other_new.txt";
        File emFile = new File(OtherPath);
        PrintWriter PWother = new PrintWriter(emFile, "utf-8");
        PWother.write("HEFTmakespan:\tHEFTsumcost:\tmaxTime:\tOthermakespan:\tOthersumcost:\tMaxTimeParameter:\t\n");

/*

       //论文基本算法运行时，消去注释

		String CFMaxPath=dir+graphModelName+"_CFMaxResult_000000tmp.txt";
		File cfmax=new File(CFMaxPath);
		PrintWriter PWCFmax=new PrintWriter(cfmax,"utf-8");
		PWCFmax.write("HEFTmakespan:\tHEFTsumcost:\tmaxTime:\tCFMaxmakespan:\tCFMaxsumcost:\tMaxTimeParameter:\t\n");
*/

        //学姐论文算法实现代码
/*
        String maxPath=dir+graphModelName+"_Result_0.txt";
        File max=new File(maxPath);
        PrintWriter PWCFmax=new PrintWriter(max,"utf-8");
        PWCFmax.write("HEFTmakespan:\tHEFTsumcost:\tmaxTime:\tmakespan:\tsumcost:\tMaxTimeParameter:\t\n");

*/

        for (Integer processorNum : processorNumsArray) {
            for (Double maxTimeParameter : maxTimeParatemerArray) {
                String computationCostPath = dirPath + graphModelName + processorNum + ".txt";
                String processorInfor = dirPath + processorNum + ".txt";
                //System.out.println("processorNum is: "+processorNum+" \tmaxTimeParameter is: "+maxTimeParameter);
                //System.out.println(" taskNums is: "+ taskNums+" \tcomputationCostPath is: "+computationCostPath);
                //System.out.println("inputGraphPath is: "+inputGraphPath+" \tprocessorInfor is: "+processorInfor);
                //System.out.println(processorNum + "\t" + maxTimeParameter + "\t" + "Airsn change from max cost difference");
              //  RunHEFT.runHEFT(maxTimeParameter, processorNum, taskNums, beta, true, computationCostPath, inputGraphPath, processorInfor,PWCFmax);
                //System.out.println(processorNum + "\t" + maxTimeParameter + "\t" + "Airsn change from min cost difference");
                //RunHEFT.runHEFT(maxTimeParameter, processorNum, taskNums, beta, computationCostPath, inputGraphPath, processorInfor);
               // newtest.runHEFT(maxTimeParameter, processorNum, taskNums, beta, computationCostPath, inputGraphPath, processorInfor,PWnewtest);
            //  newtest.runHEFT(maxTimeParameter, processorNum, taskNums, beta, computationCostPath, inputGraphPath, processorInfor, PWem);
             // otherTest.runHEFT(maxTimeParameter, processorNum, taskNums, beta, computationCostPath, inputGraphPath, processorInfor, PWother);
                //	CFMaxTest.runHEFT(maxTimeParameter, processorNum, taskNums, beta,computationCostPath, inputGraphPath, processorInfor,PWCFmax);

            }
        }

    }

}
