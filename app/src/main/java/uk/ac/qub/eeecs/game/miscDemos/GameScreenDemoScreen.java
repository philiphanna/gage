package uk.ac.qub.eeecs.game.miscDemos;

import android.graphics.Color;
import android.graphics.Paint;

import uk.ac.qub.eeecs.gage.Game;
import uk.ac.qub.eeecs.gage.engine.AssetManager;
import uk.ac.qub.eeecs.gage.engine.ElapsedTime;
import uk.ac.qub.eeecs.gage.engine.graphics.IGraphics2D;
import uk.ac.qub.eeecs.gage.ui.PushButton;
import uk.ac.qub.eeecs.gage.util.ViewportHelper;
import uk.ac.qub.eeecs.gage.world.GameScreen;

/**
 * GameScreen demo showing how transitions and information sharing between game
 * screens can be managed.
 *
 * @version 1.0
 */
public class GameScreenDemoScreen extends GameScreen {

    // /////////////////////////////////////////////////////////////////////////
    // Properties
    // /////////////////////////////////////////////////////////////////////////

    /**
     * Define the back button to return to the demo menu
     */
    private PushButton mBackButton;

    /**
     * This demo uses two other linked game screens by way of demonstrating
     * how screens can be changed and information shared. Alongside this,
     * two buttons are defined that will trigger a screen transition.
     */

    private GameScreenDemoSubScreen mLeftScreen;
    private GameScreenDemoSubScreen mRightScreen;

    private PushButton mLeftScreenTrigger;
    private PushButton mRightScreenTrigger;


    // /////////////////////////////////////////////////////////////////////////
    // Constructors
    // /////////////////////////////////////////////////////////////////////////

    /**
     * Create the game screen demo
     *
     * @param game Game to which this screen belongs
     */
    public GameScreenDemoScreen(Game game) {
        super("GameScreenDemoScreen", game);

        // Get the layer width and height to control the positioning of controls, etc.
        float layerWidth = mDefaultLayerViewport.getWidth();
        float layerHeight = mDefaultLayerViewport.getHeight();

        // Create and position a small back button in the lower-right hand corner
        // of the screen. Also, enable click sounds on press/release interactions.
        mBackButton = new PushButton(
                layerWidth * 0.95f, layerHeight * 0.10f,
                layerWidth * 0.075f, layerHeight * 0.10f,
                "BackArrow", "BackArrowSelected", this);
        mBackButton.setPlaySounds(true, true);

        // Load in the assets used by this particular demo.
        AssetManager assetManager = game.getAssetManager();
        assetManager.loadAndAddBitmap("LeftArrow", "img/LeftArrow.png");
        assetManager.loadAndAddBitmap("LeftArrowSelected", "img/LeftArrowSelected.png");
        assetManager.loadAndAddBitmap("RightArrow", "img/RightArrow.png");
        assetManager.loadAndAddBitmap("RightArrowSelected", "img/RightArrowSelected.png");

        // Create left and right push buttons that will be used to change the game screen
        mLeftScreenTrigger = new PushButton(
                layerWidth * 0.30f, layerHeight * 0.57f,
                layerWidth * 0.15f, layerHeight * 0.2f,
                "LeftArrow", "LeftArrowSelected", this);
        mRightScreenTrigger = new PushButton(
                layerWidth * 0.70f, layerHeight * 0.57f,
                layerWidth * 0.15f, layerHeight * 0.2f,
                "RightArrow", "RightArrowSelected", this);

        // Finally, create the game screens instances that this demo will use
        mLeftScreen = new GameScreenDemoSubScreen("LeftScreen", game);
        mRightScreen = new GameScreenDemoSubScreen("RightScreen", game);
    }


    // /////////////////////////////////////////////////////////////////////////
    // Methods
    // /////////////////////////////////////////////////////////////////////////

    /**
     * Update the game screen demo
     *
     * @param elapsedTime Elapsed time information
     */
    @Override
    public void update(ElapsedTime elapsedTime) {

        // Update the back button. If triggered then return to the demo menu.
        mBackButton.update(elapsedTime);
        if (mBackButton.isPushTriggered())
            mGame.getScreenManager().removeScreen(this);

        // Update the left and right buttons. If either are triggered then
        // add in the relevant game screen to the game's screen manager. This
        // means the added screen will become the new active screen and be asked
        // to process the next update and draw requests. This game screen
        // will still remain on the stack of screens managed by the screen
        // manager and will become active whenever the added game screen is
        // eventually popped (removed).

        mLeftScreenTrigger.update(elapsedTime);
        if (mLeftScreenTrigger.isPushTriggered()) {
            mGame.getScreenManager().addScreen(mLeftScreen);
        }

        // Check for the right screen trigger
        mRightScreenTrigger.update(elapsedTime);
        if (mRightScreenTrigger.isPushTriggered()) {
            mGame.getScreenManager().addScreen(mRightScreen);
        }
    }

    /**
     * Define a internal paint instance - declared externally to avoid object
     * creation costs.
     */
    private Paint textPaint = new Paint();

    /**
     * Draw the game screen
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

        // Draw text displaying the name of this screen and some instructions

        graphics2D.drawText("Screen: [" + this.getName() + "]",
                mDefaultScreenViewport.centerX(),
                mDefaultScreenViewport.top + 2.0f * textSize, textPaint);
        graphics2D.drawText("Use arrow buttons to change screen",
                mDefaultScreenViewport.centerX(),
                mDefaultScreenViewport.centerY() + 6.0f * textSize, textPaint);
        graphics2D.drawText("Shown values extracted from the relevant game screen",
                mDefaultScreenViewport.centerX(),
                mDefaultScreenViewport.centerY() + 7.0f * textSize, textPaint);

        // Update the paint instance to draw the larger text values.
        // Sized so fives lines of text can be drawn in a dark grey colour.

        textSize =
                ViewportHelper.convertXDistanceFromLayerToScreen(
                        mDefaultLayerViewport.getHeight() * 0.2f,
                        mDefaultLayerViewport, mDefaultScreenViewport);
        textPaint.setTextSize(textSize);
        textPaint.setColor(Color.DKGRAY);

        // Draw the left and right screen values. The values drawn depend
        // on the values stored in each game screen. The code assumes that
        // if the game screen has yet to update its value then -1 will be
        // return from the getScreenValue method.

        textPaint.setTextAlign(Paint.Align.LEFT);
        graphics2D.drawText(
                mLeftScreen.getScreenValue() == -1 ? "NA" : "" + mLeftScreen.getScreenValue(),
                mDefaultScreenViewport.left,
                mDefaultScreenViewport.centerY(), textPaint);
        textPaint.setTextAlign(Paint.Align.RIGHT);
        graphics2D.drawText(
                mRightScreen.getScreenValue() == -1 ? "NA" : "" + mRightScreen.getScreenValue(),
                mDefaultScreenViewport.right,
                mDefaultScreenViewport.centerY(), textPaint);

        // Draw the left/right screen trigger buttons
        mLeftScreenTrigger.draw(elapsedTime, graphics2D,
                mDefaultLayerViewport, mDefaultScreenViewport);
        mRightScreenTrigger.draw(elapsedTime, graphics2D,
                mDefaultLayerViewport, mDefaultScreenViewport);

        // Draw the back button
        mBackButton.draw(elapsedTime, graphics2D,
                mDefaultLayerViewport, mDefaultScreenViewport);
    }
}
