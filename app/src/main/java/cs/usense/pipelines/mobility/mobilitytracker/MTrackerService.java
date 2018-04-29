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
 */

package cs.usense.pipelines.mobility.mobilitytracker;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.ScanResult;

import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import cs.usense.activities.AlarmInterfaceManager;
import cs.usense.activities.AlarmReceiverInterface;
import cs.usense.activities.MainActivity;
import cs.usense.activities.MobilityActivity;
import cs.usense.pipelines.mobility.functions.Functions;
import cs.usense.pipelines.mobility.functions.ProbingFunctionsManager;
import cs.usense.pipelines.mobility.helpers.MTrackerDataSource;
import cs.usense.pipelines.mobility.interfaces.DataBaseChangeListener;
import cs.usense.pipelines.mobility.interfaces.WifiChangeListener;
import cs.usense.pipelines.mobility.models.MTrackerAP;
import cs.usense.pipelines.mobility.tasks.TxtRecord;
import cs.usense.pipelines.mobility.utils.Utils;
import cs.usense.preferences.WifiP2pTxtRecordPreferences;
import cs.usense.wifi.p2p.TextRecordKeys;


/**
 * This class is contains the core functionalities of
 * the application. The MTrackerService will run in background, getting WI-FI parameters and
 * storing the required information in the database.
 *
 * @author Jonnahtan Saltarin (ULHT)
 * @author Rute Sofia (ULHT)
 * @author Christian da Silva Pereira (ULHT)
 * @author Luis Amaral Lopes (ULHT)
 *
 * @version 3.0
 *
 */
public class MTrackerService extends Service  {
	public double uloopDispositionalTrust = 1.0;

	public MTrackerWifiManager wifiManager;
	public MTrackerServiceWifiListener wifiListener;
	public MTrackerDataSource dataSource;
	private ArrayList<DataBaseChangeListener> listeners = new ArrayList<DataBaseChangeListener>();
    private final IBinder mBinder = new LocalBinder();
	Context context;
	public class LocalBinder extends Binder {
    	public MTrackerService getService() {
            return MTrackerService.this;
        }
    }
    
    public List<MTrackerAP> getData () {
    	if (dataSource != null)
    		return new ArrayList<MTrackerAP>(dataSource.getAllAP().values());
    	else
    		return null;
    }

    /**
     * Starts the periodic scanning. This will call the adequate function in the MTrackerWifiManager,
     * which will start a scan periodically. The time between each scan is defined in the
     * MTrackerWifiManager class.
     *
     */
	public void startPeriodicScanning () {
		wifiManager.startPeriodicScanning();
	}

    /**
     * Stops the periodic scanning.
     *
     */
	public void stopPeriodicScanning () {
		wifiManager.stopPeriodicScanning();
	}
	
