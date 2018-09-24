package uk.ac.qub.eeecs.gage.util;

import java.util.List;

import uk.ac.qub.eeecs.gage.world.Sprite;

/**
 * Collection of steering behaviours that can be used to help sprites move
 */
public class SteeringBehaviours {

    // /////////////////////////////////////////////////////////////////////////
    // Seek and Flee
    // /////////////////////////////////////////////////////////////////////////

    /**
     * Store an acceleration vector for the specified body towards the specified
     * target position.
     *
     * @param seekingSprite  Seeking sprite
     * @param targetPosition Target position
     * @param acceleration   Output seek acceleration
     */
    public static void seek(Sprite seekingSprite, Vector2 targetPosition,
                            Vector2 acceleration) {
        // Return if the body has reached the target
        if (targetPosition.x == seekingSprite.position.x
                && targetPosition.y == seekingSprite.position.y) {
            acceleration.set(Vector2.Zero);
        } else {

            // Determine the seeking direction
            acceleration.set(targetPosition.x - seekingSprite.position.x,
                    targetPosition.y - seekingSprite.position.y);
            acceleration.normalise();
            acceleration.multiply(seekingSprite.maxAcceleration);
        }
    }

    /**
     * Store an acceleration vector for the specified body away from the
     * specified target position
     *
     * @param fleeingSprite  Fleeing sprite
     * @param targetPosition Target position
     * @param acceleration   Output flee acceleration
     */
    public static void flee(Sprite fleeingSprite, Vector2 targetPosition,
                            Vector2 acceleration) {
        // Return if the body has reached the target
        if (targetPosition.x == fleeingSprite.position.x
                && targetPosition.y == fleeingSprite.position.y) {
            acceleration.set(Vector2.Zero);
        } else {

            // Determine the seeking direction
            acceleration.set(fleeingSprite.position.x - targetPosition.x,
                    fleeingSprite.position.y - targetPosition.y);
            acceleration.normalise();
            acceleration.multiply(fleeingSprite.maxAcceleration);
        }
    }

    // /////////////////////////////////////////////////////////////////////////
    // Arrive
    // /////////////////////////////////////////////////////////////////////////

    /**
     * Private variables use to facilitate the arrive functionality
     */
    private static Vector2 arriveDirection = new Vector2();
    private static Vector2 arriveSpeed = new Vector2();

    /**
     * Output an acceleration vector for the specified sprite that will cause it
     * to arrive with a stopping velocity at the specified target.
     *
     * @param arrivingSprite     Arriving sprite
     * @param targetPosition     Target position at which to arrive
     * @param arriveAcceleration Output arrive accleration
     */
    public static void arrive(Sprite arrivingSprite, Vector2 targetPosition,
                              Vector2 arriveAcceleration) {

        // Determine the current separation and target direction
        arriveDirection.set(targetPosition.x - arrivingSprite.position.x,
                targetPosition.y - arrivingSprite.position.y);
        float distance = arriveDirection.length();
        arriveDirection.divide(distance);

        // Determining the slow down radius
        float slowDownRadius = arrivingSprite.maxVelocity
                * arrivingSprite.maxVelocity / arrivingSprite.maxAcceleration;

        // Determine the target speed
        if (distance > slowDownRadius) {
            arriveSpeed.set(arriveDirection);
            arriveSpeed.multiply(arrivingSprite.maxVelocity);
        } else {
            arriveSpeed.set(arriveDirection);
            arriveSpeed.multiply(arrivingSprite.maxVelocity * distance
                    / slowDownRadius);
        }

        // Determine the arrival acceleration
        arriveAcceleration.set(arriveSpeed);
        arriveAcceleration.subtract(arrivingSprite.velocity);

        if (arriveAcceleration.lengthSquared() > arrivingSprite.maxAcceleration
                * arrivingSprite.maxAcceleration) {
            arriveAcceleration.normalise();
            arriveAcceleration.multiply(arrivingSprite.maxAcceleration);
        }
    }

