package uk.ac.qub.eeecs.game.miscDemos;

import android.graphics.Color;
import android.graphics.Paint;

import java.util.Random;

import uk.ac.qub.eeecs.gage.Game;
import uk.ac.qub.eeecs.gage.engine.ElapsedTime;
import uk.ac.qub.eeecs.gage.engine.graphics.IGraphics2D;
import uk.ac.qub.eeecs.gage.ui.PushButton;
import uk.ac.qub.eeecs.gage.util.ViewportHelper;
import uk.ac.qub.eeecs.gage.world.GameScreen;

/**
 * Game screen instance used as part of the game screen demo. This class basically
 * holds and display a single integer value.
 *
 * @version 1.0
 */
public class GameScreenDemoSubScreen extends GameScreen {

    // /////////////////////////////////////////////////////////////////////////
    // Properties
    // /////////////////////////////////////////////////////////////////////////

    /**
     * Define the back button to return to the game screen demo
     */
    private PushButton mBackButton;

    /**
     * Define the value stored by this game screen. This value will be
     * updated by this game screen and will be reported back to the
     * parent game screen when requested.
     */
    private int mScreenValue = -1;

    /**
     * Define variables that control when the value maintained by this
     * game screen will change.
     */
    private static final float CHANGE_DELAY = 5.0f;
    private float mTimeToChange = 0;


    // /////////////////////////////////////////////////////////////////////////
    // Constructors
    // /////////////////////////////////////////////////////////////////////////

    /**
     * Create the game screen
     *
     * @param game Game to which this screen belongs
     */
    public GameScreenDemoSubScreen(String screenName, Game game) {
        super(screenName, game);

        // Create and position a small back button in the lower-right hand corner
        // of the screen. Also, enable click sounds on press/release interactions.
        mBackButton = new PushButton(
                mDefaultLayerViewport.getWidth() * 0.95f,
                mDefaultLayerViewport.getHeight() * 0.10f,
                mDefaultLayerViewport.getWidth() * 0.075f,
                mDefaultLayerViewport.getHeight() * 0.10f,
                "BackArrow", "BackArrowSelected", this);
        mBackButton.setPlaySounds(true, true);
    }


    // /////////////////////////////////////////////////////////////////////////
    // Methods
    // /////////////////////////////////////////////////////////////////////////

    /**
     * Return the value managed by this game screen. In a proper game this could be a
     * score variable or a user setting, etc.
     *
     * @return Value maintained by this class
     */
    public int getScreenValue() {
        return mScreenValue;
    }

    /**
     * Update the asset demo
     *
     * @param elapsedTime Elapsed time information
     */
    @Override
    public void update(ElapsedTime elapsedTime) {

        // Update the back button. If triggered then return to the demo menu.
        mBackButton.update(elapsedTime);
        if (mBackButton.isPushTriggered())
            mGame.getScreenManager().removeScreen(this);

        // If needed change the value managed by this class (a random number
        // between 0 and 999 will be selected).
        mTimeToChange += elapsedTime.stepTime;
        if (mTimeToChange >= 0.0f) {
            Random random = new Random();
            mScreenValue = random.nextInt(1000);
            mTimeToChange -= CHANGE_DELAY;
        }
    }

    /**
     * Internal paint variable, defined externally to reduce object creation costs
     */
    private Paint textPaint = new Paint();

    /**
     * Draw the menu screen
     *
     * @param elapsedTime Elapsed time information
     * @param graphics2D  Graphics instance
     */
    @Override
    public void draw(ElapsedTime elapsedTime, IGraphics2D graphics2D) {

        // Clear the screen and draw the buttons
        graphics2D.clear(Color.WHITE);

        // Determine font properties - created so a total of twenty
        // lines of text (0.05) could fit into the screen, aligned
        // along the x axis and drawn in black.

        float textSize =
                ViewportHelper.convertXDistanceFromLayerToScreen(
                        mDefaultLayerViewport.getHeight() * 0.05f,
                        mDefaultLayerViewport, mDefaultScreenViewport);
        textPaint.setTextSize(textSize);
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setColor(Color.BLACK);

        // Draw text displaying the name of this screen and relevant info

        graphics2D.drawText("Screen: [" +
                        this.getName() + "]", mDefaultScreenViewport.centerX(),
                mDefaultScreenViewport.top + 2.0f * textSize, textPaint);
        graphics2D.drawText("The value changes randomly every 5 seconds.",
                mDefaultScreenViewport.centerX(),
                mDefaultScreenViewport.centerY() + 6.0f * textSize, textPaint);
        graphics2D.drawText("Press the back arrow button to return to the demo menu",
                mDefaultScreenViewport.centerX(),
                mDefaultScreenViewport.centerY() + 7.0f * textSize, textPaint);

        // Update the paint instance to draw the larger text values.
        // Sized so two lines of text can be drawn in a dark grey colour.

        textSize =
                ViewportHelper.convertXDistanceFromLayerToScreen(
                        mDefaultLayerViewport.getHeight() * 0.5f,
                        mDefaultLayerViewport, mDefaultScreenViewport);

        textPaint.setTextSize(textSize);
        textPaint.setColor(Color.DKGRAY);

        // Draw the integer value managed by this game screen. Aside: the
        // intention is the value will be displayed in the middle of the
        // screen. The text will be centered along the x-axis but needs to be
        // offset on y axis to appeared center. In order to do this correctly,
        // the paint.getTextBounds() method should be used to determine the
        // size of the text and then this drawn centered.

        graphics2D.drawText(
                "" + mScreenValue,
                mDefaultScreenViewport.centerX(),
                mDefaultScreenViewport.centerY() * 1.2f, textPaint);

        // Draw the back button
        mBackButton.draw(elapsedTime, graphics2D,
                mDefaultLayerViewport, mDefaultScreenViewport);
    }
}