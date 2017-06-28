package cs.usense.pipelines.location;


public class NSenseDevice {

    /** MAC address received from the discover process (Wi-Fi Direct) */
    private String wifiDirectMACAddress;

    /** MAC address from the AP received from the wifi manager */
    private String wifiAPMACAddress;

    /** BT MAC address */
    private String btMACAddress;

    /** Access Point SSID */
    private String mSSID;

    /** Device Name */
    private String deviceName;

    /** Number of times this device was not found at the WiFi scans. */
    private int countNotFound;

    private String interests;


    public NSenseDevice(String deviceName, String mSSID, String wifiDirectMACAddress) {
        this.deviceName = deviceName;
        this.mSSID = mSSID;
        this.wifiDirectMACAddress = wifiDirectMACAddress;
    }

    public String getWifiDirectMACAddress() {
        return wifiDirectMACAddress;
    }

    public String getWifiAPMACAddress() {
        return wifiAPMACAddress;
    }

    public String getBtMACAddress() {
        return btMACAddress;
    }

    public String getSSID() {
        return mSSID;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public int getCountNotFound() {
        return countNotFound;
    }

    /** Representation of interests chosen by the user */
    public String getInterests() {
        return interests;
    }

    public void incrementCountNotFound() {
        countNotFound++;
    }

    public void setSSID(String mSSID) {
        this.mSSID = mSSID;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public void setCountNotFound(int countNotFound) {
        this.countNotFound = countNotFound;
    }

    public void setWifiAPMACAddress(String wifiAPMACAddress) {
        this.wifiAPMACAddress = wifiAPMACAddress;
    }

    public void setBtMACAddress(String btMACAddress) {
        this.btMACAddress = btMACAddress;
    }

    public void setInterests(String interests) {
        this.interests = interests;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(deviceName).append(" ");
        sb.append(mSSID).append(" ");
        sb.append("MAC BT ").append(btMACAddress).append(" ");
        sb.append("MAC WD ").append(wifiDirectMACAddress).append(" ");
        sb.append("MAC AP ").append(wifiAPMACAddress).append(" ");
        sb.append("Interests ").append(interests);
        return sb.toString();
    }

}