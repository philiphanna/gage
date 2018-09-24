package uk.ac.qub.eeecs.game.miscDemos;

import android.graphics.Color;
import android.graphics.Paint;

import java.util.Random;

import uk.ac.qub.eeecs.gage.Game;
import uk.ac.qub.eeecs.gage.util.SteeringBehaviours;
import uk.ac.qub.eeecs.gage.engine.AssetManager;
import uk.ac.qub.eeecs.gage.engine.ElapsedTime;
import uk.ac.qub.eeecs.gage.engine.graphics.IGraphics2D;
import uk.ac.qub.eeecs.gage.ui.PushButton;
import uk.ac.qub.eeecs.gage.util.Vector2;
import uk.ac.qub.eeecs.gage.world.GameObject;
import uk.ac.qub.eeecs.gage.world.GameScreen;
import uk.ac.qub.eeecs.gage.world.LayerViewport;
import uk.ac.qub.eeecs.gage.world.ScreenViewport;
import uk.ac.qub.eeecs.gage.world.Sprite;

/**
 * Viewport demo showing how viewports can be used to map between a virtual
 * game space and screen space
 *
 * @version 1.0
 */
public class ViewportDemoScreen extends GameScreen {

    // /////////////////////////////////////////////////////////////////////////
    // Properties
    // /////////////////////////////////////////////////////////////////////////

    /**
     * Define the back button to return to the demo menu
     */
    private PushButton mBackButton;

    /**
     * Define the size of the world within which the game objects will
     * be created and within the layer viewport will be confined.
     */
    private final static float WORLD_WIDTH = 500.0f;
    private final static float WORLD_HEIGHT = 250.0f;

    /**
     * Define the size of each game object that will be created within
     * the world.
     */
    private final static int GAMEOBJECT_WIDTH = 20;
    private final static int GAMEOBJECT_HEIGHT = 20;

    /**
     * Define tne number of and associated storage for the game objects
     * that will live inside this world.
     */
    private final static int NUM_GAMEOBJECTS = 100;
    private GameObject[] mGameObjects = new GameObject[NUM_GAMEOBJECTS];

    /**
     * Define a number of viewports - two for the game layer and two
     * for the screen. One pair of viewports will provide a mapping from
     * a window into the large game world (game layer viewport) and
     * display any game objects within the viewport on the screen
     * of the device (game screen viewport). The other two viewports will
     * be used to provide a high-level map view of the world. In this
     * case the layer viewport (map layer viewport) will capture the
     * entire game world and draw this onto a small region of the
     * device's screen (defined by map screen viewport).
     */

    private LayerViewport mMapLayerViewport;
    private LayerViewport mGameLayerViewport;

    private ScreenViewport mMapScreenViewport;
    private ScreenViewport mGameScreenViewport;

    /**
     * Define the width of the layer viewport. The height of the layer
     * viewport will be calculated based on the display screen aspect
     * ratio to ensure that drawn objects maintain the right length/width
     * ratio. The size of the game viewport will determine how much of the
     * world is visible on screen at any one time.
     */
    private final static float FOCUSED_VIEWPORT_WIDTH = 100.0f;

    /**
     * Define the size of the map is drawn. A value of 0.5 entails that
     * the map will occupy 50% of the width and height of the display screen.
     */
    private final static float MAP_SCALE = 0.4f;

    /**
     * A randomly moving target will be used to randomly move the game
     * layer viewport about - changing the objects that are visible.
     */
    private final static float SEEKER_TRIGGER_DISTANCE = 100.0f;
    private Sprite mSeeker;
    private Vector2 mSeekerTarget;


    // /////////////////////////////////////////////////////////////////////////
    // Constructors
    // /////////////////////////////////////////////////////////////////////////

