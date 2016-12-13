package com.example.anneflo.geophone;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;


public class LocationActivity extends FragmentActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, OnMapReadyCallback {
    private GoogleApiClient mGoogleApiClient;
    Location mLastLocation;
    Location julienHome;
    Double mLatitude, mLongitude;
    GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {

        } else {
            //Forcing
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 0);
        }

        buildGoogleApiClient();
        mGoogleApiClient.connect();
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mGoogleApiClient.disconnect();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {
        //Checking if ACCESS FINE LOCATION permission is granted
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
        } else {
            //if ask for permission
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 99);
        }
        //Getting last location
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);

            //Position 2
            julienHome = new Location("");
            julienHome.setLatitude(48.97061249999999);
            julienHome.setLongitude(2.287349199999994);

            LatLng julien = new LatLng(julienHome.getLatitude(), julienHome.getLongitude());


            if(mLastLocation.distanceTo(julienHome) <= 25.00) {
                Toast.makeText(this, "Téléphone localisé", Toast.LENGTH_LONG).show();

            } else {
                //If smartphone is near, ask user if enable compass mode
                AlertDialog alertDialog = new AlertDialog.Builder(LocationActivity.this).create();
                alertDialog.setTitle("Your smartphone is less than 15m");
                alertDialog.setMessage("Enable Compass mode ?");

                //if user clicks YES to enable compass mode, launch CompassActivity
                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "YES",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                Intent compassIntent = new Intent(LocationActivity.this, CompassActivity.class);
                                startActivity(compassIntent);
                            }
                        });
                //If no, just display position on map
                alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "NO",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        });
                alertDialog.show();
            }
            if (mLastLocation != null) {
                mLatitude = mLastLocation.getLatitude();
                mLongitude = mLastLocation.getLongitude();

            //Getting location for marker
            LatLng currentLocation = new LatLng(mLatitude, mLongitude);

            //Showing coordinates in market snippet
            String snippetLoc = String.valueOf(currentLocation);

            //Showing device name
            String deviceName = Build.BRAND + " " + Build.DEVICE;

            //Create a marker for map
            mMap.addMarker(new MarkerOptions()
                    .position(currentLocation)
                    .title(deviceName)
                    .snippet(snippetLoc)
                    .flat(true)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.smalllogo)));


            mMap.moveCamera(CameraUpdateFactory.newLatLng(currentLocation));
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        Toast.makeText(this, "Connection suspended", Toast.LENGTH_SHORT);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(this, "Connection failed", Toast.LENGTH_SHORT);
    }

}
