package com.example.anneflo.geophone;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Vibrator;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.widget.Toast;

public class SmsReceiver extends BroadcastReceiver
{
    static final String ACTION = "android.provider.Telephony.SMS_RECEIVED";
    private Context contexts;

    @Override
    public void onReceive(Context context, Intent intent)
    {
        final Bundle bundle = intent.getExtras();
        String locateMessage = "Where are you ?";
        String vibrateMessage = "VIBRATE";
        String ringMessage = "RING";
        String allowedNumber = "+33667198499";
        contexts = context;
        AppLocationService appLocationService;
        appLocationService = new AppLocationService(contexts);

        try {
            if (bundle != null) {
                final Object[] pdusObj = (Object[]) bundle.get("pdus");

                for (int i = 0; i < pdusObj.length; i++) {
                    SmsMessage currentSMS = SmsMessage.createFromPdu((byte[]) pdusObj[i]);
                    String currentMessage = currentSMS.getDisplayMessageBody();
                    String phoneNumber = currentSMS.getDisplayOriginatingAddress();

                    if (phoneNumber.equals(allowedNumber) && currentMessage.equals(locateMessage)) {

                        Location mRemoteLocation = appLocationService.getLocation(LocationManager.NETWORK_PROVIDER);

                        if (mRemoteLocation != null) {
                            double mRemoteLatitude = mRemoteLocation.getLatitude();
                            double mRemoteLongitude = mRemoteLocation.getLongitude();

                            String latitude = String.valueOf(mRemoteLocation.getLatitude());
                            String longitude = String.valueOf(mRemoteLocation.getLongitude());
                            String message = "lat : " + latitude + ";\n lng : " + longitude;


                            final SmsManager smsManager = android.telephony.SmsManager.getDefault();
                            smsManager.sendTextMessage(phoneNumber, null, message, null, null);

                        } else {
                            Location gpsLocation = appLocationService.getLocation(LocationManager.GPS_PROVIDER);

                            if (gpsLocation != null) {
                                double latitude = gpsLocation.getLatitude();
                                double longitude = gpsLocation.getLongitude();
                                Toast.makeText(contexts, "Mobile Location (GPS): \nLatitude: " + latitude
                                                + "\nLongitude: " + longitude,
                                        Toast.LENGTH_LONG).show();
                            }

                        }

                        /*String message = "SMS Delivered";

                        //Sending SMS back to confirm reception
                        final SmsManager smsManager = android.telephony.SmsManager.getDefault();
                        smsManager.sendTextMessage(phoneNumber, null, message, null, null);

                        //Display "i'm here" alert
                        Intent popUp = new Intent(context, PopUpOnSMS.class);
                        popUp.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(popUp);

                    } else if (phoneNumber.equals(allowedNumber) && currentMessage.equals(vibrateMessage)){
                        final Vibrator mVibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
                        //Vibrate for 5 seconds
                        mVibrator.vibrate(5000);
                        Toast.makeText(context, "VIBRATE CONDITION", Toast.LENGTH_SHORT).show();
                    }

                    else if (phoneNumber.equals(allowedNumber) && currentMessage.equals(ringMessage)){
                        final MediaPlayer hangoutMP = MediaPlayer.create(context, R.raw.hangouts_incoming_call);
                        //Play hangout ringtone
                        hangoutMP.start();
                        Toast.makeText(context, "RING CONDITION", Toast.LENGTH_SHORT).show();
                    }*/

                    }

                }
            }
        } catch (Exception e) {

        }
    }
}
