package com.example.anneflo.geophone;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.telephony.SmsManager;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;


public class LocationActivity extends FragmentActivity implements
        OnMapReadyCallback,
        GoogleMap.OnMarkerClickListener{
    GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.setOnMarkerClickListener(this);

        //Retrieving GPS data from MainActivity
        Double mLatitude = Double.parseDouble(getIntent().getStringExtra("LAT"));
        Double mLongitude = Double.parseDouble(getIntent().getStringExtra("LNG"));
        String deviceName = getIntent().getStringExtra("DEVICE");

        //Getting location for marker
        LatLng remoteLocation = new LatLng(mLatitude, mLongitude);

        //Create a marker for map
        Marker remoteMarker = mMap.addMarker(new MarkerOptions()
                .position(remoteLocation)
                .title(deviceName)
                .snippet("Click on marker if you want to RING or VIBRATE")
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.smalllogo)));
        remoteMarker.showInfoWindow();
        mMap.moveCamera(CameraUpdateFactory.newLatLng(remoteLocation));

    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        AlertDialog alertDialog = new AlertDialog.Builder(LocationActivity.this).create();
        alertDialog.setMessage("RING or VIBRATE this device");
        alertDialog.setCancelable(true);
        alertDialog.setCanceledOnTouchOutside(true);

        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "VIBRATE",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        String phoneNumber = getIntent().getStringExtra("PHONENUMBER");
                        String message = "VIBRATE";

                        final SmsManager smsManager = android.telephony.SmsManager.getDefault();
                        smsManager.sendTextMessage(phoneNumber, null, message, null, null);

                    }
                });

        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "RING",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        String phoneNumber = getIntent().getStringExtra("PHONENUMBER");
                        String message = "RING";

                        final SmsManager smsManager = android.telephony.SmsManager.getDefault();
                        smsManager.sendTextMessage(phoneNumber, null, message, null, null);
                    }
                });
        //Display alert dialog
        alertDialog.show();
        return false;
    }

}
