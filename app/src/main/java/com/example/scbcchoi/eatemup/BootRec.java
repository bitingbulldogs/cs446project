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
        if (i.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
            //setting the alarm
            Intent intent = new Intent(context, BackgroundService.class);
            PendingIntent alarmIntent = PendingIntent.getService(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

            //Set alarm to be 18:00 for each day.
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(System.currentTimeMillis());
            calendar.set(Calendar.HOUR_OF_DAY, 18);
            calendar.set(Calendar.MINUTE, 0);

            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                    1000 * 60 * 60 * 24 , alarmIntent);

        }
    }
}
