package com.example.anneflo.geophone;

import android.*;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SmsManager;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.server.converter.StringToIntConverter;
import com.google.android.gms.location.LocationServices;

public class MainActivity extends AppCompatActivity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener{
    GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Hide title bar
        this.requestWindowFeature(getWindow().FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    protected void onStart() {
        super.onStart();

        final EditText textPhone = (EditText) findViewById(R.id.editText);
        final Button buttonFind = (Button) findViewById(R.id.button);
        final ProgressBar loadingSpinner = (ProgressBar) findViewById(R.id.progressBar);
        loadingSpinner.setVisibility(View.GONE);
        final ImageView about = (ImageView) findViewById(R.id.imageView3);
        final String registeredNumber = "0667198499";
        final Integer digitsLength = 10;

        //TEST MAP
        final Button map = (Button) findViewById(R.id.button2);

        map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /*final Intent map = new Intent(MainActivity.this, LocationActivity.class);
                startActivity(map);*/

                mGoogleApiClient.connect();
            }
        });

        //Listening on About icon
        about.setClickable(true);
        about.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Sending to AboutActivity
                Intent aboutIntent = new Intent(MainActivity.this, AboutActivity.class);
                startActivity(aboutIntent);

                //Slide left to right
                overridePendingTransition(R.anim.left_slide, R.anim.right_slide);

            }
        });

        //Listening on the "FIND" button
        buttonFind.setOnClickListener(new View.OnClickListener() {
            @Override
            //Actions after click on button
            public void onClick(View v) {

                //Closing keyboard
                InputMethodManager inputManager = (InputMethodManager)
                        getSystemService(Context.INPUT_METHOD_SERVICE);

                inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);

                //ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.SEND_SMS},1);
                try {
                    String phoneNumber = textPhone.getText().toString();
                    String message = "Where are you ?";

                    //Checking if length digits is 10
                    if (!(phoneNumber.length() == digitsLength)) {
                        Toast.makeText(getApplicationContext(), "Format de numéro incorrect (10 digits)",
                                Toast.LENGTH_SHORT).show();
                    } else {
                        //Checking if phoneNumber is the same as which registered
                        if (phoneNumber.equals(registeredNumber)) {
                            try {
                                //Sending SMS to phone to be found
                                final SmsManager smsManager = SmsManager.getDefault();
                                smsManager.sendTextMessage(phoneNumber, null, message, null, null);


                                Toast.makeText(getApplicationContext(), "SMS sent with success !",
                                        Toast.LENGTH_SHORT).show();

                                //buttonFind.setVisibility(View.GONE);
                                //loadingSpinner.setVisibility(View.VISIBLE);

                            } catch (Exception e) {
                                Toast.makeText(getApplicationContext(), "Error : SMS not sent...",
                                        Toast.LENGTH_SHORT).show();
                            }

                        } else {
                            Toast.makeText(getApplicationContext(), "Unknown number",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                } catch (Exception e) {

                }
            }


            //Recepteur de SMS

            //SmsReceiver myReceiver = new SmsReceiver();

            //this.registerReceiver(myReceiver, new IntentFilter("android.provider.Telephony.SMS_RECEIVED"));
        });
    }

    //Entering in this after mGoogleAPIClient.connect() function is called
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Location mLastLocation;
        Location julienHome;

        //Position 2
        julienHome = new Location("");
        julienHome.setLatitude(48.97061249999999);
        julienHome.setLongitude(2.287349199999994);

        //Checking if ACCESS FINE LOCATION permission is granted
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
        } else {
            //if ask for permission
            requestPermissions(new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 99);
        }
        //Getting last location
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);

        final String mLatitude = String.valueOf(mLastLocation.getLatitude());
        final String mLongitude = String.valueOf(mLastLocation.getLongitude());

        if(mLastLocation.distanceTo(julienHome) < 15) {
            final Intent longMapLocation = new Intent(MainActivity.this, LongLocationActivity.class);
            longMapLocation.putExtra("LAT", mLatitude);
            longMapLocation.putExtra("LNG", mLongitude);
            startActivity(longMapLocation);
        } else {

            AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
            alertDialog.setTitle("Your smartphone is less than 15m");
            alertDialog.setMessage("Enable Compass mode ?");

            //if user clicks YES to enable compass mode, launch CompassActivity
            alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "YES",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            Intent compassLocation = new Intent(MainActivity.this, CompassActivity.class);
                            startActivity(compassLocation);
                        }
                    });
            //If no, just display position on map with vibrate and ring mode
            alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "NO",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            final Intent mapLocation = new Intent(MainActivity.this, LocationActivity.class);
                            mapLocation.putExtra("LAT", mLatitude);
                            mapLocation.putExtra("LNG", mLongitude);
                            startActivity(mapLocation);
                        }
                    });
            alertDialog.show();
        }

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    //If app is stopped, disconnect Google API
    @Override
    protected void onStop() {
        super.onStop();
        if(mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

}