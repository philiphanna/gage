package uk.ac.qub.eeecs.gage.ui;

import android.graphics.Bitmap;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;

import uk.ac.qub.eeecs.gage.engine.ElapsedTime;
import uk.ac.qub.eeecs.gage.engine.graphics.IGraphics2D;
import uk.ac.qub.eeecs.gage.engine.input.TouchEvent;
import uk.ac.qub.eeecs.gage.util.BoundingBox;
import uk.ac.qub.eeecs.gage.util.GraphicsHelper;
import uk.ac.qub.eeecs.gage.util.Vector2;
import uk.ac.qub.eeecs.gage.world.GameScreen;
import uk.ac.qub.eeecs.gage.world.LayerViewport;
import uk.ac.qub.eeecs.gage.world.ScreenViewport;

/**
 * Thumb stick control used to provide 2D direction and magnitude input.
 *
 *
 * @version 1.0
 */
public class ThumbStick extends Button {

    // /////////////////////////////////////////////////////////////////////////
    // Properties
    // /////////////////////////////////////////////////////////////////////////

    /**
     * Outer circle details for the bitmap that is displayed as soon as the thumb
     * control is triggered. The bound is assumed to be either in screen or layer
     * space depending on the value of the inherited mProcessInLayerSpace flag.
     * The diameter (Note - not the radius) of the outer circle is also specified.
     * A paint instance is also used to permit the circle to be drawn with either
     * a colour tint and/or transparency.
     */
    private Bitmap mOuterCircleBitmap;
    private BoundingBox mOuterCircleBound = new BoundingBox();
    private float mOuterCircleSize;
    private Paint mOuterCirclePaint;

    /**
     * Inner circle details for the bitmap that is displayed to track movement of the
     * touch once the control has been triggered. The bound is assumed to be either
     * in screen or layer space depending on the value of the inherited
     * mProcessInLayerSpace flag. The diameter (Note - not the radius) of the inner
     * circle is also specified. A paint instance is also used to permit the circle
     * to be drawn with either a colour tint and/or transparency.
     */
    private Bitmap mInnerCircleBitmap;
    private BoundingBox mInnerCircleBound = new BoundingBox();
    private float mInnerCircleSize;
    private Paint mInnerCirclePaint;

    /**
     * Center position for an active triggered thumb control.
     */
    private Vector2 mCentrePosition = new Vector2();


    // /////////////////////////////////////////////////////////////////////////
    // Constructors
    // /////////////////////////////////////////////////////////////////////////

    /**
     * Create a new thumbstick control
     *
     * @param x                   Centre x location of the button
     * @param y                   Centre y location of the button
     * @param regionWidth         Width of the region within which the thumbstick can be triggered
     * @param regionHeight        Height of the region within which the thumbstick can be triggered
     * @param outerCircleSize     Diameter of the outer circle that is displayed when the control is triggered
     * @param innerCircleSize     Diameter of the inner circle that is displayed to track control movement once triggered
     * @param processInLayerSpace True if input and drawing for the control should be assumed to be in terms of a defined layer space, otherwise screen space is assumed
     * @param gameScreen          Game screen to which this control belongs
     */
    public ThumbStick(float x, float y, float regionWidth, float regionHeight,
                      float outerCircleSize, float innerCircleSize,
                      boolean processInLayerSpace, GameScreen gameScreen) {
        super(x, y, regionWidth, regionHeight, null,
                processInLayerSpace, gameScreen);

        // Load in the default ThumbStick assets
        gameScreen.getGame().getAssetManager().loadAndAddBitmap(
                "ThumbControlOuterCircle",
                "img/gage/thumbControl/ThumbControlOuterCircle.png");
        gameScreen.getGame().getAssetManager().loadAndAddBitmap(
                "ThumbControlInnerCircle",
                "img/gage/thumbControl/ThumbControlInnerCircle.png");

        // Setup the remaining control parameters
        setupThumbStick(gameScreen, outerCircleSize, innerCircleSize,
                "ThumbControlOuterCircle", "ThumbControlInnerCircle");
    }

