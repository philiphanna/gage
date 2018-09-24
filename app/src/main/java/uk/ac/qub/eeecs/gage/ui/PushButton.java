package uk.ac.qub.eeecs.gage.ui;

import android.graphics.Bitmap;

import uk.ac.qub.eeecs.gage.engine.AssetManager;
import uk.ac.qub.eeecs.gage.engine.audio.Sound;
import uk.ac.qub.eeecs.gage.engine.input.TouchEvent;
import uk.ac.qub.eeecs.gage.util.Vector2;
import uk.ac.qub.eeecs.gage.world.GameScreen;

/**
 * Push button class. Providing controllable trigger events on button push
 * or button release.
 *
 * @version 1.0
 */
public class PushButton extends Button {

    // /////////////////////////////////////////////////////////////////////////
    // Properties: Button State and Trigger
    // /////////////////////////////////////////////////////////////////////////

    /**
     * Define the available button states
     */
    protected enum ButtonState {
        DEFAULT, PUSHED
    }

    /**
     * Store the current button state
     */
    protected ButtonState mButtonState = ButtonState.DEFAULT;

    /**
     * Determine if this button is triggered on a touch press or a touch release
     */
    protected boolean mTriggerOnRelease = true;

    /**
     * Private variable used to store an unconsumed trigger event
     */
    private boolean mPushTriggered;

    // /////////////////////////////////////////////////////////////////////////
    // Properties: Button Appearance and Sound
    // /////////////////////////////////////////////////////////////////////////

    /**
     * Graphical asset used to represent the default button state
     */
    protected Bitmap mDefaultBitmap;

    /**
     * Graphical asset used to represent the pushed button state
     */
    protected Bitmap mPushBitmap;


    // /////////////////////////////////////////////////////////////////////////
    // Properties: Button Sound
    // /////////////////////////////////////////////////////////////////////////

    /**
     * Determine if push sound effects are played for this button
     */
    protected boolean mPlayPushSound = false;

    /**
     * Name of the sound asset to be played whenever the button is pushed
     */
    protected Sound mPushSound;

    /**
     * Determine if release sound effects are played for this button
     */
    protected boolean mPlayReleaseSound = false;

    /**
     * Name of the sound asset to be played whenever the button is released
     */
    protected Sound mReleaseSound;


    // /////////////////////////////////////////////////////////////////////////
    // Constructors
    // /////////////////////////////////////////////////////////////////////////

    /**
     * Create a new push button.
     *
     * @param x                   Centre x location of the button
     * @param y                   Centre y location of the button
     * @param width               Width of the button
     * @param height              Height of the button
     * @param defaultBitmap       Bitmap used to represent this control
     * @param pushBitmap          Bitmap used to represent this control
     * @param pushSound           Sound to play once the button is pushed
     * @param releaseSound        Sound to play once the button is released
     * @param processInLayerSpace Specify if the button is to be processed in layer
     *                            space (screen by default)
     * @param gameScreen          Gamescreen to which this control belongs
     */
    public PushButton(float x, float y, float width, float height,
                      String defaultBitmap, String pushBitmap,
                      String pushSound, String releaseSound,
                      boolean processInLayerSpace, GameScreen gameScreen) {
        super(x, y, width, height, defaultBitmap, processInLayerSpace, gameScreen);

        // Setup images and sound for the button
        setupImages(defaultBitmap, pushBitmap);
        setupSounds(pushSound, releaseSound);
    }

    /**
     * Create a new push button.
     *
     * @param x                   Centre x location of the button
     * @param y                   Centre y location of the button
     * @param width               Width of the button
     * @param height              Height of the button
     * @param defaultBitmap       Bitmap used to represent this control
     * @param pushBitmap          Bitmap used to represent this control
     * @param pushSound           Sound to play once the button is pushed
     * @param releaseSound        Sound to play once the button is released
     * @param gameScreen          Gamescreen to which this control belongs
     */
    public PushButton(float x, float y, float width, float height,
                      String defaultBitmap, String pushBitmap,
                      String pushSound, String releaseSound,
                      GameScreen gameScreen) {
        this(x, y, width, height, defaultBitmap, pushBitmap,
                pushSound, releaseSound, true, gameScreen);
    }

    /**
     * Create a new push button.
     *
     * @param x          Centre y location of the button
     * @param y          Centre x location of the button
     * @param width      Width of the button
     * @param height     Height of the button
     * @param bitmap     Bitmap used to represent this control
     * @param gameScreen Gamescreen to which this control belongs
     */
    public PushButton(float x, float y, float width, float height,
                      String bitmap, GameScreen gameScreen) {
        this(x, y, width, height, bitmap, null,
                null, null, true, gameScreen);
    }

