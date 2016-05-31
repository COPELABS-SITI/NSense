/**
 * @version 1.2
 * COPYRIGHTS COPELABS/ULHT, LGPLv3.0, date (e.g. 22-04-2016)
 * Class is part of the USense application. It provides support for Microphone pipeline.
 * This class is contains the core functionalities of
 * the application relating to Microphone. The SoundManager provides all the information from 
 * Microphone adapter so this class can perform the analysis over Environmental Sound prior to
 * storing the required information in the database.
 * Some of the code is adapted from Google's NoiseAlert application code.
 * @author Reddy Pallavali (COPELABS/ULHT)
 */
package cs.usense.microphone;


import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import android.content.Context;
import android.os.Environment;
import android.os.Handler;
import android.text.format.DateFormat;
import cs.usense.UsenseService;
import cs.usense.db.DataBaseChangeListener;
import cs.usense.db.UsenseDataSource;

/**
 * This class is contains the core functionalities of the application relating to Microphone. 
 */
public class MicrophonePipeline implements SoundListener{

	/** This class is to access functionality of Sensor Manager from accelerometer pipeline */
	private SoundManager soundManager;
	
	/** This class is to access functionality of Usense Data base */
	UsenseDataSource dataSource;
	
	/** This class is to access Sound Level */
	private static SoundLevel mSoundLevel = new SoundLevel();
	
	/** This variable is used to access the listeners */
	private ArrayList<DataBaseChangeListener> listeners = new ArrayList<DataBaseChangeListener> ();
	
	/** This class is to use send or process message */
	Handler soundHandler = new Handler();
	
	/** This variable is used to access the average decibels */
	protected double avgdB;
	
	/** This variable is used to check whether microphone is available or not */
	private boolean micPresent=true;
	
	/** This variable is used to access the sound level */
	private SoundLevel soundlevel;
	
	/** This variable is used to access the sound in decibels */
	protected double soundDB;
	
	/** This class is to use send or process message */
	private Handler mHandler = new Handler();
	
	/** This class is to access the functionality of Usense Service */
	private UsenseService scallback = null;
	
	/** This variable is used to access the environment sound  */
	private String envSound = "";
	
	/** This variable is used to access db in decibels */
	double db;
	
	/** This variable is used to access the number of samples */
	int numberOfsamples;
	
	/** This variable is used to access Environment sound */
	public static String mEnvironmentalSound = "";

	/**
	 * This method constructs the MicrophonePipeline
	 * @param callback Supply UsenseService object for UsenseActivity to use
	 * @param dataSource UsenseDataSource to access various methods and information of the USense Data base
	 */
	public MicrophonePipeline(UsenseService callback, UsenseDataSource dataSource) {
		callback.getApplicationContext();
		this.dataSource = dataSource;
		this.scallback = callback;
		soundManager = new SoundManager(callback);
		soundManager.setSoundListener(this);
		start();

		Runnable repeatTask = new Runnable() {

			@Override
			public void run() {
				soundDB =soundManager.soundDb();
				updateSoundLevel(soundDB);
				for (numberOfsamples=0; numberOfsamples <=10; ++numberOfsamples) {
					avgdB += soundManager.soundDb();
				}
				db=avgdB/numberOfsamples;
				mHandler.postDelayed(this, 3000);
			}
		};
		mHandler.postDelayed(repeatTask, 3000);

	}

	/**
	 * This method close the context
	 * @param contect Interface to global information about an application environment.
	 */
	public void close(Context contect) {
		soundManager.stop();
	}

