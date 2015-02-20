package com.mobilemakers.remindmetv;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;

import java.sql.SQLException;
import java.util.Calendar;
import java.util.List;

/**
 * A placeholder fragment containing a simple view.
 */
public class CompleteInformationFragment extends Fragment {

    public static final String EXTRA_SHOW = "SHOW";
    private static final int REQUEST_CODE = 2;
    private static final int EVENT_NOT_FOUND = 1;
    private static final String EVENT_ALREADY_CREATED= "Event Already Created";

    DatabaseHelper mDatabaseHelper = null;
    ContentResolver mContentResolver;

    Show mShow;
    TextView mTextViewShowName;
    TextView mTextViewChannel;
    TextView mTextViewStatus;
    TextView mTextViewLink;
    TextView mTextViewStarted;
    TextView mTextViewEnded;
    TextView mTextViewSeasons;
    TextView mTextViewRuntime;
    TextView mTextViewAirtime;
    TextView mTextViewAirday;

    Long mStartTime;
    int mHour;
    int mMinutes;

    public CompleteInformationFragment() {
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
        View rootView = inflater.inflate(R.layout.fragment_complete_information, container, false);
        wireUpViews(rootView);
        prepareButtonAddToCalendar(rootView);
        return rootView;
    }

    private void wireUpViews(View rootView) {
        mTextViewShowName = (TextView)rootView.findViewById(R.id.text_view_name_complete_information);
        mTextViewChannel = (TextView)rootView.findViewById(R.id.text_view_channel_complete_information);
        mTextViewStatus = (TextView)rootView.findViewById(R.id.text_view_status_complete_information);
        mTextViewLink = (TextView)rootView.findViewById(R.id.text_view_link_complete_information);
        mTextViewStarted = (TextView)rootView.findViewById(R.id.text_view_started_complete_information);
        mTextViewEnded = (TextView)rootView.findViewById(R.id.text_view_ended_complete_information);
        mTextViewSeasons = (TextView)rootView.findViewById(R.id.text_view_seasons_complete_information);
        mTextViewRuntime = (TextView)rootView.findViewById(R.id.text_view_runtime_complete_information);
        mTextViewAirtime = (TextView)rootView.findViewById(R.id.text_view_airtime_complete_information);
        mTextViewAirday = (TextView)rootView.findViewById(R.id.text_view_airday_complete_information);
    }

    private void prepareButtonAddToCalendar(View rootView) {
        Button buttonAddToCalendar = (Button)rootView.findViewById(R.id.button_add_to_calendar);
        buttonAddToCalendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Event event = returnEventFromDatabase();
                    if (event == null) {
                        startCalendarIntentWithExtras();
                    }
                    else {
                     //Event in database
                        if (!eventExistsInCalendar(event.getId())) {
                            deleteEventInDatabase(event);
                            startCalendarIntentWithExtras();
                        }
                        else {
                        //Event in calendar too
                            Toast.makeText(getActivity(), EVENT_ALREADY_CREATED, Toast.LENGTH_LONG).show();
                        }
                    }

                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }

            private void deleteEventInDatabase(Event event) {
                try {
                    Dao<Event, Integer> dao = getDatabaseHelper().getEventDao();
                    dao.delete(event);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }

            private Event returnEventFromDatabase() throws SQLException {
                Dao<Event, Integer> dao = getDatabaseHelper().getEventDao();
                QueryBuilder<Event, Integer> queryBuilder = dao.queryBuilder();
                queryBuilder.where().eq(Event.TITLE ,mShow.getName());
                PreparedQuery<Event> preparedQuery = queryBuilder.prepare();
                List<Event> eventList = dao.query(preparedQuery);
                if (!eventList.isEmpty()){
                    return eventList.get(0);
                }
                else {
                    return null;
                }

            }

            private void startCalendarIntentWithExtras() {
                //final String EVENT_DESCRIPTION = "RemindMeTV Event";
                final String EVENT_RRULE = "FREQ=WEEKLY;COUNT=50";

                int day = parseAirdayFromShow();
                parseHourAndMinutesFromShow();

                Calendar beginTime = Calendar.getInstance();
                while( beginTime.get( Calendar.DAY_OF_WEEK ) != day)
                    beginTime.add( Calendar.DATE, 1 );

                beginTime.set(Calendar.HOUR_OF_DAY, mHour);
                beginTime.set(Calendar.MINUTE, mMinutes);
                beginTime.set(Calendar.SECOND, 0);
                beginTime.set(Calendar.MILLISECOND, 0);
                mStartTime = beginTime.getTimeInMillis();

                Calendar endTime = (Calendar)beginTime.clone();
                endTime.add(Calendar.MINUTE, mShow.getRuntime());

                Intent intent = new Intent(Intent.ACTION_EDIT);
                intent.setData(CalendarContract.Events.CONTENT_URI);
                intent.putExtra(CalendarContract.Events.TITLE, mShow.getName());
                intent.putExtra(CalendarContract.Events.DESCRIPTION, String.format(getString(R.id.event_description), mShow.getChannel()));
                intent.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, beginTime.getTimeInMillis());
                intent.putExtra(CalendarContract.EXTRA_EVENT_END_TIME, endTime.getTimeInMillis());
                intent.putExtra(CalendarContract.EXTRA_EVENT_ALL_DAY, false);
                intent.putExtra(CalendarContract.Events.RRULE, EVENT_RRULE);

                startActivityForResult(intent, REQUEST_CODE);
            }

