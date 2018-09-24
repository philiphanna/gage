package uk.ac.qub.eeecs.gage.engine.input;

import android.content.Context;
import android.view.View;

import java.util.List;

/**
 * Touch, key, accelerometer and compass input support.
 *
 * @version 1.0
 */
public class Input {

    /**
     * Define the different handlers that are responsible for managing the
     * different types of input
     */
    private AccelerometerHandler mAccelHandler;
    private KeyHandler mKeyHandler;
    private TouchHandler mTouchHandler;
    private CompassHandler mCompassHandler;

    /**
     * Create a new input manager for the specified content view
     *
     * @param context Context within which this handler will operate
     * @param view    View that this handler will collect input from
     */
    public Input(Context context, View view) {
        mAccelHandler = new AccelerometerHandler(context);
        mCompassHandler = new CompassHandler(context);
        mKeyHandler = new KeyHandler(view);
        mTouchHandler = new GestureHandler(view);
    }

    // /////////////////////////////////////////////////////////////////////////
    // Touch Input Events //
    // /////////////////////////////////////////////////////////////////////////

    /**
     * Determine if there is an ongoing touch event for the specified pointer ID
     *
     * @param pointerId Touch pointer ID to test for
     * @return true if there is an ongoing touch event, otherwise false
     */
    public boolean existsTouch(int pointerId) { return mTouchHandler.existsTouch(pointerId); }

    /**
     * Get the x-coordinate for the specified pointer ID.
     * <p>
     * A value of Float.NaN is returned if the pointer ID does not exist
     *
     * @param pointerId Touch pointer ID to retrieve
     * @return x touch location
     */
    public float getTouchX(int pointerId) { return mTouchHandler.getTouchX(pointerId); }

    /**
     * Get the y-coordinate for the specified pointer ID.
     * <p>
     * A value of Float.NaN is returned if the pointer ID does not exist
     *
     * @param pointerId Touch pointer ID to retrieve
     * @return y touch location
     */
    public float getTouchY(int pointerId) { return mTouchHandler.getTouchY(pointerId); }

    /**
     * Return a list of captured touch events occurring for this update tick.
     *
     * @return List of captured touch events
     */
    public List<TouchEvent> getTouchEvents() { return mTouchHandler.getTouchEvents(); }

    // /////////////////////////////////////////////////////////////////////////
    // Accelerometer Input Events //
    // /////////////////////////////////////////////////////////////////////////

    /**
     * Return the current accelerative force along the x-axis
     *
     * @return Accelerative force along the x-axis
     */
    public float getAccelX() {
        return mAccelHandler.getAccelX();
    }

    /**
     * Return the current accelerative force along the y-axis
     *
     * @return Accelerative force along the y-axis
     */
    public float getAccelY() {
        return mAccelHandler.getAccelY();
    }

    /**
     * Return the current accelerative force along the z-axis
     *
     * @return Accelerative force along the z-axis
     */
    public float getAccelZ() {
        return mAccelHandler.getAccelZ();
    }

    // /////////////////////////////////////////////////////////////////////////
    // Compass Input Events //
    // /////////////////////////////////////////////////////////////////////////

    /**
     * Return the current compass bearing (azimuth)
     *
     * @return Compass bearing
     */
    public float getAzimuth() {
        return mCompassHandler.getAzimuth();
    }

    // /////////////////////////////////////////////////////////////////////////
    // Key Input Events //
    // /////////////////////////////////////////////////////////////////////////

    /**
     * Determine if the specific key is currently pressed
     *
     * @param keyCode Key code to test
     * @return Boolean true if the key is currently pressed, otherwise false
     */
    public boolean isKeyPressed(int keyCode) {
        return mKeyHandler.isKeyPressed(keyCode);
    }

    /**
     * Return a list of captured key events occurring for this update tick.
     *
     * @return List of captured touch events
     */
    public List<KeyEvent> getKeyEvents() {
        return mKeyHandler.getKeyEvents();
    }

    // /////////////////////////////////////////////////////////////////////////
    // Support Methods //
    // /////////////////////////////////////////////////////////////////////////

    /**
     * Reset the touch and key accumulators so that all touch and key events
     * accumulated since the last accumulator reset are now returned through the
     * getTouchEvents() and getKeyEvents method().
     * <p>
     * This method should be invoked once per update tick (ideally as part the
     * standard game loop's update actions).
     */
    public void resetAccumulators() {
        mTouchHandler.resetAccumulator();
        mKeyHandler.resetAccumulator();
    }
}
