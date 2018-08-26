package com.india.android.mapp;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by admin on 25-08-2018.
 */

public class LocationData {
    private double lat,lon;
    private String address;
    private int minutes;
    private long time;
    private String date;

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getMinutes() {
        return minutes;
    }

    public void setMinutes(int minutes) {
        this.minutes = minutes;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
         date = new SimpleDateFormat("dd/MM/yyyy").format(new Date(time));
    }

    public String getDate() {
        return date;
    }
}
