package ch.appquest.bb.appquest_05.Drawing;

import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;

import ch.appquest.bb.appquest_05.Other.Constants;

public class DrawingPitchCell {

    //region Fields

    private int color = Constants.DRAWING_PITCH_CELL_DEFAULT_COLOR;
    private Rect rectOnDrawingPitchCanvas = null;
    private Point pointInDrawingPitch = null;

    //endregion

    //region General Methods

    @Override
    protected DrawingPitchCell clone() {
        DrawingPitchCell cellCopy = new DrawingPitchCell();
        cellCopy.setColor(color);
        cellCopy.setRectOnDrawingPitchCanvas(rectOnDrawingPitchCanvas);
        cellCopy.setPointInDrawingPitch(pointInDrawingPitch);
        return cellCopy;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public int getColor() {
        return this.color;
    }

    public Paint getPaint() {
        Paint p = new Paint();
        p.setColor(getColor());
        return p;
    }

    public Rect getRectOnDrawingPitchCanvas() {
        return this.rectOnDrawingPitchCanvas;
    }

    public void setRectOnDrawingPitchCanvas(Rect rectOnDrawingPitchCanvas) {
        this.rectOnDrawingPitchCanvas = rectOnDrawingPitchCanvas;
    }

    public void setPointInDrawingPitch(Point p) {
        this.pointInDrawingPitch = p;
    }

    public Point getPointInDrawingPitch() {
        return this.pointInDrawingPitch;
    }

    //endregion

}
