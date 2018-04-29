/*
 * COPYRIGHTS COPELABS/ULHT, LGPLv3.0, 2017/9/7.
 * Insert description here ...
 */

package cs.usense.preferences;


import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashMap;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;

public abstract class WifiP2pTxtRecordPreferences {

    /** This variable is used as a key to write and read data on preferences */
    private static final String WIFI_P2P_TXT_RECORD = "Records";

    public static void setRecord(Context context, String key, String value) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(WIFI_P2P_TXT_RECORD, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public static Map<String, String> getRecordsMap(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(WIFI_P2P_TXT_RECORD, MODE_PRIVATE);
        return castsMapToStringString(sharedPreferences.getAll());
    }

    private static Map<String, String> castsMapToStringString(Map<String, ?> currentMap) {
        Map<String, String> newMap = new HashMap<>();
        for (Map.Entry<String, ?> entry : currentMap.entrySet()) {
            if(entry.getValue() instanceof String) {
                newMap.put(entry.getKey(), (String) entry.getValue());
            }
        }
        return newMap;
    }

}
