package com.funradar;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class EventsActivity extends AppCompatActivity {

  private Fragment mEventsFragment;
  private Fragment mEventMapFragment;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_events);

    AdView mAdView = findViewById(R.id.adView_event);
    AdRequest adRequest = new AdRequest.Builder().build();
    mAdView.loadAd(adRequest);

    if (mEventsFragment == null) {
      mEventsFragment = new EventFragment();
    }

    getSupportFragmentManager().beginTransaction().add(R.id.relativelayout_event, mEventsFragment)
        .commit();

    BottomNavigationView navView = findViewById(R.id.nav_view);
    navView.setOnNavigationItemSelectedListener(item -> {
      switch (item.getItemId()) {
        case R.id.navigation_events:
          item.setChecked(true);
          getSupportFragmentManager().beginTransaction()
              .replace(R.id.relativelayout_event, mEventsFragment).commit();
          return true;
        case R.id.navigation_map:
          if (mEventMapFragment == null) {
            mEventMapFragment = new EventMapFragment();
          }
          item.setChecked(true);
          getSupportFragmentManager().beginTransaction()
              .replace(R.id.relativelayout_event, mEventMapFragment).commit();
          return true;
      }
      return false;
    });
  }

}
