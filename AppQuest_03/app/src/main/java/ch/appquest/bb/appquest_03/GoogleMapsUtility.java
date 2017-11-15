package ch.appquest.bb.appquest_03;

import android.location.Location;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class GoogleMapsUtility {

    //region Fields

    private static final int MAP_DEFAULT_MARKER_ICON_RESOURCE = R.drawable.marker;

    //endregion

    //region Static Methods

    protected static void gotoMyLocation(GoogleMap map, float zoomLevel) {
        Location l = map.getMyLocation();
        moveCameraToLocation(map, new LatLng(l.getLatitude(), l.getLongitude()), zoomLevel);
    }

    protected static void moveCameraToLocation(GoogleMap map, LatLng position, float zoom) {
        CameraUpdate cuMove = CameraUpdateFactory.newLatLngZoom(position, zoom);
        map.animateCamera(cuMove);
    }

    protected static Marker addMarker(GoogleMap map, MarkerOptions mo) {
        return map.addMarker(mo);
    }

    protected static MarkerOptions getMarkerOptions(LatLng position, boolean draggable) {
        MarkerOptions mo = new MarkerOptions();
        mo.position(position);
        mo.draggable(draggable);
        mo.icon(BitmapDescriptorFactory.fromResource(MAP_DEFAULT_MARKER_ICON_RESOURCE));
        return mo;
    }

    //endregion

}
