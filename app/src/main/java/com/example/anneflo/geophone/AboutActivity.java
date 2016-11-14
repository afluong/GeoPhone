package com.example.anneflo.geophone;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

public class AboutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

    }


    @Override
    protected void onStart() {
        super.onStart();
        
        final ImageView backPrevious = (ImageView) findViewById(R.id.imageView4);
        final ImageView devAnneFlo = (ImageView) findViewById(R.id.annefloView);
        final ImageView devJulien = (ImageView) findViewById(R.id.julienView);

        //Defining back arrow can be clicked
        backPrevious.setClickable(true);

        //Listening on back Arrow imageView
        backPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Sending to MainActivity
                Intent mainIntent = new Intent(AboutActivity.this, MainActivity.class);
                startActivity(mainIntent);

                //Slide right to left effect
                overridePendingTransition(R.anim.left_slide, R.anim.right_slide);
            }
        });

        //Defining developers pictures can be clicked
        devAnneFlo.setClickable(true);
        devJulien.setClickable(true);

        //Listening on devs pictures
        devAnneFlo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "Anne Flo's LinkedIn link to do",
                        Toast.LENGTH_SHORT).show();
            }
        });

        devJulien.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "Julien's LinkedIn link to do",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }
}
