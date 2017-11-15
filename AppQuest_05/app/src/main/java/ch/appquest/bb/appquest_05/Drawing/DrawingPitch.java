package ch.appquest.bb.appquest_05.Drawing;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;

import java.util.ArrayList;
import java.util.List;

import ch.appquest.bb.appquest_05.Other.Constants;

public class DrawingPitch extends View {

    //region Fields

    private int pxDrawingPitchCanvasOriginal = 0;
    private int pxDrawingPitchCell = 0;
    private int drawingPitchSize = Constants.INITIAL_DRAWING_PITCH_SIZE;
    private Bitmap drawingPitchBitmap = null;
    private Canvas drawingPitchCanvas = null;
    private List<DrawingPitchCell> drawingPitchCells = new ArrayList<>();
    private DrawingPitchListener listenerDrawingPitch = null;
    private ViewGroup container = null;

    //endregion

    //region Constructor

    public DrawingPitch(Context context, ViewGroup container) {
        super(context);

        this.container = container;
        this.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                initialize();
                DrawingPitch.this.getViewTreeObserver().removeOnGlobalLayoutListener(this);

                if (listenerDrawingPitch != null) {
                    listenerDrawingPitch.onInitialized();
                }
            }
        });

        this.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (listenerDrawingPitch != null) {
                    DrawingPitchCell drawingPitchCell = canvasTouched((int) event.getX(), (int) event.getY());
                    if (drawingPitchCell != null) {
                        switch (event.getAction()) {
                            case MotionEvent.ACTION_DOWN:
                                listenerDrawingPitch.onDrawingPitchTouchDown(drawingPitchCell);
                                break;
                            case MotionEvent.ACTION_MOVE:
                                listenerDrawingPitch.onDrawingPitchTouchMoved(drawingPitchCell);
                                break;
                            case MotionEvent.ACTION_UP:
                                listenerDrawingPitch.onDrawingPitchTouchUp(drawingPitchCell);
                                break;
                        }
                    }
                }
                return true;
            }
        });
    }

    //endregion

    //region General Methods

    public ViewGroup getContainer() {
        return this.container;
    }

    private void initialize() {
        if (pxDrawingPitchCanvasOriginal == 0) {
            int drawingWidth = getWidth();
            int drawingHeight = getHeight();
            pxDrawingPitchCanvasOriginal = (drawingWidth < drawingHeight ? drawingWidth : drawingHeight); //square with smaller value
        }

        int pxDrawingPitchCanvas = pxDrawingPitchCanvasOriginal;
        int amountOfSeparatorLines = drawingPitchSize + 1; // + border
        pxDrawingPitchCell = (pxDrawingPitchCanvas - amountOfSeparatorLines) / drawingPitchSize;
        pxDrawingPitchCanvas = (pxDrawingPitchCell * drawingPitchSize) + amountOfSeparatorLines;
        setLayoutParams(new FrameLayout.LayoutParams(pxDrawingPitchCanvas, pxDrawingPitchCanvas));

        initializeDrawingSlots(drawingPitchSize);

        drawingPitchBitmap = Bitmap.createBitmap(pxDrawingPitchCanvas, pxDrawingPitchCanvas, Bitmap.Config.ARGB_8888);
        drawingPitchCanvas = new Canvas(drawingPitchBitmap);

        for (DrawingPitchCell drawingPitchCell : drawingPitchCells) {
            drawingPitchCanvas.drawRect(drawingPitchCell.getRectOnDrawingPitchCanvas(), drawingPitchCell.getPaint());
        }
    }

    private void initializeDrawingSlots(int size) {
        drawingPitchCells.clear();

        for (int i = 0; i < size; i++) {
            int rectStartOfWidth = 1; // border line
            rectStartOfWidth += (i * pxDrawingPitchCell); // fields
            rectStartOfWidth += i; // separator lines

            for (int j = 0; j < size; j++) {
                int rectStartOfHeight = 1; // border line
                rectStartOfHeight += (j * pxDrawingPitchCell); // fields
                rectStartOfHeight += j; // separator lines

                DrawingPitchCell drawingPitchCell = new DrawingPitchCell();
                drawingPitchCell.setRectOnDrawingPitchCanvas(new Rect(rectStartOfWidth, rectStartOfHeight, (rectStartOfWidth + pxDrawingPitchCell - 1), (rectStartOfHeight + pxDrawingPitchCell - 1)));
                drawingPitchCell.setPointInDrawingPitch(new Point(i, j));
                drawingPitchCells.add(drawingPitchCell);
            }
        }
    }

    private DrawingPitchCell canvasTouched(int x, int y) {
        for (DrawingPitchCell drawingPitchCell : drawingPitchCells) {
            if (drawingPitchCell.getRectOnDrawingPitchCanvas().contains(x, y)) {
                return drawingPitchCell;
            }
        }
        return null;
    }

    public void loadSnapchot(List<DrawingPitchCell> cells) {
        if (cells.size() != drawingPitchCells.size()) {
            return;
        }

        for (DrawingPitchCell cell : cells) {
            drawCell(cell);
        }
        drawingPitchCells = cells;

        invalidate();
    }

    public List<DrawingPitchCell> takeSnapshot() {
        List<DrawingPitchCell> cells = new ArrayList<>();

        for (DrawingPitchCell drawingPitchCell : drawingPitchCells) {
            cells.add(drawingPitchCell.clone());
        }

        return cells;
    }

    public List<DrawingPitchCell> getDrawingPitchCells() {
        return this.drawingPitchCells;
    }

    public void drawCell(DrawingPitchCell cell) {
        drawingPitchCanvas.drawRect(cell.getRectOnDrawingPitchCanvas(), cell.getPaint());
    }

    public Bitmap getDrawingPitchBitmap() {
        return this.drawingPitchBitmap;
    }

    public int getDrawingPitchSize() {
        return this.drawingPitchSize;
    }

    public void setDrawingPitchSize(int size) {
        if (size != drawingPitchSize) {
            this.drawingPitchSize = size;
            initialize();
            invalidate();
            if(listenerDrawingPitch != null){
                listenerDrawingPitch.onSizeChanged(size);
            }
        }
    }

    public void setListenerDrawingPitch(DrawingPitchListener callback) {
        this.listenerDrawingPitch = callback;
    }

    public DrawingPitchCell getDrawingPitchCell(int x, int y) {
        if (x >= drawingPitchSize || x < 0 || y >= drawingPitchSize || y < 0) {
            return null;
        }

        int arrayPosition = (x * drawingPitchSize) + y;
        if (arrayPosition < 0 || arrayPosition >= drawingPitchCells.size()) {
            return null;
        } else {
            return drawingPitchCells.get(arrayPosition);
        }
    }

    //endregion

    //region Events

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (drawingPitchBitmap != null) {
            Rect bitmapSize = new Rect(0, 0, drawingPitchBitmap.getWidth(), drawingPitchBitmap.getHeight());
            Rect canvasSize = new Rect(0, 0, canvas.getWidth(), canvas.getHeight());
            canvas.drawBitmap(drawingPitchBitmap, bitmapSize, canvasSize, null);
        }

        if (listenerDrawingPitch != null) {
            listenerDrawingPitch.onDraw(canvas);
        }
    }

    //endregion

}
