package com.example.anneflo.geophone;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

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

        //Defining image can be clicked
        backPrevious.setClickable(true);

        //Listening on back Arrow imageView
        backPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mainIntent = new Intent(AboutActivity.this, MainActivity.class);
                startActivity(mainIntent);
            }
        });


    }
}
