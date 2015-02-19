package com.mobilemakers.remindmetv;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Layout;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.Calendar;
//
/**
 * A placeholder fragment containing a simple view.
 */
public class CompleteInformationFragment extends Fragment {

    public static final String EXTRA_SHOW = "SHOW";
    private static final int REQUEST_CODE = 2;

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
                startCalendarIntentWithExtras();
            }

            private void startCalendarIntentWithExtras() {
                final String EVENT_DESCRIPTION = "RemindMeTV Event";
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
                intent.putExtra(CalendarContract.Events.DESCRIPTION, EVENT_DESCRIPTION);
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
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //resultCode always is 0
        if (requestCode == REQUEST_CODE) {
            final int REMINDER_MINUTES = 15;

            String duration = "P" + String.valueOf(mShow.getRuntime()*60) +"S";

            final String[] EVENT_PROJECTION = new String[]{
                    CalendarContract.Events._ID,
            };

            Cursor cursorEvents;
            ContentResolver cr = getActivity().getContentResolver();
            Uri eventsUri = CalendarContract.Events.CONTENT_URI;
            //Filter by BEGIN_TIME and DURATION
            String eventSelection = "((" + CalendarContract.Events.DTSTART  + " = " + mStartTime + ") AND " +
                                "(" + CalendarContract.Events.DURATION + " = '" + duration + "'))";
            cursorEvents = cr.query(eventsUri, EVENT_PROJECTION, eventSelection, null, null);

            if (cursorEvents.moveToNext()) {
                Integer eventID =  cursorEvents.getInt(0);

                final String[] REMINDER_PROJECTION = new String[]{
                        CalendarContract.Reminders._ID
                };

                Cursor cursorReminders;
                Uri remindersUri = CalendarContract.Reminders.CONTENT_URI;
                //Filter by EVENT_ID
                String reminderSelection = "(" + CalendarContract.Reminders.EVENT_ID  + " = " + eventID +")";
                cursorReminders = cr.query(remindersUri, REMINDER_PROJECTION, reminderSelection, null , null);

                //Event without reminders
                if (!cursorReminders.moveToNext()) {
                    ContentValues values = new ContentValues();
                    values.put(CalendarContract.Reminders.MINUTES, REMINDER_MINUTES);
                    values.put(CalendarContract.Reminders.EVENT_ID, eventID);
                    values.put(CalendarContract.Reminders.METHOD, CalendarContract.Reminders.METHOD_ALERT);
                    Uri rowUri = cr.insert(remindersUri, values);
                }
            }
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        prepareTextViews();
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

}
