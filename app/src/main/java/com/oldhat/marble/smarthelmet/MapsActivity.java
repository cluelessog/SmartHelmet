package com.oldhat.marble.smarthelmet;

import android.Manifest.permission;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;


public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {


  private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
  private static final int DEFAULT_ZOOM = 15;
  private static final String TAG = MapsActivity.class.getSimpleName();
  private static final String KEY_CAMERA_POSITION = "camera_position";
  private static final String KEY_LOCATION = "location";
  private String name;
  private LatLng latLng;
  private CameraPosition mCameraPosition;
  private GoogleMap mMap;
  private FusedLocationProviderClient mFusedLocationProviderClient;
  private boolean mLocationPermissionGranted;
  private Location mLastKnownLocation;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    if (savedInstanceState != null) {
      mLastKnownLocation = savedInstanceState.getParcelable(KEY_LOCATION);
      mCameraPosition = savedInstanceState.getParcelable(KEY_CAMERA_POSITION);
    }
    setContentView(R.layout.activity_maps);

    PlaceAutocompleteFragment autocompleteFragment = (PlaceAutocompleteFragment)
        getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);

    autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
      @Override
      public void onPlaceSelected(Place place) {
        Log.i(TAG, "Place: " + place.getName());
        name = (String) place.getName();
        latLng = place.getLatLng();
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
            new LatLng(latLng.latitude, latLng.longitude), DEFAULT_ZOOM));

        showRoute();

      }

      @Override
      public void onError(Status status) {
        // TODO: Handle the error.
        Log.i(TAG, "An error occurred: " + status);
      }
    });

    AutocompleteFilter typeFilter = new AutocompleteFilter.Builder()
        .setCountry("IN")
        .build();

    autocompleteFragment.setFilter(typeFilter);

    mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
    // Obtain the SupportMapFragment and get notified when the map is ready to be used.
    SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
        .findFragmentById(R.id.map);
    mapFragment.getMapAsync(this);


  }

  private void showRoute() {
    String url;
    LatLng destination = new LatLng(latLng.latitude, latLng.longitude);

    Object dataTransfer[] = new Object[3];
    url = getDirectionUrl();
    GetDirectionsData getDirectionsData = new GetDirectionsData();
    dataTransfer[0] = mMap;
    dataTransfer[1] = url;
    dataTransfer[2] = destination;
    getDirectionsData.execute(dataTransfer);

  }

  private String getDirectionUrl() {
    StringBuilder googleDirectionsUrl = new StringBuilder(
        "https://maps.googleapis.com/maps/api/directions/json?");
    googleDirectionsUrl.append(
        "origin=" + mLastKnownLocation.getLatitude() + "," + mLastKnownLocation.getLongitude());
    googleDirectionsUrl.append("&destination=" + latLng.latitude + "," + latLng.longitude);
    googleDirectionsUrl.append("&key=" + "your key");
    return googleDirectionsUrl.toString();

  }

  /*private void zoomAndMarker(){
    CameraUpdate zoom = CameraUpdateFactory.zoomTo(15f);
    mMap.animateCamera(zoom, 1500, new CancelableCallback() {
      @Override
      public void onFinish() {

      }

      @Override
      public void onCancel() {

      }
    });
    if(mMap == null){
      return;
    }
    if(marker != null){
      marker.setPosition(latLng);
    }else{
      MarkerOptions options = new MarkerOptions()
          .position(latLng)
          .title(name.toString());

      options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
      marker = mMap.addMarker(options);
    }

  }*/

  /**
   * Manipulates the map once available.
   * This callback is triggered when the map is ready to be used.
   * This is where we can add markers or lines, add listeners or move the camera. In this case,
   * we just add a marker near Sydney, Australia.
   * If Google Play services is not installed on the device, the user will be prompted to install
   * it inside the SupportMapFragment. This method will only be triggered once the user has
   * installed Google Play services and returned to the app.
   */

  //autocomplete while search
  private void getLocationPermission() {
    if (ContextCompat
        .checkSelfPermission(this.getApplicationContext(), permission.ACCESS_FINE_LOCATION)
        == PackageManager.PERMISSION_GRANTED) {
      mLocationPermissionGranted = true;
    } else {
      ActivityCompat.requestPermissions(this,
          new String[]{permission.ACCESS_FINE_LOCATION}, PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
    }
  }

  @Override
  public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[],
      @NonNull int[] grantResults) {
    mLocationPermissionGranted = false;
    switch (requestCode) {
      case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
          mLocationPermissionGranted = true;
        }
      }
    }
    updateLocationUI();
  }

  @Override
  public void onMapReady(GoogleMap googleMap) {
    mMap = googleMap;

    getLocationPermission();
    updateLocationUI();
    getDeviceLocation();

  }

  private void updateLocationUI() {
    if (mMap == null) {
      return;
    }
    try {

      if (mLocationPermissionGranted) {
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        mMap.animateCamera(CameraUpdateFactory.zoomTo(35));
      } else {
        mMap.setMyLocationEnabled(false);
        mMap.getUiSettings().setMyLocationButtonEnabled(false);
        mLastKnownLocation = null;
        getLocationPermission();
      }
    } catch (SecurityException e) {
      Log.e("Exception : %s", e.getMessage());
    }
  }

  private void getDeviceLocation() {

    try {
      if (mLocationPermissionGranted) {
        Task locationResult = mFusedLocationProviderClient.getLastLocation();
        locationResult.addOnCompleteListener(this, new OnCompleteListener() {
          @Override
          public void onComplete(@NonNull Task task) {
            if (task.isSuccessful()) {
              mLastKnownLocation = (Location) task.getResult();
              mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                  new LatLng(mLastKnownLocation.getLatitude(),
                      mLastKnownLocation.getLongitude()), DEFAULT_ZOOM));
            }
          }
        });
      }
    } catch (SecurityException e) {
      Log.e("Exception : %s", e.getMessage());
    }

  }

  @Override
  protected void onSaveInstanceState(Bundle outState) {
    if (mMap != null) {
      outState.putParcelable(KEY_CAMERA_POSITION, mMap.getCameraPosition());
      outState.putParcelable(KEY_LOCATION, mLastKnownLocation);
      super.onSaveInstanceState(outState);
    }
  }

}
