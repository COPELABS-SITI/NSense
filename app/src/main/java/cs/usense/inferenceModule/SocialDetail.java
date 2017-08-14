/*
 * COPYRIGHTS COPELABS/ULHT, LGPLv3.0, 2015/11/25.
 * Class is part of the NSense application.
 */

package cs.usense.inferenceModule;

import android.os.Parcel;
import android.os.Parcelable;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;

import cs.usense.pipelines.location.LocationEntry;
import cs.usense.utilities.InterestsUtils;


/**
 * It provides support for inference module and provides the SocialDetail
 * object to store the information to compute Social Interaction and Propinquity.
 * @author Saeik Firdose (COPELABS/ULHT),
 * @author Miguel Tavares (COPELABS/ULHT)
 * @version 2.0, 2016
 */
public class SocialDetail extends SocialComputationalData implements Comparable<SocialDetail>, Parcelable {

	/** This variable stores the social interaction percentage. */
	private static double siPercentage;

	/** This variable stores the propinquity percentage */
	private static double propPercentage;

	/** This variable stores the average social interaction percentage. */
	private static double avgSiPercentage;

	/** This variable stores the average propinquity percentage */
	private static double avgPropPercentage;

	/** Device name variable of the SocialDetail object */
	private String deviceName;

	/** This variable stores the bt mac address */
	private String btMacAddress;

	/** Distance variable of the SocialDetail object */
	private double distance = LocationEntry.NA_DISTANCE_VALUE;

	/** This variable stores the number of stars associated to social interaction. */
	private int socialInteractionStars;

	/** This variable stores the number of stars associated to propinquity. */
	private int propinquityStars;

	/** This variable stores user's interests */
	private String interests;

	/**
	 *  This is the constructor of the SocialDetail class
	 */
	public SocialDetail(String deviceName, String btMacAddress, double socialWeight, double encDurationNow, String interests) {
		super(socialWeight, encDurationNow);
		this.deviceName = deviceName.trim();
		this.btMacAddress = btMacAddress;
		this.interests = interests;
	}

	/**
	 * This method is the parcelable constructor of SocialDetail class
	 * @param in parcel received
	 */
	private SocialDetail(Parcel in) {
		siPercentage = in.readDouble();
		propPercentage = in.readDouble();
		deviceName = in.readString();
		distance = in.readDouble();
		socialInteractionStars = in.readInt();
		propinquityStars = in.readInt();
		interests = in.readString();
	}

	public static boolean isSiLowerThanSiAvg(double value) {
		return SocialDetail.getSiPercentage() < SocialDetail.getAvgSiPercentage() * value;
	}

	public static boolean isPropLowerThanPropAvg(double value) {
		return SocialDetail.getPropPercentage() < SocialDetail.getAvgPropPercentage() * value;
	}

	/**
	 * This method returns the social interaction percentage
	 * @return siPercentage
     */
	public static double getSiPercentage() {
		return siPercentage;
	}

	/**
	 * This method returns the propinquity percentage
	 * @return propPercentage
     */
	public static double getPropPercentage() {
		return propPercentage;
	}

	/**
	 * This method returns the avg social interaction percentage
	 * @return siPercentage
	 */
	public static double getAvgSiPercentage() {
		return avgSiPercentage;
	}

	/**
	 * This method returns the avg propinquity percentage
	 * @return propPercentage
	 */
	public static double getAvgPropPercentage() {
		return avgPropPercentage;
	}


	/**
	 * This method get the device name
	 * @return deviceName the device name
	 */
	public String getDeviceName() {
		return deviceName;
	}

	/**
	 * This method returns the bt mac address
	 * @return bt mac address
     */
	public String getBtMacAddress() {
		return  btMacAddress;
	}

	/**
	 * This method get the distance
	 * @return distance the distance
	 */
	public double getDistance() {
		return distance;
	}

	/**
	 * This method returns the quantity of social interaction stars
	 * @return socialInteractionStars
     */
	public int getSocialInteractionStars() {
		return socialInteractionStars;
	}

	/**
	 * This method returns the quantity of propinquity stars
	 * @return propinquityStars
     */
	public int getPropinquityStars() {
		return propinquityStars;
	}

