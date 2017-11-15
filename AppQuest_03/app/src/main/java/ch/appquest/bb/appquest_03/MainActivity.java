package ch.appquest.bb.appquest_03;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {

    //region Fields

    private static final String TAG = MainActivity.class.getSimpleName();

    private GoogleMap googleMap = null;
    private ArrayList<Marker> googleMapMarkers = new ArrayList<>();

    //endregion

    //region Events

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.googleMap)).getMapAsync(this);

        initFloatingActionButton();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        googleMap.clear();
        switch (id) {
            case R.id.action_log:
                return action_log_clicked();
            case R.id.action_deleteMarkers:
                return action_deleteMarkers_clicked();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        this.googleMap.setMyLocationEnabled(true);
        this.googleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        this.googleMap.getUiSettings().setCompassEnabled(true);
        this.googleMap.getUiSettings().setIndoorLevelPickerEnabled(false);
        this.googleMap.getUiSettings().setMapToolbarEnabled(false);
        this.googleMap.getUiSettings().setMyLocationButtonEnabled(true);
        this.googleMap.getUiSettings().setRotateGesturesEnabled(true);
        this.googleMap.getUiSettings().setScrollGesturesEnabled(true);
        this.googleMap.getUiSettings().setTiltGesturesEnabled(false);
        this.googleMap.getUiSettings().setZoomControlsEnabled(false);
        this.googleMap.getUiSettings().setZoomGesturesEnabled(true);
        this.googleMap.setBuildingsEnabled(true);
        this.googleMap.setIndoorEnabled(false);
        this.googleMap.setTrafficEnabled(false);

        // load saved markers on first start up
        for (MarkerOptions mo : SharedPreferencesUtility.loadMarkers(this)) {
            googleMapMarkers.add(GoogleMapsUtility.addMarker(googleMap, mo));
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        // save
        SharedPreferencesUtility.saveMarkers(this, googleMapMarkers);
    }

    @Override
    protected void onResume() {
        super.onResume();

        // if resumed, load markers again
        if (googleMap != null) {
            googleMapMarkers.clear();
            for (MarkerOptions mo : SharedPreferencesUtility.loadMarkers(this)) {
                googleMapMarkers.add(GoogleMapsUtility.addMarker(googleMap, mo));
            }
        }
    }

    //endregion

    //region General Methods

    private boolean action_log_clicked() {
        Intent i = new Intent("ch.appquest.intent.LOG");

        if (getPackageManager().queryIntentActivities(i, PackageManager.MATCH_DEFAULT_ONLY).isEmpty()) {
            Toast.makeText(this, "Logbook App not Installed", Toast.LENGTH_LONG).show();
            return false;
        }

        JSONObject json = new JSONObject();
        try {
            json.put("task", "Schatzkarte");

            JSONArray jsonPoints = new JSONArray();
            for (Marker marker : googleMapMarkers) {
                JSONObject jsonMarker = new JSONObject();
                Long microdegLat = Math.round(marker.getPosition().latitude * 1000000.0);
                Long microdegLon = Math.round(marker.getPosition().longitude * 1000000.0d);

                jsonMarker.put("lat", microdegLat);
                jsonMarker.put("lon", microdegLon);
                Log.d(TAG, "JSON : " + microdegLat + " / " + microdegLon);
                jsonPoints.put(jsonMarker);
            }

            json.put("points", jsonPoints);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        i.putExtra("ch.appquest.logmessage", json.toString());

        startActivity(i);
        return true;
    }

    private boolean action_deleteMarkers_clicked() {
        new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_menu_delete)
                .setTitle(R.string.dialog_deleteMarkers_Title)
                .setMessage(R.string.dialog_deleteMarkers_Body)
                .setPositiveButton(R.string.dialog_deleteMarkers_YesButton,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                for (Marker marker : googleMapMarkers) {
                                    marker.remove();
                                }
                                googleMapMarkers.clear();
                                SharedPreferencesUtility.removeMarkersFromPreferences(getBaseContext());
                                Toast.makeText(getBaseContext(), R.string.toast_deleteMarkers_successful, Toast.LENGTH_LONG).show();
                            }
                        })
                .setNegativeButton(R.string.dialog_deleteMarkers_NoButton, null)
                .show();
        return true;
    }

    private void initFloatingActionButton() {
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // get current location
                Location l = googleMap.getMyLocation();

                if (l != null) {
                    // location found
                    // -> add marker
                    googleMapMarkers.add(
                            GoogleMapsUtility.addMarker(googleMap, GoogleMapsUtility.getMarkerOptions(new LatLng(l.getLatitude(), l.getLongitude()), true))
                    );

                    GoogleMapsUtility.gotoMyLocation(googleMap, 17.0f);

                    Toast.makeText(getBaseContext(), R.string.toast_markerAdded, Toast.LENGTH_SHORT).show();
                } else {
                    // location not found
                    // -> toast
                    Toast.makeText(getBaseContext(), R.string.toast_locationNotFound, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    //endregion

}
