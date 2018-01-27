package generatedag;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

public class GetDag {
	
	//102
	public static void getMontage(String outputFile) throws IOException{
		
		int nodeCount ;
		int layerCount = 8;
		int random1 = 23;	//The number of the first layer
		int random2 = 0;	//The number of the second layer

		random2 = random1 + 3;
		nodeCount = 4 * random1 + 10;
		//System.out.println("Montage,节点数目为：" + nodeCount);
		
		boolean[][]result = new boolean[nodeCount][nodeCount];
		int layerEndLine[] = { 0,random1,(random1 + random2),
				( random1 + random2 * 2 ),( random1 + random2 * 2 + 1 ),
				( random1 + random2 * 2 + 2),( random1 * 2 + random2 * 2 + 2),
				( random1 * 2 + random2 * 2 + 3) };

		//	The zero layer
		for(int i = layerEndLine[0] + 1;i <= layerEndLine[1]; i ++){
			result[0][i] = true;
		}
		
		//	The first layer
		for(int i = layerEndLine[0] + 1;i <= layerEndLine[1]; i ++){
			result[i][i + layerEndLine[1] - layerEndLine[0]] = true;
			result[i][i + layerEndLine[1] - layerEndLine[0] + 3] = true;
			result[i][i + layerEndLine[5] - layerEndLine[0]] = true;
		}
		
		//	The second layer
		for(int i = layerEndLine[1] + 1;i <= layerEndLine[2]; i ++){
			result[i][i + layerEndLine[2] - layerEndLine[1]] = true;
		}
		
		//	The third layer
		for(int i = layerEndLine[2] + 1;i <= layerEndLine[3]; i ++){
			result[i][layerEndLine[4]] = true;
		}
		
		//	The fourth layer
		result[layerEndLine[4]][layerEndLine[5]] = true;
		
		//	The fifth layer
		for(int i = layerEndLine[5] + 1;i <= layerEndLine[6]; i ++){
			result[layerEndLine[5]][i] = true;
		}
		
		//	The sixth layer
		for(int i = layerEndLine[5] + 1;i <= layerEndLine[6]; i ++){
			result[i][layerEndLine[7]] = true;
		}
		
		BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File(outputFile), false), "utf-8"));
		for(int i = 0; i < nodeCount; ++ i)
		{
			for(int j = 0; j < nodeCount; ++ j){
				if(result[i][j]){
					bufferedWriter.write((i + 1) + "\t" + (j + 1) + "\t" + ( (int)(Math.random()*9) + 1 ));
					bufferedWriter.newLine();
				}
			}
		}
		bufferedWriter.flush();
		bufferedWriter.close();
	}

	//77
	public static void getLigo(String outputFile) throws IOException{
		
		int nodeCount = 77;
		//System.out.println("Ligo,节点个数为："+nodeCount);
		boolean[][] result = new boolean[nodeCount][nodeCount];
		//	0th
		for(int i = 1;i <= 7;i++){
			result[0][i] = true;
		}
		//	1th
		for(int i = 1;i <= 6;i++){
			result[i][i+7] = true;
		}
		for(int i = 14;i <= 17;i++){
			result[7][i] = true;
		}
		//	2th
		for(int i = 8;i <= 12;i++){
			result[i][i+10] = true;
		}
		result[9][18] = true;
		result[9][59] = true;
		result[9][20] = true;
		
		result[10][18] = true;
		result[10][59] = true;
		result[10][19] = true;
		result[10][41] = true;
		result[10][21] = true;

		result[11][19] = true;
		result[11][20] = true;
		result[11][41] = true;
		result[11][22] = true;

		result[12][21] = true;
		result[12][41] = true;
		result[12][42] = true;
		result[12][24] = true;

		result[13][21] = true;
		result[13][22] = true;
		result[13][24] = true;
		result[13][42] = true;

		result[14][23] = true;
		result[14][25] = true;
		result[14][26] = true;

		for(int i = 15;i <= 17;i++){
			result[i][i+12] = true;
		}
		
		//	3th
		result[18][53] = true;
		result[19][54] = true;
		for(int i = 20;i <= 23;i++){
			result[i][i+10] = true;
		}
		result[23][30] = true;
		result[23][31] = true;
		result[23][32] = true;
		result[24][33] = true;
		
		result[25][34] = true;
		result[26][35] = true;
		for(int i = 27;i <= 29;i++){
			result[i][i+10] = true;
			result[i][i+11] = true;
		}
		result[27][52] = true;
		result[28][69] = true;
		result[28][40] = true;
		result[29][69] = true;
		
		//	4th
		result[30][41] = true;
		result[31][41] = true;
		result[31][42] = true;
		result[32][41] = true;
		result[32][42] = true;
		result[33][42] = true;

		result[34][43] = true;
		result[34][45] = true;
		result[34][58] = true;

		result[35][43] = true;
		result[35][45] = true;
		result[35][58] = true;

		result[36][44] = true;
		result[36][46] = true;
		result[36][47] = true;

		result[37][46] = true;
		result[38][47] = true;
		
		result[39][62] = true;
		result[40][63] = true;
		
		//	5th
		result[41][73] = true;
		result[41][70] = true;
		result[41][55] = true;
		result[41][48] = true;
		result[41][56] = true;

		result[42][76] = true;
		
		result[43][50] = true;
		result[44][50] = true;
		result[44][51] = true;
		result[44][49] = true;
		result[45][51] = true;

		result[46][52] = true;
		result[46][52] = true;

		//	6th
		result[48][65] = true;

		result[49][53] = true;
		result[49][54] = true;
		result[49][57] = true;

		result[50][58] = true;
		result[51][58] = true;

		result[52][76] = true;

		//	7th
		result[53][59] = true;

		result[54][76] = true;

		result[55][64] = true;
		result[56][66] = true;

		result[57][60] = true;
		result[57][62] = true;
		result[57][63] = true;

		result[58][75] = true;
		result[58][61] = true;
		result[58][68] = true;
		result[58][74] = true;

		//	8th
		result[59][76] = true;
		
		for(int i = 64;i <= 67;i++){
			result[60][i] = true;
		}
		
		result[61][71] = true;

		result[62][69] = true;
		result[63][69] = true;

		//	9th
		result[64][70] = true;
		result[65][70] = true;
		result[66][70] = true;

		result[67][71] = true;
		result[67][72] = true;

		result[68][72] = true;

		result[69][76] = true;

		//	10th
		result[70][73] = true;
		result[71][74] = true;
		result[72][74] = true;

		//	11th
		result[73][76] = true;
		
		result[74][75] = true;

		//	12th
		result[75][76] = true;
		
		BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File(outputFile), false), "utf-8"));
		for(int i = 0; i < nodeCount; ++ i)
		{
			for(int j = 0; j < nodeCount; ++ j){
				if(result[i][j]){
					bufferedWriter.write((i + 1) + "\t" + (j + 1) + "\t" + ( (int)(Math.random()*9) + 1 ));
					bufferedWriter.newLine();
				}
			}
		}
		bufferedWriter.flush();
		bufferedWriter.close();
	}
	
	//53
	public static void getAirsn(String outputFile) throws IOException{
		
		int nodeCount;
		int layer1 = 10;

		nodeCount = layer1 * 4 + 13;
		//System.out.println("Airsn,节点数目为：" + nodeCount);
		int layerCount = 13;
		
		boolean[][]result = new boolean[nodeCount][nodeCount];
		int layerEndLine[] = { 0,layer1,layer1 * 2, layer1 * 2 + 3,
				layer1 * 2 + 6, layer1 * 2 + 7, layer1 * 2 + 8,
				layer1 * 2 + 9, layer1 * 3 + 9, layer1 * 3 + 10, layer1 * 3 + 11, layer1 * 4 + 11, layer1 * 4 + 12 };

		//	The zero layer
		for(int i = layerEndLine[0] + 1;i <= layerEndLine[1];i++){
			result[0][i] = true;
		}
		//	The first layer
		for(int i = layerEndLine[0] + 1;i <= layerEndLine[1];i++){
			result[i][i + layerEndLine[1] - layerEndLine[0]] = true;
			result[i][i + layerEndLine[7] - layerEndLine[0]] = true;
		}
		for(int i = layerEndLine[2] + 1;i <= layerEndLine[3];i++){
			result[0][i] = true;
		}
		//	The second layer
		int random1 = 3;
		int random2 = 3;
		for(int i = layerEndLine[1] + 1;i <= layerEndLine[1] + random1;i++){
			result[i][layerEndLine[12]] = true;
		}
		for(int i = layerEndLine[1] + random1 + random2 + 1;i <= layerEndLine[2];i++){
			result[i][layerEndLine[12]] = true;
		}
		for(int i = layerEndLine[1] + random1 + 1;i <= layerEndLine[1] + random1 + random2;i++){
			result[i][i + layerEndLine[2] - layerEndLine[1] - random1] = true;
			result[i][i + layerEndLine[3] - layerEndLine[1] - random1] = true;
		}
		
		//	The third layer
		for(int i = layerEndLine[2] + 1;i <= layerEndLine[3];i++){
			result[i][i + layerEndLine[3] - layerEndLine[2]] = true;
		}
		
		//	The fourth layer
		for(int i = layerEndLine[3] + 1;i <= layerEndLine[4];i++){
			result[i][layerEndLine[5]] = true;
		}
		
		//	The fifth layer
		result[layerEndLine[5]][layerEndLine[6]] = true;
		
		//	The sixth layer
		result[layerEndLine[6]][layerEndLine[7]] = true;
		
		//	The seventh layer
		for(int i = layerEndLine[7] + 1;i <= layerEndLine[8];i++){
			result[layerEndLine[7]][i] = true;
		}
		//	The eighth layer
		for(int i = layerEndLine[7] + 1;i <= layerEndLine[8];i++){
			result[i][layerEndLine[9]] = true;
			result[i][layerEndLine[10] - layerEndLine[7]] = true;
		}
		
		//	The ninth
		result[layerEndLine[9]][layerEndLine[10]] = true;
		
		//	The 10th
		for(int i = layerEndLine[10] + 1;i <= layerEndLine[11];i++){
			result[layerEndLine[10]][i] = true;
		}
		
		//	The 11th
		for(int i = layerEndLine[10] + 1;i <= layerEndLine[11];i++){
			result[i][layerEndLine[12]] = true;
		}
		
		BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File(outputFile), false), "utf-8"));
		for(int i = 0; i < nodeCount; ++ i)
		{
			for(int j = 0; j < nodeCount; ++ j){
				if(result[i][j]){
					if(i < j){
						bufferedWriter.write((i + 1) + "\t" + (j + 1) + "\t" + ( (int)(Math.random()*9) + 1 ));
					}else{
						bufferedWriter.write((j + 1) + "\t" + (i + 1) + "\t" + ( (int)(Math.random()*9) + 1 ));
					}
					
					bufferedWriter.newLine();
				}
			}
		}
		bufferedWriter.flush();
		bufferedWriter.close();
	}
	
	//124
	public static void getSdss(String outputFile) throws IOException{
		
		int nodeCount = 124;
		//System.out.println("Sdss,节点个数为："+nodeCount);
		boolean[][]result = new boolean[nodeCount][nodeCount];

		//	The zero layer
		for(int i = 1;i <= 20;i++){
			result[0][i] = true;
		}
		
		//	The first layer
		for(int i = 1;i <= 20;i++){
			result[i][i+20] = true;
		}

		//	The second layer
		for(int i = 21;i <= 23;i++){
			result[i][41] = true;
			result[i][42] = true;
		}
		
		for(int i = 24;i <= 27;i++){
			result[i][43] = true;
			result[i][44] = true;
		}
		
		result[24][41] = true;
		result[24][42] = true;
		result[25][41] = true;
		result[25][42] = true;

		for(int i = 28;i <= 33;i++){
			result[i][45] = true;
			result[i][46] = true;
		}
		
		result[28][43] = true;
		result[28][44] = true;
		result[29][43] = true;
		result[29][44] = true;
		
		result[32][47] = true;
		result[32][48] = true;
		result[33][47] = true;
		result[33][48] = true;

		for(int i = 34;i <= 37;i++){
			result[i][47] = true;
			result[i][48] = true;
		}
		
		result[36][49] = true;
		result[36][50] = true;
		result[37][49] = true;
		result[37][50] = true;

		for(int i = 38;i <= 40;i++){
			result[i][49] = true;
			result[i][50] = true;
		}
		
		//	The third layer
		for(int i = 51;i <= 74;i++){
			result[41][i] = true;
		}

		for(int i = 51;i <= 74;i++){
			result[41][i] = true;
			result[42][i] = true;
		}
		
		for(int i = 63;i <= 86;i++){
			result[43][i] = true;
			result[44][i] = true;
		}
		
		for(int i = 75;i <= 98;i++){
			result[45][i] = true;
			result[46][i] = true;
		}
		
		for(int i = 87;i <= 110;i++){
			result[47][i] = true;
			result[48][i] = true;
		}
		
		
		for(int i = 99;i <= 122;i++){
			result[49][i] = true;
			result[50][i] = true;
		}
		
		//	The fourth layer
		for(int i = 51;i <= 122;i++){
			result[i][123] = true;
		}
		
		BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File(outputFile), false), "utf-8"));
		for(int i = 0; i < nodeCount; ++ i)
		{
			for(int j = 0; j < nodeCount; ++ j){
				if(result[i][j]){
					bufferedWriter.write((i + 1) + "\t" + (j + 1) + "\t" + ( (int)(Math.random()*9) + 1 ));
					bufferedWriter.newLine();
				}
			}
		}
		bufferedWriter.flush();
		bufferedWriter.close();
	}
	
}
