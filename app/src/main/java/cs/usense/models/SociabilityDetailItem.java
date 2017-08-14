/*
 * COPYRIGHTS COPELABS/ULHT, LGPLv3.0, 2016/11/25.
 * Class is part of the NSense application.
 */

package cs.usense.models;

import android.os.Parcel;
import android.os.Parcelable;
import android.view.View;
import android.widget.ImageView;

import cs.usense.R;

/**
 * This class is a model to be used on SociabilityActivity.
 * Stores device name and stars value of social interaction or propinquity.
 * This class implements the interface Parcelable.
 * @author Miguel Tavares (COPELABS/ULHT)
 * @version 1.0, 2016
 */
public class SociabilityDetailItem implements Parcelable {

    /** This variable is used to define stars views */
    public static final int[] STARS = {R.id.row_ratingStars0, R.id.row_ratingStars1,
            R.id.row_ratingStars2, R.id.row_ratingStars3, R.id.row_ratingStars4};

    /** This variable stores the device name */
    private String mDeviceName;

    /** This variable stores the stars value. Can be social interaction or propinquity */
    private double mStarsValue;

    /**
     * This method is the constructor of SociabilityDetailItem class.
     * @param deviceName device name
     * @param starsValue stars value
     */
    public SociabilityDetailItem(String deviceName, double starsValue) {
        mDeviceName = deviceName;
        mStarsValue = starsValue;
    }

    /**
     * This method is the parcelable constructor of SociabilityDetailItem class
     * @param in received parcel
     */
    private SociabilityDetailItem(Parcel in) {
        mDeviceName = in.readString();
        mStarsValue = in.readDouble();
    }

    /**
     * This method returns the device name.
     * @return device name
     */
    public String getDeviceName() {
        return mDeviceName;
    }

    /**
     * This method returns how many stars this user has
     * @return stars quantity
     */
    private int getStarsQuantity() {
        return (int) mStarsValue;
    }

    /**
     * This method checks if this user has an half star
     * This half star is attributed if the remainder of the division is greater than or equal to 0.5
     * @return true if has an half star, false if not has an half star
     */
    private boolean hasHalfOfStar() {
        return (mStarsValue % getStarsQuantity() > 0.5);
    }

    /**
     * This is responsible to fill stars
     * @param view view where this method will fill the stars
     * @param starsIds stars resources
     */
    public void fillStars(View view, int[]starsIds) {
        boolean halfStarAllocated = true;
        int starsQuantity = getStarsQuantity();
        for(int i = 0, j = 0; i < starsIds.length; i++, j++) {
            ImageView imageView = ((ImageView) view.findViewById(starsIds[i]));
            if(j < starsQuantity) {
                imageView.setImageResource(R.drawable.ic_star_full);
            } else if(hasHalfOfStar() && halfStarAllocated) {
                imageView.setImageResource(R.drawable.ic_star_half);
                halfStarAllocated = false;
            } else {
                imageView.setImageResource(R.drawable.ic_star_empty);
            }
        }
    }

    @Override
    public String toString() {
        return mDeviceName + " " + mStarsValue;
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<SociabilityDetailItem> CREATOR = new Parcelable.Creator<SociabilityDetailItem>() {
        @Override
        public SociabilityDetailItem createFromParcel(Parcel in) {
            return new SociabilityDetailItem(in);
        }

        @Override
        public SociabilityDetailItem[] newArray(int size) {
            return new SociabilityDetailItem[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mDeviceName);
        dest.writeDouble(mStarsValue);
    }
}