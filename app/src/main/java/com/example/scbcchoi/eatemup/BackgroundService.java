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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class BackgroundService extends IntentService {

    public static int notificationID = 0;
    static boolean somethingExpired = false; //something expired today
    private static int expiryDateUpBound = 8; //at most 5 days in history
    private static int defaultRemindingDate = 3;
    private static int interval = 1;
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
            interval = dateInterval(todaysDate, storageValue);
            Settings.setStr(key, todaysDate, c);
            return false;
        }
    }

    public static int dateInterval(String d1, String d2){
        if(d1.equals("") || d2.equals("")) return 1;
        SimpleDateFormat s = new SimpleDateFormat("MM/dd/yyyy");
        try{
            Date firstDate = s.parse(d1);
            Date secondDate = s.parse(d2);
            long diffInMillies = Math.abs(secondDate.getTime() - firstDate.getTime());
            long diff = TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS);
            return (int)diff;

        } catch (Exception e){
            System.out.println("dateInterval Doesn't work!");
        }
        return 1;
    }

    public static void oneDayHasPassed(Context c){
//        boolean flag = false;
        System.out.println("One day has passed!");
        interval = 1;

        //if we already calculated this, then we simply return
        if(todayHasCalculated(c)) return;

        //update inventories and find out if there is something expirying
        ListsModel lm = new ListsModel(c);
        List<InventoryListItem> inventorys = lm.getInventoryList();
        lm.clearList("inventory");

        Map<String, Integer> historyMap = lm.getExpiredHistoryList();
        for(int i = 0; i < inventorys.size(); ++i){
            int expiryDate = inventorys.get(i).getDateInt() - interval;
//            if(expiryDate == -1) {
//                flag = true;
//            }
            String key = inventorys.get(i).getName();
            if(expiryDate <= -2) {
                lm.addToList("inventory", key, -1);
                continue;
            }
            lm.addToList("inventory", key, expiryDate);
            //if it is expired or close to expiry, we add 1 for its expiry history.
            //maxExpiry is the max days to expiry we allow
            int maxExpiry = defaultRemindingDate;
            if(historyMap.containsKey(key))  maxExpiry = historyMap.get(key);
            if(maxExpiry > expiryDateUpBound) maxExpiry = expiryDateUpBound;
            if(expiryDate <= maxExpiry && expiryDate >= 0) { //if expiryDate < 0 it means that it is already expired and been calculated in history yesterday
                //update history
                lm.removeFromList("history", key);
                lm.addToList("history", key, maxExpiry + 1);
            }
        }
//        if(flag) {
//            NotificationCompat.Builder builder = new NotificationCompat.Builder(this, MainActivity.channelIDStr)
//                    .setSmallIcon(R.mipmap.ic_launcher)
//                    .setContentTitle("Eat'em Up!")
//                    .setContentText("You have some expired food!")
//                    .setPriority(NotificationCompat.PRIORITY_DEFAULT);
//
//            //NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
//            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
//            Intent mainActivity = new Intent(this, MainActivity.class);
//            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, mainActivity, PendingIntent.FLAG_UPDATE_CURRENT);
//            builder.setContentIntent(pendingIntent);
//            notificationManager.notify(notificationID, builder.build());
//        }
    }

    //it only returns true when something is expiring and not expired.
    public boolean isSomethingExpired(){
        System.out.println("isSomethingExpired is called!");
        ListsModel lm = new ListsModel(this);
        List<InventoryListItem> il = lm.getInventoryList();
        Map<String, Integer> historyMap = lm.getExpiredHistoryList();
        for(int i = 0; i < il.size(); ++i){
            String key = il.get(i).getName();
            int date = il.get(i).getDateInt();
            if(date == -1) continue;
            System.out.println("key = " + key + ", date = " + date);
            if(date < defaultRemindingDate) return true;
            else if(historyMap.containsKey(key)){
                int smartDate = historyMap.get(key);
                System.out.println(key + " is in history list! smartDate = " + smartDate);
                if(date < smartDate) return true;
            }
        }
        return false;
    }

    public static String todaysDate(){
        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat s = new SimpleDateFormat("MM/dd/yyyy");
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

        if(isSomethingExpired()){
            String content = "Food expiring soon:\n";
            ListsModel lm = new ListsModel(this);
            List<InventoryListItem> il = lm.getInventoryList();


            List<String> list = new ArrayList<>();
            for(int i=0; i<il.size(); i++) {
                if(il.get(i).getDateInt() >= 0 && il.get(i).getDateInt() <= 10) {
                    list.add(il.get(i).getName());
                }
            }

            int maxNum = Math.min(list.size(), 3);

            //maxNum has to be > 0.
            for(int i = 0; i < maxNum; ++i) {
                content += il.get(i).getName() + "\n";
            }
            if(list.size() > 3) {
                content += "...";
            }

//            content = content.substring(0,content.length()-2);
            String contentFirst = "Your food is expiring! Eat'em up!";

            NotificationCompat.Builder builder = new NotificationCompat.Builder(this, MainActivity.channelIDStr)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle("Eat'em Up!")
                    .setContentText(contentFirst)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT);
            builder.setStyle(new NotificationCompat.BigTextStyle().bigText(contentFirst + "\n\n" + content));

            //NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
            Intent mainActivity = new Intent(this, MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, mainActivity, PendingIntent.FLAG_UPDATE_CURRENT);
            builder.setContentIntent(pendingIntent);
            notificationManager.notify(notificationID, builder.build());
        }
    }
}
