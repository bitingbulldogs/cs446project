package com.example.scbcchoi.eatemup;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.content.Intent;
import java.util.ArrayList;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.view.MenuItem;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //****************************************************************************
        Intent intent = getIntent();
        ArrayList<String> result = intent.getStringArrayListExtra("result");
        if(!(result == null)) {
            String scannedText = "";
            for(int i = 0; i < result.size(); ++i) {
                System.out.println(result.get(i));
                scannedText = scannedText + result.get(i) + "\n";
            }
            TextView t = findViewById(R.id.result);
            t.setText(scannedText);
        }
        //****************************************************************************

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



    }

    //****************************************************************************
    public void scan(View v){
        Intent intent = new Intent(this, OcrCaptureActivity.class);
        startActivity(intent);
        finish();
    }
    //****************************************************************************

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

    public void showSettings() {
        Toast.makeText(this, "Settings", Toast.LENGTH_SHORT).show();
    }

    public void showExpiryHistory() {
        Toast.makeText(this, "History", Toast.LENGTH_SHORT).show();
    }

    public void cameraClick(View view) {
//        Intent intent = new Intent(this, CameraActivity.class);
//        startActivity(intent);
//        finish();
        Toast.makeText(this, "Camera", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, OcrCaptureActivity.class);
        startActivity(intent);
        finish();
    }

}