    @Override
    public void onCreate() {
    	super.onCreate();
	context=this;
		dataSource = new MTrackerDataSource(this);
		dataSource.openDB(true);
		wifiManager = new MTrackerWifiManager(this);
		wifiListener = new MTrackerServiceWifiListener();
		wifiManager.setOnWifiChangeListener(wifiListener);
		wifiManager.noteOngoingConnection();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
    	wifiManager.startPeriodicScanning();
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
    	wifiManager.close(this);
		dataSource.closeDB();
    }
    
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }
        
    public void setOnStateChangeListener (DataBaseChangeListener listener) 
    {
        this.listeners.add(listener);
    }
    
    public void clearOnStateChangeListeners () 
    {
        this.listeners.clear();
    }

    /**
     * Notifies a database change to the listeners.
     */
    public void notifyDataBaseChange () {
    	for (DataBaseChangeListener listener : this.listeners) 
    	{
    	    listener.onDataBaseChange(new ArrayList<MTrackerAP>(dataSource.getAllAP().values()));
    	}
    }

    /**
     * Notifies a new message to the listeners.
     *
     * @param newMessage The message to be notified.
     */
    private void notifyPredictedMoveChange (String newMessage) {
    	for (DataBaseChangeListener listener : this.listeners) 
    	{
    	    listener.onStatusMessageChange(newMessage);
    	}
    }

    /**
     * Writes the AP list to a text file.
     */
	public void writeAPListToFile () {
		dataSource.writeAPListToFile();
	}

    /**
     * Write visits to File
     */
	public void writeVisitListToFile () {
		dataSource.writeVisitListToFile();
	}

	/**
	 * Write ranking to File
	 */
	public void writeRankingToFile (String function) {
		dataSource.getAllRANK();
	}

    /**
     * Sets the ULOOP Dispositional Trust, which is the default attractiveness.
     *
     * @param uloopDT Uloop dispositional trust.
     * @return true if uloopDT is valid [0-1], false otherwise
     */
	public boolean setUloopDispositionalTrust (double uloopDT) {
		if (uloopDT >= 0.0 && uloopDT <= 1.0) {
			this.uloopDispositionalTrust = uloopDT;
			return true;
		}
		else {
			return false;
		}
	}


    /**
     * Sends a message to the Access Point with information about sverage stationary time in this AP,
     * if this is the best AP available for this device, if a change is expected soon and to which AP.
     *
     * The message is sent using Protobuffers, and the proto definition is shown below:
     *
     * message MTrackerMessage {
     *
     * 		message MTrackerPredictedMove {
     *			required string BSSID = 1;
     *			optional uint64 stationaryTime = 2;
     *			optional string lastGatewayIP = 3;
     * 		}
     *
     * 		required uint64 timeForNextMove = 1;
     * 		required uint64 currentStationaryTime = 2;
     * 		repeated MTrackerPredictedMove = 3 [packed=true];
     * }
     *
     * @param nextBssid BSSID of the next AP (The best ranked AP available). If the best AP is the
     *                  current one, this will be the string "this".
     *
     * @param nextLastGatewayIp Last IP used by the predicted next AP.
     *
     * @param nextStationaryTime Average connection time (Stationary Time) in the predicted next AP.
     *
     * @param timetoMove Expected time to change AP. When the message is sent at the beginning of the connection,
     *                   it is equal to the currentStationaryTime.
     *
     * @param currentStationaryTime  Average connection time (Stationary Time) in the current AP.
     */
	private void announcePossibleHandover (String nextBssid, String nextLastGatewayIp, long nextStationaryTime, long timetoMove, long currentStationaryTime) {

		String mtrackerServer;
		
		byte[] bytes = BigInteger.valueOf(wifiManager.getGatewayIp()).toByteArray();
		if (bytes.length == 4) {
			mtrackerServer = (bytes[3] & 0xFF) + "." + (bytes[2] & 0xFF) + "." + (bytes[1] & 0xFF) + "." + "25";
		}
		else {
			mtrackerServer = "192.168.3.25";
		}
		
		//new SendInformationWithGatewayTask().execute(nextBssid, nextLastGatewayIp, Long.toString(nextStationaryTime), Long.toString(timetoMove), Long.toString(currentStationaryTime), mtrackerServer);
		
		String message = "Time for next move: " + timetoMove + "\nCurrent ST: " + currentStationaryTime;
		message += "\nNext AP - BSSID: " + nextBssid + "\nNext AP - ST: " + nextStationaryTime + "\nNext AP - Gateway: " + nextLastGatewayIp;
		
		Toast.makeText(this, message, Toast.LENGTH_LONG).show();
	}
	
	public class MTrackerServiceWifiListener implements WifiChangeListener, ProbingFunctionsManager.RankInterface{
		private final String TAG = MTrackerServiceWifiListener.class.getSimpleName();

		private static final int CALCULATION_INTERVAL = 300000;
		public final boolean COMPUTE_ACTIVE_FUNCTIONS =false;
		public final boolean COMPUTE_PASSIVE_FUNCTION_0 = false;
		public final boolean COMPUTE_PASSIVE_FUNCTION_4 = false;
		public final boolean COMPUTE_CALCULATE_BESTAP = false;
		public final boolean CONNECT_TO_BESTAP =false;
		private int mFunction = 0;
		private String bssid;
		private List<ScanResult> results;
		public int calculations=0;
		private SimpleDateFormat periodFormat = new SimpleDateFormat("HH:mm:ss");
		private SimpleDateFormat dataFormat = new SimpleDateFormat("yyyy.MM.dd");
		public int statblishMandatoryConnection=0;
		private long lastCalculation=0;
		private TxtRecord txtRecord = new TxtRecord();
		public ProbingFunctionsManager probingFunctionsManager = new ProbingFunctionsManager(this,txtRecord);
		private float timeLastConnectionAvg;
		private long lastVisitDuration=0;
		private long gapVisit=0;
		private float gammaTimeConnection =0;
		private float lastGammaGap=0;
		private String rankTime;
		private String rankDay;
		private float batteryPct;
		private long time;
		public String mSsid;
		public String mAPMandatory;

		public void setMandatoryAP(String SSID){
			mAPMandatory=SSID;
		}
		public MTrackerServiceWifiListener() {
			Log.d(TAG, "Service starts");
		}

		public void onWifiStateDisabled(boolean valid, String bssid, String ssid, long visitId, long connectionStart, long connectionEnd) {

			notifyPredictedMoveChange("Wifi turned OFF");
			if (valid) {
				dataSource.updateVisit(visitId, null, null, null, connectionEnd);
				notifyDataBaseChange ();
			}
		}

		public void onWifiStateEnabled () {
			notifyPredictedMoveChange("Wifi turned ON");
		}

		public void onWifiConnectionDown(boolean valid, String bssid, String ssid, long visitId, long connectionStart, long connectionEnd) {
			probingFunctionsManager.setIsComputing(false);
			calculations=0;
			txtRecord.deleteRecommendations();
			txtRecord.setmBSSIDConnected("");
			notifyPredictedMoveChange("Wifi connection DOWN");
			if (valid) {
				dataSource.updateVisit(visitId, null, null, null, connectionEnd);
				notifyDataBaseChange ();
			}
		}
		public long onWifiConnectionUp(String bssid, String ssid, List<ScanResult> lastScanResults) {

			ssid = ssid.replace("\"","");
			mSsid=ssid;
			lastCalculation = System.currentTimeMillis();
			calculations=0;
			notifyPredictedMoveChange("Wifi connection UP (" + ssid + ")");
			statblishMandatoryConnection=0;
			final MTrackerAP mTrackerAP;
			if (!dataSource.hasAP(ssid)) {
				MTrackerAP ap = new MTrackerAP();
				ap.setBSSID(ssid);
				ap.setSSID(ssid);
				ap.setAttractiveness(uloopDispositionalTrust);
				ap.setLastGatewayIp(wifiManager.getGatewayIp());
				ap.setNetworkUtilization(0);
				ap.setDevicesOnNetwork(0);
				ap.setRejected(0);
				dataSource.registerNewAP(ap);
				mTrackerAP=ap;
			}
			else {
				MTrackerAP ap = dataSource.getAP(ssid);
				ap.setBSSID(ssid);
				ap.setSSID(ssid);
				ap.setLastGatewayIp(wifiManager.getGatewayIp());
				ap.setRejected(0);
				dataSource.updateAP(ap);
				mTrackerAP=ap;
			}

			if(COMPUTE_PASSIVE_FUNCTION_4 || probingFunctionsManager.COMPUTE_FUNCTION_2 || probingFunctionsManager.COMPUTE_FUNCTION_3) {
				WifiP2pTxtRecordPreferences.setRecord(MTrackerService.this, TextRecordKeys.AP, mTrackerAP.getBSSID());
				WifiP2pTxtRecordPreferences.setRecord(MTrackerService.this, TextRecordKeys.RANK_FUNCTION_4, dataSource.getRankEMA(mTrackerAP, 4) + "");
				WifiP2pTxtRecordPreferences.setRecord(MTrackerService.this, TextRecordKeys.RANK_FUNCTION_3,  dataSource.getRankEMA(mTrackerAP,3)+ "");
				txtRecord.setmBSSIDConnected(mTrackerAP.getBSSID());
			}


			notifyDataBaseChange ();

			lastVisitDuration=dataSource.getLastMesurement(mTrackerAP);
			gammaTimeConnection = dataSource.getLastGAMMA(mTrackerAP);

			lastGammaGap = dataSource.getLastGAMMAGAP(mTrackerAP,mFunction);

			if(dataSource.getLastDesconnection(mTrackerAP)==0){
				gapVisit=0;
			}else {
				gapVisit = (wifiManager.wifiCurrentAPStart - dataSource.getLastDesconnection(mTrackerAP)) / 1000;
			}
			Log.d(TAG, "Last Duration:" + lastVisitDuration);
			timeLastConnectionAvg=Functions.functionGammaTimeDisconnection(lastGammaGap,gapVisit);

			return dataSource.registerNewVisit(ssid, ssid, wifiManager.wifiCurrentAPStart, wifiManager.wifiCurrentAPStart);
		}
		public void onWifiAvailableNetworksChange(String bssid, List<ScanResult> results) {
			bssid = bssid.replace("\"","");
			Log.d(TAG, "new scan available" + bssid);
			if(COMPUTE_CALCULATE_BESTAP && bssid!=null) {
				if ((System.currentTimeMillis() - lastCalculation) > CALCULATION_INTERVAL) {
						computeBestAp(bssid, results);
						lastCalculation = System.currentTimeMillis();
						this.bssid=bssid;
						this.results=results;
				}
			}else{
				Log.d(TAG, "Calculation best AP off");
			}
		}

		@Override
		public void onWifiAvailableList(List<ScanResult> results) {

			if(statblishMandatoryConnection==1){
					wifiManager.connectToAP(mAPMandatory);
			}else {
				if (COMPUTE_CALCULATE_BESTAP) {
					MTrackerAP bestAP = dataSource.getBestAP(results);
					if (bestAP != null)
						wifiManager.connectToAP(bestAP.getSSID());
				}
			}
			}

		@Override
		public void onConnectionRejected(String bssid, String ssid) {
			ssid = ssid.replace("\"","");
			if(!ssid.equals("<unknown ssid>")) {
				if (!dataSource.hasAP(ssid)) {
					MTrackerAP ap = new MTrackerAP();
					ap.setBSSID(ssid);
					ap.setSSID(ssid);
					ap.setAttractiveness(uloopDispositionalTrust);
					ap.setLastGatewayIp(wifiManager.getGatewayIp());
					ap.setRejections(1);
					ap.setRejected(1);
					dataSource.registerNewAP(ap);

				} else {
					Log.d(TAG, "Update access point rejected");
					MTrackerAP ap = dataSource.getAP(ssid);
					ap.setBSSID(ssid);
					ap.setSSID(ssid);
					ap.setLastGatewayIp(wifiManager.getGatewayIp());
					ap.setRejected(1);
					dataSource.updateAPRejected(ap);

				}
				Toast.makeText(context, "Rejection", Toast.LENGTH_SHORT).show();
			}
		}

		private void computeBestAp (String bssid, List<ScanResult> results) {
			Log.d(TAG, "Computing best AP");

			time = System.currentTimeMillis();
			batteryPct = Utils.batteryStatus(MTrackerService.this);
			rankTime = periodFormat.format(time);
			rankDay = dataFormat.format(time);

			MTrackerAP ap=dataSource.getAP(bssid);

			if(COMPUTE_ACTIVE_FUNCTIONS) {
				if (probingFunctionsManager.isComputing()) {
					Log.d(TAG, "ProbingFunctionsManager is computing");
				} else {
					Log.d(TAG, "Start new ProbingFunctionsManager");
					ap.setQuality(wifiManager.connectionQuality());
					probingFunctionsManager.startRankingCalulation(wifiManager.ipCalculation(), ap);
				}
			}else{
				Log.d(TAG, "Active functions OFF");
			}


			//Log.d(TAG, "last gammaTimeConnection: " + gammaTimeConnection);
			//Log.d(TAG, "Last Duration: " + lastVisitDuration);
			//Log.d(TAG, "Gap duration " + gapVisit);
			if(COMPUTE_PASSIVE_FUNCTION_0){

				gammaTimeConnection = Functions.functionGammaTimeConnection(dataSource.getLastGAMMA(ap,0),wifiManager.wifiCurrentAPStart, time);
				double LastGammaRank = dataSource.getLastGammaRank(ap,0);
				double Rank = Functions.function0(dataSource.countVisits(ap),ap.getRejections(),timeLastConnectionAvg, gammaTimeConnection,  (float) ap.getAttractiveness());
				dataSource.registerNewRank(ap,
						((time - wifiManager.wifiCurrentAPStart)/1000),
						gapVisit,
						gammaTimeConnection,
						timeLastConnectionAvg,
						0,
						Rank,
						Functions.functionGammaRank(LastGammaRank,Rank),
						rankTime,
						rankDay,
						batteryPct);
				calculations++;
			}

			if (COMPUTE_PASSIVE_FUNCTION_4){

				gammaTimeConnection = Functions.functionGammaTimeConnection(dataSource.getLastGAMMA(ap,4),wifiManager.wifiCurrentAPStart, time);
				double LastGammaRank = dataSource.getLastGammaRank(ap,4);
				ap.setRecommendation(Functions.sumRank4(txtRecord.getMapSumRank4(),dataSource.getRank(ap,4)));
				double Rank = Functions.function4(dataSource.countVisits(ap),ap.getRejections(),timeLastConnectionAvg, gammaTimeConnection, (float) ap.getAttractiveness(), ap.getRecommendation());
				dataSource.registerNewRank(ap,
						((time - wifiManager.wifiCurrentAPStart)/1000),
						gapVisit,
						gammaTimeConnection,
						timeLastConnectionAvg ,
						4,
						Rank,
						Functions.functionGammaRank(LastGammaRank,Rank),
						rankTime,
						rankDay,
						batteryPct);

				WifiP2pTxtRecordPreferences.setRecord(MTrackerService.this, TextRecordKeys.RANK_FUNCTION_4, dataSource.getRankEMA(ap,4) + "");
				calculations++;
			}

			notifyDataBaseChange();



			if(COMPUTE_ACTIVE_FUNCTIONS==false) {
			MTrackerAP bestAp = dataSource.getBestAP(results);

				if (bestAp != null) {
					if (bestAp.getBSSID().equals(bssid)) {
						//long timeToMove = dataSource.getStationaryTime(bestAp) - (System.currentTimeMillis() - wifiManager.wifiCurrentAPStart) / 1000;
						notifyPredictedMoveChange("Connected to the best AP (" + bestAp.getSSID() + ")");
						//announcePossibleHandover("this", bestAp.getLastGatewayIp(), dataSource.getStationaryTime(bestAp), timeToMove, dataSource.getStationaryTime(bestAp));

					} else {
							Log.d(TAG, "Connecting to " + bestAp.getSSID());

							wifiManager.connectToAP(bestAp.getSSID());
						/*long timeToMove = dataSource.getStationaryTime(dataSource.getAP(bssid)) - (System.currentTimeMillis() - wifiManager.wifiCurrentAPStart) / 1000;
						if (timeToMove >= 0) {
							notifyPredictedMoveChange("Handover to AP " + bestAp.getSSID() + " is expected to occur in about " + timeToMove + "s");
							announcePossibleHandover(bestAp.getBSSID(), bestAp.getLastGatewayIp(), dataSource.getStationaryTime(bestAp), timeToMove, dataSource.getStationaryTime(dataSource.getAP(bssid)));
						} else {
							notifyPredictedMoveChange("Handover to AP " + bestAp.getSSID() + " was expected to occur about " + (0 - timeToMove) + "s ago");
							announcePossibleHandover(bestAp.getBSSID(), bestAp.getLastGatewayIp(), dataSource.getStationaryTime(bestAp), (0 - timeToMove), dataSource.getStationaryTime(dataSource.getAP(bssid)));
						}*/
					}
				} else {
					notifyPredictedMoveChange("No AP in DB");
				}
			}
		}

		@Override
		public void rank(double rank, int function, MTrackerAP ap) {

			gammaTimeConnection = Functions.functionGammaTimeConnection(dataSource.getLastGAMMA(ap,function),wifiManager.wifiCurrentAPStart, time);

			double LastGammaRank = dataSource.getLastGammaRank(ap,function);
			//dataSource.registerNewRank(ap, 0,0, 0,0, function,probingFunctionsManager, Functions.functionGammaRank(LastGammaRank,probingFunctionsManager), rankTime,rankDay,batteryPct);

			dataSource.registerNewRank(ap, (time - wifiManager.wifiCurrentAPStart)/1000,gapVisit, gammaTimeConnection,timeLastConnectionAvg, function,rank, Functions.functionGammaRank(LastGammaRank,rank), rankTime,rankDay,batteryPct);
			if(function==3) {
				WifiP2pTxtRecordPreferences.setRecord(MTrackerService.this, TextRecordKeys.RANK_FUNCTION_3,  dataSource.getRankEMA(ap,3)+ "");
			}
			calculations++;
			notifyDataBaseChange();
			computeBestAPActiveFunctions(bssid,results);
		}

		private void computeBestAPActiveFunctions(String bssid, List<ScanResult> results){
			MTrackerAP bestAp = dataSource.getBestAP(results);

			if (bestAp != null) {
				if (bestAp.getBSSID().equals(bssid)) {
					//long timeToMove = dataSource.getStationaryTime(bestAp) - (System.currentTimeMillis() - wifiManager.wifiCurrentAPStart)/1000;
					notifyPredictedMoveChange("Connected to the best AP (" + bestAp.getSSID() + ")");
					//announcePossibleHandover ("this", bestAp.getLastGatewayIp(), dataSource.getStationaryTime(bestAp), timeToMove, dataSource.getStationaryTime(bestAp));

				}
				else {
						Log.d(TAG, "Connecting to " + bestAp.getSSID());

						wifiManager.connectToAP(bestAp.getSSID());
					/*long timeToMove = dataSource.getStationaryTime(dataSource.getAP(bssid)) - (System.currentTimeMillis() - wifiManager.wifiCurrentAPStart)/1000;
					if (timeToMove >= 0) {
						notifyPredictedMoveChange("Handover to AP " + bestAp.getSSID() + " is expected to occur in about " + timeToMove + "s");
						announcePossibleHandover (bestAp.getBSSID(), bestAp.getLastGatewayIp(), dataSource.getStationaryTime(bestAp), timeToMove, dataSource.getStationaryTime(dataSource.getAP(bssid)));
					}
					else {
						notifyPredictedMoveChange("Handover to AP " + bestAp.getSSID() + " was expected to occur about " + (0-timeToMove) + "s ago");
						announcePossibleHandover (bestAp.getBSSID(), bestAp.getLastGatewayIp(), dataSource.getStationaryTime(bestAp), (0-timeToMove), dataSource.getStationaryTime(dataSource.getAP(bssid)));
					}*/
				}
			}
			else {
				notifyPredictedMoveChange("No AP in DB");
			}
		}

	}
}