    /**
     * Create a new push button.
     *
     * @param x             Centre y location of the button
     * @param y             Centre x location of the button
     * @param width         Width of the button
     * @param height        Height of the button
     * @param defaultBitmap Bitmap used to represent this control
     * @param pushBitmap    Bitmap used to represent this control
     * @param gameScreen    Gamescreen to which this control belongs
     */
    public PushButton(float x, float y, float width, float height,
                      String defaultBitmap, String pushBitmap, GameScreen gameScreen) {
        this(x, y, width, height, defaultBitmap, pushBitmap,
                null, null, true, gameScreen);
    }


    /**
     * Setup images for this button
     *
     * @param defaultBitmap Default button image
     * @param pushBitmap Pushed button image
     */
    private void setupImages(String defaultBitmap, String pushBitmap) {
        // Retrieve the assets used by this button
        AssetManager assetManager=
                mGameScreen.getGame().getAssetManager();
        mDefaultBitmap = assetManager.getBitmap(defaultBitmap);
        mPushBitmap = (pushBitmap == null)
                ? mDefaultBitmap : assetManager.getBitmap(pushBitmap);
    }

    /**
     * Setup sound for this button
     *
     * @param pushSound Push sound to use (can be null)
     * @param releaseSound Release sound to use (can be null)
     */
    private void setupSounds(String pushSound, String releaseSound) {
        // Load in the default button sounds
        AssetManager assetManager = mGameScreen.getGame().getAssetManager();
        assetManager.loadAndAddSound(
                "ButtonDefaultPush",
                "sound/gage/button/ButtonPush.wav");
        assetManager.loadAndAddSound(
                "ButtonDefaultRelease",
                "sound/gage/button/ButtonRelease.wav");

        // Set appropriate sound
        mPushSound = assetManager.getSound(
                (pushSound == null) ? "ButtonDefaultPush" : pushSound);
        mReleaseSound = assetManager.getSound(
                (releaseSound == null) ? "ButtonDefaultRelease" : releaseSound);

        // If specific sounds have been specified then assume sounds are to be played
        mPlayPushSound = pushSound != null;
        mPlayReleaseSound = releaseSound != null;
    }


    // /////////////////////////////////////////////////////////////////////////
    // Methods
    // /////////////////////////////////////////////////////////////////////////

    /**
     * Determine if push and/or release sounds should be played
     *
     * @param playPushSound True if a push sound effect should be played
     * @param playReleaseSound True if a release sound effect should be played
     */
    public void setPlaySounds(boolean playPushSound, boolean playReleaseSound) {
        mPlayPushSound = playPushSound;
        mPlayReleaseSound = playReleaseSound;
    }

    /**
     * Specific if the button trigger should occur on a push down or push up
     * touch event.
     *
     * @param triggerOnRelease True if the trigger should occur on button touch release
     */
    public void triggerOnRelease(boolean triggerOnRelease) {
        mTriggerOnRelease = triggerOnRelease;
    }

    /**
     * Check for and undertake trigger actions for the button.
     * <p>
     * If triggered play a sound and record that a button click has been recorded
     *
     * @param touchEvent    Touch event that gave rise to the trigger
     * @param touchLocation Touch location at which the trigger occurred
     */
    @Override
    protected void updateTriggerActions(TouchEvent touchEvent, Vector2 touchLocation) {
        // Play an appropriate sound
        if(touchEvent.type == TouchEvent.TOUCH_DOWN && mPlayPushSound)
            mPushSound.play();
        else if(touchEvent.type == TouchEvent.TOUCH_UP && mPlayReleaseSound)
            mReleaseSound.play();

        // Trigger if the appropriate touch up or touch down has occurred
        if ((!mTriggerOnRelease && touchEvent.type == TouchEvent.TOUCH_DOWN) ||
                (mTriggerOnRelease && touchEvent.type == TouchEvent.TOUCH_UP)) {
            // Record the trigger
            mPushTriggered = true;
        }
    }

    /**
     * Undertake touch actions for the button, updating the state
     * and bitmap.
     *
     * @param touchLocation Touch location at which the trigger occurred
     */
    @Override
    protected void updateTouchActions(Vector2 touchLocation) {
        mBitmap = mPushBitmap;
        mButtonState = ButtonState.PUSHED;
    }

    /**
     * Undertake default actions for the untouched button, reverting
     * to the default bitmap and state.
     */
    @Override
    protected void updateDefaultActions() {
        mBitmap = mDefaultBitmap;
        mButtonState = ButtonState.DEFAULT;
    }

    /**
     * Determine if there is an unprocessed trigger event. Once returned
     * the trigger event will be consumed.
     *
     * @return True if there is an unconsumed trigger event for
     * this button, otherwise False.
     */
    public boolean isPushTriggered() {
        if (mPushTriggered) {
            mPushTriggered = false;
            return true;
        }
        return false;
    }

    /**
     * Determine if the button is currently pushed.
     *
     * @return True if the button is pushed, otherwise False.
     */
    public boolean isPushed() {
        return mButtonState == ButtonState.PUSHED;
    }
}
