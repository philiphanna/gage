package uk.ac.qub.eeecs.game.spaceDemo;

import java.util.Random;

import uk.ac.qub.eeecs.gage.world.GameScreen;
import uk.ac.qub.eeecs.gage.world.Sprite;

/**
 * Simple asteroid
 *
 * Note: See the course documentation for extension/refactoring stories
 * for this class.
 *
 * @version 1.0
 */
public class Asteroid extends SpaceEntity {

    // /////////////////////////////////////////////////////////////////////////
    // Properties
    // /////////////////////////////////////////////////////////////////////////

    /**
     * Default size for the asteroid
     */
    private static final float DEFAULT_RADIUS = 20;

    // /////////////////////////////////////////////////////////////////////////
    // Constructors
    // /////////////////////////////////////////////////////////////////////////

    /**
     * Create an asteroid
     *
     * @param startX     x location of the asteroid
     * @param startY     y location of the asteroid
     * @param gameScreen Gamescreen to which asteroid belongs
     */
    public Asteroid(float startX, float startY, GameScreen gameScreen) {
        super(startX, startY, DEFAULT_RADIUS*2.0f, DEFAULT_RADIUS*2.0f, null, gameScreen);

        Random random = new Random();

        mBitmap = gameScreen.getGame().getAssetManager()
                .getBitmap(random.nextBoolean() ? "Asteroid1" : "Asteroid2");

        mRadius = DEFAULT_RADIUS;
        mMass = 1000.0f;

        angularVelocity = random.nextFloat() * 240.0f - 20.0f;

    }
}
