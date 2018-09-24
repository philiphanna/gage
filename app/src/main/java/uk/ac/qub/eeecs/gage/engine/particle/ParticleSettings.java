package uk.ac.qub.eeecs.gage.engine.particle;

import android.graphics.Bitmap;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import uk.ac.qub.eeecs.gage.engine.AssetManager;

/**
 * Particle settings.
 *
 * Configuration parameters used to control particle creation.
 *
 * @version 1.0
 */
public class ParticleSettings {

    // /////////////////////////////////////////////////////////////////////////
    // Properties: Declared public for speed of access
    // /////////////////////////////////////////////////////////////////////////

    /**
     * Particle settings name
     */
    public String name;

    /**
     * Size settings
     */
    public float width;
    public float height;

    /**
     * Velocity range
     */
    public float minVelocityDirection;
    public float maxVelocityDirection;
    public float minVelocityMagnitude;
    public float maxVelocityMagnitude;

    /**
     * Acceleration range
     */
    public float minAccelerationDirection;
    public float maxAccelerationDirection;
    public float minAccelerationMagnitude;
    public float maxAccelerationMagnitude;

    /**
     * Orientation range
     */
    public float minOrientation;
    public float maxOrientation;

    /**
     * Angular velocity range
     */
    public float minAngularVelocity;
    public float maxAngularVelocity;

    /**
     * Scale range
     */
    public float minScale;
    public float maxScale;

    /**
     * Scale growth range
     */
    public float minScaleGrowth;
    public float maxScaleGrowth;

    /**
     * Lifespan range
     */
    public float minLifespan;
    public float maxLifespan;

    /**
     * FadeInBy range
     */
    public float minFadeInBy;
    public float maxFadeInBy;

    /**
     * FadeOutFrom range
     */
    public float minFadeOutFrom;
    public float maxFadeOutFrom;

    /**
	 * Bitmap to be used when drawing this particle
	 */
	public Bitmap bitmap;

	// /////////////////////////////////////////////////////////////////////////
	// Constructor
	// /////////////////////////////////////////////////////////////////////////

    /**
     * Create particle settings using the specified JSON file
     *
     * The JSON file assumes the following format (variables as
     * defined wihtin this class):
     *
     {
     "name": string,
     "width": float,
     "height": float,
     "minVelocityDirection": float,
     "maxVelocityDirection": float,
     "minVelocityMagnitude": float,
     "maxVelocityMagnitude": float,
     "minAccelerationDirection": float,
     "maxAccelerationDirection": float,
     "minAccelerationMagnitude": float,
     "maxAccelerationMagnitude": float,
     "minOrientation": float,
     "maxOrientation": float,
     "minAngularVelocity": float,
     "maxAngularVelocity": float,
     "minScale": float,
     "maxScale": float,
     "minScaleGrowth": float,
     "maxScaleGrowth": float,
     "minLifespan": float,
     "maxLifespan": float,
     "minFadeInBy": float,
     "maxFadeInBy": float,
     "minFadeOutFrom": float,
     "maxFadeOutFrom": float,
     "bitmapFilename": String
     }     *
     *
     * @param assetManager Game asset manager
     * @param particleSettingsJSONFile JSON parameter file to load settings from
     */
    public ParticleSettings(AssetManager assetManager, String particleSettingsJSONFile ) {

        // Attempt to load in the JSON particle settings
        String loadedJSON;
        try {
            loadedJSON = assetManager.getFileIO().loadJSON(particleSettingsJSONFile );
        } catch (IOException e) {
            throw new RuntimeException(
                "ParticleSettings.constructor: Cannot load JSON [" + particleSettingsJSONFile + "]");
        }

        // Attempt to extract the JSON information
        try {
            JSONObject settings = new JSONObject(loadedJSON);

            name = settings.getString("name");

            width = (float)settings.getDouble("width");
            height = (float)settings.getDouble("height");

            minVelocityDirection = (float)settings.getDouble("minVelocityDirection");
            maxVelocityDirection = (float)settings.getDouble("maxVelocityDirection");
            minVelocityMagnitude = (float)settings.getDouble("minVelocityMagnitude");
            maxVelocityMagnitude = (float)settings.getDouble("maxVelocityMagnitude");

            minAccelerationDirection = (float)settings.getDouble("minAccelerationDirection");
            maxAccelerationDirection = (float)settings.getDouble("maxAccelerationDirection");
            minAccelerationMagnitude = (float)settings.getDouble("minAccelerationMagnitude");
            maxAccelerationMagnitude = (float)settings.getDouble("maxAccelerationMagnitude");

            minOrientation = (float)settings.getDouble("minOrientation");
            maxOrientation = (float)settings.getDouble("maxOrientation");

            minAngularVelocity = (float)settings.getDouble("minAngularVelocity");
            maxAngularVelocity = (float)settings.getDouble("maxAngularVelocity");

            minScale = (float)settings.getDouble("minScale");
            maxScale = (float)settings.getDouble("maxScale");

            minScaleGrowth = (float)settings.getDouble("minScaleGrowth");
            maxScaleGrowth = (float)settings.getDouble("maxScaleGrowth");

            minLifespan = (float)settings.getDouble("minLifespan");
            maxLifespan = (float)settings.getDouble("maxLifespan");

            minFadeInBy = (float)settings.getDouble("minFadeInBy");
            minFadeInBy = (float)settings.getDouble("maxFadeInBy");

            minFadeOutFrom = (float)settings.getDouble("minFadeOutFrom");
            maxFadeOutFrom = (float)settings.getDouble("maxFadeOutFrom");

            String bitmapFilename = settings.getString("bitmapFilename");
            assetManager.loadAndAddBitmap(bitmapFilename, bitmapFilename);
            bitmap = assetManager.getBitmap(bitmapFilename);
            if(bitmap == null )
                throw new IOException("Cannot load ["+bitmapFilename+"]");

        } catch (JSONException e) {
            throw new RuntimeException(
                "ParticleSettings.constructor: JSON parsing error [" + e.getMessage() + "]");
        } catch (IOException e) {
            throw new RuntimeException(
                "ParticleSettings.constructor: Could not load bitmap [" + e.getMessage() + "]");
        }
	}
}