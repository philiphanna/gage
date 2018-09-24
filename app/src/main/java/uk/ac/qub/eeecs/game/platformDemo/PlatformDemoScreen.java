package uk.ac.qub.eeecs.game.platformDemo;

import android.graphics.Color;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import uk.ac.qub.eeecs.gage.Game;
import uk.ac.qub.eeecs.gage.engine.ElapsedTime;
import uk.ac.qub.eeecs.gage.engine.graphics.IGraphics2D;
import uk.ac.qub.eeecs.gage.ui.PushButton;
import uk.ac.qub.eeecs.gage.util.BoundingBox;
import uk.ac.qub.eeecs.gage.world.GameScreen;
import uk.ac.qub.eeecs.gage.world.LayerViewport;

/**
 * A simple platform-style demo that generates a number of platforms and
 * provides a player controlled entity that can move about the images.
 * <p>
 * Illustrates button based user input, animations and collision handling.
 *
 * Note: See the course documentation for extension/refactoring stories
 * for this class.
 *
 * @version 1.0
 */
public class PlatformDemoScreen extends GameScreen {

    // /////////////////////////////////////////////////////////////////////////
    // Properties
    // /////////////////////////////////////////////////////////////////////////

    /**
     * Define the width and height of the game world
     */
    private final float LEVEL_WIDTH = 2000.0f;
    private final float LEVEL_HEIGHT = 320.0f;

    /**
     * Define the layer viewport used to display the platforms
     */
    private LayerViewport mPlatformLayerViewport;

    /**
     * Create three simple touch controls for player input
     */
    private PushButton moveLeft, moveRight, jumpUp;
    private List<PushButton> mControls;

    /**
     * Define an array of sprites to populate the game world
     */
    private ArrayList<Platform> mPlatforms;

    /**
     * Define the player
     */
    private Player mPlayer;

    // /////////////////////////////////////////////////////////////////////////
    // Constructors
    // /////////////////////////////////////////////////////////////////////////

    /**
     * Create a simple platform game level
     *
     * @param game Game to which this screen belongs
     */
    public PlatformDemoScreen(Game game) {
        super("PlatformDemoScreen", game);

        // Load in the assets used by this layer
        mGame.getAssetManager().loadAssets("txt/assets/PlatformDemoScreenAssets.JSON");

        // Create the layer viewport used to display the platforms (and other game
        // objects). The default, inherited, layer viewport will be used to display
        // movement controls and the default, inherited, screen viewport will be used
        // to define the drawable region on the screen.
        mPlatformLayerViewport = new LayerViewport(240, 160, 240, 160);

        // Create and position the touch controls (relative to the default layer viewport)

        // Determine the layer size to correctly position the touch buttons
        float layerWidth = mDefaultLayerViewport.halfWidth * 2.0f;

        // Create and position the touch buttons
        mControls = new ArrayList<>();
        moveLeft = new PushButton(35.0f, 30.0f, 50.0f, 50.0f,
                "LeftArrow", "LeftArrowSelected", this);
        mControls.add(moveLeft);
        moveRight = new PushButton(100.0f, 30.0f,50.0f, 50.0f,
                "RightArrow", "RightArrowSelected", this);
        mControls.add(moveRight);
        jumpUp = new PushButton((layerWidth - 35.0f), 30.0f, 50.0f, 50.0f,
                "UpArrow", "UpArrowSelected", this);
        mControls.add(jumpUp);

        // Create and position the game objects (relative to the platform viewport)

        // Create the player
        mPlayer = new Player(100.0f, 100.0f, this);

        // Create the platforms
        mPlatforms = new ArrayList<>();

        // Add a wide platform for the ground tile
        int groundTileWidth = 64, groundTileHeight = 35, groundTiles = 50;
        mPlatforms.add(
                new Platform(groundTileWidth * groundTiles / 2, groundTileHeight / 2,
                        groundTileWidth * groundTiles, groundTileHeight,
                        "Ground", groundTiles, 1, this));

        // Add a number of randomly positioned platforms. They are not added in
        // the first 200 units of the level to avoid overlap with the player.
        // A simple (but not that useful) approach is used to position the platforms
        // to avoid overlapping.
        Random random = new Random();
        int numPlatforms = 30, platformOffset = 200;
        float platformWidth = 70, platformHeight = 70, platformX, platformY = platformHeight;
        for (int idx = 0; idx < numPlatforms; idx++) {
            platformX = platformOffset;
            if(random.nextFloat() > 0.33f)
                platformY = (random.nextFloat() * (LEVEL_HEIGHT - platformHeight));
            mPlatforms.add(new Platform( platformX, platformY, platformWidth, platformHeight,
                    "Platform", this));
            platformOffset += (random.nextFloat() > 0.5f ?
                    platformWidth : platformWidth + random.nextFloat()*platformWidth);
        }
    }

