package uk.ac.qub.eeecs.gage.engine.particle;

import android.graphics.Bitmap;

import uk.ac.qub.eeecs.gage.util.Vector2;

/**
 * Particle.
 *
 * Single particle that is managed within an emitter.
 *
 * @version 1.0
 */
public class Particle {
	
	// /////////////////////////////////////////////////////////////////////////
	// Properties: Declared public for speed of access
	// /////////////////////////////////////////////////////////////////////////

    /**
     * Size of the particle (in game layer units)
     */
    public Vector2 size = new Vector2();

	/**
	 * Position of the particle
	 */
	public Vector2 position = new Vector2();
	
	/**
	 * Velocity of the particle
	 */
	public Vector2 velocity = new Vector2();
	
	/**
	 * Acceleration of the particle
	 */
	public Vector2 acceleration = new Vector2();

	/**
	 * Orientation of the particle (in degrees)
	 */
	public float orientation;
	
	/**
	 * Angular velocity of the particle (in degrees/second)
	 */
	public float angularVelocity;

	/**
	 * Scaling factor to applied to the particle 
	 */
	public float scale;
	
	/**
	 * Growth factor determining how the scale changes over time
	 */
	public float scaleGrowth;

	/**
	 * Length of time this particle will remain alive (in seconds)
	 */
	public float lifeSpan;

    /**
     * Length of time since the birth of this particle (in seconds)
     */
    public float timeSinceBirth;

    /**
     * Length of time (percentage value (0-1) of total lifeSpan) to fade in the particle
     */
    public float fadeInBy;

    /**
     * Length of time (percentage value (0-1) of total lifeSpan) to start fading out the particle
     */
    public float fadeOutFrom;

    /**
     * Percentage fade (0-1) to apply to the particle
     */
    public float fade;

    /**
     * Bitmap reference for this particle
     */
    public Bitmap bitmap;

	// /////////////////////////////////////////////////////////////////////////
	// Methods: 
	// /////////////////////////////////////////////////////////////////////////

	/**
	 * Initialise the particle using the specified values 
	 * 
	 * @param position Position
	 * @param velocity Velocity
	 * @param acceleration Acceleration
	 * @param orientation Orientation
	 * @param angularVelocity Angular velocity
	 * @param scale Scale
	 * @param scaleGrowth Scale growth
	 * @param lifeSpan Life span
     * @param fadeInBy Fade in end time
     * @param fadeOutFrom Fade out start time
     * @param bitmap Bitmap
	 */
	public void initialize(Vector2 size, Vector2 position, Vector2 velocity,
			Vector2 acceleration, float orientation, float angularVelocity,
			float scale, float scaleGrowth, float lifeSpan,
            float fadeInBy, float fadeOutFrom, Bitmap bitmap) {

	    this.size.x = size.x;
        this.size.y = size.y;

		this.position.x = position.x;
		this.position.y = position.y;
		
		this.velocity.x = velocity.x;
		this.velocity.y = velocity.y;

		this.acceleration.x = acceleration.x;
		this.acceleration.y = acceleration.y;

		this.orientation = orientation;
		this.angularVelocity = angularVelocity;

		this.scale = scale;
		this.scaleGrowth = scaleGrowth;

		this.lifeSpan = lifeSpan;
        this.timeSinceBirth = 0.0f;

		this.fadeInBy = fadeInBy;
        this.fadeOutFrom = fadeOutFrom;
        this.fade = 0.0f;

        this.bitmap = bitmap;
	}

	/**
	 * Evolve the particle
	 * 
	 * @param dt Amount of time elapsed (in seconds) from the last update call
	 */
	public void update(float dt) {
		velocity.x += acceleration.x * dt;
		velocity.y += acceleration.y * dt;

		position.x += velocity.x * dt;
		position.y += velocity.y * dt;

		orientation += angularVelocity * dt;

		scale += scaleGrowth * dt;

		timeSinceBirth += dt;

        float percentageTimeSinceBirth = timeSinceBirth / lifeSpan;
        fade = percentageTimeSinceBirth < fadeInBy ? percentageTimeSinceBirth / fadeInBy : 1.0f;
        if (percentageTimeSinceBirth > fadeOutFrom)
            fade = 1.0f - (percentageTimeSinceBirth - fadeOutFrom) / (1.0f - fadeOutFrom);
	}

    /**
     * Determine if this particle is still alive
     *
     * @return Boolean true if alive, otherwise false
     */
    public boolean isAlive() {
        return timeSinceBirth < lifeSpan;
    }
}