package uk.ac.qub.eeecs.gage.ui;

import android.graphics.Bitmap;

import uk.ac.qub.eeecs.gage.engine.AssetManager;
import uk.ac.qub.eeecs.gage.engine.audio.Sound;
import uk.ac.qub.eeecs.gage.engine.input.TouchEvent;
import uk.ac.qub.eeecs.gage.util.Vector2;
import uk.ac.qub.eeecs.gage.world.GameScreen;

/**
 * Toggle button class. Providing a simple toggle button with an on/off state.
 *
 * @version 1.0
 */
public class ToggleButton extends Button {

    // /////////////////////////////////////////////////////////////////////////
    // Properties: Button State
    // /////////////////////////////////////////////////////////////////////////

    /**
     * Define the available button states
     */
    protected enum ButtonState {
        ON, OFF
    }

    /**
     * Store the current button state
     */
    protected ButtonState mButtonState = ButtonState.OFF;

    // /////////////////////////////////////////////////////////////////////////
    // Properties: Button Appearance and Sound
    // /////////////////////////////////////////////////////////////////////////

    /**
     * Graphical asset used to represent the off button state
     */
    protected Bitmap mOffBitmap;

    /**
     * Graphical asset used to represent the off button touch state
     */
    protected Bitmap mOffTouchBitmap;

    /**
     * Graphical asset used to represent the on button state
     */
    protected Bitmap mOnBitmap;

    /**
     * Graphical asset used to represent the on button touch state
     */
    protected Bitmap mOnTouchBitmap;

    /**
     * Name of the sound asset to be played when the button enters the on state
     */
    protected Sound mOnTriggerSound;

    /**
     * Name of the sound asset to be played when the button enters the off state
     */
    protected Sound mOffTriggerSound;

    // /////////////////////////////////////////////////////////////////////////
    // Constructors
    // /////////////////////////////////////////////////////////////////////////


    /**
     * Create a new toggle button.
     *
     * @param x                   Centre x location of the button
     * @param y                   Centre y location of the button
     * @param width               Width of the button
     * @param height              Height of the button
     * @param offBitmap           Off button state bitmap
     * @param offTouchBitmap      Off button touch state bitmap
     * @param onBitmap            On button state bitmap
     * @param onTouchBitmap       On button touch state bitmap
     * @param onTriggerSound      Sound to play when the button enters the on state
     * @param offTriggerSound     Sound to play when the button enters the off state
     * @param processInLayerSpace Specify if the button is to be processed in layer
     *                            space (screen by default)
     * @param gameScreen          Gamescreen to which this control belongs
     */
    public ToggleButton(float x, float y, float width, float height,
                        String offBitmap, String offTouchBitmap,
                        String onBitmap, String onTouchBitmap,
                        String onTriggerSound, String offTriggerSound,
                        boolean processInLayerSpace, GameScreen gameScreen) {
        super(x, y, width, height, offBitmap, processInLayerSpace, gameScreen);

        // Retrieve the assets used by this button
        AssetManager assetManager = gameScreen.getGame().getAssetManager();
        mOffBitmap = assetManager.getBitmap(offBitmap);
        mOffTouchBitmap = (offTouchBitmap == null)
                ? null : assetManager.getBitmap(offTouchBitmap);
        mOnBitmap = assetManager.getBitmap(onBitmap);
        mOnTouchBitmap = (onTouchBitmap == null)
                ? null : assetManager.getBitmap(onTouchBitmap);
        mOnTriggerSound = (onTriggerSound == null)
                ? null : assetManager.getSound(onTriggerSound);
        mOffTriggerSound = (offTriggerSound == null)
                ? null : assetManager.getSound(offTriggerSound);
    }

    /**
     * Create a new toggle button.
     *
     * @param x              Centre y location of the button
     * @param y              Centre x location of the button
     * @param width          Width of the button
     * @param height         Height of the button
     * @param offBitmap      Off button state bitmap
     * @param offTouchBitmap Off button touch state bitmap
     * @param onBitmap       On button state bitmap
     * @param onTouchBitmap  On button touch state bitmap
     * @param gameScreen     Gamescreen to which this control belongs
     */
    public ToggleButton(float x, float y, float width, float height,
                        String offBitmap,
                        String offTouchBitmap,
                        String onBitmap,
                        String onTouchBitmap,
                        GameScreen gameScreen) {
        this(x, y, width, height, offBitmap, offTouchBitmap,
                onBitmap, onTouchBitmap, null, null, true, gameScreen);
    }

    /**
     * Create a new toggle button.
     *
     * @param x          Centre y location of the button
     * @param y          Centre x location of the button
     * @param width      Width of the button
     * @param height     Height of the button
     * @param offBitmap  Off button state bitmap
     * @param onBitmap   On button state bitmap
     * @param gameScreen Gamescreen to which this control belongs
     */
    public ToggleButton(float x, float y, float width, float height,
                        String offBitmap,
                        String onBitmap,
                        GameScreen gameScreen) {
        this(x, y, width, height, offBitmap, null, onBitmap, null,
                null, null, true, gameScreen);
    }

    // /////////////////////////////////////////////////////////////////////////
    // Methods
    // /////////////////////////////////////////////////////////////////////////

    /**
     * Check for and undertake trigger actions for the button.
     * <p>
     * If triggered play an appropriate sound and change the state
     *
     * @param touchEvent    Touch event that gave rise to the trigger
     * @param touchLocation Touch location at which the trigger occurred
     */
    protected void updateTriggerActions(TouchEvent touchEvent, Vector2 touchLocation) {
        if (touchEvent.type == TouchEvent.TOUCH_UP) {
            // A touch up has occurred on this control
            if (mButtonState == ButtonState.OFF) {
                setToggled(true);
                if (mOnTriggerSound != null)
                    mOnTriggerSound.play();
            } else {
                setToggled(false);
                if (mOffTriggerSound != null)
                    mOffTriggerSound.play();
            }
        }
    }

    /**
     * Undertake touch actions for the button, updating the state
     * and bitmap.
     *
     * @param touchLocation Touch location at which the trigger occurred
     */
    protected void updateTouchActions(Vector2 touchLocation) {
        if (mButtonState == ButtonState.ON) {
            mBitmap = mOnTouchBitmap != null
                    ? mOnTouchBitmap : mOnBitmap;
        } else {
            mBitmap = mOffTouchBitmap != null
                    ? mOffTouchBitmap : mOffBitmap;
        }
    }

    /**
     * Undertake default actions for the untouched button, reverting
     * to the un-touched button state bitmap.
     */
    protected void updateDefaultActions() {
        mBitmap = mButtonState == ButtonState.ON
                ? mOnBitmap : mOffBitmap;
    }

    /**
     * Determine if the button is in the On state.
     *
     * @return True if in the On state, False if in the Off state
     */
    public boolean isToggledOn() {
        return mButtonState == ButtonState.ON;
    }

    /**
     * Set the button state.
     *
     * @param on True if the button is in the On state, false for the
     *           Off state
     */
    public void setToggled(boolean on) {
        if (on) {
            mButtonState = ButtonState.ON;
            mBitmap = mOnBitmap;
        } else {
            mButtonState = ButtonState.OFF;
            mBitmap = mOffBitmap;
        }
    }
}
