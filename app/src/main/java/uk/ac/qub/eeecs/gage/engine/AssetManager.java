package uk.ac.qub.eeecs.gage.engine;

import android.graphics.Bitmap;
import android.graphics.Typeface;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;

import uk.ac.qub.eeecs.gage.Game;
import uk.ac.qub.eeecs.gage.engine.animation.AnimationSettings;
import uk.ac.qub.eeecs.gage.engine.audio.Music;
import uk.ac.qub.eeecs.gage.engine.audio.Sound;
import uk.ac.qub.eeecs.gage.engine.io.FileIO;

/**
 * Asset manager for holding loaded assets.
 *
 * @version 1.0
 */
public class AssetManager {

    // /////////////////////////////////////////////////////////////////////////
    // Properties
    // /////////////////////////////////////////////////////////////////////////

    /**
     * Asset types
     */
    public enum AssetType {
        Bitmap, Music, Sound, Font, Animation
    }

    /**
     * Bitmap asset store
     */
    private HashMap<String, Bitmap> mBitmaps;

    /**
     * Music asset store
     */
    private HashMap<String, Music> mMusic;

    /**
     * Sound asset store
     */
    private HashMap<String, Sound> mSounds;

    /**
     * Font asset store
     */
    private HashMap<String, Typeface> mFonts;

    /**
     * Animation settings store
     */
    private HashMap<String, AnimationSettings> mAnimations;

    /**
     * File IO
     */
    private FileIO mFileIO;

    /**
     * Game to which this manager belongs
     */
    private Game mGame;

    // /////////////////////////////////////////////////////////////////////////
    // Constructors
    // /////////////////////////////////////////////////////////////////////////

    /**
     * Create a new asset store
     *
     * @param game Game instance to which this manager belongs
     */
    public AssetManager(Game game) {
        // Store the game and get it's File IO
        mGame = game;
        mFileIO = mGame.getFileIO();

        // Build hash maps for each asset
        mBitmaps = new HashMap<>();
        mMusic = new HashMap<>();
        mSounds = new HashMap<>();
        mFonts = new HashMap<>();
        mAnimations = new HashMap<>();
    }

    // /////////////////////////////////////////////////////////////////////////
    // Store //
    // /////////////////////////////////////////////////////////////////////////

    /**
     * Add the specified bitmap asset to the manager
     *
     * @param assetName Name given to the asset
     * @param asset     Bitmap asset to add
     * @return boolean true if the asset could be added, false it not (e.g. an
     * asset with the specified name already exists).
     */
    public boolean add(String assetName, Bitmap asset) {
        if (mBitmaps.containsKey(assetName))
            return false;

        mBitmaps.put(assetName, asset);
        return true;
    }

    /**
     * Add the specified music asset to the manager
     *
     * @param assetName Name given to the asset
     * @param asset     Music asset to add
     * @return boolean true if the asset could be added, false it not (e.g. an
     * asset with the specified name already exists).
     */
    public boolean add(String assetName, Music asset) {
        if (mBitmaps.containsKey(assetName))
            return false;

        mMusic.put(assetName, asset);
        return true;
    }

    /**
     * Add the specified sound asset to the manager
     *
     * @param assetName Name given to the asset
     * @param asset     Sound asset to add
     * @return boolean true if the asset could be added, false it not (e.g. an
     * asset with the specified name already exists).
     */
    public boolean add(String assetName, Sound asset) {
        if (mSounds.containsKey(assetName))
            return false;

        mSounds.put(assetName, asset);
        return true;
    }

    /**
     * Add the specified font to the manager
     *
     * @param assetName Name given to the asset
     * @param asset     Typeface asset to add
     * @return boolean true if the asset could be added, false it not (e.g. an
     * asset with the specified name already exists).
     */
    public boolean add(String assetName, Typeface asset) {
        if (mFonts.containsKey(assetName))
            return false;

        mFonts.put(assetName, asset);
        return true;
    }

