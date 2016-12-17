package com.example.anneflo.geophone;

import android.content.DialogInterface;
import android.content.Intent;

import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;

import android.os.Bundle;


public class PopUpOnSMS extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    //Creating "I'm here" pop up with alert dialog
    private void displayAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("I'm here !");
        builder.setMessage("Please find me !");
        builder.setCancelable(false);

        AlertDialog alert = builder.create();
        alert.setButton(AlertDialog.BUTTON_POSITIVE, "FOUND IT",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent compassLocation = new Intent(PopUpOnSMS.this, MainActivity.class);
                        startActivity(compassLocation);
                    }
                });
        alert.show();
    }

    @Override
    protected void onStart() {
        super.onStart();
        displayAlert();

    }

}
