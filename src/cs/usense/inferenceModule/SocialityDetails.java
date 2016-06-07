/**
 * @version 1.3
 * COPYRIGHTS COPELABS/ULHT, LGPLv3.0, date (e.g. 22-04-2016)
 * Class is part of the NSense application. It provides support for inference module and 
 * provides the SocialityDetails object to store the information to compute Social Interaction and Propinquity.
 * @author Saeik Firdose (COPELABS/ULHT)
 */
package cs.usense.inferenceModule;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * This class provides getter and setter methods of SocialityDetails object 
 */

public class SocialityDetails implements Parcelable {
	
	/**
	 *  This constructor method for SocialityDetails
	 */
	public SocialityDetails(){
		
	}

	/** Device name variable of the SocialityDetails object */
	public String devName;
	
	/** Distance variable of the SocialityDetails object */
	public double mDistance;
	
	/** Social Interaction variable of the SocialityDetails object */
	public double mSI;
	
	/** Propinquity variable of the SocialityDetails object */
	public double mPropinquity;
	
	/** Social Interaction EMA variable of the SocialityDetails object */
	public double mSiEMA;
	
	/** Propinquity EMA variable of the SocialityDetails object */
	public double mPropEMA;
	
	/** Previous Social Interaction EMA variable of the SocialityDetails object */
	public double mPrevSiEMA;
	
	/** Previous Propinquity EMA variable of the SocialityDetails object */
	public double mPrevPropEMA;

	/**
	 * This method get the device name
	 * @return devName the device name
	 */
	public String getDevName() {
		return devName;
	}

	/**
	 * This method set the device name
	 * @param devName the device name
	 */
	public void setDevName(String devName) {
		this.devName = devName;
	}
	
	
	/**
	 * This method get the distance
	 * @return mDistance the distance
	 */
	public double getmDistance() {
		return mDistance;
	}

	/**
	 * This method set the distance
	 * @param mDistance the distance
	 */
	public void setmDistance(double mDistance) {
		this.mDistance = mDistance;
	}

	/**
	 * This method get the social interaction
	 * @return mSI the social interaction
	 */
	public double getmSI() {
		return mSI;
	}

	/**
	 * This method set the social interaction
	 * @param mSI the social interaction
	 */
	public void setmSI(double mSI) {
		this.mSI = mSI;
	}

	/**
	 * This method get the propinquity
	 * @return mPropinquity the propinquity
	 */
	public double getmPropinquity() {
		return mPropinquity;
	}

	/**
	 * This method set the propinquity
	 * @param mPropinquity the propinquity
	 */
	public void setmPropinquity(double mPropinquity) {
		this.mPropinquity = mPropinquity;
	}
	
	/**
	 * This method get the social interaction EMA
	 * @return mSiEMA the social interaction EMA
	 */
	public double getmSiEMA() {
		return mSiEMA;
	}

	/**
	 * This method set the social interaction EMA
	 * @param mSiEMA the social interaction EMA
	 */
	public void setmSiEMA(double mSiEMA) {
		this.mSiEMA = mSiEMA;
	}

	/**
	 * This method get the propinquity EMA
	 * @return mPropEMA the propinquity EMA
	 */
	public double getmPropEMA() {
		return mPropEMA;
	}

	/**
	 * This method set the propinquity EMA
	 * @param mPropEMA the propinquity EMA
	 */
	public void setmPropEMA(double mPropEMA) {
		this.mPropEMA = mPropEMA;
	}

	/**
	 * This method get the previous social interaction EMA
	 * @return mPrevSiEMA the previous social interaction EMA
	 */
	public double getmPrevSiEMA() {
		return mPrevSiEMA;
	}

	/**
	 * This method set the previous social interaction EMA
	 * @param mPrevSiEMA the previous social interaction EMA
	 */
	public void setmPrevSiEMA(double mPrevSiEMA) {
		this.mPrevSiEMA = mPrevSiEMA;
	}

	/**
	 * This method get the previous propinquity EMA
	 * @return mPrevPropEMA the previous propinquity EMA
	 */
	public double getmPrevPropEMA() {
		return mPrevPropEMA;
	}

	/**
	 * This method set the previous propinquity EMA
	 * @param mPrevPropEMA the previous propinquity EMA
	 */
	public void setmPrevPropEMA(double mPrevPropEMA) {
		this.mPrevPropEMA = mPrevPropEMA;
	}



	@Override
	public int describeContents() {
		return 0;
	}

	/**
	 * This method writes to the parcable with sociality details
	 * @param dest The Parcel in which the Sociality details should be written.
	 * @param flags Flags about how the object should be written. May be 0 or PARCELABLE_WRITE_RETURN_VALUE.
	 */
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(devName);
		dest.writeDouble(mDistance);
		dest.writeDouble(mSI);
		dest.writeDouble(mPropinquity);
		dest.writeDouble(mSiEMA);
		dest.writeDouble(mPropEMA);
		dest.writeDouble(mPrevSiEMA);
		dest.writeDouble(mPrevPropEMA);
	}

	/**
	 * This method creates the parcelable with sociality details object
	 */
	public static final Parcelable.Creator<SocialityDetails> CREATOR = new Parcelable.Creator<SocialityDetails>() {
		public SocialityDetails createFromParcel(Parcel in) {
			return new SocialityDetails(in);
		}

		public SocialityDetails[] newArray(int size) {
			return new SocialityDetails[size];
		}
	};

	/**
	 * This method initialize the parcelable with sociality details object
	 * @param in Sociality details
	 */
	public SocialityDetails(Parcel in) {
		devName = in.readString();
		mDistance=in.readDouble();
		mSI=in.readDouble();
		mPropinquity=in.readDouble();
		mSiEMA=in.readDouble();
		mPropEMA=in.readDouble();
		mPrevSiEMA=in.readDouble();
		mPrevPropEMA=in.readDouble();
	}

}
