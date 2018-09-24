package uk.ac.qub.eeecs.gage.engine.input;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

/**
 * Simple compass class returning the orientation away from magnetic north
 *
 * @version 1.0
 */
public class CompassHandler implements SensorEventListener {

    // /////////////////////////////////////////////////////////////////////////
    // Properties
    // /////////////////////////////////////////////////////////////////////////

    /**
     * Vectors for holding the raw gravity and geomagnetic vectors
     */
    private float[] mGravity = new float[3];
    private float[] mGeomagnetic = new float[3];

    /**
     * Rotation and incline transform matrices formed using the gravity and
     * geomagnetic vectors. Defined for reuse (avoiding array creation/deletion
     * costs).
     */
    private float[] mRotate = new float[9]; // Rotation matrix
    private float[] mIncline = new float[9]; // Inclination matrix

    /**
     * Orientation vector extracted from the rotation matrix. Defined for reuse
     * (avoiding array creation/deletion costs).
     */
    private float[] mOrientation = new float[3]; // Orientation matrix

    /**
     * Azimuth component of the orientation vector
     */
    private float mAzimuth;

    // /////////////////////////////////////////////////////////////////////////
    // Constructors
    // /////////////////////////////////////////////////////////////////////////

    /**
     * Create a new compass handler for the specified context
     *
     * @param context Context from which the compass handler will obtain access to
     *                the compass and magnetic field sensors
     */
    public CompassHandler(Context context) {
        // Retrieve the sensor mananger and from this the default accelerometer
        // and magnetic field sensors.
        SensorManager sensorManager = (SensorManager) context
                .getSystemService(Context.SENSOR_SERVICE);
        Sensor accelerometer = sensorManager
                .getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        Sensor magnetic = sensorManager
                .getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

        // Register this compass listener through the sensor manager
        sensorManager.registerListener(this, accelerometer,
                SensorManager.SENSOR_DELAY_GAME);
        sensorManager.registerListener(this, magnetic,
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
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
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

        // Extract the current gravity vector
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            mGravity[0] = event.values[0];
            mGravity[1] = event.values[1];
            mGravity[2] = event.values[2];
        }

        // Extract the current magnetic vector
        if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            mGeomagnetic[0] = event.values[0];
            mGeomagnetic[1] = event.values[1];
            mGeomagnetic[2] = event.values[2];
        }

        // Using the gravity and geomagnetic vectors attempt to form
        // rotation and incline matricies
        boolean done = SensorManager.getRotationMatrix(mRotate, mIncline,
                mGravity, mGeomagnetic);
        if (done) {
            // Extract the orientation vector from the rotation matrix
            SensorManager.getOrientation(mRotate, mOrientation);

            // Extract and shape the Azimuth in a 0 to 2PI range.
            // No synchronisation is needed to the shared access
            // azimuth property as reads/writes to primitive
            // data are atomic.
            mAzimuth = mOrientation[0];
            float twoPI = (float) (2.0 * Math.PI);
            mAzimuth = (mAzimuth + twoPI) % twoPI;
        }
    }

    // /////////////////////////////////////////////////////////////////////////
    // Sensor Access
    // /////////////////////////////////////////////////////////////////////////

    /**
     * Return the compass heading towards magnetic north
     *
     * @return Compass heading (radians) towards magnetic north
     */
    public float getAzimuth() {
        return mAzimuth;
    }
}
