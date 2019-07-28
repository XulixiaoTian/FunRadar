package com.funradar;

import static com.funradar.DBUtils.incrementEventLike;
import static com.funradar.Util.getBitmapFromURL;
import static com.funradar.Util.toDate;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.funradar.entity.Event;
import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link Event}.
 */
public class EventRecyclerViewAdapter extends
    RecyclerView.Adapter<EventRecyclerViewAdapter.ViewHolder> {

  private final List<Event> events;
  private final Context context;

  public EventRecyclerViewAdapter(List<Event> events,
      final Context context) {
    this.events = events;
    this.context = context;
  }

  @Override
  public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View view = LayoutInflater.from(parent.getContext())
        .inflate(R.layout.fragment_event, parent, false);
    return new ViewHolder(view);
  }

  @SuppressLint("StaticFieldLeak")
  @Override
  public void onBindViewHolder(final ViewHolder holder, int position) {
    holder.mEvent = events.get(position);

    final Event event = events.get(position);
    holder.title.setText(event.title());
    holder.hostName.setText(event.hostName());
    holder.location.setText(event.address());
    holder.description.setText(event.description());
    holder.eventTime.setText(toDate(event.eventTime()));
    holder.likeNumber.setText(String.valueOf(event.likes()));
    holder.commentNumber.setText(String.valueOf(event.comments()));

    if (!event.pictureUrl().isEmpty()) {
      final String url = event.pictureUrl();
      holder.imgview.setVisibility(View.VISIBLE);
      new AsyncTask<Void, Void, Bitmap>() {
        @Override
        protected Bitmap doInBackground(Void... params) {
          return getBitmapFromURL(url);
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
          holder.imgview.setImageBitmap(bitmap);
        }
      }.execute();
    } else {
      holder.imgview.setVisibility(View.GONE);
    }

    // listen to "like" button
    // update data
    holder.likeButton.setOnClickListener(v -> incrementEventLike(event.eventId())
        .ifPresent(like -> holder.likeNumber.setText(String.valueOf(like))));

    // listen to "comment" button
    holder.commentButton.setOnClickListener(view -> {
      Intent intent = new Intent(context,
          CommentActivity.class);
      String eventId = event.eventId();
      intent.putExtra("eventId", eventId);
      context.startActivity(intent);
    });
  }

  @Override
  public int getItemCount() {
    return events.size();
  }

  public class ViewHolder extends RecyclerView.ViewHolder {

    public final View mView;
    public Event mEvent;

    public TextView title;
    public TextView hostName;
    public TextView location;
    public TextView description;
    public TextView eventTime;
    public ImageView imgview;
    public ImageView likeButton;
    public ImageView commentButton;
    public TextView likeNumber;
    public TextView commentNumber;

    public ViewHolder(View view) {
      super(view);
      mView = view;

      title = view.findViewById(R.id.event_item_title);
      hostName = view.findViewById(R.id.event_item_user);
      location = view.findViewById(R.id.event_item_location);
      description = view.findViewById(R.id.event_item_description);
      eventTime = view.findViewById(R.id.event_item_time);
      imgview = view.findViewById(R.id.event_item_img);
      likeButton = view.findViewById(R.id.event_like_button);
      commentButton = view.findViewById(R.id.event_comment_button);
      likeNumber = view.findViewById(R.id.event_like_number);
      commentNumber = view.findViewById(R.id.event_comment_number);
    }
  }
}
