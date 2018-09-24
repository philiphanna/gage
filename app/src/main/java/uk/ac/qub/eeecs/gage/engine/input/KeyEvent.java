package uk.ac.qub.eeecs.gage.engine.input;

/**
 * Key event.
 *
 * @version 1.0
 */
public class KeyEvent {

    /**
     * Key event constants
     */
    public static final int KEY_DOWN = 0;
    public static final int KEY_UP = 1;

    /**
     * Type of key event that has occurred (KEY_DOWN, KEY_UP)
     */
    public int type;

    /**
     * Unicode key code
     */
    public int keyCode;

    /**
     * Key character
     */
    public char keyChar;
}