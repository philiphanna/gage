package uk.ac.qub.eeecs.gage.engine.io;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Typeface;
import android.media.SoundPool;
import android.os.Environment;
import android.preference.PreferenceManager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import uk.ac.qub.eeecs.gage.R;
import uk.ac.qub.eeecs.gage.engine.audio.Music;
import uk.ac.qub.eeecs.gage.engine.audio.Sound;

/**
 * Input support across standard file stores
 *
 * @version 1.0
 */
public class FileIO {

    // /////////////////////////////////////////////////////////////////////////
    // Properties
    // /////////////////////////////////////////////////////////////////////////

    /**
     * Context for the file IO
     */
    private Context mContext;

    /**
     * Asset manager that will be used
     */
    private AssetManager mAndroidIO;

    /**
     * Location of the external storage
     */
    private String mExternalStoragePath;

    // /////////////////////////////////////////////////////////////////////////
    // Constructors
    // /////////////////////////////////////////////////////////////////////////

    /**
     * Create a new File IO service
     *
     * @param context Context to which this File IO will use
     */
    public FileIO(Context context) {
        mContext = context;
        mAndroidIO = context.getAssets();
        mExternalStoragePath = Environment.getExternalStorageDirectory()
                .getAbsolutePath() + File.separator;
    }

    // /////////////////////////////////////////////////////////////////////////
    // Asset IO //
    // /////////////////////////////////////////////////////////////////////////

    /**
     * Open an input stream to the named asset stored in the APK file.
     *
     * @param assetName Name of the asset to open for reading
     * @return InputStream that can be used to read the asset
     * @throws IOException if the asset cannot be opened
     */
    public InputStream readAsset(String assetName) throws IOException {
        return mAndroidIO.open(assetName);
    }

    /**
     * Load the specified bitmap using the specified format from the APK file.
     *
     * @param fileName Name of the bitmap to be loaded
     * @param format   Bitmap format to be used when loading the bitmap
     * @throws IOException if the asset cannot be opened or read.
     */
    public Bitmap loadBitmap(String fileName, Bitmap.Config format)
            throws IOException {

        Options options = new Options();
        options.inPreferredConfig = format;
        InputStream in = null;
        Bitmap bitmap = null;
        try {
            in = mAndroidIO.open(fileName);
            bitmap = BitmapFactory.decodeStream(in);
            if (bitmap == null) {
                String message = mContext.getApplicationContext()
                        .getResources().getString(R.string.WARNING_TAG)
                        + "Could not load bitmap [" + fileName + "]";
                throw new IOException(message);
            }
        } catch (IOException e) {
            String message = mContext.getApplicationContext().getResources()
                    .getString(R.string.WARNING_TAG)
                    + "Could not load bitmap [" + fileName + "]";
            throw new IOException(message);
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                }
            }
        }

        return bitmap;
    }

    /**
     * Load in the specified music file.
     *
     * @param fileName Name of the music asset to be loaded
     * @return Loaded Music instance, or null if the effect could not be loaded
     * @throws IOException if the asset cannot be opened or read.
     */
    public Music loadMusic(String fileName) throws IOException {
        try {
            AssetFileDescriptor assetDescriptor = mAndroidIO.openFd(fileName);
            return new Music(assetDescriptor);
        } catch (IOException e) {
            String message = mContext.getApplicationContext().getResources()
                    .getString(R.string.WARNING_TAG)
                    + "Could not load music [" + fileName + "]";
            throw new IOException(message);
        }
    }

    /**
     * Load in the specified sound effect file.
     *
     * @param filename Name of the sound asset to be loaded
     * @return Loaded Sound instance, or null if clip could not be loaded
     * @throws IOException if the asset cannot be opened or read.
     */
    public Sound loadSound(String filename, SoundPool soundPool)
            throws IOException {
        try {
            AssetFileDescriptor assetDescriptor = mAndroidIO.openFd(filename);
            int soundId = soundPool.load(assetDescriptor, 0);
            return new Sound(soundPool, soundId);
        } catch (IOException e) {
            String message = mContext.getApplicationContext().getResources()
                    .getString(R.string.WARNING_TAG)
                    + "Could not load sound [" + filename + "]";
            throw new IOException(message);
        }
    }

    /**
     * Load in the specified Typeface
     *
     * @param fileName Name of the typeface to load
     * @return Loaded Typeface
     * @throws IOException if the asset cannot be opened or read.
     */
    public Typeface loadFont(String fileName) throws IOException {
        try {
            return Typeface.createFromAsset(mAndroidIO, fileName);
        } catch (Exception e) {
            String message = mContext.getApplicationContext().getResources()
                    .getString(R.string.WARNING_TAG)
                    + "Could not load font [" + fileName + "]";
            throw new IOException(message);
        }
    }

    /**
     * Load in the specified JSON file.
     *
     * @param fileName Name of the JSON file to be loaded
     * @return Loaded JSON file
     * @throws IOException if the asset cannot be opened or read.
     */
    public String loadJSON(String fileName) throws IOException {

        String loadedJSON;
        InputStream in = null;
        try {
            in = mAndroidIO.open(fileName);
            byte[] data = new byte[in.available()];
            in.read(data);
            in.close();
            loadedJSON = new String(data, "UTF-8");

        } catch (IOException e) {
            String message = "Gage Warning:"
                + "Could not load JSON [" + fileName + "]";
            throw new IOException(message);
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                }
            }
        }

        return loadedJSON;
    }

    // /////////////////////////////////////////////////////////////////////////
    // Device Storage IO //
    // /////////////////////////////////////////////////////////////////////////

    /**
     * Open an input stream to the name file in device storage.
     *
     * @param fileName Name of the file to open for reading
     * @return InputStream that can be used to read the file
     * @throws IOException if the asset cannot be opened.
     */
    public InputStream readFile(String fileName) throws IOException {
        return new FileInputStream(mExternalStoragePath + fileName);
    }

    /**
     * Open an output stream to the name file in device storage.
     *
     * @param fileName Name of the file to open for writing
     * @return OutputStream that can be used to write to the file
     * @throws IOException if the asset cannot be opened for writing
     */
    public OutputStream writeFile(String fileName) throws IOException {
        return new FileOutputStream(mExternalStoragePath + fileName);
    }

    // /////////////////////////////////////////////////////////////////////////
    // Preferences IO //
    // /////////////////////////////////////////////////////////////////////////

    /**
     * Get the shared preferences for the app.
     *
     * @return Shared preferences instance
     */
    public SharedPreferences getPreferences() {
        return PreferenceManager.getDefaultSharedPreferences(mContext);
    }
}