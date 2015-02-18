package com.mobilemakers.remindmetv;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

public class Show  implements Parcelable {

    private String mName = "";
    private String mChannel = "";
    private String mURL = "";
    private String mStatus = "";
    private List<String> mGenres;
    private String mStartedDate = "";
    private String mEndedDate = "";
    private int mSeasons = 0;
    private String mAirtime = "";
    private String mAirday = "";
    private int mRuntime = 0;


    public Show() {
        mGenres = new ArrayList<>();
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        this.mName = name;
    }

    public String getChannel() {
        return mChannel;
    }

    public void setChannel(String channel) {
        this.mChannel = channel;
    }

    public String getURL() {
        return mURL;
    }

    public void setURL(String URL) {
        mURL = URL;
    }

    public String getStatus() {
        return mStatus;
    }

    public void setStatus(String status) {
        mStatus = status;
    }

    public List<String> getGenres() {
        return mGenres;
    }

    public void setGenres(List<String> genres) {
        mGenres = genres;
    }

    public String getStartedDate() {
        return mStartedDate;
    }

    public void setStartedDate(String startedDate) {
        mStartedDate = startedDate;
    }

    public String getEndedDate() {
        return mEndedDate;
    }

    public void setEndedDate(String endedDate) {
        mEndedDate = endedDate;
    }

    public int getSeasons() {
        return mSeasons;
    }

    public void setSeasons(int seasons) {
        mSeasons = seasons;
    }

    public String getAirtime() {
        return mAirtime;
    }

    public void setAirtime(String airtime) {
        mAirtime = airtime;
    }

    public String getAirday() {
        return mAirday;
    }

    public void setAirday(String airday) {
        mAirday = airday;
    }

    public int getRuntime() {
        return mRuntime;
    }

    public void setRuntime(int runtime) {
        mRuntime = runtime;
    }

    //PARCELABLE INTERFACE
    public Show(Parcel parcel){
        mName = parcel.readString();
        mChannel = parcel.readString();
        mURL = parcel.readString();
        mStatus = parcel.readString();
        mGenres = parcel.readArrayList(null);
        mStartedDate = parcel.readString();
        mEndedDate = parcel.readString();
        mSeasons = parcel.readInt();
        mAirtime = parcel.readString();
        mAirday = parcel.readString();
        mRuntime = parcel.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mName);
        dest.writeString(mChannel);
        dest.writeString(mURL);
        dest.writeString(mStatus);
        dest.writeList(mGenres);
        dest.writeString(mStartedDate);
        dest.writeString(mEndedDate);
        dest.writeInt(mSeasons);
        dest.writeString(mAirtime);
        dest.writeString(mAirday);
        dest.writeInt(mRuntime);
    }

    public static final Creator<Show> CREATOR = new Creator<Show>() {
        @Override
        public Show createFromParcel(Parcel source) {
            return new Show(source);
        }

        @Override
        public Show[] newArray(int size) {
            return new Show[size];
        }
    };
}