    // /////////////////////////////////////////////////////////////////////////
    // Align, Align with Movement and Look At
    // /////////////////////////////////////////////////////////////////////////

    /**
     * Default value that is used to control the align smoothness
     */
    public static float ALIGN_DEFAULT_SMOOTHNESS = 10.0f;

    /**
     * Align the orientation of the specified sprite with the target
     * orientation, using the default align smoothness value.
     *
     * @param aligningSprite    Sprite to align
     * @param targetOrientation Target orientation
     * @return Angular acceleration needed to align
     */
    public static float align(Sprite aligningSprite, float targetOrientation) {
        return align(aligningSprite, targetOrientation,
                ALIGN_DEFAULT_SMOOTHNESS);
    }

    /**
     * Align the orientation of the specified sprite with the target
     * orientation, using the specified align smoothness value.
     *
     * @param aligningSprite    Sprite to align
     * @param targetOrientation Target orientation
     * @param smoothness        Align smoothness value
     * @return Angular acceleration needed to align
     */
    public static float align(Sprite aligningSprite, float targetOrientation,
                              float smoothness) {
        // Determine the target rotation (in +-PI range)
        float separation = targetOrientation - aligningSprite.orientation;
        int numDivisions = (int) ((separation) / 180.0f);
        if (numDivisions > 0)
            numDivisions += 1;
        else
            numDivisions -= 1;
        separation -= (numDivisions / 2) * 360;
        float absSeparation = separation < 0.0 ? -separation : separation;

        // Determining the slow down rotational distance
        float slowDownDistance = smoothness * aligningSprite.maxAngularVelocity
                / aligningSprite.maxAngularAcceleration;

        // Determine the target rotation
        float targetAngularVelocity;
        if (absSeparation < 1.0f)
            targetAngularVelocity = 0.0f;
        else {
            if (absSeparation > slowDownDistance)
                targetAngularVelocity = aligningSprite.maxAngularVelocity;
            else
                targetAngularVelocity = aligningSprite.maxAngularVelocity
                        * absSeparation / slowDownDistance;
            targetAngularVelocity *= separation / absSeparation;
        }

        // Determine the aligning angular acceleration
        float aligningAngularAcceleration = targetAngularVelocity
                - aligningSprite.angularVelocity;
        if (aligningAngularAcceleration > aligningSprite.maxAngularVelocity)
            aligningAngularAcceleration = aligningSprite.maxAngularVelocity;
        else if (aligningAngularAcceleration < -aligningSprite.maxAngularVelocity)
            aligningAngularAcceleration = -aligningSprite.maxAngularVelocity;

        return aligningAngularAcceleration;
    }

    /**
     * Change the orientation of the specified sprite so that it is looking at
     * the specified target location.
     *
     * @param aligningSprite Sprite that should look at the target
     * @param targetPosition Location at which the sprite should look
     * @return Angular acceleration needed to look at the target.
     */
    public static float lookAt(Sprite aligningSprite, Vector2 targetPosition) {
        // Determine the lookat vector
        float targetOrientation = (float) Math.atan2(
                -(targetPosition.y - aligningSprite.position.y),
                (targetPosition.x - aligningSprite.position.x));

        // Determine the desired angular acceleration
        return align(aligningSprite, (float) Math.toDegrees(targetOrientation));
    }

    /**
     * Align the orientation of the specified sprite with it's current velocity.
     *
     * @param aligningSprite Sprite whose orientation should be aligned.
     * @return Angular acceleration needed to align the orientation with
     * velocity.
     */
    public static float alignWithMovement(Sprite aligningSprite) {
        // Determine the direction of movement
        float targetOrientation = (float) Math.atan2(
                -aligningSprite.velocity.y, aligningSprite.velocity.x);

        // Determine the desired angular acceleration
        return align(aligningSprite, (float) Math.toDegrees(targetOrientation));
    }

