package uk.ac.qub.eeecs.gage.util;

import android.graphics.Bitmap;
import android.graphics.Rect;

import uk.ac.qub.eeecs.gage.world.GameObject;
import uk.ac.qub.eeecs.gage.world.LayerViewport;
import uk.ac.qub.eeecs.gage.world.ScreenViewport;

public final class GraphicsHelper {

    // /////////////////////////////////////////////////////////////////////////
    // Source and Destination Rects
    // /////////////////////////////////////////////////////////////////////////

    /**
     * Determine if the specified bound falls within the specified viewport
     *
     * @param bound         Game object bound to be considered
     * @param layerViewport Layer viewport region to check the entity against
     * @return True if the specified bound is visible, False otherwise
     */
    public static boolean isVisible(BoundingBox bound, LayerViewport layerViewport) {

        // Determine if the bound falls within the layer viewport
        return (bound.x - bound.halfWidth < layerViewport.x + layerViewport.halfWidth &&
                bound.x + bound.halfWidth > layerViewport.x - layerViewport.halfWidth &&
                bound.y - bound.halfHeight < layerViewport.y + layerViewport.halfHeight &&
                bound.y + bound.halfHeight > layerViewport.y - layerViewport.halfHeight);
    }

    /**
     * Determine the full source bitmap Rect and destination screen Rect if the
     * specified game object bound falls within the layer's viewport
     * <p>
     * The return rects are not clipped against the screen viewport.
     *
     * @param gameObject     Game object instance to be considered
     * @param layerViewport  Layer viewport region to check the entity against
     * @param screenViewport Screen viewport region that will be used to draw the
     * @param sourceRect     Output Rect holding the region of the bitmap to draw
     * @param screenRect     Output Rect holding the region of the screen to draw to
     * @return True if the specified game object is visible, False otherwise
     */
    public static boolean getSourceAndScreenRect(GameObject gameObject,
                                                       LayerViewport layerViewport, ScreenViewport screenViewport,
                                                       Rect sourceRect, Rect screenRect) {
        return getSourceAndScreenRect(gameObject.getBound(), gameObject.getBitmap(),
                layerViewport, screenViewport, sourceRect, screenRect);
    }

    /**
     * Determine the full source bitmap Rect and destination screen Rect if the
     * specified bitmap and rectangular bound falls within the layer's viewport
     * <p>
     * The return rects are not clipped against the screen viewport.
     *
     * @param bound          Game object bound to be considered
     * @param bitmap         Game object bitmap to be drawn
     * @param layerViewport  Layer viewport region to check the entity against
     * @param screenViewport Screen viewport region that will be used to draw the
     * @param sourceRect     Output Rect holding the region of the bitmap to draw
     * @param screenRect     Output Rect holding the region of the screen to draw to
     * @return True if the specified bound is visible, False otherwise
     */
    public static boolean getSourceAndScreenRect(BoundingBox bound, Bitmap bitmap,
                                                       LayerViewport layerViewport, ScreenViewport screenViewport,
                                                       Rect sourceRect, Rect screenRect) {

        // Determine if the bound falls within the layer viewport
        if (bound.x - bound.halfWidth < layerViewport.x + layerViewport.halfWidth &&
                bound.x + bound.halfWidth > layerViewport.x - layerViewport.halfWidth &&
                bound.y - bound.halfHeight < layerViewport.y + layerViewport.halfHeight &&
                bound.y + bound.halfHeight > layerViewport.y - layerViewport.halfHeight) {

            // Define the source rectangle
            sourceRect.set(0, 0, bitmap.getWidth(), bitmap.getHeight());

            // Determine the x- and y-aspect rations between the layer and screen viewports
            float screenXScale = (float) screenViewport.width / (2 * layerViewport.halfWidth);
            float screenYScale = (float) screenViewport.height / (2 * layerViewport.halfHeight);

            // Determine the screen rectangle
            float screenX = screenViewport.left + screenXScale *
                    ((bound.x - bound.halfWidth)
                            - (layerViewport.x - layerViewport.halfWidth));
            float screenY = screenViewport.top + screenYScale *
                    ((layerViewport.y + layerViewport.halfHeight)
                            - (bound.y + bound.halfHeight));

            screenRect.set((int) screenX, (int) screenY,
                    (int) (screenX + (bound.halfWidth * 2) * screenXScale),
                    (int) (screenY + (bound.halfHeight * 2) * screenYScale));

            return true;
        }

        // Not visible
        return false;
    }

