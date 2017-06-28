/**
 * @version 2.0
 * COPYRIGHTS COPELABS/ULHT, LGPLv3.0, date (e.g. 22-04-2016)
 * Class is part of the NSense application. This class provides support for microphone pipeline, and
 * it used to receive the decibels values.
 * @author Miguel Tavares (COPELABS/ULHT)
 */

package cs.usense.pipelines.sound;

interface SoundManagerListener {

    /**
     * This method provides the soundLevel level in decibels
     *  @param soundLevel Sound in decibels
     */
    void onReceiveSound(long soundLevel);

}