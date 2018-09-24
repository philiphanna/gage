package uk.ac.qub.eeecs.gage.engine.audio;

import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;

import java.io.IOException;

/**
 * Music clip
 *
 * @version 1.1
 */
public class Music implements OnCompletionListener {

    // /////////////////////////////////////////////////////////////////////////
    // Properties
    // /////////////////////////////////////////////////////////////////////////

    /**
     * Media player that will be used to playback this music clip
     */
    private MediaPlayer mMediaPlayer;

    /**
     * Flag indicating if playback can commence
     */
    private boolean mIsPrepared = false;

    /**
     * Asset filename
     */
    private String mAssetFile;

    // /////////////////////////////////////////////////////////////////////////
    // Constructors
    // /////////////////////////////////////////////////////////////////////////

    /**
     * Create a new music clip
     *
     * @param assetDescriptor Asset descriptor linked to this audio file
     */
    public Music(AssetFileDescriptor assetDescriptor) {
        mAssetFile = assetDescriptor.getFileDescriptor().toString();

        // Create a new play player linked to the specified music asset
        mMediaPlayer = new MediaPlayer();
        try {
            // Link the data source
            mMediaPlayer.setDataSource(assetDescriptor.getFileDescriptor(),
                    assetDescriptor.getStartOffset(),
                    assetDescriptor.getLength());

            // Prep the audio for playback
            mMediaPlayer.prepare();
            mIsPrepared = true;

            // Add an on completion listener for the clip
            mMediaPlayer.setOnCompletionListener(this);
        } catch (IOException e) {
            throw new RuntimeException(
                "Music clip " + mAssetFile + " cannot be loaded.");
        }
    }

    // /////////////////////////////////////////////////////////////////////////
    // Methods
    // /////////////////////////////////////////////////////////////////////////

    /**
     * Play the music clip.
     * <p>
     * Note: If the music clip is already playing the play request is ignored.
     */
    public void play() {
        if (mMediaPlayer.isPlaying())
            return;
        try {
            synchronized (this) {
                // Start the clip, preparing it if needed
                if (!mIsPrepared)
                    mMediaPlayer.prepare();
                mMediaPlayer.start();
            }
        } catch (Exception e) { // Either IllegalStateException or IOException
            throw new RuntimeException(
                "Music clip " + mAssetFile + " cannot be played.");
        }
    }

    /**
     * Stop the music clip.
     */
    public void stop() {
        mMediaPlayer.stop();
        synchronized (this) {
            mIsPrepared = false;
        }
    }

    /**
     * Pause the music clip.
     */
    public void pause() {
        if (mMediaPlayer.isPlaying())
            mMediaPlayer.pause();
    }

    /**
     * Determine if the music clip will loop
     *
     * @param looping Boolean true to loop, false for play once.
     */
    public void setLopping(boolean looping) {
        mMediaPlayer.setLooping(looping);
    }

    /**
     * Set the playback volume
     *
     * @param volume Playback volume (0-1)
     */
    public void setVolume(float volume) {
        mMediaPlayer.setVolume(volume, volume);
    }

    /**
     * Set the playback volume
     *
     * @param leftVolume  Left channel playback volume (0-1)
     * @param rightVolume Right channel playback volume (0-1)
     */
    public void setVolume(float leftVolume, float rightVolume) {
        mMediaPlayer.setVolume(leftVolume, rightVolume);
    }

    /**
     * Determine if the music clip is currently playing
     *
     * @return Boolean true if the music clip is currently playing, otherwise
     * false
     */
    public boolean isPlaying() {
        return mMediaPlayer.isPlaying();
    }

    /**
     * Determine if the music clip is set to loop
     *
     * @return Boolean true if the clip is looping, otherwise false
     */
    public boolean isLooping() {
        return mMediaPlayer.isLooping();
    }

    /**
     * Dispose of the music clip
     */
    public void dispose() {
        if (mMediaPlayer.isPlaying())
            mMediaPlayer.stop();
        mMediaPlayer.release();
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * android.media.MediaPlayer.OnCompletionListener#onCompletion(android.media
     * .MediaPlayer)
     */
    public void onCompletion(MediaPlayer player) {
        synchronized (this) {
            mIsPrepared = false;
        }
    }
}
