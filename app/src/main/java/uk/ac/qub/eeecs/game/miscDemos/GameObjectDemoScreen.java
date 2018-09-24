package uk.ac.qub.eeecs.game.miscDemos;

import android.graphics.Bitmap;
import android.graphics.Color;

import java.util.Random;

import uk.ac.qub.eeecs.gage.Game;
import uk.ac.qub.eeecs.gage.engine.AssetManager;
import uk.ac.qub.eeecs.gage.engine.ElapsedTime;
import uk.ac.qub.eeecs.gage.engine.graphics.IGraphics2D;
import uk.ac.qub.eeecs.gage.engine.input.Input;
import uk.ac.qub.eeecs.gage.ui.PushButton;
import uk.ac.qub.eeecs.gage.util.Vector2;
import uk.ac.qub.eeecs.gage.util.ViewportHelper;
import uk.ac.qub.eeecs.gage.world.GameObject;
import uk.ac.qub.eeecs.gage.world.GameScreen;
import uk.ac.qub.eeecs.gage.world.Sprite;

/**
 * Game object and sprite demo showing how game objects can be created and used
 *
 * @version 1.0
 */
public class GameObjectDemoScreen extends GameScreen {

    // /////////////////////////////////////////////////////////////////////////
    // Properties
    // /////////////////////////////////////////////////////////////////////////

    /**
     * Define the back button to return to the demo menu
     */
    private PushButton mBackButton;

    /**
     * The demo comprises two rows of objects - one row of game objects (up
     * arrows) and another of row of sprites (balls).  The density of
     * entities in each row is controllable.
     */

    private final static int GAMEOBJECT_DENSITY = 10;
    private GameObject[] mGameObjects = new GameObject[GAMEOBJECT_DENSITY];
    private Sprite[] mSprites = new Sprite[GAMEOBJECT_DENSITY];

    /**
     * The ball sprites can trigger a jump up and are subject to gravity
     * pulling then down. Define a number of values that control the
     * strength of the jump and gravity. Values for how 'bouncy' the
     * balls are and the sensitivity of the jump trigger are also defined.
     */

    private final static float GRAVITY = -300.0f;
    private final static float DAMPENING = 0.5f;
    private final static float JUMP_STRENGTH_MIN = 250.0f;
    private final static float JUMP_STRENGTH_MAX = 400.0f;
    private final static float JUMP_TRIGGER_DISTANCE = 5.0f;


    // /////////////////////////////////////////////////////////////////////////
    // Constructors
    // /////////////////////////////////////////////////////////////////////////

    /**
     * Create the game object demo
     *
     * @param game Game to which this screen belongs
     */
    public GameObjectDemoScreen(Game game) {
        super("GameObjectDemoScreen", game);

        // Create and position a small back button in the lower-right hand corner
        // of the screen. Also, enable click sounds on press/release interactions.
        mBackButton = new PushButton(
                mDefaultLayerViewport.getWidth() * 0.95f,mDefaultLayerViewport.getHeight() * 0.10f,
                mDefaultLayerViewport.getWidth() * 0.075f, mDefaultLayerViewport.getHeight() * 0.10f,
                "BackArrow", "BackArrowSelected", this);
        mBackButton.setPlaySounds(true, true);

        // Load in the bitmap assets used by this demo
        AssetManager assetManager = mGame.getAssetManager();
        assetManager.loadAndAddBitmap("UpArrow", "img/UpArrow.png");
        assetManager.loadAndAddBitmap("UpArrowSelected", "img/UpArrowSelected.png");
        assetManager.loadAndAddBitmap("Ball", "img/Ball.png");

        // Create the game object and sprite entities
        for (int idx = 0; idx < GAMEOBJECT_DENSITY; idx++) {

            // Determine the size of the arrows and balls entities based on the entity density
            float objectWidth = (mDefaultLayerViewport.getWidth()
                    - mDefaultLayerViewport.getHeight() * 0.10f) / GAMEOBJECT_DENSITY;
            float objectHeight = objectWidth;

            // Create the arrow game object trigger
            GameObject trigger = new GameObject(
                    objectWidth * (idx + 0.5f), objectHeight / 2.0f, objectWidth, objectHeight,
                    assetManager.getBitmap("UpArrow"), this);
            mGameObjects[idx] = trigger;

            // Create the ball sprite
            Sprite ball = new Sprite(
                    objectWidth * (idx + 0.5f),
                    random.nextInt((int) (mDefaultLayerViewport.getHeight() - objectHeight * 2.0f)) + objectHeight * 1.5f,
                    objectWidth, objectHeight, assetManager.getBitmap("Ball"), this);
            mSprites[idx] = ball;
        }
    }


