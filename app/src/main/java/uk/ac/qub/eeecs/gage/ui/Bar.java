package uk.ac.qub.eeecs.gage.ui;

import android.graphics.Bitmap;

import uk.ac.qub.eeecs.gage.engine.ElapsedTime;
import uk.ac.qub.eeecs.gage.engine.graphics.IGraphics2D;
import uk.ac.qub.eeecs.gage.util.BoundingBox;
import uk.ac.qub.eeecs.gage.util.GraphicsHelper;
import uk.ac.qub.eeecs.gage.util.Vector2;
import uk.ac.qub.eeecs.gage.world.GameObject;
import uk.ac.qub.eeecs.gage.world.GameScreen;
import uk.ac.qub.eeecs.gage.world.LayerViewport;
import uk.ac.qub.eeecs.gage.world.ScreenViewport;

/**
 * Bar class that can display a variable bar percentage
 */
public class Bar extends GameObject {

    // /////////////////////////////////////////////////////////////////////////
    // Properties: Bar Defined Values
    // /////////////////////////////////////////////////////////////////////////

    /**
     * Define the available bar orientations
     */
    public enum Orientation {
        Horizontal, Vertical
    }

    // /////////////////////////////////////////////////////////////////////////
    // Properties:
    // /////////////////////////////////////////////////////////////////////////

    /**
     * Current bar orientation.
     */
    private Orientation mOrientation = Orientation.Horizontal;

    /**
     * Bitmap providing the realisation of the bar. The portion of
     * the bar which is displayed depends upon the current and
     * maximum property values.
     */
    private Bitmap mBarBitmap;

    /**
     * Record if this bar comes with a defined border
     */
    private boolean mHasBorder;

    /**
     * Bitmap providing the realisation of the border. If defined,
     * the border will always be displayed irrespective of the current
     * bar value.
     */
    private Bitmap mBorderBitmap;

    /**
     * Border offset value. The offset defines by how much the
     * bar image will be offset (x and y). It is intended to be
     * used when drawing a border image where the bar is visible
     * 'inside' the border.
     */
    private Vector2 mBorderOffset = new Vector2(Vector2.Zero);

    /**
     * Initial maximum value used to provide starting property values
     */
    private static final int INITIAL_MAX_VALUE = 100;

    /**
     * Maximum value size that the bar can take on. This value will
     * also determine the size of the 'graphical' chunks that the bar
     * bitmap item is split into.
     */
    private int mMaxValue = INITIAL_MAX_VALUE;

    /**
     * Current value for the bar.
     */
    private int mValue = INITIAL_MAX_VALUE;

    /**
     * Value for the bar that is used when drawing the bar to the screen.
     * This may be different than the current bar value if the bar is
     * currently 'animating' the transition due to a change of value.
     */
    private int mDisplayValue = INITIAL_MAX_VALUE;

    /**
     * Maximum value size that the displayed value variable can
     * be either increased or decreased during a 1 second interval
     * (this provides a means of controlling how quickly the bar
     * visually  reacts to change). By default a value of 100.0
     * is assumed.
     */

    private float mValueChangeSpeed = 100.0f;

    /**
     * In order to permit the bar to visually display a smooth value
     * change over time, an accumulator is maintained to ensure
     * accurate value changes over several frames.
     */
    private float mValueChangeAccumulator = 0.0f;


    // /////////////////////////////////////////////////////////////////////////
    // Constructors
    // /////////////////////////////////////////////////////////////////////////

