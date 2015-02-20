package com.mobilemakers.remindmetv;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.support.annotation.Nullable;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;

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
        try {
            synchronizeDatabaseAndCalendar();
            prepareListView();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void synchronizeDatabaseAndCalendar() throws SQLException {
        List<Event> databaseEntries = getDatabaseHelper().getEventDao().queryForAll();
        Event event;
        for (int i = 0; i< databaseEntries.size(); i++) {
            event = databaseEntries.get(i);
            if (!eventExistsInCalendar(event.getId())){
                deleteEventInDatabase(event);
            }
        }
    }

    private boolean eventExistsInCalendar(Integer eventID){

        final String[] EVENT_PROJECTION = new String[]{
                CalendarContract.Events._ID,
        };

        ContentResolver contentResolver = getActivity().getContentResolver();
        Cursor cursorEvents;
        Uri eventsUri = CalendarContract.Events.CONTENT_URI;
        //Filter by BEGIN_TIME and DURATION
        String eventSelection = "(" + CalendarContract.Events._ID  + " = " + eventID +")";
        cursorEvents = contentResolver.query(eventsUri, EVENT_PROJECTION, eventSelection, null, null);

        return cursorEvents.moveToNext();
    }

    private void deleteEventInDatabase(Event event) {
        try {
            Dao<Event, Integer> dao = getDatabaseHelper().getEventDao();
            dao.delete(event);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void prepareListView() throws SQLException {
        List<String> titles = new ArrayList<>();
        titles = getRowsFromDatabase(titles);
        mAdapter = new ArrayAdapter<>(getActivity(), R.layout.list_item_event, R.id.text_view_event_title, titles);
        setListAdapter(mAdapter);
    }

    private List<String> getRowsFromDatabase(List<String> titles) throws SQLException {
        List<Event> databaseEntries = getDatabaseHelper().getEventDao().queryForAll();
        for (int i = 0; i< databaseEntries.size(); i++){
            titles.add( databaseEntries.get(i).getTitle());
        }
        return titles;
    }

    @Override
    public void onDestroy() {
        if (mDatabaseHelper != null){
            OpenHelperManager.releaseHelper();
            mDatabaseHelper = null;
        }
        super.onDestroy();
    }
}
