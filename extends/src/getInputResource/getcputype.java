package getInputResource;


import java.io.*;
import java.util.Arrays;
import java.util.Random;

/**
 * Created by lenovo on 2017/7/21.
 */
public class getcputype {
    public int[][] getCPUtype(File file, int count) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file), "utf-8"));
        int[][] cpu = new int[count][3];
        String str = br.readLine();
        int i = 0;
        while (str != null) {
            String[] strings = str.split("  ");
            int j = 0;
            for (String string : strings) {
                cpu[i][j] = Integer.valueOf(string);
                j++;
            }
            //System.out.println(i + "\t" + Arrays.toString(cpu[i]));
            i++;
            str = br.readLine();
        }
        return cpu;
    }

    public static void main(String[] args) throws IOException {
        int cpuCount = 5;
        String inputdir = "./input/";
        String processordir = "/ProcessorFile/";
        File cpufile = new File(inputdir + "CPUtype.txt");
        getcputype cputype = new getcputype();
        int[][] cpu = cputype.getCPUtype(cpufile, cpuCount);
        Random random = new Random();

        for (int i = 1; i < 8; i++) {
            int count = 5 * i;
            PrintWriter pw = new PrintWriter(inputdir+processordir + count + ".txt");
            for (int j=0;j<count;j++) {
                int rand = random.nextInt(65536) % cpuCount;
                pw.write(cpu[rand][0] + "\t" + cpu[rand][1] + "\t" + cpu[rand][2] + "\n");
                pw.flush();
            }
        }

    }
}