    /**
     * Create a new Bar
     *
     * @param x                Centre x location of the bar
     * @param y                Centre y location of the bar
     * @param width            Width of the bar
     * @param height           Height of the bar
     * @param barBitmap        Bitmap to display for the bar
     * @param borderBitmap     Bitmap to display for the border
     * @param borderOffset     Offset of the bar bitmap relative to the border bitmap
     * @param orientation      Orientation of the bar
     * @param maxValue         Maximum bar value
     * @param initialValue     Initial bar value
     * @param valueChangeSpeed Speed at which the bar will visually react to value changes
     * @param gameScreen       Game screen instance to which this bar belongs
     */
    public Bar(float x, float y, float width, float height,
               Bitmap barBitmap, Bitmap borderBitmap, Vector2 borderOffset,
               Orientation orientation, int maxValue, int initialValue,
               float valueChangeSpeed, GameScreen gameScreen) {
        super(x, y, width, height, null, gameScreen);

        mBarBitmap = barBitmap;
        mHasBorder = true;
        mBorderBitmap = borderBitmap;
        mBorderOffset.set(borderOffset.x, borderOffset.y);
        mOrientation = orientation;
        mMaxValue = maxValue;
        mDisplayValue = initialValue;
        mValue = initialValue;
        mValueChangeSpeed = valueChangeSpeed;
    }

    /**
     * Create a new Bar
     *
     * @param x                Centre x location of the bar
     * @param y                Centre y location of the bar
     * @param width            Width of the bar
     * @param height           Height of the bar
     * @param barBitmap        Bitmap to display for the bar
     * @param borderBitmap     Bitmap to display for the border
     * @param borderOffset     Offset of the bar bitmap relative to the border bitmap
     * @param orientation      Orientation of the bar
     * @param gameScreen       Game screen instance to which this bar belongs
     */
    public Bar(float x, float y, float width, float height,
               Bitmap barBitmap, Bitmap borderBitmap, Vector2 borderOffset,
               Orientation orientation, GameScreen gameScreen) {
        super(x, y, width, height, null, gameScreen);

        mBarBitmap = barBitmap;
        mHasBorder = true;
        mBorderBitmap = borderBitmap;
        mBorderOffset.set(borderOffset.x, borderOffset.y);
        mOrientation = orientation;
    }

    /**
     * Create a new Bar
     *
     * @param x                Centre x location of the bar
     * @param y                Centre y location of the bar
     * @param width            Width of the bar
     * @param height           Height of the bar
     * @param barBitmap        Bitmap to display for the bar
     * @param orientation      Orientation of the bar
     * @param maxValue         Maximum bar value
     * @param initialValue     Initial bar value
     * @param valueChangeSpeed Speed at which the bar will visually react to value changes
     * @param gameScreen       Game screen instance to which this bar belongs
     */
    public Bar(float x, float y, float width, float height,
               Bitmap barBitmap, Orientation orientation, int maxValue,
               int initialValue, float valueChangeSpeed, GameScreen gameScreen) {
        super(x, y, width, height, null, gameScreen);

        mBarBitmap = barBitmap;
        mHasBorder = false;
        mOrientation = orientation;
        mMaxValue = maxValue;
        mDisplayValue = initialValue;
        mValue = initialValue;
        mValueChangeSpeed = valueChangeSpeed;
    }

    /**
     * Create a new Bar
     *
     * @param x                Centre x location of the bar
     * @param y                Centre y location of the bar
     * @param width            Width of the bar
     * @param height           Height of the bar
     * @param barBitmap        Bitmap to display for the bar
     * @param orientation      Orientation of the bar
     * @param gameScreen       Game screen instance to which this bar belongs
     */
    public Bar(float x, float y, float width, float height,
               Bitmap barBitmap, Orientation orientation, GameScreen gameScreen) {
        super(x, y, width, height, null, gameScreen);

        mBarBitmap = barBitmap;
        mHasBorder = false;
        mOrientation = orientation;
    }

    /**
     * Create a new Bar
     *
     * @param x                Centre x location of the bar
     * @param y                Centre y location of the bar
     * @param width            Width of the bar
     * @param height           Height of the bar
     * @param barBitmap        Bitmap to display for the bar
     * @param gameScreen       Game screen instance to which this bar belongs
     */
    public Bar(float x, float y, float width, float height,
               Bitmap barBitmap, GameScreen gameScreen) {
        super(x, y, width, height, null, gameScreen);

        mBarBitmap = barBitmap;
        mHasBorder = false;
    }