    // /////////////////////////////////////////////////////////////////////////
    // Update and Draw
    // /////////////////////////////////////////////////////////////////////////

    /**
     * Update the platform demo screen
     *
     * @param elapsedTime Elapsed time information
     */
    @Override
    public void update(ElapsedTime elapsedTime) {

        // Update the touch buttons checking for player input
        for (PushButton control : mControls)
            control.update(elapsedTime, mDefaultLayerViewport, mDefaultScreenViewport);

        // Update the player
        mPlayer.update(elapsedTime, moveLeft.isPushed(),
                moveRight.isPushed(), jumpUp.isPushed(), mPlatforms);

        // Ensure the player cannot leave the confines of the world
        BoundingBox playerBound = mPlayer.getBound();
        if (playerBound.getLeft() < 0)
            mPlayer.position.x -= playerBound.getLeft();
        else if (playerBound.getRight() > LEVEL_WIDTH)
            mPlayer.position.x -= (playerBound.getRight() - LEVEL_WIDTH);

        if (playerBound.getBottom() < 0)
            mPlayer.position.y -= playerBound.getBottom();
        else if (playerBound.getTop() > LEVEL_HEIGHT)
            mPlayer.position.y -= (playerBound.getTop() - LEVEL_HEIGHT);

        // Focus the layer viewport on the player's x location
        mPlatformLayerViewport.x = mPlayer.position.x;

        // Ensure the viewport cannot leave the confines of the world
        if (mPlatformLayerViewport.getLeft() < 0)
            mPlatformLayerViewport.x -= mPlatformLayerViewport.getLeft();
        else if (mPlatformLayerViewport.getRight() > LEVEL_WIDTH)
            mPlatformLayerViewport.x -= (mPlatformLayerViewport.getRight() - LEVEL_WIDTH);

        if (mPlatformLayerViewport.getBottom() < 0)
            mPlatformLayerViewport.y -= mPlatformLayerViewport.getBottom();
        else if (mPlatformLayerViewport.getTop() > LEVEL_HEIGHT)
            mPlatformLayerViewport.y -= (mPlatformLayerViewport.getTop() - LEVEL_HEIGHT);
    }

    /**
     * Draw the platform demo screen
     *
     * @param elapsedTime Elapsed time information
     * @param graphics2D  Graphics instance
     */
    @Override
    public void draw(ElapsedTime elapsedTime, IGraphics2D graphics2D) {

        graphics2D.clear(Color.WHITE);

        // Draw the player
        mPlayer.draw(elapsedTime, graphics2D, mPlatformLayerViewport, mDefaultScreenViewport);

        // Draw each of the platforms
        for (Platform platform : mPlatforms)
            platform.draw(elapsedTime, graphics2D, mPlatformLayerViewport, mDefaultScreenViewport);

        // Draw the controls last of all
        for (PushButton control : mControls)
            control.draw(elapsedTime, graphics2D, mDefaultLayerViewport, mDefaultScreenViewport);
    }
}