    /**
     * Create a new thumbstick control
     *
     * @param x                   Centre x location of the button
     * @param y                   Centre y location of the button
     * @param regionWidth         Width of the region within which the thumbstick can be triggered
     * @param regionHeight        Height of the region within which the thumbstick can be triggered
     * @param outerCircleSize     Diameter of the outer circle that is displayed when the control is triggered
     * @param innerCircleSize     Diameter of the inner circle that is displayed to track control movement once triggered
     * @param outerCircleBitmap   Name of the bitmap to display as the outer circle
     * @param innerCircleBitmap   Name of the bitmap to display as the inner circle
     * @param processInLayerSpace True if input and drawing for the control should be assumed to be in terms of a defined layer space, otherwise screen space is assumed
     * @param gameScreen          Game screen to which this control belongs
     */
    public ThumbStick(float x, float y, float regionWidth, float regionHeight,
                      float outerCircleSize, float innerCircleSize,
                      String outerCircleBitmap, String innerCircleBitmap,
                      boolean processInLayerSpace, GameScreen gameScreen) {
        super(x, y, regionWidth, regionHeight, null,
                processInLayerSpace, gameScreen);

        // Setup the remaining control parameters
        setupThumbStick(gameScreen,
                outerCircleSize, innerCircleSize, outerCircleBitmap, innerCircleBitmap);
    }

    /**
     * Store/setup the outer and inner circle properties
     *
     * @param gameScreen        Game screen to which this control belongs
     * @param outerCircleSize   Diameter of the outer circle that is displayed when the control is triggered
     * @param innerCircleSize   Diameter of the inner circle that is displayed to track control movement once triggered
     * @param outerCircleBitmap Name of the bitmap to display as the outer circle
     * @param innerCircleBitmap Name of the bitmap to display as the inner circle
     */
    private void setupThumbStick(GameScreen gameScreen,
                                 float outerCircleSize, float innerCircleSize,
                                 String outerCircleBitmap, String innerCircleBitmap) {
        // Store the specified bitmaps
        mOuterCircleBitmap = gameScreen.getGame().getAssetManager().getBitmap(outerCircleBitmap);
        mInnerCircleBitmap = gameScreen.getGame().getAssetManager().getBitmap(innerCircleBitmap);

        // Store the size of the circles
        mOuterCircleSize = outerCircleSize;
        mInnerCircleSize = innerCircleSize;

        // Populate unchanged elements of the circle bounds
        mOuterCircleBound.halfHeight = mOuterCircleSize / 2.0f;
        mOuterCircleBound.halfWidth = mOuterCircleSize / 2.0f;

        mInnerCircleBound.halfHeight = mInnerCircleSize / 2.0f;
        mInnerCircleBound.halfWidth = mInnerCircleSize / 2.0f;

        // Create default paint instances for each circle
        mOuterCirclePaint = new Paint();
        mInnerCirclePaint = new Paint();
    }

    // /////////////////////////////////////////////////////////////////////////
    // Methods: Accessors
    // /////////////////////////////////////////////////////////////////////////

    /**
     * Define the alpha value for the outer circle when drawn
     *
     * @param alpha Alpha value (0-1)
     */
    public void setOuterCircleAlpha(float alpha) {
        mOuterCirclePaint.setAlpha((int) (255.0f * alpha));
    }

    /**
     * Define the alpha value for the inner circle when drawn
     *
     * @param alpha Alpha value (0-1)
     */
    public void setInnerCircleAlpha(float alpha) {
        mInnerCirclePaint.setAlpha((int) (255.0f * alpha));
    }

    /**
     * Define a colour value that will be multiplied against each
     * pixel when drawing the outer circle (e.g. 0xFFFFFFFF - white
     * will not result in any change, whilst, 0x5577FFFF will reduce
     * the alpha channel and the red channel values).
     *
     * @param colour Integer ARGB colour
     */
    public void setOuterCircleColour(int colour) {
        ColorFilter filter = new PorterDuffColorFilter(
                colour, PorterDuff.Mode.MULTIPLY);
        mOuterCirclePaint.setColorFilter(filter);
    }

