package com.example.anneflo.geophone;


import android.os.Build;

import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class LongLocationActivity extends FragmentActivity implements
        OnMapReadyCallback {

    GoogleMap mMap;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }


    //If app is stopped, disconnect Google API
    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        Double mLatitude = Double.parseDouble(getIntent().getStringExtra("LAT"));
        Double mLongitude = Double.parseDouble(getIntent().getStringExtra("LNG"));

        //Getting location for marker
        LatLng currentLocation = new LatLng(mLatitude, mLongitude);

        //Showing device name
        String deviceName = Build.BRAND + " " + Build.DEVICE;

        //Create a marker for map
        mMap.addMarker(new MarkerOptions()
                .position(currentLocation)
                .title(deviceName)
                .snippet("")
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.smalllogo)));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(currentLocation));

    }




}
