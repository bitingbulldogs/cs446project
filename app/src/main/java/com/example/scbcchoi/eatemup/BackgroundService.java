package com.example.scbcchoi.eatemup;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;

import com.example.scbcchoi.eatemup.inventory.InventoryListItem;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class BackgroundService extends IntentService {

    public static int notificationID = 0;
    static boolean somethingExpired = false; //something expired today
    private static int expiryDateUpBound = 5; //at most 5 days in history
    public BackgroundService(){
        super("BackgroundService");
    }

    private static boolean todayHasCalculated(Context c){
        String key = "todaysDate";
        String storageValue = Settings.getStr(key, c);
        String todaysDate = todaysDate();

        System.out.println("today's date = " + todaysDate + ", storage value = " + storageValue + "and they are equal?" + storageValue.equals(todaysDate));

        //if we already calculated this
        if(storageValue.equals(todaysDate)) return true;
        else {
            Settings.setStr(key, todaysDate, c);
            return false;
        }
    }

    public static void oneDayHasPassed(Context c){
        somethingExpired = false;

        System.out.println("One day has passed!");

        //if we already calculated this, then we simply return
        if(todayHasCalculated(c)) return;

        //update inventories and find out if there is something expirying
        ListsModel lm = new ListsModel(c);
        List<InventoryListItem> inventorys = lm.getInventoryList();
        lm.clearList("inventory");

        Map<String, Integer> historyMap = lm.getExpiredHistoryList();
        for(int i = 0; i < inventorys.size(); ++i){
            int expiryDate = inventorys.get(i).getDateInt() - 1;
            String key = inventorys.get(i).getName();
            if(expiryDate == -1) {
                lm.addToList("inventory", key, expiryDate + 1);
                continue;
            }
            lm.addToList("inventory", key, expiryDate);
            //if it is expired or close to expiry, we add 1 for its expiry history.
            //maxExpiry is the max days to expiry we allow
            int maxExpiry = 0;
            if(historyMap.containsKey(key))  maxExpiry = historyMap.get(key);
            if(maxExpiry > expiryDateUpBound) maxExpiry = expiryDateUpBound;
            if(expiryDate <= maxExpiry && expiryDate >= 0) { //if expiryDate < 0 it means that it is already expired and been calculated in history yesterday
                somethingExpired = true;
                //update history
                lm.removeFromList("history", key);
                lm.addToList("history", key, maxExpiry + 1);
            }
        }
    }

    public static String todaysDate(){
        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat s = new SimpleDateFormat("dd-MMM-yyyy");
        String ret = s.format(c);
        return ret;
    }

    //this will be called at the certain notification time once a day.
    //It's the background logic executing once a day
    @Override
    protected void onHandleIntent(Intent intent) {
        System.out.println("Background Handeler called!");

        //pretty straightforward
        oneDayHasPassed(this);

        if(somethingExpired){
            NotificationCompat.Builder builder = new NotificationCompat.Builder(this, MainActivity.channelIDStr)
                    .setSmallIcon(R.drawable.camera_icon)
                    .setContentTitle("Eat Em Up")
                    .setContentText("You got food expirying, eat em up!")
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT);

            //NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
            Intent mainActivity = new Intent(this, MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, mainActivity, PendingIntent.FLAG_UPDATE_CURRENT);
            builder.setContentIntent(pendingIntent);
            notificationManager.notify(notificationID, builder.build());
        }
    }
}
