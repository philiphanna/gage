package uk.ac.qub.eeecs.game.miscDemos;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import uk.ac.qub.eeecs.gage.engine.ElapsedTime;
import uk.ac.qub.eeecs.gage.engine.animation.Animation;
import uk.ac.qub.eeecs.gage.engine.animation.AnimationManager;
import uk.ac.qub.eeecs.gage.engine.graphics.IGraphics2D;
import uk.ac.qub.eeecs.gage.world.GameScreen;
import uk.ac.qub.eeecs.gage.world.LayerViewport;
import uk.ac.qub.eeecs.gage.world.ScreenViewport;
import uk.ac.qub.eeecs.gage.world.Sprite;

/**
 * Simple animated zombie
 *
 * @version 1.0
 */
public class AnimationDemoZombie extends Sprite {

    // /////////////////////////////////////////////////////////////////////////
    // Properties
    // /////////////////////////////////////////////////////////////////////////

    /**
     * Define the available states a zombie can be in - either playing
     * an idle, walking or attacking animation. If playing the walking
     * animation then the sprite will also change it's location based
     * on the direction in which it's facing.
     */
    public enum ZombieState {
        IDLE, WALKING, ATTACKING
    }

    /**
     * Each zombie randomly changes it's state. A probability is used to
     * determine if the state changes. To manage this a map is maintained
     * that holds the probability a state change will occur based on the
     * current zombie state. For example, if walking then there could
     * be a 50% chance of a state change each second, etc.
     */
    private Map<ZombieState, Float> stateTransitionProbability = new HashMap<>();

    /**
     * Define the available facings this zombie can assume. In this case
     * it's simply left and right. Depending on the type of animated
     * character, some might up up/down, etc. animations.
     */
    public enum ZombieFacing {
        LEFT, RIGHT
    }

    /**
     * Define the current state the zombie is in.
     */
    private ZombieState currentState = ZombieState.IDLE;

    /**
     * Define the current facing the zombie is in.
     */
    private ZombieFacing mCurrentFacing = ZombieFacing.RIGHT;

    /**
     * Each zombie will use an animation manager to control the playback of it's
     * current animation.
     */
    private AnimationManager mAnimationManager;


    // /////////////////////////////////////////////////////////////////////////
    // Constructors
    // /////////////////////////////////////////////////////////////////////////

    /**
     * Create the zombie
     *
     * @param startX     x location of the zombie
     * @param startY     y location of the zombie
     * @param width      Width of the zombie
     * @param height     Height of the zombie*
     * @param gameScreen Game screen to which the zombie belongs
     */
    public AnimationDemoZombie(
            float startX, float startY, float width, float height, GameScreen gameScreen) {
        super(startX, startY, width, height, null, gameScreen);

        // Define a set of somewhat arbitrary state transition probabilities
        stateTransitionProbability.put(ZombieState.IDLE, 1.0f);
        stateTransitionProbability.put(ZombieState.WALKING, 0.5f);
        stateTransitionProbability.put(ZombieState.ATTACKING, 1.0f);

        // Create the animation manager that will be used by this zombie to manage
        // the playback of animations. The animation manager will be asked to load
        // in the set of zombie animation parameters from the specified JSON file.
        // The file details a total of three animations (walking, idle and attacking).
        // By default, all zombies start of using the idle animation.

        mAnimationManager = new AnimationManager(this);
        mAnimationManager.addAnimation("txt/animation/ZombieAnimations.JSON");
        mAnimationManager.setCurrentAnimation("ZombieIdle");
    }


    // /////////////////////////////////////////////////////////////////////////
    // Methods
    // /////////////////////////////////////////////////////////////////////////

    /**
     * Set the facing of the zombie to the specified direction
     *
     * @param facing Facing direction
     */
    public void setFacing(ZombieFacing facing) {

        // Store the current facing which will be used by this class to control
        // the direction of travel when walking. Importantly, the animation manager
        // also needs to be updated so it will draw the current animation with an
        // appropriate facing direction.

        mCurrentFacing = facing;
        mAnimationManager.setFacing(facing == ZombieFacing.LEFT ?
                Animation.Facing.Left : Animation.Facing.Right);
    }

    /**
     * Change the facing direction of the zombie
     */
    public void changeDirection() {
        // Call the setFacing method with the opposite facing and change the velocity
        // direction of the zombie.
        setFacing(mCurrentFacing == ZombieFacing.LEFT ? ZombieFacing.RIGHT : ZombieFacing.LEFT);
        velocity.x = -velocity.x;
    }


    /**
     * Explicitly declare an internal random variable to avoid object creation costs
     */
    private Random random = new Random();

    /**
     * Update the zombie
     *
     * @param elapsedTime Elapsed time information
     */
    public void update(ElapsedTime elapsedTime) {

        // Determine if a change of state is needed. Do this by getting the current state
        // transition probability and then checking if it is triggered for this frame.

        float triggerProbability = stateTransitionProbability.get(currentState);
        if (random.nextFloat() < elapsedTime.stepTime * triggerProbability) {

            // Randomly pick a new (but different) state
            int newState;
            ZombieState[] states = ZombieState.values();
            do {
                newState = random.nextInt(states.length);
            } while (states[newState] == currentState);

            // Store the current state
            currentState = states[newState];

            // As the state has changed update the zombie so it is playing an
            // appropriate animation and has an appropriate velocity.
            switch (currentState) {
                case IDLE:
                    velocity.x = 0;
                    mAnimationManager.setCurrentAnimation("ZombieIdle");
                    mAnimationManager.play(elapsedTime);
                    break;
                case WALKING:
                    float WALK_VELOCITY = 40.0f;
                    velocity.x = mCurrentFacing == ZombieFacing.RIGHT ? WALK_VELOCITY : -WALK_VELOCITY;
                    mAnimationManager.setCurrentAnimation("ZombieWalk");
                    mAnimationManager.play(elapsedTime);
                    break;
                case ATTACKING:
                    velocity.x = 0;
                    mAnimationManager.setCurrentAnimation("ZombieAttack");
                    mAnimationManager.play(elapsedTime);
                    break;
            }
        }

        // Update the animation manager. This will ensure that the correct
        // animation frame is selected, based on the current animation and
        // the length of time this animation has been playing.
        mAnimationManager.update(elapsedTime);

        // Call the sprite's update method to ensure the zombie's velocity value
        // will be used to update the zombie's position if moving.
        super.update(elapsedTime);
    }

    /**
     * Draw the zombie to the screen
     *
     * @param elapsedTime    Elapsed time information
     * @param graphics2D     Graphics2D instance to draw using
     * @param layerViewport  Layer viewport
     * @param screenViewport Screen viewport
     */
    @Override
    public void draw(ElapsedTime elapsedTime, IGraphics2D graphics2D,
                     LayerViewport layerViewport, ScreenViewport screenViewport) {

        // Get the animation manager to draw the current animation frame.
        mAnimationManager.draw(elapsedTime, graphics2D, layerViewport, screenViewport);
    }
}