    /**
     * Define a colour value that will be multiplied against each
     * pixel when drawing the inner circle (e.g. 0xFFFFFFFF - white
     * will not result in any change, whilst, 0x5577FFFF will reduce
     * the alpha channel and the red channel values).
     *
     * @param colour Integer ARGB colour
     */
    public void setInnerCircleColour(int colour) {
        ColorFilter filter = new PorterDuffColorFilter(
                colour, PorterDuff.Mode.MULTIPLY);
        mInnerCirclePaint.setColorFilter(filter);
    }

    // /////////////////////////////////////////////////////////////////////////
    // Methods: Update
    // /////////////////////////////////////////////////////////////////////////

    /**
     * Internal variable used to track the distance of the inner circle from the
     * centre of the trigger. Defined externally to reduce object creation.
     */
    private Vector2 innerCircleDistance = new Vector2();

    /**
     * Update the thumb stick control when a touch event occurs
     *
     * @param touchEvent    Touch event that gave rise to the trigger
     * @param touchLocation Touch location at which the trigger occurred
     */
    @Override
    protected void updateTriggerActions(TouchEvent touchEvent, Vector2 touchLocation) {

        // Check for the type of event that occurred
        if (touchEvent.type == TouchEvent.TOUCH_DOWN) {
            // When a touch down occurs set the location to the touch centre position
            mCentrePosition.set(touchLocation.x, touchLocation.y);

            // Update the inner and outer circle bounds
            mOuterCircleBound.x = mCentrePosition.x;
            mOuterCircleBound.y = mCentrePosition.y;
            mInnerCircleBound.x = mCentrePosition.x;
            mInnerCircleBound.y = mCentrePosition.y;

        } else if (touchEvent.type == TouchEvent.TOUCH_DRAGGED) {
            // If a drag event has occurred then update the inner circle location,
            // but make sure it cannot leave the confines of the outer cirle.

            // Determine the distance from the initial touch location
            innerCircleDistance.x = touchLocation.x - mCentrePosition.x;
            innerCircleDistance.y = touchLocation.y - mCentrePosition.y;

            // Check if the touch location will result in the inner circle,
            // leaving the confines of the outer circle.
            if (mOuterCircleSize * mOuterCircleSize / 4.0f
                    - innerCircleDistance.lengthSquared() < mInnerCircleSize * mInnerCircleSize) {
                // Reposition the inner circle to maintain the direction from the
                // centre but ensure it does not leave the outer circle.
                innerCircleDistance.normalise();
                innerCircleDistance.multiply(mOuterCircleSize / 2.0f - mInnerCircleSize / 2.0f);
                mInnerCircleBound.x = mCentrePosition.x + innerCircleDistance.x;
                mInnerCircleBound.y = mCentrePosition.y + innerCircleDistance.y;
            } else {
                // Update the location of the inner circle
                mInnerCircleBound.x = touchLocation.x;
                mInnerCircleBound.y = touchLocation.y;
            }
        }
    }

    /**
     * Consider touch points on the bottom.
     * <p>
     * Note: The ThumbStick behaviour is managed entirely through TouchEvents.
     *
     * @param touchLocation Touch location at which the trigger occurred
     */
    @Override
    protected void updateTouchActions(Vector2 touchLocation) {
    }

    /**
     * Consider actions for an untouched thumbstick.
     * <p>
     * Note: No default behaviours are currently defined for a thumbstick.
     */
    @Override
    protected void updateDefaultActions() {
    }


    // /////////////////////////////////////////////////////////////////////////
    // Methods: Control input reporting
    // /////////////////////////////////////////////////////////////////////////

    /**
     * Determine if the thumb control is currently touched and active
     *
     * @return True if the thumb control is touched, otherwise false
     */
    public boolean isTouched() {
        return mIsTouched;
    }

    /**
     * Get the magnitude along the x-direction for the thumb control.
     *
     * @return x magnitude (from -1.0 (full left) to +1.0 (full right))
     */
    public float getXMagnitude() {
        return mIsTouched ?
                (mTouchLocation.x - mCentrePosition.x) / (mOuterCircleSize / 2.0f) : 0.0f;
    }

    /**
     * Get the magnitude along the y-direction for the thumb control.
     *
     * @return y magnitude (from -1.0 (full down) to +1.0 (full top))
     */
    public float getYMagnitude() {
        return mIsTouched ?
                (mTouchLocation.y - mCentrePosition.y) / (mOuterCircleSize / 2.0f) : 0.0f;
    }

