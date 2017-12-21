package taskscheduling;

import java.util.ArrayList;

public class Processor {
	
	public int processorId;       //id
	public int fLevel;            //处理器频率步长      
	public int fMax;              //max frequency
	public int fMin;              //min frequency
	public double costMaxUnit;           //per unit of time cost at max frequency
	public double costMinUnit;           //per unit of time cost at min frequency
	public int costModel;         //cost model 
	public double availableTime = 0; //available time
	public int fre;
	
	public ArrayList<TimeGap> timeGapList = new ArrayList<>();  //the idle time list
	
	
}
