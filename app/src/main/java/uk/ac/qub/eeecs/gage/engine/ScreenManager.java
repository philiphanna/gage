package uk.ac.qub.eeecs.gage.engine;

import java.util.Stack;

import uk.ac.qub.eeecs.gage.Game;
import uk.ac.qub.eeecs.gage.world.GameScreen;

/**
 * The screen manager stores the available screens defined within the game.
 * Screens can be added or remove to reflect the evolution of the game. Within
 * the central game loop, the current game screen will be retrieved and
 * updated/rendered.
 *
 * A stack structure is used, with the screen at the top of the stack considered
 * to be the current screen that should be updated and rendered.
 *
 * @version 1.0
 */
public class ScreenManager {

    // /////////////////////////////////////////////////////////////////////////
    // Properties
    // /////////////////////////////////////////////////////////////////////////

    /**
     * Collection of available game screens
     */
    private Stack<GameScreen> mGameScreens;

    /**
     * Game instance
     */
    private Game mGame;

    // /////////////////////////////////////////////////////////////////////////
    // Constructors
    // /////////////////////////////////////////////////////////////////////////

    /**
     * Create a new screen manager
     *
     * @param game Game instance
     */
    public ScreenManager(Game game) {
        mGame = game;
        mGameScreens = new Stack<>();
    }

    // /////////////////////////////////////////////////////////////////////////
    // Methods
    // /////////////////////////////////////////////////////////////////////////

    /**
     * Add the specified game screen to the manager.
     * <p>
     * Note: When added to the screen manager a screen will automatically become
     * the current game screen (to be updated and rendered).
     *
     * @param screen GameScreen instance to be added
     * @return Boolean true if the screen was added, false if the screen could
     * not be added (a screen with the specified name already exists).
     */
    public boolean addScreen(GameScreen screen) {
        // Add the game screen if the specified name isn't already added
        if (mGameScreens.contains(screen))
            return false;

        mGameScreens.push(screen);
        return true;
    }

    /**
     * Return the current game screen.
     *
     * @return Current game instance instance, or null if no current game screen
     * has been defined.
     */
    public GameScreen getCurrentScreen() {
        return mGameScreens.peek();
    }

    /**
     * Return the named game screen.
     *
     * @param name String name reference for the target screen.
     * @return Current game instance instance, or null if no the specified game
     * screen could not be found.
     */
    public GameScreen getScreen(String name) {
        for(GameScreen gameScreen : mGameScreens) {
            if(gameScreen.getName().compareTo(name) == 0)
                return gameScreen;
        }
        return null;
    }

    /**
     * Remove the specified game screen from the manager.
     * <p>
     * Note: Remove a screen from the manager will not result in dispose being
     * automatically called on the removed screen.
     *
     * @param gameScreen Reference to the screen to remove.
     * @return Boolean true if the screen was removed, false otherwise (the
     * specified screen could not be found).
     */
    public boolean removeScreen(GameScreen gameScreen) {
         return mGameScreens.remove(gameScreen);
    }

    /**
     * Remove the specified game screen from the manager.
     * <p>
     * Note: Remove a screen from the manager will not result in dispose being
     * automatically called on the removed screen.
     *
     * @param name String name reference for the screen to remove.
     * @return Boolean true if the screen was removed, false otherwise (the
     * specified screen could not be found).
     */
    public boolean removeScreen(String name) {
        GameScreen screenToRemove = null;
        for(GameScreen gameScreen : mGameScreens) {
            if(gameScreen.getName().compareTo(name) == 0)
                screenToRemove = gameScreen;
        }

        if(screenToRemove == null)
            return false;
        else
            return mGameScreens.remove(screenToRemove);
    }

    /**
     * Remove all screens held by this screen manager.
     */
    public void removeAllScreens() {
        mGameScreens.clear();
    }

    /**
     * Dispose of the manager and all game screens stored within the manager.
     */
    public void dispose() {
        for (GameScreen gameScreen : mGameScreens )
            gameScreen.dispose();
    }
}
