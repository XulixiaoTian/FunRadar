package com.funradar;

import static com.funradar.DBUtils.getAllCloseEvents;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.funradar.entity.Event;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} implementing Map View.
 */
public class EventMapFragment extends Fragment implements OnMapReadyCallback,
    GoogleMap.OnInfoWindowClickListener, GoogleMap.OnMarkerClickListener {

  private MapView mMapView;

  private List<Event> events;
  private LocationService locationService;
  private GoogleMap mGoogleMap;
  private Marker lastClicked;

  public EventMapFragment() {
    // Required empty public constructor
  }


  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    events = new ArrayList<>();
    locationService = new LocationService();
    // Inflate the layout for this fragment
    return inflater.inflate(R.layout.fragment_event_map, container, false);
  }

  @Override
  public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    mMapView = view.findViewById(R.id.event_map_view);
    if (mMapView != null) {
      mMapView.onCreate(null);
      mMapView.onResume();
      mMapView.getMapAsync(this);
    }
  }

  @Override
  public void onMapReady(GoogleMap googleMap) {
    MapsInitializer.initialize(getContext());
    mGoogleMap = googleMap;
    mGoogleMap.setOnInfoWindowClickListener(this);
    // load event image when clicking on marker
    mGoogleMap.setOnMarkerClickListener(this);
    Location location = locationService.getLocation(getActivity()).get();
    double curLatitude = location.getLatitude();
    double curLongitude = location.getLongitude();
    CameraPosition cameraPosition = new CameraPosition.Builder()
        .target(new LatLng(curLatitude, curLongitude)).zoom(12).build();
    googleMap.animateCamera(
        CameraUpdateFactory.newCameraPosition(cameraPosition));

    // 1: mark current location
    // create marker
    MarkerOptions marker = new MarkerOptions().position(new LatLng(curLatitude, curLongitude))
        .title("Your location");
    // Changing marker icon
    marker.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
    // adding marker
    googleMap.addMarker(marker);
    // 2: mark events nearby
    setUpMarkersCloseToCurLocation(googleMap);
  }

  // Go through data from database, and find out events that less or equal to 10 miles away from current location
  private void setUpMarkersCloseToCurLocation(final GoogleMap googleMap) {
    events = getAllCloseEvents(getActivity());
    // Set up every events with marker
    for (Event event : events) {
      // create marker
      MarkerOptions marker = new MarkerOptions()
          .position(new LatLng(event.latitude(), event.longitude())).title(event.title());
      // Changing marker icon
      marker.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
      // adding marker to event
      Marker mker = googleMap.addMarker(marker);
      mker.setTag(event);
    }
  }

  // When user click on title of marker, then pops out to corresponding CommentActivity
  @Override
  public void onInfoWindowClick(Marker marker) {
    Event event = (Event) marker.getTag();
    Intent intent = new Intent(getContext(), CommentActivity.class);
    String eventId = event.eventId();
    intent.putExtra("eventId", eventId);
    getContext().startActivity(intent);
  }

  // First click marker -> load event image, second click marker -> hide information
  @SuppressLint("StaticFieldLeak")
  @Override
  public boolean onMarkerClick(final Marker marker) {
    final Event event = (Event) marker.getTag();
    if (lastClicked != null && lastClicked.equals(marker)) {
      lastClicked = null;
      marker.hideInfoWindow();
      marker.setIcon(null);
      return true;
    } else {
      lastClicked = marker;
      final String imgUrl = event.pictureUrl();
      if (imgUrl != null && !imgUrl.isEmpty()) {
        new AsyncTask<Void, Void, Bitmap>() {
          @Override
          protected Bitmap doInBackground(Void... voids) {
            return Util.getBitmapFromURL(imgUrl);
          }

          @Override
          protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            marker.setIcon(BitmapDescriptorFactory.fromBitmap(bitmap));
            marker.setTitle(event.title());
          }
        }.execute();
      }
      return false;
    }
  }

  @Override
  public void onResume() {
    super.onResume();
    mMapView.onResume();
  }

  @Override
  public void onPause() {
    super.onPause();
    mMapView.onPause();
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    mMapView.onDestroy();
  }

  @Override
  public void onLowMemory() {
    super.onLowMemory();
    mMapView.onLowMemory();
  }
}
