package getInputResource;


import java.io.*;
import java.util.*;

/**
 * Created by lenovo on 2017/7/21.
 */
public class getCPUruntime {
    public void getDagAtcpuRuntime(int[] freqMax, String filename) throws IOException {

        int BasicFreq = 3000;
        File file = new File(filename + ".txt");

        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file), "utf-8"));
        String string = br.readLine();
        HashMap<String, String> runtime = new HashMap<>();
        while (string != null) {

            String[] strs = string.split("\t");
            double time = Double.valueOf(strs[1]);
            double times = 0;
            for (int i = 0; i < freqMax.length; i++) {
                times = time * BasicFreq / freqMax[i];

                if (i == 0) {
                    runtime.put(strs[0], String.format("%.2f",times));
                } else {
                    runtime.put(strs[0], String.format("%.2f",times)+ "\t" + runtime.get(strs[0]));
                }
            }
            string = br.readLine();
        }
        PrintWriter pw = new PrintWriter(filename + freqMax.length + ".txt");
        Iterator iterator = runtime.keySet().iterator();
        while (iterator.hasNext()) {
            String str = (String) iterator.next();
            pw.write(str + "\t" + runtime.get(str) + "\n");
            pw.flush();
        }

    }

    public static void main(String[] args)throws IOException {
        int cpuNums = 10;//璧勬簮涓暟
        int cpuCount=5;//璧勬簮绉嶇被鏁�
        String inputdir="./inputDir/";
        String DAGmodel="Inspiral";
        int DAGnodes=30;
        int[] freqMax = new int[cpuNums];
        String filename = inputdir+DAGmodel+"/"+DAGmodel+"_"+DAGnodes+"runtime";
        File cpufile = new File(inputdir+"CPUtype.txt");
        getcputype cputype=new getcputype();
        int[][] cpu=cputype.getCPUtype(cpufile,cpuCount);
        Random random = new Random();
        for (int i=0;i<cpuNums;i++){
            int rand=random.nextInt(65536)%cpu.length;
            freqMax[i]=cpu[rand][0];
          //  System.out.println(rand+"\t"+freqMax[i]);
        }
      //  System.out.println(Arrays.toString(freqMax)+cpu.length);
        getCPUruntime runtime = new getCPUruntime();
        runtime.getDagAtcpuRuntime(freqMax, filename);

    }
}
