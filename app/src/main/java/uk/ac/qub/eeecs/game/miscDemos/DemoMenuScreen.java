package uk.ac.qub.eeecs.game.miscDemos;

import android.graphics.Color;

import java.util.ArrayList;
import java.util.List;

import uk.ac.qub.eeecs.gage.Game;
import uk.ac.qub.eeecs.gage.engine.ElapsedTime;
import uk.ac.qub.eeecs.gage.engine.graphics.IGraphics2D;
import uk.ac.qub.eeecs.gage.engine.input.Input;
import uk.ac.qub.eeecs.gage.engine.input.TouchEvent;
import uk.ac.qub.eeecs.gage.ui.PushButton;
import uk.ac.qub.eeecs.gage.world.GameScreen;

/**
 * Collection of linked demos
 *
 * @version 1.0
 */
public class DemoMenuScreen extends GameScreen {

    // /////////////////////////////////////////////////////////////////////////
    // Properties
    // /////////////////////////////////////////////////////////////////////////

    /**
     * Define the collection of buttons to trigger the linked demos
     */
    private PushButton mInputDemoButton;
    private PushButton mAssetDemoButton;
    private PushButton mScreenDemoButton;
    private PushButton mGameObjectDemoButton;
    private PushButton mViewportDemoButton;
    private PushButton mAnimationDemoButton;

    /**
     * Define the back button to return to the main menu
     */
    private PushButton mBackButton;

    /**
     * Provide a list to hold the buttons to provide an convenient
     * means of updating and drawing each button
     */
    private List<PushButton> mButtons = new ArrayList<>();


    // /////////////////////////////////////////////////////////////////////////
    // Constructors
    // /////////////////////////////////////////////////////////////////////////

    /**
     * Create the demos menu screen
     *
     * @param game Game to which this screen belongs
     */
    public DemoMenuScreen(Game game) {
        super("DemoMenuScreen", game);

        // Load in the set of bitmaps used for the buttons on the demo screen
        mGame.getAssetManager().loadAssets("txt/assets/DemoScreenAssets.JSON");

        // Define spacing for a 5x3 grid that will be used to size and position the buttons
        float spacingX = mDefaultLayerViewport.getWidth() / 5.0f;
        float spacingY = mDefaultLayerViewport.getHeight() / 3.5f;

        // Create the buttons
        mInputDemoButton = new PushButton(
                spacingX * 1.0f, spacingY * 2.5f, spacingX, spacingY,
                "InputDemoIcon", "InputDemoIconSelected", this);
        mButtons.add(mInputDemoButton);

        mAssetDemoButton = new PushButton(
                spacingX * 2.5f, spacingY * 2.5f, spacingX, spacingY,
                "AssetDemoIcon", "AssetDemoIconSelected", this);
        mButtons.add(mAssetDemoButton);

        mScreenDemoButton = new PushButton(
                spacingX * 4.0f, spacingY * 2.5f, spacingX, spacingY,
                "ScreenDemoIcon", "ScreenDemoIconSelected", this);
        mButtons.add(mScreenDemoButton);

        mGameObjectDemoButton = new PushButton(
                spacingX * 1.0f, spacingY * 1.0f, spacingX, spacingY,
                "GameObjectDemoIcon", "GameObjectDemoIconSelected", this);
        mButtons.add(mGameObjectDemoButton);

        mViewportDemoButton = new PushButton(
                spacingX * 2.5f, spacingY * 1.0f, spacingX, spacingY,
                "ViewportDemoIcon", "ViewportDemoIconSelected", this);
        mButtons.add(mViewportDemoButton);

        mAnimationDemoButton = new PushButton(
                spacingX * 4.0f, spacingY * 1.0f, spacingX, spacingY,
                "AnimationDemoIcon", "AnimationDemoIconSelected", this);
        mButtons.add(mAnimationDemoButton);

        mBackButton = new PushButton(
                mDefaultLayerViewport.getWidth() * 0.95f, mDefaultLayerViewport.getHeight() * 0.10f,
                mDefaultLayerViewport.getWidth() * 0.075f, mDefaultLayerViewport.getHeight() * 0.10f,
                "BackArrow", "BackArrowSelected", this);
        mButtons.add(mBackButton);

        // Ensure click sounds are played for all created buttons
        for (PushButton button : mButtons)
            button.setPlaySounds(true, true);
    }

    // /////////////////////////////////////////////////////////////////////////
    // Methods
    // /////////////////////////////////////////////////////////////////////////

    /**
     * Update the menu screen
     *
     * @param elapsedTime Elapsed time information
     */
    @Override
    public void update(ElapsedTime elapsedTime) {

        // Process any touch events occurring since the update
        Input input = mGame.getInput();

        List<TouchEvent> touchEvents = input.getTouchEvents();
        if (touchEvents.size() > 0) {

            // Update each button
            for (PushButton button : mButtons)
                button.update(elapsedTime);

            // Trigger a clicked demo if required
            if (mInputDemoButton.isPushTriggered())
                mGame.getScreenManager().addScreen(new InputDemoScreen(mGame));
            else if (mAssetDemoButton.isPushTriggered())
                mGame.getScreenManager().addScreen(new AssetDemoScreen(mGame));
            else if (mScreenDemoButton.isPushTriggered())
                mGame.getScreenManager().addScreen(new GameScreenDemoScreen(mGame));
            else if (mGameObjectDemoButton.isPushTriggered())
                mGame.getScreenManager().addScreen(new GameObjectDemoScreen(mGame));
            else if (mViewportDemoButton.isPushTriggered())
                mGame.getScreenManager().addScreen(new ViewportDemoScreen(mGame));
            else if (mAnimationDemoButton.isPushTriggered())
                mGame.getScreenManager().addScreen(new AnimationDemoScreen(mGame));

            // Finally consider if the back button has been pressed
            else if (mBackButton.isPushTriggered()) {
                mGame.getScreenManager().removeScreen(this);
            }
        }
    }

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

        // Draw each button
        for (PushButton button : mButtons)
            button.draw(elapsedTime, graphics2D, mDefaultLayerViewport, mDefaultScreenViewport);
    }
}
