package com.funradar;

import static com.funradar.DBUtils.incrementCommentLike;
import static com.funradar.Util.getBitmapFromURL;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.funradar.CommentAdapter.ViewHolder;
import com.funradar.entity.Comment;
import java.util.List;

public class CommentAdapter extends
    RecyclerView.Adapter<ViewHolder> {

  private static final String DATE_FORMAT = "dd/MM/yyyy hh:mm";

  private List<Comment> comments;

  public CommentAdapter(List<Comment> comments) {
    this.comments = comments;
  }

  public void setComments(List<Comment> comments) {
    this.comments = comments;
  }

  @Override
  public CommentAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View view = LayoutInflater.from(parent.getContext())
        .inflate(R.layout.comment_item, parent, false);
    return new CommentAdapter.ViewHolder(view);
  }

  @SuppressLint("StaticFieldLeak")
  @Override
  public void onBindViewHolder(final CommentAdapter.ViewHolder holder, int position) {
    //holder.mEvent = comments.get(position);

    final Comment comment = this.comments.get(position);
    System.out.println("Setting up comment " + comment);
    holder.commentDescription.setText(comment.comment());
    holder.commentUser.setText(comment.commentUser());
    holder.commentTime.setText(DateFormat.format(DATE_FORMAT, comment.commentTime()));
    holder.likeNumber.setText(String.valueOf(comment.likes()));
    if (!Util.user.pictureUrl().isEmpty()) {
      new AsyncTask<Void, Void, Bitmap>() {
        @Override
        protected Bitmap doInBackground(Void... params) {
          return getBitmapFromURL(Util.user.pictureUrl());
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
          holder.userImage.setImageBitmap(bitmap);
        }
      }.execute();
    }

    // listen to "like" button
    holder.likeButton.setOnClickListener(view -> {
      // update comment like number
      incrementCommentLike(comment.commentId())
          .ifPresent(like -> holder.likeNumber.setText(String.valueOf(like)));
    });
  }

  @Override
  public int getItemCount() {
    return comments.size();
  }

  public class ViewHolder extends RecyclerView.ViewHolder {

    public TextView commentUser;
    public TextView commentDescription;
    public TextView commentTime;
    public ImageView likeButton;
    public ImageView userImage;
    public TextView likeNumber;
    public View commentView;

    public ViewHolder(View v) {
      super(v);
      commentView = v;
      commentUser = v.findViewById(R.id.comment_item_user);
      commentDescription = v.findViewById(R.id.comment_item_description);
      commentTime = v.findViewById(R.id.comment_item_time);
      likeButton = v.findViewById(R.id.comment_like_button);
      likeNumber = v.findViewById(R.id.comment_like_number);
      userImage = v.findViewById(R.id.comment_item_view_user_image);
    }
  }
}
