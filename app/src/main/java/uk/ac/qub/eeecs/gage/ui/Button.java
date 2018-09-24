package uk.ac.qub.eeecs.gage.ui;

import uk.ac.qub.eeecs.gage.engine.ElapsedTime;
import uk.ac.qub.eeecs.gage.engine.graphics.IGraphics2D;
import uk.ac.qub.eeecs.gage.engine.input.Input;
import uk.ac.qub.eeecs.gage.engine.input.TouchEvent;
import uk.ac.qub.eeecs.gage.engine.input.TouchHandler;
import uk.ac.qub.eeecs.gage.util.BoundingBox;
import uk.ac.qub.eeecs.gage.util.GraphicsHelper;
import uk.ac.qub.eeecs.gage.util.ViewportHelper;
import uk.ac.qub.eeecs.gage.util.Vector2;
import uk.ac.qub.eeecs.gage.world.GameObject;
import uk.ac.qub.eeecs.gage.world.GameScreen;
import uk.ac.qub.eeecs.gage.world.LayerViewport;
import uk.ac.qub.eeecs.gage.world.ScreenViewport;

/**
 * Button base class. Provides touch detection for both screen space and layer
 * space buttons.
 *
 * @version 1.0
 */
public abstract class Button extends GameObject {

    // /////////////////////////////////////////////////////////////////////////
    // Properties
    // /////////////////////////////////////////////////////////////////////////

    /**
     * Specify if the button will be processed in layer space or screen space
     */
    protected boolean mProcessInLayerSpace = false;

    /**
     * Track if the button is currently pushed
     */
    protected boolean mIsTouched;

    /**
     * Private variable to track the most recent touch location
     */
    protected Vector2 mTouchLocation = new Vector2();

    // /////////////////////////////////////////////////////////////////////////
    // Constructors
    // /////////////////////////////////////////////////////////////////////////

    /**
     * Setup base Button properties
     *
     * @param x                   Centre x location of the button
     * @param y                   Centre y location of the button
     * @param width               Width of the button
     * @param height              Height of the button
     * @param baseButtonImage     Base bitmap used to represent this button
     * @param processInLayerSpace Specify if the button is to be processed in
     *                            layer space (screen by default)
     * @param gameScreen          Gamescreen to which this control belongs
     */
    public Button(float x, float y, float width, float height,
                  String baseButtonImage,
                  boolean processInLayerSpace,
                  GameScreen gameScreen) {
        super(x, y, width, height,
                baseButtonImage == null ? null : gameScreen.getGame().getAssetManager().getBitmap(baseButtonImage),
                gameScreen);
        this.mProcessInLayerSpace = processInLayerSpace;
    }


    // /////////////////////////////////////////////////////////////////////////
    // Methods
    // /////////////////////////////////////////////////////////////////////////

    /**
     * Determine if the button is processed in layer space or in screen space.
     * <p>
     * If processed in layer space a valid layer and screen viewport needs to be
     * passed to the update and draw methods. If in screen space the update and
     * draw methods that do not require a viewport parameter can be used.
     *
     * @param processInLayerSpace True if in layer space, false if in screen space.
     */
    public void processInLayerSpace(boolean processInLayerSpace) {
        mProcessInLayerSpace = processInLayerSpace;
    }

    /**
     * Update the button.
     *
     * @param elapsedTime Elapsed time information
     */
    public void update(ElapsedTime elapsedTime) {
        if(mProcessInLayerSpace)
            this.update(elapsedTime,
                    getGameScreen().getDefaultLayerViewport(),
                    getGameScreen().getDefaultScreenViewport());
        else
            this.update(elapsedTime, null, null);
    }

