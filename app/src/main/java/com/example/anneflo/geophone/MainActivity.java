package com.example.anneflo.geophone;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
        buttonFind.setOnClickListener(new View.OnClickListener() {
            @Override
            //Actions after click on button
            public void onClick(View v) {

                //Closing keyboard
                InputMethodManager inputManager = (InputMethodManager)
                        getSystemService(Context.INPUT_METHOD_SERVICE);

                inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);

                try {

                    String phoneNumber = textPhone.getText().toString();

                    Toast.makeText(getApplicationContext(), "SMS envoyé avec succès !",
                            Toast.LENGTH_SHORT).show();

                    buttonFind.setVisibility(View.GONE);
                    loadingSpinner.setVisibility(View.VISIBLE);


                } catch (NumberFormatException e){

                }
            }
        });
    }



}