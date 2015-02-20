package com.mobilemakers.remindmetv;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.TransitionDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ListFragment;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
//
public class ShowsListFragment extends ListFragment {

    public static final String EXTRA_SEARCH = "extra_search";
    private static final String LOG_TAG = ShowsListFragment.class.getSimpleName();
    ShowAdapter mAdapter;
    EditText mEditShowName;
    ImageButton mImageButtonSearch;
    RelativeLayout mProgressLayout;
    TransitionDrawable mTransitionImageButton;
    TransitionDrawable mTransitionEditText;
    CustomListener customListener = new CustomListener(true);

    public ShowsListFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_shows_list, container, false);
        wireUpViews(rootView);
        wireupTransitions();
        setClickListener();
        setTextWatcher();
        return rootView;
    }

    private void wireupTransitions() {
        mTransitionEditText = (TransitionDrawable) mEditShowName.getBackground();
        mTransitionImageButton = (TransitionDrawable) mImageButtonSearch.getBackground();

    }

    private void setTextWatcher() {
        mEditShowName.addTextChangedListener(new TextWatcher() {
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
                if (s.length() == 1 && check) {
                    customListener.setEnabled(true);
                    mTransitionEditText.reverseTransition(1000);
                    mTransitionImageButton.reverseTransition(1000);
                } else {
                    if (TextUtils.isEmpty(s.toString().trim())) {
                        customListener.setEnabled(false);
                        mTransitionImageButton.startTransition(1000);
                        mTransitionEditText.startTransition(1000);
                    }
                }
                mEditShowName.setPadding(30, 0, 0, 0);
                mImageButtonSearch.setPadding(20, 0, 30, 0);
            }
        });
    }

    private void wireUpViews(View rootView) {
        mEditShowName = (EditText) rootView.findViewById(R.id.edit_text_search_list);
        mImageButtonSearch = (ImageButton) rootView.findViewById(R.id.image_button_search);
        mProgressLayout = (RelativeLayout)rootView.findViewById(R.id.loadingPanel);
    }

    private void setClickListener() {
        mImageButtonSearch.setOnClickListener(customListener);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        prepareListView();
        searchForInitialCall();
    }

    private void prepareListView() {
        List<Show> shows = new ArrayList<>();
        mAdapter = new ShowAdapter(getActivity(), shows);
        setListAdapter(mAdapter);
        getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Show selectedShow = mAdapter.getItem(position);
                Intent intent = new Intent(getActivity(), CompleteInformationActivity.class);
                intent.putExtra(CompleteInformationActivity.EXTRA_SHOW, selectedShow);
                startActivity(intent);
            }
        });
    }

    private void searchForInitialCall() {
        Bundle bundle = getArguments();
        if (bundle != null && bundle.containsKey(EXTRA_SEARCH)) {
            String searchName = bundle.getString(EXTRA_SEARCH);
            mEditShowName.setText(searchName);
            mEditShowName.setSelection(mEditShowName.getText().length());
            fetchShowsInQueue(searchName);
        }
    }

    private void fetchShowsInQueue(String showName) {
        try {
            mProgressLayout.setVisibility(View.VISIBLE);
            URL url = constructURLQuery(showName);
            Request request = new Request.Builder().url(url.toString()).build();
            OkHttpClient client = new OkHttpClient();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Request request, IOException e) {
                    e.printStackTrace();
                }

                @Override
                public void onResponse(Response response) throws IOException {
                    String responseString = response.body().string();
                    final List<Show> listOfShows = ShowParser.parseResponse(responseString);
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mAdapter.clear();
                            mAdapter.addAll(listOfShows);
                            mAdapter.notifyDataSetChanged();
                            mProgressLayout.setVisibility(View.GONE);
                        }
                    });
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private URL constructURLQuery(String showName) throws MalformedURLException {
        final String HTTP = "http";
        final String TVRage_BASE_URL = "services.tvrage.com";
        final String FEEDS_PATH = "feeds";
        final String SEARCH_ENDPOINT = "full_search.php";
        final String QUERY_PARAMETER = "show";

        Uri.Builder builder = new Uri.Builder();
        builder.scheme(HTTP)
                .authority(TVRage_BASE_URL)
                .appendPath(FEEDS_PATH)
                .appendPath(SEARCH_ENDPOINT)
                .appendQueryParameter(QUERY_PARAMETER, showName);

        Uri uri = builder.build();
        Log.d(LOG_TAG, "Built URI: " + uri.toString());

        return new URL(uri.toString());

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
                String showName = mEditShowName.getText().toString();
                fetchShowsInQueue(showName);
                hideKeyboard();
            }
        }

        private void hideKeyboard() {
            mEditShowName.clearFocus();
            InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(
                    Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(mEditShowName.getWindowToken(), 0);
        }
    }

}
