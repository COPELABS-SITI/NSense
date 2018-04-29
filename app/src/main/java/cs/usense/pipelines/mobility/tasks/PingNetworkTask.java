package cs.usense.pipelines.mobility.tasks;

import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.net.InetAddress;

/**
 * This class is an async tack class used to executed ping into the network.
 *
 * Created by copelabs on 27/04/2017.
 */

public class PingNetworkTask extends AsyncTask<String,Integer,Integer> {

    /**
     * Interface used to notify action from this class.
     */
    public  interface FindOnNetworkInterface{
        /**
         * This method is used to notify when the task ends.
         * @param devices Number of devices connected to the AP.
         */
        void networkFinder(int devices);
    }

    /**
     * Variable used for debug.
     */
    private String TAG = PingNetworkTask.class.getSimpleName();

    /**
     * Interface of the task.
     */
    private FindOnNetworkInterface mInterface;

    /**
     * PingNetworkTask constructor.
     * @param findOnNetworkInterface Interface of the task.
     */
    public PingNetworkTask(FindOnNetworkInterface findOnNetworkInterface){
        mInterface=findOnNetworkInterface;
    }

    /**
     * This method executes in background the task in the network.
     * @param host Host of the access point.
     * @return
     */
    protected Integer doInBackground(String... host) {

        int totalSize = 0;
        String hostIp = host[0];

        try {

            for (int i = 0; i <= 255; i++) {

                String addr = hostIp;
                addr = addr.substring(0, addr.lastIndexOf('.') + 1) + i;

                if(InetAddress.getByName(addr).isReachable(50)){
                    Log.d(TAG, "Host: " + addr + " is Reachable");
                    totalSize ++;
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return totalSize;
    }

    /**
     * When the task ends a interface is notify with the number os the devices connected to the
     * access point.
     * @param result Number of devices connected.
     */
    protected void onPostExecute(Integer result) {
        Log.d(TAG, "Total Hosts: " + String.valueOf(result));
        mInterface.networkFinder(result);
    }

}

