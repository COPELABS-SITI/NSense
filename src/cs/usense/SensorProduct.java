/**
 * @version 1.2
 * COPYRIGHTS COPELABS/ULHT, LGPLv3.0, date (e.g. 22-04-2016)
 * Class is part of the USense application. It provides support to USense service by providing the sensors to initiate for the each and every pipeline.
 * @author Saeik Firdose (COPELABS/ULHT)
 */

package cs.usense;

import java.io.Serializable;

/**
 * This class provides methods to get the sensor name, interval, and class of the sensor
 */
public class SensorProduct implements Serializable{
	
	/** Sensor name of the device */
	private String sensorname = "";
	
	/** Sensor interval of the device */
	private String interval = "";
	
	/** Class name of the device */
	private String classname = "";
	
	/**
	 * This method get the sensor name
	 * @return sensorname Sensor name from the device
	 */
	public String getSensorName() {
		return sensorname;
	}
	
	/**
	 * This method set the sensor name
	 * @param sensorname Sensor name from the device
	 */
	public void setSensorName(String name) {
		this.sensorname = name;
	}
	
	/**
	 * This method get the interval
	 * @return interval Interval time for the sampling
	 */
	public String getInterval() {
		return interval;
	}
	
	/**
	 * This method set the interval
	 * @param interval Interval time for the sampling
	 */
	public void setInterval(String interval) {
		this.interval = interval;
	}
	
	/**
	 * This method get the class name
	 * @return classname Class name of the sensor
	 */
	public String getClassname() {
		return classname;
	}
	
	/**
	 * This method set the class name
	 * @param classname Class name of the sensor
	 */
	public void setClassname(String classname) {
		this.classname = classname;
	}
	
	/**
	 * This method return a string containing the sensor name, interval, and class name.
	 * @return string with sensor name, interval, and class name.
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("sensorname: " + this.sensorname + "\n");
		sb.append("interval: " + this.interval + "\n");
		sb.append("classname: " + this.classname + "\n");
		
		String string = sb.toString();
		
		return string;
	}
	
	/**
	 * This method provides the constructor of SensorProduct class
	 */
	public SensorProduct() {
		super();
	}
	
}
