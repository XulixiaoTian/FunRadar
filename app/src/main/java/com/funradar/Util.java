package com.funradar;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.text.format.DateFormat;
import android.util.Log;
import com.funradar.entity.User;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class Util {

  private static final String DATE_FORMAT = "MM/dd/yyyy hh:mm";
  public static User user = null;

  public static Bitmap getBitmapFromURL(String imageUrl) {
    Bitmap bitmap = null;

    try {
      URL url = new URL(imageUrl);
      HttpURLConnection connection = (HttpURLConnection) url.openConnection();
      connection.setDoInput(true);
      connection.connect();
      InputStream input = connection.getInputStream();
      bitmap = BitmapFactory.decodeStream(input);
    } catch (IOException e) {
      e.printStackTrace();
      Log.e("Error: ", e.getMessage());
    }

    return bitmap;
  }

  public static String toDate(long mili) {
    return String.valueOf(DateFormat.format(DATE_FORMAT, mili));
  }


  public static int distanceBetweenTwoLocations(double currentLatitude, double currentLongitude,
      double destLatitude, double destLongitude) {
    Location currentLocation = new Location("CurrentLocation");
    currentLocation.setLatitude(currentLatitude);
    currentLocation.setLongitude(currentLongitude);
    Location destLocation = new Location("DestLocation");
    destLocation.setLatitude(destLatitude);
    destLocation.setLongitude(destLongitude);
    double distance = currentLocation.distanceTo(destLocation); // return meters

    return (int) ((39.370078 * distance) / 63360.0);
  }
}
