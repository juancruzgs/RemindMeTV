package com.mobilemakers.remindmetv;

import android.os.Parcel;
import android.os.Parcelable;

public class Show  implements Parcelable {

    //TODO Set all the members needed and change the constructor
    private String mName;
    private String mChannel;

    public Show() {
    }

    public Show(String name, String channel) {
        this.mName = name;
        this.mChannel = channel;
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


    //PARCELABLE INTERFACE
    public Show(Parcel parcel){
        mName = parcel.readString();
        mChannel = parcel.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mName);
        dest.writeString(mChannel);
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
