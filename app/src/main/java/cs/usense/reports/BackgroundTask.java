/**
 * @version 2.0
 * COPYRIGHTS COPELABS/ULHT, LGPLv3.0, 16-11-2015
 * Class is part of the NSense application.
 * This class build report on background
 * @author Miguel Tavares (COPELABS/ULHT)
 */

package cs.usense.reports;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.widget.Toast;

import java.util.ArrayList;

import cs.usense.R;
import cs.usense.preferences.GeneralPreferences;
import cs.usense.utilities.Utils;

class BackgroundTask {

    /** This variable is used to provide to the user the status of report */
    private ProgressDialog mProgressDialog;

    /**
     * This method is used to build the report on background
     * @param context application context
     * @param reportData report data
     * @param fileName report file name
     */
    void buildReport(final Context context, final ArrayList<String[]> reportData, final String fileName) {
        new AsyncTask<Void, Double, Void>() {

            @Override
            protected void onPreExecute() {
                mProgressDialog = new ProgressDialog(context);
                mProgressDialog.setMessage(context.getString(R.string.building_your_report));
                mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                mProgressDialog.setCanceledOnTouchOutside(false);
                mProgressDialog.setCancelable(false);
                mProgressDialog.show();
            }

            @Override
            protected void onProgressUpdate(Double... progress) {
                mProgressDialog.setProgress(progress[0].intValue());
            }

            @Override
            protected Void doInBackground(Void... params) {
                double percentage = 100.0 / reportData.size();
                for(int i = 0; i < reportData.size(); i++) {
                    Utils.appendLogs(fileName, reportData.get(i));
                    publishProgress(percentage * i);
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void result) {
                mProgressDialog.dismiss();
            }

        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    /**
     * This method creates a dialog to send the report by e-mail
     * @param context application context
     * @param fileName report file name
     */
    static void sendEmail(Context context, String fileName) {
        String email;
        if((email = GeneralPreferences.getReportEmail(context)) != null) {
            Intent emailIntent = new Intent(Intent.ACTION_SEND);
            // set the type to 'email'
            emailIntent.setType("vnd.android.cursor.dir/email");
            // set the destination address
            emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[] {email});
            // the attachment
            emailIntent.putExtra(Intent.EXTRA_STREAM, Utils.getCsvFileUri(fileName));
            // the mail subject
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, context.getString(R.string.users_report, fileName, Utils.getBluetoothName()));
            // the mail body
            emailIntent.putExtra(Intent.EXTRA_TEXT, context.getString(R.string.hi_here_is_your_report));
            // open activity to send the email
            context.startActivity(Intent.createChooser(emailIntent , context.getString(R.string.send_report_by_email)));
        } else {
            Toast.makeText(context, context.getString(R.string.send_report_by_email_error), Toast.LENGTH_LONG).show();
        }
    }


}