    /**
     * Create the viewport demo
     *
     * @param game Game to which this screen belongs
     */
    public ViewportDemoScreen(Game game) {
        super("ViewportDemoScreen", game);

        // Create and position a small back button in the lower-right hand corner
        // of the screen. Also, enable click sounds on press/release interactions.
        mBackButton = new PushButton(
                mDefaultLayerViewport.getWidth() * 0.95f,
                mDefaultLayerViewport.getHeight() * 0.10f,
                mDefaultLayerViewport.getWidth() * 0.075f,
                mDefaultLayerViewport.getHeight() * 0.10f,
                "BackArrow", "BackArrowSelected", this);
        mBackButton.setPlaySounds(true, true);

        // Whenever a game screen is created the constructor (called through super(...)
        // will automatically generate two viewports - mDefaultLayerViewport and
        // mDefaultScreenViewport. The default layer viewport is a 480x320 sized region
        // and the default screen viewport is a center 3:2 aspect ratio region.
        // For this demo we won't be using or modifying these viewports, instead we'll
        // create new viewports.

        float screenWidth = mGame.getScreenWidth();
        float screenHeight = mGame.getScreenHeight();

        // Build the two layers viewports. The map layer viewport is created to be the
        // same size as the entire game world (because the map viewport displays the
        // whole world. In contrast, the game viewport is sized based on a specified
        // width value. The height of the viewport is calculated from this width and also
        // the aspect ratio of the screen we will be drawing to. This ensures that when
        // drawing from the game viewport onto the screen viewport objects will correctly
        // display (e.g. a square object will be displayed as a square on screen)

        mMapLayerViewport = new LayerViewport(
                WORLD_WIDTH / 2.0f, WORLD_HEIGHT / 2.0f,
                WORLD_WIDTH / 2.0f, WORLD_HEIGHT / 2.0f);
        float aspectRatio = screenHeight / screenWidth;
        mGameLayerViewport = new LayerViewport(
                WORLD_WIDTH / 2.0f, WORLD_HEIGHT / 2.0f,
                FOCUSED_VIEWPORT_WIDTH / 2.0f,
                aspectRatio * FOCUSED_VIEWPORT_WIDTH / 2.0f);

        // Build the screen map viewport. The width of the map screen viewport is
        // calculated based on the map scale value. The height of the viewport also
        // takes into the aspect ratio of the game world (ensuring the displayed
        // on-screen map has the right 'shape' based on the game world dimensions.

        float mapWidth = screenWidth * MAP_SCALE;
        float mapHeight = mapWidth * WORLD_HEIGHT / WORLD_WIDTH;
        mMapScreenViewport = new ScreenViewport(
                (int) (screenWidth * (1.0f - MAP_SCALE)), 0,
                (int) screenWidth, (int) mapHeight);

        // The game screen viewport is simply sized to take over all of the drawable
        // space on the screen.
        mGameScreenViewport =
                new ScreenViewport(0, 0, (int) screenWidth, (int) screenHeight);

        // Create a bunch of game objects that can be randomly positioned within
        // the world to provide something that can be seen as the viewport moves.

        AssetManager assetManager = mGame.getAssetManager();
        assetManager.loadAndAddBitmap("Platform", "img/Platform1.png");

        Random random = new Random();
        for (int idx = 0; idx < NUM_GAMEOBJECTS; idx++) {
            GameObject platform = new GameObject(
                    random.nextInt((int) WORLD_WIDTH), random.nextInt((int) WORLD_HEIGHT),
                    GAMEOBJECT_WIDTH, GAMEOBJECT_HEIGHT,
                    assetManager.getBitmap("Platform"), this);
            mGameObjects[idx] = platform;
        }

        // Create a seeker object that will be used to move the viewport about and
        // provide it with an initial target to chase.

        mSeeker = new Sprite(mGameLayerViewport.x, mGameLayerViewport.y,
                1.0f, 1.0f, null, this);
        mSeeker.maxAcceleration = 30.0f;
        mSeeker.maxVelocity = 50.0f;
        createNewTarget();
    }


    // /////////////////////////////////////////////////////////////////////////
    // Methods
    // /////////////////////////////////////////////////////////////////////////

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

        // Use the steering behaviour helper class to get the seeker to move
        // towards the target. Once we get sufficient close to the target then
        // generate a new target

