package runtest;

import java.io.*;

/**
 * Created by lenovo on 2017/7/8.
 */
public class test {
    private void test(boolean flag){
        flag=true;
    }
    public static void main(String[] args)throws IOException{
       // String dirPath = "";
        String graphModelName = "Airsn";
        String inputGraphPath = "airsn.txt";
      //  int processorNum=3;
    //    String processorInfor = dirPath + processorNum + ".txt";
        String computationCostPath = graphModelName +"3.txt";
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(
                new FileInputStream(new File(computationCostPath)), "utf-8"));

        String inputLine = bufferedReader.readLine();
        computationCostPath=graphModelName+"runtime.txt";
        File emFile = new File(computationCostPath);
        PrintWriter PW = new PrintWriter(emFile, "utf-8");
        while (inputLine!=null){
            String[] data = inputLine.split("\t");
            data[0]=String.valueOf(Integer.valueOf(data[0])-1);
            PW.write(data[0]+"\t"+data[1]+"\t"+data[2]+"\t"+data[3]+"\n");
            PW.flush();
            inputLine = bufferedReader.readLine();
        }
        PW.close();
        bufferedReader.close();

        bufferedReader = new BufferedReader(new InputStreamReader(
                new FileInputStream(new File(inputGraphPath)), "utf-8"));
        inputLine = bufferedReader.readLine();
        computationCostPath=graphModelName+"transfer.txt";
        emFile = new File(computationCostPath);
        PW = new PrintWriter(emFile, "utf-8");
        while (inputLine!=null){
            String[] data = inputLine.split("\t");
            data[0]=String.valueOf(Integer.valueOf(data[0])-1);
            data[1]=String.valueOf(Integer.valueOf(data[1])-1);
            PW.write(data[0]+"\t"+data[1]+"\t"+data[2]+"\n");
            PW.flush();
            inputLine = bufferedReader.readLine();
        }

    }
}
