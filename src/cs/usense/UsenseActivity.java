/**
 * @version 1.2
 * COPYRIGHTS COPELABS/ULHT, LGPLv3.0, date (e.g. 22-04-2016)
 * Class is part of the USense application. This class controls the User Interface and update it based on UsenseService results. 
 * @author Saeik Firdose (COPELABS/ULHT), 
 * @author Luis Lopes (COPELABS/ULHT), 
 * @author Waldir Moreira (COPELABS/ULHT), 
 * @author Reddy Pallavali (COPELABS/ULHT)
 */

package cs.usense;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import cs.usense.bluetooth.BluetoothCore.socialWeight;
import cs.usense.inferenceModule.SocialityDetails;
import cs.usense.location.LocationEntry;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.bluetooth.BluetoothAdapter;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.util.Log;

/**
 * This class provides the layout of USense application and initialize the USense Service
 *
 */
public class UsenseActivity extends Activity {

	private static final String TAG = "USENSE";
	public static final String PKG = "cs.usense";

	/** This class provides the Usense Service to bound service*/
	private UsenseService mBoundService;
	
	/** This variable is to bound request */
	private boolean mRequestToBound = false;

	/** This variable is request to bound service */
	private boolean mIsServiceBound;
	
	/** This variable is to provide the sensor name */
	private String sensorname;
	
	/** This variable is to provide sensor interval */
	private String sensorinterval;
	
	/** This variable is to store the action message */
	public static String actionMessage = "";

	/** This class is to provide ListView*/
	private ListView siListView;
	
	/** This class is to get SocialityDetails object*/
	private SIAdapter mSIAdapter;
	
	/** This list of SocialityDetails objects to get the History list of SocialityDetails */
	private static ArrayList<SocialityDetails> listHistory;
	
	/** This list of SocialityDetails objects to get the Social Interaction */
	List<SocialityDetails> mSInteraction = new LinkedList<SocialityDetails>();
	
	/** This list of LocationEntry objects to get the location information */
	List<LocationEntry> mDataLocation = new LinkedList<LocationEntry>();

	/** This variable is request to bound service*/
	ListView listView;
	
	/** This variable is request to bound service*/
	CircleDisplay leftCircle;
	
	/** This variable is request to bound service*/
	CircleDisplay rightCircle;


	/**
	 * This method bind the USense service by passing the sensors details, and service connection
	 * @param sensors List of Sensors from SensorProduct class
	 */
	void doBindService(ArrayList<SensorProduct> sensors) {
		Log.i(TAG, "Inside the on doBindService method");
		if (sensors != null) {
			if (isMyServiceRunning(UsenseService.class)) {
				bindService(
						new Intent(UsenseActivity.this, UsenseService.class),
						mConnection, Context.BIND_AUTO_CREATE);
			} else {

				Bundle bundleObject = new Bundle();
				bundleObject.putSerializable("sensors", sensors);

				/** The following code forces visibility of BT to always. This 
				 * code works for 4.3 and latest versions
				 * of android. For Android versions prior to 4.3 the maximum 
				 * time visible is one hour.
				 * Even forcing the visibility, a alert message will appear to
				 * the user.
				 * The user must agree or not.
				 * After the Usense finish, the bluetooth must be disable and
				 * enable again to get the
				 * previous configurations (2 min of visibility). If this is not done, the BT visibility
				 * is always on, draining the device battery.
				 * To remove the alert (no user interaction) the device needs to
				 *  be rooted. As indicated at: https://code.google.com/p/android/issues/detail?id=15486
				 * */

				Intent intent = new Intent(
						BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
				intent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 0);
				startActivity(intent);

				Intent mIntent = new Intent(getBaseContext(),
						UsenseService.class);
				mIntent.putExtras(bundleObject);

				startService(mIntent);
				mRequestToBound = true;
				bindService(
						new Intent(UsenseActivity.this, UsenseService.class),
						mConnection, Context.BIND_AUTO_CREATE);
				Log.i(TAG, "First time calling startService");

			}
		}
	}


