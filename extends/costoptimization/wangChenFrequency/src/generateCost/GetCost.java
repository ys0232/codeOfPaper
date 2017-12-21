package generateCost;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;

public class GetCost {
	public static void getCost(String outputFile, int nodeCount, int processorCount) throws IOException{
		//outputFile:输出文件名;nodeCount:DAG的节点数;processorCount:处理器数量
		BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File(outputFile), false), "utf-8"));
		
		for(int i = 1; i <= nodeCount; i++){
			bufferedWriter.write(i  + "\t");
			
			for(int j = 1; j <= processorCount; j++){

				bufferedWriter.write((int)(Math.random() * 4 + 1) + 5 * j + "");// ?
				if(j != processorCount){
					bufferedWriter.write("\t");
				}else{
					bufferedWriter.newLine();
				}
			}
		}
		bufferedWriter.flush();
		bufferedWriter.close();
	}
}
