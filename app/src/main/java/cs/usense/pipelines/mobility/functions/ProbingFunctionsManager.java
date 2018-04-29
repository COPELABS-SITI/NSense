package cs.usense.pipelines.mobility.functions;

import android.util.Log;

import cs.usense.pipelines.mobility.models.MTrackerAP;
import cs.usense.pipelines.mobility.tasks.ConnectionTask;
import cs.usense.pipelines.mobility.tasks.DownloadTask;
import cs.usense.pipelines.mobility.tasks.PingNetworkTask;
import cs.usense.pipelines.mobility.tasks.TxtRecord;


/**
 * This function contains the functionalities to compute the fucntion with probing.
 * From here are computed the task to calculated network utilization and number of
 * active devices connected to the AP.
 * Created by copelabs on 10/10/2017.
 */

public class ProbingFunctionsManager implements
        DownloadTask.DonwnloadTaskInterface,
        PingNetworkTask.FindOnNetworkInterface, ConnectionTask.ConnectionInterface{

    /**
     * This interface is used to notify when a rank function has a result.
     */
    public interface RankInterface{
        /**
         * This funtion is used to notify when a rank function has a result.
         * @param rank Rank value.
         * @param function Funtion used to calcule the rank.
         * @param ap Access point information.
         */
        void rank(double rank, int function, MTrackerAP ap);
    }

    /**
     * Variable used for debug.
     */
    private static final String TAG = ProbingFunctionsManager.class.getSimpleName();

    /**
     * Variable  used to identify function 1.
     */
    private static final int FUNCTION_NUMBER_1 = 1;

    /**
     * Variable  used to identify function 2.
     */
    private static final int FUNCTION_NUMBER_2 = 2;

    /**
     * Variable  used to identify function 3.
     */
    private static final int FUNCTION_NUMBER_3 = 3;

    /**
     * Function to be used in the computation.
     */
    public final boolean COMPUTE_FUNCTION_1 = false;

    /**
     * Function to be used in the computation.
     */
    public final boolean COMPUTE_FUNCTION_2 = false;

    /**
     * Function to be used in the computation.
     */
    public final boolean COMPUTE_FUNCTION_3 = false;

    /**
     * TxtRecord used to receive recommendation from others devices.
     */
    private TxtRecord mTxtRedord;

    /**
     * Interface implemented and used to notify the results.
     */
    private RankInterface mInterface;

    /**
     * Ip of the access point connected.
     */
    private String mAccessPointIp;

    /**
     * MTracker Acces point actually connected.
     */
    private MTrackerAP mTrackerAP;

    /**
     * Value true if there is a computing task running.
     */
    private boolean isComputing;

    /**
     * ProbingFunctionManager constructor.
     * @param rankInterface Rank interface.
     * @param txtRecord Txt record object.
     */
    public ProbingFunctionsManager(RankInterface rankInterface, TxtRecord txtRecord){
        isComputing=false;
        mInterface=rankInterface;
        mTxtRedord = txtRecord;
    }

    /**
     * This function starts a new ranking coputaion of active paramiters.
     * @param accessPointIp Ip of the acces point actually connected.
     * @param ap MTracker access point connected.
     */
    public void startRankingCalulation(String accessPointIp, MTrackerAP ap){
        isComputing=true;
        mTrackerAP=ap;
        mAccessPointIp =accessPointIp;
        new ConnectionTask(this).execute("http://www.google.com");


    }

    @Override
    public void connection(int connection) {
        if (connection==1){
            Log.d(TAG,"Is Connected");
            new DownloadTask(this,0).execute("http://ovh.net/files/10Mb.dat");
            mTrackerAP.setConnection(connection);
        }else{
            Log.d(TAG,"Is No Connected");

            isComputing=false;
            if(COMPUTE_FUNCTION_1) {
                mTrackerAP.setConnection(connection);
                mInterface.rank(Functions.function1(mTrackerAP.getConnection(), mTrackerAP.getNetworkUtilization(), mTrackerAP.getDevicesOnNetwork(), mTrackerAP.getQuality()), FUNCTION_NUMBER_1, mTrackerAP);
            }
            if(COMPUTE_FUNCTION_2) {
                mTrackerAP.setNumRecommendations(mTxtRedord.getmBestAPShared());
                mTrackerAP.setRecommendation(mTxtRedord.getmBestAPShared());
                mInterface.rank(Functions.function2(mTrackerAP.getConnection(), mTrackerAP.getNetworkUtilization(), mTrackerAP.getNumRecommendations(), mTrackerAP.getQuality()), FUNCTION_NUMBER_2, mTrackerAP);
            }
            if(COMPUTE_FUNCTION_3) {
                mTrackerAP.setNumRecommendations(mTxtRedord.getmBestAPShared());
                mTrackerAP.setRecommendation(mTxtRedord.getSumRank3());
                mInterface.rank(Functions.function3(mTrackerAP.getConnection(), mTrackerAP.getNetworkUtilization(), mTrackerAP.getNumRecommendations(), mTrackerAP.getRecommendation(), mTrackerAP.getQuality()), FUNCTION_NUMBER_3, mTrackerAP);
            }

        }
    }
    @Override
    public void donwloadTime(float networkUtilization) {
        mTrackerAP.setNetworkUtilization(networkUtilization);
        new PingNetworkTask(this).execute(mAccessPointIp);
    }

    @Override
    public void networkFinder(int devices) {
        mTrackerAP.setDevicesOnNetwork(devices);


        if(COMPUTE_FUNCTION_1)
        mInterface.rank(Functions.function1(mTrackerAP.getConnection(),mTrackerAP.getNetworkUtilization(),mTrackerAP.getDevicesOnNetwork(),mTrackerAP.getQuality()),FUNCTION_NUMBER_1,mTrackerAP);

        if(COMPUTE_FUNCTION_2) {
            mTrackerAP.setNumRecommendations(mTxtRedord.getmBestAPShared());
            mTrackerAP.setRecommendation(mTxtRedord.getmBestAPShared());
            mInterface.rank(Functions.function2(mTrackerAP.getConnection(), mTrackerAP.getNetworkUtilization(), mTrackerAP.getNumRecommendations(), mTrackerAP.getQuality()), FUNCTION_NUMBER_2, mTrackerAP);
        }
        if(COMPUTE_FUNCTION_3) {
            mTrackerAP.setNumRecommendations(mTxtRedord.getmBestAPShared());
            mTrackerAP.setRecommendation(mTxtRedord.getSumRank3());
            mInterface.rank(Functions.function3(mTrackerAP.getConnection(), mTrackerAP.getNetworkUtilization(), mTrackerAP.getNumRecommendations(), mTrackerAP.getRecommendation(), mTrackerAP.getQuality()), FUNCTION_NUMBER_3, mTrackerAP);
        }
        isComputing=false;

    }

    public boolean isComputing(){
        return isComputing;
    }

    public void setIsComputing(boolean computing){
        isComputing=computing;
    }
}
