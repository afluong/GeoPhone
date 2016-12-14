package com.example.anneflo.geophone;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.widget.Toast;

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
        builder.setPositiveButton("FOUND IT", null);

        AlertDialog alert = builder.create();
        alert.show();
    }

    @Override
    protected void onStart() {
        super.onStart();
        displayAlert();

    }

}
