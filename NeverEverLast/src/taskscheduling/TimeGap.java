package taskscheduling;

public class TimeGap implements Comparable<Object>{
	
	public double startTime;
	public double endTime;
	public double gap;
	
	public TimeGap(){
		
	}
	
	public TimeGap(double startTime, double endTime){
		this.startTime = startTime;
		this.endTime = endTime;
		this.gap = endTime - startTime;
	}
		
	@Override
	//Sort by the start of the execution time from large to small
	public int compareTo(Object o) {
		// TODO Auto-generated method stub
		TimeGap otherTimeGap = (TimeGap)o;
		double otherStartTime = otherTimeGap.startTime;
		return ((Double)otherStartTime).compareTo((Double)this.startTime);
	}
	
}
