package cs.usense.pipelines.mobility.tasks;

import android.net.wifi.p2p.WifiP2pDevice;
import android.util.Log;

import java.util.HashMap;
import java.util.Map;

import cs.usense.pipelines.mobility.functions.Functions;
import cs.usense.wifi.p2p.TextRecordKeys;
import cs.usense.wifi.p2p.WifiP2pListener;
import cs.usense.wifi.p2p.WifiP2pListenerManager;

/**
 * Created by copelabs on 11/12/2017.
 */

public class TxtRecord implements WifiP2pListener.TxtRecordAvailable {
    private static  final String TAG = TxtRecord.class.getSimpleName();

    private int mAPShared;

    private Map<String,String> mAPmap = new HashMap<>();

    private Map<String,Double> mAPRankFunction3 = new HashMap<>();

    private Map<String,Double> mAPRankFunction4 = new HashMap<>();

    private String mBSSIDConnected;

    private double mSumRank3 = 0;

    private double mSumRank4 = 0;

    public TxtRecord() {
        WifiP2pListenerManager.registerListener(this);
    }

    public int getmBestAPShared(){
        return mAPShared;
    }

    public double getSumRank3(){
        return mSumRank3;
    }

    public double getSumRank4(){
        return mSumRank4;
    }

    public Map getMapSumRank4(){
        return mAPRankFunction4;
    }

    public void setmBSSIDConnected(String BSSID){
        mBSSIDConnected=BSSID;
    }

    public void deleteRecommendations(){
        mAPRankFunction3.clear();
        mAPRankFunction4.clear();
        mAPmap.clear();
    }
    @Override
    public void onTxtRecordAvailable(String fullDomainName, Map<String, String> txtRecordMap, WifiP2pDevice srcDevice) {
        Log.d(TAG, "TxtRecord Received: "
                + txtRecordMap.get(TextRecordKeys.AP) + " "
                + txtRecordMap.get(TextRecordKeys.BT_MAC_KEY) + " "
                + txtRecordMap.get(TextRecordKeys.RANK_FUNCTION_3) + " "
                + txtRecordMap.get(TextRecordKeys.RANK_FUNCTION_4));

        if(txtRecordMap.get(TextRecordKeys.AP)!=null) {
            mAPmap.put(txtRecordMap.get(TextRecordKeys.BT_MAC_KEY), txtRecordMap.get(TextRecordKeys.AP));

            mAPShared = Functions.countOccurences(mAPmap, mBSSIDConnected);

            if(txtRecordMap.get(TextRecordKeys.AP).equals(mBSSIDConnected)) {

                if (txtRecordMap.get(TextRecordKeys.RANK_FUNCTION_3) != null) {
                    mAPRankFunction3.put(txtRecordMap.get(TextRecordKeys.BT_MAC_KEY), Double.parseDouble(txtRecordMap.get(TextRecordKeys.RANK_FUNCTION_3)));

                    mSumRank3 = Functions.sumRank3(mAPRankFunction3);
                }
                if (txtRecordMap.get(TextRecordKeys.RANK_FUNCTION_4) != null) {
                    mAPRankFunction4.put(txtRecordMap.get(TextRecordKeys.BT_MAC_KEY), Double.parseDouble(txtRecordMap.get(TextRecordKeys.RANK_FUNCTION_4)));

                    mSumRank4 = Functions.sumRank4(mAPRankFunction4, 3.0);
                }

            }

            Log.d(TAG, "Total node connected: " + mAPShared + " sum probingFunctionsManager: " + mSumRank3);
        }


    }
}
