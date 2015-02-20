package com.mobilemakers.remindmetv;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.j256.ormlite.android.apptools.OpenHelperManager;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * A placeholder fragment containing a simple view.
 */
public class EventsListFragment extends ListFragment {

    DatabaseHelper mDatabaseHelper = null;
    ArrayAdapter<String> mAdapter;

    public EventsListFragment() {
    }

    public DatabaseHelper getDatabaseHelper() {
        if (mDatabaseHelper == null){
            mDatabaseHelper = OpenHelperManager.getHelper(getActivity(), DatabaseHelper.class);
        }
        return mDatabaseHelper;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_events_list, container, false);
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        prepareListView();
    }

    private void prepareListView() {
        List<String> titles = new ArrayList<>();
        titles = getRowsFromDatabase(titles);
        mAdapter = new ArrayAdapter<>(getActivity(), R.layout.list_item_event, R.id.text_view_event_title, titles);
        setListAdapter(mAdapter);
    }

    private List<String> getRowsFromDatabase(List<String> titles) {
        try {
            List<Event> entries;
            entries = getDatabaseHelper().getEventDao().queryForAll();

            for (int i = 0; i<entries.size(); i++){
                titles.add(entries.get(i).getTitle());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return titles;
    }
}
