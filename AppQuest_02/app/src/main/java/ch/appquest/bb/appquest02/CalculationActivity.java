package ch.appquest.bb.appquest02;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.app.Activity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

public class CalculationActivity extends Activity {

    private static final int SCAN_QR_CODE_REQUEST_CODE = 0;

    private TextView txtAngleAlpha;
    private TextView txtAngleBeta;
    private TextView txtDistance;
    private TextView txtResult;

    private Double angleAlpha;
    private Double angleBeta;
    private Double distance;
    private Double height;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calculation);
        getActionBar().setDisplayHomeAsUpEnabled(true);

        txtAngleAlpha = (TextView) findViewById(R.id.txtAngleAlpha);
        txtAngleBeta = (TextView) findViewById(R.id.txtAngleBeta);
        txtDistance = (TextView) findViewById(R.id.txtDistance);
        txtResult = (TextView) findViewById(R.id.txtResult);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            angleAlpha = extras.getDouble(MainActivity.PARAMKEY_ANGLE_ANLPHA);
            angleBeta = extras.getDouble(MainActivity.PARAMKEY_ANGLE_BETA);

            txtAngleAlpha.setText(String.valueOf(angleAlpha) + "°");
            txtAngleBeta.setText(String.valueOf(angleBeta) + "°");
        }

        txtDistance.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try {
                    distance = Double.parseDouble(txtDistance.getText().toString());
                    calculate();
                } catch (Exception ex) {
                    txtResult.setText("");
                    height = null;
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.menu_log) {
            if (height != null) {
                Intent intent = new Intent("com.google.zxing.client.android.SCAN");
                intent.putExtra("SCAN_MODE", "QR_CODE_MODE");
                startActivityForResult(intent, SCAN_QR_CODE_REQUEST_CODE);
                return true;
            }
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == SCAN_QR_CODE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                String logMsg = intent.getStringExtra("SCAN_RESULT");

                Intent i = new Intent("ch.appquest.intent.LOG");

                if (getPackageManager().queryIntentActivities(i, PackageManager.MATCH_DEFAULT_ONLY).isEmpty()) {
                    Toast.makeText(this, "Logbook App not Installed", Toast.LENGTH_LONG).show();
                    return;
                }

                JSONObject json = new JSONObject();
                try {
                    json.put("task", "Groessenmesser");
                    json.put("object", logMsg);
                    json.put("height", height.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                i.putExtra("ch.appquest.logmessage", json.toString());

                startActivity(i);
            }
        }
    }

    private void calculate() {
        Double sideC = distance / Math.sin(Math.toRadians(angleAlpha));
        Log.d(MainActivity.TAG, "Side C: " + sideC);

        Double angleGamma = 180d - angleAlpha - angleBeta;
        Log.d(MainActivity.TAG, "Angle Gamma: " + angleGamma);

        height = sideC / Math.sin(Math.toRadians(angleGamma)) * Math.sin(Math.toRadians(angleBeta));
        Log.d(MainActivity.TAG, "height: " + height);

        txtResult.setText(String.valueOf(height));
    }

}
