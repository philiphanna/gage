package uk.ac.qub.eeecs.gage.engine.particle;

import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;

import java.util.ArrayList;
import java.util.Iterator;

import uk.ac.qub.eeecs.gage.Game;
import uk.ac.qub.eeecs.gage.engine.ElapsedTime;
import uk.ac.qub.eeecs.gage.engine.graphics.IGraphics2D;
import uk.ac.qub.eeecs.gage.util.Pool;
import uk.ac.qub.eeecs.gage.util.Vector2;
import uk.ac.qub.eeecs.gage.util.ViewportHelper;
import uk.ac.qub.eeecs.gage.world.LayerViewport;
import uk.ac.qub.eeecs.gage.world.ScreenViewport;

/**
 * Particle System Manager.
 *
 * The particle system manager is responsible for managing a number of emitters,
 * requesting that emitters update themselves and then managing how particles
 * are drawn to the screen.
 *
 * @version 1.0
 */
public class ParticleSystemManager {

    // /////////////////////////////////////////////////////////////////////////
    // Properties: Emitters, particle counters and gravity
    // /////////////////////////////////////////////////////////////////////////

    /**
     * Game instance this manager is attached to
     */
    private Game mGame;

    /**
     * Define the emitters managed by this particle system manager.
     */
    private ArrayList<Emitter> mEmitters = new ArrayList<>();

    /**
     * Record the number of particles updated in the last update
     */
    private int mNumUpdatedParticles;

    /**
     * Record the number of particles draw in the last draw
     */
    private int mNumDrawnParticles;

    /**
     * Define a gravitational acceleration that can be (optionally) added
     * to all particles during update
     */
    private Vector2 mGravity = new Vector2();


    // /////////////////////////////////////////////////////////////////////////
    // Constructors
    // /////////////////////////////////////////////////////////////////////////

    /**
     * Create a new Particle System Manager
     *
     * @param game Parent game
     */
    public ParticleSystemManager(Game game) {
        this(game, PARTICLE_POOL_MAXIMUM_SIZE);
    }

    /**
     * Create a new Particle System Manager
     *
     * @param game Parent game
     * @param particlePoolMaximumSize Initial pool size for free particles
     */
    public ParticleSystemManager(Game game, int particlePoolMaximumSize) {
        mGame = game;
        setupParticlePool(particlePoolMaximumSize);
        setupDraw(game);
    }


    // /////////////////////////////////////////////////////////////////////////
    // Accessor methods
    // /////////////////////////////////////////////////////////////////////////

    /**
     * Return the number of particles updated within the latest update
     *
     * @return Number of particles updated
     */
    public int getNumUpdatedParticles() {
        return mNumUpdatedParticles;
    }

    /**
     * Return the number of particles drawn within the latest draw
     *
     * @return Number of particles drawn
     */
    public int getNumDrawnParticles() {
        return mNumDrawnParticles;
    }

    /**
     * Set gravitational acceleration to that specified
     *
     * @param x Gravitational x acceleration
     * @param y Gravitational y acceleration
     */
    public void setGravity(float x, float y) {
        mGravity.x = x;
        mGravity.y = y;
    }

    /**
     * Return the current gravitational acceleration
     * @return Gravitational vector
     */
    public Vector2 getGravity() {
        return mGravity;
    }

    /**
     * Get the game instance for this manager
     */
    public Game getGame() {
        return mGame;
    }


    // /////////////////////////////////////////////////////////////////////////
    // Emitter Management
    // /////////////////////////////////////////////////////////////////////////

    /**
     * Add the specified emtter to the manager
     *
     * @param emitter Emitter to add
     */
    public void addEmitter( Emitter emitter) {
        mEmitters.add(emitter);
    }

    /**
     * Remove the specified emitter from the manager.
     *
     * Note: All particles associated with the emitter will be released and
     * return to the particle pool.
     * #
     * @param emitterToRemove Emitter to remove
     */
    public void removeEmitter( Emitter emitterToRemove) {
        Iterator<Emitter> iterator = mEmitters.iterator();
        while (iterator.hasNext()) {
            Emitter emitter = iterator.next();
            if (emitter == emitterToRemove) {
                emitter.releaseAllParticles();
                iterator.remove();
            }
        }
    }


    // /////////////////////////////////////////////////////////////////////////
    // Update
    // /////////////////////////////////////////////////////////////////////////

    /**
     * Update all added emitters
     *
     * @param elapsedTime Elapsed time since the last update
     */
    public void update(ElapsedTime elapsedTime) {
        mNumUpdatedParticles = 0;
        Iterator<Emitter> iterator = mEmitters.iterator();
        while (iterator.hasNext()) {
            Emitter emitter = iterator.next();
            mNumUpdatedParticles += emitter.update(elapsedTime);
        }
   }


