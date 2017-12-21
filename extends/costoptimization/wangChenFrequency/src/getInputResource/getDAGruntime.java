package getInputResource;

import java.io.*;

/**
 * Created by lenovo on 2017/7/21.
 */
public class getDAGruntime {
    public static void main(String[] args) throws IOException {
        int taskNums = 1000;

        String inputdir = "./inputDir/";
        String DAGmodel = "Inspiral";
        String processordir = "/ProcessorFile/";
        String inputDAG = inputdir + DAGmodel + "/" + DAGmodel + "_" + taskNums;
        String inputGraphPath = inputDAG + "_";
        String CPUdir = inputdir + processordir;
        for (int i = 1; i < 8; i++) {
            int count = 5 * i;
            int[] freqMax=new int[count];
            BufferedReader br = new BufferedReader(new InputStreamReader(
                    new FileInputStream(CPUdir + count + ".txt"), "utf-8"));//读取CPU文件
            String str = br.readLine();
            int k=0;
            while (str != null) {
                String[] strs=str.split("\t");
                 freqMax[k]=Integer.valueOf(strs[0]);
                 k++;
                str = br.readLine();
            }
            getCPUruntime runtime = new getCPUruntime();
            runtime.getDagAtcpuRuntime(freqMax, inputGraphPath);
        }

    }
}