    /**
     * Determine a source bitmap Rect and destination screen Rect if the
     * specified game object bound falls within the layer's viewport.
     * <p>
     * The returned Rects are clipped against the layer and screen viewport
     *
     * @param gameObject     Game object instance to be considered
     * @param layerViewport  Layer viewport region to check the entity against
     * @param screenViewport Screen viewport region that will be used to draw the
     * @param sourceRect     Output Rect holding the region of the bitmap to draw
     * @param screenRect     Output Rect holding the region of the screen to draw to
     * @return True if the specified object is visible, False otherwise
     */
    public static boolean getClippedSourceAndScreenRect(GameObject gameObject,
                                                              LayerViewport layerViewport, ScreenViewport screenViewport,
                                                              Rect sourceRect, Rect screenRect) {
        return getClippedSourceAndScreenRect(gameObject.getBound(), gameObject.getBitmap(),
                layerViewport, screenViewport, sourceRect, screenRect);
    }

    /**
     * Determine a source bitmap Rect and destination screen Rect if the
     * specified game object bound falls within the layer's viewport.
     * <p>
     * The returned Rects are clipped against the layer and screen viewport
     *
     * @param bound          Game object bound to be considered
     * @param bitmap         Game object bitmap to be drawn
     * @param layerViewport  Layer viewport region to check the entity against
     * @param screenViewport Screen viewport region that will be used to draw the
     * @param sourceRect     Output Rect holding the region of the bitmap to draw
     * @param screenRect     Output Rect holding the region of the screen to draw to
     * @return True if the specified object is visible, False otherwise
     */
    public static boolean getClippedSourceAndScreenRect(BoundingBox bound, Bitmap bitmap,
                                                              LayerViewport layerViewport, ScreenViewport screenViewport,
                                                              Rect sourceRect, Rect screenRect) {

        // Determine if the bound falls within the layer viewport
        if (bound.x - bound.halfWidth < layerViewport.x + layerViewport.halfWidth &&
                bound.x + bound.halfWidth > layerViewport.x - layerViewport.halfWidth &&
                bound.y - bound.halfHeight < layerViewport.y + layerViewport.halfHeight &&
                bound.y + bound.halfHeight > layerViewport.y - layerViewport.halfHeight) {

            // Work out what region of the sprite is visible within the layer viewport,

            float sourceX = Math.max(0.0f,
                    (layerViewport.x - layerViewport.halfWidth)
                            - (bound.x - bound.halfWidth));
            float sourceY = Math.max(0.0f,
                    (bound.y + bound.halfHeight)
                            - (layerViewport.y + layerViewport.halfHeight));

            float sourceWidth = ((bound.halfWidth * 2 - sourceX) - Math
                    .max(0.0f, (bound.x + bound.halfWidth)
                            - (layerViewport.x + layerViewport.halfWidth)));
            float sourceHeight = ((bound.halfHeight * 2 - sourceY) - Math
                    .max(0.0f, (layerViewport.y - layerViewport.halfHeight)
                            - (bound.y - bound.halfHeight)));

            // Determining the scale factor for mapping the bitmap onto this
            // Rect and set the sourceRect value.

            float sourceScaleWidth = (float) bitmap.getWidth()
                    / (2 * bound.halfWidth);
            float sourceScaleHeight = (float) bitmap.getHeight()
                    / (2 * bound.halfHeight);

            sourceRect.set((int) (sourceX * sourceScaleWidth),
                    (int) (sourceY * sourceScaleHeight),
                    (int) ((sourceX + sourceWidth) * sourceScaleWidth),
                    (int) ((sourceY + sourceHeight) * sourceScaleHeight));

            // Determine =which region of the screen viewport (relative to the
            // canvas) we will be drawing to.

            // Determine the x- and y-aspect rations between the layer and screen viewports
            float screenXScale = (float) screenViewport.width / (2 * layerViewport.halfWidth);
            float screenYScale = (float) screenViewport.height / (2 * layerViewport.halfHeight);

            float screenX = screenViewport.left + screenXScale * Math.max(
                    0.0f,
                    ((bound.x - bound.halfWidth)
                            - (layerViewport.x - layerViewport.halfWidth)));
            float screenY = screenViewport.top + screenYScale * Math.max(
                    0.0f,
                    ((layerViewport.y + layerViewport.halfHeight)
                            - (bound.y + bound.halfHeight)));

            // Set the region to the canvas to which we will draw
            screenRect.set((int) screenX, (int) screenY,
                    (int) (screenX + sourceWidth * screenXScale),
                    (int) (screenY + sourceHeight * screenYScale));

            return true;
        }

        // Not visible
        return false;
    }
}