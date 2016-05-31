/**
 * @version 1.2
 * COPYRIGHTS COPELABS/ULHT, LGPLv3.0, date (e.g. 22-04-2016)
 * Class is part of the USense application. It provides support for Microphone pipeline.
 * This class provides the core idealogy of sound process with microphone adapter.
 * @author Reddy Pallavali (COPELABS/ULHT)
 */

package cs.usense.microphone;


import java.io.IOException;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.media.MediaRecorder;
import android.widget.Toast;
import cs.usense.R;
import cs.usense.UsenseService;

/**
 * This gives us the filtered sound frequency, and converion to amplitude.
 * In this class we implement two filters which are EMA and HPF filters.
 */
public class SoundManager {
	
	/** Interface to global information about an application environment. */
	private static Context mContext;

	/** This variable to set the filter for High pass filter  */
	static final private double HPF_FILTER = 0.97; 
	
	/** This class is Used to record audio.*/
	private MediaRecorder mRecorder = null;
	
	/** This variable to set the High pass filter */
	private double hPF = 0.0;
	
	/** This variable to set the Amplitude */
	public static final double AMP = 1.0;
	
	/** This variable to check the Feature is not available */
	private static final String FEATURE_NOT_AVAILBLE = null;
	
	/** This variable to set the poll interval */
	private static final int POLL_INTERVAL = 300;
	
	/** This variable to check the microphone is exist */
	private static Boolean micExist;
	
	/** This variable to check the capturing */
	private static boolean capturing = false;
	
	/** This variable to define start off set */
	int startOffset;
	
	/** This variable to access average decibles */
	protected double avgdB;

	@SuppressWarnings("unused")
	private Runnable mRunPool = new Runnable() {
		@Override
		public void run() {
			if(isCapturing()){
				stop();
			}else{
				Toast.makeText(mContext, "Error enabling Microphone Closing...", Toast.LENGTH_SHORT).show();
				stop();
			}
		}
	};

	/**
	 * This method construct the SoundManager
	 * @param callback Supply functionality for UsenseActivity to use
	 */	
	public SoundManager(UsenseService callback) {

	}

	/**
	 * This method checks whether the microphone exists in the device or not 
	 * @param context Interface to global information about an application environment
	 * @return true if device exists, dialog otherwise
	 */
	public static boolean micExist(Context context){
		mContext = context;
		if(!micExist)
		{
			showDialog(FEATURE_NOT_AVAILBLE);
		}else {
			PackageManager pm = getPackageManager();
			micExist = pm.hasSystemFeature(PackageManager.FEATURE_MICROPHONE);
			micExist = true;
		}
		return micExist;
	}

	/**
	 * This method shows the dailog
	 * @param id String which provide the availability of feature
	 * @return True/False if Feature not available/Feature is available
	 */
	protected static Dialog showDialog(String id) {
		if (id == FEATURE_NOT_AVAILBLE) {
			Context SoundManager = null;
			return new AlertDialog.Builder(SoundManager)
					.setIcon(R.drawable.ic_launcher)
					.setTitle(R.string.app_name)
					.setMessage(R.string.no_availble_feture)
					.setNeutralButton(R.string.ok, new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							finish();
						}
					}).create();
		} else return null;
	}
	private static PackageManager getPackageManager() {
		return null;
	}

	/**
	 * This method intialise the Media recorder. Media Recorder is used to start microhphone activity by collecting sound samples captured dynamically.
	 * The process takes place in the temporary file of the users device.
	 */
	public void start() {
		if (mRecorder == null) {

			mRecorder = new MediaRecorder();
			mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
			mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
			mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
			mRecorder.setOutputFile("/dev/null");

			try {
				mRecorder.prepare();
				mRecorder.start();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			hPF = 0.0;
		}
	}

	/**
	 * This method stop the media recorder
	 */

	public void stop() {
		if (mRecorder != null) {
			mRecorder.stop();
			mRecorder.release();
			mRecorder = null;
		}
	}

	/**
	 * This method convert the received sound apmlitude into decibels by using acustic wave equation
	 * @return sound in amplitude
	 */
	public double soundDb() {
		return (int)(20 * Math.log10(getAmplitudeHPF() / AMP));

	}

	/**
	 * This method extract the maximum sound amplitude by using Android method getMaxAmplitude() 
	 * @return getMaxAmplitude null or maximum amplitude of captured sound wave 
	 */
	public double getMaxAmplitude() {
		if (mRecorder != null){
			return mRecorder.getMaxAmplitude();
		}
		else{
			return 0;
		}
	}

	/**
	 * This method is to apply the sound filter to supress some noise on the captured sound wave
	 * In this method implemented two filters namely EMA and HPF 
	 * Either the filter we can choose

	 * Highpass Pre-emphasizing Filter
	 * hPF y(n) = x(n)-0.97*x(n-1)
	 *  
	 * EMA Filter
	 * EMA yk = a * xk+(1-a)yk-1
	 * @return hpf Highpass Pre-emphasizing Filter
	 */
	public double getAmplitudeHPF() {
		double amp = getMaxAmplitude();
		hPF = amp - HPF_FILTER * hPF; 
		
		return hPF;

	}


	/**
	 * This method set the SoundListener
	 * @param listener the SoundListener
	 */
	public void setSoundListener(SoundListener listener) {
		return;
	}

	/**
	 * This method returns true if capturing the sound
	 * @return capturing Checks whether it is capturing or not
	 */
	public static boolean isCapturing() {
		return capturing;
	}

	/**
	 * This method to finish 
	 */
	protected static void finish() {
		return;
	}


}
