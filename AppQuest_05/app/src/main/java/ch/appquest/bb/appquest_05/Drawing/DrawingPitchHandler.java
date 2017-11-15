package ch.appquest.bb.appquest_05.Drawing;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.RectF;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import ch.appquest.bb.appquest_05.Other.Constants;
import ch.appquest.bb.appquest_05.R;

public class DrawingPitchHandler implements DrawingPitchListener {

    //region Fields

    private DrawingPitch drawingPitch = null;
    private int currentDrawingColor = Color.WHITE;
    private DrawingMode currentDrawingMode = Constants.INITIAL_DRAWING_MODE;
    private AppCompatActivity parent = null;
    private DrawingPitchCell drawingStartingCell = null;
    private DrawingPitchCell drawingCurrentCell = null;

    private ArrayList<List<DrawingPitchCell>> snapshots = new ArrayList<>();
    private boolean justAddedNewSnapshot = false;
    private List<DrawingPitchCell> tmpSnapshot = null;

    //endregion

    //region Constructor

    public DrawingPitchHandler(AppCompatActivity parent, DrawingPitch drawingPitch) {
        this.parent = parent;
        this.drawingPitch = drawingPitch;
    }

    //endregion

    //region General Methods

    public void updateSnapchots() {
        if (snapshots.size() == Constants.MAX_SAVED_UNDO_IN_CACHE && snapshots.size() > 0) {
            snapshots.remove(0);
        }

        if (tmpSnapshot != null) {
            snapshots.add(tmpSnapshot);
            tmpSnapshot = null;
        } else {
            snapshots.add(drawingPitch.takeSnapshot());
        }

        justAddedNewSnapshot = true;
    }

    public DrawingMode getDrawingMode() {
        return this.currentDrawingMode;
    }

    public DrawingPitchHandler setDrawingMode(DrawingMode drawingMode) {
        this.currentDrawingMode = drawingMode;
        return this;
    }

