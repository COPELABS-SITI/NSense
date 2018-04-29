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
 * @file Contains MTrackerVisit class. This class represents what MTracker considers a Visit.
 *       The information kept in this object are SSID, BSSID, start and end time of the connection,
 *       the day of the week, and the hour of the day.
 *
 */

package cs.usense.pipelines.mobility.models;
import java.text.SimpleDateFormat;

/**
 * This class represents what MTracker considers a Visit.
 * The information kept in this object are SSID, BSSID, start and end time of the connection,
 * the day of the week, and the hour of the day.
 *
 * @author Jonnahtan Saltarin (ULHT)
 * @author Rute Sofia (ULHT)
 * @author Christian da Silva Pereira (ULHT)
 * @author Luis Amaral Lopes (ULHT)
 * @author Omar Aponte (ULHT)
 * @version 3.0
 *
 */
public class MTrackerVisit {
	
	private String SSID;
	private String BSSID;
	private Long startTime;
	private Long endTime;
	private int dayOfTheWeek;
	private int hourOfTheDay;
	private SimpleDateFormat dataFormat = new SimpleDateFormat("yyyy.MM.dd 'at' HH:mm:ss");
	private SimpleDateFormat periodFormat = new SimpleDateFormat("HH:mm:ss");
	private SimpleDateFormat dataFormatDay = new SimpleDateFormat("yyyy.MM.dd");
	/**
	 * @return the sSID
	 */
	public String getSSID() {
		return SSID;
	}

	/**
	 * @return the bSSID
	 */
	public String getBSSID() {
		return BSSID;
	}

	/**
	 * @return the startTime
	 */
	public long getStartTime() {
		return startTime;
	}

	/**
	 * @return the endTime
	 */
	public long getEndTime() {
		return endTime;
	}

	/**
	 * @return the dayOfTheWeek
	 */
	public int getDayOfTheWeek() {
		return dayOfTheWeek;
	}

	/**
	 * @return the hourOfTheDay
	 */
	public int getHourOfTheDay() {
		return hourOfTheDay;
	}

	/**
	 * @param sSID the sSID to set
	 */
	public void setSSID(String sSID) {
		SSID = sSID;
	}
	
	/**
	 * @param bSSID the bSSID to set
	 */
	public void setBSSID(String bSSID) {
		BSSID = bSSID;
	}

	/**
	 * @param startTime the startTime to set
	 */
	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}

	/**
	 * @param endTime the endTime to set
	 */
	public void setEndTime(long endTime) {
		this.endTime = endTime;
	}

	/**
	 * @param dayOfTheWeek the dayOfTheWeek to set
	 */
	public void setDayOfTheWeek(int dayOfTheWeek) {
		this.dayOfTheWeek = dayOfTheWeek;
	}

	/**
	 * @param hourOfTheDay the hourOfTheDay to set
	 */
	public void setHourOfTheDay(int hourOfTheDay) {
		this.hourOfTheDay = hourOfTheDay;
	}
	
	public MTrackerVisit() {
		super();
	}
	
	public void setToDefault() {
		this.startTime = null;
		this.endTime = null;
	}
	
	public void update(String SSID, String BSSID, Long startTime, Long endTime) {
		this.SSID = SSID;
		this.BSSID = BSSID;
		this.startTime = startTime;
		this.endTime = endTime;
		
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		//sb.append("Day: " + this.dayOfTheWeek + "\n");
		sb.append("Start: " + dataFormat.format(this.startTime) + "\n");
		sb.append("End: " + dataFormat.format(this.endTime) + "\n");
		sb.append("Total: " + periodFormat.format(this.endTime - this.startTime) + "\n");
		return sb.toString();
	}

	public String toStringTabFormat() {
		StringBuilder sb = new StringBuilder();
		sb.append(dataFormatDay.format(this.startTime) + ";");
		sb.append(periodFormat.format(this.startTime) + ";");
		sb.append(dataFormatDay.format(this.endTime) + ";");
		sb.append(periodFormat.format(this.endTime) + ";");
		sb.append(periodFormat.format(this.endTime - this.startTime) + ";");
		sb.append(this.SSID+"\n");
		return sb.toString();
	}
} // ends class APEntry