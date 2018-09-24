package uk.ac.qub.eeecs.game.spaceDemo;

import uk.ac.qub.eeecs.gage.util.SteeringBehaviours;
import uk.ac.qub.eeecs.gage.engine.ElapsedTime;
import uk.ac.qub.eeecs.gage.engine.particle.Emitter;
import uk.ac.qub.eeecs.gage.engine.particle.ParticleSystemManager;
import uk.ac.qub.eeecs.gage.util.MathsHelper;
import uk.ac.qub.eeecs.gage.util.Vector2;
import uk.ac.qub.eeecs.gage.world.Sprite;

/**
 * AI controlled turret.
 *
 * Note: See the course documentation for extension/refactoring stories
 * for this class.
 *
 * @version 1.0
 */
public class Turret extends SpaceEntity {

    // /////////////////////////////////////////////////////////////////////////
    // Properties
    // /////////////////////////////////////////////////////////////////////////

    /**
     * Default size for the Seeker
     */
    private static final float DEFAULT_RADIUS = 30;

    // /////////////////////////////////////////////////////////////////////////
    // Constructors
    // /////////////////////////////////////////////////////////////////////////

    /**
     * Create a AI controlled turret
     *
     * @param startX        x location of the AI turret
     * @param startY        y location of the AI turret
     * @param gameScreen    Gamescreen to which AI belongs
     */
    public Turret(float startX, float startY, SpaceshipDemoScreen gameScreen) {
        super(startX, startY, DEFAULT_RADIUS*2.0f, DEFAULT_RADIUS*2.0f, null, gameScreen);

        maxAcceleration = 0.0f;
        maxVelocity = 0.0f;
        maxAngularVelocity = 50.0f;
        maxAngularAcceleration = 50.0f;
        mBitmap = gameScreen.getGame().getAssetManager().getBitmap("Turret");

        mRadius = DEFAULT_RADIUS;
        mMass = 10000.0f;
    }

    // /////////////////////////////////////////////////////////////////////////
    // Methods
    // /////////////////////////////////////////////////////////////////////////

    /**
     * Update the AI turret
     *
     * @param elapsedTime Elapsed time information
     */
    @Override
    public void update(ElapsedTime elapsedTime) {

        // Turn towards the player
        angularAcceleration =
                SteeringBehaviours.lookAt(this,
                        ((SpaceshipDemoScreen) mGameScreen).getPlayerSpaceship().position);

        // Call the sprite's superclass to apply the determined accelerations
        super.update(elapsedTime);
    }
}
