package com.sbc.childtracker;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

  private GoogleMap mMap;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_maps);
    // Obtain the SupportMapFragment and get notified when the map is ready to be used.
    SupportMapFragment mapFragment =
        (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
    mapFragment.getMapAsync(this);
  }

  @Override
  public void onMapReady(GoogleMap googleMap) {
    mMap = googleMap;

    LatLng CAALAR = new LatLng(39.8755761, 32.83285);
    LatLng SaiD = new LatLng(39.8528257, 32.8436875);

    if (mMap != null) {
      mMap.addMarker(
          new MarkerOptions()
              .position(CAALAR)
              .title("CaalaR in the HousE")
              .snippet("HZ MUHAMMED OKUMA BILMIYORMUS"));

      mMap.addMarker(
          new MarkerOptions()
              .position(SaiD)
              .title("SaiDoW in the HousE")
              .snippet("BANA VAR DENDI E BANA VAR DENDI"));

      PolylineOptions options =
          new PolylineOptions()
              .add(CAALAR)
              .add(SaiD)
              .width(7)
              .color(Color.GREEN)
              .visible(true)
              .geodesic(true);
      mMap.addPolyline(options);

      mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID); // MAP TYPE

      mMap.setTrafficEnabled(true); // TRAFFIC

      if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
              == PackageManager.PERMISSION_GRANTED
          && ContextCompat.checkSelfPermission(
                  this, android.Manifest.permission.ACCESS_COARSE_LOCATION)
              == PackageManager.PERMISSION_GRANTED) {
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
      } else {
        ActivityCompat.requestPermissions(
            this,
            new String[] {
              Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION
            },
            1530);
      }
    }
    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(CAALAR, 11));
  }
}
