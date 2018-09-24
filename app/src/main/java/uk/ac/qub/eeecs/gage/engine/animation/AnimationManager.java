package uk.ac.qub.eeecs.gage.engine.animation;

import java.util.HashMap;
import java.util.Map;

import uk.ac.qub.eeecs.gage.engine.AssetManager;
import uk.ac.qub.eeecs.gage.engine.ElapsedTime;
import uk.ac.qub.eeecs.gage.engine.graphics.IGraphics2D;
import uk.ac.qub.eeecs.gage.world.GameObject;
import uk.ac.qub.eeecs.gage.world.LayerViewport;
import uk.ac.qub.eeecs.gage.world.ScreenViewport;

/**
 * Animation manager.
 *
 * @version 1.0
 */
public class AnimationManager {

    // /////////////////////////////////////////////////////////////////////////
    // Properties:
    // /////////////////////////////////////////////////////////////////////////

    /**
     * Game object instance that this manager is associated with
     */
    private GameObject mGameObject;

    /**
     * Collection of animations held within this manager
     */
    private Map<String, Animation> mAnimations = new HashMap<>();


    /**
     * Current animation facing assumed by the manager to be applied to
     * animation playback.
     */
    private Animation.Facing mAnimationFacing = Animation.Facing.Right;

    /**
     * Current animation that is being played by the manager
     */
    private Animation mCurrentAnimation;


    // /////////////////////////////////////////////////////////////////////////
    // Constructors:
    // /////////////////////////////////////////////////////////////////////////

    /**
     * Create a new animation manager
     *
     * @param managedGameObject Game object linked to this manager
     */
    public AnimationManager(GameObject managedGameObject) {
        mGameObject = managedGameObject;
    }


    // /////////////////////////////////////////////////////////////////////////
    // Methods: Accessor methods
    // /////////////////////////////////////////////////////////////////////////

    /**
     * Get the game object linked to this manager
     *
     * @return Game object linked ot this manager
     */
    public GameObject getGameObject() {
        return mGameObject;
    }


    // /////////////////////////////////////////////////////////////////////////
    // Methods: Animation Control
    // /////////////////////////////////////////////////////////////////////////

    /**
     * Add all animations defined within the specified settings file to this manager
     *
     * @param animationSettingsJSON JSON animation settings file
     */
    public void addAnimation(String animationSettingsJSON) {
        // Get the asset manager to load the animation settings if
        // this set of values has not already been loaded.
        AssetManager assetManager = mGameObject.getGameScreen().getGame().getAssetManager();
        assetManager.loadAndAddAnimation(
                animationSettingsJSON, animationSettingsJSON);
        AnimationSettings animationSettings =
            assetManager.getAnimation(animationSettingsJSON);

        // Add each stored animation
        for(int idx = 0; idx < animationSettings.numAnimations; idx++)
            addAnimation(new Animation(animationSettings, idx));
    }

    /**
     * Add the specified animation instance to this manager.
     *
     * Note: The first animation to be added to the manager is automatically
     * set as the current animation.
     *
     * @param animation Animation to add
     */
    public void addAnimation(Animation animation) {
        mAnimations.put(animation.getName(), animation);
        if(mCurrentAnimation == null)
            mCurrentAnimation = animation;
    }

    /**
     * Set the current animation.
     *
     * Note: Setting the current animation will be trigger playback of that animation.
     * The play method must be called to commence playback.
     *
     * @param animationName Name of the animation to use as the current animation
     */
    public void setCurrentAnimation(String animationName) {
        Animation animation = mAnimations.get(animationName);
        if(animation != null) {
            mCurrentAnimation = animation;
            mCurrentAnimation.setFacing(mAnimationFacing);
        } else {
            throw new RuntimeException(
                "AnimationManager.setCurrentAnimation: Cannot find animation [" + animationName + "]");
        }
    }

    /**
     * Return the current animation
     *
     * @return Current animation, null if no animation has been set as current
     */
    public Animation getCurrentAnimation() {
        return mCurrentAnimation;
    }

    /**
     * Remove the specified animation from the manager
     *
     * @param animationName Name of the animation to be removed
     * @return True if the animation could be found and removed, false otherwise
     */
    public boolean removeAnimation(String animationName ) {
        if(!mAnimations.containsKey(animationName))
            return false;

        mAnimations.remove(mAnimations.get(animationName));
        return true;
    }

    /**
     * Indicate if an animation is currently playing
     *
     * @return True if an animation is currently playing, otherwise false
     */
    public boolean isAnimationPlaying() {
        if( mCurrentAnimation == null)
            return false;
        return mCurrentAnimation.isPlaying();
    }

    /**
     * Set the facing of animations that are played
     *
     * @param facing Facing of animations that are played
     */
    public void setFacing(Animation.Facing facing) {
        mAnimationFacing = facing;
        if(mCurrentAnimation != null )
            mCurrentAnimation.setFacing((mAnimationFacing));
    }

    /**
     * Play the specified animation.
     *
     * If the specified animation is currently playing, then playback will continue.
     * In all other cases, the current animation will be set to the animation to be
     * played and playback will commence.
     *
     * @param animationName Name of the animation to play
     * @param elapsedTime Elapsed time information
     */
    public void play(String animationName, ElapsedTime elapsedTime) {
        // Return if the named animation is already playing
        if(mCurrentAnimation != null
                && mCurrentAnimation.getName().equals(animationName)
                && mCurrentAnimation.isPlaying())
            return;

        // Otherwise, retrieve the named animation, make it current and start playing
        Animation animation = mAnimations.get(animationName);
        if(animation != null) {
            mCurrentAnimation = animation;
            mCurrentAnimation.setFacing(mAnimationFacing);
            mCurrentAnimation.play(elapsedTime);
        } else {
            throw new RuntimeException(
                "AnimationManager.play: Cannot find animation [" + animationName + "]");
        }
    }

    /**
     * Play the current animation.
     *
     * If the current animation is already playing, then playback will continue.
     *
     * @param elapsedTime Elapsed time information
     */
    public void play(ElapsedTime elapsedTime) {
        if( mCurrentAnimation != null) {
            if(!mCurrentAnimation.isPlaying())
                mCurrentAnimation.play(elapsedTime);
        } else {
            throw new RuntimeException(
                "AnimationManager.play: No current animation to play.");
        }
    }

    /**
     * Immediately stop playback of the current animation
     */
    public void stop() {
        if( mCurrentAnimation != null) {
            mCurrentAnimation.stop();
        } else {
            throw new RuntimeException(
                "AnimationManager.stop: No current animation to stop.");
        }
    }

    /**
     * Update the current animation, ensuring the correct display frame is selected
     *
     * @param elapsedTime Elapsed time information
     */
    public void update(ElapsedTime elapsedTime) {
        if( mCurrentAnimation != null)
            mCurrentAnimation.update(elapsedTime);
    }


    // /////////////////////////////////////////////////////////////////////////
    // Methods: Draw
    // /////////////////////////////////////////////////////////////////////////

    /**
     /**
     * Draw the current animation.
     *
     * The game object associated with this manager will be used to provide
     * the object bound into which the animation will be drawn.
     *
     * @param elapsedTime Elapsed time information
     * @param graphics2D  Graphics instance
     * @param layerViewport Game viewport
     * @param screenViewport Screen viewport
     */
    public void draw(ElapsedTime elapsedTime, IGraphics2D graphics2D,
                     LayerViewport layerViewport, ScreenViewport screenViewport) {
        if( mCurrentAnimation != null)
            mCurrentAnimation.draw(elapsedTime, graphics2D, mGameObject,
                    layerViewport, screenViewport);

    }
}
