/*
 * COPYRIGHTS COPELABS/ULHT, LGPLv3.0, 2016/11/18.
 * Class is part of the NSense application. It provides support for sound pipeline.
 */


package cs.usense.pipelines.sound;


/**
 * This interface is used to listen the microphone recording the environment sound.
 * @author Miguel Tavares (COPELABS/ULHT)
 * @version 2.0, 2016
 */
interface SoundManagerListener {

    /**
     * This method provides the soundLevel level in decibels
     *  @param soundLevel Sound in decibels
     */
    void onReceiveSound(long soundLevel);

}