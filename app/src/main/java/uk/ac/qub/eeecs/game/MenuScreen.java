package uk.ac.qub.eeecs.game;

import android.graphics.Color;

import java.util.List;

import uk.ac.qub.eeecs.gage.Game;
import uk.ac.qub.eeecs.gage.engine.AssetManager;
import uk.ac.qub.eeecs.gage.engine.ElapsedTime;
import uk.ac.qub.eeecs.gage.engine.graphics.IGraphics2D;
import uk.ac.qub.eeecs.gage.engine.input.Input;
import uk.ac.qub.eeecs.gage.engine.input.TouchEvent;
import uk.ac.qub.eeecs.gage.ui.PushButton;
import uk.ac.qub.eeecs.gage.world.GameScreen;
import uk.ac.qub.eeecs.game.cardDemo.CardDemoScreen;
import uk.ac.qub.eeecs.game.miscDemos.DemoMenuScreen;
import uk.ac.qub.eeecs.game.platformDemo.PlatformDemoScreen;
import uk.ac.qub.eeecs.game.spaceDemo.SpaceshipDemoScreen;

/**
 * An exceedingly basic menu screen with a couple of touch buttons
 *
 * @version 1.0
 */
public class MenuScreen extends GameScreen {

    // /////////////////////////////////////////////////////////////////////////
    // Properties
    // /////////////////////////////////////////////////////////////////////////

    /**
     * Define the buttons for playing the 'games'
     */
    private PushButton mSpaceshipDemoButton;
    private PushButton mPlatformDemoButton;
    private PushButton mCardDemoButton;
    private PushButton mDemosButton;

    // /////////////////////////////////////////////////////////////////////////
    // Constructors
    // /////////////////////////////////////////////////////////////////////////

    /**
     * Create a simple menu screen
     *
     * @param game Game to which this screen belongs
     */
    public MenuScreen(Game game) {
        super("MenuScreen", game);

        // Load in the bitmaps used on the main menu screen
        AssetManager assetManager = mGame.getAssetManager();
        assetManager.loadAndAddBitmap("SpaceDemoIcon", "img/SpaceDemoIcon.png");
        assetManager.loadAndAddBitmap("SpaceDemoIconSelected", "img/SpaceDemoIconSelected.png");
        assetManager.loadAndAddBitmap("CardDemoIcon", "img/CardDemoIcon.png");
        assetManager.loadAndAddBitmap("CardDemoIconSelected", "img/CardDemoIconSelected.png");
        assetManager.loadAndAddBitmap("PlatformDemoIcon", "img/PlatformDemoIcon.png");
        assetManager.loadAndAddBitmap("PlatformDemoIconSelected", "img/PlatformDemoIconSelected.png");
        assetManager.loadAndAddBitmap("DemosIcon", "img/DemosIcon.png");
        assetManager.loadAndAddBitmap("DemosIconSelected", "img/DemosIconSelected.png");

        // Define the spacing that will be used to position the buttons
        int spacingX = (int)mDefaultLayerViewport.getWidth() / 5;
        int spacingY = (int)mDefaultLayerViewport.getHeight() / 3;

        // Create the trigger buttons
        mSpaceshipDemoButton = new PushButton(
                spacingX * 0.50f, spacingY * 1.5f, spacingX, spacingY,
                "SpaceDemoIcon", "SpaceDemoIconSelected",this);
        mSpaceshipDemoButton.setPlaySounds(true, true);
        mPlatformDemoButton = new PushButton(
                spacingX * 1.83f, spacingY * 1.5f, spacingX, spacingY,
                "PlatformDemoIcon", "PlatformDemoIconSelected", this);
        mPlatformDemoButton.setPlaySounds(true, true);
        mCardDemoButton = new PushButton(
                spacingX * 3.17f, spacingY * 1.5f, spacingX, spacingY,
                "CardDemoIcon", "CardDemoIconSelected", this);
        mCardDemoButton.setPlaySounds(true, true);
        mDemosButton = new PushButton(
                spacingX * 4.50f, spacingY * 1.5f, spacingX, spacingY,
                "DemosIcon", "DemosIconSelected", this);
        mDemosButton.setPlaySounds(true, true);
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

            // Update each button and transition if needed
            mSpaceshipDemoButton.update(elapsedTime);
            mCardDemoButton.update(elapsedTime);
            mPlatformDemoButton.update(elapsedTime);
            mDemosButton.update(elapsedTime);

            if (mSpaceshipDemoButton.isPushTriggered())
                mGame.getScreenManager().addScreen(new SpaceshipDemoScreen(mGame));
            else if (mCardDemoButton.isPushTriggered())
                mGame.getScreenManager().addScreen(new CardDemoScreen(mGame));
            else if (mPlatformDemoButton.isPushTriggered())
                mGame.getScreenManager().addScreen(new PlatformDemoScreen(mGame));
            else if (mDemosButton.isPushTriggered())
                mGame.getScreenManager().addScreen(new DemoMenuScreen(mGame));
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

        mSpaceshipDemoButton.draw(elapsedTime, graphics2D, mDefaultLayerViewport, mDefaultScreenViewport);
        mPlatformDemoButton.draw(elapsedTime, graphics2D, mDefaultLayerViewport, mDefaultScreenViewport);
        mDemosButton.draw(elapsedTime, graphics2D, mDefaultLayerViewport, mDefaultScreenViewport);
        mCardDemoButton.draw(elapsedTime, graphics2D, mDefaultLayerViewport, mDefaultScreenViewport);
    }
}
