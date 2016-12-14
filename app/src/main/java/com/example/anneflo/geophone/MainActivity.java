package com.example.anneflo.geophone;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SmsManager;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Hide title bar
        this.requestWindowFeature(getWindow().FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
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
                Intent map = new Intent(MainActivity.this, LocationActivity.class);
                startActivity(map);
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
}