    // /////////////////////////////////////////////////////////////////////////
    // Methods
    // /////////////////////////////////////////////////////////////////////////

    /**
     * Create internal random and Vector2 instance, defined externally to reduce
     * object creation costs.
     */
    private Random random = new Random();
    private Vector2 touchLocation = new Vector2();

    /**
     * Update the game object demo
     *
     * @param elapsedTime Elapsed time information
     */
    @Override
    public void update(ElapsedTime elapsedTime) {

        // Update the back button. If triggered then return to the demo menu
        mBackButton.update(elapsedTime);
        if (mBackButton.isPushTriggered())
            mGame.getScreenManager().removeScreen(this);

        // Get the input manager and retrieve the arrows bitmaps in preparation
        // for updating the entities.

        Input input = mGame.getInput();
        Bitmap upArrow = mGame.getAssetManager().getBitmap("UpArrow");
        Bitmap upArrowSelected = mGame.getAssetManager().getBitmap("UpArrowSelected");

        // Update each entity. This involves checking to see if any touch points
        // occur on the arrow triggers (changing the bitmap is appropriate).
        // If a touch point occurs a further check if carried out to determine
        // if the ball is in close proximity to the arrow. If so, a jump on the
        // ball is triggered.

        for (int idx = 0; idx < mGameObjects.length; idx++) {
            GameObject trigger = mGameObjects[idx];
            trigger.setBitmap(upArrow);

            final int TOUCH_IDS = 5;
            for (int touchID = 0; touchID < TOUCH_IDS; touchID++) {
                // Consider if a corresponding touch points exists
                if (input.existsTouch(touchID)) {
                    // If so, then convert it from an on-screen position into the game layer
                    ViewportHelper.convertScreenPosIntoLayer(mDefaultScreenViewport,
                            input.getTouchX(touchID), input.getTouchY(touchID), mDefaultLayerViewport, touchLocation);

                    // Check to see if the corresponding layer touch point is over the trigger
                    if (trigger.getBound().contains(touchLocation.x, touchLocation.y)) {
                        // If so, then change the bitmap
                        trigger.setBitmap(upArrowSelected);

                        // Finally, check the corresponding ball sprite to see if it is within the
                        // trigger distance for jumping, and trigger a jump if needed
                        Sprite ball = mSprites[idx];
                        if ((ball.position.y - ball.getHeight() / 2.0f)
                                - (trigger.position.y + trigger.getHeight() / 2.0f) < JUMP_TRIGGER_DISTANCE)
                            ball.velocity.y =
                                    random.nextInt((int) (JUMP_STRENGTH_MAX - JUMP_STRENGTH_MIN)) +
                                            JUMP_STRENGTH_MIN;
                    }
                }
            }
        }

        // The ball sprites are also subject to gravity which will be pulling then down.
        // Define a 'ground' height under which the sprite will not be permitted to move.
        float groundHeight = (mDefaultLayerViewport.getWidth()
                - mDefaultLayerViewport.getHeight() * 0.10f) / GAMEOBJECT_DENSITY;

        // Update each ball sprite
        for (Sprite ball : mSprites) {

            // Apply gravity and update the ball's position
            ball.acceleration.y = GRAVITY;
            ball.update(elapsedTime);

            // If the ball have move under the ground height then reposition
            // and change the direction of the velocity (subject to dampening)
            // as it bounces up with less strength.

            if (ball.position.y - ball.getHeight() / 2.0f < groundHeight) {
                ball.position.y = groundHeight + ball.getHeight() / 2.0f;
                ball.velocity.y = -ball.velocity.y * DAMPENING;
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

        // Clear the screen
        graphics2D.clear(Color.WHITE);

        // Draw each of the trigger game objects
        for (GameObject trigger : mGameObjects)
            trigger.draw(elapsedTime, graphics2D, mDefaultLayerViewport, mDefaultScreenViewport);

        // Draw each of the ball sprites
        for (Sprite ball : mSprites)
            ball.draw(elapsedTime, graphics2D, mDefaultLayerViewport, mDefaultScreenViewport);

        // Draw the back button
        mBackButton.draw(elapsedTime, graphics2D, mDefaultLayerViewport, mDefaultScreenViewport);
    }
}
