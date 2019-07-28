package com.funradar;

import static com.funradar.DBUtils.getAllCloseEvents;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * A fragment representing a list of Items.
 */
public class EventFragment extends Fragment {

  /**
   * Mandatory empty constructor for the fragment manager to instantiate the fragment (e.g. upon
   * screen orientation changes).
   */
  public EventFragment() {
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_event_list, container, false);

    setupAddEventButton(view);

    setupEventRecyclerView(view);

    return view;
  }

  private void setupEventRecyclerView(View view) {
    RecyclerView recyclerView = view.findViewById(R.id.event_recycler_view);
    recyclerView.setHasFixedSize(true);
    recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    recyclerView.setAdapter(
        new EventRecyclerViewAdapter(getAllCloseEvents(getActivity()), getActivity()));
  }

  private void setupAddEventButton(View view) {
    view.findViewById(R.id.add_event_button).setOnClickListener(view1 -> {
      Intent eventReportIntent = new Intent(getActivity(), AddEventActivity.class);
      startActivity(eventReportIntent);
    });
  }

}
