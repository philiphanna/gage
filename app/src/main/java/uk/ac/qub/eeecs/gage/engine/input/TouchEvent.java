package uk.ac.qub.eeecs.gage.engine.input;

/**
 * Touch event
 *
 * @version 1.1
 */
public class TouchEvent {

    /**
     * Touch event constants
     */
    public static final int TOUCH_DOWN = 0;
    public static final int TOUCH_UP = 1;
    public static final int TOUCH_DRAGGED = 2;
    public static final int TOUCH_SHOW_PRESS = 3;
    public static final int TOUCH_LONG_PRESS = 4;
    public static final int TOUCH_SINGLE_TAP = 5;
    public static final int TOUCH_SCROLL = 6;
    public static final int TOUCH_FLING = 7;

    /**
     * Type of touch event that has occurred (TOUCH_DOWN, TOUCH_UP,
     * TOUCH_DRAGGED)
     */
    public int type;

    /**
     * Screen position (pixel) at which the touch event occurred.
     */
    public float x, y;

    /**
     * Additional event values. If TOUCH_SCROLL then dx, dy records the x and y
     * scroll movement. If TOUCH_FLING then dx, dy records the fling velocity.
     */
    public float dx, dy;

    /**
     * Pointer ID associated with this touch event
     */
    public int pointer;
}