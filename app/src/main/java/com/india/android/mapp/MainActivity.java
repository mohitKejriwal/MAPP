package com.india.android.mapp;

import android.Manifest;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.DatePicker;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.orhanobut.hawk.Hawk;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity implements LocationClick {
    TextView startLoc,stopLoc,filterDate;
    ArrayList<LocationData> locList;
    RecyclerView rvLocations;
    LocationsAdapter locAdapter;
    RelativeLayout rlResults;
    FloatingActionButton refresh;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setupView();
    }

    @Override
    protected void onResume() {
        super.onResume();
       setupAdapter();
    }

    private void checkPermission(){
        if ((ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED ) ||(ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED)) {
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION},100);
        }else{
            startService();
        }
    }

    private void startService(){
        checkLocationProvider();
        Intent service=new Intent(MainActivity.this,LocationService.class);
        startService(service);
    }

    private void stopService(){
        Intent service=new Intent(MainActivity.this,LocationService.class);
        stopService(service);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],@NonNull int[] grantResults) {
        switch (requestCode) {
            case 100: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    startService();
                } else {
                    Toast.makeText(getApplicationContext(), "Permission denied, cannot fetch location", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private void checkLocationProvider(){
        LocationManager locationManager=(LocationManager) getSystemService(Context.LOCATION_SERVICE);
        boolean gps_enabled=false,network_enabled=false;
        try {
            gps_enabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch(Exception ex) {}

        try {
            network_enabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch(Exception ex) {}

        if(!gps_enabled && !network_enabled) {
            AlertDialog.Builder dialog = new AlertDialog.Builder(this);
            dialog.setMessage(getResources().getString(R.string.gps_network_not_enabled));
            dialog.setPositiveButton(getResources().getString(R.string.open_location_settings), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    Intent myIntent = new Intent( Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(myIntent);
                }
            });
            dialog.setNegativeButton(getApplicationContext().getString(R.string.cancel), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                }
            });
            dialog.show();
        }
    }

    private void setupAdapter(){
        if (!Hawk.contains(Constants.LOCATION_LIST)){
            locList=new ArrayList<>();
            Hawk.put(Constants.LOCATION_LIST,locList);
        }else{
            locList=Hawk.get(Constants.LOCATION_LIST);
        }
        locAdapter = new LocationsAdapter(locList,this);
        rvLocations.setAdapter(locAdapter);
        locAdapter.notifyDataSetChanged();
    }


    @Override
    public void onLocationClick(double lat,double lon) {
        Intent mapsIntent=new Intent(this,MapsActivity.class);
        mapsIntent.putExtra(Constants.LATITUDE,lat);
        mapsIntent.putExtra(Constants.LONGITUDE,lon);
        startActivity(mapsIntent);
    }


    private void setupView(){
        startLoc=findViewById(R.id.tvStartLoc);
        stopLoc=findViewById(R.id.tvStopLoc);
        filterDate=findViewById(R.id.tvFilterDate);
        rlResults=findViewById(R.id.rlResults);
        rvLocations=findViewById(R.id.rvLocations);
        refresh=findViewById(R.id.fabRefresh);
        rvLocations.setLayoutManager(new LinearLayoutManager(this));

        startLoc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkPermission();

            }
        });

        stopLoc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopService();
            }
        });

        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setupAdapter();
            }
        });

        filterDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar calendar = Calendar.getInstance();
                int mYear = calendar.get(Calendar.YEAR);
                int mMonth = calendar.get(Calendar.MONTH);
                int mDay = calendar.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(MainActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {

                                calendar.set(year, monthOfYear, dayOfMonth);
                                SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
                                String strDate = format.format(calendar.getTime());
                                filterDate.setText(strDate);
                                locAdapter.getFilter().filter(strDate);
                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.show();
            }
        });
    }
}
