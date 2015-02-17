package com.mobilemakers.remindmetv;

import android.content.Intent;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.Calendar;

/**
 * A placeholder fragment containing a simple view.
 */
public class CompleteInformationFragment extends Fragment {

    public static final String EXTRA_SHOW = "SHOW";
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
                Calendar calendar = Calendar.getInstance();
                //TODO Set the information correctly
                calendar.set(Calendar.HOUR_OF_DAY, 20);
                calendar.set(Calendar.MINUTE, 40);
                calendar.set(2015, Calendar.FEBRUARY , 16);
                Intent intent = new Intent(Intent.ACTION_EDIT);
                intent.setType("vnd.android.cursor.item/event");
                intent.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, calendar.getTimeInMillis());
                intent.putExtra(CalendarContract.EXTRA_EVENT_ALL_DAY, false);
                intent.putExtra(CalendarContract.Events.RRULE, "FREQ=WEEKLY;BYDAY=TU,TH");
                intent.putExtra(CalendarContract.EXTRA_EVENT_END_TIME, calendar.getTimeInMillis()+60*60*1000);
                intent.putExtra(CalendarContract.Reminders.TITLE, "");
                intent.putExtra(CalendarContract.Events.TITLE, "A Test Event from android app");
                intent.putExtra(CalendarContract.Events.HAS_ALARM, 1);
                //TODO Set timezone extra (read it from the API)
                //intent.putExtra(CalendarContract.Reminders.TITLE, "A Test Event from android app");
                //intent.putExtra(CalendarContract.Reminders.METHOD, CalendarContract.Reminders.METHOD_ALERT);
                //intent.putExtra(CalendarContract.Reminders.MINUTES, 20);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        prepareTextViews();
    }

    private void prepareTextViews() {
        if (getArguments().containsKey(EXTRA_SHOW)){
            Show show = getArguments().getParcelable(EXTRA_SHOW);
            mTextViewShowName.setText(show.getName());
            mTextViewChannel.setText(show.getChannel());
            mTextViewStatus.setText(show.getStatus());
            mTextViewLink.setText(show.getURL());
            mTextViewStarted.setText(show.getStartedDate());
            mTextViewEnded.setText(show.getEndedDate());
            mTextViewSeasons.setText(String.valueOf(show.getSeasons()));
            mTextViewRuntime.setText(String.valueOf(show.getRuntime()));
            mTextViewAirtime.setText(show.getAirtime());
            mTextViewAirday.setText(show.getAirday());
        }
    }
}