    // /////////////////////////////////////////////////////////////////////////
    // Methods: Accessors
    // /////////////////////////////////////////////////////////////////////////

    /**
     * Get the Bar's orientation
     *
     * @return Bar orientation
     */
    public Orientation getOrientation() {
        return mOrientation;
    }

    /**
     * Set the orientation of the bar
     *
     * @param orientation Bar orientation
     */
    public void setOrientation(Orientation orientation) {
        mOrientation = orientation;
    }


    /**
     * Get the offset of the bar bitmap relative to the border bitmap
     *
     * @return Offset of the bar relative to the border bitmap
     */
    public Vector2 getBorderOffset() {
        return mBorderOffset;
    }

    /**
     * Set the offset of the bar bitmap relative to the border bitmap
     *
     * @param borderOffset Offset of the bar relative to the border bitmap
     */
    public void setBorderOffset(Vector2 borderOffset) {
        mBorderOffset.set(borderOffset.x, borderOffset.y);
    }

    /**
     * Report if this Bar has a defined border
     *
     * @return True if the bar displays a border, false otherwise
     */
    public boolean hasBorder() {
        return mHasBorder;
    }

    /**
     * Set the Border bitmap
     *
     * @param borderBitmap Border bitmap
     */
    public void setBorder(Bitmap borderBitmap) {
        mBorderBitmap = borderBitmap;
        mHasBorder = true;
    }

    /**
     * Get the maximum bar value
     *
     * @return Maximum bar value
     */
    public int getMaxValue() {
        return mMaxValue;
    }

    /**
     * Set the maximum bar value
     *
     * @param maxValue Maximum bar value
     */
    public void setMaxValue(int maxValue) {
        mMaxValue = maxValue;
        if (mDisplayValue > maxValue) mDisplayValue = maxValue;
        if (mValue > maxValue) mValue = maxValue;
    }

    /**
     * Get the rate at which the displayed bar value will change
     * to the defined current bar value
     *
     * @return Rate of change per second
     */
    public float getValueChangeSpeed() {
        return mValueChangeSpeed;
    }

    /**
     * Set the rate at which the displayed bar value will change
     * to the defined current bar value
     *
     * @param valueChangeSpeed Rate of change per second
     */
    public void setValueChangeSpeed(float valueChangeSpeed) {
        mValueChangeSpeed = valueChangeSpeed;
    }

    // /////////////////////////////////////////////////////////////////////////
    // Methods: Value Report and Change
    // /////////////////////////////////////////////////////////////////////////

    /**
     * Get the current bar value
     *
     * @return Current value
     */
    public int getValue() {
        return mValue;
    }

    /**
     * Set the current bar value. Animation will be used to show the value
     * change.
     *
     * Note the current value cannot be less than 0 or exceed the defined
     * bar maximum value.
     *
     * @param value Current value
     */
    public void setValue(int value) {
        mValue = value;
        if (mValue < 0) mValue = 0;
        else if (mValue > mMaxValue) mValue = mMaxValue;
    }

    /**
     * Force an immediate change to the bar maximum value (no animation
     * of value change will be displayed).
     */
    public void forceMaxValue() {
        mDisplayValue = mMaxValue;
        mValue = mMaxValue;
    }

    /**
     * Force an immediate change to the bar minimum value, i.e. zero (no
     * animation of value change will be displayed).
     */
    public void forceMinValue() {
        mDisplayValue = 0;
        mValue = 0;
    }

    /**
     * Force an immediate change to the specified value (no animation
     * of value change will be displayed).
     *
     * Note the value to change to cannot be less than 0 or exceed the
     * defined bar maximum value.
     */
    public void forceValue(int valueToForce) {
        if (valueToForce < 0) valueToForce = 0;
        else if (valueToForce > mMaxValue) valueToForce = mMaxValue;

        mValue = valueToForce;
        mDisplayValue = valueToForce;
    }

