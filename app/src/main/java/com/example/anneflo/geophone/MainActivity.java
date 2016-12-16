package com.example.anneflo.geophone;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import java.util.StringTokenizer;

public class MainActivity extends AppCompatActivity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener{
    GoogleApiClient mGoogleApiClient;
    Location mLastLocation;
    String mRemoteLat, mRemoteLng, mRemoteDevice, phoneNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Hide title bar
        this.requestWindowFeature(getWindow().FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);

        //Creation Google API Instance
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    @Override
    protected void onResume() {
        super.onResume();
        final Button findButton = (Button) findViewById(R.id.button);
        final ProgressBar loadingSpinner = (ProgressBar) findViewById(R.id.progressBar);

        findButton.setVisibility(View.VISIBLE);
        loadingSpinner.setVisibility(View.GONE);
    }

    protected void onStart() {
        super.onStart();

        final EditText textPhone = (EditText) findViewById(R.id.editText);
        final Button findButton = (Button) findViewById(R.id.button);
        final ProgressBar loadingSpinner = (ProgressBar) findViewById(R.id.progressBar);
        loadingSpinner.setVisibility(View.GONE);
        final ImageView about = (ImageView) findViewById(R.id.imageView3);
        //final String registeredNumber = "0631192880";
        final String registeredNumber = "0667198499";
        final Integer digitsLength = 10;

        //Listening on About icon
        about.setClickable(true);
        about.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Sending to AboutActivity
                Intent aboutIntent = new Intent(MainActivity.this, AboutActivity.class);
                startActivity(aboutIntent);

                //Slide left to right
                overridePendingTransition(R.anim.right_slide, R.anim.left_slide);

            }
        });

        //Listening on the "FIND" button
        findButton.setOnClickListener(new View.OnClickListener() {
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
                    phoneNumber = textPhone.getText().toString();
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
                                smsManager.sendTextMessage(phoneNumber, "GeoPhone", message, null, null);

                                findButton.setVisibility(View.GONE);
                                loadingSpinner.setVisibility(View.VISIBLE);

                                /*Toast.makeText(getApplicationContext(), "SMS sent with success !",
                                        Toast.LENGTH_SHORT).show();*/

                                BroadcastReceiver getGPSReceiver = new BroadcastReceiver() {
                                    @Override
                                    public void onReceive(Context context, Intent intent) {
                                        switch (getResultCode()) {
                                            case Activity.RESULT_OK:
                                                final Bundle bundle = intent.getExtras();
                                                final Object[] pdusObj = (Object[]) bundle.get("pdus");

                                                for (int i = 0; i < pdusObj.length; i++) {

                                                    SmsMessage currentSMS = SmsMessage.createFromPdu((byte[]) pdusObj[i]);
                                                    String currentMessage = currentSMS.getDisplayMessageBody();
                                                    String phoneNumber = currentSMS.getDisplayOriginatingAddress();

                                                    if(currentMessage.contains("lat") && phoneNumber.equals("+33667198499")) {
                                                        StringTokenizer tokenizer = new StringTokenizer(currentMessage, "\n");
                                                        int numberOfTokens = tokenizer.countTokens();
                                                        String[] splitArr = new String[numberOfTokens];
                                                        splitArr[0] = tokenizer.nextToken();
                                                        splitArr[1] = tokenizer.nextToken();
                                                        splitArr[2] = tokenizer.nextToken();

                                                        String mLat = splitArr[0];
                                                        String mLng = splitArr[1];
                                                        String mDevice = splitArr[2];

                                                        mRemoteLat = mLat.substring(5, mLat.length());
                                                        mRemoteLng = mLng.substring(5, mLng.length());
                                                        mRemoteDevice = mDevice.substring(8, mDevice.length());

                                                        mGoogleApiClient.connect();
                                                    }
                                                }
                                                break;

                                            case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                                                Toast.makeText(context, "Test Generic failure", Toast.LENGTH_SHORT).show();
                                                break;

                                            case SmsManager.RESULT_ERROR_NULL_PDU:
                                                Toast.makeText(context, "Test Null PDU", Toast.LENGTH_SHORT).show();
                                                break;

                                            case SmsManager.RESULT_ERROR_NO_SERVICE:
                                                Toast.makeText(context, "Test no service", Toast.LENGTH_SHORT).show();
                                                break;
                                        }

                                    }
                                };

                                IntentFilter filter = new IntentFilter();
                                filter.addAction("android.provider.Telephony.SMS_RECEIVED");
                                registerReceiver(getGPSReceiver, filter);


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
        });
    }

    //Entering in this after mGoogleAPIClient.connect() function is called
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Location mRemoteLocation;

        mRemoteLocation = new Location(" ");
        mRemoteLocation.setLatitude(Double.parseDouble(mRemoteLat));
        mRemoteLocation.setLongitude(Double.parseDouble(mRemoteLng));

        //Checking if ACCESS FINE LOCATION permission is granted
        if (ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
        } else {
            //if ask for permission
            requestPermissions(new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 99);
        }
        //Getting last location
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);

        if(mLastLocation != null) {

            final String mLatitude = String.valueOf(mLastLocation.getLatitude());
            final String mLongitude = String.valueOf(mLastLocation.getLongitude());

            //Checking if phone is more far than 15m
            if (mLastLocation.distanceTo(mRemoteLocation) > 15) {

                final Intent longMapLocation = new Intent(MainActivity.this, LongLocationActivity.class);
                longMapLocation.putExtra("LAT", mLatitude);
                longMapLocation.putExtra("LNG", mLongitude);
                longMapLocation.putExtra("DEVICE", mRemoteDevice);
                startActivity(longMapLocation);

                //if not, display alertdialog
            } else {
                //Creating alertdialog to ask user if want to use compass or just display map
                AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
                alertDialog.setTitle("Your smartphone is within 15m");
                alertDialog.setMessage("Enable Compass mode ?");

                //if user clicks YES to enable compass mode, launch CompassActivity
                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "YES",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                //Go to CompassActivity
                                Intent compassLocation = new Intent(MainActivity.this, CompassActivity.class);
                                compassLocation.putExtra("PHONENUMBER", phoneNumber);
                                startActivity(compassLocation);
                            }
                        });
                //If no, just display position on map with vibrate and ring mode
                alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "NO",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                //Go to LocationActivity
                                final Intent mapLocation = new Intent(MainActivity.this, LocationActivity.class);
                                //Passing GPS data to LocationActivity
                                mapLocation.putExtra("LAT", mLatitude);
                                mapLocation.putExtra("LNG", mLongitude);
                                startActivity(mapLocation);
                            }
                        });
                alertDialog.show();
            }

        } else {
            Toast.makeText(this, "Google API : Can't find current location, please make sure that Google API services are up",
                    Toast.LENGTH_SHORT).show();
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