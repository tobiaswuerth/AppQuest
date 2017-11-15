package ch.apquest.bb.appquest_04.Step;

import android.content.Context;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v4.content.ContextCompat;
import android.view.View;

import com.androidplot.ui.SizeLayoutType;
import com.androidplot.ui.SizeMetrics;
import com.androidplot.xy.BezierLineAndPointFormatter;
import com.androidplot.xy.LineAndPointFormatter;
import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.xy.XYPlot;
import com.androidplot.xy.XYSeries;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ch.apquest.bb.appquest_04.Other.Constants;
import ch.apquest.bb.appquest_04.Other.RingBuffer;
import ch.apquest.bb.appquest_04.R;

public class StepController implements SensorEventListener {

    //region Fields

    private boolean accelerating = false;
    private StepListener stepListener = null;

    private RingBuffer buffer = new RingBuffer(Constants.SC_STEP_MEASUREMENT_COUNT);

    private SensorManager sensorManager = null;
    private Sensor sensorAccelerometer = null;

    private XYPlot plot = null;

    private Context context = null;

    //endregion

    //region Constructor

    public StepController(Context context, StepListener stepListener, XYPlot plot) {
        this.stepListener = stepListener;
        this.plot = plot;
        this.context = context;

        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        sensorAccelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        // setup plot design
        plot.getLayoutManager().remove(plot.getLegendWidget());
        plot.getLayoutManager().remove(plot.getTitleWidget());
        plot.getLayoutManager().remove(plot.getDomainLabelWidget());
        plot.getLayoutManager().remove(plot.getRangeLabelWidget());

        plot.getBackgroundPaint().setColor(Color.TRANSPARENT);
        plot.getBorderPaint().setColor(Color.TRANSPARENT);
        plot.setDomainLeftMax(0);

        plot.getGraphWidget().getDomainLabelPaint().setColor(Color.TRANSPARENT);
        plot.getGraphWidget().getRangeLabelPaint().setColor(Color.TRANSPARENT);
        plot.getGraphWidget().getGridBackgroundPaint().setColor(Color.TRANSPARENT);
        plot.getGraphWidget().getBackgroundPaint().setColor(Color.TRANSPARENT);
        plot.getGraphWidget().setSize(new SizeMetrics(0, SizeLayoutType.FILL, 0, SizeLayoutType.FILL));
        plot.getGraphWidget().setMargins(0f, 0f, 0f, 0f);
        plot.getGraphWidget().setRangeLabelWidth(0f);
        plot.getGraphWidget().setDomainLabelWidth(0f);
    }

    //endregion

    //region Events

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        float x = event.values[0];
        float y = event.values[1];
        float z = event.values[2];
        float magnitude = (float) (Math.pow(x, Constants.SC_STEP_MAGNITUDE_APLIFIER) + Math.pow(y, Constants.SC_STEP_MAGNITUDE_APLIFIER) + Math.pow(z, Constants.SC_STEP_MAGNITUDE_APLIFIER));

        buffer.put(magnitude);

        float average = buffer.getAverage();

        if (!accelerating && (magnitude > average * Constants.SC_STEP_ACCELERATION_MODIFIER_TOP)) {
            accelerating = true;
            stepListener.onStep();
        }

        if (accelerating && (magnitude < average * Constants.SC_STEP_ACCELERATION_MODIFIER_BOTTOM)) {
            accelerating = false;
        }

        if (plot.getVisibility() == View.VISIBLE) {
            updatePlot();
        }
    }

    //endregion

    //region General Methods

    private void updatePlot() {
        // sensor values
        List<Float> values = buffer.getValues();
        XYSeries dataSeries = new SimpleXYSeries(values, SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, "");
        LineAndPointFormatter dataSeriesFormat = new BezierLineAndPointFormatter();
        dataSeriesFormat.getFillPaint().setColor(ContextCompat.getColor(context, R.color.colorAccentLight)); // fill color
        dataSeriesFormat.getLinePaint().setColor(Color.BLACK); // line color
        dataSeriesFormat.getVertexPaint().setColor(Color.TRANSPARENT); // vertex color (dots in graph)

        // average line
        XYSeries averageSeries1 = new SimpleXYSeries(new ArrayList<Float>(Collections.nCopies(values.size(), buffer.getAverage() * Constants.SC_STEP_ACCELERATION_MODIFIER_TOP)), SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, "");
        XYSeries averageSeries2 = new SimpleXYSeries(new ArrayList<Float>(Collections.nCopies(values.size(), buffer.getAverage() * Constants.SC_STEP_ACCELERATION_MODIFIER_BOTTOM)), SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, "");
        LineAndPointFormatter averageSeriesFormat = new BezierLineAndPointFormatter();
        averageSeriesFormat.getFillPaint().setColor(Color.TRANSPARENT); // fill color
        averageSeriesFormat.getLinePaint().setColor(ContextCompat.getColor(context, R.color.colorPrimaryDark)); // line color
        averageSeriesFormat.getVertexPaint().setColor(Color.TRANSPARENT); // vertex color (dots in graph)

        // x-axis value range minimum to display
        plot.setDomainRightMin(values.size()-1);

        // set new data
        plot.clear();
        plot.addSeries(dataSeries, dataSeriesFormat);
        plot.addSeries(averageSeries1, averageSeriesFormat);
        plot.addSeries(averageSeries2, averageSeriesFormat);
        plot.redraw();
    }

    public void resume() {
        sensorManager.registerListener(this, sensorAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
    }

    public void pause() {
        sensorManager.unregisterListener(this);
    }

//endregion

}