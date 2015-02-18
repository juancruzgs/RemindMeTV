package com.mobilemakers.remindmetv;

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
import android.util.Xml;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;

import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class ShowsListFragment extends ListFragment {

    public static final String EXTRA_SEARCH = "extra_search";
    private static final String LOG_TAG = ShowsListFragment.class.getSimpleName();
    ShowAdapter mAdapter;
    EditText mEditShowName;
    ImageButton mImageButtonSearch;
    TransitionDrawable mTransitionImageButton;
    TransitionDrawable mTransitionEditText;
    CustomListener customListener = new CustomListener(true);

    final String ns = null;
    final String RESULTS = "Results";
    final String SHOW = "show";
    final String NAME = "name";
    final String CHANNEL = "network";
    final String LINK = "link";
    final String STARTED = "started";
    final String ENDED = "ended";
    final String SEASONS = "seasons";
    final String STATUS = "status";
    final String RUNTIME = "runtime";
    final String GENRES = "genres";
    final String AIRTIME = "airtime";
    final String AIRDAY = "airday";
    final String AKAS = "akas";

    public ShowsListFragment() {
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
                if (s.length()==1 && check) {
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
                mEditShowName.setPadding(30,0,0,0);
                mImageButtonSearch.setPadding(20,0,30,0);
            }
        });
    }

    private void wireUpViews(View rootView) {
        mEditShowName = (EditText) rootView.findViewById(R.id.edit_text_search_list);
        mImageButtonSearch = (ImageButton) rootView.findViewById(R.id.image_button_search);
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
                Show selectedShow = (Show) mAdapter.getItem(position);
                Intent intent = new Intent(getActivity(), CompleteInformationActivity.class);
                intent.putExtra(CompleteInformationActivity.EXTRA_SHOW, selectedShow);
                startActivity(intent);
            }
        });
    }

    private void searchForInitialCall() {
        if (getArguments().containsKey(EXTRA_SEARCH)) {
            String searchName = getArguments().getString(EXTRA_SEARCH);
            mEditShowName.setText(searchName);
            mEditShowName.setSelection(mEditShowName.getText().length());
            fetchShowsInQueue(searchName);
        }
    }

    private void fetchShowsInQueue(String showName) {
        try {
            URL url = constructURLQuery(showName);
            Request request = new Request.Builder().url(url.toString()).build();
            OkHttpClient client = new OkHttpClient();
            //Enqueue for Async mode --- Execute for Sync mode
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Request request, IOException e) {
                    e.printStackTrace();
                }

                @Override
                public void onResponse(Response response) throws IOException {
                    String responseString = response.body().string();
                    final List<Show> listOfShows = parseResponse(responseString);
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mAdapter.clear();
                            mAdapter.addAll(listOfShows);
                            //Only for compatibility
                            mAdapter.notifyDataSetChanged();
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

    private List<Show> parseResponse(String response) {

        List<Show> shows = new ArrayList<>();
        try {
            InputStream is = new ByteArrayInputStream(response.getBytes("UTF-8"));
            XmlPullParser parser = Xml.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(is, null);
            parser.nextTag();
            shows = readShows(parser);
            //TODO In finally
            is.close();
        } catch (XmlPullParserException | IOException e) {
            e.printStackTrace();
        }

        return shows;
    }

    private List<Show> readShows(XmlPullParser parser) throws XmlPullParserException, IOException {
        List<Show> entries = new ArrayList<>();

        parser.require(XmlPullParser.START_TAG, ns, RESULTS);
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            if (name.equals(SHOW)) {
                entries.add(readEntry(parser));
            } else {
                skip(parser);
            }
        }
        return entries;
    }

    private Show readEntry(XmlPullParser parser) throws XmlPullParserException, IOException {
        Show show = new Show();
        parser.require(XmlPullParser.START_TAG, ns, SHOW);
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            switch (name) {
                case NAME:
                    parser.require(XmlPullParser.START_TAG, ns, NAME);
                    if (parser.next() == XmlPullParser.TEXT) {
                        show.setName(parser.getText());
                        parser.nextTag();
                    }
                    parser.require(XmlPullParser.END_TAG, ns, NAME);
                    break;
                case LINK:
                    parser.require(XmlPullParser.START_TAG, ns, LINK);
                    if (parser.next() == XmlPullParser.TEXT) {
                        show.setURL(parser.getText());
                        parser.nextTag();
                    }
                    parser.require(XmlPullParser.END_TAG, ns, LINK);
                    break;
                case CHANNEL:
                    parser.require(XmlPullParser.START_TAG, ns, CHANNEL);
                    if (parser.next() == XmlPullParser.TEXT) {
                        show.setChannel(parser.getText());
                        parser.nextTag();
                    }
                    parser.require(XmlPullParser.END_TAG, ns, CHANNEL);
                    break;
                case STARTED:
                    parser.require(XmlPullParser.START_TAG, ns, STARTED);
                    if (parser.next() == XmlPullParser.TEXT) {
                        show.setStartedDate(parser.getText());
                        parser.nextTag();
                    }
                    parser.require(XmlPullParser.END_TAG, ns, STARTED);
                    break;
                case ENDED:
                    parser.require(XmlPullParser.START_TAG, ns, ENDED);
                    if (parser.next() == XmlPullParser.TEXT) {
                        show.setEndedDate(parser.getText());
                        parser.nextTag();
                    }
                    parser.require(XmlPullParser.END_TAG, ns, ENDED);
                    break;
                case SEASONS:
                    parser.require(XmlPullParser.START_TAG, ns, SEASONS);
                    if (parser.next() == XmlPullParser.TEXT) {
                        show.setSeasons(Integer.valueOf(parser.getText()));
                        parser.nextTag();
                    }
                    parser.require(XmlPullParser.END_TAG, ns, SEASONS);
                    break;
                case STATUS:
                    parser.require(XmlPullParser.START_TAG, ns, STATUS);
                    if (parser.next() == XmlPullParser.TEXT) {
                        show.setStatus(parser.getText());
                        parser.nextTag();
                    }
                    parser.require(XmlPullParser.END_TAG, ns, STATUS);
                    break;
                case RUNTIME:
                    parser.require(XmlPullParser.START_TAG, ns, RUNTIME);
                    if (parser.next() == XmlPullParser.TEXT) {
                        show.setRuntime(Integer.valueOf(parser.getText()));
                        parser.nextTag();
                    }
                    parser.require(XmlPullParser.END_TAG, ns, RUNTIME);
                    break;
                //TODO Case GENRE:
                case AIRTIME:
                    parser.require(XmlPullParser.START_TAG, ns, AIRTIME);
                    if (parser.next() == XmlPullParser.TEXT) {
                        show.setAirtime(parser.getText());
                        parser.nextTag();
                    }
                    parser.require(XmlPullParser.END_TAG, ns, AIRTIME);
                    break;
                case AIRDAY:
                    parser.require(XmlPullParser.START_TAG, ns, AIRDAY);
                    if (parser.next() == XmlPullParser.TEXT) {
                        show.setAirday(parser.getText());
                        parser.nextTag();
                    }
                    parser.require(XmlPullParser.END_TAG, ns, AIRDAY);
                    break;
                //TODO Case aka
                default:
                    skip(parser);
                    break;
            }
        }
        return show;
    }

    private void skip(XmlPullParser parser) throws XmlPullParserException, IOException {
        if (parser.getEventType() != XmlPullParser.START_TAG) {
            throw new IllegalStateException();
        }
        int depth = 1;
        while (depth != 0) {
            switch (parser.next()) {
                case XmlPullParser.END_TAG:
                    depth--;
                    break;
                case XmlPullParser.START_TAG:
                    depth++;
                    break;
            }
        }
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
            }
        }
    }

}
