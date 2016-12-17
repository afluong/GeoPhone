package com.example.anneflo.geophone;

import android.app.Activity;
import android.app.PendingIntent;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;

import android.location.Location;

import android.os.Build;
import android.os.Bundle;

import android.provider.Settings;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
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
    String mLatitude, mLongitude, mRemoteLat, mRemoteLng, mRemoteDevice, phoneNumber;

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

        //HERE only registered numbers which can be found
        //final String registeredNumber = "0631192880";
        final String registeredNumber = "0667198499";
        //final String registeredNumber = "0676412797";
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
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

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
                        Toast.makeText(getApplicationContext(), "Incorrect number format (10 digits)",
                                Toast.LENGTH_SHORT).show();
                    } else {
                        //Checking if phoneNumber is the same as which registered
                        if (phoneNumber.equals(registeredNumber)) {
                            try {
                                String SENT = "sent";
                                Intent sentIntent = new Intent(SENT);
                                //Create Pending Intents
                                PendingIntent sentPI = PendingIntent.getBroadcast(
                                        getApplicationContext(), 0, sentIntent,
                                        PendingIntent.FLAG_UPDATE_CURRENT);

                                //Listening if SMS has been sent
                                registerReceiver(new BroadcastReceiver() {
                                    @Override
                                public void onReceive(Context context, Intent intent) {
                                    switch (getResultCode()) {

                                        case Activity.RESULT_OK:
                                            Toast.makeText(context, "SMS Sent Successfully", Toast.LENGTH_SHORT).show();
                                            break;

                                        case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                                            Toast.makeText(context, "GENERIC FAILURE: Transmission Failed", Toast.LENGTH_SHORT).show();
                                            findButton.setVisibility(View.VISIBLE);
                                            loadingSpinner.setVisibility(View.GONE);
                                            break;

                                        case SmsManager.RESULT_ERROR_RADIO_OFF:
                                            //Creating alertdialog asking user to disable Airplane Mode
                                            final AlertDialog radioDialog = new AlertDialog.Builder(MainActivity.this).create();
                                            radioDialog.setTitle("Radio Mode is OFF");
                                            radioDialog.setMessage("Please make sure Airplane mode is disabled in order to access to Network services");
                                            radioDialog.setButton(AlertDialog.BUTTON_POSITIVE, "SETTINGS", new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int which) {
                                                    startActivity(new Intent(Settings.ACTION_WIRELESS_SETTINGS));
                                                }
                                            });
                                            radioDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "CANCEL", new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int which) {
                                                    final Intent restart = new Intent(MainActivity.this, MainActivity.class);
                                                    startActivity(restart);
                                                    return;
                                                }
                                            });
                                            radioDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                                                public void onCancel(DialogInterface dialog) {
                                                    return;
                                                }
                                            });
                                            radioDialog.show();

                                            findButton.setVisibility(View.VISIBLE);
                                            loadingSpinner.setVisibility(View.GONE);
                                            break;

                                        case SmsManager.RESULT_ERROR_NULL_PDU:
                                            Toast.makeText(context, "No PDU Defined", Toast.LENGTH_SHORT).show();
                                            findButton.setVisibility(View.VISIBLE);
                                            loadingSpinner.setVisibility(View.GONE);
                                            break;

                                        case SmsManager.RESULT_ERROR_NO_SERVICE:
                                            final AlertDialog servDialog = new AlertDialog.Builder(MainActivity.this).create();
                                            servDialog.setTitle("SMS cannot be sent");
                                            servDialog.setMessage("Cellular services are not available");
                                            servDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK",
                                                    new DialogInterface.OnClickListener() {
                                                        public void onClick(DialogInterface dialog, int which) {
                                                            servDialog.cancel();
                                                        }
                                                    });
                                            servDialog.show();
                                            findButton.setVisibility(View.VISIBLE);
                                            loadingSpinner.setVisibility(View.GONE);
                                            break;

                                    }
                                }

                            }, new IntentFilter(SENT));

                                //Sending SMS to phone to be found
                                final SmsManager smsManager = SmsManager.getDefault();
                                smsManager.sendTextMessage(phoneNumber, null, message, sentPI, null);

                                findButton.setVisibility(View.GONE);
                                loadingSpinner.setVisibility(View.VISIBLE);

                                //Listening on received GPS data from remote phone
                                BroadcastReceiver getGPSReceiver = new BroadcastReceiver() {
                                    @Override
                                    public void onReceive(Context context, Intent intent) {
                                        if(getResultCode() == Activity.RESULT_OK) {
                                                final Bundle bundle = intent.getExtras();
                                                final Object[] pdusObj = (Object[]) bundle.get("pdus");

                                                for (int i = 0; i < pdusObj.length; i++) {
                                                    SmsMessage currentSMS = SmsMessage.createFromPdu((byte[]) pdusObj[i]);
                                                    String currentMessage = currentSMS.getDisplayMessageBody();
                                                    String phoneNumber = currentSMS.getDisplayOriginatingAddress();

                                                    //HERE allowed phone numbers which send their location
                                                    //String allowedNumber = "+33631192880";
                                                    String allowedNumber = "+33667198499";
                                                    //String allowedNumber = "+33676412797";

                                                    //Only listening to authorized numbers
                                                    if(phoneNumber.equals(allowedNumber)) {
                                                        //if SMS contains lat, that's means coordinates have been found !
                                                        if (currentMessage.contains("lat")) {
                                                            //Split GPS data to keep only coordinates and ignore text
                                                            StringTokenizer tokenizer = new StringTokenizer(currentMessage, "\n");
                                                            int numberOfTokens = tokenizer.countTokens();
                                                            String[] splitArr = new String[numberOfTokens];
                                                            splitArr[0] = tokenizer.nextToken();
                                                            splitArr[1] = tokenizer.nextToken();
                                                            splitArr[2] = tokenizer.nextToken();

                                                            String mLat = splitArr[0];
                                                            String mLng = splitArr[1];
                                                            String mDevice = splitArr[2];

                                                            //Setting all remote info for map
                                                            mRemoteLat = mLat.substring(5, mLat.length());
                                                            mRemoteLng = mLng.substring(5, mLng.length());
                                                            mRemoteDevice = mDevice.substring(8, mDevice.length());

                                                            //Connecting to Google API Client instance
                                                            mGoogleApiClient.connect();

                                                        } else if (currentMessage.contains("ERROR")) {
                                                            final AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
                                                            alertDialog.setTitle("Remote phone cannot be found");
                                                            alertDialog.setMessage("GPS Services on the remote phone are not available");
                                                            alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK",
                                                                    new DialogInterface.OnClickListener() {
                                                                        public void onClick(DialogInterface dialog, int which) {
                                                                            alertDialog.cancel();
                                                                        }
                                                                    });
                                                            alertDialog.show();

                                                            findButton.setVisibility(View.VISIBLE);
                                                            loadingSpinner.setVisibility(View.GONE);
                                                        }
                                                    }
                                                }
                                        }

                                    }
                                };
                                IntentFilter filter = new IntentFilter();
                                filter.addAction("android.provider.Telephony.SMS_RECEIVED");
                                registerReceiver(getGPSReceiver, filter);

                            } catch (Exception e) {
                                Toast.makeText(getApplicationContext(), "ERROR: Please try again",
                                        Toast.LENGTH_SHORT).show();
                            }

                        } else {
                            Toast.makeText(getApplicationContext(), "This number is not registered",
                                        Toast.LENGTH_SHORT).show();
                        }
                    }

                } catch (Exception e) {

                }
            }
        });
    }


    //Entering in this after mGoogleAPIClient.connect() function is called
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Location mRemoteLocation;

        mRemoteLocation = new Location(" ");
        mRemoteLocation.setLatitude(Double.parseDouble(mRemoteLat));
        mRemoteLocation.setLongitude(Double.parseDouble(mRemoteLng));

        //Checking if ACCESS FINE LOCATION permission is granted
        /*if (ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
        } else {
            //if not ask for permission to enable access to FINE LOCATION
            requestPermissions(new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 99);
        }*/

        //Getting last location
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);

        //Toast.makeText(this, String.valueOf(mLastLocation), Toast.LENGTH_SHORT).show();
        //Toast.makeText(this, String.valueOf(mLastLocation.distanceTo(mRemoteLocation)), Toast.LENGTH_SHORT).show();

        if(mLastLocation != null) {

            mLatitude = String.valueOf(mRemoteLocation.getLatitude());
            mLongitude = String.valueOf(mRemoteLocation.getLongitude());

            //Checking if phone is more far than 15m
            if (mLastLocation.distanceTo(mRemoteLocation) > 15) {
                final Intent longMapLocation = new Intent(MainActivity.this, LongLocationActivity.class);
                //Passing GPS data to LongLocationActivity
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
                                mapLocation.putExtra("PHONENUMBER", phoneNumber);
                                startActivity(mapLocation);
                            }
                        });
                //Display alert dialog
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