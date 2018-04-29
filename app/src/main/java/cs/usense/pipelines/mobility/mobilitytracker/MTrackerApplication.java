/**
 *  Copyright (C) 2013 ULHT
 *  Author(s): jonnahtan.saltarin@ulusofona.pt
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License as
 *  published by  the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this program.  If not, see
 *  <http://www.gnu.org/licenses/>.
 *
 * ULOOP Mobility tracking plugin: Mtracker
 *
 * Mtracker is an Android app that collects information concerning visited APs
 * It computes a probingFunctionsManager and then estimates a potential handover - time and target AP
 * v1.0 - pre-prototype, D3.3, July 2012
 * v2.0 - prototype on September 2012 - D3.6
 * v3.0 - prototype on June 2013
 *
 * @author Jonnahtan Saltarin
 * @author Rute Sofia
 * @author Christian da Silva Pereira
 * @author Luis Amaral Lopes
 *
 * @version 3.0
 *
 * @file Contains MTrackerApplication class. This class is the Main Activity class for the
 * android application. It starts the MTracker server, if not running already.
 *
 */

package cs.usense.pipelines.mobility.mobilitytracker;

import android.app.ListActivity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.LinkedList;
import java.util.List;

import cs.usense.R;
import cs.usense.pipelines.mobility.interfaces.DataBaseChangeListener;
import cs.usense.pipelines.mobility.models.MTrackerAP;


/**
 * The Class MTrackerApplication is the front-end of the android application. It extends ListActivity
 * to show the list of visited access points as a ListView.  It starts the MTracker server, if not running already.
 *
 * @author Jonnahtan Saltarin (ULHT)
 * @author Rute Sofia (ULHT)
 * @author Christian da Silva Pereira (ULHT)
 * @author Luis Amaral Lopes (ULHT)
 *
 * @version 3.0
 */
public class MTrackerApplication extends ListActivity {
	
	/** The MTracker service to be bounded. */
	private MTrackerService mBoundService;
	
	/** Indicates if the MTracker service has been bounded successfully. */
	private boolean mIsServiceBound = false;
	
	/** The List that contains the visited access points as MTrackerAP. */
	List<MTrackerAP> data = new LinkedList<MTrackerAP>();
	
	/** The adapter to feed the ListView with the access points contained in data. */
	ArrayAdapter<MTrackerAP> adapter;
	
	/** The Connection to the MTracker service. When the service is successfully connected,
	 * it loads the ListView with the initial data and creates a listener on the MTracker
	 * service that re-populates the data on the ListView any tame the DataBase changes
	 */

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
		setContentView(R.layout.activity_main);		
		startService(new Intent(MTrackerApplication.this, MTrackerService.class));
		adapter = new ArrayAdapter<MTrackerAP>(this, android.R.layout.simple_list_item_1, data);
	    setListAdapter(adapter);


		getListView().setOnItemClickListener(new OnItemClickListener() {
        	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
               
        		MTrackerAP ap = (MTrackerAP)getListView().getItemAtPosition(position);

            	/*Intent i = new Intent(getApplicationContext(), DetailsActivity.class);
            	i.putExtra("bssid", ap.getBSSID());
            	i.putExtra("ssid", ap.getSSID());
            	i.putExtra("probingFunctionsManager", Double.toString(mBoundService.dataSource.getRank(ap)));
            	i.putExtra("stationarytime", Long.toString(mBoundService.dataSource.getStationaryTime(ap)));
            	i.putExtra("visitnumber", Long.toString(mBoundService.dataSource.countVisits(ap)));
            	//i.putExtra("devices", Long.toString(mBoundService.dataSource.devicesOnNetwork(ap)));
				i.putExtra("rejections", Long.toString(mBoundService.dataSource.rejectConnections(ap)));


				List<String> visitsList = mBoundService.dataSource.getAllVisitsString(ap);
            	
            	i.putExtra("visits", visitsList.toArray(new String[visitsList.size()]));
            	
             	startActivity(i);*/
          }
        });


		doBindService();
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
	    	    	mBoundService.startPeriodicScanning();
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

	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}
	
	/**
	 * Bind the MTracker service in case it is not already binded.
	 */
	void doBindService() {
		bindService(new Intent(MTrackerApplication.this, MTrackerService.class), mConnection, Context.BIND_AUTO_CREATE);
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
	}
	
}
