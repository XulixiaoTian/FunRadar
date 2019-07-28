package com.funradar;

import static com.funradar.DBUtils.getAllComments;
import static com.funradar.DBUtils.getEvent;
import static com.funradar.DBUtils.incrementEventComment;
import static com.funradar.DBUtils.incrementEventLike;
import static com.funradar.DBUtils.upsertComment;
import static com.funradar.Util.getBitmapFromURL;
import static com.funradar.Util.toDate;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.funradar.entity.Comment;
import com.funradar.entity.Event;
import java.util.Random;

public class CommentActivity extends AppCompatActivity {

  private EditText editTextComment;
  private CommentAdapter commentAdapter;

  @SuppressLint("StaticFieldLeak")
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_comment);

    Intent intent = getIntent(); // start by EventListAdapter
    final String eventId = intent.getStringExtra("eventId");
    Event event = getEvent(eventId).get();

    // Setup comment adapter
    commentAdapter = new CommentAdapter(getAllComments(eventId));

    RecyclerView recyclerView = findViewById(R.id.comment_recycler_view);
    recyclerView.setHasFixedSize(true);
    recyclerView.setLayoutManager(new LinearLayoutManager(this));
    recyclerView.setAdapter(commentAdapter);

    // Setup buttons
    editTextComment = findViewById(R.id.comment_edit_text);
    findViewById(R.id.comment_submit).setOnClickListener(v -> {
      sendComment(eventId);
      incrementEventComment(eventId);

      // Refresh view
      commentAdapter.setComments(getAllComments(eventId));
      commentAdapter.notifyDataSetChanged();
      editTextComment.setText("");
    });

    // Setup event/user info
    ((TextView) findViewById(R.id.comment_view_user_name)).setText(Util.user.name());
    ((TextView) findViewById(R.id.comment_view_event_title)).setText(event.title());
    ((TextView) findViewById(R.id.comment_view_event_description)).setText(event.description());
    ((TextView) findViewById(R.id.comment_view_user_location)).setText(event.address());
    ((TextView) findViewById(R.id.comment_view_event_time)).setText(toDate(event.eventTime()));
    ((TextView) findViewById(R.id.comment_view_event_likes)).setText(String.valueOf(event.likes()));
    ((TextView) findViewById(R.id.comment_view_comment_number))
        .setText(String.valueOf(event.comments()));
    if (!event.pictureUrl().isEmpty()) {
      new AsyncTask<Void, Void, Bitmap>() {
        @Override
        protected Bitmap doInBackground(Void... params) {
          return getBitmapFromURL(event.pictureUrl());
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
          ((ImageView) findViewById(R.id.comment_view_event_image))
              .setImageBitmap(bitmap);
        }
      }.execute();

    }

    if (!Util.user.pictureUrl().isEmpty()) {
      new AsyncTask<Void, Void, Bitmap>() {
        @Override
        protected Bitmap doInBackground(Void... params) {
          return getBitmapFromURL(Util.user.pictureUrl());
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
          ((ImageView) findViewById(R.id.comment_view_user_image))
              .setImageBitmap(bitmap);
        }
      }.execute();
    }

    // setup event like button
    findViewById(R.id.comment_view_event_like_button).setOnClickListener(v -> {
      incrementEventLike(eventId);
      ((TextView) findViewById(R.id.comment_view_event_likes))
          .setText(String.valueOf(getEvent(eventId).get().likes()));
    });
  }

  private void sendComment(final String eventId) {
    String comment = editTextComment.getText().toString();
    if (comment.equals("")) {
      Toast toast = Toast
          .makeText(getApplicationContext(), "Your comment is blank.", Toast.LENGTH_SHORT);
      toast.show();
      return;
    }
    upsertComment(Comment.builder().setComment(comment).setCommentOwnerId(Util.user.userId())
        .setCommentUser(Util.user.name()).setEventId(eventId).setLikes(0)
        .setCommentTime(System.currentTimeMillis())
        .setCommentId(String.valueOf(new Random().nextLong())).build());
  }
}
