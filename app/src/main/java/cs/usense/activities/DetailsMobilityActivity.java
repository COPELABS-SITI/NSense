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
 * @file Contains DetailsActivity. This class provides an activity that shows detailed information
 * of a given AP.
 *
 */

package cs.usense.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.text.DecimalFormat;

import cs.usense.R;

/**
 * This class provides an activity that shows detailed information
 * of a given AP.
 *
* @author Jonnahtan Saltarin (ULHT)
* @author Rute Sofia (ULHT)
* @author Christian da Silva Pereira (ULHT)
* @author Luis Amaral Lopes (ULHT)
*
* @version 3.0
*
*
*/
public class DetailsMobilityActivity extends AppCompatActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_mobility_details);
		Intent i = getIntent();

		((TextView)findViewById(R.id.bssid_value)).setText(i.getStringExtra("bssid"));
		((TextView)findViewById(R.id.ssid_value)).setText(i.getStringExtra("ssid"));
		((TextView)findViewById(R.id.rank_value)).setText(i.getStringExtra("probingFunctionsManager"));
		((TextView)findViewById(R.id.visitnumber_value)).setText(i.getStringExtra("visitnumber"));
		((TextView)findViewById(R.id.stationarytime_value)).setText(i.getStringExtra("stationarytime"));
		((TextView)findViewById(R.id.rejections_value)).setText(i.getStringExtra("rejections"));
		((TextView)findViewById(R.id.attractiveness_value)).setText("" + i.getDoubleExtra("attractiveness", 0.0));

		ListView listview = (ListView)findViewById(R.id.listView1);
		final String[] visits = i.getStringArrayExtra("visits");
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, visits);
		listview.setAdapter(adapter);

	}
	
	@Override
	protected void onStart() {		
		super.onStart();
	}
	
	@Override
	protected void onStop() {
	    super.onStop();
	}

	@Override
	protected void onDestroy() {
	    super.onDestroy();
	}

}
