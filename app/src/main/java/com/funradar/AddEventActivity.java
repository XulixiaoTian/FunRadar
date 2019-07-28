package com.funradar;

import static com.funradar.DBUtils.insertEvent;
import static com.funradar.DBUtils.uploadImage;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;
import com.funradar.entity.Event;
import java.util.Optional;
import java.util.Random;

public class AddEventActivity extends AppCompatActivity {

  private static int RESULT_LOAD_IMAGE = 1; // gallery

  private LocationService locationService;
  private Optional<Location> location;

  private EditText mEditTextLocation;
  private EditText mEditTextTitle;
  private EditText mEditTextContent;
  private ImageView sendEventButton;
  private ImageView addImageButton;
  private ImageView eventLocationButton;
  private ImageView eventImage;


  private Uri localImageUri;


  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_add_event);

    // initialization
    locationService = new LocationService();
    location = locationService.getLocation(this);

    mEditTextLocation = findViewById(R.id.edit_text_event_location);
    mEditTextTitle = findViewById(R.id.edit_text_event_title);
    mEditTextContent = findViewById(R.id.edit_text_event_description);
    addImageButton = findViewById(R.id.img_event_camera);
    sendEventButton = findViewById(R.id.send_event);
    eventLocationButton = findViewById(R.id.img_event_location);

    eventImage = findViewById(R.id.img_event_picture_capture);

    setupSendEventButton();
    setupAddImageButton();
    setupLocationButton();

  }

  @SuppressLint("StaticFieldLeak")
  private void setupLocationButton() {
    // location image click
    eventLocationButton.setOnClickListener(v -> {
      new AsyncTask<Void, Void, Optional<String>>() {
        @Override
        protected Optional<String> doInBackground(Void... inputs) {
          if (location.isPresent()) {
            return locationService
                .getAddress(AddEventActivity.this, location.get().getLatitude(),
                    location.get().getLongitude());
          }
          return Optional.empty();
        }

        @Override
        protected void onPostExecute(Optional<String> result) {
          result.ifPresent(s -> mEditTextLocation.setText(s));
        }
      }.execute();
    });
  }

  private void setupAddImageButton() {
    addImageButton.setOnClickListener(view -> {
      Intent intent = new Intent(
          Intent.ACTION_PICK,
          android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

      startActivityForResult(intent, RESULT_LOAD_IMAGE);
    });
  }

  private void setupSendEventButton() {
    // send event button click
    sendEventButton.setOnClickListener(v -> {
      String eventId = sendEvent();
      if (localImageUri != null) {
        uploadImage(eventId, localImageUri);
        localImageUri = null;
        eventImage.setVisibility(View.GONE);
      }
      startActivity(new Intent(AddEventActivity.this, EventsActivity.class));
    });
  }

  @Override
  public void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    try {
      if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
        Uri selectedImage = data.getData();
        eventImage.setVisibility(View.VISIBLE);
        eventImage.setImageURI(selectedImage);
        localImageUri = selectedImage;
      }
    } catch (Exception ex) {
      ex.printStackTrace();
    }
  }

  private String sendEvent() {
    String title = mEditTextTitle.getText().toString();
    String location = mEditTextLocation.getText().toString();
    String description = mEditTextContent.getText().toString();
    if (location.equals("") || description.equals("") ||
        title.equals("") || Util.user == null || !this.location.isPresent()) {
      return null;
    }
    Random rand = new Random();

    Event event = Event.builder()
        .setTitle(title)
        .setDescription(description)
        .setAddress(location)
        .setEventTime(System.currentTimeMillis())
        .setLatitude(this.location.get().getLatitude())
        .setLongitude(this.location.get().getLongitude())
        .setHostUserId(Util.user.userId())
        .setHostName(Util.user.name())
        .setEventId(String.valueOf(rand.nextLong()))
        .setComments(0)
        .setLikes(0)
        .setPictureUrl("") // Empty picture url here
        .build();

    insertEvent(event);
    return event.eventId();
  }
}
