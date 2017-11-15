package ch.appquest.bb.appquest02;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

public class AngleSensorManager implements SensorEventListener {

    private boolean isCapturing = false;
    private Double currentAngle;
    private Double angleStartPosition;
    private SensorManager manager;
    private Sensor sensorAcceleration;

    private float[] accelerationData = new float[3];

    public AngleSensorManager(Context context) {
        this.manager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
    }

    public void start() {
        if (!isCapturing) {
            isCapturing = true;

            sensorAcceleration = manager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

            manager.registerListener(this, sensorAcceleration, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    public void stop() {
        if (isCapturing) {
            isCapturing = false;
            manager.unregisterListener(this, sensorAcceleration);
            sensorAcceleration = null;
        }
    }

    public void reset() {
        currentAngle = null;
        angleStartPosition = null;
        accelerationData = new float[3];
    }

    public Double getAlpha() {
        return angleStartPosition;
    }

    public Double getBeta() {
        return currentAngle;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            System.arraycopy(event.values, 0, accelerationData, 0, 3);
        }
        if (angleStartPosition == null) {
            angleStartPosition = getCurrentRotationValue();
        }
        currentAngle = getCurrentRotationValue() - angleStartPosition;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    private double getCurrentRotationValue() {
        float[] rotationMatrix = new float[16];

        double norm_Of_g = Math.sqrt(accelerationData[0] * accelerationData[0] + accelerationData[1] * accelerationData[1] + accelerationData[2] * accelerationData[2]);

        // Normalize the accelerometer vector
        accelerationData[0] = (float) (accelerationData[0] / norm_Of_g);
        accelerationData[1] = (float) (accelerationData[1] / norm_Of_g);
        accelerationData[2] = (float) (accelerationData[2] / norm_Of_g);

        Double inclination = Math.toDegrees(Math.acos(accelerationData[2]));

        return inclination;
    }
}