    /**
     * Add the specified animation settings file to the manager
     *
     * @param assetName Name given to the asset
     * @param asset     Animation settings asset to add
     * @return boolean true if the asset could be added, false it not (e.g. an
     * asset with the specified name already exists).
     */
    public boolean add(String assetName, AnimationSettings asset) {
        if (mAnimations.containsKey(assetName))
            return false;

        mAnimations.put(assetName, asset);
        return true;
    }

    /**
     * Load and add the specified bitmap asset to the manager
     *
     * @param assetName  Name given to the asset
     * @param bitmapFile Location of the bitmap asset
     * @return boolean true if the asset could be added, false it not (e.g. an
     *          asset with the specified name already exists).
     */
    public boolean loadAndAddBitmap(String assetName, String bitmapFile) {
        if (mBitmaps.containsKey(assetName))
            return false;

        try {
            Bitmap bitmap = mFileIO.loadBitmap(bitmapFile, null);
            return add(assetName, bitmap);
        } catch (IOException e) {
            throw new RuntimeException(
                "AssetManager.loadAndAddBitmap: Cannot load [" + bitmapFile + "]");
        }
    }

    /**
     * Load and add the specified music asset to the manager
     *
     * @param assetName Name given to the asset
     * @param musicFile Location of the music asset
     * @return boolean true if the asset could be added, false it not (e.g. an
     *          asset with the specified name already exists).
     */
    public boolean loadAndAddMusic(String assetName, String musicFile) {
        if (mMusic.containsKey(assetName))
            return false;

        try {
            Music music = mFileIO.loadMusic(musicFile);
            return add(assetName, music);
        } catch (IOException e) {
            throw new RuntimeException(
                    "AssetManager.loadAndAddMusic: Cannot load [" + musicFile + "]");
        }
    }

    /**
     * Load and add the specified sound asset to the manager
     *
     * @param assetName Name given to the asset
     * @param soundFile Location of the sound asset
     * @return boolean true if the asset could be added, false it not (e.g. an
     *          asset with the specified name already exists).
     */
    public boolean loadAndAddSound(String assetName, String soundFile) {
        if (mSounds.containsKey(assetName))
            return false;

        try {
            Sound sound = mFileIO.loadSound(soundFile,
                    mGame.getAudioManager().getSoundPool());
            return add(assetName, sound);
        } catch (IOException e) {
            throw new RuntimeException(
                "AssetManager.loadAndAddSound: Cannot load [" + soundFile + "]");
        }
    }

    /**
     * Load and add the specified font asset to the store
     *
     * @param assetName Name given to the asset
     * @param fontFile  Location of the font asset
     * @return boolean true if the asset could be loaded and added, false if not
     */
    public boolean loadAndAddFont(String assetName, String fontFile) {
        if (mFonts.containsKey(assetName))
            return false;

        try {
            Typeface typeFace = mFileIO.loadFont(fontFile);
            return add(assetName, typeFace);
        } catch (IOException e) {
            throw new RuntimeException(
                    "AssetManager.loadAndAddFont: Cannot load [" + fontFile + "]");
        }
    }

    /**
     * Load and add the specified animation settings asset to the store
     *
     * @param assetName Name given to the asset
     * @param animationSettingsFile  Location of the animation settings asset
     * @return boolean true if the asset could be loaded and added, false if not
     */
    public boolean loadAndAddAnimation(String assetName, String animationSettingsFile) {
        if (mAnimations.containsKey(assetName))
            return false;

        AnimationSettings animationSettings =
                new AnimationSettings(this, animationSettingsFile);
        return add(assetName, animationSettings);
    }

