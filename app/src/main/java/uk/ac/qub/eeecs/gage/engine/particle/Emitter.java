package uk.ac.qub.eeecs.gage.engine.particle;

import android.graphics.RectF;

import java.util.Random;

import uk.ac.qub.eeecs.gage.engine.ElapsedTime;
import uk.ac.qub.eeecs.gage.util.Vector2;

/**
 * Particle emitter.
 *
 * Each emitter is responsible for creating, updating and disposing a set of
 * managed particles.
 *
 * @version 1.0
 */
public class Emitter {

    // /////////////////////////////////////////////////////////////////////////
    // Properties: Emitter settings and location
    // /////////////////////////////////////////////////////////////////////////

    /**
     * Settings used to drive the emitter
     */
    private EmitterSettings mEmitterSettings;

    /**
     * Position of the emitter in game world coordinates
     */
    private Vector2 mPosition = new Vector2();

    /**
     * Previous position of the emitter (only relevant for moving emitters)
     */
    private Vector2 mLastPosition = new Vector2();

    // /////////////////////////////////////////////////////////////////////////
    // Properties: Particle storage
    // /////////////////////////////////////////////////////////////////////////

    /**
     * Storage for particles
     */
    private Particle[] mParticleStorage;

    /**
     * Number of stored particles (either alive or recently expired but yet
     * to be released).
     */
    private int mNumParticles;

    /**
     * Percentage threshold used to determine when expired particles will be
     * released back to the particle pool.
     */
    private float mParticleStorageCleanupThreshold = 0.75f;

    // /////////////////////////////////////////////////////////////////////////
    // Properties: Other properties
    // /////////////////////////////////////////////////////////////////////////

    /**
     * Particle system manager that this emitter belongs to
     */
    private ParticleSystemManager mParticleSystemManager;

    /**
     * Track the maximum region the particles have reached from the emitter
     * center (used as part of the emitter's visibility check in the particle
     * system manager.
     */
    private RectF mEmitterVisibleRange = new RectF();


    // /////////////////////////////////////////////////////////////////////////
    // Constructors
    // /////////////////////////////////////////////////////////////////////////

    /**
     * Create an emitter
     *
     * @param particleSystemManager Particle system manager
     * @param emitterSettingsJSON   Emitter settings JSON file
     * @param position              Emitter position in game space
     */
    public Emitter(ParticleSystemManager particleSystemManager,
                   String emitterSettingsJSON, Vector2 position) {
        this(particleSystemManager,
            new EmitterSettings(particleSystemManager.getGame().getAssetManager(),
                        emitterSettingsJSON), position);
    }

    /**
     * Create an emitter
     *
     * @param particleSystemManager Particle system manager
     * @param emitterSettings       Emitter settings
     * @param position              Emitter position in game space
     */
    public Emitter(ParticleSystemManager particleSystemManager,
                   EmitterSettings emitterSettings, Vector2 position) {

        // Store the passed values
        mParticleSystemManager = particleSystemManager;
        mEmitterSettings = emitterSettings;
        mPosition.set(position);
        mLastPosition.set(position);

        // Determine the maximum number of particles associated with this emitter
        // This comprises a full set of live particle alongside sufficient storage
        // for expired particles that have yet to be released.

        int maxParticles = mEmitterSettings.maxParticleDensity;
        if (mEmitterSettings.emitterMode == EmitterSettings.EmitterMode.Continuous
                && mEmitterSettings.particleSettings.maxLifespan > 1.0f)
            maxParticles = (int) ((float) maxParticles * mEmitterSettings.particleSettings.maxLifespan);
        maxParticles = (int) Math.ceil((float) maxParticles / mParticleStorageCleanupThreshold);

        // Create storage for the particles
        mParticleStorage = new Particle[maxParticles];
        mNumParticles = 0;
    }


    // /////////////////////////////////////////////////////////////////////////
    // Methods: Accessor Methods
    // /////////////////////////////////////////////////////////////////////////

    /**
     * Set the position of the emitter
     *
     * @param x Emitter x position
     * @param y Emitter y position
     */
    public void setPosition(float x, float y) {
        mPosition.x = x;
        mPosition.y = y;
    }

    /**
     * Get the position of the emitter
     *
     * @return Emitter position
     */
    public Vector2 getPosition() {
        return mPosition;
    }

    /**
     * Get the number of particles currently managed by this emitter
     * (including alive particles and those recently expired but have yet
     * to be released).
     *
     * @return Number of managed particles
     */
    public int getNumParticles() {
        return mNumParticles;
    }

