package com.mobilemakers.remindmetv;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.Calendar;
import java.util.Date;

/**
 * A placeholder fragment containing a simple view.
 */
public class CompleteInformationFragment extends Fragment {

    public static final String EXTRA_SHOW = "SHOW";
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

    Long mTimeA;
    Long mTimeB;

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
                Calendar beginTime = Calendar.getInstance();
                beginTime.set(2015, Calendar.FEBRUARY, 17, 22, 30, 0);
                beginTime.set(Calendar.MILLISECOND, 0);
                mTimeA = beginTime.getTimeInMillis();
                Calendar endTime = Calendar.getInstance();
                endTime.set(2015, Calendar.FEBRUARY, 17, 23, 30, 0);
                endTime.set(Calendar.MILLISECOND, 0);

                Intent intent = new Intent(Intent.ACTION_EDIT);
                intent.setData(CalendarContract.Events.CONTENT_URI);
                intent.putExtra(CalendarContract.Events.TITLE, mShow.getName());
                intent.putExtra(CalendarContract.Events.DESCRIPTION, "RemindMeTV Event");
                intent.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, beginTime.getTimeInMillis());
                intent.putExtra(CalendarContract.EXTRA_EVENT_END_TIME, endTime.getTimeInMillis());
                intent.putExtra(CalendarContract.EXTRA_EVENT_ALL_DAY, false);
                intent.putExtra(CalendarContract.Events.RRULE, "FREQ=WEEKLY;COUNT=50");
                //TODO Set timezone extra (read it from the API)
                //TODO Get Event ID
//                intent.putExtra(CalendarContract.Reminders.TITLE, "A Test Event from android app");
//                intent.putExtra(CalendarContract.Reminders.METHOD, CalendarContract.Reminders.METHOD_ALERT);
//                intent.putExtra(CalendarContract.Reminders.MINUTES, 20);
                startActivityForResult(intent, 2);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 2) {

            final String[] EVENT_PROJECTION = new String[]{
                    CalendarContract.Events._ID,
                    CalendarContract.Events.DTSTART,
                    CalendarContract.Events.DURATION,
            };

            Cursor cur;
            ContentResolver cr = getActivity().getContentResolver();
            Uri uri = CalendarContract.Events.CONTENT_URI;
            String selection = "((" + CalendarContract.Events.DTSTART  + " = " + mTimeA +") AND " +
                                "(" + CalendarContract.Events.DURATION + " = 'P3600S'))";
            cur = cr.query(uri, EVENT_PROJECTION, selection, null, null);

            while (cur.moveToNext()) {
                String eventID;
                Long event_start;
                String event_duration;

                eventID = cur.getString(0);
                event_start = cur.getLong(1);
                event_duration = cur.getString(2);

                Log.d("LOG", eventID + " - " + String.valueOf(event_start) + " - "+event_duration);

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
            mTextViewChannel.setText(mShow.getChannel());
            mTextViewStatus.setText(mShow.getStatus());
            mTextViewLink.setText(mShow.getURL());
            mTextViewStarted.setText(mShow.getStartedDate());
            mTextViewEnded.setText(mShow.getEndedDate());
            mTextViewSeasons.setText(String.valueOf(mShow.getSeasons()));
            mTextViewRuntime.setText(String.valueOf(mShow.getRuntime()));
            mTextViewAirtime.setText(mShow.getAirtime());
            mTextViewAirday.setText(mShow.getAirday());
        }
    }
}
