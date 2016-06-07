/**
 * @version 1.3
 * COPYRIGHTS COPELABS/ULHT, LGPLv3.0, date (e.g. 22-04-2016)
 * Class is part of the NSense application. It provides support for NSenseActivity class 
 * and provides custom-view for displaying the SocialInteraction and Propinquity level.
 * @author Saeik Firdose (COPELABS/ULHT)
 * @author Philipp Jahoda
 */
package cs.usense;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.Style;
import android.graphics.PointF;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;

import java.text.DecimalFormat;

/**
 * This class provides Simple custom-view for displaying values (with and without animation) and
 * selecting values onTouch().
 * 
 * @author Philipp Jahoda
 */
@SuppressLint("NewApi")
public class CircleDisplay extends View implements OnGestureListener {

    private static final String LOG_TAG = "CircleDisplay";

    /** the unit that is represented by the circle-display */
    private String mUnit = "%";

    /** startangle of the view */
    private float mStartAngle = 270f;

    /**
     * field representing the minimum selectable value in the display - the
     * minimum interval
     */
    private float mStepSize = 1f;

    /** angle that represents the displayed value */
    private float mAngle = 0f;

    /** current state of the animation */
    private float mPhase = 0f;

    /** the currently displayed value, can be percent or actual value */
    private float mValue = 0f;

    /** the maximum displayable value, depends on the set value */
    private float mMaxValue = 0f;

    /** percent of the maximum width the arc takes */
    private float mValueWidthPercent = 50f;

    /** if enabled, the inner circle is drawn */
    private boolean mDrawInner = true;

    /** if enabled, the center text is drawn */
    private boolean mDrawText = true;

    /** if enabled, touching and therefore selecting values is enabled */
    private boolean mTouchEnabled = true;

    /** represents the alpha value used for the remainder bar */
    private int mDimAlpha = 80;

    /** the decimalformat responsible for formatting the values in the view */
    private DecimalFormat mFormatValue = new DecimalFormat("###,###,###,##0.0");

    /** array that contains values for the custom-text */
    private String[] mCustomText = null;

    /**
     * rect object that represents the bounds of the view, needed for drawing
     * the circle
     */
    private RectF mCircleBox = new RectF();

    private Paint mArcPaint;
    private Paint mInnerCirclePaint;
    private Paint mTextPaint;

    /** object animator for doing the drawing animations */
    private ObjectAnimator mDrawAnimator;

    /**
     * This method construct the CircleDisplay class
     * @param context Interface to global information about an application environment
     */
    public CircleDisplay(Context context) {
        super(context);
        init();
    }

