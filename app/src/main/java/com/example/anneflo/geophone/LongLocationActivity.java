package com.example.anneflo.geophone;

import android.location.Address;
import android.location.Geocoder;

import android.support.v4.app.FragmentActivity;

import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class LongLocationActivity extends FragmentActivity implements
        OnMapReadyCallback {

    GoogleMap mMap;

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

        //Enabling to find current location
        mMap.setMyLocationEnabled(true);

        //Retrieving GPS data from MainActivity
        Double mLatitude = Double.parseDouble(getIntent().getStringExtra("LAT"));
        Double mLongitude = Double.parseDouble(getIntent().getStringExtra("LNG"));
        String deviceName = getIntent().getStringExtra("DEVICE");

        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(this, Locale.getDefault());
        String address = "",
                city = "",
                postalCode = "";

        try {
            addresses = geocoder.getFromLocation(mLatitude, mLongitude, 1);
            address = addresses.get(0).getAddressLine(0);
            city = addresses.get(0).getLocality();
            postalCode = addresses.get(0).getPostalCode();

        } catch (IOException e) {
            e.printStackTrace();
        }

        //Getting location for marker
        LatLng remoteLocation = new LatLng(mLatitude, mLongitude);

        //Showing address where the phone is
        String completeAddress = address + "\n" + postalCode + " " + city;

        //Create a marker for map
        mMap.addMarker(new MarkerOptions()
                .position(remoteLocation)
                .title(deviceName)
                .snippet(completeAddress)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.smalllogo)));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(remoteLocation));

    }




}
