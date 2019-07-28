package com.funradar.entity;

import com.google.auto.value.AutoValue;
import java.util.HashMap;
import java.util.Map;

/**
 * Contains User related information.
 */
@AutoValue
public abstract class User {

  public abstract String userId();

  public abstract String email();

  public abstract String name();

  public abstract String firstName();

  public abstract String lastName();

  public abstract String pictureUrl();

  public static Builder builder() {
    return new AutoValue_User.Builder();
  }

  public Map<String, Object> toMap() {
    Map<String, Object> user = new HashMap<>();
    user.put("userId", userId());
    user.put("email", email());
    user.put("name", name());
    user.put("firstName", firstName());
    user.put("lastName", lastName());
    user.put("pictureUrl", pictureUrl());
    return user;
  }

  public static User fromMap(Map<String, Object> map) {
    return builder().setUserId((String) map.get("userId")).setEmail((String) map.get("email"))
        .setName((String) map.get("name")).setFirstName((String) map.get("firstName"))
        .setLastName((String) map.get("lastName")).setPictureUrl((String) map.get("pictureUrl"))
        .build();
  }

  @AutoValue.Builder
  public abstract static class Builder {

    public abstract Builder setUserId(String value);

    public abstract Builder setEmail(String value);

    public abstract Builder setName(String value);

    public abstract Builder setFirstName(String value);

    public abstract Builder setLastName(String value);

    public abstract Builder setPictureUrl(String value);

    public abstract User build();
  }

}
