package uk.ac.qub.eeecs.gage.world;

import uk.ac.qub.eeecs.gage.Game;
import uk.ac.qub.eeecs.gage.engine.ElapsedTime;
import uk.ac.qub.eeecs.gage.engine.graphics.IGraphics2D;
import uk.ac.qub.eeecs.gage.util.ViewportHelper;

/**
 * Game screen class acting as a container for a coherent section of the game (a
 * level, configuration screen, etc.)
 *
 * @version 1.0
 */
public abstract class GameScreen {

    // /////////////////////////////////////////////////////////////////////////
    // Properties
    // /////////////////////////////////////////////////////////////////////////

    /**
     * Name that is given to this game screen
     */
    protected final String mName;

    /**
     * Return the name of this game screen
     *
     * @return Name of this game screen
     */
    public String getName() {
        return mName;
    }

    /**
     * Game to which game screen belongs
     */
    protected final Game mGame;

    /**
     * Return the game to which this game screen is attached
     *
     * @return Game to which screen is attached
     */
    public Game getGame() {
        return mGame;
    }

    /**
     * Define the default layer viewport for the game screen.
     */
    protected LayerViewport mDefaultLayerViewport;

    /**
     * Return the default layer viewport for this game screen
     */
    public LayerViewport getDefaultLayerViewport() {
        return mDefaultLayerViewport;
    }

    /**
     * Define the default screen viewport for the game screen.
     */
    protected ScreenViewport mDefaultScreenViewport;

    /**
     * Return the default screen viewport for this game screen
     */
    public ScreenViewport getDefaultScreenViewport() {
        return mDefaultScreenViewport;
    }

    // /////////////////////////////////////////////////////////////////////////
    // Constructors
    // /////////////////////////////////////////////////////////////////////////

    /**
     * Create a new game screen associated with the specified game instance
     *
     * @param game Game instance to which the game screen belongs
     */
    public GameScreen(String name, Game game) {
        mName = name;
        mGame = game;

        // Create the default layer and screen viewports
        mDefaultScreenViewport = new ScreenViewport();
        mDefaultLayerViewport = new LayerViewport();
        ViewportHelper.createDefaultLayerViewport(mDefaultLayerViewport);
        ViewportHelper.create3To2AspectRatioScreenViewport(
                game, mDefaultScreenViewport);
    }

    // /////////////////////////////////////////////////////////////////////////
    // Update and Draw
    // /////////////////////////////////////////////////////////////////////////

    /**
     * Update the game screen. Invoked automatically from the game.
     * <p>
     * NOTE: If the update is multi-threaded control should not be returned from
     * the update call until all update processes have completed.
     *
     * @param elapsedTime Elapsed time information for the frame
     */
    public abstract void update(ElapsedTime elapsedTime);

    /**
     * Draw the game screen. Invoked automatically from the game.
     *
     * @param elapsedTime Elapsed time information for the frame
     * @param graphics2D  Graphics instance used to draw the screen
     */
    public abstract void draw(ElapsedTime elapsedTime, IGraphics2D graphics2D);

    // /////////////////////////////////////////////////////////////////////////
    // Android Life Cycle
    // /////////////////////////////////////////////////////////////////////////

    /**
     * Invoked automatically by the game whenever the app is paused.
     */
    public void pause() {
    }

    /**
     * Invoked automatically by the game whenever the app is resumed.
     */
    public void resume() {
    }

    /**
     * Invoked automatically by the game whenever the app is disposed.
     */
    public void dispose() {
    }
}