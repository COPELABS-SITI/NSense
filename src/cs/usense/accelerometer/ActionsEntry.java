/**
 * @version 1.2
 * COPYRIGHTS COPELABS/ULHT, LGPLv3.0, date (e.g. 22-04-2016)
 * Class is part of the USense application. It provides support for accelerometer pipeline.
 * @author Saeik Firdose (COPELABS/ULHT)
 */

package cs.usense.accelerometer;
/**
 * This class provides various methods to provide the action details to the UsenseService
 */
public class ActionsEntry {

	/** Id variable of the ActionsEntry object */
	private int ID;
	
	/** ActionType variable of the ActionsEntry object */
	private String ActionType;
	
	/** ActionStartTime variable of the ActionsEntry object */
	private long ActionStartTime;
	
	/** AverageDuration variable of the ActionsEntry object */
	private double AverageDuration;
	
	/** ActionEndTime variable of the ActionsEntry object */
	private long ActionEndTime;
	
	/** Day variable of the ActionsEntry object */
	private long Day;
	
	/** Hour variable of the ActionsEntry object */
	private int Hour;
	
	/** TimeFrame variable of the ActionsEntry object */
	private String TimeFrame;
	
	/** ActionCounter variable of the ActionsEntry object */
	private int ActionCounter;


	/**
	 * This method constructs the Actions Entry
	 */
	public ActionsEntry() {
		super();
	}


	/**
	 * This method return a string containing the ACTIONTYPE, ActionStartTime, AverageDuration, ActionEndTime, HOUR, ACTIONCOUNTER, DAY, and TIMEFRAME.
	 * @return string String with the ACTIONTYPE, ActionStartTime, AverageDuration, ActionEndTime, HOUR, ACTIONCOUNTER, DAY, and TIMEFRAME values
	 */
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("ACTIONTYPE: " + this.ActionType + "\n");
		sb.append("ActionStartTime: " + this.ActionStartTime + "\n");
		sb.append("AverageDuration: " + this.AverageDuration + "\n");
		sb.append("ActionEndTime: " + this.ActionEndTime+ "\n");
		sb.append("HOUR: " + this.Hour + "\n");
		sb.append("ACTIONCOUNTER: " + this.ActionCounter + "\n");
		sb.append("DAY: " + this.Day + "\n");
		sb.append("TIMEFRAME: " + this.TimeFrame + "\n");

		String string =sb.toString();
		return string;
	}


	/**
	 * This method get the action type
	 * @return ActionType the action type
	 */
	public String getActionType() {
		return ActionType;
	}


	/**
	 * This method set the actionType
	 * @param actionType the action type
	 */
	public void setActionType(String actionType) {
		ActionType = actionType;
	}


	/**
	 * This method to get Action Start Time
	 * @return ActionStartTime the action start time
	 */
	public long getActionStartTime() {
		return ActionStartTime;
	}

	/**
	 * This method get the day
	 * @return Day the current day
	 */
	public long getDay() {
		return Day;
	}


	/**
	 * This method set the day
	 * @param day the current day
	 */
	public void setDay(long day) {
		Day = day;
	}


	/**
	 * This method get the hour
	 * @return hour the hour
	 */
	public int getHour() {
		return Hour;
	}


	/**
	 * This method set hour
	 * @param hour the hour
	 */
	public void setHour(int hour) {
		Hour = hour;
	}


	/**
	 * This method set the action start time
	 * @param actionStartTime the action start time
	 */
	public void setActionStartTime(long actionStartTime) {
		ActionStartTime = actionStartTime;
	}


	/**
	 * This method get the average duration
	 * @return AverageDuration the average duration
	 */
	public double getAverageDuration() {
		return AverageDuration;
	}


	/**
	 * This method set the averageDuration
	 * @param averageDuration the average duration
	 */
	public void setAverageDuration(double averageDuration) {
		AverageDuration = averageDuration;
	}


	/**
	 * This method get the action end time
	 * @return ActionEndTime the action end time
	 */
	public long getActionEndTime() {
		return ActionEndTime;
	}


	/**
	 * This method set the action end time
	 * @param actionEndTime the action end time
	 */
	public void setActionEndTime(long actionEndTime) {
		ActionEndTime = actionEndTime;
	}


	
	/**
	 * This method get the time frame
	 * @return TimeFrame the time frame
	 */
	public String getTimeFrame() {
		return TimeFrame;
	}


	
	/**
	 * This method set the time frame
	 * @param timeFrame the time frame
	 */
	public void setTimeFrame(String timeFrame) {
		TimeFrame = timeFrame;
	}


	/**
	 * This method get the action counter
	 * @return ActionCounter the action counter
	 */
	public int getActionCounter() {
		return ActionCounter;
	}


	/**
	 * This method set the action counter
	 * @param actionCounter the action counter
	 */
	public void setActionCounter(int actionCounter) {
		ActionCounter = actionCounter;
	}


	/**
	 * This method get the id
	 * @return the iD ithe id
	 */
	public int getID() {
		return ID;
	}


	/**
	 * This method set the iD
	 * @param iD the iD
	 */
	public void setID(int iD) {
		ID = iD;
	}
}
