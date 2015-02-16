package com.mobilemakers.remindmetv;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;

public class StartScreenFragment extends Fragment implements View.OnClickListener, TextWatcher {

    ImageButton mImageButtonSearch;
    EditText mEditTextSearch;

    public StartScreenFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_start_screen, container, false);
        wireUpViews(rootView);
        setupListenersAndWatchers();
        return rootView;
    }

    private void setupListenersAndWatchers() {
        mEditTextSearch.addTextChangedListener(this);
    }

    private void wireUpViews(View rootView) {
        mImageButtonSearch = (ImageButton)rootView.findViewById(R.id.imageButton_startscreen_search);
        mEditTextSearch = (EditText)rootView.findViewById(R.id.editText_startscreen_search);
    }

    @Override
    public void onClick(View v) {
        Intent searchIntent = new Intent(getActivity(), ShowsListActivity.class);
        searchIntent.putExtra(ShowsListActivity.EXTRA_SEARCH , mEditTextSearch.getText().toString());
        startActivity(searchIntent);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        if (!TextUtils.isEmpty(s.toString().trim())) {
            mImageButtonSearch.setOnClickListener(this);
            ((GradientDrawable)mImageButtonSearch.getBackground()).setColor(Color.GREEN);
        }
        else {
            mImageButtonSearch.setOnClickListener(null);
            ((GradientDrawable)mImageButtonSearch.getBackground()).setColor(Color.RED);
        }
    }
}
