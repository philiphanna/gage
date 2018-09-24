package uk.ac.qub.eeecs.gage.engine;

/**
 * Elapsed time information.
 *
 * @version 1.0
 */
public class ElapsedTime {

    /**
     * Amount of time that has elapsed since the last frame
     */
    public double stepTime;

    /**
     * Amount of time that has elapsed since the game started (first frame)
     */
    public double totalTime;
}
