/**
 * @version 2.0
 * COPYRIGHTS COPELABS/ULHT, LGPLv3.0, 06-04-2016
 * Class is part of the NSense application. 
 * It provides the interface between BTManager and BluetoothCore.
 * @author Waldir Moreira (COPELABS/ULHT)
 */

package cs.usense.pipelines.proximity;

import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;

/**
 * This class provides the interface between BTManager and BluetoothCore.
 * @author Waldir Moreira (COPELABS/ULHT)
 *
 */
public interface BTDeviceFinder {
	
	/**
	 * This method called when device found from BluetoothDevice and BluetoothClass  
	 * @param device Bluetooth Device
	 * @param btClass Bluetooth Class
	 */
	void onDeviceFound(BluetoothDevice device, BluetoothClass btClass);
}