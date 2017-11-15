package ch.appquest.bb.appquest_03;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class SharedPreferencesUtility {

    //region Fields

    private static final String TAG = SharedPreferencesUtility.class.getSimpleName();
    private static final String PREF_KEY_MARKERS = TAG + "$MARKERS";
    private static final String JSON_KEY_LAT = "lat";
    private static final String JSON_KEY_LNG = "lng";
    private static final String JSON_KEY_DRAGGABLE = "drag";

    //endregion

    //region Static Methods

    protected static void saveMarkers(Context context, ArrayList<Marker> markers) {
        SharedPreferences.Editor editor = context.getSharedPreferences(TAG, Context.MODE_PRIVATE).edit();
        try {
            JSONArray jsonMarkers = new JSONArray();

            for (Marker m : markers) {
                JSONObject jsonMarker = new JSONObject();
                jsonMarker.put(JSON_KEY_LAT, m.getPosition().latitude);
                jsonMarker.put(JSON_KEY_LNG, m.getPosition().longitude);
                jsonMarker.put(JSON_KEY_DRAGGABLE, m.isDraggable());
                jsonMarkers.put(jsonMarker);
                m.remove();
            }

            editor.putString(PREF_KEY_MARKERS, jsonMarkers.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        editor.apply();
    }

    protected static ArrayList<MarkerOptions> loadMarkers(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(TAG, Context.MODE_PRIVATE);
        String sJsonMarkers = prefs.getString(PREF_KEY_MARKERS, "");
        if (!sJsonMarkers.equals("")) {
            ArrayList<MarkerOptions> markers = new ArrayList<>();

            try {
                JSONArray jsonMarkers = new JSONArray(sJsonMarkers);

                for (int i = 0; i < jsonMarkers.length(); i++) {
                    markers.add(
                            GoogleMapsUtility.getMarkerOptions(
                                    new LatLng(
                                            jsonMarkers.getJSONObject(i).getDouble(JSON_KEY_LAT)
                                            , jsonMarkers.getJSONObject(i).getDouble(JSON_KEY_LNG)),
                                    jsonMarkers.getJSONObject(i).getBoolean(JSON_KEY_DRAGGABLE))
                    );
                }

                return markers;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return new ArrayList<MarkerOptions>();
    }

    protected static void removeMarkersFromPreferences(Context context) {
        context.getSharedPreferences(TAG, Context.MODE_PRIVATE).edit().clear().apply();
    }

    //endregion

}
