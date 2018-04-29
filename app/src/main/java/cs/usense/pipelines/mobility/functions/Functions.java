package cs.usense.pipelines.mobility.functions;

import android.util.Log;

import java.text.DecimalFormat;
import java.util.Map;

/**
 * This class provides a set o fucntion that can be used to calculate the ranking value of an access
 * point.
 *
 * @author Omar Aponte (ULHT)
 * @version 3.0
 *          <p>
 *          Created by copelabs on 16/10/2017.
 */

public abstract class Functions {

    private final static String TAG = Functions.class.getSimpleName();

    public static double function01(long visits, int rejections, float timeLastConnectionAvrg, float timeAdvg, float attractiveness) {
        double result = Math.pow(attractiveness, 2) * Math.sqrt(timeAdvg) * Math.pow(timeAdvg / (timeLastConnectionAvrg + timeAdvg), (double) visits / (double) (rejections + 1));
        DecimalFormat df = new DecimalFormat("#.####");
        Log.d(TAG, "Function_0: " + visits + " " + rejections + " " + timeLastConnectionAvrg + " " + timeAdvg + " " + attractiveness + " result: " + result);
        return result;
    }

    /**
     * Function 0. Function without probing.
     *
     * @param visits                 Number of visits.
     * @param rejections             Number of rejections.
     * @param timeLastConnectionAvrg Gap time connection (EMA).
     * @param timeAdvg               Time connection (EMA).
     * @param attractiveness         Attractiveness of the AP.
     * @return Function result.
     */
    public static double function0(long visits, int rejections, float timeLastConnectionAvrg, float timeAdvg, float attractiveness) {
        double result = Math.pow(attractiveness, 2) * Math.sqrt(timeAdvg) * Math.pow(timeAdvg / (timeLastConnectionAvrg + timeAdvg), (double) rejections / (double) visits);
        DecimalFormat df = new DecimalFormat("#.####");
        Log.d(TAG, "Function_0: " + visits + " " + rejections + " " + timeLastConnectionAvrg + " " + timeAdvg + " " + attractiveness + " result: " + result);
        return result;
    }

    /**
     * Function 1 with Probing.
     *
     * @param connectivity       Internet connection status.
     * @param networkUtilization Network utilization.
     * @param numDevices         Number of devices active (with Ip) connected to the AP.
     * @param quality            Quality of the signal.
     * @return Result of function 1.
     */
    public static double function1(int connectivity, float networkUtilization, long numDevices, int quality) {
        int factor = 4;
        double result = (connectivity * quality * networkUtilization) / ((1 + numDevices) * factor);
        //DecimalFormat df = new DecimalFormat("#.####");
        Log.d(TAG, "Function_1: " + connectivity + " " + networkUtilization + " " + numDevices + " " + quality + " result: " + result);
        return result;
    }

    /**
     * Function 2 with probing and recommendations.
     *
     * @param connectivity       Internet connection status.
     * @param networkUtilization Network utilization.
     * @param numRecommendations Number of recommendation received from other devices.
     * @param quality            Quality of the signal.
     * @return Result of function 2.
     */
    public static double function2(int connectivity, float networkUtilization, int numRecommendations, int quality) {
        int factor = 4;
        double result = (connectivity * networkUtilization * quality * Math.log10(numRecommendations + 2)) / ((1 + numRecommendations) * factor);
        DecimalFormat df = new DecimalFormat("#.####");
        Log.d(TAG, "Function_2: " + connectivity + " " + networkUtilization + " " + numRecommendations + " " + quality + " result: " + result);
        return result;
    }

    /**
     * Function 3 with probing and recommendation.
     *
     * @param connectivity       Internet connection status.
     * @param networkUtilization Network utilization.
     * @param numRecommendations Numer of recommendations received from others devices.
     * @param recommendations    Sum of the recommendation values received from others devices.
     * @param quality            Quality of the signal.
     * @return Result of the function 3.
     */
    public static double function3(int connectivity, float networkUtilization, int numRecommendations, double recommendations, int quality) {
        int factor = 4;
        double result = (connectivity * networkUtilization * quality * Math.log10(recommendations + 2)) / ((1 + numRecommendations) * factor);
        DecimalFormat df = new DecimalFormat("#.####");
        Log.d(TAG, "Function_3: " + connectivity + " " + networkUtilization + " " + numRecommendations + " " + recommendations + " " + quality + " " + " result: " + result);
        return result;
    }

