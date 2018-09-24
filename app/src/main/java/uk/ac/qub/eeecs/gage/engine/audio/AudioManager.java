package uk.ac.qub.eeecs.gage.engine.audio;

import android.media.AudioAttributes;
import android.media.SoundPool;

import uk.ac.qub.eeecs.gage.Game;

/**
 * Audio Manager - providing centralised control for the playback of
 * music and sound effects
 */
public class AudioManager {

    // /////////////////////////////////////////////////////////////////////////
    // Properties
    // /////////////////////////////////////////////////////////////////////////

    /**
     * Define the maximum, minimum and default music volume values.
     */
    private static final float MIN_MUSIC_VOLUME = 0.0f;
    private static final float MAX_MUSIC_VOLUME = 1.0f;
    private static final float DEFAULT_MUSIC_VOLUME = 0.75f;

    /**
     * Define the maximum, minimum and default sound effect values.
     */
    private static final float MIN_SFX_VOLUME = 0.0f;
    private static final float MAX_SFX_VOLUME = 1.0f;
    private static final float DEFAULT_SFX_VOLUME = 0.75f;

    /**
     * Define the current music volume
     */
    private float mMusicVolume = DEFAULT_MUSIC_VOLUME;

    /**
     * Define the current sound effect volume
     */
    private float mSfxVolume = DEFAULT_SFX_VOLUME;

    /**
     * Reference to the currently playing music instance if available
     */
    private Music mCurrentMusic;

    /**
     * Sound pool instance used to hold/play sound effects
     */
    private SoundPool mSoundPool;

    /**
     * Game instance to which this audio manager belongs to
     */
    private Game mGame;


    // /////////////////////////////////////////////////////////////////////////
    // Constructors
    // /////////////////////////////////////////////////////////////////////////

    /**
     * Create a new audio manager
     *
     * @param game Parent game instance
     */
    public AudioManager(Game game) {
        mGame = game;

        // Build a sound pool for loaded sfx
        AudioAttributes audioAttributes =
                new AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .setUsage(AudioAttributes.USAGE_GAME)
                        .build();
        mSoundPool = new SoundPool.Builder()
                .setMaxStreams(Sound.MAX_CONCURRENT_SOUNDS)
                .setAudioAttributes(audioAttributes)
                .build();

        // Request control of the volume
        mGame.getActivity().setVolumeControlStream(
                android.media.AudioManager.STREAM_MUSIC);
    }


    public SoundPool getSoundPool() {
        return mSoundPool;
    }


    // /////////////////////////////////////////////////////////////////////////
    // Methods: Music related
    // /////////////////////////////////////////////////////////////////////////

    /**
     * Returns the current music volume.
     *
     * @return Current music volume
     */
    public float getMusicVolume() {
        return mMusicVolume;
    }

    /**
     * Set the music volume.
     *
     * @param musicVolume Music volume to use
     */
    public void setMusicVolume(float musicVolume) {
        mMusicVolume = musicVolume < MIN_MUSIC_VOLUME ? MIN_MUSIC_VOLUME :
                (musicVolume > MAX_MUSIC_VOLUME ? MAX_MUSIC_VOLUME : musicVolume);

        if (mCurrentMusic != null)
            mCurrentMusic.setVolume(musicVolume);
    }

    /**
     * Play the specified music instance, stopping any playing music if needed
     *
      * @param music Music to play
     */
    public void playMusic(Music music) {
        // Stop any currently playing music
        if (mCurrentMusic != null && mCurrentMusic.isPlaying())
            mCurrentMusic.stop();

        // Start playback of the new music
        mCurrentMusic = music;
        mCurrentMusic.setVolume(mMusicVolume);
        mCurrentMusic.play();
    }

    /**
     * Pause playback of the current music instance
     */
    public void pauseMusic() {
        if (mCurrentMusic != null && mCurrentMusic.isPlaying())
            mCurrentMusic.pause();
    }

    /**
     * Resume playback of a paused music instance
     */
    public void resumeMusic() {
        if (mCurrentMusic != null && !mCurrentMusic.isPlaying())
            mCurrentMusic.play();
    }

    /**
     * Stop playback of the current music instance
     */
    public void stopMusic() {
        if (mCurrentMusic != null && mCurrentMusic.isPlaying())
            mCurrentMusic.stop();
    }

    /**
     * Report is music is currently playing
     *
     * @return True if music is currently playing, otherwise false
     */
    public boolean isMusicPlaying() {
        return mCurrentMusic != null && mCurrentMusic.isPlaying();
    }


    // /////////////////////////////////////////////////////////////////////////
    // Methods: Sound effect related
    // /////////////////////////////////////////////////////////////////////////

    /**
     * Set the sfx volume.
     *
     * Note changes in sfx volume will not change the volume of any sound
     * effect that is currently being played.
     *
     * @param sfxVolume Sfx volume to use
     */
    public void setSfxVolume(float sfxVolume) {
        mSfxVolume = sfxVolume < MIN_SFX_VOLUME ? MIN_SFX_VOLUME :
                (sfxVolume > MAX_SFX_VOLUME ? MAX_SFX_VOLUME : sfxVolume);
    }

    /**
     * Returns the current sfx volume.
     *
     * @return Current sfx volume
     */
    public float getSfxVolume() {
        return mSfxVolume;
    }

    /**
     * Play the sound effect
     *
     * @param sound Sfx to play
     */
    public void play(Sound sound) {
        sound.play(mSfxVolume);
    }

    /**
     * Play the sound effect
     *
     * @param sound  Sfx to play
     * @param volume Play back volume (0-1)
     */
    public void play(Sound sound, float volume) {
        sound.play(mSfxVolume * volume);
    }

    /**
     * Play the sound effect
     *
     * @param sound       Sfx to play
     * @param leftVolume  Left channel play back volume (0-1)
     * @param rightVolume Right channel play back volume (0-1)
     */
    public void play(Sound sound, float leftVolume, float rightVolume) {
        sound.play(mSfxVolume * leftVolume, mSfxVolume * rightVolume);
    }

    /**
     * Pause the playback of all currently playing sound effects
     */
    public void pauseSfx() {
        mSoundPool.autoPause();
    }

    /**
     * Resumed playback of any paused sound effects
     */
    public void resumeSfx() {
        mSoundPool.autoResume();
    }

    // /////////////////////////////////////////////////////////////////////////
    // Methods: Management
    // /////////////////////////////////////////////////////////////////////////

    /**
     * Stop any playing music and dispose of all loaded sound effects.
     */
    public void dispose() {
        if (isMusicPlaying())
            mCurrentMusic.stop();

        mSoundPool.release();
    }
}
