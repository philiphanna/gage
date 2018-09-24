package uk.ac.qub.eeecs.gage.engine.input;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

/**
 * Accelerometer handler.
 *
 * @version 1.0
 */
public class AccelerometerHandler implements SensorEventListener {

    // /////////////////////////////////////////////////////////////////////////
    // Properties
    // /////////////////////////////////////////////////////////////////////////

    /**
     * Define directional components for the acceleration
     */
    private float mAccelX;
    private float mAccelY;
    private float mAccelZ;

    // /////////////////////////////////////////////////////////////////////////
    // Constructors
    // /////////////////////////////////////////////////////////////////////////

    /**
     * Create a new accelerometer handler for the specified context
     *
     * @param context Context from which the accelerometer will be accessed
     */
    public AccelerometerHandler(Context context) {
        // Retrieve the sensor mananger and from this the default accelerometer,
        // and register this listener.
        SensorManager manager = (SensorManager) context
                .getSystemService(Context.SENSOR_SERVICE);
        Sensor accelerometer = manager
                .getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        manager.registerListener(this, accelerometer,
                SensorManager.SENSOR_DELAY_GAME);
    }

    // /////////////////////////////////////////////////////////////////////////
    // Sensor Update
    // /////////////////////////////////////////////////////////////////////////

    /*
     * (non-Javadoc)
     *
     * @see
     * android.hardware.SensorEventListener#onAccuracyChanged(android.hardware
     * .Sensor, int)
     */
    @Override
    public void onAccuracyChanged(Sensor arg0, int arg1) {
        // Not used within this listener
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * android.hardware.SensorEventListener#onSensorChanged(android.hardware
     * .SensorEvent)
     */
    @Override
    public void onSensorChanged(SensorEvent event) {

        // Store the current accelerometer values. No synchronisation is needed
        // to the shared access azimuth property as reads/writes to primitive
        // data are atomic.
        mAccelX = event.values[0];
        mAccelY = event.values[1];
        mAccelZ = event.values[2];
    }

    // /////////////////////////////////////////////////////////////////////////
    // Sensor Access
    // /////////////////////////////////////////////////////////////////////////

    /**
     * Return the current x-axis acceleration
     *
     * @return x-axis acceleration
     */
    public float getAccelX() {
        return mAccelX;
    }

    /**
     * Return the current y-axis acceleration
     *
     * @return y-axis acceleration
     */
    public float getAccelY() {
        return mAccelY;
    }

    /**
     * Return the current z-axis acceleration
     *
     * @return z-axis acceleration
     */
    public float getAccelZ() {
        return mAccelZ;
    }
}
