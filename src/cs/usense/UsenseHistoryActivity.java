/**
 * @version 1.2
 * COPYRIGHTS COPELABS/ULHT, LGPLv3.0, date (e.g. 22-04-2016)
 * Class is part of the USense application. It provides support to the USense History Activity.
 * @author Saeik Firdose (COPELABS/ULHT)
 */
package cs.usense;

import java.util.ArrayList;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import cs.usense.inferenceModule.SocialityDetails;

/**
 * This class provides layout of the UsenseHistoryActivity
 */
public class UsenseHistoryActivity  extends Activity {


	private static final String TAG = "UsenseHistoryActivity";
	
	/** This list of SocialityDetails objects to get the History list of SocialityDetails */
	public static ArrayList<SocialityDetails> listHistory;
	

	/** This class is to get History Listview */
	private ListView historyListView;
	
	/** USense History Adapter class */
	UsenseHistoryAdapter usenseHistoryAdapter;
	
	/** USense Activity class */
	UsenseActivity usenseActivity;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if(getIntent() != null){
			listHistory = new ArrayList<SocialityDetails>();
			listHistory = (ArrayList<SocialityDetails>)getIntent().getSerializableExtra("cs.usense");

			if(listHistory != null){
				Log.i(TAG, "listHistory size ####### " + listHistory.size()+"");

				for (SocialityDetails entry : listHistory) {
					Log.e(TAG, "####### " + entry.getDevName() + " " + entry.getmDistance() + " "+  entry.getmSI() + " " + entry.getmPropinquity() + " " + entry.getmSiEMA() + " "+  entry.getmPropEMA() + " " + entry.getmPrevSiEMA() + " " + entry.getmPrevPropEMA() );	
				}			
			}
		}

		setContentView(R.layout.history_activity);
		historyListView = (ListView) findViewById(R.id.ProximityListView);

		usenseHistoryAdapter=new UsenseHistoryAdapter(this, listHistory, R.layout.listview_history, usenseActivity);
		historyListView.setAdapter(usenseHistoryAdapter);
	}
}
