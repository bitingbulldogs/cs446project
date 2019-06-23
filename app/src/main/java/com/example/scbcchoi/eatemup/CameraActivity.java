package com.example.scbcchoi.eatemup;

import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.content.Intent;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import com.example.scbcchoi.eatemup.inventory.InventoryListItem;

import java.util.ArrayList;
import java.util.List;

public class CameraActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    List<ScanItem> scanlist;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.camera_activity);

        Intent intent = getIntent();
        ArrayList<String> result = intent.getStringArrayListExtra("result");
        if(!(result == null)) {
            String scannedText = "";
            for(int i = 0; i < result.size(); ++i) {
                System.out.println(result.get(i));
                scannedText = scannedText + result.get(i) + "\n";
            }

            Toolbar tbar = (Toolbar) findViewById(R.id.toolbar_cam);
            tbar.setVisibility(View.VISIBLE);

            //set up recyclerView
            recyclerView = findViewById(R.id.rv_scan);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            scanlist = new ArrayList<>();
            ListsModel lm = new ListsModel(this);

            for(int i = 0; i < result.size(); ++i){
                scanlist.add(new ScanItem(result.get(i), "Null Cat", lm.getExpiryDate(result.get(i))));
            }
            ScanAdapter scanA = new ScanAdapter(scanlist);
            recyclerView.setAdapter(scanA);



            //hide the scan button
            Button b = findViewById(R.id.button);
            b.setVisibility(View.GONE);
        }
    }

    public void scan(View v){
        Intent intent = new Intent(this, OcrCaptureActivity.class);
        startActivity(intent);
        finish();
    }

    public void selectAllScans(View v){
        //Todo
        System.out.println("selectAllScans is called");
    }

    public void doneScanning(View v){
        /*
        View rv = recyclerView.getLayoutManager().findViewByPosition(0);
        CheckBox c = (CheckBox)rv.findViewById(R.id.scan_checkbox);
        if(c.isChecked())System.out.println("The item on 0 position is checked!");
        else System.out.println("The item on 0 position is not checked!");
        */
        //Todo
        System.out.println("doneScanning is called");
    }
}