	/**
	 * This method provides four different levels of sound and their duration Based on the captured sound decibels
	 * @param soundDB soundLevel in decibels 
	 * @return Quite, Normal, Alert, Noisy and Time duration in each state
	 */
	public void updateSoundLevel(double soundDB) {
		avgdB+=soundDB;	       
		long startTime = System.currentTimeMillis();
		long endTime   = 0l;
		long quietSoundDuration=0l;
		long normalSoundDuration=0l;
		long alertSoundDuration=01;
		long noisySoundDuration=0l;

		if (db > 50) {
			if(db>89){
				endTime=System.currentTimeMillis();
				noisySoundDuration=endTime-startTime;
				mSoundLevel.setNoiseTime(noisySoundDuration);
				mSoundLevel.setQuietTime(quietSoundDuration);
				mSoundLevel.setNormalTime(normalSoundDuration);
				mSoundLevel.setAlertTime(alertSoundDuration);
				envSound = "NOISY";
			}else if(db<89) {
				endTime=System.currentTimeMillis();
				alertSoundDuration=endTime-startTime;
				mSoundLevel.setAlertTime(alertSoundDuration);
				mSoundLevel.setNormalTime(normalSoundDuration);
				mSoundLevel.setQuietTime(quietSoundDuration);
				mSoundLevel.setNoiseTime(noisySoundDuration);
				envSound = "ALERT";
			} 
		}else{
			if (db < 20){
				endTime   = System.currentTimeMillis();
				quietSoundDuration=endTime-startTime;
				mSoundLevel.setQuietTime(quietSoundDuration);
				mSoundLevel.setNormalTime(normalSoundDuration);
				mSoundLevel.setAlertTime(alertSoundDuration);
				mSoundLevel.setNoiseTime(noisySoundDuration);
				envSound = "QUIET"; 
			} else if(db <49){
				endTime=System.currentTimeMillis();
				normalSoundDuration=endTime-startTime;
				mSoundLevel.setNormalTime(normalSoundDuration);
				mSoundLevel.setQuietTime(quietSoundDuration);
				mSoundLevel.setAlertTime(alertSoundDuration);
				mSoundLevel.setNoiseTime(noisySoundDuration);
				envSound = "NORMAL";
			} 
		}
		mEnvironmentalSound = envSound;  

		Calendar c = Calendar.getInstance();
		/** set object Date */
		c.setTime(new Date()); 
		int day = c.get(Calendar.DATE);
		mSoundLevel.setSoundDate(day);
		if(dataSource.hasSoundLevel(mSoundLevel.SoundDate) ){
			SoundLevel sound= dataSource.getSoundLevel(mSoundLevel.getSoundDate());
			sound.setQuietTime(quietSoundDuration);
			sound.setNormalTime(normalSoundDuration);
			sound.setAlertTime(alertSoundDuration);
			sound.setNoiseTime(noisySoundDuration);
			dataSource.updateSoundLevel(sound);
			notifyDataBaseChange();
		}else {
			SoundLevel sound = new SoundLevel();
			sound.setSoundDate(day);
			sound.setQuietTime(quietSoundDuration);
			sound.setNormalTime(normalSoundDuration);
			sound.setAlertTime(alertSoundDuration);
			sound.setNoiseTime(noisySoundDuration);
			dataSource.registerNewSoundLevel(mSoundLevel);
		}

		avgdB=0;

		String currentTime = (String) DateFormat.format("dd/MM - HH:mm:ss.sss", Calendar.getInstance().getTime());

		Soundstorage(currentTime, envSound);
		scallback.notifySoundLevel(envSound);
	}

	/**
	 * This method calculates the duration
	 * @param milliseconds Time in milliseconds
	 * @return the time duration in hours, minutes and seconds
	 */
	public String getDuration(long milliseconds) {
		int seconds = (int) (milliseconds / 1000) % 60;
		int minutes = (int) ((milliseconds / (1000 * 60)) % 60);
		int hours = (int) ((milliseconds / (1000 * 60 * 60)) % 24);
		return String.format("%d:%d:%d", hours, minutes, seconds);
	}
	/**
	 * This method provides current date, hours,  minutes, seconds and milliseconds of the system
	 * @return current date and time of the system
	 */
	private long getTodayMillis(){
		Calendar prefcal = Calendar.getInstance();
		prefcal.setTimeInMillis(System.currentTimeMillis());
		prefcal.set(Calendar.HOUR_OF_DAY, 0);
		prefcal.set(Calendar.MINUTE, 0);
		prefcal.set(Calendar.SECOND, 0);
		prefcal.set(Calendar.MILLISECOND, 0);

		return prefcal.getTimeInMillis();
	}
	/**
	 * This method initialize the sound manager class which Start Capturing sound waves from microphone adapter.
	 */
	private void start() {
		if (micPresent) {
			soundManager.start();
			long todayMillis=getTodayMillis();
			if(soundlevel==null){
				soundlevel=dataSource.getSoundLevel(todayMillis);
			}

		}
	}


	/**
	 * This method provides the sound level data from the database
	 * @return soundlevel Array list of SoundLevel objects from the database
	 */
	protected List<SoundLevel> getData () {
		if (dataSource != null)
			return new ArrayList<SoundLevel>(dataSource.getAllSoundLevel().values());
		else
			return null;
	}
	/**
	 * This method notifies a database change to the listeners.
	 */
	private void notifyDataBaseChange () {
		for (DataBaseChangeListener listener : this.listeners) 
		{
			listener.onSoundLevelChange(new ArrayList<SoundLevel>(dataSource.getAllSoundLevel().values()));
		}
	}
	/**
	 * This method writes a file for Microphone pipeline
	 */
	private void Soundstorage(String envSound, String date ){
		File logFile = new File(Environment.getExternalStorageDirectory()+File.separator+"Experiment","Microphone.txt");
		if (!logFile.exists())
		{
			try
			{
				logFile.createNewFile();
			} 
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
		try
		{
			String oneChar = ";";
			/** BufferedWriter for performance, true to set append to file flag */
			BufferedWriter buf = new BufferedWriter(new FileWriter(logFile, true)); 
			buf.write(envSound);
			buf.write(oneChar);
			buf.write(date);
			buf.write(oneChar);
			buf.newLine();
			buf.close();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * This method update the sound level
	 */
	@Override
	public void updateSoundLevel(SoundLevel soundLevel) {

		return;
	}
}



