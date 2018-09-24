package uk.ac.qub.eeecs.game.spaceDemo;

import uk.ac.qub.eeecs.gage.util.SteeringBehaviours;
import uk.ac.qub.eeecs.gage.engine.ElapsedTime;
import uk.ac.qub.eeecs.gage.engine.particle.Emitter;
import uk.ac.qub.eeecs.gage.engine.particle.ParticleSystemManager;
import uk.ac.qub.eeecs.gage.ui.ThumbStick;
import uk.ac.qub.eeecs.gage.util.MathsHelper;
import uk.ac.qub.eeecs.gage.util.Vector2;
import uk.ac.qub.eeecs.gage.world.GameScreen;
import uk.ac.qub.eeecs.gage.world.Sprite;

/**
 * Player controlled spaceship
 * <p>
 * Note: See the course documentation for extension/refactoring stories
 * for this class.
 *
 * @version 1.0
 */
public class PlayerSpaceship extends SpaceEntity {

    // /////////////////////////////////////////////////////////////////////////
    // Properties
    // /////////////////////////////////////////////////////////////////////////

    /**
     * Default size for the asteroid
     */
    private static final float DEFAULT_RADIUS = 25;

    /**
     * Particle emitters showing the movement of the player's spaceship
     */
    private Emitter mMovementEmitterLeft;
    private Emitter mMovementEmitterRight;

    /**
     * Offset for the movement emitters so they appears to exit from the back
     * of the spaceship (relative to the centre position of the spaceship)
     */
    private Vector2 mMovementEmitterOffsetLeft;
    private Vector2 mMovementEmitterOffsetRight;
    private Vector2 mMovementEmitterLocation;


    // /////////////////////////////////////////////////////////////////////////
    // Constructors
    // /////////////////////////////////////////////////////////////////////////

    /**
     * Create a player controlled spaceship
     *
     * @param startX     x location of the player spaceship
     * @param startY     y location of the player spaceship
     * @param gameScreen Gamescreen to which spaceship belongs
     */
    public PlayerSpaceship(float startX, float startY, GameScreen gameScreen) {
        super(startX, startY, DEFAULT_RADIUS*2.0f, DEFAULT_RADIUS*2.0f, gameScreen.getGame()
                .getAssetManager().getBitmap("Spaceship1"), gameScreen);

        // Define the maximum velocities and accelerations of the spaceship
        maxAcceleration = 600.0f;
        maxVelocity = 100.0f;
        maxAngularVelocity = 1440.0f;
        maxAngularAcceleration = 1440.0f;

        mRadius = DEFAULT_RADIUS;
        mMass = 1000.0f;

        // Create an offset for the movement emitters based on the size of the spaceship
        mMovementEmitterOffsetLeft = new Vector2(-20.0f, 20.0f);
        mMovementEmitterOffsetRight = new Vector2(-20.0f, -20.0f);

        // Create and add a particle effect for the movement of the ship
        ParticleSystemManager particleSystemManager =
                ((SpaceshipDemoScreen) mGameScreen).getParticleSystemManager();

        // Create and add the left emitter
        mMovementEmitterLocation = new Vector2(position);
        mMovementEmitterLocation.add(mMovementEmitterOffsetLeft);
        mMovementEmitterLeft = new Emitter(
                particleSystemManager, "txt/particle/ThrusterEmitter.JSON",
                mMovementEmitterLocation);
        particleSystemManager.addEmitter(mMovementEmitterLeft);

        // Create and add the right emitter
        mMovementEmitterLocation.set(position);
        mMovementEmitterLocation.add(mMovementEmitterOffsetRight);
        mMovementEmitterRight = new Emitter(
                particleSystemManager, "txt/particle/ThrusterEmitter.JSON",
                mMovementEmitterLocation);
        particleSystemManager.addEmitter(mMovementEmitterRight);
    }

    // /////////////////////////////////////////////////////////////////////////
    // Methods
    // /////////////////////////////////////////////////////////////////////////

    /**
     * Update the player spaceship
     *
     * @param elapsedTime        Elapsed time information
     * @param movementThumbstick Movement thumbstick control
     */
    public void update(ElapsedTime elapsedTime, ThumbStick movementThumbstick) {

        //  Consider movement requests
        if (movementThumbstick.isTouched()) {
            // Apply an input acceleration
            acceleration.x = movementThumbstick.getXMagnitude() * maxAcceleration;
            acceleration.y = movementThumbstick.getYMagnitude() * maxAcceleration;
        }

        // Ensure that the ships points in the direction of movement
        angularAcceleration = SteeringBehaviours.alignWithMovement(this);

        // Dampen the linear and angular acceleration and velocity
        angularAcceleration *= 0.95f;
        angularVelocity *= 0.75f;
        acceleration.multiply(0.75f);
        velocity.multiply(0.95f);

        // Apply the determined accelerations
        super.update(elapsedTime);

        // Update the particle emitters associated with this ship to rhe new position,
        // calculating an offset so the steam comes from the rear of the ship
        MathsHelper.rotateOffsetAboutCentre(
                position, mMovementEmitterOffsetLeft, orientation, mMovementEmitterLocation);
        mMovementEmitterLeft.setPosition(mMovementEmitterLocation.x, mMovementEmitterLocation.y);
        MathsHelper.rotateOffsetAboutCentre(
                position, mMovementEmitterOffsetRight, orientation, mMovementEmitterLocation);
        mMovementEmitterRight.setPosition(mMovementEmitterLocation.x, mMovementEmitterLocation.y);

        // Depending on the speed of the spaceship tweak the number of created particles
        mMovementEmitterLeft.getEmitterSettings().minParticleDensity = (int) velocity.length();
        mMovementEmitterLeft.getEmitterSettings().maxParticleDensity = (int) (1.2f * velocity.length());
        mMovementEmitterRight.getEmitterSettings().minParticleDensity = (int) velocity.length();
        mMovementEmitterRight.getEmitterSettings().maxParticleDensity = (int) (1.2f * velocity.length());
    }
}
