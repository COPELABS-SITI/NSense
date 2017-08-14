/*
 * COPYRIGHTS COPELABS/ULHT, LGPLv3.0, 2017/05/24.
 * Class is part of the NSense application.
 */

package cs.usense.reports;

import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.Socket;

import cs.usense.activities.ActionBarActivity;
import cs.usense.utilities.DateUtils;
import cs.usense.utilities.Utils;

/**
 * This class is responsible to send the all reports to siti server
 * @author Miguel Tavares (COPELABS/ULHT)
 * @version 1.0, 2017
 */
class ServerReportSender implements Runnable {

    /** This variable is used to debug ServerReportSender class */
    private static final String TAG = "ServerReportSender";

    /** This variable stores the server address */
    private static final String HOST_ADDRESS = "siti2.ulusofona.pt";

    /** This variable stores the socket port to transfer the reports */
    private static final int PORT = 8888;

    /** This variable sets if the report was build successfully */
    private static boolean mLastReportBuiltSuccessfully = false;

    /** This object is used to schedule the report sender */
    private Handler mHandler = new Handler();

    /**
     * This method is the constructor of ServerReportSender class
     */
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

    /**
     * This method generates the zip file to be send
     */
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

    /**
     * This method sends the zip file created to the siti server
     * @param file zip file reference
     */
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

    /**
     * This method stops the ServerReportSender
     */
    public void close() {
        mLastReportBuiltSuccessfully = false;
        mHandler.removeCallbacks(this);
    }

}
