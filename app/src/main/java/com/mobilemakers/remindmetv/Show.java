package com.mobilemakers.remindmetv;

public class Show {

    private String name;
    private String channel;

    public Show() {
    }

    public Show(String name, String channel) {
        this.name = name;
        this.channel = channel;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

}
