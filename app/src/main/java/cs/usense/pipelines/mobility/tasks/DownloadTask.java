package cs.usense.pipelines.mobility.tasks;

import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;


/**
 * Class is an asynctask class which is used to calculate network utilization. To do this,
 * a file is downloaded from a server and is counting the time used to do it.
 * Created by copelabs on 08/09/2017.
 */

public class DownloadTask extends AsyncTask<String, Void, Long> {
    /**
     * Variable use for debug.
     */
    private static final String TAG = DownloadTask.class.getSimpleName();
    /**
     * This variable is used to notify when the file is downloaded.
     */
    private static final int mDownloadFailed = 0;
    /**
     * This is a counter for the number of attempts to execute this task.
     */
    private int mAttemps = 0;
    /**
     * Interface used to notify when the task ends.
     */
    private DonwnloadTaskInterface mInterface;

    /**
     * Downloadtask Constructor.
     *
     * @param donwnloadTaskInterface interface of the task.
     * @param attemps                number of attempts.
     */
    public DownloadTask(DonwnloadTaskInterface donwnloadTaskInterface, int attemps) {
        mAttemps = attemps;
        mInterface = donwnloadTaskInterface;
    }

    /**
     * This method downloads a file from a server ann is counting the time used to do it.
     *
     * @param sUrl
     * @return
     */
    @Override
    protected Long doInBackground(String... sUrl) {
        Log.d(TAG, "Start Donwload " + sUrl[0]);
        long startTime = 0;
        long finishTime = 0;
        InputStream input = null;
        OutputStream output = null;
        HttpURLConnection connection = null;
        try {
            startTime = System.currentTimeMillis();
            URL url = new URL(sUrl[0]);
            connection = (HttpURLConnection) url.openConnection();
            connection.connect();

            // expect HTTP 200 OK, so we don't mistakenly save error report
            // instead of the file
            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                Log.d(TAG, "Server returned HTTP " + connection.getResponseCode()
                        + " " + connection.getResponseMessage());
            }

            // download the file
            input = connection.getInputStream();
            output = new FileOutputStream(Environment
                    .getExternalStorageDirectory().toString()
                    + "/file.dat");

            byte data[] = new byte[2048];
            int count;
            while ((count = input.read(data)) != -1) {
                // allow canceling with back button
                Log.d(TAG, "Downloading " +(System.currentTimeMillis()-startTime)/1000);
                if((System.currentTimeMillis()-startTime)>10000){
                    return Long.valueOf(mDownloadFailed);
                }
                if (isCancelled()) {
                    input.close();
                    return null;
                }

                output.write(data, 0, count);

            }
            finishTime = System.currentTimeMillis();
        } catch (Exception e) {
            Log.e(TAG, e.toString());


        } finally {
            try {
                if (output != null)
                    output.close();
                if (input != null)
                    input.close();
            } catch (IOException ignored) {
            }

            if (connection != null)
                connection.disconnect();
        }
        return finishTime - startTime;

    }

    /**
     * When the task end, this method is called and is calculated the network utilization
     * of the AP.
     *
     * @param result Time used to download the file.
     */
    @Override
    protected void onPostExecute(Long result) {

        if (result > 0) {
            float seconds = (float) result / 1000;
            float networkUtilization = (float) 1.25 / seconds;
            Log.d(TAG, "Time: " + seconds);
            Log.d(TAG, "NetworkUtilization: " + networkUtilization);
            mInterface.donwloadTime(networkUtilization);
        } else {
            Log.d(TAG, "NetworkUtilization: " + mDownloadFailed);
            mInterface.donwloadTime(mDownloadFailed);
            //When the task fails, is execute again the task, this is done maximum two time.
            /*if (mAttemps < 2) {
                Log.d(TAG, "Start a new download ");
                mAttemps++;

                new DownloadTask(mInterface, mAttemps).execute("http://ovh.net/files/10Mb.dat");
            } else {
                mInterface.donwloadTime(mDownloadFailed);
            }*/
        }

    }

    /**
     * Interface used to notify actions from this class.
     */
    public interface DonwnloadTaskInterface {
        /**
         * This method is used to notify the network utilization calculated by this task.
         *
         * @param networkUtilization
         */
        void donwloadTime(float networkUtilization);
    }
}
