package uk.ac.qub.eeecs.gage.util;

import uk.ac.qub.eeecs.gage.Game;
import uk.ac.qub.eeecs.gage.world.LayerViewport;
import uk.ac.qub.eeecs.gage.world.ScreenViewport;

/**
 * Collection of viewport related utility methods
 */
public final class ViewportHelper {

    // /////////////////////////////////////////////////////////////////////////
    // Screen and Layer Mappings
    // /////////////////////////////////////////////////////////////////////////

    /**
     * Convert the specified screen position from the screen viewport into
     * the coordinate space defined for the layer viewport, storing the result
     * in the specified layer Vector2 position.
     *
     * @param screenViewport Screen viewport enclosing the screen position
     * @param screenPosition Screen position
     * @param layerViewport  Layer viewport
     * @param layerPosition  Calculated layer position
     */
    public static void convertScreenPosIntoLayer(
            ScreenViewport screenViewport, Vector2 screenPosition,
            LayerViewport layerViewport, Vector2 layerPosition) {

        // Convert screen coordinate into [-0.5, 0.5] range
        float screenXRatio = (screenPosition.x - screenViewport.left)
                / (float) (screenViewport.right - screenViewport.left) - 0.5f;
        float screenYRatio = (screenPosition.y - screenViewport.bottom)
                / (float) (screenViewport.top - screenViewport.bottom) - 0.5f;

        // Determine layer coordinate
        layerPosition.x = layerViewport.x + 2.0f * screenXRatio * layerViewport.halfWidth;
        layerPosition.y = layerViewport.y + 2.0f * screenYRatio * layerViewport.halfHeight;
    }

    /**
     * Convert the specified screen position from the screen viewport into
     * the coordinate space defined for the layer viewport, storing the result
     * in the specified layer Vector2 position.
     *
     * @param screenViewport Screen viewport enclosing the screen position
     * @param screenX        Screen x position
     * @param screenY        Screen y position
     * @param layerViewport  Layer viewport
     * @param layerPosition  Calculated layer position
     */
    public static void convertScreenPosIntoLayer(
            ScreenViewport screenViewport, float screenX, float screenY,
            LayerViewport layerViewport, Vector2 layerPosition) {

        // Convert screen coordinate into [-0.5, 0.5] range
        float screenXRatio = (screenX - screenViewport.left)
                / (screenViewport.right - screenViewport.left) - 0.5f;
        float screenYRatio = (screenY - screenViewport.bottom)
                / (screenViewport.top - screenViewport.bottom) - 0.5f;

        // Determine layer coordinate
        layerPosition.x = layerViewport.x + 2.0f * screenXRatio * layerViewport.halfWidth;
        layerPosition.y = layerViewport.y + 2.0f * screenYRatio * layerViewport.halfHeight;
    }

    /**
     * Convert the specified layer position from the game layer viewport into
     * the coordinate space defined for the screen viewport, storing the result
     * in the specified screen Vector2 position.
     *
     * @param layerViewport  Layer viewport
     * @param layerPosition  Layer position
     * @param screenViewport Screen viewport
     * @param screenPosition Calculated screen position
     */
    public static void convertLayerPosIntoScreen(
            LayerViewport layerViewport, Vector2 layerPosition,
            ScreenViewport screenViewport, Vector2 screenPosition) {

        // Determine the x- and y-aspect rations between the layer and screen viewports
        float screenXScale = (float) screenViewport.width / (2 * layerViewport.halfWidth);
        float screenYScale = (float) screenViewport.height / (2 * layerViewport.halfHeight);

        // Determine the screen position
        screenPosition.x = screenViewport.left + screenXScale *
                (layerPosition.x - (layerViewport.x - layerViewport.halfWidth));
        screenPosition.y = screenViewport.top + screenYScale *
                ((layerViewport.y + layerViewport.halfHeight) - layerPosition.y);
    }

    /**
     * Convert the specified layer position from the game layer viewport into
     * the coordinate space defined for the screen viewport, storing the result
     * in the specified screen Vector2 position.
     *
     * @param layerViewport  Layer viewport
     * @param layerX         Layer x position
     * @param layerY         Layer y position
     * @param screenViewport Screen viewport
     * @param screenPosition Calculated screen position
     */
    public static void convertLayerPosIntoScreen(
            LayerViewport layerViewport, float layerX, float layerY,
            ScreenViewport screenViewport, Vector2 screenPosition) {

        // Determine the x- and y-aspect rations between the layer and screen viewports
        float screenXScale = (float) screenViewport.width / (2 * layerViewport.halfWidth);
        float screenYScale = (float) screenViewport.height / (2 * layerViewport.halfHeight);

        // Determine the screen position
        screenPosition.x = screenViewport.left + screenXScale *
                (layerX - (layerViewport.x - layerViewport.halfWidth));
        screenPosition.y = screenViewport.top + screenYScale *
                ((layerViewport.y + layerViewport.halfHeight) - layerY);
    }

    /**
     * Convert the specified x-axis distance from layer space to screen space.
     *
     * @param layerXDistance Layer x distance
     * @param layerViewport Layer viewport
     * @param screenViewport Screen viewport
     * @return Corresponding screen x distance
     */
    public static float convertXDistanceFromLayerToScreen(float layerXDistance,
                                                          LayerViewport layerViewport, ScreenViewport screenViewport) {
        float conversionFactor = (float)screenViewport.width / (layerViewport.halfWidth * 2.0f);
        return layerXDistance * conversionFactor;
    }

    /**
     * Convert the specified y-axis distance from layer space to screen space.
     *
     * @param layerYDistance Layer y distance
     * @param layerViewport Layer viewport
     * @param screenViewport Screen viewport
     * @return Corresponding screen y distance
     */
    public static float convertYDistanceFromLayerToScreen(float layerYDistance,
                                                          LayerViewport layerViewport, ScreenViewport screenViewport) {
        float conversionFactor = (float)screenViewport.height / (layerViewport.halfHeight * 2.0f);
        return layerYDistance * conversionFactor;
    }

    // /////////////////////////////////////////////////////////////////////////
    // Viewport Creation
    // /////////////////////////////////////////////////////////////////////////

    /**
     * Create a default layer viewport (sized 480 x 320, centered [240,160].
     *
     * @param layerViewport Viewport instance to populate
     */
    public static void createDefaultLayerViewport(
            LayerViewport layerViewport) {
        layerViewport.x = 240.0f;
        layerViewport.y = 160.0f;
        layerViewport.halfWidth = 240.0f;
        layerViewport.halfHeight = 160.0f;
    }

    /**
     * Create a 3:2 aspect ratio screen viewport.
     *
     * @param game           Game view for which the screenport will be defined
     * @param screenViewport Screen viewport to be defined
     */
    public static void create3To2AspectRatioScreenViewport(
            Game game, ScreenViewport screenViewport) {

        // Create the screen viewport, size it to provide a 3:2 aspect
        float aspectRatio = (float) game.getScreenWidth() / (float) game.getScreenHeight();

        if (aspectRatio > 1.5f) { // 16:10/16:9
            int viewWidth = (int) (game.getScreenHeight() * 1.5f);
            int viewOffset = (game.getScreenWidth() - viewWidth) / 2;
            screenViewport.set(viewOffset, 0, viewOffset + viewWidth, game.getScreenHeight());
        } else { // 4:3
            int viewHeight = (int) (game.getScreenWidth() / 1.5f);
            int viewOffset = (game.getScreenHeight() - viewHeight) / 2;
            screenViewport.set(0, viewOffset, game.getScreenWidth(), viewOffset + viewHeight);
        }
    }
}
