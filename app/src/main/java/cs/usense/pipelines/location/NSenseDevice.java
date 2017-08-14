/*
 * COPYRIGHTS COPELABS/ULHT, LGPLv3.0, 2015/11/16.
 * Class is part of the NSense application. It provides support for location pipeline.
 */

package cs.usense.pipelines.location;


/**
 * This class is a model to describe a NSense device on application
 * @author Luis Amaral Lopes (COPELABS/ULHT),
 * @author Miguel Tavares (COPELABS/ULHT)
 * @version 2.0, 2016
 */
public class NSenseDevice {

    /** Device Name */
    private String mDeviceName;

    /** Access Point SSID */
    private String mSsid;

    /** MAC address received from the discover process (Wi-Fi Direct) */
    private String mWifiDirectMac;

    /** MAC address from the AP received from the wifi manager */
    private String mWifiApMac;

    /** BT MAC address */
    private String mBtMac;

    /** User's interests */
    private String mInterests;

    /**
     * NSenseDevice class constructor
     * @param deviceName the device name
     * @param ssid ssid of WI-FI Direct AP
     * @param wifiDirectMac mac of WI-FI Direct AP
     */
    public NSenseDevice(String deviceName, String ssid, String wifiDirectMac) {
        mDeviceName = deviceName;
        mSsid = ssid;
        mWifiDirectMac = wifiDirectMac;
    }

    /**
     * This method returns the device name
     * @return device name
     */
    public String getDeviceName() {
        return mDeviceName;
    }

    /**
     * This method returns the ssid
     * @return ssid
     */
    public String getSsid() {
        return mSsid;
    }

    /**
     * This method returns MAC of WI-FI Direct
     * @return MAC of WI-FI Direct
     */
    public String getWifiDirectMac() {
        return mWifiDirectMac;
    }

    /**
     * This method returns MAC of WI-FI Direct AP
     * @return MAC of WI-FI Direct AP
     */
    public String getWifiApMac() {
        return mWifiApMac;
    }

    /**
     * This method returns MAC of BT
     * @return MAC of BT
     */
    public String getBtMac() {
        return mBtMac;
    }

    /**
     * This method returns user's interests
     * @return user's interests
     */
    public String getInterests() {
        return mInterests;
    }

    /**
     * This method sets the mDeviceName attribute
     * @param deviceName value to be set
     */
    public void setDeviceName(String deviceName) {
        mDeviceName = deviceName;
    }

    /**
     * This method sets the mSsid attribute
     * @param ssid value to be set
     */
    public void setSsid(String ssid) {
        mSsid = ssid;
    }

    /**
     * This method sets the mWifiApMac attribute
     * @param wifiApMac value to be set
     */
    public void setWifiApMac(String wifiApMac) {
        mWifiApMac = wifiApMac;
    }

    /**
     * This method sets the mBtMac attribute
     * @param btMac value to be set
     */
    public void setBtMac(String btMac) {
        mBtMac = btMac;
    }

    /**
     * This method sets the mInterests attribute
     * @param interests value to be set
     */
    public void setInterests(String interests) {
        mInterests = interests;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(mDeviceName).append(" ");
        sb.append(mSsid).append(" ");
        sb.append("MAC BT ").append(mBtMac).append(" ");
        sb.append("MAC WD ").append(mWifiDirectMac).append(" ");
        sb.append("MAC AP ").append(mWifiApMac).append(" ");
        sb.append("Interests ").append(mInterests);
        return sb.toString();
    }

}