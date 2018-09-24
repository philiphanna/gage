package uk.ac.qub.eeecs.gage.engine.graphics;

import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;

/**
 * The graphics 2D interface defines the set of graphical operations that can be
 * applied to a render surface
 *
 * @version 1.0
 */
public interface IGraphics2D {

    /**
     * Get the width of the render surface
     *
     * @return Width of the render surface
     */
    int getSurfaceWidth();

    /**
     * Get the height of the render surface
     *
     * @return Height of the render surface
     */
    int getSurfaceHeight();

    /**
     * Insert the specified rectangular clip region
     *
     * @param clipRegion Clip rectangular region
     */
    void clipRect(Rect clipRegion);

    /**
     * Set the surface colour to that specified colour (assumed to be in the
     * same format as the Color class - ARGB).
     *
     * @param colour ARGB formatted colour
     */
    void clear(int colour);

    /**
     * Draw the specified text string
     *
     * @param text  String of text to be rendered
     * @param x     Location of text on x-axis
     * @param y     Location of text on y-axis
     * @param paint Paint parameters controlling text render format
     */
    void drawText(String text, float x, float y, Paint paint);

    /**
     * Draw the specified bitmap
     *
     * @param bitmap  Bitmap to be rendered
     * @param srcRect Source region to be rendered (if null full source is rendered)
     * @param desRect Destination region for the render
     * @param paint   Paint parameters controlling how the bitmap is rendered
     */
    void drawBitmap(Bitmap bitmap, Rect srcRect, Rect desRect,
                           Paint paint);

    /**
     * Draw the specified bitmap
     *
     * @param bitmap Bitmap to be rendered
     * @param matrix Matrix defining bitmap scaling, rotation, translation, etc.
     * @param paint  Paint parameters controlling how the bitmap is rendered
     */
    void drawBitmap(Bitmap bitmap, Matrix matrix, Paint paint);

    /**
     * Draw the specified rectangle
     *
     * @param left   Left extent of rectangle
     * @param top    Top extent of rectangle
     * @param right  Right extent of rectangle
     * @param bottom Bottom extent of rectangle
     * @param paint  Paint used to specified line colour/width
     */
    void drawRect(float left, float top, float right, float bottom, Paint paint);
}
