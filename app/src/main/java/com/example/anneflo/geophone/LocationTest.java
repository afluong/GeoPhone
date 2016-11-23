package com.example.anneflo.geophone;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class LocationTest extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_location);
    }

    protected void onStart() {
        super.onStart();

        final Button vib = (Button) findViewById(R.id.button_vibror);
        final Button ring = (Button)findViewById(R.id.button_ring);
        // Get instance of Vibrator form current Context
        final Vibrator mVibrator = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);
        final MediaPlayer hangoutMP = MediaPlayer.create(this, R.raw.hangouts_incoming_call);

        vib.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            // Vibrate for 3000 milliseconds
                mVibrator.vibrate(3000);
            }
        });

        ring.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hangoutMP.start();

            }
        });
    }
}