    /**
     * Get the magnitude (length) of the thumbstick activation
     *
     * @return Activation magnitude (1.0 full activation to 0.0 no activation)
     */
    public float getMagnitude() {
        if (mIsTouched) {
            float xDist = (mTouchLocation.x - mCentrePosition.x) / (mOuterCircleSize / 2.0f);
            float yDist = (mTouchLocation.y - mCentrePosition.y) / (mOuterCircleSize / 2.0f);
            return xDist * xDist + yDist * yDist;
        } else
            return 0.0f;
    }

    /**
     * Get a directional vector based on the current touch location and the initial
     * thumbstick activation position
     *
     * @param direction Directional vector
     */
    public void getDirection(Vector2 direction) {
        direction.x = (mTouchLocation.x - mCentrePosition.x) / (mOuterCircleSize / 2.0f);
        direction.y = (mTouchLocation.y - mCentrePosition.y) / (mOuterCircleSize / 2.0f);
    }


    // /////////////////////////////////////////////////////////////////////////
    // Methods: Draw
    // /////////////////////////////////////////////////////////////////////////

    /**
     * Draw the thumbstick control
     *
     * @param elapsedTime Elapsed time information
     * @param graphics2D  Graphics instance
     */
    @Override
    public void draw(ElapsedTime elapsedTime, IGraphics2D graphics2D) {
        if (mIsTouched) {
            // Draw the outer circle bitmap
            drawSourceRect.set(
                    0, 0, mOuterCircleBitmap.getWidth(), mOuterCircleBitmap.getHeight());
            drawScreenRect.set((int) (mOuterCircleBound.x - mOuterCircleBound.halfWidth),
                    (int) (mOuterCircleBound.y - mOuterCircleBound.halfHeight),
                    (int) (mOuterCircleBound.x + mOuterCircleBound.halfWidth),
                    (int) (mOuterCircleBound.y + mOuterCircleBound.halfHeight));
            graphics2D.drawBitmap(mOuterCircleBitmap, drawSourceRect, drawScreenRect, mOuterCirclePaint);

            // Draw the inner circle bitmap
            drawSourceRect.set(
                    0, 0, mInnerCircleBitmap.getWidth(), mInnerCircleBitmap.getHeight());
            drawScreenRect.set((int) (mInnerCircleBound.x - mInnerCircleBound.halfWidth),
                    (int) (mInnerCircleBound.y - mInnerCircleBound.halfHeight),
                    (int) (mInnerCircleBound.x + mInnerCircleBound.halfWidth),
                    (int) (mInnerCircleBound.y + mInnerCircleBound.halfHeight));
            graphics2D.drawBitmap(mInnerCircleBitmap, drawSourceRect, drawScreenRect, mInnerCirclePaint);
        }
    }

    /**
     * Draw the thumbstick control
     *
     * @param elapsedTime    Elapsed time information
     * @param graphics2D     Graphics instance
     * @param layerViewport  Layer viewport
     * @param screenViewport Screen viewport
     */
    @Override
    public void draw(ElapsedTime elapsedTime, IGraphics2D graphics2D,
                     LayerViewport layerViewport, ScreenViewport screenViewport) {
        if (mIsTouched) {
            if (mProcessInLayerSpace) {
                // If in layer space, draw the outer circle
                if (GraphicsHelper.getClippedSourceAndScreenRect(
                        mOuterCircleBound, mOuterCircleBitmap,
                        layerViewport, screenViewport,
                        drawSourceRect, drawScreenRect)) {
                    graphics2D.drawBitmap(mOuterCircleBitmap, drawSourceRect, drawScreenRect, mOuterCirclePaint);
                }

                // Draw the inner circle
                if (GraphicsHelper.getClippedSourceAndScreenRect(
                        mInnerCircleBound, mInnerCircleBitmap,
                        layerViewport, screenViewport,
                        drawSourceRect, drawScreenRect)) {
                    graphics2D.drawBitmap(mInnerCircleBitmap, drawSourceRect, drawScreenRect, mInnerCirclePaint);
                }
            } else {
                // Draw directly to the screen
                draw(elapsedTime, graphics2D);
            }
        }
    }
}
