package com.mobilemakers.remindmetv;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ListFragment;
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

/**
 * A placeholder fragment containing a simple view.
 */
public class ShowsListFragment extends ListFragment {

    private static final String LOG_TAG = ShowsListFragment.class.getSimpleName();
    ShowAdapter mAdapter;
    EditText mEditShowName;

    final String ns = null;
    final String RESULTS = "Results";
    final String SHOW = "show";
    final String NAME = "name";
    final String CHANNEL = "network";

    public ShowsListFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_shows_list, container, false);
        wireUpViews(rootView);
        prepareImageButton(rootView);
        return rootView;
    }

    private void wireUpViews(View rootView) {
        mEditShowName = (EditText) rootView.findViewById(R.id.edit_text_search_list);
    }

    private void prepareImageButton(View rootView) {
        ImageButton imageButtonSearch = (ImageButton) rootView.findViewById(R.id.image_button_search);
        imageButtonSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String showName = mEditShowName.getText().toString();
                fetchShowsInQueue(showName);
            }
        });
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        prepareListView();
    }

    private void prepareListView() {
        List<Show> shows = new ArrayList<>();
        mAdapter = new ShowAdapter(getActivity(), shows);
        setListAdapter(mAdapter);
        getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Show selectedShow = (Show)mAdapter.getItem(position);
                //TODO Set the correct class name and make Show parcelable
                //Intent intent = new Intent(getActivity(), CompleteInformation.class);
                //intent.putExtra(selectedShow);
                //startActivity(intent);
            }
        });
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

    private List<Show> parseResponse(String response){

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
        parser.require(XmlPullParser.START_TAG, ns, SHOW);
        String showName = "";
        String channel = "";
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            switch (name) {
                case NAME:
                    parser.require(XmlPullParser.START_TAG, ns, NAME);
                    if (parser.next() == XmlPullParser.TEXT) {
                        showName = parser.getText();
                        parser.nextTag();
                    }
                    parser.require(XmlPullParser.END_TAG, ns, NAME);
                    break;
                case CHANNEL:
                    parser.require(XmlPullParser.START_TAG, ns, CHANNEL);
                    if (parser.next() == XmlPullParser.TEXT) {
                        channel = parser.getText();
                        parser.nextTag();
                    }
                    parser.require(XmlPullParser.END_TAG, ns, CHANNEL);
                    break;
                default:
                    skip(parser);
                    break;
            }
        }
        return new Show(showName, channel);
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

}
