package taskscheduling.util;

import taskscheduling.Processor;

public class CalcCost {
	
	public static double getCost(int frequency, Processor processor){
		
		double cost = 0;
		
		switch (processor.costModel) {
		case 1:
			cost = calcCostModel1Linear(frequency, processor);
			break;
		case 2:
			cost = calcCostModel2Superlinear(frequency, processor);
			break;
		case 3:
			cost = calcCostModel3Sublinear(frequency, processor);
			break;
		}
		
		return cost;
	}
	
	private static double calcCostModel1Linear(int frequency, Processor processor){
		
		double costDif = 3.33;
		double cost = 0;
		double costMin =9.24;
		int fMin = processor.fMin;
		
		cost = costMin + costDif * ((frequency * 1.0 - fMin) / fMin);
		
		return cost;
	}
	
	private static double calcCostModel2Superlinear(int frequency, Processor processor){
		
		double costDif = 4.44;
		double cost = 0;
		double costMin =9.24;
		int fMin = processor.fMin;
		
		cost = costMin + costDif * ((1 + (frequency * 1.0 - fMin) / fMin) * Math.log(1 + (frequency - fMin) / fMin));
		//System.out.println("ahahahahahah"+frequency*1.0);
		return cost;
	}
	
	private static double calcCostModel3Sublinear(int frequency, Processor processor){
		
		double costDif = 12;
		double cost = 0;
		double costMin = 2.78;
		int fMin = processor.fMin;
		
		cost = costMin + costDif * Math.log(1 + (frequency * 1.0 - fMin) / fMin);
		//System.out.println("ahahahahahah"+frequency*1.0);
		return cost;
	}

}