    public void loadFromImage(final Uri imageUri) {
        if (tmpSnapshot != null || snapshots.size() == 0) {
            updateSnapchots();
        }

        final ProgressDialog progressDialog = new ProgressDialog(parent);
        progressDialog.setTitle(parent.getString(R.string.dialog_processing_image_title));
        progressDialog.setMessage(parent.getString(R.string.dialog_processing_image_body));
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setIndeterminate(false);
        progressDialog.setProgress(0);
        progressDialog.setMax(100);
        progressDialog.setProgressNumberFormat(null);
        progressDialog.setCancelable(false);
        progressDialog.show();

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Bitmap bm = MediaStore.Images.Media.getBitmap(parent.getContentResolver(), imageUri);

                    if (bm != null) {

                        int drawingPitchSize = drawingPitch.getDrawingPitchSize();
                        double minX = bm.getWidth() / drawingPitchSize;
                        double minY = bm.getHeight() / drawingPitchSize;

                        for (int i = 0; i < drawingPitchSize; i++) {
                            for (int j = 0; j < drawingPitchSize; j++) {
                                long a = 0l;
                                long r = 0l;
                                long g = 0l;
                                long b = 0l;

                                int amountOfPixels = 0;

                                for (int k = (int) (minX * i); k < (int) ((minX * i) + minX); k++) {
                                    for (int h = (int) (minY * j); h < (int) ((minY * j) + minY); h++) {
                                        int pixel = bm.getPixel(k, h);
                                        a += Color.alpha(pixel);
                                        r += Color.red(pixel);
                                        g += Color.green(pixel);
                                        b += Color.blue(pixel);

                                        amountOfPixels++;
                                    }
                                }

                                long aAverage = a / amountOfPixels;
                                long rAverage = r / amountOfPixels;
                                long gAverage = g / amountOfPixels;
                                long bAverage = b / amountOfPixels;

                                int c = Color.argb((int) aAverage, (int) rAverage, (int) gAverage, (int) bAverage);
                                DrawingPitchCell cell = drawingPitch.getDrawingPitchCell(i, j);
                                cell.setColor(c);
                                drawingPitch.drawCell(cell);

                                int p = ((i * drawingPitchSize + j) * 100) / (drawingPitchSize * drawingPitchSize);
                                if (p != progressDialog.getProgress()) {
                                    progressDialog.setProgress(p);
                                }
                            }
                        }

                        Snackbar.make(drawingPitch, R.string.dialog_processing_image_successful, Snackbar.LENGTH_LONG).show();
                        parent.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                redraw();
                                updateSnapchots();
                            }
                        });
                    }
                } catch (IOException e) {
                    Snackbar.make(drawingPitch, R.string.dialog_processing_image_failed, Snackbar.LENGTH_LONG).show();
                }

                parent.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progressDialog.dismiss();
                    }
                });
            }
        }).start();
    }

    public void clearPaint() {
        if (tmpSnapshot != null || snapshots.size() == 0) {
            updateSnapchots();
        }

        for (DrawingPitchCell drawingPitchCell : drawingPitch.getDrawingPitchCells()) {
            drawingPitchCell.setColor(Color.WHITE);
            drawingPitch.drawCell(drawingPitchCell);
        }
        redraw();
        updateSnapchots();
    }

    public void redraw() {
        drawingPitch.invalidate();
    }

    private List<DrawingPitchCell> getSameColoredAdjecentPitchCells(DrawingPitchCell drawingPitchCell) {
        List<DrawingPitchCell> sameColoredPitchCells = new ArrayList<>();
        sameColoredPitchCells.add(drawingPitchCell);
        getSameColoredAdjecentPitchCellsRekursively(sameColoredPitchCells, drawingPitchCell);
        return sameColoredPitchCells;
    }

    private void getSameColoredAdjecentPitchCellsRekursively(List<DrawingPitchCell> alreadyAdjecentPitchCells, DrawingPitchCell parentPitchCell) {
        List<DrawingPitchCell> adjecentPitchCells = getAdjecentPitchCells(parentPitchCell);

        for (int i = adjecentPitchCells.size() - 1; i >= 0; i--) {
            if (adjecentPitchCells.get(i).getColor() != parentPitchCell.getColor() || alreadyAdjecentPitchCells.contains(adjecentPitchCells.get(i))) {
                adjecentPitchCells.remove(i);
            }
        }

        if (adjecentPitchCells.size() > 0) {
            alreadyAdjecentPitchCells.addAll(adjecentPitchCells);

            for (DrawingPitchCell adjecentPitchCell : adjecentPitchCells) {
                getSameColoredAdjecentPitchCellsRekursively(alreadyAdjecentPitchCells, adjecentPitchCell);
            }
        }
    }

    private List<DrawingPitchCell> getAdjecentPitchCells(DrawingPitchCell drawingPitchCell) {
        Point drawingPitchCellLocation = drawingPitchCell.getPointInDrawingPitch();
        List<DrawingPitchCell> adjecentPitchCells = new ArrayList<>();

        if (drawingPitchCellLocation.y > 0) {
            adjecentPitchCells.add(drawingPitch.getDrawingPitchCell(drawingPitchCellLocation.x, drawingPitchCellLocation.y - 1));
        }
        if ((drawingPitchCellLocation.y + 1) < drawingPitch.getDrawingPitchSize()) {
            adjecentPitchCells.add(drawingPitch.getDrawingPitchCell(drawingPitchCellLocation.x, drawingPitchCellLocation.y + 1));
        }
        if (drawingPitchCellLocation.x > 0) {
            adjecentPitchCells.add(drawingPitch.getDrawingPitchCell(drawingPitchCellLocation.x - 1, drawingPitchCellLocation.y));
        }
        if ((drawingPitchCellLocation.x + 1) < drawingPitch.getDrawingPitchSize()) {
            adjecentPitchCells.add(drawingPitch.getDrawingPitchCell(drawingPitchCellLocation.x + 1, drawingPitchCellLocation.y));
        }

        return adjecentPitchCells;
    }

    public void setCurrentDrawingColor(int color) {
        this.currentDrawingColor = color;
    }

    private boolean ovalContainsPoint(RectF oval, Point point) {
        float rX = (oval.right - oval.left) / 2;
        float rY = (oval.bottom - oval.top) / 2;

        return (((Math.pow(point.x - oval.centerX(), 2) / Math.pow(rX, 2)) + (Math.pow(point.y - oval.centerY(), 2) / Math.pow(rY, 2))) <= 1.0f);
    }

    private RectF calcRect(int x1, int y1, int x2, int y2) {
        int left = (x1 < x2 ? x1 : x2);
        int right = (x2 > x1 ? x2 : x1);
        int top = (y1 < y2 ? y1 : y2);
        int bottom = (y2 > y1 ? y2 : y1);

        return (new RectF(left, top, right, bottom));
    }

    public void undo() {
        if (tmpSnapshot != null) {
            updateSnapchots();
        }

        if (justAddedNewSnapshot && snapshots.size() > 1) {
            justAddedNewSnapshot = false;
            snapshots.remove(snapshots.size() - 1); // because its the same as its now
        }

        if (!justAddedNewSnapshot && snapshots.size() > 0) {
            List<DrawingPitchCell> cells = snapshots.get(snapshots.size() - 1);
            snapshots.remove(snapshots.size() - 1);
            drawingPitch.loadSnapchot(cells);
            Snackbar.make(drawingPitch, parent.getString(R.string.dialog_undo_successful), Snackbar.LENGTH_LONG).show();
        } else {
            Snackbar.make(drawingPitch, parent.getString(R.string.dialog_undo_failed), Snackbar.LENGTH_LONG).show();
        }
    }

    //endregion

    //region Events

    @Override
    public void onInitialized() {
        drawingPitch.getContainer().invalidate();
        drawingPitch.setBackgroundColor(Color.BLACK);
    }

    @Override
    public void onDrawingPitchTouchDown(final DrawingPitchCell drawingPitchCell) {
        switch (currentDrawingMode) {
            case SQUARE:
            case SPHERE:
                drawingStartingCell = drawingPitchCell;
                drawingCurrentCell = drawingPitchCell;
                break;
        }
        if (tmpSnapshot != null || snapshots.size() == 0) {
            updateSnapchots();
        }
    }

    @Override
    public void onDrawingPitchTouchMoved(final DrawingPitchCell drawingPitchCell) {
        switch (currentDrawingMode) {
            case PEN:
                drawingPitchCell.setColor(currentDrawingColor);
                drawingPitch.drawCell(drawingPitchCell);
                tmpSnapshot = drawingPitch.takeSnapshot();
                break;
            case SQUARE:
            case SPHERE:
                if (drawingStartingCell == null) {
                    drawingStartingCell = drawingPitchCell;
                }
                drawingCurrentCell = drawingPitchCell;
                break;
            default:
                return;
        }

        redraw();
    }

    @Override
    public void onDrawingPitchTouchUp(final DrawingPitchCell drawingPitchCell) {
        switch (currentDrawingMode) {
            case PEN:
                drawingPitchCell.setColor(currentDrawingColor);
                drawingPitch.drawCell(drawingPitchCell);
                redraw();
                updateSnapchots();
                break;
            case FILL:
                final ProgressDialog pd = new ProgressDialog(parent);
                pd.setTitle(parent.getString(R.string.dialog_fill_canvas_title));
                pd.setMessage(parent.getString(R.string.dialog_fill_canvas_body));
                pd.setCancelable(false);
                pd.show();

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        List<DrawingPitchCell> cells = getSameColoredAdjecentPitchCells(drawingPitchCell);
                        for (DrawingPitchCell cell : cells) {
                            cell.setColor(currentDrawingColor);
                            drawingPitch.drawCell(cell);
                        }

                        parent.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                redraw();
                                updateSnapchots();
                                pd.dismiss();
                            }
                        });
                    }
                }).start();
                break;
            case SPHERE:
            case SQUARE:
                RectF rect = calcRect(drawingStartingCell.getPointInDrawingPitch().x
                        , drawingStartingCell.getPointInDrawingPitch().y
                        , drawingPitchCell.getPointInDrawingPitch().x
                        , drawingPitchCell.getPointInDrawingPitch().y);

                switch (currentDrawingMode) {
                    case SPHERE:
                        RectF rectSphere = new RectF(drawingStartingCell.getRectOnDrawingPitchCanvas().centerX(),
                                drawingStartingCell.getRectOnDrawingPitchCanvas().centerY()
                                , drawingCurrentCell.getRectOnDrawingPitchCanvas().centerX()
                                , drawingCurrentCell.getRectOnDrawingPitchCanvas().centerY());

                        for (int i = (int) rect.left; i <= rect.right; i++) {
                            for (int j = (int) rect.top; j <= rect.bottom; j++) {
                                DrawingPitchCell cell = drawingPitch.getDrawingPitchCell(i, j);
                                if (ovalContainsPoint(rectSphere, new Point(cell.getRectOnDrawingPitchCanvas().centerX(), cell.getRectOnDrawingPitchCanvas().centerY()))) {
                                    cell.setColor(currentDrawingColor);
                                    drawingPitch.drawCell(cell);
                                }
                            }
                        }
                        break;
                    case SQUARE:

                        for (int i = (int) rect.left; i <= rect.right; i++) {
                            for (int j = (int) rect.top; j <= rect.bottom; j++) {
                                DrawingPitchCell cell = drawingPitch.getDrawingPitchCell(i, j);
                                cell.setColor(currentDrawingColor);
                                drawingPitch.drawCell(cell);
                            }
                        }
                        break;
                }

                drawingCurrentCell = null;
                drawingStartingCell = null;
                redraw();
                updateSnapchots();
                break;
        }
    }

    @Override
    public void onDraw(final Canvas canvas) {
        switch (currentDrawingMode) {
            case SQUARE:
            case SPHERE:
                if (drawingStartingCell != null && drawingCurrentCell != null) {
                    RectF rect = calcRect(drawingStartingCell.getRectOnDrawingPitchCanvas().centerX(),
                            drawingStartingCell.getRectOnDrawingPitchCanvas().centerY()
                            , drawingCurrentCell.getRectOnDrawingPitchCanvas().centerX()
                            , drawingCurrentCell.getRectOnDrawingPitchCanvas().centerY());
                    Paint paint = new Paint();
                    paint.setColor(currentDrawingColor);
                    paint.setAlpha(150);
                    paint.setStyle(Paint.Style.FILL);

                    switch (currentDrawingMode) {
                        case SQUARE:
                            canvas.drawRect(rect, paint);
                            break;
                        case SPHERE:
                            canvas.drawOval(rect, paint);
                            break;
                    }

                    paint.setColor(Color.BLACK);
                    paint.setStyle(Paint.Style.STROKE);
                    paint.setStrokeWidth(3f);

                    switch (currentDrawingMode) {
                        case SQUARE:
                            canvas.drawRect(rect, paint);
                            break;
                        case SPHERE:
                            canvas.drawOval(rect, paint);
                            break;
                    }

                    break;
                }
        }
    }

    @Override
    public void onSizeChanged(int size) {
        snapshots.clear();
    }

    //endregion

}
