package com.example.anneflo.geophone;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import android.location.Location;
import android.location.LocationManager;

import android.media.MediaPlayer;

import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;

import android.telephony.SmsManager;
import android.telephony.SmsMessage;

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

        //HERE allowing phone number of phone that requests location
        String allowedNumber = "+33667198499";
        //String allowedNumber = "+33631192880";
        //String allowedNumber = "+33676412797";
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

                    if(phoneNumber.equals(allowedNumber)) {

                        if(currentMessage.equals(locateMessage)) {
                            Location mRemoteLocation = appLocationService.getLocation(LocationManager.NETWORK_PROVIDER);

                            if (mRemoteLocation != null) {
                                String latitude = String.valueOf(mRemoteLocation.getLatitude());
                                String longitude = String.valueOf(mRemoteLocation.getLongitude());
                                String message = "lat : " + latitude +
                                        "\nlng : " + longitude +
                                        "\ndevice : " + Build.BRAND + " " + Build.DEVICE +
                                        "\nData from NETWORK";

                                final SmsManager smsManager = android.telephony.SmsManager.getDefault();
                                smsManager.sendTextMessage(phoneNumber, null, message, null, null);

                            } else {
                                Location gpsLocation = appLocationService.getLocation(LocationManager.GPS_PROVIDER);

                                if (gpsLocation != null) {
                                    String latitude = String.valueOf(mRemoteLocation.getLatitude());
                                    String longitude = String.valueOf(mRemoteLocation.getLongitude());
                                    String message = "lat : " + latitude +
                                            ";\n lng : " + longitude +
                                            "\ndevice : " + Build.BRAND + " " + Build.DEVICE +
                                            "\nData from GPS";

                                    final SmsManager smsManager = android.telephony.SmsManager.getDefault();
                                    smsManager.sendTextMessage(phoneNumber, null, message, null, null);

                                } else {
                                    String message ="ERROR: GPS Services not available on this phone.";
                                    final SmsManager smsManager = android.telephony.SmsManager.getDefault();
                                    smsManager.sendTextMessage(phoneNumber, null, message, null, null);

                                }

                            }

                        //Display "i'm here" pop up on screen of remote phone
                        /*Intent popUp = new Intent(context, PopUpOnSMS.class);
                        popUp.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(popUp);*/

                        //If SMS contains Vibrate message, enabling vibrate function
                        } else if (currentMessage.equals(vibrateMessage)) {
                            final Vibrator mVibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
                            //Vibrate for 3 seconds
                            mVibrator.vibrate(3000);
                            //Toast.makeText(context, "VIBRATE CONDITION", Toast.LENGTH_SHORT).show();

                        //If SMS contains Ring message, enabling Ringing function
                        } else if (currentMessage.equals(ringMessage)) {
                            final MediaPlayer hangoutMP = MediaPlayer.create(context, R.raw.hangouts_incoming_call);
                            //Play hangout ringtone
                            hangoutMP.start();
                            //Toast.makeText(context, "RING CONDITION", Toast.LENGTH_SHORT).show();
                        }

                    }


                }

            }
        } catch (Exception e) {

        }
    }
}
