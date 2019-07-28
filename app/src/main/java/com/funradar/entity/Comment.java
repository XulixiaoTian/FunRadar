package com.funradar.entity;

import com.google.auto.value.AutoValue;
import java.util.HashMap;
import java.util.Map;

@AutoValue
public abstract class Comment {

  public abstract String commentId();

  public abstract String eventId();

  public abstract String comment();

  public abstract String commentUser();

  public abstract String commentOwnerId();

  public abstract long commentTime();

  public abstract long likes();

  public static Builder builder() {
    return new AutoValue_Comment.Builder();
  }

  public Map<String, Object> toMap() {
    Map<String, Object> user = new HashMap<>();
    user.put("commentId", commentId());
    user.put("eventId", eventId());
    user.put("comment", comment());
    user.put("commentUser", commentUser());
    user.put("commentOwnerId", commentOwnerId());
    user.put("commentTime", commentTime());
    user.put("likes", likes());
    return user;
  }

  public static Comment fromMap(Map<String, Object> map) {
    return builder().setCommentId((String) map.get("commentId"))
        .setEventId((String) map.get("eventId"))
        .setComment((String) map.get("comment"))
        .setCommentUser((String) map.get("commentUser"))
        .setCommentOwnerId((String) map.get("commentOwnerId"))
        .setCommentTime((long) map.get("commentTime"))
        .setLikes((long) map.get("likes"))
        .build();
  }

  @AutoValue.Builder
  public abstract static class Builder {

    public abstract Builder setCommentId(String value);

    public abstract Builder setEventId(String value);

    public abstract Builder setComment(String value);

    public abstract Builder setCommentUser(String value);

    public abstract Builder setCommentOwnerId(String value);

    public abstract Builder setCommentTime(long value);

    public abstract Builder setLikes(long value);

    public abstract Comment build();
  }
}
