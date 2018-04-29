/**
 * Copyright (C) 2013 ULHT
 * Author(s): jonnahtan.saltarin@ulusofona.pt
 * <p>
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by  the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * <p>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * <p>
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/>.
 * <p>
 * ULOOP Mobility tracking plugin: Mtracker
 * <p>
 * Mtracker is an Android app that collects information concerning visited APs
 * It computes a mRank and then estimates a potential handover - time and target AP
 * v1.0 - pre-prototype, D3.3, July 2012
 * v2.0 - prototype on September 2012 - D3.6
 * v3.0 - prototype on June 2013
 *
 * @author Jonnahtan Saltarin
 * @author Rute Sofia
 * @author Christian da Silva Pereira
 * @author Luis Amaral Lopes
 * @author Omar Aponte
 * @version 3.0
 * @file Contains MTrackerAP class. This class represents what MTracker considers an AP.
 * The information kept in this object are mSsid, mBssid, mAttractiveness, and last IP.
 */

package cs.usense.pipelines.mobility.models;

import java.math.BigInteger;

/**
 *  This class represents what MTracker considers an AP.
 *  The information kept in this object are mSsid, mBssid, mAttractiveness, and last IP.
 *
 * @author Jonnahtan Saltarin (ULHT)
 * @author Rute Sofia (ULHT)
 * @author Christian da Silva Pereira (ULHT)
 * @author Luis Amaral Lopes (ULHT)
 * @author Omar Aponte (ULHT)
 *
 * @version 3.0
 */
public class MTrackerAP {

    /**
     * SSID of the AP.
     */
    private String mSsid;
    /**
     * BSSID of the AP.
     */
    private String mBssid;
    /**
     * Attractiveness of this AP.
     */
    private double mAttractiveness;
    /**
     * Last Gateway's Ip connected to.
     */
    private String mLastGatewayIp;
    /**
     * Network utilization of this AP.
     */
    private float mNetworkUtilization;

    /**
     * Devices active connected to this AP.
     */
    private int mDevicesConnectedToNetwork;
    /**
     * Rejections occur to this AP
     */
    private int mRejections;
    /**
     * Recommendation received to this AP.
     */
    private int mConnection;
    /**
     * Signal quality of this AP.
     */
    private int mQuality;
    /**
     * Recommendation received to this AP.
     */
    private double mRecommendation;
    /**
     * Number of recommendations receive to this AP.
     */
    private int mNumRecommendations;
    /**
     * ProbingFunctionsManager value to this AP.
     */
    private double mRank;
    /**
     * This variable save the sataed of tis AP i terms of if ir was rejected or not.
     */
    private int mRejected;

    /**
     * MTracker AP Constructor
     */
    public MTrackerAP() {
        super();
    }

    /**
     * Get information of if the AP was mRejected.
     * @return Value 1 if was mRejected.
     */
    public int getRejected() {
        return mRejected;
    }

    /**
     * Set if this AP was mRejected.
     * @param rejected Value 1 if AP was mRejected.
     */
    public void setRejected(int rejected) {
        mRejected = rejected;
    }

    /**
     * Get the value mRank of this AP.
     * @return value mRank.
     */
    public double getRank() {
        return mRank;
    }

    /**
     * Set the value of the mRank of this AP.
     * @param rank ProbingFunctionsManager value.
     */
    public void setRank(double rank) {
        mRank = rank;
    }

    /**
     * Get the number of mRejections of this AP.
     * @return Number of rejection.
     */
    public int getRejections() {
        return mRejections;
    }

    /**
     * Set the number of rejection of this AP.
     * @param rejections Number of mRejections.
     */
    public void setRejections(int rejections) {
        mRejections = rejections;
    }

    /**
     * Get the Network Utilization of this AP.
     * @return Network utilization value.
     */
    public float getNetworkUtilization() {
        return mNetworkUtilization;
    }

    /**
     * Set the Network Utilization of this AP.
     * @param networkUtilization Network utilization value.
     */
    public void setNetworkUtilization(float networkUtilization) {
        mNetworkUtilization = networkUtilization;
    }

    /**
     * Get devices connected to this AP.
     * @return Number of devices connected.
     */
    public int getDevicesOnNetwork() {
        return mDevicesConnectedToNetwork;
    }

