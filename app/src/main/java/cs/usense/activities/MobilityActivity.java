package cs.usense.activities;


import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

import cs.usense.R;

import cs.usense.pipelines.mobility.fragments.AttractivenessDialogFragment;
import cs.usense.pipelines.mobility.interfaces.DataBaseChangeListener;
import cs.usense.pipelines.mobility.mobilitytracker.MTrackerService;
import cs.usense.pipelines.mobility.models.MTrackerAP;
import cs.usense.pipelines.mobility.utils.Utils;

public class MobilityActivity extends ActionBarActivity implements AttractivenessDialogFragment.AttractivenessDialogListener, AlarmReceiverInterface {

    private static final String TAG = MobilityActivity.class.getSimpleName();
    private MTrackerService mBoundService;

    /** Indicates if the MTracker service has been bounded successfully. */
    private boolean mIsServiceBound = false;

    /** The List that contains the visited access points as MTrackerAP. */
    List<MTrackerAP> data = new LinkedList<MTrackerAP>();

    /** The adapter to feed the ListView with the access points contained in data. */
    ArrayAdapter<MTrackerAP> adapter;

    private ListView lstView;

    /** The Connection to the MTracker service. When the service is successfully connected,
     * it loads the ListView with the initial data and creates a listener on the MTracker
     * service that re-populates the data on the ListView any tame the DataBase changes
     */

    private int mAttempt = 0;

    private ServiceConnection mConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            mBoundService = ((MTrackerService.LocalBinder)service).getService();
            mIsServiceBound = true;
            updateListView (mBoundService.getData());
            mBoundService.setOnStateChangeListener(
                    new DataBaseChangeListener() {
                        public void onDataBaseChange(List<MTrackerAP> apEntries) {
                            updateListView (apEntries);
                        }
                        public void onStatusMessageChange(String newMessage)  {
                            ((TextView)findViewById(R.id.infoText)).setText(newMessage);
                        }
                    }
            );