        SteeringBehaviours.seek(mSeeker, mSeekerTarget, mSeeker.acceleration);
        mSeeker.update(elapsedTime);
        if ((mSeeker.position.x - mSeekerTarget.x) * (mSeeker.position.x - mSeekerTarget.x) +
                (mSeeker.position.y - mSeekerTarget.y) * (mSeeker.position.y - mSeekerTarget.y)
                < SEEKER_TRIGGER_DISTANCE * SEEKER_TRIGGER_DISTANCE) {
            createNewTarget();
        }

        // Move the location of the game layer viewport so it focussed on
        // the location on the seeker - effectively tracking the seeker as it
        // move about the level.

        mGameLayerViewport.x = mSeeker.position.x;
        mGameLayerViewport.y = mSeeker.position.y;

        // Because the seeker can move off the edge of the map (depending on its
        // speed and the changing location of the target, add in a check that
        // makes sures the viewport cannot leave the world.

        if (mGameLayerViewport.getLeft() < 0) {
            mGameLayerViewport.x -= mGameLayerViewport.getLeft();
            createNewTarget();
        } else if (mGameLayerViewport.getRight() > WORLD_WIDTH) {
            mGameLayerViewport.x -= (mGameLayerViewport.getRight() - WORLD_WIDTH);
            createNewTarget();
        }

        if (mGameLayerViewport.getBottom() < 0) {
            mGameLayerViewport.y -= mGameLayerViewport.getBottom();
            createNewTarget();
        } else if (mGameLayerViewport.getTop() > WORLD_HEIGHT) {
            mGameLayerViewport.y -= (mGameLayerViewport.getTop() - WORLD_HEIGHT);
            createNewTarget();
        }
    }

    /**
     * Create a new random target within the world for the seeker object to reach
     */
    private void createNewTarget() {
        Random random = new Random();
        mSeekerTarget = new Vector2(
                random.nextInt((int) WORLD_WIDTH), random.nextInt((int) WORLD_HEIGHT));
    }

    /**
     * Internal paint variable, defined externally to reduce object creation costs.
     */
    private Paint rectPaint = new Paint();

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

        // Go through each game object and draw it against the game layer viewport,
        // mGameLayerViewport - any object that isn't visible within this viewport
        // won't be displayed. Aside: for 1000 objects most won't be visible within
        // the game viewport at any one point in time - hence this is inefficient.
        // A scene graph can be used to more efficiently determine which objects
        // may be visible if desired.

        for (GameObject platforms : mGameObjects) {
            platforms.draw(elapsedTime, graphics2D, mGameLayerViewport, mGameScreenViewport);
        }

        // Draw a light gray rectangle to highlight the on-screen location of
        // the map screen viewport.

        rectPaint.setColor(Color.LTGRAY);
        rectPaint.setAlpha(128);
        graphics2D.drawRect(
                mMapScreenViewport.left, mMapScreenViewport.top,
                mMapScreenViewport.right, mMapScreenViewport.bottom, rectPaint);

        // Next, draw a dark gray rectangle to highlight the location of the game
        // layer world viewport within the bigger game world.

        rectPaint.setColor(Color.DKGRAY);
        graphics2D.drawRect(
                mMapScreenViewport.left + (float) mMapScreenViewport.width * mGameLayerViewport.getLeft() / WORLD_WIDTH,
                mMapScreenViewport.bottom - (float) mMapScreenViewport.height * mGameLayerViewport.getTop() / WORLD_HEIGHT,
                mMapScreenViewport.left + (float) mMapScreenViewport.width * mGameLayerViewport.getRight() / WORLD_WIDTH,
                mMapScreenViewport.bottom - (float) mMapScreenViewport.height * mGameLayerViewport.getBottom() / WORLD_HEIGHT, rectPaint);

        // Go through all of the objects again and draw them again but this time
        // using the map viewport. All of the objects will be visible within this
        // viewport and hence drawn ot the screen. Aside: Often a map view will
        // draw very simple objects to reduce the cost of drawing everything a
        // 'second' time.

        for (GameObject platforms : mGameObjects) {
            platforms.draw(elapsedTime, graphics2D, mMapLayerViewport, mMapScreenViewport);
        }

        // Draw the back button
        mBackButton.draw(elapsedTime, graphics2D, mDefaultLayerViewport, mDefaultScreenViewport);
    }
}