package uk.ac.qub.eeecs.game.spaceDemo;

import android.graphics.Bitmap;

import uk.ac.qub.eeecs.gage.world.GameScreen;
import uk.ac.qub.eeecs.gage.world.Sprite;

/**
 * Simple base class of all space-based entities used in this demo
 */
public class SpaceEntity extends Sprite {

    // /////////////////////////////////////////////////////////////////////////
    // Properties
    // /////////////////////////////////////////////////////////////////////////

    /**
     * Bounding volume radius
     */
    public float mRadius = 1.0f;

    /**
     * Mass of the entity
     */
    public float mMass = 1.0f;

    // /////////////////////////////////////////////////////////////////////////
    // Constructors
    // /////////////////////////////////////////////////////////////////////////

    /**
     * Create a space entity
     *
     * @param startX     x location of the entity
     * @param startY     y location of the entity
     * @param gameScreen Gamescreen to which entity belongs
     */
    public SpaceEntity(float startX, float startY,
                       float width, float height, Bitmap bitmap, GameScreen gameScreen) {
        super(startX, startY, width, height, bitmap, gameScreen);
    }
}
