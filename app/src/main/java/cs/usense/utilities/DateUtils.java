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

    private static final SimpleDateFormat SDF_DAY_MONTH = new SimpleDateFormat("dd/MM");

    private static final SimpleDateFormat SDF_DATE = new SimpleDateFormat("dd/MM/yyyy");

    private static final SimpleDateFormat SDF_TIME_SECOND = new SimpleDateFormat("dd/MM - HH:mm:ss");

    private static final SimpleDateFormat SDF_TIME = new SimpleDateFormat("dd/MM - HH:mm");

    private static final SimpleDateFormat SDF_TIME_2 = new SimpleDateFormat("dd-MM - HH:mm");

    private static final SimpleDateFormat TIME_SERIES_ANALYSIS = new SimpleDateFormat("ddMMMyy:HH:mm:ss");


    public static String getTodaysDayOfMonth() {
        return SDF_DAY_MONTH.format(Calendar.getInstance().getTime());
    }

    public static String getTodaysDate() {
        return SDF_DATE.format(Calendar.getInstance().getTime());
    }

    public static String getTimeNowAsStringSecond() {
        return SDF_TIME_SECOND.format(Calendar.getInstance().getTime());
    }

    public static String getTimeNowAsString() {
        return SDF_TIME.format(Calendar.getInstance().getTime());
    }

    public static String getTimeNowFileNameFormatted() {
        return SDF_TIME_2.format(Calendar.getInstance().getTime());
    }

    public static String getTimeNowTimeSeriesFormat() {
        return TIME_SERIES_ANALYSIS.format(Calendar.getInstance().getTime());
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
