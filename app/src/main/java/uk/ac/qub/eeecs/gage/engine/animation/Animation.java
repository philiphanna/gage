package uk.ac.qub.eeecs.gage.engine.animation;

import android.graphics.Bitmap;
import android.graphics.Rect;

import uk.ac.qub.eeecs.gage.engine.ElapsedTime;
import uk.ac.qub.eeecs.gage.engine.graphics.IGraphics2D;
import uk.ac.qub.eeecs.gage.util.BoundingBox;
import uk.ac.qub.eeecs.gage.util.GraphicsHelper;
import uk.ac.qub.eeecs.gage.world.GameObject;
import uk.ac.qub.eeecs.gage.world.LayerViewport;
import uk.ac.qub.eeecs.gage.world.ScreenViewport;

/**
 * Animation.
 *
 * Animated sequence of frames from a sprite sheet.
 */
public class Animation {

    // /////////////////////////////////////////////////////////////////////////
    // Properties: Animation Enums
    // /////////////////////////////////////////////////////////////////////////

    /**
     * Define the assumed animation facing (either to the Left or Right). Animations
     * drawn with a Left facing will have their frames swapped on the x-axis,
     * animations drawn with a Right facing will be drawn unaltered.
     */
    public enum Facing {Right, Left}


    // /////////////////////////////////////////////////////////////////////////
    // Properties:
    // /////////////////////////////////////////////////////////////////////////

    /**
     * Name of this animation.
     */
    private String mName;

    /**
     * Link to the sprite sheet used to draw this animation
     */
    private Bitmap mSpritesheet;

    /**
     * Number of rows and columns of frames in the sprite sheet.
     */
    private int mNumRows;
    private int mNumColumns;

    /**
     * Pixel width and height of each frame
     */
    private int mFrameWidth;
    private int mFrameHeight;

    /**
     * Start and end frames for the associated animation. It is assumed that
     * the frame will be expressed as a single integer index into a row-ordered
     * sprite sheet.
     */
    private int mStartFrame;
    private int mEndFrame;

    /**
     * Index of the current animation frame
     */
    private int mCurrentFrame;

    /**
     * Total duration of a single animation play through
     */
    private float mTotalPeriod;

    /**
     * Timestamp of when the animation was triggered
     */
    private double mAnimationStartTime;

    /**
     * Flag determining if the playback will be looped. If false, the animation
     * will be played only once and then remain on the defined end frame.
     */
    private boolean mLoopAnimation;

    /**
     * Flag determining if the animation is currently playing
     */
    private boolean mIsPlaying;

    /**
     * Facing of the animation.
     */
    private Facing mFacing;


    // /////////////////////////////////////////////////////////////////////////
    // Constructors:
    // /////////////////////////////////////////////////////////////////////////

    /**
     * Create an animation. If there is more than one defined animation
     * within the animation settings, then default to the initial animation.
     *
     * @param animationSettings Animation settings to use
     */
    public Animation(AnimationSettings animationSettings) {
        this(animationSettings, 0);
    }

    /**
     * Create an animation using the defined settings
     *
     * @param animationSettings Animation settings to use
     * @param animationIdx Index of the animation to use
     */
    public Animation(AnimationSettings animationSettings, int animationIdx) {

        // Store details of the sprite sheet
        mSpritesheet = animationSettings.spritesheet;
        mNumRows = animationSettings.numRows;
        mNumColumns = animationSettings.numColumns;

        mFrameWidth = animationSettings.spritesheet.getWidth() / mNumColumns;
        mFrameHeight = animationSettings.spritesheet.getHeight() / mNumRows;

        // Store details of the selected animation
        mName = animationSettings.name[animationIdx];

        mStartFrame = animationSettings.startFrame[animationIdx];
        mEndFrame = animationSettings.endFrame[animationIdx];

        mTotalPeriod = animationSettings.totalPeriod[animationIdx];
        mLoopAnimation = animationSettings.loopAnimation[animationIdx];

        // Set the current frame equal to the starting frame and use the default facing
        mCurrentFrame = animationSettings.startFrame[animationIdx];
        mFacing = Facing.Right;

        // Initially set playback to false
        mIsPlaying = false;
    }


    // /////////////////////////////////////////////////////////////////////////
    // Methods: Accessor methods
    // /////////////////////////////////////////////////////////////////////////

    /**
     * Get the name of this animation
     *
     * @return Animation name
     */
    public String getName() {
        return mName;
    }

    /**
     * Set the facing for this animation
     *
     * @param facing Animation facing
     */
    public void setFacing(Facing facing) {
        mFacing = facing;
    }

    /**
     * Get the facing for this animation
     *
     * @return Animation facing
     */
    public Facing getFacing() {
        return mFacing;
    }

    /**
     * Get the current frame of this animation
     */
    public int getCurrentFrame() { return mCurrentFrame; }

    // /////////////////////////////////////////////////////////////////////////
    // Methods: Playback control and update
    // /////////////////////////////////////////////////////////////////////////

    /**
     * Indicate if this animation is currently playing
     *
     * @return Boolean true if currently playing, otherwise false
     */
    public boolean isPlaying() {
        return mIsPlaying;
    }

    /**
     * Start playback of this animation.
     *
     * If the animation is currently playing then no action will be taken.
     *
      * @param elapsedTime Elapsed time
     */
    public void play(ElapsedTime elapsedTime) {
        // Only commence playback is the animation is not currently playing
        if(!mIsPlaying) {
            mAnimationStartTime = elapsedTime.totalTime;
            mCurrentFrame = mStartFrame;
            mIsPlaying = true;
        }
    }

