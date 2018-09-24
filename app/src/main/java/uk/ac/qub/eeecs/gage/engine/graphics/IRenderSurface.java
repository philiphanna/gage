package uk.ac.qub.eeecs.gage.engine.graphics;

import uk.ac.qub.eeecs.gage.engine.ElapsedTime;
import uk.ac.qub.eeecs.gage.world.GameScreen;

import android.view.View;

/**
 * The render surface interface defines a graphical surface to which a game
 * screen can be rendered.
 * <p>
 * It is assumed that each GameScreen will be responsible for determining how it
 * is rendered on the surface using a IGraphics2D instance supplied by the
 * IRenderSurface.
 *
 * @version 1.0
 */
public interface IRenderSurface {

    /**
     * Render the specified GameScreen on this surface
     * <p>
     * The render surface will prepare for the render, including creating a
     * suitable IGraphics2D instance that can be used to render to the surface.
     * The IGraphics2D instance will be passed to the GameScreen's render method
     * to permit the GameScreen to appropriately realise the render for the
     * screen.
     *
     * @param elapsedTime    Render request timing information
     * @param screenToRender GameScreen to be rendered
     */
    void render(ElapsedTime elapsedTime, GameScreen screenToRender);

    /**
     * Return the Android View associated with this render surface.
     *
     * @return View associated with this surface
     */
    View getAsView();
}