    /**
     * This method construct the CircleDisplay class
     * @param context Interface to global information about an application environment
     * @param atts A collection of attributes, as found associated with a tag in an XML document
     */
    public CircleDisplay(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    /**
     * This method construct the CircleDisplay class
     * @param context Interface to global information about an application environment
     * @param atts A collection of attributes, as found associated with a tag in an XML document
     * @param defStyleAttr different styles when retrieving attribute values
     */
    public CircleDisplay(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    /**
     * This method initialize the function of Arc, InnerCircle, Text Paint to set the style, color and also 
     * Initialize Drawing Animator function to calculated elapsed fraction of the animator
     */
    
    private void init() {

        mBoxSetup = false;

        mArcPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mArcPaint.setStyle(Style.FILL);
        mArcPaint.setColor(Color.rgb(192, 255, 140));

        mInnerCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mInnerCirclePaint.setStyle(Style.FILL);
        mInnerCirclePaint.setColor(Color.WHITE);

        mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setStyle(Style.STROKE);
        mTextPaint.setTextAlign(Align.CENTER);
        mTextPaint.setColor(Color.BLACK);
        mTextPaint.setTextSize(Utils.convertDpToPixel(getResources(), 16f));

        mDrawAnimator = ObjectAnimator.ofFloat(this, "phase", mPhase, 1.0f).setDuration(3000);
        mDrawAnimator.setInterpolator(new AccelerateDecelerateInterpolator());

        mGestureDetector = new GestureDetector(getContext(), this);
    }

    /** boolean flag that indicates if the box has been setup */
    private boolean mBoxSetup = false;

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (!mBoxSetup) {
            mBoxSetup = true;
            setupBox();
        }

        drawWholeCircle(canvas);

        drawValue(canvas);

        if (mDrawInner)
            drawInnerCircle(canvas);

        if (mDrawText) {
            
            if (mCustomText != null)
                drawCustomText(canvas);
            else
                drawText(canvas);
        }
    }

    /**
     * This method draws the text in the center of the view
     * 
     * @param c canvas
     */
    private void drawText(Canvas c) {
        c.drawText(mFormatValue.format(mValue * mPhase) + " " + mUnit, getWidth() / 2,
                getHeight() / 2 + mTextPaint.descent(), mTextPaint);
    }

    /**
     * This method draws the custom text in the center of the view
     * 
     * @param c canvas
     */
    private void drawCustomText(Canvas c) {
        
        int index = (int) ((mValue * mPhase) / mStepSize);
        
        if(index < mCustomText.length) {
            c.drawText(mCustomText[index], getWidth() / 2,
                    getHeight() / 2 + mTextPaint.descent(), mTextPaint);
        } else {
            Log.e(LOG_TAG, "Custom text array not long enough.");
        }        
    }

    /**
     * This method draws the background circle with less alpha
     * 
     * @param c canvas
     */
    private void drawWholeCircle(Canvas c) {
        mArcPaint.setAlpha(mDimAlpha);

        float r = getRadius();

        c.drawCircle(getWidth() / 2, getHeight() / 2, r, mArcPaint);
    }

    /**
     * This method draws the inner circle of the view
     * 
     * @param c canvas
     */
    private void drawInnerCircle(Canvas c) {

        c.drawCircle(getWidth() / 2, getHeight() / 2, getRadius() / 100f
                * (100f - mValueWidthPercent), mInnerCirclePaint);
    }

    /**
     * This method draws the actual value slice/arc
     * 
     * @param c Canvas
     */
    private void drawValue(Canvas c) {

        mArcPaint.setAlpha(255);

        float angle = mAngle * mPhase;

        c.drawArc(mCircleBox, mStartAngle, angle, true, mArcPaint);

    }

    /**
     * This method up the bounds of the view
     */
    private void setupBox() {

        int width = getWidth();
        int height = getHeight();

        float diameter = getDiameter();

        mCircleBox = new RectF(width / 2 - diameter / 2, height / 2 - diameter / 2, width / 2
                + diameter / 2, height / 2 + diameter / 2);
    }

    /**
     * This method shows the given value in the circle view
     * 
     * @param toShow to be show
     * @param total Total
     * @param animated Animation
     */
    public void showValue(float toShow, float total, boolean animated) {

        mAngle = calcAngle(toShow / total * 100f);
        mValue = toShow;
        mMaxValue = total;

        if (animated)
            startAnim();
        else {
            mPhase = 1f;
            invalidate();
        }
    }

    /**
     * This method Sets the unit that is displayed next to the value in the center of the
     * view. Default "%". Could be "€" or "$" or left blank or whatever it is
     * you display.
     * 
     * @param unit displayed next to the value in the center of the view
     */
    public void setUnit(String unit) {
        mUnit = unit;
    }

    /**
     * Returns the currently displayed value from the view. Depending on the
     * used method to show the value, this value can be percent or actual value.
     * 
     * @return mValue value can be percent or actual value
     */
    public float getValue() {
        return mValue;
    }

    public void startAnim() {
        mPhase = 0f;
        mDrawAnimator.start();
    }

    /**
     * This method set the duration of the drawing animation in milliseconds
     * 
     * @param durationmillis Duration in milli seconds
     */
    public void setAnimDuration(int durationmillis) {
        mDrawAnimator.setDuration(durationmillis);
    }

    /**
     * This method returns the diameter of the drawn circle/arc
     * 
     * @return Math.min(getWidth(),getHeight()) the diameter of the drawn circle/arc
     */
    public float getDiameter() {
        return Math.min(getWidth(), getHeight());
    }

    /**
     * This method returns the radius of the drawn circle
     * 
     * @return getDiameter()/2f the radius of the drawn circle
     */
    public float getRadius() {
        return getDiameter() / 2f;
    }

    /**
     * This method calculates the needed angle for a given value
     * 
     * @param percent Input as percentage
     * @return percent/100f*360f Output as percentage 
     */
    private float calcAngle(float percent) {
        return percent / 100f * 360f;
    }

    /**
     * This method set the starting angle for the view
     * 
     * @param angle input of angle
     */
    public void setStartAngle(float angle) {
        mStartAngle = angle;
    }

    /**
     * This method returns the current animation status of the view
     * @return mPhase phase of the view
     */
    public float getPhase() {
        return mPhase;
    }

    /**
     * DONT USE THIS METHOD
     * 
     * @param phase phase of the view
     */
    public void setPhase(float phase) {
        mPhase = phase;
        invalidate();
    }

    /**
     * This method set this to true to draw the inner circle, default: true
     * 
     * @param enabled true to draw the inner circle
     */
    public void setDrawInnerCircle(boolean enabled) {
        mDrawInner = enabled;
    }

    /**
     * This method returns true if drawing the inner circle is enabled, false if not
     * 
     * @return mDrawInner drawing the inner circle
     */
    public boolean isDrawInnerCircleEnabled() {
        return mDrawInner;
    }

    /**
     * This method set the drawing of the center text to be enabled or not
     * 
     * @param enabled true to drawing of the center text
     */
    public void setDrawText(boolean enabled) {
        mDrawText = enabled;
    }

    /**
     * This method returns true if drawing the text in the center is enabled
     * 
     * @return mDrawText true to drawing of the text
     */
    public boolean isDrawTextEnabled() {
        return mDrawText;
    }

    /**
     * This method set the color of the arc
     * 
     * @param color color of the arc
     */
    public void setColor(int color) {
        mArcPaint.setColor(color);
    }

    /**
     * This method set the size of the center text in dp
     * 
     * @param size size of the center text
     */
    public void setTextSize(float size) {
        mTextPaint.setTextSize(Utils.convertDpToPixel(getResources(), size));
    }

    /**
     * This method set the thickness of the value bar, default 50%
     * 
     * @param percentFromTotalWidth percentage to thickness of the value bar
     */
    public void setValueWidthPercent(float percentFromTotalWidth) {
        mValueWidthPercent = percentFromTotalWidth;
    }

    /**
     * This method Set an array of custom texts to be drawn instead of the value in the
     * center of the CircleDisplay. If set to null, the custom text will be
     * reset and the value will be drawn. Make sure the length of the array corresponds with the maximum number of steps (set with setStepSize(float stepsize).
     * 
     * @param custom custom texts to be drawn instead of the value
     */
    public void setCustomText(String[] custom) {
        mCustomText = custom;
    }

    /**
     * This method sets the number of digits used to format values
     * 
     * @param digits number of digits used to format values
     */
    public void setFormatDigits(int digits) {

        StringBuffer b = new StringBuffer();
        for (int i = 0; i < digits; i++) {
            if (i == 0)
                b.append(".");
            b.append("0");
        }

        mFormatValue = new DecimalFormat("###,###,###,##0" + b.toString());
    }

    /**
     * This method set the aplha value to be used for the remainder of the arc, default 80
     * (use value between 0 and 255)
     * 
     * @param alpha value to be used for the remainder of the arc
     */
    public void setDimAlpha(int alpha) {
        mDimAlpha = alpha;
    }

    /** paint used for drawing the text */
    public static final int PAINT_TEXT = 1;

    /** paint representing the value bar */
    public static final int PAINT_ARC = 2;

    /** paint representing the inner (by default white) area */
    public static final int PAINT_INNER = 3;

    /**
     * This method sets the given paint object to be used instead of the original/default
     * one
     * 
     * @param which e.g.CircleDisplay.PAINT_TEXT to set a new text paint
     * @param p paint colour
     */
    public void setPaint(int which, Paint p) {

        switch (which) {
            case PAINT_ARC:
                mArcPaint = p;
                break;
            case PAINT_INNER:
                mInnerCirclePaint = p;
                break;
            case PAINT_TEXT:
                mTextPaint = p;
                break;
        }
    }

    /**
     * This method Sets the stepsize (minimum selection interval) of the circle display,
     * default 1f. It is recommended to make this value not higher than 1/5 of
     * the maximum selectable value, and not lower than 1/200 of the maximum
     * selectable value. For a maximum value of 100 for example, a stepsize
     * between 0.5 and 20 is recommended.
     * 
     * @param stepsize setting minimum selection interval
     */
    public void setStepSize(float stepsize) {
        mStepSize = stepsize;
    }

    /**
     * This method returns the current stepsize of the display, default 1f
     * 
     * @return mStepSize get the step size
     */
    public float getStepSize() {
        return mStepSize;
    }

    /**
     * This method returns the center point of the view in pixels
     * 
     * @return PointF(float x, float y) the width of view in pixels
     */
    public PointF getCenter() {
        return new PointF(getWidth() / 2, getHeight() / 2);
    }

    /**
     * Enable touch gestures on the circle-display. If enabled, selecting values
     * onTouch() is possible. Set a SelectionListener to retrieve selected
     * values. Do not forget to set a value before selecting values. By default
     * the maxvalue is 0f and therefore nothing can be selected.
     * 
     * @param enabled selecting values onTouch() is possible
     */
    public void setTouchEnabled(boolean enabled) {
        mTouchEnabled = enabled;
    }

    /**
     * This method returns true if touch-gestures are enabled, false if not
     * 
     * @return mTouchEnabled true/false if touch-gestures are enabled/or not
     */
    public boolean isTouchEnabled() {
        return mTouchEnabled;
    }

    /**
     * This method set a selection listener for the circle-display that is called whenever a
     * value is selected onTouch()
     * 
     * @param l selection listener for the circle-display
     */
    public void setSelectionListener(SelectionListener l) {
        mListener = l;
    }

    /** listener called when a value has been selected on touch */
    private SelectionListener mListener;

    /** gesturedetector for recognizing single-taps */
    private GestureDetector mGestureDetector;

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        if (mTouchEnabled) {

            if (mListener == null)
                Log.w(LOG_TAG,
                        "No SelectionListener specified. Use setSelectionListener(...) to set a listener for callbacks when selecting values.");

            /** if the detector recognized a gesture, consume it */
            if (mGestureDetector.onTouchEvent(e))
                return true;

            float x = e.getX();
            float y = e.getY();

            /**  get the distance from the touch to the center of the view */
            float distance = distanceToCenter(x, y);
            float r = getRadius();

            /**  touch gestures only work when touches are made exactly on the bar/arc */
            if (distance >= r - r * mValueWidthPercent / 100f && distance < r) {

                switch (e.getAction()) {

                    case MotionEvent.ACTION_MOVE:

                        updateValue(x, y);
                        invalidate();
                        if (mListener != null)
                            mListener.onSelectionUpdate(mValue, mMaxValue);
                        break;
                    case MotionEvent.ACTION_UP:
                        if (mListener != null)
                            mListener.onValueSelected(mValue, mMaxValue);
                        break;
                }
            }

            return true;
        }
        else
            return super.onTouchEvent(e);
    }