	/**
	 * This method returns the stars average
	 * @return stars average
	 */
	public static double computeStarsAvg(String methodToCall) {
		double result = 0.0;
		try {
			for (SocialDetail socialDetail : SocialInteraction.getCurrentSocialInformation()) {
				Method method = socialDetail.getClass().getMethod(methodToCall);
				result += (int) method.invoke(socialDetail);
			}
			if(!SocialInteraction.getCurrentSocialInformation().isEmpty()) {
				result /= SocialInteraction.getCurrentSocialInformation().size();
			}
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * This method returns the user's interests
	 * @return interests
     */
	public String getInterests() {
		return interests;
	}

	public ArrayList<String> getCategories() {
		ArrayList<String> categories = new ArrayList<>();
		ArrayList<String> userInterests = new ArrayList<>(Arrays.asList(interests.replace(" ", "").split(",")));
		for(String userInterest : userInterests) {
			categories.add(InterestsUtils.getCategoryOfRating(userInterest));
		}
		return categories;
	}

	/**
	 * This method sets a new value for social interaction percentage
	 * @param newSIPercentage
     */
	public static void setSIPercentage(double newSIPercentage) {
		siPercentage = newSIPercentage;
	}

	/**
	 * This method sets a new value for propinquity percentage
	 * @param newPropPercentage
     */
	public static void setPropPercentage(double newPropPercentage) {
		propPercentage = newPropPercentage;
	}

	/**
	 * This method sets a new value for avg social interaction percentage
	 * @param newAvgSiPercentage
	 */
	public static void setAvgSIPercentage(double newAvgSiPercentage) {
		avgSiPercentage = newAvgSiPercentage;
	}

	/**
	 * This method sets a new value for avg propinquity percentage
	 * @param newAvgPropPercentage
	 */
	public static void setAvgPropPercentage(double newAvgPropPercentage) {
		avgPropPercentage = newAvgPropPercentage;
	}


	/**
	 * This method set the distance
	 * @param distance
	 */
	public void setDistance(double distance) {
		this.distance = distance;
	}

	/**
	 * This method set the social interaction EMA
	 * @param socialInteractionEMA the social interaction EMA
	 */
	public void setSocialInteractionEMA(double socialInteractionEMA) {
		socialInteractionStars = computeStars(socialInteractionEMA, SI_STARS_FACTOR);
		this.socialInteractionEMA = socialInteractionEMA;
	}

	/**
	 * This method set the propinquity EMA
	 * @param propinquityEMA the propinquity EMA
	 */
	public void setPropinquityEMA(double propinquityEMA) {
		propinquityStars = computeStars(propinquityEMA, PROP_STARS_FACTOR);
		this.propinquityEMA = propinquityEMA;
	}

	/**
	 * This method set the user's interests
	 * @param interests interests
	 */
	public void setInterests(String interests) {
		this.interests = interests;
	}

	@Override
	public int compareTo(SocialDetail socialDetail) {
		int result;
		double tempDistance = socialDetail.distance;
		double tempDistance2 = this.distance;

		/* If the distance is -1 we set it temporarily to 1000. We are doing this because -1 < 0 */
		if(tempDistance == LocationEntry.NA_DISTANCE_VALUE) {
			tempDistance = 1000;
		}

		if(tempDistance2 == LocationEntry.NA_DISTANCE_VALUE) {
			tempDistance2 = 1000;
		}

		if(tempDistance2 == tempDistance) {
			result = 0;
		} else if (tempDistance2 < tempDistance) {
			result = -1;
		} else {
			result = 1;
		}
		return result;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("\n\n").append("Device Name: ").append(getDeviceName()).append("\n");
		sb.append("BT MAC Address: ").append(getBtMacAddress()).append("\n");
		sb.append("Distance: ").append(getDistance()).append("\n");
		sb.append("Social Interaction %: ").append(getSiPercentage()).append("\n");
		sb.append("Propinquity %: ").append(getPropPercentage()).append("\n");
		sb.append("Social Interaction(EMA) %: ").append(getAvgSiPercentage()).append("\n");
		sb.append("Propinquity(EMA) %: ").append(getAvgPropPercentage()).append("\n");
		sb.append("Social Interaction: ").append(getSocialInteraction()).append("\n");
		sb.append("Propinquity: ").append(getPropinquity()).append("\n");
		sb.append("Social Interaction(EMA): ").append(getSocialInteractionEMA()).append("\n");
		sb.append("Propinquity(EMA): ").append(getmPropinquityEMA()).append("\n");
		sb.append("Social Interaction Stars: ").append(getSocialInteractionStars()).append("\n");
		sb.append("Propinquity Stars: ").append(getPropinquityStars()).append("\n");
		sb.append("SW: ").append(getSocialWeight()).append("\n");
		sb.append("Times Checking: ").append(getTimesCheckingEncDuration()).append("\n");
		sb.append("Enc Duration Now: ").append(getEncDurationNow()).append("\n");
		sb.append("Last Enc Duration: ").append(getLastSeenEncDurationNow()).append("\n");
		sb.append("Interests: ").append(getInterests()).append("\n");
		return sb.toString();
	}

	public static final Parcelable.Creator<SocialDetail> CREATOR = new Parcelable.Creator<SocialDetail>() {

		@Override
		public SocialDetail createFromParcel(Parcel source) {
			return new SocialDetail(source);
		}

		@Override
		public SocialDetail[] newArray(int size) {
			return new SocialDetail[size];
		}
	};

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeDouble(siPercentage);
		dest.writeDouble(propPercentage);
		dest.writeString(deviceName);
		dest.writeDouble(distance);
		dest.writeInt(socialInteractionStars);
		dest.writeInt(propinquityStars);
		dest.writeString(interests);
	}

	@Override
	public int describeContents() {
		return 0;
	}
}
