package com.example.scbcchoi.eatemup;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import java.util.Calendar;

public class BootRec extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent i) {
        //System.out.println("BootRec is getting called!");


        /*
        if (i.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
            Settings.backgroundInit(context);
        }
        */
        /*
        else {
            Intent notifyIntent = new Intent(context, BackgroundService.class);
            context.startActivity(notifyIntent);
        }


        Intent notifyIntent = new Intent(context, BackgroundService.class);
        context.startActivity(notifyIntent);
        */
    }
}
