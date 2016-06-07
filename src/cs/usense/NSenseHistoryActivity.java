/**
 * @version 1.3
 * COPYRIGHTS COPELABS/ULHT, LGPLv3.0, date (e.g. 22-04-2016)
 * Class is part of the NSense application. It provides support to the NSense History Activity.
 * @author Saeik Firdose (COPELABS/ULHT)
 */
package cs.usense;

import java.util.ArrayList;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import cs.usense.R;
import cs.usense.inferenceModule.SocialityDetails;

/**
 * This class provides layout of the NSenseHistoryActivity
 */
public class NSenseHistoryActivity  extends Activity {


	private static final String TAG = "NSenseHistoryActivity";
	
	/** This list of SocialityDetails objects to get the History list of SocialityDetails */
	public static ArrayList<SocialityDetails> listHistory;
	

	/** This class is to get History Listview */
	private ListView historyListView;
	
	/** NSense History Adapter class */
	NSenseHistoryAdapter nsenseHistoryAdapter;
	
	/** NSense Activity class */
	NSenseActivity nsenseActivity;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		getActionBar().setDisplayShowTitleEnabled(false);

		if(getIntent() != null){
			listHistory = new ArrayList<SocialityDetails>();
			listHistory = (ArrayList<SocialityDetails>)getIntent().getSerializableExtra("cs.nsense");

			if(listHistory != null){
				Log.i(TAG, "listHistory size ####### " + listHistory.size()+"");

				for (SocialityDetails entry : listHistory) {
					Log.e(TAG, "####### " + entry.getDevName() + " " + entry.getmDistance() + " "+  entry.getmSI() + " " + entry.getmPropinquity() + " " + entry.getmSiEMA() + " "+  entry.getmPropEMA() + " " + entry.getmPrevSiEMA() + " " + entry.getmPrevPropEMA() );	
				}			
			}
		}

		setContentView(R.layout.history_activity);
		historyListView = (ListView) findViewById(R.id.ProximityListView);

		nsenseHistoryAdapter=new NSenseHistoryAdapter(this, listHistory, R.layout.listview_history, nsenseActivity);
		historyListView.setAdapter(nsenseHistoryAdapter);
	}
}
