package com.example.scbcchoi.eatemup;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.util.Calendar;

public class Settings extends AppCompatActivity {
    private static SharedPreferences settingsLocalStorage;
    private static int defaulthour = 17;
    private static int defaultmin = 30;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting);
    }

    public static void setInt(String key, int val, Context context){
        settingsLocalStorage = context.getSharedPreferences("settingsLocalStorage", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settingsLocalStorage.edit();
        editor.putInt(key, val)
                .apply();
    }

    //returns -1 if key doesn't exist, otherwise returns corresponding int value to the key
    public static int getInt(String key, Context context){
        settingsLocalStorage = context.getSharedPreferences("settingsLocalStorage", Context.MODE_PRIVATE);
        return settingsLocalStorage.getInt(key, -1);
    }

    //init background service
    public static void backgroundInit(Context context){
        //init receiver
        ComponentName receiver = new ComponentName(context, BootRec.class);
        PackageManager pm = context.getPackageManager();
        pm.setComponentEnabledSetting(receiver,
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP);

        //init background intent
        Intent intent = new Intent(context, BackgroundService.class);
        PendingIntent alarmIntent = PendingIntent.getService(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        //try to get hour of day and minute from local storage
        int hourOfDay = getInt("hour", context);
        int minute = getInt("minute", context);
        if(hourOfDay < 0) hourOfDay = defaulthour; //by default we set it to 17:30
        if(minute < 0) minute = defaultmin;

        //Set alarm for each day.
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
        calendar.set(Calendar.MINUTE, minute);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),1000 * 60* 60 * 24 , alarmIntent);
    }

    public void doneSetting(View v){
        //set notification times
        EditText edtHour = findViewById(R.id.defaultHour);
        EditText edtMinute = findViewById(R.id.defaultMinute);
        String hour = edtHour.getText().toString();
        String minute = edtMinute.getText().toString();
        if(hour.equals("")) hour = Integer.toString(defaulthour);
        if(minute.equals("")) minute = Integer.toString(defaultmin);
        int dhour = Integer.valueOf(hour);
        int dmin = Integer.valueOf(minute);
        //proceed only if dhour and dmin are valid
        if(!hour.equals("") && !minute.equals("") && dhour >= 0 && dhour < 24 && dmin >= 0 && dmin < 60){
            setInt("hour", dhour, this);
            setInt("minute", dmin, this);
            backgroundInit(this);

            //everything is fine. we go to main
            Intent intent = new Intent(this, MainActivity.class);
            this.startActivity(intent);
            finish();
        }
        //this means that dhour or dmin are not valid.
        else{
            Toast.makeText(this, "invalid hour or minutes!", Toast.LENGTH_SHORT).show();
        }

    }
}
