package uk.ac.qub.eeecs.gage.engine.particle;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import uk.ac.qub.eeecs.gage.engine.AssetManager;

/**
 * Particle emitter settings.
 * <p>
 * Configuration parameters used to control a given particle emitter.
 *
 * @version 1.0
 */
public class EmitterSettings {

    // /////////////////////////////////////////////////////////////////////////
    // Properties: Emitter Defined Values
    // /////////////////////////////////////////////////////////////////////////

    /**
     * Define the emitter mode - if burst then all the particles will be
     * generated at once within the manager. If set to continuous, then
     * a continuous stream of particles will be generated.
     */
    public enum EmitterMode {
        Burst, Continuous
    }

    /**
     * Define the emitter blend mode - particles can be drawn using
     * either nor alpha blending or additive blending.
     */
    public enum BlendMode {
        Additive, Alpha
    }

    /**
     * Define the acceleration mode - if set to aligned then any
     * generated acceleration will be added in the direction of the
     * current particle velocity. If nonaligned then the defined
     * acceleration directional values will be used.
     */
    public enum AccelerationMode {
        Aligned, NonAligned
    }


    // /////////////////////////////////////////////////////////////////////////
    // Properties: Declared public for speed of access
    // /////////////////////////////////////////////////////////////////////////

    /**
     * Emitter settings name
     */
    public String name;

    /**
     * Define the particle emission mode
     */
    public EmitterMode emitterMode;

    /**
     * Particle Settings - settings of the particles to be emitted.
     */
    public ParticleSettings particleSettings;

    /**
     * Particle Density - density of the particles to be generated,
     * representing either the burst size or the number of particles
     * generated each second depending on the emitter mode.
     */
    public int minParticleDensity;
    public int maxParticleDensity;

    /**
     * Blend Mode - either render the particle using additive or normal
     * alpha blending.
     */
    public BlendMode blendMode;

    /**
     * Acceleration Mode - if set to aligned particle acceleration direction
     * will be set based on the selected velocity direction (for example used
     * to either slow or speed up particles along their initial direction of
     * travel. If set to non-aligned, then the acceleration direction and
     * velocity will be based on the specified particle settings.
     */
    public AccelerationMode accelerationMode;

    /**
     * Particle Velocity Bias - if set to zero, no velocity bias will be
     * applied (the particle settings will simply determine particle velocity).
     * If set to a non-zero number, then a velocity will be calculated using
     * the current and previous locations. This will then be scaled by the
     * velocityBias (e.g. 1.0 - add current velocity to all particles, -2.0
     * add a double strength inverse velocity to all particle).
     */
    public float velocityBias;

    /**
     * Gravity Acceleration - if set to true then the gravity acceleration
     * value defined with the particle manager will be added to each
     * particle during update.
     */
    public boolean applyGravity;


    // /////////////////////////////////////////////////////////////////////////
    // Constructor
    // /////////////////////////////////////////////////////////////////////////

    /**
     * Create emitter settings using the specified JSON file
     *
     * The following JSON format is assumed:
     *
     {
     "name": string,                // Name of the emitter
     "emitterMode": string,         // EmitterMode type
     "particleSettings": string,    // Particle settings JSON file
     "minParticleDensity": int,     // Minimum generated particles
     "maxParticleDensity": int,     // Maximum generated particles
     "blendMode": string,           // BlendMode type
     "accelerationMode": string,    // AccelerationMode type
     "velocityBias": float,         // Velocity bias
     "applyGravity": bool           // True to apply gravity acceleration
     }     *
     *
     * @param assetManager            Game asset manager
     * @param emitterSettingsJSONFile JSON parameter file to load settings from
     */
    public EmitterSettings(AssetManager assetManager, String emitterSettingsJSONFile) {

        // Attempt to load in the JSON particle settings
        String loadedJSON;
        try {
            loadedJSON = assetManager.getFileIO().loadJSON(emitterSettingsJSONFile);
        } catch (IOException e) {
            throw new RuntimeException(
                "EmitterSettings.constructor: Cannot load JSON [" + emitterSettingsJSONFile + "]");
        }

        // Attempt to extract the JSON information
        try {
            JSONObject settings = new JSONObject(loadedJSON);

            name = settings.getString("name");

            emitterMode = EmitterMode.valueOf(settings.getString("emitterMode"));

            String particleSettingsJSON = settings.getString("particleSettings");
            particleSettings = new ParticleSettings(assetManager, particleSettingsJSON);

            minParticleDensity = settings.getInt("minParticleDensity");
            maxParticleDensity = settings.getInt("maxParticleDensity");

            blendMode = BlendMode.valueOf(settings.getString("blendMode"));

            accelerationMode = AccelerationMode.valueOf(settings.getString("accelerationMode"));

            velocityBias = (float) settings.getDouble("velocityBias");

            applyGravity = settings.getBoolean("applyGravity");

        } catch (JSONException | IllegalArgumentException e) {
            throw new RuntimeException(
                "EmitterSettings.constructor: JSON parsing error [" + e.getMessage() + "]");
        }
    }
}