            displayConfiguration();
        }

        public void onServiceDisconnected(ComponentName className) {
            mBoundService.clearOnStateChangeListeners ();
            mBoundService = null;
            mIsServiceBound = false;
        }
    };

    /* (non-Javadoc)
     * @see android.app.Activity#onCreate(android.os.Bundle)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mobility_1);
        Log.d(TAG,"on create--");
        AlarmInterfaceManager.registerListener(this);
        //Utils.setAlarm(this,11,14);

        startService(new Intent(MobilityActivity.this, MTrackerService.class));

        lstView = (ListView) findViewById(R.id.list);
        adapter = new ArrayAdapter<MTrackerAP>(this, android.R.layout.simple_list_item_1, data);
        lstView.setAdapter(adapter);

        lstView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                MTrackerAP ap = (MTrackerAP) lstView.getItemAtPosition(position);
                show(ap);
                return true;
            }
        });

        lstView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                AlarmInterfaceManager.unRegisterListener(MobilityActivity.this);
                MTrackerAP ap = (MTrackerAP) lstView.getItemAtPosition(position);

            	Intent i = new Intent(getApplicationContext(), DetailsMobilityActivity.class);
            	i.putExtra("bssid", ap.getBSSID());
            	i.putExtra("ssid", ap.getSSID());
            	i.putExtra("attractiveness", ap.getAttractiveness());
            	i.putExtra("probingFunctionsManager", Double.toString(mBoundService.dataSource.getRank(ap)));
            	i.putExtra("stationarytime", Long.toString(mBoundService.dataSource.getStationaryTime(ap)));
            	i.putExtra("visitnumber", Long.toString(mBoundService.dataSource.countVisits(ap)));
				i.putExtra("rejections", Long.toString(mBoundService.dataSource.rejectConnections(ap)));

				List<String> visitsList = mBoundService.dataSource.getAllVisitsString(ap);
                i.putExtra("visits", visitsList.toArray(new String[visitsList.size()]));

             	//startActivityForResult(i, 0);
            }
        });

        doBindService();


    }

    public void show(MTrackerAP ap){
        AttractivenessDialogFragment dialog = new AttractivenessDialogFragment(ap);
        dialog.show(getFragmentManager(),"Show");
    }
    /* (non-Javadoc)
     * @see android.app.Activity#onStart()
     */
    @Override
    protected void onStart() {
        super.onStart();
    }

    /* (non-Javadoc)
     * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_mobility, menu);
        return true;
    }

    /* (non-Javadoc)
     * @see android.app.Activity#onOptionsItemSelected(android.view.MenuItem)
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.stopScanningMenuEntry:
                if (mIsServiceBound) {
                    mBoundService.stopPeriodicScanning();
                }
                return true;
            case R.id.restartScanningMenuEntry:
                if (mIsServiceBound) {
                    mBoundService.wifiListener.statblishMandatoryConnection = 1;
                    mBoundService.wifiListener.setMandatoryAP("COPELABS");
                    mBoundService.wifiManager.connectToAP("COPELABS");
                    //mBoundService.startPeriodicScanning();
                }
                return true;
            case R.id.writeAPEntriesToFile:
                if (mIsServiceBound) {
                    mBoundService.writeAPListToFile ();
                }
                return true;
            case R.id.writeVisitListToFile:
                if (mIsServiceBound) {
                    mBoundService.writeVisitListToFile();
                }
                return true;
            case R.id.writeRankingToFile0:
                if (mIsServiceBound) {
                    mBoundService.writeRankingToFile("0");
                }
                return true;
            case R.id.connectoToAp:
                if(mIsServiceBound) {
                    mBoundService.wifiListener.statblishMandatoryConnection = 1;
                    mBoundService.wifiListener.setMandatoryAP("freeisg");
                    mBoundService.wifiManager.connectToAP("freeisg");

                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    @Override
    public void onBackPressed() {

        startActivity(new Intent(this, MainActivity.class));
        finish();
    }
    /**
     * Bind the MTracker service in case it is not already binded.
     */
    void doBindService() {
        bindService(new Intent(MobilityActivity.this, MTrackerService.class), mConnection, Context.BIND_AUTO_CREATE);
    }

    /**
     * Un-bind the MTracker service in case it is binded.
     */
    void doUnbindService() {
        if (mIsServiceBound && mConnection != null) {
            unbindService(mConnection);
        }
    }

    /* (non-Javadoc)
     * @see android.app.Activity#onStop()
     */
    @Override
    protected void onStop() {
        super.onStop();
    }

    /* (non-Javadoc)
     * @see android.app.ListActivity#onDestroy()
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        doUnbindService();
    }

    /**
     * Update the ListView, cleaning the actual values and populating it with the values on apEntries.
     *
     * @param apEntries
     *            the new access point entries to be shown.
     */
    private void updateListView (List<MTrackerAP> apEntries) {
        adapter.clear();
        for (MTrackerAP apEntry : apEntries) {
            adapter.add(apEntry);
        }
        ((TextView)findViewById(R.id.textViewCalculations)).setText("Calculation #: " + mBoundService.wifiListener.calculations);
    }

    private void displayConfiguration(){
        ((TextView)findViewById(R.id.textViewF0)).setText("Function_0: " + mBoundService.wifiListener.COMPUTE_PASSIVE_FUNCTION_0);
        ((TextView)findViewById(R.id.textViewF4)).setText("Function_4: " + mBoundService.wifiListener.COMPUTE_PASSIVE_FUNCTION_4);
        ((TextView)findViewById(R.id.textViewF1)).setText("Function_1: " + mBoundService.wifiListener.probingFunctionsManager.COMPUTE_FUNCTION_1);
        ((TextView)findViewById(R.id.textViewF2)).setText("Function_2: " + mBoundService.wifiListener.probingFunctionsManager.COMPUTE_FUNCTION_2);
        ((TextView)findViewById(R.id.textViewF3)).setText("Function_3: " + mBoundService.wifiListener.probingFunctionsManager.COMPUTE_FUNCTION_3);
        ((TextView)findViewById(R.id.textViewBestAP)).setText("Calculate best AP: " + mBoundService.wifiListener.COMPUTE_CALCULATE_BESTAP);
        ((TextView)findViewById(R.id.textViewConnBestAP)).setText("Connect to best AP: " + mBoundService.wifiListener.CONNECT_TO_BESTAP);

    }
    @Override
    public void onUpdateAP(MTrackerAP ap) {

        mBoundService.dataSource.updateAttractivenessAP(ap);
        mBoundService.notifyDataBaseChange();


    }

    @Override
    public void connectToAP(MTrackerAP ap) {
        mBoundService.wifiListener.statblishMandatoryConnection = 1;
        mBoundService.wifiManager.connectToAP(ap.getSSID());
    }

    @Override
    public void onAlarme() {
        Log.d(TAG, "---------------------------------------------");

        switch (mAttempt){
            case 0:
                mAttempt++;
                    mBoundService.wifiListener.statblishMandatoryConnection = 1;
                    mBoundService.wifiListener.setMandatoryAP("COPELABS");
                    mBoundService.wifiManager.connectToAP("COPELABS");
                Utils.setAlarm(this,11,53);

                break;
            case 1:
                mAttempt++;
                mBoundService.wifiListener.statblishMandatoryConnection = 1;
                mBoundService.wifiListener.setMandatoryAP("freeisg");
                mBoundService.wifiManager.connectToAP("freeisg");
                Utils.setAlarm(this,12,18);
                break;
            case 2:
                mAttempt++;
                if(!mBoundService.wifiListener.mSsid.equals("AP_1")){
                    mBoundService.wifiListener.setMandatoryAP("AP_1");
                    mBoundService.wifiListener.statblishMandatoryConnection = 1;
                    mBoundService.wifiManager.connectToAP("AP_1");
                }
                Utils.setAlarm(this,12,50);
                break;
            case 3:
                mAttempt++;
                mBoundService.writeAPListToFile ();
                mBoundService.writeVisitListToFile();
                mBoundService.writeRankingToFile("0");
                Toast.makeText(this,"Test ends",Toast.LENGTH_LONG).show();
                Utils.setAlarm(this,23,0);
                break;

        }
    }
}