    /**
     * Update the button.
     *
     * @param elapsedTime    Elapsed time information
     * @param layerViewport  Layer viewport
     * @param screenViewport Screen viewport
     */
    public void update(ElapsedTime elapsedTime,
                       LayerViewport layerViewport, ScreenViewport screenViewport) {
        // Consider any touch events occurring in this update
        Input input = mGameScreen.getGame().getInput();

        BoundingBox bound = getBound();

        // Check for a trigger event on this button
        for (TouchEvent touchEvent : input.getTouchEvents()) {
            getTouchLocation(mTouchLocation, touchEvent.x, touchEvent.y,
                    layerViewport, screenViewport);
            if (bound.contains(mTouchLocation.x, mTouchLocation.y)) {
                // Check if a trigger has occurred and invoke specific behaviour
                updateTriggerActions(touchEvent, mTouchLocation);
            }
        }

        // Check for any touch events on this button
        for (int idx = 0; idx < TouchHandler.MAX_TOUCHPOINTS; idx++) {
            if (input.existsTouch(idx)) {
                getTouchLocation(mTouchLocation,
                        input.getTouchX(idx), input.getTouchY(idx),
                        layerViewport, screenViewport);
                if (bound.contains(mTouchLocation.x, mTouchLocation.y)) {
                    if (!mIsTouched) {
                        // Record the button has been touched and take button specific behaviour
                        mIsTouched = true;
                        updateTouchActions(mTouchLocation);
                    }
                    return;
                }
            }
        }

        // If we have not returned by this point, then there is no touch event on the button
        if (mIsTouched) {
            // Take any default button specific behaviour
            updateDefaultActions();
            mIsTouched = false;
        }
    }

    /**
     * Get the button touch location, converting from screen space to layer
     * space if needed. The touch location is stored within the specified
     * touchLocation vector.
     *
     * @param touchLocation  Touch location instance to be updated by this method.
     * @param x              Touch x screen location
     * @param y              Touch y screen location
     * @param layerViewport  Layer viewport
     * @param screenViewport Screen viewport
     */
    private void getTouchLocation(Vector2 touchLocation, float x, float y,
                                  LayerViewport layerViewport,
                                  ScreenViewport screenViewport) {
        if (!mProcessInLayerSpace) {
            // If in screen space just store the touch location
            touchLocation.x = x;
            touchLocation.y = y;
        } else {
            // If in layer screen convert and store the touch location
            ViewportHelper.convertScreenPosIntoLayer(screenViewport,
                    x, y, layerViewport, touchLocation);
        }
    }

    /**
     * Check for and undertake trigger actions for the button.
     * <p>
     * A trigger check is undertaken for each touch event that occurs
     * on this button. If detected, then the method will also take
     * appropriate trigger actions.
     *
     * @param touchEvent    Touch event that gave rise to the trigger
     * @param touchLocation Touch location at which the trigger occurred
     */
    protected abstract void updateTriggerActions(
            TouchEvent touchEvent, Vector2 touchLocation);

    /**
     * Undertake touch actions for the button.
     * <p>
     * These actions will be triggered each frame there is at least one
     * touch point on the button.
     *
     * @param touchLocation Touch location at which the trigger occurred
     */
    protected abstract void updateTouchActions(Vector2 touchLocation);

    /**
     * Undertake default actions for the untouched button.
     * <p>
     * The default action is triggered if there is no touch event on
     * the button. It is used to undertake non-touch behaviours, e.g.
     * setting the displayed image to the appropriate non-touch bitmap.
     */
    protected abstract void updateDefaultActions();

    /*
     * (non-Javadoc)
     *
     * @see
     * uk.ac.qub.eeecs.gage.world.GameObject#draw(uk.ac.qub.eeecs.gage.engine
     * .ElapsedTime, uk.ac.qub.eeecs.gage.engine.graphics.IGraphics2D,
     * uk.ac.qub.eeecs.gage.world.LayerViewport,
     * uk.ac.qub.eeecs.gage.world.ScreenViewport)
     */
    @Override
    public void draw(ElapsedTime elapsedTime, IGraphics2D graphics2D,
                     LayerViewport layerViewport, ScreenViewport screenViewport) {

        if (mProcessInLayerSpace) {
            // If in layer space, then determine an appropriate screen space bound
            if (GraphicsHelper.getClippedSourceAndScreenRect(this, layerViewport,
                    screenViewport, drawSourceRect, drawScreenRect))
                graphics2D.drawBitmap(mBitmap, drawSourceRect, drawScreenRect, null);
        } else {
            // If in screen space just draw the whole thing
            draw(elapsedTime, graphics2D);
        }
    }
}
