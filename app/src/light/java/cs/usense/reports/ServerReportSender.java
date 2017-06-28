package cs.usense.reports;

import android.content.Context;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.List;

import cs.usense.activities.ActionBarActivity;
import cs.usense.utilities.DateUtils;
import cs.usense.utilities.Utils;

import static android.content.Context.WIFI_SERVICE;


class ServerReportSender implements Runnable{

    private static final String TAG = "ServerReportSender";

    private static final String HOST_ADDRESS = "siti2.ulusofona.pt";

    private static final int PORT = 8888;

    private static boolean mLastReportBuiltSuccessfully = false;

    private Handler mHandler = new Handler();


    ServerReportSender() {
        mHandler.post(this);
    }

    @Override
    public void run() {
        Log.i(TAG, "Checking if I should send the file");
        Log.i(TAG, "mLastReportBuiltSuccessfully: " + mLastReportBuiltSuccessfully);
        if(!mLastReportBuiltSuccessfully) {
            if(DateUtils.isMidNight()) {
                generateZip();
                sendReportData(Utils.mostRecentFile());
            }
        } else {
            sendReportData(Utils.mostRecentFile());
        }
        mHandler.postDelayed(this, 20 * 1000);
    }


    private void generateZip() {
        try {
            Log.i(TAG, "Creating file to send");
            Utils.dbBackup(ActionBarActivity.getActivityContext());
            SocialReport.buildReport(ActionBarActivity.getActivityContext());
            InterestsReport.buildReport(ActionBarActivity.getActivityContext());
            MergedReport.buildReport(ActionBarActivity.getActivityContext());
            Utils.zipExperimentDir();
            mLastReportBuiltSuccessfully = true;
        } catch (IOException e) {
            Log.e(TAG, "Problem creating zip file");
            e.printStackTrace();
        }
    }


    private void sendReportData(final File file) {
        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... params) {
                try {
                    Socket socket = new Socket(HOST_ADDRESS, PORT);
                    DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
                    FileInputStream fis = new FileInputStream(file);
                    byte[] bufferWrite = new byte[fis.available()];
                    fis.read(bufferWrite, 0, bufferWrite.length);
                    dos.writeChars(file.getName());
                    dos.flush();
                    dos.write(bufferWrite, 0, bufferWrite.length);
                    dos.close();
                    socket.close();
                    Log.e(TAG, "File sent with success " + file.getName());
                    mLastReportBuiltSuccessfully = false;
                } catch (IOException e) {
                    Log.e(TAG, "Cannot send " + file.getName());
                    e.printStackTrace();
                }
                return null;
            }
        }.execute();
    }

    public void close() {
        mLastReportBuiltSuccessfully = false;
        mHandler.removeCallbacks(this);
    }

}