    /**
     * Add the specified value to the bar's current value. Animation will be
     * used to show the value change.
     *
     * Note the new value cannot be less than 0 or exceed the defined bar
     * maximum value.
     *
     * @param valueToAdd Value to add
     */
    public void addValue(int valueToAdd) {
        mValue += valueToAdd;
        if (mValue < 0) mValue = 0;
        else if (mValue > mMaxValue) mValue = mMaxValue;
    }

    /**
     * Remove the specified value to the bar's current value. Animation will be
     * used to show the value change.
     *
     * Note the new value cannot be less than 0 or exceed the defined bar
     * maximum value.
     *
     * @param valueToSubtract Value to subtract
     */
    public void subtractValue(int valueToSubtract) {
        mValue -= valueToSubtract;
        if (mValue < 0) mValue = 0;
        else if (mValue > mMaxValue) mValue = mMaxValue;
    }


    // /////////////////////////////////////////////////////////////////////////
    // Methods: Update
    // /////////////////////////////////////////////////////////////////////////

    /**
     * Update the bar, moving the displayed value towards the set value if
     * needed.
     *
     * @param elapsedTime Elapsed time information
     */
    @Override
    public void update(ElapsedTime elapsedTime) {
        // if the display and current value match then no update is needed
        if (mDisplayValue == mValue)
            return;

        // Determine the difference between display and target and the
        // amount of change for this update.
        int difference = mValue - mDisplayValue;
        float valueChange = mValueChangeSpeed * (float) elapsedTime.stepTime;

        // Apply the value change
        if (Math.abs(difference) < valueChange)
            mDisplayValue = mValue;
        else {
            mValueChangeAccumulator += valueChange;
            int intValueChange = (int) Math.floor(mValueChangeAccumulator);
            if (difference > 0) {
                mDisplayValue += intValueChange;
                mValueChangeAccumulator -= (float) intValueChange;
            } else {
                mDisplayValue -= intValueChange;
                mValueChangeAccumulator -= (float) intValueChange;
            }
        }
    }


    // /////////////////////////////////////////////////////////////////////////
    // Methods: Draw
    // /////////////////////////////////////////////////////////////////////////

    /**
     * Define a internal bounding box instance - declared externally
     * to reduce object creation costs.
     */
    private BoundingBox barBound = new BoundingBox();

    /**
     * Draw the bar
     *
     * @param elapsedTime    Elapsed time information
     * @param graphics2D     Graphics instance
     * @param layerViewport  Layer viewport
     * @param screenViewport Screen viewport
     */
    @Override
    public void draw(ElapsedTime elapsedTime, IGraphics2D graphics2D,
                     LayerViewport layerViewport, ScreenViewport screenViewport) {
        // Draw the border if defined and visible
        if (mHasBorder) {
            if (GraphicsHelper.getClippedSourceAndScreenRect(
                    mBound, mBorderBitmap, layerViewport, screenViewport,
                    drawSourceRect, drawScreenRect)) {
                graphics2D.drawBitmap(mBorderBitmap, drawSourceRect, drawScreenRect, null);
            }
        }

        // Construct a bound for the bar
        barBound.x = mBound.x;
        barBound.y = mBound.y;
        barBound.halfWidth = mHasBorder ?
                mBound.halfWidth - 2.0f * mBorderOffset.x : mBound.halfWidth;
        barBound.halfHeight = mHasBorder ?
                mBound.halfHeight - 2.0f * mBorderOffset.y : mBound.halfHeight;

        // Draw the bar - this may not be entirely straightforward as the source and
        // destination rectangles may have been clipped depending on the defined
        // viewports. This entails that any scaling of the bar also needs to take into
        // account clipped.

        // Determine the visible areas
        if (GraphicsHelper.getClippedSourceAndScreenRect(
                barBound, mBarBitmap, layerViewport, screenViewport, drawSourceRect, drawScreenRect)) {

            // Scale the source and dest rectangles to match the bar value and draw
            reduceSourceDestBarRectanglesByValue();
            graphics2D.drawBitmap(mBarBitmap, drawSourceRect, drawScreenRect, null);
        }
    }

