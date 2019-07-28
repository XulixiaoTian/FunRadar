package com.funradar.entity;

import com.google.auto.value.AutoValue;
import java.util.HashMap;
import java.util.Map;

/**
 * Contains Event related information.
 */
@AutoValue
public abstract class Event {

  public abstract String eventId();

  public abstract String title();

  public abstract String description();

  public abstract String address();

  public abstract String hostUserId();

  public abstract String hostName();

  public abstract String pictureUrl();

  public abstract long eventTime();

  public abstract long likes();

  public abstract long comments();

  public abstract double latitude();

  public abstract double longitude();

  public static Builder builder() {
    return new AutoValue_Event.Builder();
  }

  public Map<String, Object> toMap() {
    Map<String, Object> event = new HashMap<>();
    event.put("eventId", eventId());
    event.put("title", title());
    event.put("description", description());
    event.put("address", address());
    event.put("hostUserId", hostUserId());
    event.put("hostName", hostName());
    event.put("pictureUrl", pictureUrl());
    event.put("eventTime", eventTime());
    event.put("likes", likes());
    event.put("comments", comments());
    event.put("latitude", latitude());
    event.put("longitude", longitude());
    return event;
  }

  public static Event fromMap(Map<String, Object> map) {
    return builder().setEventId((String) map.get("eventId")).setTitle((String) map.get("title"))
        .setDescription((String) map.get("description")).setAddress((String) map.get("address"))
        .setHostUserId((String) map.get("hostUserId"))
        .setHostName((String) map.get("hostName")).setPictureUrl((String) map.get("pictureUrl"))
        .setPictureUrl((String) map.get("pictureUrl"))
        .setEventTime((long) map.get("eventTime"))
        .setLikes((long) map.get("likes"))
        .setComments((long) map.get("comments"))
        .setLatitude((double) map.get("latitude"))
        .setLongitude((double) map.get("longitude"))
        .build();
  }

  @AutoValue.Builder
  public abstract static class Builder {

    public abstract Builder setEventId(String value);

    public abstract Builder setTitle(String value);

    public abstract Builder setDescription(String value);

    public abstract Builder setAddress(String value);

    public abstract Builder setHostUserId(String value);

    public abstract Builder setHostName(String value);

    public abstract Builder setPictureUrl(String value);

    public abstract Builder setEventTime(long value);

    public abstract Builder setLikes(long value);

    public abstract Builder setComments(long value);

    public abstract Builder setLatitude(double value);

    public abstract Builder setLongitude(double value);

    public abstract Event build();
  }

}
