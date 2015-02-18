package com.mobilemakers.remindmetv;

import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public final class ShowParser {

    private final static String ns = null;
    private final static String RESULTS = "Results";
    private final static String SHOW = "show";
    private final static String NAME = "name";
    private final static String CHANNEL = "network";
    private final static String LINK = "link";
    private final static String STARTED = "started";
    private final static String ENDED = "ended";
    private final static String SEASONS = "seasons";
    private final static String STATUS = "status";
    private final static String RUNTIME = "runtime";
    private final static String GENRES = "genres";
    private final static String GENRE = "genre";
    private final static String AIRTIME = "airtime";
    private final static String AIRDAY = "airday";

    private ShowParser() {
    }

    public static  List<Show> parseResponse(String response) {

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

    private static List<Show> readShows(XmlPullParser parser) throws XmlPullParserException, IOException {
        List<Show> shows = new ArrayList<>();

        parser.require(XmlPullParser.START_TAG, ns, RESULTS);
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            if (name.equals(SHOW)) {
                shows.add(readShow(parser));
            } else {
                skip(parser);
            }
        }
        return shows;
    }

    private static Show readShow(XmlPullParser parser) throws XmlPullParserException, IOException {
        Show show = new Show();
        parser.require(XmlPullParser.START_TAG, ns, SHOW);
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            switch (name) {
                case NAME:
                    show.setName(readTextFromTag(parser, NAME));
                    break;
                case LINK:
                    show.setURL(readTextFromTag(parser, LINK));
                    break;
                case STARTED:
                    show.setStartedDate(readTextFromTag(parser, STARTED));
                    break;
                case ENDED:
                    show.setEndedDate(readTextFromTag(parser, ENDED));
                    break;
                case SEASONS:
                    show.setSeasons(Integer.valueOf(readTextFromTag(parser, SEASONS)));
                    break;
                case STATUS:
                    show.setStatus(readTextFromTag(parser, STATUS));
                    break;
                case RUNTIME:
                    show.setRuntime(Integer.valueOf(readTextFromTag(parser, RUNTIME)));
                    break;
                case GENRES:
                    show.setGenres(readGenres(parser));
                    break;
                case CHANNEL:
                    show.setChannel(readTextFromTag(parser, CHANNEL));
                    break;
                case AIRTIME:
                    show.setAirtime(readTextFromTag(parser, AIRTIME));
                    break;
                case AIRDAY:
                    show.setAirday(readTextFromTag(parser, AIRDAY));
                    break;
                default:
                    skip(parser);
                    break;
            }
        }
        return show;
    }

    private static List<String> readGenres(XmlPullParser parser) throws XmlPullParserException, IOException {
        List<String> genres = new ArrayList<>();

        parser.require(XmlPullParser.START_TAG, ns, GENRES);
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            if (name.equals(GENRE)){
                genres.add(readText(parser));
            }
            else {
                skip(parser);
            }
        }
        return genres;
    }

    private static String readTextFromTag(XmlPullParser parser, String tag) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, ns, tag);
        String text = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, tag);
        return text;
    }

    private static String readText(XmlPullParser parser) throws XmlPullParserException, IOException {
        String result = "";
        if (parser.next() == XmlPullParser.TEXT) {
            result = parser.getText();
            parser.nextTag();
        }
        return result;
    }

    private static void skip(XmlPullParser parser) throws XmlPullParserException, IOException {
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
