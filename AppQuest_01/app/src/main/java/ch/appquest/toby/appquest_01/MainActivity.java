package ch.appquest.toby.appquest_01;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.androidplot.xy.BezierLineAndPointFormatter;
import com.androidplot.xy.LineAndPointFormatter;
import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.xy.XYPlot;
import com.androidplot.xy.XYSeries;

import java.util.ArrayList;
import java.util.Arrays;

public class MainActivity extends Activity implements SensorEventListener {

    //region Fields

    // UID for AppQuest QR-Code intent result
    private static final int SCAN_QR_CODE_REQUEST_CODE = 0;

    // Graph
    private XYPlot plot;
    private ArrayList<Double> data = new ArrayList<Double>();
    private static final int MAX_GRAPH_DATA = 150;

    // Sensor
    private SensorManager sensorManager;
    private Sensor magneticSensor;

    // Sensor Visualisation
    private ProgressBar pbX;
    private ProgressBar pbY;
    private ProgressBar pbZ;
    private TextView txtValue;

    //endregion

    //region Events

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // this method is called when the app first launches

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // initialize magnetic field sensor
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        magneticSensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

        // get controls and assign to fields
        pbX = (ProgressBar) findViewById(R.id.pbX);
        pbY = (ProgressBar) findViewById(R.id.pbY);
        pbZ = (ProgressBar) findViewById(R.id.pbZ);
        txtValue = (TextView) findViewById(R.id.txtValue);
        plot = (XYPlot) findViewById(R.id.mySimpleXYPlot);

        // initialize display mode
        initDisplayMode();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // add new menu item "log" in the actionbar
        MenuItem menuItem = menu.add("Log");
        menuItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Intent intent = new Intent("com.google.zxing.client.android.SCAN");
                intent.putExtra("SCAN_MODE", "QR_CODE_MODE");
                startActivityForResult(intent, SCAN_QR_CODE_REQUEST_CODE);
                return false;
            }
        });

        // show in actionbar if room
        menuItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM | MenuItem.SHOW_AS_ACTION_WITH_TEXT);
        menuItem.setIcon(R.drawable.ic_note_light);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        // this method gets called whenever  startActivityForResult(..) gets called
        if (requestCode == SCAN_QR_CODE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                String logMsg = intent.getStringExtra("SCAN_RESULT");
                // submit to the AppQuest-Logbuch App
                (new LogBuchLogger(this)).log(logMsg);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        // register sensor
        sensorManager.registerListener(this, magneticSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        super.onPause();

        // release sensor
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        updateGui(event.values);
        updateGraph(event.values);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    //endregion

    //region General Methods

    private void updateGui(float[] eventValues) {
        // calculate and display sensor value
        txtValue.setText(Double.toString(Math.sqrt(eventValues[0] * eventValues[0] + eventValues[1] * eventValues[1] + eventValues[2] * eventValues[2])));

        // set progressbar values
        pbX.setProgress((int) eventValues[0]);
        pbY.setProgress((int) eventValues[1]);
        pbZ.setProgress((int) eventValues[2]);
    }

    private void updateGraph(float[] eventValues) {
        // manage data
        if (data.size() >= MAX_GRAPH_DATA) {
            data.remove(0);
        }
        data.add(Math.sqrt(eventValues[0] * eventValues[0] + eventValues[1] * eventValues[1] + eventValues[2] * eventValues[2]));

        // create new Number[] out of data
        Number[] dataSeriesNumbers = new Number[data.size()];
        for (int i = 0; i < data.size(); i++) {
            dataSeriesNumbers[i] = data.get(i);
        }

        XYSeries dataSeries = new SimpleXYSeries(
                Arrays.asList(dataSeriesNumbers),
                SimpleXYSeries.ArrayFormat.Y_VALS_ONLY,
                " Messwerte "
        );

        LineAndPointFormatter dataSeriesFormat = new BezierLineAndPointFormatter();

        // fill color
        Paint paintFill = dataSeriesFormat.getFillPaint();
        paintFill.setColor(Color.argb(175, 148, 50, 50));

        // line color
        Paint paintLine = dataSeriesFormat.getLinePaint();
        paintLine.setColor(Color.argb(150, 0, 0, 0));

        // vertex color (dots in graph)
        Paint paintVertex = dataSeriesFormat.getVertexPaint();
        paintVertex.setColor(Color.argb(0, 0, 0, 0)); // invisible color

        // hide label widget
        plot.getDomainLabelWidget().setVisible(false);
        plot.getRangeLabelWidget().setVisible(false);

        // x-axis value range minimum to display
        plot.setDomainLeftMax(0);
        plot.setDomainRightMin(MAX_GRAPH_DATA);

        // title
        plot.setTitle("Sensor Graph");

        // set new data
        plot.clear();
        plot.addSeries(dataSeries, dataSeriesFormat);
        plot.redraw();
    }

    private void initDisplayMode() {
        // set max value of progressbar
        pbX.setMax((int) magneticSensor.getMaximumRange());
        pbY.setMax((int) magneticSensor.getMaximumRange());
        pbZ.setMax((int) magneticSensor.getMaximumRange());
    }

    //endregion

}
