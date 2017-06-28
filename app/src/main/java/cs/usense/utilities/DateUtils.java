/**
 * @version 2.0
 * COPYRIGHTS COPELABS/ULHT, LGPLv3.0, 16-11-2015
 * Class is part of the NSense application.
 * This class provides a couple of date utilities
 * @author Miguel Tavares (COPELABS/ULHT)
 */

package cs.usense.utilities;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


public abstract class DateUtils {

    private static final SimpleDateFormat sdfDayMonth = new SimpleDateFormat("dd/MM");

    private static final SimpleDateFormat sdfDate = new SimpleDateFormat("dd/MM/yyyy");

    private static final SimpleDateFormat sdfTimeSecond = new SimpleDateFormat("dd/MM - HH:mm:ss");

    private static final SimpleDateFormat sdfTime = new SimpleDateFormat("dd/MM - HH:mm");

    private static final SimpleDateFormat sdfTime2 = new SimpleDateFormat("dd-MM - HH:mm");


    public static String getTodaysDayOfMonth() {
        return sdfDayMonth.format(Calendar.getInstance().getTime());
    }

    public static String getTodaysDate() {
        return sdfDate.format(Calendar.getInstance().getTime());
    }

    public static String getTimeNowAsStringSecond() {
        return sdfTimeSecond.format(Calendar.getInstance().getTime());
    }

    public static String getTimeNowAsString() {
        return sdfTime.format(Calendar.getInstance().getTime());
    }

    public static String getTimeNowFileNameFormatted() {
        return sdfTime2.format(Calendar.getInstance().getTime());
    }


    public static long getTimeNowAsLong() {
        return Calendar.getInstance().getTime().getTime();
    }

    public static boolean isMidNight() {
        Date date = new Date();
        return (date.getHours() == 0 && date.getMinutes() == 0);
    }

    public static boolean isEndOfWeek() {
        Date date = new Date();
        int dayOfWeek = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
        return (date.getHours() == 12 && date.getMinutes() == 0 && dayOfWeek == Calendar.FRIDAY);
    }

    /**
     * Provides the current time slot
     * @return currentTimeSlot The actual time slot
     */
    public static int getTimeSlot(){
        return Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
    }

}