    /**
     * Load in the list of assets from the specified JSON file.
     *
     * The JSON file assumes the following format:
     *
     {
         "assets": [
             {
                 "type": string,    // Asset type
                 "name": string,    // Asset name
                 "file": string     // Asset location
             },
             {
                 "type": string,    // Asset type
                 "name": string,    // Asset name
                 "file": string     // Asset location
             }
         ]
     }     *
     *
     * @param assetsToLoadJSONFile JSON file to load and process
     */
    public void loadAssets(String assetsToLoadJSONFile) {
        // Attempt to load in the JSON asset details
        String loadedJSON;
        try {
            loadedJSON = mFileIO.loadJSON(assetsToLoadJSONFile);
        } catch (IOException e) {
            throw new RuntimeException(
                    "AssetManager.constructor: Cannot load JSON [" + assetsToLoadJSONFile + "]");
        }

        // Attempt to extract the JSON information
        try {
            JSONObject settings = new JSONObject(loadedJSON);
            JSONArray assets = settings.getJSONArray("assets");

            // Load in each asset
            for (int idx = 0; idx < assets.length(); idx++){
                AssetType assetType =
                        AssetType.valueOf(assets.getJSONObject(idx).getString("type"));
                String assetName = assets.getJSONObject(idx).getString("name");
                String fileName = assets.getJSONObject(idx).getString("file");

                switch(assetType) {
                    case Bitmap:
                        loadAndAddBitmap(assetName, fileName);
                        break;
                    case Music:
                        loadAndAddMusic(assetName, fileName);
                        break;
                    case Sound:
                        loadAndAddSound(assetName, fileName);
                        break;
                    case Font:
                        loadAndAddFont(assetName, fileName);
                        break;
                    case Animation:
                        loadAndAddAnimation(assetName, fileName);
                        break;
                }
            }

        } catch (JSONException | IllegalArgumentException e) {
            throw new RuntimeException(
                    "AssetManager.constructor: JSON parsing error [" + e.getMessage() + "]");
        }
    }

    /**
     * Retrieve the specified bitmap asset from the manager
     *
     * @param assetName Name of the asset to retrieve
     * @return Specified Bitmap asset
     */
    public Bitmap getBitmap(String assetName) {
        if(!mBitmaps.containsKey(assetName))
            throw new RuntimeException(
                "AssetManager.getBitmap: Cannot find [" + assetName + "]");

        return mBitmaps.get(assetName);
    }

    /**
     * Retrieve the specified music asset from the manager
     *
     * @param assetName Name of the asset to retrieve
     * @return Specified music asset
     */
    public Music getMusic(String assetName) {
        if(!mMusic.containsKey(assetName))
            throw new RuntimeException(
                    "AssetManager.getMusic: Cannot find [" + assetName + "]");

        return mMusic.get(assetName);
    }

    /**
     * Retrieve the specified sound asset from the manager
     *
     * @param assetName Name of the asset to retrieve
     * @return Specified sound asset
     */
    public Sound getSound(String assetName) {
        if(!mSounds.containsKey(assetName))
            throw new RuntimeException(
                    "AssetManager.getSound: Cannot find [" + assetName + "]");

        return mSounds.get(assetName);
    }

    /**
     * Retrieve the specified typeface asset from the manager
     *
     * @param assetName Name of the asset to retrieve
     * @return Specified font asset
     */
    public Typeface getFont(String assetName) {
        if(!mFonts.containsKey(assetName))
            throw new RuntimeException(
                    "AssetManager.getFont: Cannot find [" + assetName + "]");

        return mFonts.get(assetName);
    }

    /**
     * Retrieve the specified animation settings asset from the manager
     *
     * @param assetName Name of the asset to retrieve
     * @return Specified animation settings asset
     */
    public AnimationSettings getAnimation(String assetName) {
        if(!mAnimations.containsKey(assetName))
            throw new RuntimeException(
                    "AssetManager.getAnimation: Cannot find [" + assetName + "]");

        return mAnimations.get(assetName);
    }


    // /////////////////////////////////////////////////////////////////////////
    // Misc
    // /////////////////////////////////////////////////////////////////////////

    /**
     * Return the FileIO for this AssetManager
     *
     * @return FileIO instance
     */
    public FileIO getFileIO() {
        return mFileIO;
    }
}