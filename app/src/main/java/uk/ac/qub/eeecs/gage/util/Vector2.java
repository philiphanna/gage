package uk.ac.qub.eeecs.gage.util;

/**
 * Simple Vector2 object, holding a x and y value.
 *
 * @version 1.0
 */
public class Vector2 {

    // /////////////////////////////////////////////////////////////////////////
    // Defined Vectors
    // /////////////////////////////////////////////////////////////////////////

    /**
     * Zero [0,0] vector
     */
    public final static Vector2 Zero = new Vector2(0, 0);

    // /////////////////////////////////////////////////////////////////////////
    // Properties
    // /////////////////////////////////////////////////////////////////////////

    /**
     * x component of this vector
     */
    public float x;

    /**
     * y component of this vector
     */
    public float y;

    // /////////////////////////////////////////////////////////////////////////
    // Constructors
    // /////////////////////////////////////////////////////////////////////////

    /**
     * Constructs a new vector with a value of [0,0]
     */
    public Vector2() {
    }

    /**
     * Constructs a vector with the given component values
     *
     * @param x The x-component value
     * @param y The y-component value
     */
    public Vector2(float x, float y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Constructs a vector with the given component value
     *
     * @param other Other vector
     */
    public Vector2(Vector2 other) {
        this.x = other.x;
        this.y = other.y;
    }

    // /////////////////////////////////////////////////////////////////////////
    // Methods
    // /////////////////////////////////////////////////////////////////////////

    /**
     * Determine if this Vector2 is [0,0]
     *
     * @return Boolean true if [0,0], otherwise false
     */
    public boolean isZero() {
        return x == 0.0f && y == 0.0f;
    }

    /**
     * Determine the Euclidean length of the vector
     *
     * @return The Euclidean length of the vector
     */
    public float length() {
        return (float) Math.sqrt(x * x + y * y);
    }

    /**
     * Determine the squared Euclidean length of the vector
     *
     * @return The squared Euclidean length of the vector
     */
    public float lengthSquared() {
        return x * x + y * y;
    }

    /**
     * Set the component value of the vector
     *
     * @param x The x-component
     * @param y The y-component
     */
    public void set(float x, float y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Set the component value of the vector
     *
     * @param other Vector from which to copy the values
     */
    public void set(Vector2 other) {
        this.x = other.x;
        this.y = other.y;
    }

    /**
     * Add the component value to the vector
     *
     * @param x The x-component to add
     * @param y The y-component to add
     */
    public void add(float x, float y) {
        this.x += x;
        this.y += y;
    }

    /**
     * Add the component value to the vector
     *
     * @param other Vector from which to add the values
     */
    public void add(Vector2 other) {
        this.x += other.x;
        this.y += other.y;
    }

    /**
     * Subtract the component value from the vector
     *
     * @param other Vector whose values are to be subtracted
     */
    public void subtract(Vector2 other) {
        this.x -= other.x;
        this.y -= other.y;
    }

    /**
     * Multiple the vector components by the specified scalar
     *
     * @param scalar Value with which to multiple the component values
     */
    public void multiply(float scalar) {
        x *= scalar;
        y *= scalar;
    }

    /**
     * Divide the vector components by the specified scalar
     *
     * @param scalar Value with which to divide the component values
     */
    public void divide(float scalar) {
        x /= scalar;
        y /= scalar;
    }

    /**
     * Normalise the vector
     */
    public void normalise() {
        float length = length();
        if (length != 0) {
            x /= length;
            y /= length;
        }
    }
}