    // /////////////////////////////////////////////////////////////////////////
    // Draw
    // /////////////////////////////////////////////////////////////////////////

    /**
     * Internal variables that are reused when drawing particles -
     * defined externally to the method to reduce temporary object creation.
     */
    private Paint alphaBlend;
    private Paint additiveBlend;
    private Matrix drawMatrix = new Matrix();
    private Vector2 screenPosition = new Vector2();

    /**
     * Setup the internal draw variables to be used during draw callls
     *
     * @param game Parent game instance
     */
    private void setupDraw(Game game) {
        // Create a paint for normal alpha blending
        alphaBlend = new Paint();
        alphaBlend.setAntiAlias(true);

        // Createa a paint of additive blending
        additiveBlend = new Paint();
        additiveBlend.setAntiAlias(true);
        additiveBlend.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.ADD));
    }

    /**
     * Draw all live emitter particles.
     *
     * Note: If viewports are not used then it is assumed that all particles are
     * defined in screen space and will be drawn directly to screen space. No
     * visibility checking will be performed and the particle's size property is
     * assumed to be in terms of on-screen pixels.
     *
     * @param elapsedTime Elapsed time information
     * @param graphics2D  Graphics instance
     */
    public void draw(ElapsedTime elapsedTime, IGraphics2D graphics2D) {
        mNumDrawnParticles = 0;

        // Draw the particles associated with each emitter. All particles
        // are assumed to be drawn directly in screen space so no visibility
        // bound checking is applied.
        for( int emitterIdx = 0; emitterIdx < mEmitters.size(); emitterIdx++) {
            Emitter emitter = mEmitters.get(emitterIdx );

            // Extract the bitmap for drawing
            Bitmap bitmap = emitter.getEmitterSettings().particleSettings.bitmap;
            float bitmapHalfWidth = bitmap.getWidth() / 2.0f;
            float bitmapHalfHeight = bitmap.getHeight() / 2.0f;

            // Define a scaling for the bitmap based on it's size and the defined
            // particle size
            float scaleX = emitter.getEmitterSettings().particleSettings.width / bitmap.getWidth();
            float scaleY = emitter.getEmitterSettings().particleSettings.height / bitmap.getHeight();

            // Retrieve an appropriate paint reference for drawing
            Paint paint =
                    emitter.getEmitterSettings().blendMode == EmitterSettings.BlendMode.Additive ?
                            additiveBlend : alphaBlend;

            // Draw all active particles for this emitter
            Particle[] particles = emitter.getParticleStorage();
            for( int particleIdx = 0; particleIdx < emitter.getNumParticles(); particleIdx++) {
                if (particles[particleIdx].isAlive()) {
                    Particle particle = particles[particleIdx];

                    // Use the particle's alpha value
                    paint.setAlpha((int) (particle.fade * 255.0f));

                    // Build an appropriate transformation matrix
                    drawMatrix.reset();

                    // Define the scaling to apply
                    drawMatrix.postScale(scaleX * particle.scale, scaleY * particle.scale);

                    // Rotate about the (scaled) bitmap centre
                    drawMatrix.postRotate(particle.orientation,
                            scaleX * bitmapHalfWidth, scaleY * bitmapHalfHeight);

                    // Translate to the correct on screen location
                    drawMatrix.postTranslate(
                            particle.position.x - scaleX * bitmapHalfWidth,
                            particle.position.y - scaleY * bitmapHalfHeight);

                    // Draw the image
                    graphics2D.drawBitmap(bitmap, drawMatrix, paint);
                    mNumDrawnParticles++;
                }
            }
        }
    }

    /**
     * Draw all live emitter particles
     *
     * Note: If viewports are specified it is assumed that all particles are
     * defined in game space and will be convert into screen space as part of the
     * draw process. Game layer Visibility checking will be performed and the
     * particle's size property is assumed to be in terms of game layer units.
     *
     * @param elapsedTime Elapsed time information
     * @param graphics2D  Graphics instance
     * @param layerViewport Game viewport
     * @param screenViewport Screen viewport
     */
    public void draw(ElapsedTime elapsedTime, IGraphics2D graphics2D,
                     LayerViewport layerViewport, ScreenViewport screenViewport) {

        // Record the number of particles that are drawn for reporting purposes
        mNumDrawnParticles = 0;

        // Draw the particles associated with each emitter
        for( int emitterIdx = 0; emitterIdx < mEmitters.size(); emitterIdx++) {
            Emitter emitter = mEmitters.get(emitterIdx);

            // Skip this emitter if it's particles are all outside of the layer viewport
            RectF emitterVisibility = emitter.getEmitterVisibleRange();
            if (emitterVisibility.left > layerViewport.getRight() ||
                    emitterVisibility.right < layerViewport.getLeft() ||
                    emitterVisibility.bottom > layerViewport.getTop() ||
                    emitterVisibility.top < layerViewport.getBottom()) {
                continue;
            }

            // If the emitter has visible particles then get its settings
            EmitterSettings emitterSettings = emitter.getEmitterSettings();

            // Extract the bitmap for drawing
            Bitmap bitmap = emitterSettings.particleSettings.bitmap;
            float bitmapHalfWidth = bitmap.getWidth() / 2.0f;
            float bitmapHalfHeight = bitmap.getHeight() / 2.0f;

            // Define a bitmap scale factor for drawing to the screen. This
            // equates to the scaling between the game and screen viewport,
            // times the size of the particle, divided by the particle bitmap size.
            float toScreenXScale = ((float) screenViewport.width / (2 * layerViewport.halfWidth))
                    * emitterSettings.particleSettings.width / (float)bitmap.getWidth();
            float toScreenYScale = ((float) screenViewport.height / (2 * layerViewport.halfHeight))
                    * emitterSettings.particleSettings.height / (float)bitmap.getHeight();

            // Retrieve an appropriate paint reference for drawing
            Paint paint = emitterSettings.blendMode == EmitterSettings.BlendMode.Additive
                    ? additiveBlend : alphaBlend;

            // Draw all active particles for this emitter
            Particle[] particles = emitter.getParticleStorage();
            for( int particleIdx = 0; particleIdx < emitter.getNumParticles(); particleIdx++) {
                if (particles[particleIdx].isAlive()) {
                    Particle particle = particles[particleIdx];

                    // Use the particle's alpha value
                    paint.setAlpha((int) (particle.fade * 255.0f));

                    // Build an appropriate transformation matrix
                    drawMatrix.reset();

                    // Define the scaling to apply
                    float scaleX = toScreenXScale * particle.scale;
                    float scaleY = toScreenYScale * particle.scale;
                    drawMatrix.postScale(scaleX, scaleY);

                    // Rotate about the (scaled) bitmap centre
                    drawMatrix.postRotate(particle.orientation,
                            scaleX * bitmapHalfWidth, scaleY * bitmapHalfHeight);

                    // Translate to the correct on screen location
                    ViewportHelper.convertLayerPosIntoScreen(
                            layerViewport, particle.position, screenViewport, screenPosition);
                    drawMatrix.postTranslate(
                            screenPosition.x - scaleX * bitmapHalfWidth,
                            screenPosition.y - scaleY * bitmapHalfHeight);

                    // Draw the image
                    graphics2D.drawBitmap(bitmap, drawMatrix, paint);
                    mNumDrawnParticles++;
                }
            }
        }
    }

    // /////////////////////////////////////////////////////////////////////////
    // Particle Pool
    // /////////////////////////////////////////////////////////////////////////

    /**
     * Define the maximum and initial particle pool population sizes
     */
    private static final int PARTICLE_POOL_MAXIMUM_SIZE = 10000;
    private static final int PARTICLE_POOL_INITIAL_POPULATION = 100;

    /**
     * Define a pool of particles that can be recycled amongst emitters
     */
    private Pool<Particle> mParticlePool;

    /**
     * Get a particle from the pool.
     *
     * Note: The particle may be 'dirty' containing data from its last
     * use. Any returned particle should be initialised once retrieved.
     *
     * @return Particle instance
     */
    public Particle getParticleFromPool() {
        return mParticlePool.get();
    }

    /**
     * Return an expired particle back into the pool to be reused
     *
     * @param particle Particle to reuse
     */
    public void returnParticleToPool(Particle particle) {
        mParticlePool.add(particle);
    }

    /**
     * Setup the particle pool using the defined maximum and initial values
     *
     * @param particlePoolMaximumSize Maximum particle pool size
     */
    private void setupParticlePool(int particlePoolMaximumSize) {

        // Create a new particle pool
        mParticlePool = new Pool<>(new Pool.ObjectFactory<Particle>() {
            public Particle createObject() {
                return new Particle();
            }
        }, particlePoolMaximumSize);

        // Introduce an initial batch of particles into the pool
        for (int i = 0; i < PARTICLE_POOL_INITIAL_POPULATION; i++) {
            Particle particle = new Particle();
            mParticlePool.add(particle);
        }
    }
}
