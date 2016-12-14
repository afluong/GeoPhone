package com.example.anneflo.geophone;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.widget.Toast;

public class SmsReceiver extends BroadcastReceiver
{

    @Override
    public void onReceive(Context context, Intent intent)
    {
        final Bundle bundle = intent.getExtras();
        String phoneRegistered = "06631192880";

        try {
            if (bundle != null) {
                final Object[] pdusObj = (Object[]) bundle.get("pdus");

                for (int i = 0; i < pdusObj.length; i++) {
                    SmsMessage currentSMS = SmsMessage.createFromPdu((byte[]) pdusObj[i]);
                    if(currentSMS.getDisplayOriginatingAddress().equals(phoneRegistered)) {

                        String phoneNumber = currentSMS.getDisplayOriginatingAddress();
                        String senderNum = phoneNumber;
                        String message = "SMS reçu !";

                        final SmsManager smsManager = android.telephony.SmsManager.getDefault();
                        smsManager.sendTextMessage(senderNum, null, message, null, null);
                    } else {
                        Toast.makeText(context, "a SMS from unknown contact has been received", Toast.LENGTH_SHORT).show();
                    }

                }

            }

        } catch (Exception e) {

        }
    }
}
