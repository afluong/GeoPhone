package com.example.anneflo.geophone;

import android.content.Intent;

import android.support.v7.app.AppCompatActivity;

import android.os.Bundle;

import android.telephony.SmsManager;

import android.view.View;

import android.widget.Button;
import android.widget.ImageView;

public class CompassActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compass);
    }

    @Override
    protected void onStart() {
        super.onStart();

        final Button vibrateButton = (Button) findViewById(R.id.button);
        final Button ringButton = (Button) findViewById(R.id.button1);
        final ImageView arrowPrevious = (ImageView) findViewById(R.id.imageView);

        //Defining back arrow can be clicked
        arrowPrevious.setClickable(true);

        //Listening on back Arrow imageView
        arrowPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Sending to MainActivity
                Intent mainActivity = new Intent(CompassActivity.this, MainActivity.class);
                startActivity(mainActivity);

                //Slide right to left effect
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_in_right);
            }
        });

        //Vibrating remote phone by sending "VIBRATE" SMS
        vibrateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phoneNumber = getIntent().getStringExtra("PHONENUMBER");
                String message = "VIBRATE";

                final SmsManager smsManager = android.telephony.SmsManager.getDefault();
                smsManager.sendTextMessage(phoneNumber, null, message, null, null);
            }
        });

        //Ringing remote phone by sending "RING" SMS
        ringButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phoneNumber = getIntent().getStringExtra("PHONENUMBER");
                String message = "RING";

                final SmsManager smsManager = android.telephony.SmsManager.getDefault();
                smsManager.sendTextMessage(phoneNumber, null, message, null, null);
            }
        });
    }
}
