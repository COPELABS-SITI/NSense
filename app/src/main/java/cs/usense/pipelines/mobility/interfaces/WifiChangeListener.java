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
 * It computes a rank and then estimates a potential handover - time and target AP
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
 *
 */
package cs.usense.pipelines.mobility.interfaces;

import android.net.wifi.ScanResult;

import java.util.List;

public interface WifiChangeListener {
	void onWifiStateDisabled (boolean valid, String bssid, String ssid, long visitId, long connectionStart, long connectionEnd);
	void onWifiStateEnabled();
	void onWifiConnectionDown (boolean valid, String bssid, String ssid, long visitId, long connectionStart, long connectionEnd);
	long onWifiConnectionUp (String bssid, String ssid, List<ScanResult> lastScanResults);
	void onWifiAvailableNetworksChange (String bssid, List<ScanResult> results);
	void onWifiAvailableList (List<ScanResult> results);
	void onConnectionRejected(String bssid, String ssid);
}
