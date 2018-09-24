package uk.ac.qub.eeecs.gage.engine.input;

import android.annotation.SuppressLint;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

import java.util.ArrayList;
import java.util.List;

import uk.ac.qub.eeecs.gage.util.Pool;

/**
 * Gesture handler
 *
 * @version 1.0
 */
public class GestureHandler extends TouchHandler
        implements GestureDetector.OnGestureListener {

    // /////////////////////////////////////////////////////////////////////////
    // Properties
    // /////////////////////////////////////////////////////////////////////////

    /**
     * Gesture detector instance used by this handler
     */
    private GestureDetector mGestureDetector;


    // /////////////////////////////////////////////////////////////////////////
    // Constructors
    // /////////////////////////////////////////////////////////////////////////

    /**
     * Create a new gesture handler instance for the specified view.
     *
     * @param view View whose touch events should be captured by this handler
     */
    public GestureHandler(View view) {

        // Setup the base touch handler and the gesture detector
        super();
        mGestureDetector = new GestureDetector(view.getContext(), this);

        view.setOnTouchListener(this);
    }

    // /////////////////////////////////////////////////////////////////////////
    // Methods: Touch Events
    // /////////////////////////////////////////////////////////////////////////

    /**
     * (non-Javadoc)
     *
     * @see OnTouchListener#onTouch(View,
     * MotionEvent)
     */
    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouch(View v, final MotionEvent event) {

        // Instruct the gesture detector to process the value
        boolean consumed = mGestureDetector.onTouchEvent(event);

        // If not consumed, then process as normal
        if (!consumed)
            super.onTouch(v, event);

        return true;
    }

    /**
     * Triggered when a tap down touch event occurs. Triggered at the start of
     * all other event types.
     *
     * @param motionEvent event details
     * @return true if the event is consumed, otherwise false
     */
    @Override
    public boolean onDown(MotionEvent motionEvent) {
        // No processing needed - handled by the base touch event handler
        return false;
    }

    /**
     * Triggered when a press has been detected but no movement has occurred.
     *
     * @param motionEvent event details
     */
    @Override
    public void onShowPress(MotionEvent motionEvent) {
        // Instantiate and add a show press event
        TouchEvent touchEvent = instantiateTouchEvent(motionEvent);
        touchEvent.type = TouchEvent.TOUCH_SHOW_PRESS;
        addTouchEvent(touchEvent);
    }

    /**
     * Triggered when a long press touch event occurs.
     *
     * @param motionEvent event details
     */
    @Override
    public void onLongPress(MotionEvent motionEvent) {
        // Instantiate and add a long press event
        TouchEvent touchEvent = instantiateTouchEvent(motionEvent);
        touchEvent.type = TouchEvent.TOUCH_LONG_PRESS;
        addTouchEvent(touchEvent);
    }

    /**
     * Triggered when a single tap up event has been detected (touch and release
     * without notable movement).
     *
     * @param motionEvent event details
     * @return true if the event is consumed, otherwise false
     */
    @Override
    public boolean onSingleTapUp(MotionEvent motionEvent) {
        // Instantiate and add a single tap up event
        TouchEvent touchEvent = instantiateTouchEvent(motionEvent);
        touchEvent.type = TouchEvent.TOUCH_SINGLE_TAP;
        addTouchEvent(touchEvent);

        // Returning false as the basic underlying event handling needs to run
        return false;
    }

    /**
     * Triggered if a scrolling touch event is detected.
     *
     * @param motionEvent1 Event which triggered the scroll
     * @param motionEvent2 Movement event which triggered this on scroll event
     * @param distanceX    x distance moved since the last time onScroll was called
     * @param distanceY    y distance moved since the last time onScroll was called
     * @return true if the event is consumed, otherwise false
     */
    @Override
    public boolean onScroll(MotionEvent motionEvent1, MotionEvent motionEvent2,
                            float distanceX, float distanceY) {
        // Instantiate and add a scroll event
        TouchEvent touchEvent = instantiateTouchEvent(motionEvent2);
        touchEvent.type = TouchEvent.TOUCH_SCROLL;
        touchEvent.dx = distanceX;
        touchEvent.dy = distanceY;
        addTouchEvent(touchEvent);

        // Returning false as the basic underlying event handling needs to run
        return false;
    }

    /**
     * Triggered if a fling touch event is detected.
     *
     * @param motionEvent1 Event which triggered the scroll
     * @param motionEvent2 Movement event which triggered this on scroll event
     * @param velocityX    velocity of the fling event along the x-axis
     * @param velocityY    velocity of the fling event along the y-axis
     * @return true if the event is consumed, otherwise false
     */
    @Override
    public boolean onFling(MotionEvent motionEvent1, MotionEvent motionEvent2,
                           float velocityX, float velocityY) {
        // Instantiate and add a fling event
        TouchEvent touchEvent = instantiateTouchEvent(motionEvent2);
        touchEvent.type = TouchEvent.TOUCH_FLING;
        touchEvent.dx = velocityX;
        touchEvent.dy = velocityY;
        addTouchEvent(touchEvent);

        // Returning false as the basic underlying event handling needs to run
        return false;
    }
}