package ch.appquest.bb.appquest02;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.util.Log;
import android.util.Size;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CameraLivePreviewSurfaceView extends SurfaceView implements SurfaceHolder.Callback {

    private static final String TAG = CameraLivePreviewSurfaceView.class.getSimpleName();

    private CameraManager manager;
    private SurfaceHolder holder;
    private String cameraId;
    private CameraDevice camera;
    private CameraCaptureSession activeSession;

    private List<Paint> linePaint = new ArrayList<Paint>();

    public CameraLivePreviewSurfaceView(Context context) {
        super(context);

        holder = this.getHolder();
        manager = (CameraManager) context.getSystemService(Context.CAMERA_SERVICE);

        cameraId = null;
        Size[] sizes = null;
        try {
            String[] ids = manager.getCameraIdList();
            for (String id : ids) {
                CameraCharacteristics cc = manager.getCameraCharacteristics(id);
                Log.d(TAG, id + ": " + cc.toString());
                if (cc.get(CameraCharacteristics.LENS_FACING) == CameraCharacteristics.LENS_FACING_BACK) {
                    cameraId = id;
                }
                StreamConfigurationMap configs = cc.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
                sizes = configs.getOutputSizes(SurfaceHolder.class);
                break;
            }

        } catch (CameraAccessException e) {
            Log.e(TAG, "getCameraIdList() oder getCameraCharacteristics()", e);
        }
        if ((cameraId == null) || (sizes == null)) {
            Log.d(TAG, "keine passende Kamera gefunden!");
            return;
        } else {
            final int width = sizes[0].getWidth();
            final int height = sizes[0].getHeight();
            holder.setFixedSize(width, height);
        }

        setWillNotDraw(false);

        Paint pB = new Paint();
        Paint pW = new Paint();
        Paint pC = new Paint();

        pB.setColor(Color.BLACK);
        pW.setColor(Color.WHITE);
        pC.setColor(Color.argb(255, 20, 255, 20));

        linePaint.addAll(Arrays.asList(pB, pC, pW));

        Paint pT = new Paint();
        pT.setColor(Color.CYAN);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (linePaint.size() > 0) {
            int width = canvas.getWidth();
            int height = canvas.getHeight();

            int center = (int) (height / 2d);

            canvas.drawLine(0f, (float) center, (float) width, (float) center, linePaint.get(0));

            for (int i = 1; i < linePaint.size(); i++) {
                canvas.drawLine(0f, (float) center - i, (float) width, (float) center - i, linePaint.get(i));
            }
            for (int i = 1; i < linePaint.size(); i++) {
                canvas.drawLine(0f, (float) center + i, (float) width, (float) center + i, linePaint.get(i));
            }
        }
    }

    public void pause() {
        if (camera != null) {
            if (activeSession != null) {
                activeSession.close();
                activeSession = null;
            }
            camera.close();
            camera = null;
        }

        holder.removeCallback(this);
        Log.d(TAG, "pause()");
    }

    public void resume() {
        holder.addCallback(this);
        Log.d(TAG, "resume()");
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        Log.d(TAG, "surfaceCreated()");
        openCamera();
    }

    private void openCamera() {
        try {
            manager.openCamera(cameraId, new CameraDevice.StateCallback() {
                @Override
                public void onOpened(CameraDevice camera) {
                    Log.d(TAG, "onOpened()");
                    CameraLivePreviewSurfaceView.this.camera = camera;
                    createPreviewCaptureSession();
                }

                @Override
                public void onDisconnected(CameraDevice camera) {
                    Log.d(TAG, "onDisconnected()");
                }

                @Override
                public void onError(CameraDevice camera, int error) {
                    Log.d(TAG, "onError()");
                }
            }, null);

        } catch (CameraAccessException ex) {
            Log.e(TAG, "openCamera()", ex);
        } catch (SecurityException ex) {
            Log.e(TAG, "no permission for camera", ex);
        }
    }

    private void createPreviewCaptureSession() {
        List<Surface> outputs = new ArrayList<>();
        outputs.add(holder.getSurface());
        try {
            final CaptureRequest.Builder builder = camera.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            builder.addTarget(holder.getSurface());
            camera.createCaptureSession(outputs, new CameraCaptureSession.StateCallback() {
                @Override
                public void onConfigured(CameraCaptureSession session) {
                    try {
                        session.setRepeatingRequest(builder.build(), null, null);
                        CameraLivePreviewSurfaceView.this.activeSession = session;
                    } catch (CameraAccessException e) {
                        Log.e(TAG, "capture()", e);
                    }
                }

                @Override
                public void onConfigureFailed(CameraCaptureSession session) {
                    Log.e(TAG, "onConfigureFailed()");
                }
            }, null);
        } catch (CameraAccessException e) {
            Log.e(TAG, "createPreviewCaptureSession()", e);
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        Log.d(TAG, "surfaceChanged()");
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        Log.d(TAG, "surfaceDestroyed()");
    }
}