    // /////////////////////////////////////////////////////////////////////////
    // Separate
    // /////////////////////////////////////////////////////////////////////////

    /**
     * Private variable used within the separate algorithms
     */
    private static Vector2 separateAcceleration = new Vector2();

    /**
     * Separate the specified sprite from the list of other sprites.
     *
     * @param separatingSprite     Sprite that should be separated.
     * @param targetSprites        Sprites to separate from.
     * @param separateThreshold    Distance within which the sprite should separate.
     * @param repulsionDecayFactor How strongly should the sprite separate based on distance.
     * @param outputAcceleration   Angular acceleration needed to seperate the sprite.
     */
    public static void separate(Sprite separatingSprite,
                                List<? extends Sprite> targetSprites, float separateThreshold,
                                float repulsionDecayFactor, Vector2 outputAcceleration) {
        // Start with zero net acceleration
        outputAcceleration.set(Vector2.Zero);

        // Consider each target body
        int numSprites = targetSprites.size();
        float separateThresholdPow2 = separateThreshold * separateThreshold;
        for (int idx = 0; idx < numSprites; idx++) {
            Sprite targetSprite = targetSprites.get(idx);
            if (separatingSprite.position.x == targetSprite.position.x
                    && separatingSprite.position.y == targetSprite.position.y)
                continue;

            // Separate if within the trigger distance to the object
            float separationX = (separatingSprite.position.x - targetSprite.position.x);
            float separationY = (separatingSprite.position.y - targetSprite.position.y);
            float separationPow2 = separationX * separationX + separationY
                    * separationY;

            if (separationPow2 < separateThresholdPow2) {
                // Determine the repulsive strength
                float repulsiveStrength = repulsionDecayFactor * separationPow2;
                if (repulsiveStrength > separatingSprite.maxAcceleration)
                    repulsiveStrength = separatingSprite.maxAcceleration;

                // Build the repulsive force
                separateAcceleration.set(separationX, separationY);
                separateAcceleration.normalise();
                separateAcceleration.multiply(repulsiveStrength);

                // Update the output acceleration
                outputAcceleration.add(separateAcceleration);
            }
        }
    }

    /**
     * Separate the specified sprite from the other specified sprite
     *
     * @param separatingSprite     Sprite that should be separated.
     * @param targetSprite         Sprite to separate from.
     * @param separateThreshold    Distance within which the sprite should separate.
     * @param repulsionDecayFactor How strongly should the sprite separate based on distance.
     * @param outputAcceleration   Angular acceleration needed to separate the sprite.
     */
    public static void separate(Sprite separatingSprite, Sprite targetSprite,
                                float separateThreshold, float repulsionDecayFactor,
                                Vector2 outputAcceleration) {
        // Start with zero net acceleration
        outputAcceleration.set(Vector2.Zero);

        // Consider the distance to the target body
        float separateThresholdPow2 = separateThreshold * separateThreshold;

        if (separatingSprite.position.x == targetSprite.position.x
                && separatingSprite.position.y == targetSprite.position.y)
            return;

        // Separate if within the trigger distance to the object
        float separationX = (separatingSprite.position.x - targetSprite.position.x);
        float separationY = (separatingSprite.position.y - targetSprite.position.y);
        float separationPow2 = separationX * separationX + separationY
                * separationY;

        if (separationPow2 < separateThresholdPow2) {
            // Determine the repulsive strength
            float repulsiveStrength = repulsionDecayFactor * separationPow2;
            if (repulsiveStrength > separatingSprite.maxAcceleration)
                repulsiveStrength = separatingSprite.maxAcceleration;

            // Build the repulsive force
            separateAcceleration.set(separationX, separationY);
            separateAcceleration.normalise();
            separateAcceleration.multiply(repulsiveStrength);

            // Update the output acceleration
            outputAcceleration.add(separateAcceleration);
        }
    }
}
