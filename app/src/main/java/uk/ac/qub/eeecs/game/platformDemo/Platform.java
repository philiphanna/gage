package uk.ac.qub.eeecs.game.platformDemo;

import uk.ac.qub.eeecs.gage.engine.ElapsedTime;
import uk.ac.qub.eeecs.gage.engine.graphics.IGraphics2D;
import uk.ac.qub.eeecs.gage.util.BoundingBox;
import uk.ac.qub.eeecs.gage.util.GraphicsHelper;
import uk.ac.qub.eeecs.gage.world.GameObject;
import uk.ac.qub.eeecs.gage.world.GameScreen;
import uk.ac.qub.eeecs.gage.world.LayerViewport;
import uk.ac.qub.eeecs.gage.world.ScreenViewport;

/**
 * Rectangular platform class that can be drawn using a tile base image.
 *
 * Note: See the course documentation for extension/refactoring stories
 * for this class.
 *
 * @version 1.0
 */
public class Platform extends GameObject {

    // /////////////////////////////////////////////////////////////////////////
    // Properties
    // /////////////////////////////////////////////////////////////////////////

    /**
     * Number of tiles to draw along the x-axis when drawing this platform.
     * <p>
     * Defaults to a value of 1.
     */
    protected int mTileXCount = 1;

    /**
     * Number of tiles to draw along the y-axis when drawing this platform.
     * <p>
     * Defaults to a value of 1.
     */
    protected int mTileYCount = 1;

    // /////////////////////////////////////////////////////////////////////////
    // Constructors
    // /////////////////////////////////////////////////////////////////////////

    /**
     * Create a new platform.
     *
     * @param x          Centre y location of the platform
     * @param y          Centre x location of the platform
     * @param width      Width of the platform
     * @param height     Height of the platform
     * @param bitmapName Base bitmap used to draw this platform
     * @param gameScreen Gamescreen to which this platform belongs
     */
    public Platform(float x, float y, float width, float height,
                    String bitmapName, GameScreen gameScreen) {
        super(x, y, width, height, gameScreen.getGame().getAssetManager()
                .getBitmap(bitmapName), gameScreen);
    }

    /**
     * Create a new platform.
     *
     * @param x          Centre y location of the platform
     * @param y          Centre x location of the platform
     * @param width      Width of the platform
     * @param height     Height of the platform
     * @param bitmapName Base bitmap used to draw this platform
     * @param tileXCount Base bitmap tile count along the x-axis
     * @param tileYCount Base bitmap tile count along the y-axis
     * @param gameScreen Gamescreen to which this platform belongs
     */
    public Platform(float x, float y, float width, float height,
                    String bitmapName, int tileXCount, int tileYCount, GameScreen gameScreen) {
        this(x, y, width, height, bitmapName, gameScreen);
        mTileXCount = tileXCount;
        mTileYCount = tileYCount;
    }

    // /////////////////////////////////////////////////////////////////////////
    // Methods
    // /////////////////////////////////////////////////////////////////////////

    /**
     * Private helper variable used to provide a layer bound for the tile
     * being drawn.
     */
    private BoundingBox tileBound = new BoundingBox();

    /**
     * Draw the game platform
     *
     * @param elapsedTime    Elapsed time information
     * @param graphics2D     Graphics instance
     * @param layerViewport  Game layer viewport
     * @param screenViewport Screen viewport
     */
    @Override
    public void draw(ElapsedTime elapsedTime, IGraphics2D graphics2D,
                     LayerViewport layerViewport, ScreenViewport screenViewport) {

        // Call the getBound method to make sure we're using an up-to-date bound
        BoundingBox bound = getBound();

        // Only draw if it is visible
        if (GraphicsHelper.isVisible(bound, layerViewport)) {

            // Define the tile size
            tileBound.halfWidth = bound.halfWidth / mTileXCount;
            float tileWidth = 2.0f * tileBound.halfWidth;
            tileBound.halfHeight = bound.halfHeight / mTileYCount;
            float tileHeight = 2.0f * tileBound.halfHeight;

            // Store the bottom left corner of the platform
            float platformLeft = bound.getLeft();
            float platformBottom = bound.getBottom();

            // Consider drawing each tile
            for (int tileXIdx = 0; tileXIdx < mTileXCount; tileXIdx++)
                for (int tileYIdx = 0; tileYIdx < mTileYCount; tileYIdx++) {

                    // Build a layer bound for the tile
                    tileBound.x = platformLeft + (tileXIdx + 0.5f) * tileWidth;
                    tileBound.y = platformBottom + (tileYIdx + 0.5f) * tileHeight;

                    // If the layer tile is visible then draw tne tile
                    if (GraphicsHelper.getClippedSourceAndScreenRect(
                            tileBound, mBitmap, layerViewport, screenViewport, drawSourceRect, drawScreenRect)) {
                        graphics2D
                                .drawBitmap(mBitmap, drawSourceRect, drawScreenRect, null);
                    }
                }
        }
    }
}