    /**
     * This method updates the display with the given touch position, takes stepsize into
     * consideration
     * 
     * @param x point
     * @param y point
     */
    private void updateValue(float x, float y) {

        /** calculate the touch-angle */
        float angle = getAngleForPoint(x, y);

        /** calculate the new value depending on angle */
        float newVal = mMaxValue * angle / 360f;

        /** if no stepsize */
        if (mStepSize == 0f) {
            mValue = newVal;
            mAngle = angle;
            return;
        }

        float remainder = newVal % mStepSize;

        /** check if the new value is closer to the next, or the previous */
        if (remainder <= mStepSize / 2f) {

            newVal = newVal - remainder;
        } else {
            newVal = newVal - remainder + mStepSize;
        }

        /** set the new values */
        mAngle = getAngleForValue(newVal);
        mValue = newVal;
    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {

        /** get the distance from the touch to the center of the view */
        float distance = distanceToCenter(e.getX(), e.getY());
        float r = getRadius();

        /** touch gestures only work when touches are made exactly on the bar/arc */
        if (distance >= r - r * mValueWidthPercent / 100f && distance < r) {

            updateValue(e.getX(), e.getY());
            invalidate();

            if (mListener != null)
                mListener.onValueSelected(mValue, mMaxValue);
        }

        return true;
    }

    /**
     * This method returns the angle relative to the view center for the given point on the
     * chart in degrees. The angle is always between 0 and 360°, 0° is NORTH
     * 
     * @param x point
     * @param y point
     * @return the angle relative to the view center for the given point 
     */
    public float getAngleForPoint(float x, float y) {

        PointF c = getCenter();

        double tx = x - c.x, ty = y - c.y;
        double length = Math.sqrt(tx * tx + ty * ty);
        double r = Math.acos(ty / length);

        float angle = (float) Math.toDegrees(r);

        if (x > c.x)
            angle = 360f - angle;

        angle = angle + 180;

        /** neutralize overflow */
        if (angle > 360f)
            angle = angle - 360f;

        return angle;
    }

    /**
     * This method returns the angle representing the given value
     * 
     * @param value representing the value 
     * @return value/mMaxValue*360f angle
     */
    public float getAngleForValue(float value) {
        return value / mMaxValue * 360f;
    }

    /**
     * This method returns the value representing the given angle
     * 
     * @param angle input value
     * @return angle/360f*mMaxValue value representing the given angle
     */
    public float getValueForAngle(float angle) {
        return angle / 360f * mMaxValue;
    }

    /**
     * This method returns the distance of a certain point on the view to the center of the
     * view
     * 
     * @param x point
     * @param y point
     * @return dist distance of a certain point on the view
     */
    public float distanceToCenter(float x, float y) {

        PointF c = getCenter();

        float dist = 0f;

        float xDist = 0f;
        float yDist = 0f;

        if (x > c.x) {
            xDist = x - c.x;
        } else {
            xDist = c.x - x;
        }

        if (y > c.y) {
            yDist = y - c.y;
        } else {
            yDist = c.y - y;
        }

        /** pythagoras */
        dist = (float) Math.sqrt(Math.pow(xDist, 2.0) + Math.pow(yDist, 2.0));

        return dist;
    }

    /**
     * This method listener for callbacks when selecting values ontouch
     * 
     * @author Philipp Jahoda
     */
    public interface SelectionListener {

        /**
         * This method called everytime the user moves the finger on the circle-display
         * 
         * @param val Value
         * @param maxval Max value
         */
        public void onSelectionUpdate(float val, float maxval);

        /**
         * This method called when the user releases his finger fromt he circle-display
         * 
         * @param val Value
         * @param maxval Max value
         */
        public void onValueSelected(float val, float maxval);
    }

    public static abstract class Utils {

        /**
         * This method converts dp unit to equivalent pixels, depending on
         * device density.
         * 
         * @param r Resource
         * @param dp A value in dp (density independent pixels) unit. Which we
         *            need to convert into pixels
         * @return px A float value to represent px equivalent to dp depending on
         *         device density
         */
        public static float convertDpToPixel(Resources r, float dp) {
            DisplayMetrics metrics = r.getDisplayMetrics();
            float px = dp * (metrics.densityDpi / 160f);
            return px;
        }
    }

    @Override
    public boolean onDown(MotionEvent e) {
        
        return false;
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {
        

    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        
        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {
        

    }
}
