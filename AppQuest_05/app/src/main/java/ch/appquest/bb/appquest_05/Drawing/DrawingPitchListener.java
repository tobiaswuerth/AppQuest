package ch.appquest.bb.appquest_05.Drawing;

import android.graphics.Canvas;

public interface DrawingPitchListener {
    void onInitialized();
    void onDrawingPitchTouchDown(final DrawingPitchCell drawingPitchCell);
    void onDrawingPitchTouchMoved(final DrawingPitchCell drawingPitchCell);
    void onDrawingPitchTouchUp(final DrawingPitchCell drawingPitchCell);
    void onDraw(final Canvas canvas);
    void onSizeChanged(final int size);
}
