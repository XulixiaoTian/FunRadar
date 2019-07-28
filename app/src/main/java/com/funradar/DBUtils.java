package com.funradar;

import static android.content.ContentValues.TAG;
import static com.funradar.Util.distanceBetweenTwoLocations;

import android.app.Activity;
import android.location.Location;
import android.net.Uri;
import android.util.Log;
import com.funradar.entity.Comment;
import com.funradar.entity.Event;
import com.funradar.entity.User;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class DBUtils {

  // Event
  public static void insertEvent(Event event) {
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    db.collection("events")
        .add(event.toMap())
        .addOnSuccessListener(
            documentReference -> Log.d(TAG, "Event added with ID: " + documentReference.getId()))
        .addOnFailureListener(e -> Log.w(TAG, "Error adding event", e));
  }

  public static Optional<Event> getEvent(String eventId) {
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    Task<QuerySnapshot> task = db.collection("events").whereEqualTo("eventId", eventId)
        .get();
    while (!task.isComplete()) {
      // wait for task completion
    }
    QuerySnapshot snapshot = task.getResult();
    if (snapshot != null && !snapshot.getDocuments().isEmpty()) {
      return Optional.of(Event.fromMap(snapshot.getDocuments().get(0).getData()));
    }
    return Optional.empty();
  }

  public static List<Event> getAllCloseEvents(Activity context) {
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    LocationService locationService = new LocationService();
    Location location = locationService.getLocation(context).get();
    Task<QuerySnapshot> task = db.collection("events")
        .get();
    while (!task.isComplete()) {
      // wait for task completion
    }

    QuerySnapshot snapshot = task.getResult();
    if (snapshot != null && !snapshot.getDocuments().isEmpty()) {
      return snapshot.getDocuments().stream().map(doc -> Event.fromMap(doc.getData()))
          .filter(event ->
              distanceBetweenTwoLocations(location.getLatitude(), location.getLongitude(),
                  event.latitude(), event.longitude()) < 100)
          .sorted((e1, e2) -> Math.toIntExact((e1.eventTime() - e2.eventTime())))
          .collect(Collectors.toList());

    }
    return new ArrayList<>();
  }

  public static Optional<Long> incrementEventLike(String eventId) {
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    Task<QuerySnapshot> task = db.collection("events").whereEqualTo("eventId", eventId)
        .get();
    while (!task.isComplete()) {
      // wait for task completion
    }

    QuerySnapshot snapshot = task.getResult();
    if (snapshot != null && !snapshot.getDocuments().isEmpty()) {
      long likes = (long) snapshot.getDocuments().get(0).get("likes") + 1;
      snapshot.getDocuments().get(0).getReference().update("likes", likes);
      return Optional.of(likes);
    }
    return Optional.empty();
  }

  public static Optional<Long> incrementEventComment(String eventId) {
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    Task<QuerySnapshot> task = db.collection("events").whereEqualTo("eventId", eventId)
        .get();
    while (!task.isComplete()) {
      // wait for task completion
    }

    QuerySnapshot snapshot = task.getResult();
    if (snapshot != null && !snapshot.getDocuments().isEmpty()) {
      long likes = (long) snapshot.getDocuments().get(0).get("comments") + 1;
      snapshot.getDocuments().get(0).getReference().update("comments", likes);
      return Optional.of(likes);
    }
    return Optional.empty();
  }

  public static void upsertEvent(Event event) {
    if (!getEvent(event.eventId()).isPresent()) {
      insertEvent(event);
    }
  }

  // User
  public static void insertUser(User user) {
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    db.collection("users")
        .add(user.toMap())
        .addOnSuccessListener(
            documentReference -> Log.d(TAG, "User added with ID: " + documentReference.getId()))
        .addOnFailureListener(e -> Log.w(TAG, "Error adding user", e));
  }

  public static Optional<User> getUser(String userId) {
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    Task<QuerySnapshot> task = db.collection("users").whereEqualTo("userId", userId)
        .get();
    while (!task.isComplete()) {
      // wait for task completion
    }

    QuerySnapshot snapshot = task.getResult();
    if (snapshot != null && !snapshot.getDocuments().isEmpty()) {
      return Optional.of(User.fromMap(snapshot.getDocuments().get(0).getData()));
    }
    return Optional.empty();
  }

  public static void upsertUser(User user) {
    if (!getUser(user.userId()).isPresent()) {
      insertUser(user);
    }
  }

  public static void uploadImage(final String eventId, final Uri imageUri) {
    if (imageUri == null) {
      return;
    }
    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageRef = storage.getReference();

    StorageReference imageRef = storageRef.child("images/" + imageUri.getLastPathSegment() + "_"
        + System.currentTimeMillis());
    UploadTask uploadTask = imageRef.putFile(imageUri);

    // Register observers to listen for when the download is done or if it fails
    uploadTask.addOnSuccessListener(taskSnapshot -> {
      @SuppressWarnings("VisibleForTests")
      Uri downloadUrl = taskSnapshot.getUploadSessionUri();
      Log.i(TAG, "upload successfully" + eventId);

      updateEventUrl(downloadUrl, eventId);
    });
  }

  public static void updateEventUrl(Uri downloadUrl, String eventId) {
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    Task<QuerySnapshot> task = db.collection("events").whereEqualTo("eventId", eventId)
        .get();
    while (!task.isComplete()) {
      // wait for task completion
    }

    QuerySnapshot snapshot = task.getResult();
    if (snapshot != null && !snapshot.getDocuments().isEmpty()) {
      snapshot.getDocuments().get(0).getReference().update("pictureUrl", downloadUrl);
    }
  }

  // Comment
  public static void insertComment(Comment comment) {
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    db.collection("comments")
        .add(comment.toMap())
        .addOnSuccessListener(
            documentReference -> Log.d(TAG, "User comment with ID: " + documentReference.getId()))
        .addOnFailureListener(e -> Log.w(TAG, "Error adding comment", e));
  }

  public static List<Comment> getAllComments(String eventId) {
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    Task<QuerySnapshot> task = db.collection("comments").whereEqualTo("eventId", eventId)
        .get();
    while (!task.isComplete()) {
      // wait for task completion
    }

    QuerySnapshot snapshot = task.getResult();
    if (snapshot != null && !snapshot.getDocuments().isEmpty()) {
      return snapshot.getDocuments().stream().map(doc -> Comment.fromMap(doc.getData()))
          .sorted((e1, e2) -> Math.toIntExact((e1.commentTime() - e2.commentTime())))
          .collect(Collectors.toList());

    }
    return new ArrayList<>();
  }

  public static Optional<Comment> getComment(String commentId) {
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    Task<QuerySnapshot> task = db.collection("comments").whereEqualTo("commentId", commentId)
        .get();
    while (!task.isComplete()) {
      // wait for task completion
    }

    QuerySnapshot snapshot = task.getResult();
    if (snapshot != null && !snapshot.getDocuments().isEmpty()) {
      return Optional.of(Comment.fromMap(snapshot.getDocuments().get(0).getData()));
    }
    return Optional.empty();
  }

  public static void upsertComment(Comment comment) {
    if (!getUser(comment.commentId()).isPresent()) {
      insertComment(comment);
    }
  }

  public static Optional<Long> incrementCommentLike(String commentId) {
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    Task<QuerySnapshot> task = db.collection("comments").whereEqualTo("commentId", commentId)
        .get();
    while (!task.isComplete()) {
      // wait for task completion
    }

    QuerySnapshot snapshot = task.getResult();
    if (snapshot != null && !snapshot.getDocuments().isEmpty()) {
      long likes = (long) snapshot.getDocuments().get(0).get("likes") + 1;
      snapshot.getDocuments().get(0).getReference().update("likes", likes);
      return Optional.of(likes);
    }
    return Optional.empty();
  }

}