    /**
     * Draw the bar
     *
     * @param elapsedTime Elapsed time information
     * @param graphics2D  Graphics instance
     */
    @Override
    public void draw(ElapsedTime elapsedTime, IGraphics2D graphics2D) {
        // If we have a border, draw it directly to the screen
        if (mHasBorder) {
            // Build appropriate border source and destination rectangles
            drawSourceRect.set(
                    0, 0, mBorderBitmap.getWidth(), mBorderBitmap.getHeight());
            drawScreenRect.set(
                    (int) (position.x - mBound.halfWidth), (int) (position.y - mBound.halfHeight),
                    (int) (position.x + mBound.halfWidth), (int) (position.y + mBound.halfHeight));
            graphics2D.drawBitmap(mBorderBitmap, drawSourceRect, drawScreenRect, null);
        }

        // Construct a bound for the bar
        barBound.x = mBound.x;
        barBound.y = mBound.y;
        barBound.halfWidth = mHasBorder ?
                mBound.halfWidth - 2.0f * mBorderOffset.x : mBound.halfWidth;
        barBound.halfHeight = mHasBorder ?
                mBound.halfHeight - 2.0f * mBorderOffset.y : mBound.halfHeight;

        // Build appropriate bar source and destination rectangles
        drawSourceRect.set(
                0, 0, mBarBitmap.getWidth(), mBarBitmap.getHeight());
        drawScreenRect.set(
                (int) (position.x - mBound.halfWidth) + (mHasBorder ? (int)mBorderOffset.x : 0),
                (int) (position.y - mBound.halfHeight) + (mHasBorder ? (int)mBorderOffset.y : 0),
                (int) (position.x + mBound.halfWidth) - (mHasBorder ? (int)mBorderOffset.x : 0),
                (int) (position.y + mBound.halfHeight) - (mHasBorder ? (int)mBorderOffset.y : 0));

        // Scale the source and dest rectangles to match the bar value and draw
        reduceSourceDestBarRectanglesByValue();
        graphics2D.drawBitmap(mBarBitmap, drawSourceRect, drawScreenRect, null);
    }

    /**
     * Reduce the source and destination rectangles to match the bar value
     */
    private void reduceSourceDestBarRectanglesByValue() {
        // Scale the bar depending on the value, taking into account any clipped region
        float barPercentage = (float) mDisplayValue / (float) mMaxValue;
        if (mOrientation == Orientation.Horizontal) {
            int targetXDraw = (int) ((float) mBarBitmap.getWidth() * barPercentage);
            if (drawSourceRect.right > targetXDraw) {
                float percentageReduction = (float) (targetXDraw - drawSourceRect.left)
                        / (float) (drawSourceRect.right - drawSourceRect.left);
                drawScreenRect.right = drawScreenRect.left +
                        (int) ((float) (drawScreenRect.right - drawScreenRect.left) * percentageReduction);
                drawSourceRect.right = targetXDraw;
            }
        } else {
            // Invert the draw along the y-axis
            int targetYDraw = (int) ((float) mBarBitmap.getHeight() * (1.0f - barPercentage));
            if (drawSourceRect.top < targetYDraw) {
                float percentageReduction = (float) (targetYDraw - drawSourceRect.top)
                        / (float) (drawSourceRect.bottom - drawSourceRect.top);
                drawScreenRect.top = drawScreenRect.bottom -
                        (int) ((float) (drawScreenRect.bottom - drawScreenRect.top) * (1.0f - percentageReduction));
                drawSourceRect.top = targetYDraw;
            }
        }
    }


}