    /**
     * Get access to the array of storage particles (including alive particles
     * and recently expired particles that have yet to be released).
     * <p>
     * Note: The getNumParticles() method should be used to determine the
     * safe iteration size of this array.
     *
     * @return Array of Particles
     */
    public Particle[] getParticleStorage() {
        return mParticleStorage;
    }

    /**
     * Return the emitter settings
     *
     * @return Emitter settings
     */
    public EmitterSettings getEmitterSettings() {
        return mEmitterSettings;
    }

    /**
     * Return the particle visibility region based on the most recent update.
     * <p>
     * Note: This method is used by the particle system manager to determine
     * if the emitter has visible particles.
     *
     * @return Particle visibility extents based on the most recent update.
     */
    public RectF getEmitterVisibleRange() {
        return mEmitterVisibleRange;
    }


    // /////////////////////////////////////////////////////////////////////////
    // Methods: Update Particles
    // /////////////////////////////////////////////////////////////////////////

    /**
     * Internal accumulator to keep track of when a particle should be generated.
     */
    private float numParticlesToAddAccumulator = 0.0f;

    /**
     * Update the emitter, creating new particles if needed, updating
     * alive particles, removing expired particles.
     * <p>
     * Note: If the emitter is set to burst mode, it will automatically
     * remove itself from the particle manager when all the managed particles
     * have expired.
     *
     * @param elapsedTime Elapsed time information
     * @return Number of alive particles that were updated
     */
    public int update(ElapsedTime elapsedTime) {

        // Add particles if needed
        considerAddingParticles((float)elapsedTime.stepTime);

        // Apply gravity to the managed particles if needed
        if (mEmitterSettings.applyGravity) {
            Vector2 gravity = mParticleSystemManager.getGravity();
            for (int particleIdx = 0; particleIdx < mNumParticles; particleIdx++) {
                mParticleStorage[particleIdx].acceleration.x += gravity.x;
                mParticleStorage[particleIdx].acceleration.y += gravity.y;
            }
        }

        // Update the particles, keep a track of how far the particles are from the emitter
        int numUpdatedParticles = 0;
        mEmitterVisibleRange.set(mPosition.x, mPosition.y, mPosition.x, mPosition.y);
        for (int particleIdx = 0; particleIdx < mNumParticles; particleIdx++) {
            Particle particle = mParticleStorage[particleIdx];
            if (particle.isAlive()) {
                // Update the particle
                particle.update((float)elapsedTime.stepTime);
                numUpdatedParticles++;

                // Check if the visible bound for particles needs to be widened
                float halfWidth = particle.size.x / 2.0f, halfHeight = particle.size.y / 2.0f;
                if (particle.position.x + halfWidth < mEmitterVisibleRange.left)
                    mEmitterVisibleRange.left = particle.position.x + halfWidth;
                else if (particle.position.x - halfWidth > mEmitterVisibleRange.right)
                    mEmitterVisibleRange.right = particle.position.x - halfWidth;
                if (particle.position.y + halfHeight < mEmitterVisibleRange.bottom)
                    mEmitterVisibleRange.bottom = particle.position.y + halfHeight;
                else if (particle.position.y - halfHeight > mEmitterVisibleRange.top)
                    mEmitterVisibleRange.top = particle.position.y - halfHeight;
            }
        }

        // Remove particles if needed
        considerRemovingParticles(numUpdatedParticles);

        // Update last location to be this location
        mLastPosition.set(mPosition);

        return numUpdatedParticles;
    }

    // /////////////////////////////////////////////////////////////////////////
    // Methods: Add, Create and Remove Particles
    // /////////////////////////////////////////////////////////////////////////

    /**
     * If new particles if appropraite. For a burst emitter, all particles are
     * added the first time this method is called. For a continuous emitter
     * consider adding particles each update.
     *
     * @param dt Elapsed time step since last update
     */
    private void considerAddingParticles(float dt) {
        // Add particles if needed
        if (mEmitterSettings.emitterMode == EmitterSettings.EmitterMode.Burst) {
            if (mNumParticles == 0)
                addParticles(dt,
                        (int) randomBetween(mEmitterSettings.minParticleDensity,
                                mEmitterSettings.maxParticleDensity));
        } else {
            numParticlesToAddAccumulator += randomBetween(mEmitterSettings.minParticleDensity,
                    mEmitterSettings.maxParticleDensity) * dt;
            if (numParticlesToAddAccumulator > 1.0) {
                addParticles(dt, (int) numParticlesToAddAccumulator);
                numParticlesToAddAccumulator = numParticlesToAddAccumulator % 1.0f;
            }
        }

    }