    /**
     * Function 4 without probing and with recommendations.
     *
     * @param visits                 Number of visits.
     * @param rejections             Number of rejections.
     * @param timeLastConnectionAvrg Gap average connection (EMA).
     * @param timeAdvg               Time connection (EMA).
     * @param attractiveness         Attractiveness of the AP.
     * @param recommendations        Recommendation receive from others devices. Result of Centrality function.
     * @return Result of function 4.
     */
    public static double function4(long visits, int rejections, float timeLastConnectionAvrg, float timeAdvg, float attractiveness, double recommendations) {
        double result = recommendations * Math.pow(attractiveness, 2) * Math.sqrt(timeAdvg) * Math.pow(timeAdvg / (timeLastConnectionAvrg + timeAdvg), (double) rejections / (double) visits);
        DecimalFormat df = new DecimalFormat("#.####");
        Log.d(TAG, "Function_4: " + visits + " " + rejections + " " + timeLastConnectionAvrg + " " + timeAdvg + " " + attractiveness + " " + recommendations + " result: " + result);
        return result;
    }

    /**
     * Function used to calculate the EMA function using the time connected to a specific access
     * point.
     *
     * @param timeAvrg   Time average previously calculated.
     * @param actualTime Actual time connection.
     * @param time       Actual time.
     * @return Result of EMA function.
     */
    public static float functionGammaTimeConnection(float timeAvrg, long actualTime, long time) {
        float result;
        float gamma = (float) 0.5;
        float partialTime = (time - actualTime) / 1000;
        result = ((timeAvrg * gamma) + ((1 - gamma) * partialTime));
        //Log.d(TAG, "Gamma Time Conection: " + result);
        return result;
    }

    /**
     * Function used to calculate the EMA function of the disconnected time.(Gap time between connections).
     *
     * @param timeAvrg   Time average previously calculated.
     * @param actualTime Actual time connected.
     * @return Result of EMA function.
     */
    public static float functionGammaTimeDisconnection(float timeAvrg, long actualTime) {
        float result;
        float gamma = (float) 0.5;
        result = (long) ((timeAvrg * gamma) + ((1 - gamma) * actualTime));
        //Log.d(TAG, "Gamma Time Disconnection: " + result);
        return result;
    }

    /**
     * Function used to calculate the EMA value of the rank.
     *
     * @param rankAvg Rank average previously calculated.
     * @param rank    Actual rank value
     * @return Result of EMA function.
     */
    public static double functionGammaRank(double rankAvg, double rank) {
        double result;
        float gamma = (float) 0.5;
        //Log.d(TAG, "Gamma ProbingFunctionsManager EMA values : " + rankAvg + "  " + probingFunctionsManager);
        result = ((rankAvg * gamma) + ((1 - gamma) * rank));
        //Log.d(TAG, "Gamma ProbingFunctionsManager EMA: " + result);
        //DecimalFormat df = new DecimalFormat("#.####");
        return result;
    }

    /**
     * This fucntion is used to sum the rank values received from others devices.
     *
     * @param mapRank Map whit the values received.
     * @return Sum of every value.
     */
    public static double sumRank3(Map<String, Double> mapRank) {
        double sum = 0;
        for (Map.Entry<String, Double> entry : mapRank.entrySet()) {
            sum = sum + entry.getValue();
        }
        return sum;
    }

    /**
     * This function return the number of recommendation received, this values is used in function 2.
     *
     * @param table Map with avery recommendation received.
     * @param value Value of the SSID of the acces point actually active.
     * @return Sum of number of entry in the map.
     */
    public static int countOccurences(Map<String, String> table, String value) {
        int count = 0;
        for (String key : table.keySet()) {
            if (table.get(key).equals(value)) {
                count++;
            }
        }
        return count;
    }

    /**
     * This function calculates the sum of all ranking values received fron other device related to
     * function 4.
     *
     * @param mapRank Map with every rank received from others devices.
     * @param rankIJ  Actual value rank of the access point actual connected.
     * @return retur the sum of the rank values. Using centrality function.
     */

    public static double sumRank4(Map<String, Double> mapRank, double rankIJ) {
        double sum = 0;
        double result;
        for (Map.Entry<String, Double> entry : mapRank.entrySet()) {

            sum = sum + Math.abs(entry.getValue() - rankIJ);
            //Log.d(TAG, "partial value: " + entry.getValue());
            //Log.d(TAG, "partial sum: " + "Rkj = " + entry.getValue() + " Rij = " +rankIJ  );
        }
        //Log.d(TAG, "partial sum: " + sum);
        result = 1 + (sum / (1 + Math.abs((rankIJ - 1) * (rankIJ - 2))));
        //Log.d(TAG, "Result: " + result);

        return result;
    }


}