    /**
     * Stop playback of this animation.
     */
    public void stop() {
        mIsPlaying = false;
    }

    /**
     * Update the animation, ensuring that an appropriate animation frame is selected.
     *
     * @param elapsedTime Elapsed time
     */
    public void update(ElapsedTime elapsedTime) {
        // Do nothing if the animation is not currently playing
        if (!mIsPlaying) return;

        // Determine the length of time the animation has been playing for
        float timeSinceAnimationStart =
                (float) (elapsedTime.totalTime - mAnimationStartTime);

        // If the animation period has been exceeded and the animation is not
        // looping, then end playback and default to the final frame of the animation
        if (!mLoopAnimation
                && timeSinceAnimationStart > mTotalPeriod) {
            mCurrentFrame = mEndFrame;
            mIsPlaying = false;
        } else {
            // Select an appropriate animation frame
            float animationPosition =
                    timeSinceAnimationStart / mTotalPeriod;
            animationPosition -= (int) animationPosition;

            mCurrentFrame = (int) (
                    (float) (mEndFrame - mStartFrame + 1) * animationPosition) + mStartFrame;
        }
    }


    // /////////////////////////////////////////////////////////////////////////
    // Methods: Draw
    // /////////////////////////////////////////////////////////////////////////

    // Define externally to avoid object creation costs
    private Rect sourceRect = new Rect();
    private Rect screenRect = new Rect();

    /**
     * Draw the current frame of the animation.
     *
     * The game object associated with this animation is used to provide
     * the object bound into which the animation will be drawn.
     *
     * @param elapsedTime Elapsed time information
     * @param graphics2D  Graphics instance
     * @param gameObject Game object associated with this animation
     * @param layerViewport Game viewport
     * @param screenViewport Screen viewport
     */
    public void draw(ElapsedTime elapsedTime, IGraphics2D graphics2D,
                     GameObject gameObject,
                     LayerViewport layerViewport, ScreenViewport screenViewport) {

        // Firstly determine if the game object associated with this animation is
        // visible and, if so, determine whole bitmap and screen viewport rectangles.
        if (GraphicsHelper.getClippedSourceAndScreenRect(
                gameObject.getBound(), mSpritesheet,
                layerViewport, screenViewport, sourceRect, screenRect)) {

            // The calculated source rectangle will apply to the entire sprite
            // sheet, shrink this down to this a single frame
            sourceRect.left /= mNumColumns;
            sourceRect.right /= mNumColumns;
            sourceRect.top /= mNumRows;
            sourceRect.bottom /= mNumRows;

            // Determine the location of the current frame within the sprite sheet
            int rowIdx = mCurrentFrame / mNumColumns;
            int colIdx = mCurrentFrame % mNumColumns;

            // Offset the shrunk source rectangle onto the current frame
            sourceRect.left = sourceRect.left + colIdx * mFrameWidth;
            sourceRect.right = sourceRect.right + colIdx * mFrameWidth;
            sourceRect.top = sourceRect.top + rowIdx * mFrameHeight;
            sourceRect.bottom = sourceRect.bottom + rowIdx * mFrameHeight;

            // If the facing is to the left, then swap the X source values
            if (mFacing == Facing.Left) {
                // Determine frame left and right bound separations
                int leftGap = sourceRect.left - mFrameWidth * colIdx;
                int rightGap = mFrameWidth*(colIdx+1)-sourceRect.right;
                // Swap the left and right bounds maintaining the gaps
                sourceRect.left = mFrameWidth*(colIdx+1) - leftGap;
                sourceRect.right = mFrameWidth*colIdx + rightGap;
            }

            // Draw the frame
            graphics2D.drawBitmap(mSpritesheet, sourceRect, screenRect, null);
        }
    }

    /**
     * Draw the current frame of the animation.
     *
     * The game object associated with this animation is used to provide
     * the object bound into which the animation will be drawn.
     *
     * Note: As viewports are not used then it is assumed that the game object,
     * and it's bounds are defined directly in terms of screen space.
     *
     * @param elapsedTime Elapsed time information
     * @param gameObject Game object associated with this animation
     * @param graphics2D  Graphics instance
     */
    public void draw(ElapsedTime elapsedTime, IGraphics2D graphics2D,
                     GameObject gameObject) {

        // Build a screen rect using the specified game object
        BoundingBox objectBound = gameObject.getBound();
        screenRect.left = (int) objectBound.getLeft();
        screenRect.right = (int) objectBound.getRight();
        screenRect.top = (int) objectBound.getBottom();
        screenRect.bottom = (int) objectBound.getTop();

        // Calculate a source rectangle for a single frame
        sourceRect.left = 0;
        sourceRect.right = mFrameWidth;
        sourceRect.top = 0;
        sourceRect.bottom = mFrameHeight;

        // Determine the location of the current frame within the sprite sheet
        int rowIdx = mCurrentFrame / mNumColumns;
        int colIdx = mCurrentFrame % mNumColumns;

        // Offset the shrunk source rectangle onto the current frame
        sourceRect.left = sourceRect.left + colIdx * mFrameWidth;
        sourceRect.right = sourceRect.right + colIdx * mFrameWidth;
        sourceRect.top = sourceRect.top + rowIdx * mFrameHeight;
        sourceRect.bottom = sourceRect.bottom + rowIdx * mFrameHeight;

        // If the facing is to the left, then swap the X source values
        if (mFacing == Facing.Left) {
            int temp = sourceRect.right;
            sourceRect.right = sourceRect.left;
            sourceRect.left = temp;
        }

        // Draw the frame
        graphics2D.drawBitmap(mSpritesheet, sourceRect, screenRect, null);
    }
}