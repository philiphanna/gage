package uk.ac.qub.eeecs.gage.util;

/**
 * Collection of miscellaneous mathematical related functions
 */
public class MathsHelper {

    /**
     * Rotate the specified relative offset point around the centre point
     * returning the result in output.
     *
     * @param centre Centre point
     * @param offset Offset point to be rotated about the center
     * @param degrees Amount of rotation in degrees
     * @param output Location of rotated point
     */
    public static void rotateOffsetAboutCentre(
            Vector2 centre, Vector2 offset, float degrees, Vector2 output ) {

        // Convert into radians. Also swap the direction of the angle
        // from +ve counterclockwise to +ve clockwise as assumed by Gage
        double radians = -(double)degrees * Math.PI / 180.0;

        // Determine the rotated point
        float cosAngle = (float)Math.cos(radians);
        float sinAngle = (float)Math.sin(radians);

        output.x = centre.x + (offset.x * cosAngle - offset.y * sinAngle);
        output.y = centre.y + (offset.x * sinAngle + offset.y * cosAngle);
    }

    /**
     * Rotate the specified point around the specified centre point returning
     * the result in output.
     *
     * @param centre Centre point
     * @param point Point to be rotated about the center
     * @param degrees Amount of rotation in degrees
     * @param output Location of rotated point
     */
    public static void rotatePointAboutCentre(
            Vector2 centre, Vector2 point, float degrees, Vector2 output ) {

        // Convert into radians. Also swap the direction of the angle
        // from +ve counterclockwise to +ve clockwise as assumed by Gage
        double radians = -(double)degrees * Math.PI / 180.0;

        // Determine the rotated point
        float cosAngle = (float)Math.cos(radians);
        float sinAngle = (float)Math.sin(radians);

        float offsetX = point.x - centre.x;
        float offsetY = point.y - centre.y;

        output.x = centre.x + (offsetX * cosAngle - offsetY * sinAngle);
        output.y = centre.y + (offsetX * sinAngle + offsetY * cosAngle);
    }
}
