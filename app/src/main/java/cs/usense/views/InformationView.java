/*
 * COPYRIGHTS COPELABS/ULHT, LGPLv3.0, 2017/8/11.
 * Class is part of the NSense application.
 */

package cs.usense.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import cs.usense.R;

/**
 * This class is used to build a view which allows the user to
 * see his interests and ratings
 * @author Miguel Tavares (COPELABS/ULHT)
 * @version 1.0, 2017
 */
public class InformationView extends LinearLayout {

    /** This variable holds the image to be shown */
    private ImageView mImage;

    /** This variable holds the title to be shown */
    private TextView mTitle;

    /** This variable represents if the image view is enabled */
    private boolean mIsEnabled;

    /** This variable represents the view main color when is enabled */
    private int mPrimaryColor = R.color.black;

    /** This variable represents the view main color when is disabled */
    private int mSecondaryColor = R.color.white;

    /**
     * This method is the constructor of InformationView class
     * @param context application context
     */
    public InformationView(Context context) {
        super(context);
    }

    /**
     * This method is the constructor of InformationView class
     * @param context application context
     * @param attrs attributes written on xml layout
     */
    public InformationView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setup(context, attrs);
    }

    /**
     * This method initializes the view
     * @param context application context
     * @param attrs attributes written on xml layout
     */
    private void setup(Context context, AttributeSet attrs) {
        setupView(context);
        setupAttributes(context, attrs);
    }

    /**
     * This method instantiates the view objects
     * @param context application context
     */
    private void setupView(Context context) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.view_color_options, this, true);
        mImage = ((ImageView) findViewById(R.id.image));
        mTitle = ((TextView) findViewById(R.id.title));
        mIsEnabled = false;
    }

    /**
     * This method initialize the view objects
     * @param context application context
     * @param attrs attributes written on xml layout
     */
    private void setupAttributes(Context context, AttributeSet attrs) {
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.InformationView, 0, 0);
        Drawable drawable = ta.getDrawable(R.styleable.InformationView_titleImage);
        String text = ta.getString(R.styleable.InformationView_titleText);
        if(drawable != null)
            mImage.setBackgroundDrawable(drawable);
        if(text != null)
            mTitle.setText(text);
    }

    /**
     * This method sets a drawable to the ImageView
     * @param drawable drawable to set
     */
    public void setImage(Drawable drawable) {
        mImage.setBackgroundDrawable(drawable);
    }

    /**
     * This method sets a drawable to the ImageView using an integer reference
     * @param drawableReference drawable reference to set
     */
    public void setImage(int drawableReference) {
        setImage(getResources().getDrawable(drawableReference));
    }

    /**
     * This method sets a drawable to the ImageView and also it's color
     * @param drawableReference drawable reference to set
     * @param color color reference to set
     */
    public void setImage(int drawableReference, int color) {
        setImage(getResources().getDrawable(drawableReference));
        setImageColor(color);
    }

    /**
     * This method sets a drawable to the ImageView and also the title text
     * @param drawableReference drawable reference to set
     * @param title text to set
     */
    public void setImageAndTitle(int drawableReference, String title) {
        setImage(getResources().getDrawable(drawableReference));
        setTitle(title);
    }

    /**
     * This method sets a drawable to the ImageView, a color and also the title text
     * @param drawableReference drawable reference to set
     * @param color color reference to set
     * @param title text to set
     */
    public void setImageAndTitle(int drawableReference, int color, String title) {
        setImage(drawableReference, color);
        setTitle(title);
    }

    /**
     * This method sets a color to the ImageView
     * @param color color reference to set
     */
    public void setImageColor(int color) {
        if(mImage.getBackground() != null)
            mImage.getBackground().setColorFilter(getResources().getColor(color), PorterDuff.Mode.SRC_ATOP);
    }

    /**
     * This method sets the text color of the TextView
     * @param color color reference to set
     */
    public void setTitleColor(int color) {
        mTitle.setTextColor(getResources().getColor(color));
    }

    /**
     * This method sets the text of the TextView
     * @param title text to set
     */
    public void setTitle(String title) {
        mTitle.setText(title);
    }

    /**
     * This method sets the view's primary and secondary colors
     * @param primaryColor primary color reference to set
     * @param secondaryColor secondary color reference to set
     */
    public void setColors(int primaryColor, int secondaryColor) {
        mPrimaryColor = primaryColor;
        mSecondaryColor = secondaryColor;
    }

    /**
     * This method sets the view primary color
     * @param primaryColor primary color reference to set
     */
    public void setPrimaryColor(int primaryColor) {
        mPrimaryColor = primaryColor;
    }

    /**
     * This method sets the view secondary color
     * @param secondaryColor secondary color reference to set
     */
    public void setSecundaryColor(int secondaryColor) {
        mSecondaryColor = secondaryColor;
    }

    /**
     * This method returns the TextView text content
     * @return a String with the TextView text content
     */
    public String getTitle() {
        return mTitle.getText().toString();
    }

    /**
     * This method returns the view status, if is enabled or not.
     * @return a boolean which represents the view status
     */
    public boolean isEnabled() {
        return mIsEnabled;
    }

    /**
     * This method returns the reference of the view's primary color
     * @return primary color reference
     */
    public int getPrimaryColor() {
        return mPrimaryColor;
    }

    /**
     * This method returns the reference of the view's secondary color
     * @return secondary color reference
     */
    public int getSecondaryColor() {
        return mSecondaryColor;
    }

    /**
     * This method is used to trigger some behaviors when the view
     * is touched. In this case, it calls the OnClick method and also
     * the switchStatus
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            super.callOnClick();
            switchStatus();
        }
        return true;
    }

    /**
     * This method is used to change the color and also the view
     * status, if is enabled or not.
     */
    public void switchStatus() {
        if (mIsEnabled)
            changeColors(mPrimaryColor, mSecondaryColor);
        else
            changeColors(mSecondaryColor, mPrimaryColor);
        mIsEnabled = !mIsEnabled;
    }

    /**
     * This method changes the view's color
     * @param primaryColor primary color reference
     * @param secondaryColor secondary color reference
     */
    private void changeColors(int primaryColor, int secondaryColor) {
        setBackgroundColor(getResources().getColor(secondaryColor));
        setImageColor(primaryColor);
        setTitleColor(primaryColor);
    }
}
