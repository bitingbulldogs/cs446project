package com.example.scbcchoi.eatemup;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;

public class BackgroundService extends IntentService {

    private int notificationID = 10;

    public BackgroundService(){
        super("BackgroundService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "eatemup")
                .setSmallIcon(R.drawable.camera_icon)
                .setContentTitle("Eat Em Up")
                .setContentText("You got food about to expire, eat em up!")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

        Intent mainActivity = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, mainActivity, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(pendingIntent);

        notificationManager.notify(notificationID, builder.build());
        //System.out.println("intent called");
    }
}
