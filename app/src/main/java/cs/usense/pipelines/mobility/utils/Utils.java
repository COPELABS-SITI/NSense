package cs.usense.pipelines.mobility.utils;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Build;
import android.util.Log;

import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Map;

import cs.usense.activities.AlarmInterfaceManager;
import cs.usense.activities.AlarmReceiver;

/**
 * Created by copelabs on 16/10/2017.
 */

public abstract class Utils {

private final static String TAG = Utils.class.getSimpleName();

     public static float batteryStatus(Context context){
          IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
          Intent batteryStatus = context.registerReceiver(null, ifilter);
          int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
          int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
          return level / (float)scale;
     }


     public static  void setAlarm(Context context, int hour, int minute){

          AlarmManager alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
          Intent intent = new Intent(context, AlarmReceiver.class);
          PendingIntent alarmIntent = PendingIntent.getBroadcast(context, 0, intent, 0);

          Calendar calendar = Calendar.getInstance();
          calendar.setTimeInMillis(System.currentTimeMillis());
          calendar.set(Calendar.HOUR_OF_DAY, hour);
          calendar.set(Calendar.MINUTE, minute);
          calendar.set(calendar.SECOND,0);
          calendar.set(calendar.MILLISECOND,0);

          if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
               alarmManager.setExact(AlarmManager.RTC_WAKEUP,calendar.getTimeInMillis(),alarmIntent);
          }
     }
}
