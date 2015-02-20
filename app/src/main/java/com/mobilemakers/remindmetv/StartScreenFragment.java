package com.mobilemakers.remindmetv;

import android.content.Intent;
import android.graphics.drawable.TransitionDrawable;
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

public class StartScreenFragment extends Fragment {

    ImageButton mImageButtonSearch;
    EditText mEditTextSearch;
    TransitionDrawable mTransitionImageButton;
    TransitionDrawable mTransitionEditText;
    CustomListener customListener = new CustomListener(false);

    public StartScreenFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_start_screen, container, false);
        wireUpViews(rootView);
        wireupTransitions();
        setupListenersAndWatchers();
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        mEditTextSearch.setText("");
        reseetTransitions();
    }

    private void wireupTransitions() {
        mTransitionImageButton = (TransitionDrawable) mImageButtonSearch.getBackground();
        mTransitionEditText = (TransitionDrawable) mEditTextSearch.getBackground();
        mImageButtonSearch.setPadding(20,0,30,0);
        mEditTextSearch.setPadding(30,0,0,0);
    }

    private void setupListenersAndWatchers() {
        mImageButtonSearch.setOnClickListener(customListener);
        mEditTextSearch.addTextChangedListener(new TextWatcher() {
            Boolean check;
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                check = TextUtils.isEmpty(s.toString().trim());
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length()==1 && check) {
                    customListener.setEnabled(true);
                    startTransitions();
                } else {
                    if (TextUtils.isEmpty(s.toString().trim())){
                        customListener.setEnabled(false);
                        reverseTransitions();
                    }
                }
            }
        });
    }

    private void reverseTransitions() {
        mTransitionImageButton.reverseTransition(700);
        mTransitionEditText.reverseTransition(700);
    }

    private void startTransitions() {
        mTransitionImageButton.startTransition(700);
        mTransitionEditText.startTransition(700);
    }

    private void wireUpViews(View rootView) {
        mImageButtonSearch = (ImageButton)rootView.findViewById(R.id.imageButton_startscreen_search);
        mEditTextSearch = (EditText)rootView.findViewById(R.id.editText_startscreen_search);
    }

    private class CustomListener implements View.OnClickListener{

        private Boolean isEnabled;

        public void setEnabled(Boolean isEnabled) {
            this.isEnabled = isEnabled;
        }

        private CustomListener(Boolean isEnabled) {
            this.isEnabled = isEnabled;
        }

        @Override
        public void onClick(View v) {
            if (isEnabled) {
                Intent searchIntent = new Intent(getActivity(), ShowsListActivity.class);
                searchIntent.putExtra(ShowsListActivity.EXTRA_SEARCH , mEditTextSearch.getText().toString());
                startActivity(searchIntent);
            }
        }
    }

    private void reseetTransitions() {
        mTransitionEditText.resetTransition();
        mTransitionImageButton.resetTransition();
    }
}