	/**
	 * This method creates the service connection, which can be used to bind the USense service
	 */
	private ServiceConnection mConnection = new ServiceConnection() {
		public void onServiceConnected(ComponentName className, IBinder service) {
			mBoundService = ((UsenseService.LocalBinder) service).getService();
			mIsServiceBound = true;
			mRequestToBound = false;
			mBoundService.setOnStateChangeListener(new ChangeListener() {

				@Override
				public void onGetSociabilityChange(ArrayList<SocialityDetails> newSocial) {
					listHistory =newSocial;
					Log.e(TAG, newSocial + "");
					if (newSocial == null)
						return;

					mSIAdapter.clear();

					/** get the minimum distance */
					double minDistance = 0.0;
					String devName = "";

					LinkedList<Double> list = new LinkedList<Double>();

					for(int i=0;i<newSocial.size();i++){
						SocialityDetails details=newSocial.get(i);
						if(details.getmDistance() != 0.0){
							list.add(details.getmDistance());
						}
					}

					if (list.isEmpty())
						return;
					else {
						minDistance = Collections.min(list);
					}


					/** for circles */
					double maxSI=0;
					double maxProp=0;
					double avgSI = 0.0;
					double sumSI = 0.0;
					double avgProp = 0.0;
					double sumProp = 0.0;

					for(int i=0;i<newSocial.size();i++){
						SocialityDetails details=newSocial.get(i);

						if(details.getmDistance()!=0.0)
						{

							if(details.getmSI()>maxSI){

								maxSI=details.getmSI();
								sumSI = sumSI + details.getmSI();

							}

							if(details.getmPropinquity()>maxProp){

								maxProp=details.getmPropinquity();                       
								sumProp = sumProp + details.getmPropinquity();

							}

							if(details.getmDistance() == minDistance){
								devName = details.getDevName();
							}

							/** for get Sociability details */
							mSIAdapter.add(details);

						}

					}


					mSIAdapter.notifyDataSetChanged();

					avgProp = sumProp / newSocial.size();
					avgSI = sumSI / newSocial.size();

					Log.i(TAG, "Avg  SIvalue " +avgSI);
					Log.i(TAG, "Avg  Prop value " +avgProp);
					Log.i(TAG, "Minumum distance  " +minDistance);
					Log.i(TAG, "Minumum distance to the device owner " +devName);

					double siPercentage = ((avgSI / 0.0564139626)) * 100.0;
					double propPercentage = ((avgProp / 3.45900878)) * 100.0;


					leftCircle.showValue((float) siPercentage, 100f, true);

					/**
					 * Yellow Alert = Propinquity < x% (Tells me that I’ve a low probability to keep my current SI)
					 * Red Alert = SI < x% (Tells me that I may become social isolated)
					 * Green1 Alert = Person A is Dist < x && SI > y (Tells me that a Person with whom I had good SI is close to me)
					 */


					if(siPercentage >= 20 && siPercentage <= 50){
						leftCircle.setColor(Color.YELLOW);
					} else if ( siPercentage >= 0.1 && siPercentage <= 20){
						leftCircle.setColor(Color.RED);
						final Toast tag = Toast.makeText(UsenseActivity.this, "You may become socially isolated",Toast.LENGTH_SHORT);

						tag.show();

						new CountDownTimer(49000, 1000)
						{

							public void onTick(long millisUntilFinished) {tag.show();}
							public void onFinish() {tag.show();}

						}.start();

						
					} else if (siPercentage >= 50 ){
						leftCircle.setColor(Color.GREEN);

						final Toast tag = Toast.makeText(UsenseActivity.this, devName + " is close to you",Toast.LENGTH_SHORT);

						tag.show();

						new CountDownTimer(49000, 1000)
						{

							public void onTick(long millisUntilFinished) {tag.show();}
							public void onFinish() {tag.show();}

						}.start();

						
					}

					rightCircle.showValue((float) propPercentage, 100f, true);

					Log.e(TAG, "propPercentage value :"  + propPercentage);
					Log.e(TAG, "socialInterPercent value :"  + siPercentage);
				}

				@Override
				public void onLocationOutdoorChange(String newMessage) {
					return;					
				}

				@Override
				public void onLocationIndoorChange(List<LocationEntry> apEntries) {
					
					return;	
				}

				@Override
				public void onActionChange(String newMessage) {
					return;	
				}

				@Override
				public void onSocialWeightChange(ArrayList<socialWeight> arrayList) {
					return;	
				}

				@Override
				public void onSoundLevelChange(String newSound) {
					return;	
				}

				@Override
				public void onSocialInteractionChange(double socialInt) {
					return;	
				}

				@Override
				public void onPropinquityChange(double propIn) {
					return;	
				}

			
			});

			Button btnStart = (Button) findViewById(R.id.btnStart);
			btnStart.setText("Stop");

		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
			mBoundService = null;
			mIsServiceBound = false;
			mRequestToBound = false;
		}
	};


	/**
	 * This method compare the map of location entries based on the sorted distance
	 */
	public class CustomComparator implements Comparator<LocationEntry> {

		@Override
		public int compare(LocationEntry entry1, LocationEntry entry2) {
			return (int) (entry2.getDistance() - entry1.getDistance());
		}
	}

