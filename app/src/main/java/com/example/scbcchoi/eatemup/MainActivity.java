package com.example.scbcchoi.eatemup;

import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.content.Intent;
import java.util.Calendar;
import java.util.List;

import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.view.MenuItem;
import android.support.v7.widget.Toolbar;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.scbcchoi.eatemup.inventory.InventoryAdapter;
import com.example.scbcchoi.eatemup.inventory.InventoryListItem;

public class MainActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private InventoryAdapter adapter;
    private Dialog addDialog;

    // to test recylerview, should be removed later
    List<InventoryListItem> InventoryList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //init back ground service
        backgroundInit();

        //init Lists Model
        ListsModel lm = new ListsModel(this);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.menu_icon);

        drawerLayout = findViewById(R.id.drawer);
        navigationView = findViewById(R.id.navigation);

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.about:
                        menuItem.setChecked(true);
                        showAbout();
                        drawerLayout.closeDrawers();
                        return true;
                    case R.id.recipe:
                        menuItem.setChecked(true);
                        showRecipe();
                        drawerLayout.closeDrawers();
                        return true;
                    case R.id.settings:
                        menuItem.setChecked(true);
                        showSettings();
                        drawerLayout.closeDrawers();
                        return true;
                    case R.id.history:
                        menuItem.setChecked(true);
                        showExpiryHistory();
                        drawerLayout.closeDrawers();
                        return true;
                }
                return false;
            }
        });

        //set up recyclerView and floating action button
        recyclerView = findViewById(R.id.rv_inventory);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(view.getContext(), "add", Toast.LENGTH_SHORT).show();
                addDialog = new Dialog(view.getContext());
                addDialog.setContentView(R.layout.dialog_add);
                addDialog.show();
            }
        });

        InventoryList = lm.getInventoryList();

        //setup adapter for RecyclerView
        adapter = new InventoryAdapter(InventoryList);
        recyclerView.setAdapter(adapter);
    }

    //init background service
    private void backgroundInit(){
        //init receiver
        ComponentName receiver = new ComponentName(this, BootRec.class);
        PackageManager pm = this.getPackageManager();
        pm.setComponentEnabledSetting(receiver,
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP);

        //init background intent
        Intent intent = new Intent(this, BackgroundService.class);
        PendingIntent alarmIntent = PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        //Set alarm to be 18:00 for each day.
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, 18);
        calendar.set(Calendar.MINUTE, 0);

        AlarmManager alarmManager = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
        //alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
        //1000 * 60* 60 * 24 , alarmIntent);

        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(),
                1000 * 30 , alarmIntent);
    }

    public void scan(View v){
        Intent intent = new Intent(this, OcrCaptureActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void showAbout() {
        Toast.makeText(this, "About", Toast.LENGTH_SHORT).show();

    }

    public void showRecipe() {
        Toast.makeText(this, "Recipe", Toast.LENGTH_SHORT).show();
    }
    public void showSettings() {
        Toast.makeText(this, "Settings", Toast.LENGTH_SHORT).show();
    }

    public void showExpiryHistory() {
        Toast.makeText(this, "History", Toast.LENGTH_SHORT).show();
    }

    public void cameraClick(View view) {
        Intent intent = new Intent(this, CameraActivity.class);
        startActivity(intent);
        //finish();
    }

    //insert an item to InventoryList
    //should be moved to "InventoryList Class"......?
    private int insertItem(InventoryListItem item){
        int j = item.getDateInt();
        for (int i = 0; i < InventoryList.size(); i++) {
            if (InventoryList.get(i).getDateInt() >= j){
                InventoryList.add(i,item);
                return i;
            }
        }
        InventoryList.add(item);
        return InventoryList.size()-1;
    }

    public void addCancel(View view){
        addDialog.dismiss();
    }

    //add an item to inventory list
    public void addInventory(View view){
        EditText nameText = addDialog.findViewById(R.id.dialog_name_add);
        EditText dateText = addDialog.findViewById(R.id.dialog_date_add);
        int date = Integer.parseInt(dateText.getText().toString());
        String name = nameText.getText().toString();
        InventoryListItem item = new InventoryListItem(name, date);

        //store in shared preferences
        ListsModel lm = new ListsModel(this);
        lm.addToList("inventory", name.toLowerCase(), date);

        int pos = insertItem(item);
        recyclerView.smoothScrollToPosition(pos);
        adapter.notifyDataSetChanged();
        addDialog.dismiss();
    }

    public void  updateInventory(View view){
        InventoryListItem item =  adapter.updateItem();
        InventoryList.remove(adapter.getPos());//index of item changed

        //Todo: the update isn't physically stored
        int pos = insertItem(item);
        recyclerView.smoothScrollToPosition(pos);
        adapter.notifyDataSetChanged();

    }

    public void noUpdate(View view){
        adapter.noUpdate();
    }

    public void clearAll(View v){
        ListsModel lm = new ListsModel(this);
        lm.clearList("alias");
        lm.clearList("inventory");
        lm.clearList("shopping");
        InventoryList = lm.getInventoryList();
        adapter = new InventoryAdapter(InventoryList);
        recyclerView.setAdapter(adapter);
        recyclerView.getAdapter().notifyDataSetChanged();
    }
}