    /**
     * Internal Vector2 objects that are reused when adding particles - defined
     * externally to the method to reduce temporary object creation.
     */
    private Vector2 particlePosition = new Vector2();
    private Vector2 particleOffset = new Vector2();
    private Vector2 particleVelocityBias = new Vector2();

    /**
     * Add the specified number of particles to those managed by the emitter.
     *
     * @param dt                Elapsed time step since last update
     * @param numParticlesToAdd Number of particles to add
     */
    public void addParticles(float dt, int numParticlesToAdd) {

        // Determine the location and offset depending upon the emission mode
        switch (mEmitterSettings.emitterMode) {
            case Burst:
                particlePosition.set(mPosition);
                particleOffset.set(Vector2.Zero);
                break;
            case Continuous:
                particlePosition.set(mLastPosition);
                particleOffset.set((mPosition.x - mLastPosition.x) / numParticlesToAdd,
                        (mPosition.y - mLastPosition.y) / numParticlesToAdd);
                break;
        }

        // Determine if a velocity bias should be applied
        if (mEmitterSettings.velocityBias == 0.0f) {
            particleVelocityBias.set(0.0f, 0.0f);
        } else {
            particleVelocityBias.x = mPosition.x - mLastPosition.x;
            particleVelocityBias.y = mPosition.y - mLastPosition.y;
            if (particleVelocityBias.lengthSquared() > 1.0f)
                particleVelocityBias.normalise();
            particleVelocityBias.multiply(
                    mEmitterSettings.velocityBias / dt);
        }

        // Initialise and add the particles
        for (int i = 0; i < numParticlesToAdd; i++) {
            if (mNumParticles == mParticleStorage.length)
                increaseParticleStorage();

            Particle particle = mParticleSystemManager.getParticleFromPool();

            initialiseParticle(particle, particlePosition, particleVelocityBias);
            mParticleStorage[mNumParticles++] = particle;

            particlePosition.x += particleOffset.x;
            particlePosition.y += particleOffset.y;
        }
    }

    /**
     * Internal Vector2 objects that are reused when initialising particles -
     * defined externally to the method to reduce temporary object creation.
     */
    private Vector2 size = new Vector2();
    private Vector2 direction = new Vector2();
    private Vector2 velocity = new Vector2();
    private Vector2 acceleration = new Vector2();

    /**
     * Initialise the particle
     *
     * @param particle Particle to initialise
     * @param position Position of the particle
     */
    private void initialiseParticle(
            Particle particle, Vector2 position, Vector2 velocityBias) {
        ParticleSettings particleSettings = mEmitterSettings.particleSettings;

        // Store the particle size
        size.x = particleSettings.width;
        size.y = particleSettings.height;

        // Determine the velocity
        pickRandomDirection(
                particleSettings.minVelocityDirection,
                particleSettings.maxVelocityDirection, direction);
        float speed = randomBetween(particleSettings.minVelocityMagnitude,
                particleSettings.maxVelocityMagnitude);
        velocity.x = direction.x * speed + velocityBias.x;
        velocity.y = direction.y * speed + velocityBias.y;

        // Determine the acceleration
        switch (mEmitterSettings.accelerationMode) {
            case Aligned:
                // Default to zero acceleration for (near)-zero velocity
                float zeroVelocityThreshold = 0.001f;
                if (velocity.lengthSquared() < zeroVelocityThreshold) {
                    acceleration.x = 0.0f;
                    acceleration.y = 0.0f;
                } else {
                    // Determine the velocity direction
                    direction.x = velocity.x;
                    direction.y = velocity.y;
                    direction.normalise();

                    // Generate an acceleration magnitude whilst using
                    // the current velocity direction
                    float accelerationMagnitude = randomBetween(
                            particleSettings.minAccelerationMagnitude,
                            particleSettings.maxAccelerationMagnitude);
                    acceleration.x = direction.x * accelerationMagnitude;
                    acceleration.y = direction.y * accelerationMagnitude;
                }
                break;
            case NonAligned:
                // Generate an acceleration using the defined magnitude
                // and directional ranges
                pickRandomDirection(
                        particleSettings.minAccelerationDirection,
                        particleSettings.maxAccelerationDirection, acceleration);
                float accelerationMagnitude = randomBetween(
                        particleSettings.minAccelerationMagnitude,
                        particleSettings.maxAccelerationMagnitude);
                acceleration.x *= accelerationMagnitude;
                acceleration.y *= accelerationMagnitude;
                break;
            default:
                break;
        }

        // Determine the orientation
        float orientation = randomBetween(particleSettings.minOrientation,
                particleSettings.maxOrientation);

        // Determine the angular velocity
        float angularVelocity = randomBetween(particleSettings.minAngularVelocity,
                particleSettings.maxAngularVelocity);

        // Determine the scale and scale growth
        float scale = randomBetween(particleSettings.minScale, particleSettings.maxScale);
        float scaleGrowth = randomBetween(
                particleSettings.minScaleGrowth, particleSettings.maxScaleGrowth);

        // Determine the life span
        float lifeSpan = randomBetween(
                particleSettings.minLifespan, particleSettings.maxLifespan);

        // Determine the fade in and out values
        float fadeInBy = randomBetween(
                particleSettings.minFadeInBy, particleSettings.maxFadeInBy);
        float fadeOutFrom = randomBetween(
                particleSettings.minFadeOutFrom, particleSettings.maxFadeOutFrom);

        // Initialise the particle
        particle.initialize(size, position, velocity, acceleration, orientation,
                angularVelocity, scale, scaleGrowth, lifeSpan, fadeInBy, fadeOutFrom,
                particleSettings.bitmap);
    }