    /**
     * Set devices connected to this AP.
     * @param devices Numbr of devices connected to this AP.
     */
    public void setDevicesOnNetwork(int devices) {
        mDevicesConnectedToNetwork = devices;
    }

    /**
     * Get mConnection condition of this AP
     * @return Value 1 if there is internet mConnection.
     */
    public int getConnection() {
        return mConnection;
    }

    /**
     * Set mConnection condition of this AP.
     * @param connect Value 1 if there is internet mConnection.
     */
    public void setConnection(int connect) {
        mConnection = connect;
    }

    /**
     * Get signal mQuality of thif AP.
     * @return Quality of this AP.
     */
    public int getQuality() {
        return mQuality;
    }

    /**
     * Set Signal mQuality of this AP.
     * @param quality Quality value of this AP.
     */
    public void setQuality(int quality) {
        this.mQuality = quality;
    }

    /**
     * Get mRecommendation value of this AP.
     * @return Recommendation value.
     */
    public double getRecommendation() {
        return mRecommendation;
    }

    /**
     * Set mRecommendation to this AP.
     * @param recommendation recommendations value.
     */
    public void setRecommendation(double recommendation) {
        this.mRecommendation = recommendation;
    }

    /**
     * Get number of recommendations of this AP.
     * @return Number of recommendations.
     */
    public int getNumRecommendations() {
        return mNumRecommendations;
    }

    /**
     * Set  number of recommendations of this AP.
     * @param numRecommendations number of mRecommendation.
     */
    public void setNumRecommendations(int numRecommendations) {
        this.mNumRecommendations = numRecommendations;
    }

    /**
     * Get the mSsid of this AP
     * @return the sSID
     */
    public String getSSID() {
        return mSsid;
    }

    /**
     * Set the mSsid of this AP
     * @param sSID the sSID to set
     */
    public void setSSID(String sSID) {
        mSsid = sSID;
    }

    /**
     * Get the mBssid of this AP
     * @return the bSSID
     */
    public String getBSSID() {
        return mBssid;
    }

    /**
     * Set the mBssid of this AP
     * @param bSSID the bSSID to set
     */
    public void setBSSID(String bSSID) {
        mBssid = bSSID;
    }

    /**
     * Get the mAttractiveness of this AP
     * @return the mAttractiveness
     */
    public double getAttractiveness() {
        return mAttractiveness;
    }

    /**
     * Set the mAttractiveness of this AP
     * @param attractiveness the mAttractiveness to set
     */
    public void setAttractiveness(double attractiveness) {
        mAttractiveness = attractiveness;
    }

    /**
     * Get the last IP shown by this AP
     * @return the mLastGatewayIp
     */
    public String getLastGatewayIp() {
        return mLastGatewayIp;
    }

    /**
     * Set the last IP shown by this AP
     * @param lastGatewayIp String representing the last gateway IP to set
     */
    public void setLastGatewayIp(String lastGatewayIp) {
        mLastGatewayIp = lastGatewayIp;
    }

    /**
     *  Set the last IP shown by this AP
     * @param lastGatewayIp Integer representing the last gateway IP to set
     */
    public void setLastGatewayIp(int lastGatewayIp) {
        byte[] bytes = BigInteger.valueOf(lastGatewayIp).toByteArray();
        if (bytes.length == 4) {
            this.mLastGatewayIp = (bytes[3] & 0xFF) + "." + (bytes[2] & 0xFF) + "." + (bytes[1] & 0xFF) + "." + (bytes[0] & 0xFF);
            System.out.println(this.mLastGatewayIp);
        }
    }

    /**
     * Set some parameters to default
     */
    public void setToDefault(double uloopDispositionalTrust) {
        mAttractiveness = uloopDispositionalTrust;
        mLastGatewayIp = "";
    }

    /**
     * Return a string containing the mSsid, mBssid and the Attractiveness.
     */
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("SSID: " + mSsid + "\n");
        //sb.append("mBssid: " + this.mBssid + "\n");
        sb.append("Attractiveness: " + mAttractiveness + "\n");
        sb.append("Ranking: " + mRank + "\n");
        sb.append("Rejected: " + mRejected);
        //sb.append("Last Gateway IP: " + this.mLastGatewayIp + "\n");

        return sb.toString();
    }
}