            private int parseAirdayFromShow() {
                final String MONDAY = "monday";
                final String TUESDAY = "tuesday";
                final String WEDNESDAY = "wednesday";
                final String THURSDAY = "thursday";
                final String FRIDAY = "friday";
                final String SATURDAY = "saturday";
                final String SUNDAY = "sunday";

                int day;
                switch (mShow.getAirday().toLowerCase().trim()){
                    case MONDAY: day = Calendar.MONDAY;
                        break;
                    case TUESDAY: day = Calendar.TUESDAY;
                        break;
                    case WEDNESDAY: day = Calendar.WEDNESDAY;
                        break;
                    case THURSDAY: day = Calendar.THURSDAY;
                        break;
                    case FRIDAY: day = Calendar.FRIDAY;
                        break;
                    case SATURDAY: day = Calendar.SATURDAY;
                        break;
                    case SUNDAY: day = Calendar.SUNDAY;
                        break;
                    default: day = Calendar.MONDAY;
                        break;
                }
                return day;
            }

            private void parseHourAndMinutesFromShow() {
                String hourAndMinutes = mShow.getAirtime();
                int colonIndex = hourAndMinutes.indexOf(":");
                mHour = Integer.valueOf(hourAndMinutes.substring(0,colonIndex));
                mMinutes = Integer.valueOf(hourAndMinutes.substring(colonIndex+1, hourAndMinutes.length()));
            }

            private boolean eventExistsInCalendar(Integer eventID){

                final String[] EVENT_PROJECTION = new String[]{
                        CalendarContract.Events._ID,
                };

                Cursor cursorEvents;
                Uri eventsUri = CalendarContract.Events.CONTENT_URI;
                //Filter by BEGIN_TIME and DURATION
                String eventSelection = "(" + CalendarContract.Events._ID  + " = " + eventID +")";
                cursorEvents = mContentResolver.query(eventsUri, EVENT_PROJECTION, eventSelection, null, null);

                return cursorEvents.moveToNext();
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //resultCode always is 0
        if (requestCode == REQUEST_CODE) {
            final int REMINDER_MINUTES = 15;

            int eventID = returnEventIDFromCalendar();

            if (eventID != EVENT_NOT_FOUND){
                saveEventInDatabase(eventID);

                Uri remindersUri = CalendarContract.Reminders.CONTENT_URI;
                //Event without reminders
                if (!eventHasReminders(remindersUri,eventID)) {
                    saveDefaultReminderForEventInCalendar(REMINDER_MINUTES, mContentResolver, eventID, remindersUri);
                }
            }
        }
    }

    private int returnEventIDFromCalendar(){
        String duration = "P" + String.valueOf(mShow.getRuntime()*60) +"S";

        final String[] EVENT_PROJECTION = new String[]{
                CalendarContract.Events._ID,
        };

        Cursor cursorEvents;
        Uri eventsUri = CalendarContract.Events.CONTENT_URI;
        //Filter by BEGIN_TIME and DURATION
        String eventSelection = "((" + CalendarContract.Events.DTSTART  + " = " + mStartTime + ") AND " +
                "(" + CalendarContract.Events.DURATION + " = '" + duration + "'))";
        cursorEvents = mContentResolver.query(eventsUri, EVENT_PROJECTION, eventSelection, null, null);

        if (cursorEvents.moveToNext()) {
            //Event was created
            return cursorEvents.getInt(0);
        }
        else {
            return EVENT_NOT_FOUND;
        }
    }

    private void saveEventInDatabase(int eventID) {
        Event event = new Event();
        event.setId(eventID);
        event.setTitle(mShow.getName());

        try {
            Dao<Event, Integer> dao = getDatabaseHelper().getEventDao();
            dao.create(event);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private boolean eventHasReminders(Uri remindersUri, Integer eventID){
        final String[] REMINDER_PROJECTION = new String[]{
                CalendarContract.Reminders._ID
        };

        Cursor cursorReminders;
        String reminderSelection = "(" + CalendarContract.Reminders.EVENT_ID  + " = " + eventID +")";

        cursorReminders = mContentResolver.query(remindersUri, REMINDER_PROJECTION, reminderSelection, null , null);

        return cursorReminders.moveToNext();
    }

    private void saveDefaultReminderForEventInCalendar(int REMINDER_MINUTES, ContentResolver cr, int eventID, Uri remindersUri) {
        ContentValues values = new ContentValues();
        values.put(CalendarContract.Reminders.MINUTES, REMINDER_MINUTES);
        values.put(CalendarContract.Reminders.EVENT_ID, eventID);
        values.put(CalendarContract.Reminders.METHOD, CalendarContract.Reminders.METHOD_ALERT);
        cr.insert(remindersUri, values);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        prepareTextViews();
        mContentResolver = getActivity().getContentResolver();
    }

    private void prepareTextViews() {
        if (getArguments().containsKey(EXTRA_SHOW)){
            mShow = getArguments().getParcelable(EXTRA_SHOW);
            mTextViewShowName.setText(mShow.getName());
            mTextViewChannel.setText(String.format(getString(R.string.info_channel), mShow.getChannel()));
            mTextViewStatus.setText(String.format(getString(R.string.info_status), mShow.getStatus()));
            mTextViewLink.setText(mShow.getURL());
            mTextViewStarted.setText(String.format(getString(R.string.info_started_date), mShow.getStartedDate()));
            mTextViewEnded.setText(String.format(getString(R.string.info_ended_date), mShow.getEndedDate()));
            mTextViewSeasons.setText(String.format(getString(R.string.info_seasons), String.valueOf(mShow.getSeasons())));
            mTextViewRuntime.setText(String.format(getString(R.string.info_runtime), String.valueOf(mShow.getRuntime())));
            mTextViewAirtime.setText(String.format(getString(R.string.info_airtime), mShow.getAirtime()));
            mTextViewAirday.setText(String.format(getString(R.string.info_airday), mShow.getAirday()));
        }
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