    /**
     * Consider removing particles, either if all that remains is expired particles, or
     * the percentage of expired particles exceeds the release threshold
     *
     * @param numUpdatedParticles Number of alive particles that were updated
     */
    private void considerRemovingParticles(int numUpdatedParticles) {
        // Check if particles need to be released
        if (numUpdatedParticles == 0 ||
                (float) numUpdatedParticles / (float) mNumParticles < mParticleStorageCleanupThreshold) {
            int aliveIdx = 0;
            for (int particleIdx = 0; particleIdx < mNumParticles; particleIdx++) {
                if (mParticleStorage[particleIdx].isAlive()) {
                    // If alive, copy to alive index and increment
                    mParticleStorage[aliveIdx] = mParticleStorage[particleIdx];
                    aliveIdx++;
                } else {
                    // If dead, release the particle
                    mParticleSystemManager.returnParticleToPool(mParticleStorage[particleIdx]);
                    mParticleStorage[particleIdx] = null;
                }
            }

            // Update the number of stored particles to reflect the released dead particles
            mNumParticles = aliveIdx;
        }

        // If this is a burst emitter then remove it if all the particles are dead
        if (mEmitterSettings.emitterMode == EmitterSettings.EmitterMode.Burst
                && mNumParticles == 0)
            mParticleSystemManager.removeEmitter(this);

    }


    // /////////////////////////////////////////////////////////////////////////
    // Utility Methods: Particle storage and Value generation
    // /////////////////////////////////////////////////////////////////////////

    /**
     * Increase the particle storage array by 50%
     */
    private void increaseParticleStorage() {
        Particle[] newParticleStorage = new Particle[(int) (mParticleStorage.length * 1.5f)];
        System.arraycopy(
                mParticleStorage, 0, newParticleStorage, 0, mParticleStorage.length);
        mParticleStorage = newParticleStorage;
    }

    /**
     * Release all particles managed by this emitter
     */
    public void releaseAllParticles() {
        for (int particleIdx = 0; particleIdx < mNumParticles; particleIdx++) {
            mParticleSystemManager.returnParticleToPool(mParticleStorage[particleIdx]);
            mParticleStorage[particleIdx] = null;
        }
        mNumParticles = 0;
    }

    /**
     * Static random instance used by all emitters to configure their particles
     */
    private static Random random = new Random();

    /**
     * Return a random between between the specified min and max
     *
     * @param min Minimum value
     * @param max Maximum value
     * @return Value in the specified range
     */
    private static float randomBetween(float min, float max) {
        return min + random.nextFloat() * (max - min);
    }

    /**
     * Return a random direction between the specified min and max
     *
     * @param min          Minimum value
     * @param max          Maximum value
     * @param outputVector Vector within which the direction will be stored
     */
    private static void pickRandomDirection(float min, float max, Vector2 outputVector) {
        float angle = randomBetween(min, max);
        // our settings angles are in degrees, so we must convert to radians
        angle = (float) Math.toRadians(angle);
        outputVector.set((float) Math.cos(angle), (float) Math.sin(angle));
    }
}





