/**
 * @version 1.3
 * COPYRIGHTS COPELABS/ULHT, LGPLv3.0, date (e.g. 22-04-2016)
 * Class is part of the NSense application. It provides helper methods for SocialityDetails class.
 * @author Saeik Firdose (COPELABS/ULHT)
 */
package cs.usense.inferenceModule;

import java.util.List;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * This class provides Parcelable for SocialityDetails object 
 */

public class SocialityDetailsListParcelable implements Parcelable {
	
	/** The constructor of SocialityDetailsListParcelable */
	public SocialityDetailsListParcelable (){
		return;
	}
	/** This variable is used to get the ArrayList of SocialityDetails */
	List<SocialityDetails> socialityDetailsList;

	/**
	 * This method get the sociality details list
	 * @return socialityDetailsList List of sociality details object
	 */
	public List<SocialityDetails> getSocialityDetailsList() {
		return socialityDetailsList;
	}

	/**
	 * This method set the sociality details list
	 * @param socialityList List of SocialityDetails objects
	 */
	public void setSocialityDetailsList(
			List<SocialityDetails> socialityList) {
		this.socialityDetailsList = socialityList;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	/**
	 * This method write to the parcel list
	 * @param dest The Parcel in which the Sociality details should be written.
	 * @param flags Flags about how the object should be written. May be 0 or PARCELABLE_WRITE_RETURN_VALUE.
	 */
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeTypedList(socialityDetailsList);
	}

	/**
	 * This method creates the Parcelable
	 */
	public static final Parcelable.Creator<SocialityDetailsListParcelable> CREATOR =
			new Parcelable.Creator<SocialityDetailsListParcelable>() {
		public SocialityDetailsListParcelable createFromParcel(Parcel in) {
			return new SocialityDetailsListParcelable(in);
		}

		public SocialityDetailsListParcelable[] newArray(int size) {
			return new SocialityDetailsListParcelable[size];
		}
	};

	/**
	 * This method initialize the Sociality DetailsList Parcelable 
	 * @param in The Parcel in which the object should be written.
	 */
	public SocialityDetailsListParcelable(Parcel in) {
		in.readTypedList(socialityDetailsList, SocialityDetails.CREATOR);
	}

}
