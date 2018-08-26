package com.india.android.mapp;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

import com.orhanobut.hawk.Hawk;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static com.india.android.mapp.Constants.NOTIFICATION_CHANNEL_DESC;
import static com.india.android.mapp.Constants.NOTIFICATION_CHANNEL_ID;
import static com.india.android.mapp.Constants.NOTIFICATION_CHANNEL_NAME;
import static com.india.android.mapp.Constants.NOTIFICATION_ID;

/**
 * Created by admin on 25-08-2018.
 */

public class LocationService extends Service implements LocationListener {

    LocationManager locationManager;
    LocationData locData;
    boolean isSessionLive=false;
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        startSession();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopSession();
    }


    private void startInForeground() {
        Intent notificationIntent = new Intent(this, MainActivity.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "location_notif")
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setContentTitle("Location tracking ON")
                .setContentText("Tap to open the app and stop tracking")
                .setTicker("TICKER")
                .setContentIntent(pendingIntent);
        Notification notification = builder.build();
        if (Build.VERSION.SDK_INT >= 26) {
            NotificationChannel channel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, NOTIFICATION_CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription(NOTIFICATION_CHANNEL_DESC);
            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(channel);
        }
        startForeground(NOTIFICATION_ID, notification);
    }


    private void startSession() {
        locData=null;
        Toast.makeText(getApplicationContext(), "You will be notified when tracking session starts", Toast.LENGTH_SHORT).show();
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
    }



    @Override
    public void onLocationChanged(Location loc) {

        if(locData==null){
            Toast.makeText(getApplicationContext(), "Session started", Toast.LENGTH_SHORT).show();
            startInForeground();
            isSessionLive=true;
            locData=new LocationData();
            locData.setLat(loc.getLatitude());
            locData.setLon(loc.getLongitude());
            locData.setTime(System.currentTimeMillis());

            List<Address> addresses;
            Geocoder gcd = new Geocoder(getApplicationContext(), Locale.getDefault());
            try {
                addresses = gcd.getFromLocation(loc.getLatitude(),
                        loc.getLongitude(), 1);
                if (addresses.size() > 0)
                    locData.setAddress( addresses.get(0).getAddressLine(0));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else{
            float[] result=new float[3];
            Location.distanceBetween(locData.getLat(),locData.getLon(),loc.getLatitude(),loc.getLongitude(),result);

            if (result[0]>100){
                stopSession();
                }
        }

    }

    @Override
    public void onProviderDisabled(String provider) {}

    @Override
    public void onProviderEnabled(String provider) {}

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {}


    private void stopSession(){
        stopForeground(true);
        stopSelf();
        if(locationManager!=null){
            locationManager.removeUpdates(this);
        }
        if (isSessionLive){
        locData.setMinutes((int)((System.currentTimeMillis()-locData.getTime())/(60*1000)));
        ArrayList<LocationData> locList=Hawk.get(Constants.LOCATION_LIST);
        locList.add(0,locData);
        Hawk.put(Constants.LOCATION_LIST,locList);
        isSessionLive=false;
            Toast.makeText(getApplicationContext(), "Session Saved", Toast.LENGTH_SHORT).show();
        }
    }



}
