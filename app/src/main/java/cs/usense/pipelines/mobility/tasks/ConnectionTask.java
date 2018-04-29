package cs.usense.pipelines.mobility.tasks;

import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;


/**
 * This class extend to AsyncTask class and is used to verify if there is internet connectivity
 * in the access point. To do this, a HTTP connection is establish to an specific URL.
 * Created by copelabs on 08/09/2017.
 */

public class ConnectionTask extends AsyncTask<String, Void, Integer> {

    /**
     * Variable used for debug.
     */
    private static final String TAG = ConnectionTask.class.getSimpleName();
    /**
     * Interface of this class.
     */
    private ConnectionInterface mInterface;

    /**
     * InternetConnectionTAsk Constructor.
     *
     * @param connectionInterface
     */
    public ConnectionTask(ConnectionInterface connectionInterface) {
        mInterface = connectionInterface;
    }

    /**
     * This method is executed in background and its function is to establish a HTTP connection
     * to a specific server.
     *
     * @param sUrl URL to connect.
     * @return
     */
    @Override
    protected Integer doInBackground(String... sUrl) {
        Log.d(TAG, "Start Connection Check " + sUrl[0]);
        int connection = 0;
        try {
            HttpURLConnection urlc = (HttpURLConnection) (new URL(sUrl[0]).openConnection());
            urlc.setRequestProperty("User-Agent", "Test");
            urlc.setRequestProperty("Connection", "close");
            urlc.setConnectTimeout(1500);
            urlc.connect();

            if (urlc.getResponseCode() == 200)
                connection = 1;
            else
                connection = 0;

        } catch (IOException e) {
            Log.e("Connection", "Error checking internet connection", e);
        }

        return connection;
    }

    /**
     * When the task ends the interface is notify with the status of the internet connectivity.
     *
     * @param result 1 if there is internet connection, otherwise 0.
     */
    @Override
    protected void onPostExecute(Integer result) {
        mInterface.connection(result);
    }

    /**
     * Interface of this class used to communicate some results.
     */
    public interface ConnectionInterface {
        /**
         * Method used to notify the status of the internet connectivity.
         * 1 if ther is internet connection, otherwise 0.
         *
         * @param connection
         */
        void connection(int connection);
    }
}
