package uk.ac.qub.eeecs.gage.engine.animation;

import android.graphics.Bitmap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import uk.ac.qub.eeecs.gage.engine.AssetManager;

/**
 * Animation settings.
 * <p>
 * Configuration settings used to define a sprite sheet animation
 *
 * @version 1.0
 */
public class AnimationSettings {

    // /////////////////////////////////////////////////////////////////////////
    // Properties: Declared public for speed of access
    // /////////////////////////////////////////////////////////////////////////

    /**
     * Sprite sheet holding the animation frames
     */
    public Bitmap spritesheet;

    /**
     * Number of rows in the sprite sheet.
     */
    public int numRows;

    /**
     * Number of columns in the sprite sheet.
     */
    public int numColumns;

    /**
     * Number of separate animations contained within the sprite sheet
     */
    public int numAnimations;

    /**
     * Names of each of the animations.
     */
    public String[] name;

    /**
     * Start frames for each of the animations. It is assumed that the frame index
     * will be expressed as a single integer index into a row-ordered sprite sheet.
     */
    public int[] startFrame;

    /**
     * End frames for each of the animations. It is assumed that the frame index
     * will be expressed as a single integer index into a row-ordered sprite sheet.
     */
    public int[] endFrame;

    /**
     * Total duration (in seconds) of each of the animations
     */
    public float[] totalPeriod;

    /**
     * Flag for each animation indicating if it is to be looped during playback
     */
    public boolean[] loopAnimation;


    // /////////////////////////////////////////////////////////////////////////
    // Constructor
    // /////////////////////////////////////////////////////////////////////////

    /**
     * Load animation details for the specified JSON file.
     *
     * The following JSON format is assumed:
     *
     {
         "spritesheet" : string,    // Sprite sheet image
         "numRows" : int,           // Num of rows in sheet
         "numColumns" : int,        // Num of cols in sheet
         "animations": [            // Array of 1, or more animations in sheet
             {
             "name": string,        // Name of this animation
             "startFrame" : int,    // Optional starting frame (0 if omitted)
             "endFrame" : int,      // Optional ending period (last frame if omitted)
             "totalPeriod" : float, // Period over which full animation plays
             "loopAnimation" : bool // True if the animation will loop
             },
             {
             "name": string,  // Repeated for other animations in sheet
                ...
             },
         ]
     }     *
     *
     * @param assetManager Game asset manager
     * @param animationSettingsJSONFile JSON parameter file to load settings from
     */
    public AnimationSettings(AssetManager assetManager, String animationSettingsJSONFile) {

        // Attempt to load in the JSON particle settings
        String loadedJSON;
        try {
            loadedJSON = assetManager.getFileIO().loadJSON(animationSettingsJSONFile);
        } catch (IOException e) {
            throw new RuntimeException(
                "AnimationSettings.constructor: Cannot load JSON [" + animationSettingsJSONFile + "]");
        }

        // Attempt to extract the JSON information
        try {
            JSONObject settings = new JSONObject(loadedJSON);

            String spritesheetFilename = settings.getString("spritesheet");
            assetManager.loadAndAddBitmap(spritesheetFilename, spritesheetFilename);
            spritesheet = assetManager.getBitmap(spritesheetFilename);
            if(spritesheet == null )
                throw new IOException("Cannot load ["+spritesheetFilename+"]");

            numRows = settings.getInt("numRows");
            numColumns = settings.getInt("numColumns");

            // Determine the number of stored animations and create storage space
            JSONArray animations = settings.getJSONArray("animations");
            numAnimations = animations.length();

            name = new String[numAnimations];
            startFrame = new int[numAnimations];
            endFrame= new int[numAnimations];
            totalPeriod= new float[numAnimations];
            loopAnimation = new boolean[numAnimations];

            // Load in details of each stored animation
            for (int idx = 0; idx < animations.length(); idx++){
                name[idx] = animations.getJSONObject(idx).getString("name");

                startFrame[idx] = animations.getJSONObject(idx).has("startFrame") ?
                        animations.getJSONObject(idx).getInt("startFrame") : 0;
                endFrame[idx] = animations.getJSONObject(idx).has("endFrame") ?
                        animations.getJSONObject(idx).getInt("endFrame") : numRows * numColumns -1;

                totalPeriod[idx] = (float) animations.getJSONObject(idx).getDouble("totalPeriod");

                loopAnimation[idx] = animations.getJSONObject(idx).getBoolean("loopAnimation");
            }

        } catch (JSONException | IllegalArgumentException e) {
            throw new RuntimeException(
                "AnimationSettings.constructor: JSON parsing error [" + e.getMessage() + "]");
        } catch (IOException e) {
            throw new RuntimeException(
                "AnimationSettings.constructor: Could not load sprite sheet[" + e.getMessage() + "]");
        }
    }
}