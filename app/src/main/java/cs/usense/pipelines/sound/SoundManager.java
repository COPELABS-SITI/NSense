/*
 * COPYRIGHTS COPELABS/ULHT, LGPLv3.0, 2015/5/26.
 * Class is part of the NSense application. It provides support for sound pipeline.
 */

package cs.usense.pipelines.sound;

import android.content.Context;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.AsyncTask;
import android.util.Log;


/**
 * This class provides the core idealogy of sound process with microphone adapter.
 * This class gives us the filtered sound frequency, and convertion to amplitude.
 * In this class we implement two filters which are EMA and HPF filters.
 * @author Reddy Pallavali (COPELABS/ULHT),
 * @author Miguel Tavares (COPELABS/ULHT)
 * @version 2.0, 2016
 */
class SoundManager {

	/** This TAG is used to do some debug tests */
	private static final String TAG = "SoundManager";

	/** This variable to set the filter for High pass filter  */
	private static final double HPF_FILTER = 0.9;

	/** This variable defines the sample rate of recording */
	private static final int SAMPLE_RATE = 8000;

	/** This variable stores the audio source type */
	private static final int AUDIO_SOURCE = MediaRecorder.AudioSource.VOICE_RECOGNITION;

	/** This variable stores the buffer size of samples collected */
	private final int mBufferSize;

	/** This variable checks if the recorder is paused or not */
	private boolean mIsPaused;

	/** This object is used to notify SoundPipeline */
	private SoundManagerListener mSoundManagerListener;

	/** This object is used to record the environment sound */
	private AudioRecord mAudioRecord;

	/** This object is used to check if the microphone is available to start recording */
	private AudioManager mAudioManager;

	/**
	 * This method is the constructor of SoundManager class
	 * @param soundPipeline callback to notify with data related with the record sound
	 * @param context application context
	 */
	SoundManager(SoundPipeline soundPipeline, Context context) {
		mSoundManagerListener = soundPipeline;
		mAudioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
		mBufferSize = bufferSize(SAMPLE_RATE, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT);
		mAudioRecord = new AudioRecord(AUDIO_SOURCE, SAMPLE_RATE, AudioFormat.CHANNEL_IN_MONO,
				AudioFormat.ENCODING_PCM_16BIT, mBufferSize);
		pause();
	}

	/**
	 * Helper method to find a buffer size for AudioRecord which will be at
	 * least 1 second.
	 *
	 * @param sampleRateInHz the sample rate expressed in Hertz.
	 * @param channelConfig describes the configuration of the audio channels.
	 * @param audioFormat the format in which the audio data is represented.
	 * @return buffSize the size of the audio record input buffer.
	 */
	private int bufferSize(int sampleRateInHz, int channelConfig, int audioFormat) {
		int buffSize = AudioRecord.getMinBufferSize(sampleRateInHz, channelConfig, audioFormat);
		return buffSize < sampleRateInHz ? sampleRateInHz : buffSize;
	}

	/** This method is used to record the environment sounds and notifies the pipeline with db's */
	void getSoundInDB() {
		Log.i(TAG, "getSoundInDB method was invoked");
		new AsyncTask<Void, Void, Void>() {

			@Override
			public Void doInBackground(Void... params) {
				Log.i(TAG, "doInBackground method was invoked");
				if(isMicrophoneAvailable()) {
					Log.i(TAG, "Microphone is available. I will start recording.");
					resume();
					mSoundManagerListener.onReceiveSound(getSoundSample());
					pause();
				} else {
					Log.e(TAG, "There is a call in progress. Cannot record now.");
				}
				return null;
			}
		}.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
	}

	/**
	 * This method returns the max value of read samples
	 * @return max value
	 */
	private long getSoundSample() {
		double rms = 0, result = 0;
		short[] buffer20ms = new short[mBufferSize / 50];
		for(int i = 0; i < mBufferSize; i += buffer20ms.length) {
			if (mAudioRecord.read(buffer20ms, 0, buffer20ms.length) > 0) {
				for (short buffer20m : buffer20ms) {
					rms += buffer20m * buffer20m;
				}
				rms = Math.sqrt(rms / buffer20ms.length);
				result = soundFilter(result, convertToDB(rms));
			}
		}
		return Math.round(result);
	}

	/**
	 * This method is to apply the sound filter to supress some noise on the captured sound wave
	 * In this method implemented two filters namely EMA and HPF
	 * Either the filter we can choose
	 *
	 * Highpass Pre-emphasizing Filter
	 * hPF y(n) = x(n) * a + (1 - a) * x(n-1)
	 *
	 * EMA Filter
	 * EMA yk = a * xk + (1 - a) * yk-1
	 * @return hpf Highpass Pre-emphasizing Filter
	 */
	private double soundFilter(double lastSound, double soundAverage) {
		return lastSound > 0 ? lastSound * HPF_FILTER + (1 - HPF_FILTER) * soundAverage : soundAverage;
	}

	/**
	 * This method convert the received sound amplitude into decibels by using acustic wave equation
	 * @return sound in amplitude
	 */
	private double convertToDB(double soundAverage) {
		return 20.0 * Math.log10((2500.0 / Math.pow(100.0, 30.0 / 20.0)) * soundAverage);
	}

	/**
	 * This method stops the recording
	 */
	private void pause() {
		if (!mIsPaused)
			mAudioRecord.stop();
		mIsPaused = true;
	}

	/**
	 * This method starts the recording
	 */
	private void resume() {
		if (mIsPaused)
			mAudioRecord.startRecording();
		mIsPaused = false;
	}

	/**
	 * This method checks if the microphone is available to record the environment sound.
	 * @return microphone availability
	 */
	private boolean isMicrophoneAvailable() {
		Log.i(TAG, "isMicrophoneAvailable method was invoked");
		return mAudioManager.getMode() == 0;
	}

}