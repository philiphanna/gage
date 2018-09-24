package uk.ac.qub.eeecs.gage.world;

import uk.ac.qub.eeecs.gage.util.BoundingBox;

/**
 * Layer viewport.
 *
 * @version 1.0
 */
public class LayerViewport {

    // /////////////////////////////////////////////////////////////////////////
    // Properties
    // /////////////////////////////////////////////////////////////////////////

    /**
     * Centre x location of the viewport
     */
    public float x;

    /**
     * Centre y location of the viewport
     */
    public float y;

    /**
     * Half-width of the viewport
     */
    public float halfWidth;

    /**
     * Half-height of the viewport
     */
    public float halfHeight;

    // /////////////////////////////////////////////////////////////////////////
    // Constructor and Methods
    // /////////////////////////////////////////////////////////////////////////

    /**
     * Create a layer viewport with a default 480x320 resolution
     */
    public LayerViewport() {
        this.x = 240;
        this.y = 160;
        this.halfWidth = 240;
        this.halfHeight = 160;
    }

    /**
     * Create a layer viewport of the specified dimensions
     *
     * @param x          Centre x location of the viewport
     * @param y          Centre y location of the viewport
     * @param halfWidth  Half width of the viewport
     * @param halfHeight Half height of the viewport
     */
    public LayerViewport(float x, float y, float halfWidth, float halfHeight) {
        this.x = x;
        this.y = y;
        this.halfWidth = halfWidth;
        this.halfHeight = halfHeight;
    }

    /**
     * Set the layer viewport of the specified dimensions
     *
     * @param x          Centre x location of the viewport
     * @param y          Centre y location of the viewport
     * @param halfWidth  Half width of the viewport
     * @param halfHeight Half height of the viewport
     */
    public void set(float x, float y, float halfWidth, float halfHeight) {
        this.x = x;
        this.y = y;
        this.halfHeight = halfHeight;
        this.halfWidth = halfWidth;
    }

    /**
     * Return the width of the viewport
     *
     * @return Width of the viewport
     */
    public float getWidth() {
        return halfWidth * 2.0f;
    }

    /**
     * Return the height of the viewport
     *
     * @return Height of the viewport
     */
    public float getHeight() {
        return halfHeight * 2.0f;
    }

    /**
     * Return the left bound
     *
     * @return left side location of the viewport
     */
    public float getLeft() {
        return x - halfWidth;
    }

    /**
     * Return the right bound
     *
     * @return right side location of the viewport
     */
    public float getRight() {
        return x + halfWidth;
    }

    /**
     * Return the top bound
     *
     * @return top side location of the viewport
     */
    public float getTop() {
        return y + halfHeight;
    }

    /**
     * Return the bottom bound
     *
     * @return bottom side location of the viewport
     */
    public float getBottom() {
        return y - halfHeight;
    }

    /**
     * Determine if the viewport contains the specified point
     *
     * @param x Test point x-coordinate
     * @param y Test point y-coordinate
     * @return boolean true if the point is contained within the viewport,
     * otherwise false
     */
    public boolean contains(float x, float y) {
        return (x - halfWidth < x && x + halfWidth > x
                && y - halfHeight < y && y + halfHeight > y);
    }

    /**
     * Determine if the viewport intersects with the specified bounding box
     *
     * @param bound Bounding box to test for intersection with the viewport
     * @return boolean true if the box is within the viewport, otherwise false
     */
    public boolean intersects(BoundingBox bound) {
        return (x - halfWidth < bound.x + bound.halfWidth &&
                x + halfWidth > bound.x - bound.halfWidth &&
                y - halfHeight < bound.y + bound.halfHeight &&
                y + halfHeight > bound.y - bound.halfHeight);
    }
}
