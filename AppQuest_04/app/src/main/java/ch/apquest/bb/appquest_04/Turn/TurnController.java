package ch.apquest.bb.appquest_04.Turn;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import ch.apquest.bb.appquest_04.Other.Constants;
import ch.apquest.bb.appquest_04.Other.RingBuffer;

public class TurnController implements SensorEventListener {

    //region Fields

    private RingBuffer initialRotation = new RingBuffer(Constants.TC_BUFFER_SIZE);
    private RingBuffer rotation = new RingBuffer(Constants.TC_BUFFER_SIZE);

    private SensorManager sensorManager = null;
    private Sensor rotationSensor = null;

    private TurnListener turnListener = null;

    private float[] rotationMatrix = new float[16];
    private float[] orientationVals = new float[3];

    //endregion

    //region Constructor

    public TurnController(Context context, TurnListener turnListener) {
        this.turnListener = turnListener;
        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        rotationSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
    }

    //endregion

    //region Events

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ROTATION_VECTOR) {
            SensorManager.getRotationMatrixFromVector(rotationMatrix, event.values);
            SensorManager.getOrientation(rotationMatrix, orientationVals);

            orientationVals[0] = (float) Math.toDegrees(orientationVals[0]);

            // Zuerst füllen wir den Buffer mit der Initialrotation um den
            // Startwinkel zu bestimmen, und wenn dieser voll ist (was sehr
            // schnell passiert), dann füllen wir einen zweiten RingBuffer.
            if (initialRotation.getCount() < Constants.TC_BUFFER_SIZE) {
                initialRotation.put(orientationVals[0]);
            } else {
                rotation.put(orientationVals[0]);
            }

            // Wenn der zweite Buffer auch gefüllt ist, vergleichen wir die
            // beiden Durchschnittswerte fortlaufend, und sobald wir eine
            // Drehung von grösser als 50 Grad erkennen, melden wir dies.
            if (rotation.getCount() >= Constants.TC_BUFFER_SIZE) {
                float r = Math.abs(rotation.getAverage() - initialRotation.getAverage());
                if (r > 50) {
                    turnListener.onTurn();
                }
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    //endregion

    //region General Methods

    public void resume() {
        sensorManager.registerListener(this, rotationSensor, SensorManager.SENSOR_DELAY_UI);
    }

    public void pause() {
        sensorManager.unregisterListener(this);
    }

    public void reset() {
        rotationMatrix = new float[16];
        orientationVals = new float[3];
        initialRotation = new RingBuffer(Constants.TC_BUFFER_SIZE);
        rotation = new RingBuffer(Constants.TC_BUFFER_SIZE);
    }

    //endregion

}