	/**
	 * This method unbind the USense service
	 */
	void doUnbindService() {
		if (mIsServiceBound && mConnection != null) {
			unbindService(mConnection);
		}
		mBoundService = null;
		mIsServiceBound = false;
		mRequestToBound = false;
	}

	/**
	 * This method returns True/false if the USense service is running/not
	 * @param usenseService UsenseService class
	 * @return true/false 
	 */
	private boolean isMyServiceRunning(Class<UsenseService> usenseService) {
		Log.i(TAG, "Inside isMyService is running");
		ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);

		for (RunningServiceInfo service : manager
				.getRunningServices(Integer.MAX_VALUE)) {

			if (service.service.getClassName().endsWith(".UsenseService")) {
				Log.i(TAG, "Inside running" + usenseService.getClass().getName());
				return true;
			}
		}
		return false;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_v3);
		Log.i(TAG, "Inside the on create method");

		RelativeLayout relative = (RelativeLayout) findViewById(R.id.relative_layout);

		leftCircle = (CircleDisplay) findViewById(R.id.left_circle);
		rightCircle = (CircleDisplay) findViewById(R.id.right_circle);

		leftCircle.setAnimDuration(4000);
		leftCircle.setValueWidthPercent(50f);
		leftCircle.setFormatDigits(1);
		leftCircle.setDimAlpha(50);
		leftCircle.setTouchEnabled(false);
		leftCircle.setUnit("%");
		leftCircle.setStepSize(0.5f);
		leftCircle.setColor(Color.rgb(220,221,227));


		rightCircle.setAnimDuration(4000);
		rightCircle.setValueWidthPercent(50f);
		rightCircle.setFormatDigits(1);
		rightCircle.setDimAlpha(50);
		rightCircle.setTouchEnabled(false);
		rightCircle.setUnit("%");
		rightCircle.setStepSize(0.5f);
		rightCircle.setColor(Color.rgb(220,221,227));

		relative.setOnTouchListener(new OnSwipeTouchListener(this) {

			@Override
			public void onSwipeRight() {
				
				super.onSwipeRight();
			}

			@Override
			public void onSwipeLeft() {
				
				super.onSwipeLeft();
			}

			@Override
			public void onSwipeTop() {
				
				super.onSwipeTop();
				if(listHistory != null){
					Intent intent = new Intent(UsenseActivity.this, UsenseHistoryActivity.class);
					intent.putParcelableArrayListExtra("cs.usense", listHistory);

					startActivity(intent);

				} else {
					Toast.makeText(UsenseActivity.this, "History is being updated please wait 1 minute.", Toast.LENGTH_SHORT).show();
				}
			}

			@Override
			public void onSwipeBottom() {
				
				super.onSwipeBottom();
			}

		});
		/** Find the ListView resource. */
		siListView = (ListView) findViewById(R.id.SociabilityListView);

		mSIAdapter = new SIAdapter(UsenseActivity.this, mSInteraction ,
				R.layout.listview_si_row, UsenseActivity.this);
		siListView.setAdapter(mSIAdapter);
		mSIAdapter.notifyDataSetChanged();
		Log.e(TAG, "isMyServiceRunning"
				+ isMyServiceRunning(UsenseService.class) + "");
		if (isMyServiceRunning(UsenseService.class)) {
			bindService(new Intent(UsenseActivity.this, UsenseService.class),
					mConnection, Context.BIND_AUTO_CREATE);

		} else {
			final Button btnStart = (Button) findViewById(R.id.btnStart);
			final RelativeLayout one = (RelativeLayout) findViewById(R.id.ActionDetails);
			one.setVisibility(View.INVISIBLE);

			btnStart.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					String button = (String) ((Button) v).getText();

					if (button.equals(getString(R.string.btn_start))) {
						one.setVisibility(View.VISIBLE);
						btnStart.setVisibility(View.INVISIBLE);

						String xmlFile = "settings.xml";
						XmlPullParserFactory pullParserFactory;
						try {
							pullParserFactory = XmlPullParserFactory
									.newInstance();
							XmlPullParser parser = pullParserFactory
									.newPullParser();
							InputStream in_s = getApplicationContext()
									.getAssets().open(xmlFile);
							parser.setFeature(
									XmlPullParser.FEATURE_PROCESS_NAMESPACES,
									false);
							parser.setInput(in_s, null);
							Log.i(TAG, "Inside the try block onCreate method"
									+ in_s);
							ArrayList<SensorProduct> sensors = parseXML(parser);
							doBindService(sensors);
						} catch (XmlPullParserException e) {

							e.printStackTrace();
						} catch (IOException e) {
							
							e.printStackTrace();
						}

					} else {
						one.setVisibility(View.INVISIBLE);
						Button btnStart = (Button) findViewById(R.id.btnStart);
						btnStart.setText(getString(R.string.btn_start));
						mBoundService.closeAll();
						mBoundService.onDestroy();
						doUnbindService();
					}
				}
			});
		}
	}

	/**
	 * This method parse the XML data
	 * @param parser The Xml Pull Parser with list of sensors
	 * @return products List of sensors from SensorProduct
	 * @throws XmlPullParserException Xml Pull Parser Exception
	 * @throws IOException Input, Output Exception
	 */
	private ArrayList<SensorProduct> parseXML(XmlPullParser parser)
			throws XmlPullParserException, IOException {
		ArrayList<SensorProduct> products = new ArrayList<SensorProduct>();
		SensorProduct currentProduct = null;
		int eventType = parser.getEventType();
		String text = "";
		while (eventType != XmlPullParser.END_DOCUMENT) {
			String name = "";
			switch (eventType) {
			case XmlPullParser.START_DOCUMENT:
				products = new ArrayList<SensorProduct>();
				break;
			case XmlPullParser.TEXT:
				text = parser.getText();
				break;
			case XmlPullParser.START_TAG:
				name = parser.getName();
				if ((name.compareTo("sensor") == 0)
						|| (name.compareTo("adapter") == 0)) {
					currentProduct = new SensorProduct();
				}
				break;
			case XmlPullParser.END_TAG:
				name = parser.getName();
				if ((name.compareTo("sensor") == 0)
						|| (name.compareTo("adapter") == 0)) {
					products.add(currentProduct);
				} else {
					if (currentProduct != null) {
						if (name != null) {
							if (name.compareTo("sensorname") == 0) {
								currentProduct.setSensorName(text);
							} else if (name.compareTo("sensorinterval") == 0) {
								currentProduct.setInterval(text);
							} else if (name.compareTo("classname") == 0) {
								currentProduct.setClassname(text);
							} else if (name.compareTo("adaptername") == 0) {
								currentProduct.setSensorName(text);
							}
						}
					}
				}
				break;
			}
			eventType = parser.next();

		}
		return products;
	}

	@Override
	public void onBackPressed() {

		if (mIsServiceBound && mConnection != null) {
			return;
		} else {
			if (mIsServiceBound) {
				doUnbindService();
			}
		}
		super.onBackPressed();
	}

	/**
	 * This method close the USense service
	 */
	protected void onDestroy() {

		if (mIsServiceBound) {
			doUnbindService();
		}

		super.onDestroy();
	}

	/**
	 * This method start the USense activity
	 */
	protected void onStart() {

		super.onStart();
	}

	/**
	 * This method stop the USense activity
	 */
	protected void onStop() {
		super.onStop();
	}

	/**
	 * This class implements the OnSwipeTouch functionality
	 *
	 */
	public class OnSwipeTouchListener implements OnTouchListener {

		private final GestureDetector gestureDetector;

		public OnSwipeTouchListener(Context ctx) {
			gestureDetector = new GestureDetector(ctx, new GestureListener());
		}

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			return gestureDetector.onTouchEvent(event);
		}

		private final class GestureListener extends SimpleOnGestureListener {

			private static final int SWIPE_THRESHOLD = 100;
			private static final int SWIPE_VELOCITY_THRESHOLD = 100;

			@Override
			public boolean onDown(MotionEvent e) {
				return true;
			}

			@Override
			public boolean onFling(MotionEvent e1, MotionEvent e2,
					float velocityX, float velocityY) {
				boolean result = false;
				try {
					float diffY = e2.getY() - e1.getY();
					float diffX = e2.getX() - e1.getX();
					if (Math.abs(diffX) > Math.abs(diffY)) {
						if (Math.abs(diffX) > SWIPE_THRESHOLD
								&& Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
							if (diffX > 0) {
								onSwipeRight();
							} else {
								onSwipeLeft();
							}
						}
						result = true;
					} else if (Math.abs(diffY) > SWIPE_THRESHOLD
							&& Math.abs(velocityY) > SWIPE_VELOCITY_THRESHOLD) {
						if (diffY > 0) {
							onSwipeBottom();
						} else {
							onSwipeTop();
						}
					}
					result = true;

				} catch (Exception exception) {
					exception.printStackTrace();
				}
				return result;
			}
		}

		public void onSwipeRight() {
		}

		public void onSwipeLeft() {
		}

		public void onSwipeTop() {

		}

		public void onSwipeBottom() {
		}
	}

}