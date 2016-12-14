package com.example.anneflo.geophone;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.widget.Toast;

public class SmsReceiver extends BroadcastReceiver
{

    @Override
    public void onReceive(Context context, Intent intent)
    {
        final Bundle bundle = intent.getExtras();

        try {
            if (bundle != null) {
                final Object[] pdusObj = (Object[]) bundle.get("pdus");

                for (int i = 0; i < pdusObj.length; i++) {
                    SmsMessage currentSMS = SmsMessage.createFromPdu((byte[]) pdusObj[i]);
                    String phoneNumber = currentSMS.getDisplayOriginatingAddress();

                    String senderNum = phoneNumber;
                    String message = "SMS reçu !";

                    final SmsManager smsManager = android.telephony.SmsManager.getDefault();
                    smsManager.sendTextMessage(senderNum, null, message, null, null);

                }

            }

        } catch (Exception e) {

        }
    }
}
