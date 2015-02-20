package com.mobilemakers.remindmetv;

import com.j256.ormlite.field.DatabaseField;

public class Event {

    public static final String ID = "_id"; //It needs the underscore
    public static final String TITLE = "title";

    @DatabaseField(id = true, columnName = ID) private int mId;
    @DatabaseField(columnName = TITLE) private String mTitle;

    public int getId() {
        return mId;
    }

    public void setId(int id) {
        this.mId = id;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }
}
