package com.mobilemakers.remindmetv;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * A placeholder fragment containing a simple view.
 */
public class CompleteInformationFragment extends Fragment {

    public static final String EXTRA_SHOW = "SHOW";
    TextView mTextViewShowName;
    TextView mTextViewChannel;

    public CompleteInformationFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_complete_information, container, false);
        wireUpViews(rootView);
        return rootView;
    }

    private void wireUpViews(View rootView) {
        mTextViewShowName = (TextView)rootView.findViewById(R.id.text_view_show_name_complete_information);
        mTextViewChannel = (TextView)rootView.findViewById(R.id.text_view_show_channel_complete_information);
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
        }
    }
}
