package uk.ac.qub.eeecs.game.miscDemos;

import android.graphics.Color;

import java.util.Random;

import uk.ac.qub.eeecs.gage.Game;
import uk.ac.qub.eeecs.gage.engine.ElapsedTime;
import uk.ac.qub.eeecs.gage.engine.graphics.IGraphics2D;
import uk.ac.qub.eeecs.gage.ui.PushButton;
import uk.ac.qub.eeecs.gage.util.BoundingBox;
import uk.ac.qub.eeecs.gage.util.CollisionDetector;
import uk.ac.qub.eeecs.gage.world.GameScreen;

/**
 * Animation demo showing a number of animated (zombie) sprites
 *
 * @version 1.0
 */
public class AnimationDemoScreen extends GameScreen {

    // /////////////////////////////////////////////////////////////////////////
    // Properties
    // /////////////////////////////////////////////////////////////////////////

    /**
     * Define the back button to return to the demo menu
     */
    private PushButton mBackButton;

    /**
     * The zombies will be initially positioned into a grid layout.
     * The following variables define the number of rows of zombies
     * and the number of zombies in each row.
     */
    private final static int NUM_ROWS = 6;
    private final static int NUM_ZOMBIES_IN_ROW = 3;

    /**
     * Create an array to hold the zombie objects. Each zombie will
     * be held as a separate object (with it's own position, animation
     * state, etc.)
     */
    private AnimationDemoZombie[][] mZombies
            = new AnimationDemoZombie[NUM_ROWS][NUM_ZOMBIES_IN_ROW];


    // /////////////////////////////////////////////////////////////////////////
    // Constructors
    // /////////////////////////////////////////////////////////////////////////

    /**
     * Create the animation demo
     *
     * @param game Game to which this screen belongs
     */
    public AnimationDemoScreen(Game game) {
        super("AnimationDemoScreen", game);

        // Create and position a small back button in the lower-right hand corner
        // of the screen. Also, enable click sounds on press/release interactions.
        mBackButton = new PushButton(
                mDefaultLayerViewport.getWidth() * 0.95f,
                mDefaultLayerViewport.getHeight() * 0.10f,
                mDefaultLayerViewport.getWidth() * 0.075f,
                mDefaultLayerViewport.getHeight() * 0.10f,
                "BackArrow", "BackArrowSelected", this);
        mBackButton.setPlaySounds(true, true);

        // In order to position the zombies a width, height and spacing is
        // calculated to provide a regular grid-like layout. The zombies
        // are sized and positioned based on the number of zombies.

        float zombieHeight = mDefaultLayerViewport.getHeight() / NUM_ROWS;
        float zombieWidth = zombieHeight * 0.66f;
        float zombieSpacing = mDefaultLayerViewport.getWidth() / (NUM_ZOMBIES_IN_ROW + 1);

        Random random = new Random();
        for (int rowIdx = 0; rowIdx < NUM_ROWS; rowIdx++)
            for (int zombieIdx = 0; zombieIdx < NUM_ZOMBIES_IN_ROW; zombieIdx++) {
                // Create a new zombie instance with a random left/right facing.
                AnimationDemoZombie zombie = new AnimationDemoZombie(
                        zombieSpacing * (zombieIdx + 1),
                        zombieHeight * (0.5f + rowIdx),
                        zombieWidth, zombieHeight, this);
                zombie.setFacing(random.nextBoolean() ?
                        AnimationDemoZombie.ZombieFacing.LEFT :
                        AnimationDemoZombie.ZombieFacing.RIGHT);

                // Store the zombie
                mZombies[rowIdx][zombieIdx] = zombie;
            }
    }

    // /////////////////////////////////////////////////////////////////////////
    // Methods
    // /////////////////////////////////////////////////////////////////////////

    /**
     * Update the animation demo
     *
     * @param elapsedTime Elapsed time information
     */
    @Override
    public void update(ElapsedTime elapsedTime) {

        // Update the back button. If triggered then return to the demo menu.
        mBackButton.update(elapsedTime);
        if (mBackButton.isPushTriggered())
            mGame.getScreenManager().removeScreen(this);

        // Update all of the zombies and ensure they do not collide or leave the screen
        for (int rowIdx = 0; rowIdx < NUM_ROWS; rowIdx++) {
            for (int zombieIdx = 0; zombieIdx < NUM_ZOMBIES_IN_ROW; zombieIdx++) {

                // Extract and update the current zombie. In doing so the zombie will
                // update and potentially change it's state (walking, idle or attack).
                AnimationDemoZombie zombie = mZombies[rowIdx][zombieIdx];
                zombie.update(elapsedTime);

                // If the zombie has moved, then it might now be in collision with another
                // zombie in its row. If this happens, resolve the collision by repositioning
                // the zombie that moved and change it's facing direction.

                for (int otherZombieIdx = 0; otherZombieIdx < NUM_ZOMBIES_IN_ROW; otherZombieIdx++) {
                    // Make sure a zombie cannot collide with itself
                    if (zombieIdx != otherZombieIdx ) {
                        // Use the collision detector to both detect and reposition the moving
                        // zombie following a collision.
                        CollisionDetector.CollisionType collisionType =
                                CollisionDetector.determineAndResolveCollision(
                                    zombie, mZombies[rowIdx][otherZombieIdx]);

                        // If a collision occurred, then tell the zombie to change it's direction
                        if (collisionType != CollisionDetector.CollisionType.None) {
                            zombie.changeDirection();
                        }
                    }
                }

                // If the zombie has moved it may also have moved outside of the 'level' (in this
                // case the level is arbitrarily assumed to start with an x value of 0 and a width
                // equal to the width of the default layer viewport). If this happens then
                // reposition the zombie and change it's facing direction.

                BoundingBox zombieBound = zombie.getBound();
                if (zombieBound.getLeft() < 0) {
                    zombie.position.x -= zombieBound.getLeft();
                    zombie.changeDirection();
                } else if (zombieBound.getRight() > mDefaultLayerViewport.getWidth()) {
                    zombie.position.x -= (zombieBound.getRight() - mDefaultLayerViewport.getWidth());
                    zombie.changeDirection();
                }
            }
        }
    }

    /**
     * Draw the animation demo
     *
     * @param elapsedTime Elapsed time information
     * @param graphics2D  Graphics instance
     */
    @Override
    public void draw(ElapsedTime elapsedTime, IGraphics2D graphics2D) {

        // Clear the screen
        graphics2D.clear(Color.WHITE);

        // Ask each of the zombies to draw themselves using the default layer and screen viewports
        for (int rowIdx = 0; rowIdx < NUM_ROWS; rowIdx++)
            for (int zombieIdx = 0; zombieIdx < NUM_ZOMBIES_IN_ROW; zombieIdx++)
                mZombies[rowIdx][zombieIdx].draw(
                        elapsedTime, graphics2D, mDefaultLayerViewport, mDefaultScreenViewport);

        // Draw the back button
        mBackButton.draw(elapsedTime, graphics2D, mDefaultLayerViewport, mDefaultScreenViewport);
    }
}