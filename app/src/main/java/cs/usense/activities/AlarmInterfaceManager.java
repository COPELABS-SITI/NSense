package cs.usense.activities;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by copelabs on 20/03/2018.
 */

public abstract class AlarmInterfaceManager {
    private static List<AlarmReceiverInterface> interfaces = new ArrayList<>();

    public static void registerListener(AlarmReceiverInterface alarmListener) {
        interfaces.add(alarmListener);
    }

    public static void unRegisterListener(AlarmReceiverInterface alarmListener) {
        interfaces.remove(alarmListener);
    }
    public static void notifyScanResultsAvailable() {
        for(AlarmReceiverInterface listener : interfaces) {
            if(listener instanceof AlarmReceiverInterface) {
                listener.onAlarme();
            }
        }
    }
}
