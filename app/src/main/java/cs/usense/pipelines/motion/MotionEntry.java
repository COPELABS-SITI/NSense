/*
 * COPYRIGHTS COPELABS/ULHT, LGPLv3.0, 2015/5/26.
 * Class is part of the NSense application. It provides support for accelerometer pipeline.
 */

package cs.usense.pipelines.motion;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import cs.usense.utilities.DateUtils;


/**
 * This class is a model of motion pipeline.
 * @author Saeik Firdose (COPELABS/ULHT)
 * @author Miguel Tavares (COPELABS/ULHT)
 * @version 2.0, 2016
 */
public class MotionEntry {

	/** mActionType variable of the MotionEntry object */
	private String mActionType;
	
	/** mActionStartTime variable of the MotionEntry object */
	private long mActionStartTime;
	
	/** mActionDuration variable of the MotionEntry object */
	private long mActionDuration;

	/** mActionEndTime variable of the MotionEntry object */
	private long mActionEndTime;

	/** day variable of the MotionEntry object */
	private String mDate;
	
	/** mHour variable of the MotionEntry object */
	private int mHour;
	
	/** mTimeFrame variable of the MotionEntry object */
	private String mTimeFrame;
	
	/** mActionCounter variable of the MotionEntry object */
	private int mActionCounter;


	/**
	 * This method constructs the Actions Entry class
	 */
	public MotionEntry(String actionType) {
		mActionType = actionType;
		mActionStartTime = System.currentTimeMillis();
		mDate = DateUtils.getTodaysDate();
		mHour = DateUtils.getTimeSlot();
		mTimeFrame = checkTimeFrame();
	}

	/**
	 * This method get the action type
	 * @return mActionType the action type
	 */
	public String getActionType() {
		return mActionType;
	}

	/**
	 * This method to get Action start Time
	 * @return mActionStartTime the action start time
	 */
	public long getActionStartTime() {
		return mActionStartTime;
	}

	/**
	 * This method get the mDate
	 * @return current mDate
	 */
	public String getDate() {
		return mDate;
	}

	/**
	 * This method get the mHour
	 * @return mHour the mHour
	 */
	public int getHour() {
		return mHour;
	}

	/**
	 * This method get the average duration
	 * @return mActionDuration the average duration
	 */
	public double getActionDuration() {
		return mActionDuration;
	}

	/**
	 * This method set the mActionDuration
	 * @param actionDuration the average duration
	 */
	public void setActionDuration(long actionDuration) {
		mActionDuration = actionDuration;
	}

	/**
	 * This method get the action end time
	 * @return mActionEndTime the action end time
	 */
	public long getActionEndTime() {
		return mActionEndTime;
	}

	/**
	 * This method set the action end time
	 * @param actionEndTime the action end time
	 */
	public void setActionEndTime(long actionEndTime) {
		mActionEndTime = actionEndTime;
	}

	/**
	 * This method get the time frame
	 * @return mTimeFrame the time frame
	 */
	public String getTimeFrame() {
		return mTimeFrame;
	}

	/**
	 * This method get the action counter
	 * @return mActionCounter the action counter
	 */
	public int getActionCounter() {
		return mActionCounter;
	}

	/**
	 * This method set the action counter
	 * @param actionCounter the action counter
	 */
	public void setActionCounter(int actionCounter) {
		mActionCounter = actionCounter;
	}

	/**
	 * This method return a string containing the ACTIONTYPE, mActionStartTime, mActionDuration, mActionEndTime, HOUR, ACTIONCOUNTER, DAY, and TIMEFRAME.
	 * @return String with the ACTIONTYPE, mActionStartTime, mActionDuration, mActionEndTime, HOUR, ACTIONCOUNTER, DAY, and TIMEFRAME values
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("ACTIONTYPE: ").append(mActionType).append("\n");
		sb.append("mActionStartTime: ").append(mActionStartTime).append("\n");
		sb.append("mActionDuration: ").append(mActionDuration).append("\n");
		sb.append("mActionEndTime: ").append(mActionEndTime).append("\n");
		sb.append("HOUR: ").append(mHour).append("\n");
		sb.append("ACTIONCOUNTER: ").append(mActionCounter).append("\n");
		sb.append("DAY: ").append(mDate).append("\n");
		sb.append("TIMEFRAME: ").append(mTimeFrame).append("\n");
		return sb.toString();
	}

	/**
	 * This method returns the time frame, WeekDay or WeekEnd.
	 * @return time frame, WeekDay or WeekEnd
     */
	private String checkTimeFrame() {
		SimpleDateFormat dayFormat = new SimpleDateFormat("EEEE", Locale.UK);
		String timeFrame = dayFormat.format(Calendar.getInstance().getTime());
		return timeFrame.equals("Sunday") || timeFrame.equals("Saturday") ? "WeekEnd" : "WeekDay";
	}

}