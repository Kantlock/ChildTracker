package com.sbc.childtracker;

import android.Manifest;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.sbc.childtracker.requests.CustomRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Timer;
import java.util.TimerTask;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

  private static final String TAG = MapsActivity.class.getSimpleName();
  private static final String serverAddress = "https://sbcchildtrackerapi.azurewebsites.net";

  private GoogleMap mMap;
  private Marker positionMarker;
  private LatLng position;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_maps);
    SupportMapFragment mapFragment =
            (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
    mapFragment.getMapAsync(this);
  }

  @Override
  public void onMapReady(GoogleMap googleMap) {
    mMap = googleMap;

    position = new LatLng(39.8755761, 32.83285);

    if (mMap != null) {
      positionMarker =
              mMap.addMarker(
                      new MarkerOptions().position(position).title("Ali").snippet(" "));

      mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL); // MAP TYPE

      //mMap.setTrafficEnabled(true); // TRAFFIC

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
    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(position, 18));

    getLocationInfoFromApi();
  }

  private void getLocationInfoFromApi() {
    TimerTask repeatedTask =
            new TimerTask() {
              @Override
              public void run() {

                runOnUiThread(
                        new Runnable() {
                          @Override
                          public void run() {

                            SharedPreferences pref =
                                    getApplicationContext().getSharedPreferences("MyPref", 0);

                            Optional<String> authToken = Optional.ofNullable(pref.getString("auth", null));

                            authToken.ifPresent(
                                    token -> {
                                      String url = serverAddress + "/api/user/coordinates";

                                      Map<String, String> params = new HashMap<>();

                                      params.put("auth", token);

                                      Response.Listener<JSONObject> successListener =
                                              new Response.Listener<JSONObject>() {
                                                @Override
                                                public void onResponse(JSONObject response) {
                                                  try {
                                                    double latitude = response.getDouble("latitude");
                                                    double longitude = response.getDouble("longitude");

                                                    position = new LatLng(latitude, longitude);

                                                    Log.d(TAG, position.toString());
                                                  } catch (JSONException e) {
                                                    e.printStackTrace();
                                                  }
                                                }
                                              };

                                      Response.ErrorListener errorListener =
                                              new Response.ErrorListener() {
                                                @Override
                                                public void onErrorResponse(VolleyError error) {
                                                  Log.e(
                                                          TAG,
                                                          "Error: "
                                                                  + new String(
                                                                  error.networkResponse.data, StandardCharsets.UTF_8));
                                                }
                                              };

                                      RequestQueue queue = Volley.newRequestQueue(MapsActivity.this);
                                      CustomRequest customRequest =
                                              new CustomRequest(
                                                      Request.Method.POST, url, params, successListener, errorListener);

                                      queue
                                              .add(customRequest)
                                              .setRetryPolicy(
                                                      new RetryPolicy() {
                                                        @Override
                                                        public int getCurrentTimeout() {
                                                          return 5000;
                                                        }

                                                        @Override
                                                        public int getCurrentRetryCount() {
                                                          return 0; // retry turn off
                                                        }

                                                        @Override
                                                        public void retry(VolleyError error) throws VolleyError {}
                                                      });
                                    });

                            positionMarker.setPosition(position);
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(position, 18));
                          }
                        });
              }
            };

    Timer timer = new Timer("Timer");
    long delay = 5000L;
    long period = 5000L;

    timer.scheduleAtFixedRate(repeatedTask, delay, period);
  }

  /*@Override
  public void onBackPressed() {
    finish();

    Intent intent = new Intent(this, MainActivity.class);
    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

    finish();
    startActivity(intent);
  }*/

}
