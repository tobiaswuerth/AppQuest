package ch.appquest.bb.appquest02;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CaptureRequest;
import android.os.Bundle;
import android.text.method.CharacterPickerDialog;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity {

    public static final String TAG = MainActivity.class.getSimpleName();

    public static final String PARAMKEY_ANGLE_ANLPHA = "angle_alpha";
    public static final String PARAMKEY_ANGLE_BETA = "angle_beta";


    private CameraLivePreviewSurfaceView cameraPreview;
    private FrameLayout frameLayout;

    private Button btnStartStopMeasurement;
    private Button btnCalculate;

    private AngleSensorManager angleSensorManager;

    private boolean isMeasurementStarted = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        frameLayout = (FrameLayout) findViewById(R.id.frame_camera_preview);

        btnStartStopMeasurement = (Button) findViewById(R.id.btnStartStopMeasurement);
        btnCalculate = (Button) findViewById(R.id.btnCalculate);

        btnStartStopMeasurement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isMeasurementStarted = !isMeasurementStarted;

                if(isMeasurementStarted){
                    angleSensorManager.reset();
                    angleSensorManager.start();
                }else{
                    angleSensorManager.stop();
                }

                updateView();
            }
        });
        btnCalculate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calculate();
            }
        });

        cameraPreview = new CameraLivePreviewSurfaceView(this);
        frameLayout.addView(cameraPreview);

        updateView();

        angleSensorManager = new AngleSensorManager(this);
    }

    private void updateView() {
        if (isMeasurementStarted) {
            btnStartStopMeasurement.setText(R.string.stopMeasurement);
            btnCalculate.setEnabled(false);
        } else {
            btnStartStopMeasurement.setText(R.string.startMeasurement);
            btnCalculate.setEnabled(true);
        }
        if(angleSensorManager == null){
            btnCalculate.setEnabled(false);
        }
    }

    private void calculate() {
        Intent i = new Intent(this, CalculationActivity.class);
        i.putExtra(PARAMKEY_ANGLE_ANLPHA, angleSensorManager.getAlpha());
        i.putExtra(PARAMKEY_ANGLE_BETA, angleSensorManager.getBeta());

        startActivity(i);
    }

    @Override
    protected void onPause() {
        super.onPause();
        cameraPreview.pause();
        angleSensorManager.stop();
        Log.d(TAG, "onPause()");
    }

    @Override
    protected void onResume() {
        super.onResume();
        cameraPreview.resume();
        Log.d(TAG, "onResume()");
    }
}
