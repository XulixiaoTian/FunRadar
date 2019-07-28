package com.funradar;

import static android.content.Context.LOCATION_SERVICE;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

public class LocationService implements LocationListener {

  private static final int PERMISSIONS_REQUEST_LOCATION = 99;
  private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10;
  private static final long MIN_TIME_BW_UPDATES = 1000 * 60;

  private boolean mIsGPSEnabled = false;
  private boolean mIsNetworkEnabled;

  private LocationManager locationManager;

  public Optional<Location> getLocation(Activity context) {
    try {
      locationManager = (LocationManager) context
          .getSystemService(LOCATION_SERVICE);

      // getting GPS status
      mIsGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

      // getting network status
      mIsNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

      if (!mIsGPSEnabled && !mIsNetworkEnabled) {
        return Optional.empty();
      }

      // First get location from Network Provider
      checkLocationPermission(context);
      if (mIsNetworkEnabled) {
        locationManager
            .requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME_BW_UPDATES,
                MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
        if (locationManager != null) {
          return Optional.of(locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER));
        }
      }
      // if GPS Enabled get lat/long using GPS Services
      if (mIsGPSEnabled) {
        locationManager
            .requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME_BW_UPDATES,
                MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
        Log.d("GPS Enabled", "GPS Enabled");
        if (locationManager != null) {
          return Optional.of(locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER));
        }
      }

    } catch (Exception e) {
      e.printStackTrace();
    }

    return Optional.empty();
  }

  // runtime permission check
  public boolean checkLocationPermission(Activity context) {
    if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION)
        != PackageManager.PERMISSION_GRANTED) {
      ActivityCompat.requestPermissions(context,
          new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
          PERMISSIONS_REQUEST_LOCATION);
    }
    return true;
  }

  public Optional<String> getAddress(Context context, double lat, double lon) {
    Geocoder geocoder = new Geocoder(context, Locale.ENGLISH);

    try {
      List<Address> addresses = geocoder.getFromLocation(lat, lon, 1);

      if (addresses != null && addresses.size() > 0) {
        StringBuilder sb = new StringBuilder();
        Address address = addresses.get(0);
        for (int i = 0; i < address.getMaxAddressLineIndex(); i++) {
          sb.append(address.getAddressLine(i)).append(" ");
        }
        return Optional.of(sb.toString());
      }

    } catch (IOException e) {
      e.printStackTrace();
    }
    return Optional.empty();
  }

  @Override
  public void onLocationChanged(Location location) {

  }

  @Override
  public void onStatusChanged(String s, int i, Bundle bundle) {

  }

  @Override
  public void onProviderEnabled(String s) {

  }

  @Override
  public void onProviderDisabled(String s) {

  }
}
